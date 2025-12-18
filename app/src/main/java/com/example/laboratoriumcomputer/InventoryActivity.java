package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.laboratoriumcomputer.adapters.EquipmentAdapter;
import com.example.laboratoriumcomputer.databinding.ActivityInventoryBinding;
import com.example.laboratoriumcomputer.databinding.DialogAddEquipmentBinding;
import com.example.laboratoriumcomputer.managers.DatabaseManager;
import com.example.laboratoriumcomputer.models.Equipment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    //Made by: Ian Mulya Chiuandi, 2702218891

    private ActivityInventoryBinding binding;
    private EquipmentAdapter equipmentAdapter;
    private List<Equipment> equipmentList;
    private List<Equipment> allEquipmentList;
    private DatabaseManager databaseManager;
    private String currentStatusFilter = "All Status";

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(InventoryActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    binding.etSearch.setText(result.getContents());
                    applyFilters();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvEquipment.setLayoutManager(new LinearLayoutManager(this));
        equipmentList = new ArrayList<>();
        allEquipmentList = new ArrayList<>();
        equipmentAdapter = new EquipmentAdapter(equipmentList);
        binding.rvEquipment.setAdapter(equipmentAdapter);

        databaseManager = new DatabaseManager();
        databaseManager.getEquipmentList(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allEquipmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);
                    if (equipment != null) {
                        allEquipmentList.add(equipment);
                    }
                }
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSearch.setOnClickListener(v -> applyFilters());

        binding.btnScanQR.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Scan a QR Code");
            options.setCameraId(0);
            options.setBeepEnabled(false);
            options.setBarcodeImageEnabled(true);
            options.setOrientationLocked(false);
            barcodeLauncher.launch(options);
        });

        binding.btnFilter.setOnClickListener(v -> showStatusPopup(v));

        binding.btnAddEquipment.setOnClickListener(v -> showAddEquipmentDialog());

        binding.menuButton.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                startActivity(new Intent(InventoryActivity.this, MainActivity.class));
            } else if (id == R.id.menu_borrow) {
                startActivity(new Intent(InventoryActivity.this, BorrowActivity.class));
            } else if (id == R.id.menu_return) {
                startActivity(new Intent(InventoryActivity.this, ReturnActivity.class));
            } else if (id == R.id.menu_history) {
                startActivity(new Intent(InventoryActivity.this, HistoryActivity.class));
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showStatusPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("All Status");
        popup.getMenu().add("Available");
        popup.getMenu().add("Borrowed");
        popup.getMenu().add("Damaged");
        
        popup.setOnMenuItemClickListener(item -> {
            currentStatusFilter = item.getTitle().toString();
            binding.btnFilter.setText(currentStatusFilter);
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void applyFilters() {
        String query = binding.etSearch.getText().toString().toLowerCase();
        equipmentList.clear();
        for (Equipment equipment : allEquipmentList) {
            boolean matchesSearch = query.isEmpty() || equipment.getName().toLowerCase().contains(query);
            boolean matchesStatus = currentStatusFilter.equals("All Status") || 
                                    (equipment.getStatus() != null && equipment.getStatus().equalsIgnoreCase(currentStatusFilter));
            
            if (matchesSearch && matchesStatus) {
                equipmentList.add(equipment);
            }
        }
        equipmentAdapter.notifyDataSetChanged();
    }

    private void showAddEquipmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        DialogAddEquipmentBinding dialogBinding = DialogAddEquipmentBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        String[] statuses = {"Available", "Borrowed", "Damaged"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        dialogBinding.actvStatus.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String serialNumber = dialogBinding.etSerialNumber.getText().toString().trim();
            String name = dialogBinding.etName.getText().toString().trim();
            String type = dialogBinding.etType.getText().toString().trim();
            String status = dialogBinding.actvStatus.getText().toString().trim();

            if (serialNumber.isEmpty() || name.isEmpty() || type.isEmpty() || status.isEmpty()) {
                Toast.makeText(InventoryActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Equipment newEquipment = new Equipment(serialNumber, name, type, "Never", status);
                databaseManager.addEquipment(newEquipment);
                Toast.makeText(InventoryActivity.this, "Equipment added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
