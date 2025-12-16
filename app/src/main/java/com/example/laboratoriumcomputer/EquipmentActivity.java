package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laboratoriumcomputer.databinding.ActivityEquipmentBinding;
import com.example.laboratoriumcomputer.models.Equipment;

public class EquipmentActivity extends AppCompatActivity {
    //Made by: Ian Mulya Chiuandi, 2702218891

    private ActivityEquipmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEquipmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Equipment equipment = (Equipment) getIntent().getSerializableExtra("equipment");
        if (equipment != null) {
            binding.txtEquipment.setText(equipment.getName());
            binding.txtStatus.setText(equipment.getStatus());
            binding.txtCondition.setText("Condition: " + equipment.getStatus());
            
            binding.txtEqName.setText("Equipment Name: " + equipment.getName());
            binding.txtEqType.setText("Type: " + equipment.getType());
            binding.txtEqSerialNumber.setText("Serial Number: " + equipment.getSerialNumber());
            binding.txtEqLastBorrowed.setText("Last Borrowed: " + equipment.getLastBorrowed());
        }

        binding.btnBackToInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EquipmentActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        binding.menuButton.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                startActivity(new Intent(EquipmentActivity.this, MainActivity.class));
            } else if (id == R.id.menu_borrow) {
                startActivity(new Intent(EquipmentActivity.this, BorrowActivity.class));
            } else if (id == R.id.menu_return) {
                startActivity(new Intent(EquipmentActivity.this, ReturnActivity.class));
            } else if (id == R.id.menu_history) {
                startActivity(new Intent(EquipmentActivity.this, HistoryActivity.class));
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}