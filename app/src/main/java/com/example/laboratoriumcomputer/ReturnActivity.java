package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReturnActivity extends AppCompatActivity {

    private EditText etReturneeName, etSerialNumber;
    private Button btnReturn;
    private DatabaseReference equipmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                startActivity(new Intent(ReturnActivity.this, MainActivity.class));
            } else if (id == R.id.menu_inventory) {
                startActivity(new Intent(ReturnActivity.this, InventoryActivity.class));
            } else if (id == R.id.menu_borrow) {
                startActivity(new Intent(ReturnActivity.this, BorrowActivity.class));
            } else if (id == R.id.menu_history) {
                startActivity(new Intent(ReturnActivity.this, HistoryActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        etReturneeName = findViewById(R.id.etReturneeName);
        etSerialNumber = findViewById(R.id.etSerialNumber);
        btnReturn = findViewById(R.id.btnReturn);
        equipmentRef = FirebaseDatabase.getInstance().getReference("equipment");
        btnReturn.setOnClickListener(v -> returnEquipment());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void returnEquipment() {
        String returneeName = etReturneeName.getText().toString().trim();
        String serialNumber = etSerialNumber.getText().toString().trim();

        if (returneeName.isEmpty() || serialNumber.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference selectedEquipment = equipmentRef.child(serialNumber);

        selectedEquipment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ReturnActivity.this, "Equipment not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentStatus = snapshot.child("status").getValue(String.class);

                if (!"Borrowed".equalsIgnoreCase(currentStatus)) {
                    Toast.makeText(ReturnActivity.this, "This equipment is not currently borrowed!", Toast.LENGTH_LONG).show();
                    return;
                }

                selectedEquipment.child("status").setValue("Available");

                Toast.makeText(ReturnActivity.this, "Return Success!", Toast.LENGTH_SHORT).show();
                etReturneeName.setText("");
                etSerialNumber.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReturnActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
