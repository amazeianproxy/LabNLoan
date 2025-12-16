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

import com.example.laboratoriumcomputer.models.History;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BorrowActivity extends AppCompatActivity {

    private EditText etBorrower, etSerial;
    private Button btnBorrow;

    private DatabaseReference equipmentRef;
    private DatabaseReference historyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_borrow);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        menuButton.setOnClickListener(v -> 
                drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                startActivity(new Intent(BorrowActivity.this, MainActivity.class));
            } else if (id == R.id.menu_inventory) {
                startActivity(new Intent(BorrowActivity.this, InventoryActivity.class));
            } else if (id == R.id.menu_return) {
                startActivity(new Intent(BorrowActivity.this, ReturnActivity.class));
            } else if (id == R.id.menu_history) {
                startActivity(new Intent(BorrowActivity.this, HistoryActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        etBorrower = findViewById(R.id.etBorrower);
        etSerial = findViewById(R.id.etSerialNumber);
        btnBorrow = findViewById(R.id.btnBorrow);

        equipmentRef = FirebaseDatabase.getInstance().getReference("equipment");
        historyRef = FirebaseDatabase.getInstance().getReference("history");

        btnBorrow.setOnClickListener(v -> borrowEquipment());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void borrowEquipment() {
        String borrower = etBorrower.getText().toString().trim();
        String serial = etSerial.getText().toString().trim();

        if (borrower.isEmpty() || serial.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference selectedEquipment = equipmentRef.child(serial);

        selectedEquipment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (!snapshot.exists()) {
                     Toast.makeText(BorrowActivity.this, "Equipment not found!", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 String currentStatus = snapshot.child("status").getValue(String.class);

                if ("Borrowed".equalsIgnoreCase(currentStatus)) {
                    Toast.makeText(BorrowActivity.this, "This equipment is already borrowed!", Toast.LENGTH_LONG).show();
                    return;
                }

                if ("Damaged".equalsIgnoreCase(currentStatus)) {
                    Toast.makeText(BorrowActivity.this, "Cannot borrow damaged equipment!", Toast.LENGTH_LONG).show();
                    return;
                }

                String type = snapshot.child("type").getValue(String.class);
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                // Update Equipment
                selectedEquipment.child("status").setValue("Borrowed");
                selectedEquipment.child("lastBorrowed").setValue(currentDate);

                // Add to History
                History history = new History(serial, borrower, currentDate, "Borrowed", type != null ? type : "Unknown");
                // Using push() as keys cannot contain [ ] characters in Firebase Realtime Database
                historyRef.push().setValue(history);

                Toast.makeText(BorrowActivity.this, "Borrow Success!", Toast.LENGTH_SHORT).show();

                etBorrower.setText("");
                etSerial.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BorrowActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}