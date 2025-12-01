package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.laboratoriumcomputer.databinding.ActivityEquipmentBinding;
import com.example.laboratoriumcomputer.models.Equipment;

public class EquipmentActivity extends AppCompatActivity {

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
            // Condition isn't explicitly in the model, reusing Status or just "Good" if not present?
            // The user asked for specific fields earlier. The Model has 'status'.
            // Layout has txtCondition. Let's assume status acts as condition or display status there too.
            binding.txtCondition.setText("Condition: " + equipment.getStatus());
            
            binding.txtEqName.setText("Equipment Name: " + equipment.getName());
            binding.txtEqType.setText("Type: " + equipment.getType());
            binding.txtEqSerialNumber.setText("Serial Number: " + equipment.getSerialNumber());
            binding.txtEqLastBorrowed.setText("Last Borrowed: " + equipment.getLastBorrowed());

            // Also color the status card? The user didn't ask for this specifically in EquipmentActivity, 
            // but they asked for color in RecyclerView.
            // I'll leave EquipmentActivity colors as default unless requested, but might be nice to match.
            // The request "background color of the equipment" referred to "equipment_layout.xml" presumably (the list item).
        }

        binding.btnBackToInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EquipmentActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });
    }
}