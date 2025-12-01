package com.example.laboratoriumcomputer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoriumcomputer.adapters.EquipmentAdapter;
import com.example.laboratoriumcomputer.managers.DatabaseManager;
import com.example.laboratoriumcomputer.models.Equipment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView rvEquipment;
    private EquipmentAdapter equipmentAdapter;
    private List<Equipment> equipmentList;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        rvEquipment = findViewById(R.id.rvEquipment);
        rvEquipment.setLayoutManager(new LinearLayoutManager(this));
        equipmentList = new ArrayList<>();
        equipmentAdapter = new EquipmentAdapter(equipmentList);
        rvEquipment.setAdapter(equipmentAdapter);

        databaseManager = new DatabaseManager();
        databaseManager.getEquipmentList(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                equipmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);
                    if (equipment != null) {
                        equipmentList.add(equipment);
                    }
                }
                equipmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnAddEquipment = findViewById(R.id.btnAddEquipment);
        btnAddEquipment.setOnClickListener(v -> showAddEquipmentDialog());

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
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
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showAddEquipmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_equipment, null);
        builder.setView(dialogView);

        EditText etSerialNumber = dialogView.findViewById(R.id.etSerialNumber);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etType = dialogView.findViewById(R.id.etType);
        EditText etStatus = dialogView.findViewById(R.id.etStatus);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String serialNumber = etSerialNumber.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String status = etStatus.getText().toString().trim();

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