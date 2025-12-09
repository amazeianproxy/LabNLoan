package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.laboratoriumcomputer.databinding.ActivityMainBinding;
import com.example.laboratoriumcomputer.managers.DatabaseManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;

    // Dashboard UI
    private TextView txtTotal, txtAvailable, txtBorrowed, txtDamaged;

    // Equipment by type
    private TextView txtLaptop, txtCamera, txtWebcam, txtTablet;

    // Loan Status
    private TextView txtActiveLoans, txtOverdueLoans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseManager = new DatabaseManager();

        // Link to XML IDs
        ImageButton menuButton = findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        menuButton.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        txtTotal = findViewById(R.id.txtTotalEquipment);
        txtAvailable = findViewById(R.id.txtAvailableEquipment);
        txtBorrowed = findViewById(R.id.txtBorrowedEquipment);
        txtDamaged = findViewById(R.id.txtDamagedEquipment);

        txtLaptop = findViewById(R.id.txtLaptopCount);
        txtCamera = findViewById(R.id.txtCameraCount);
        txtWebcam = findViewById(R.id.txtWebcamCount);
        txtTablet = findViewById(R.id.txtTabletCount);

        txtActiveLoans = findViewById(R.id.txtActiveLoans);
        txtOverdueLoans = findViewById(R.id.txtOverdueLoans);

        loadDashboardData();

        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_inventory) {
                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
            } else if (id == R.id.menu_borrow) {
                startActivity(new Intent(MainActivity.this, BorrowActivity.class));
            } else if (id == R.id.menu_return) {
                startActivity(new Intent(MainActivity.this, ReturnActivity.class));
            } else if (id == R.id.menu_history) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private void loadDashboardData() {
        databaseManager.getEquipmentList(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total = 0;
                int available = 0;
                int borrowed = 0;
                int damaged = 0;

                int laptop = 0;
                int camera = 0;
                int webcam = 0;
                int tablet = 0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                    total++;

                    String status = itemSnapshot.child("status").getValue(String.class);
                    String type = itemSnapshot.child("type").getValue(String.class);

                    if (status != null) {
                        switch (status) {
                            case "Available":
                                available++;
                                break;
                            case "Borrowed":
                                borrowed++;
                                break;
                            case "Damaged":
                                damaged++;
                                break;
                        }
                    }

                    if (type != null) {
                        switch (type) {
                            case "Laptop":
                                laptop++;
                                break;
                            case "Camera":
                                camera++;
                                break;
                            case "Webcam":
                                webcam++;
                                break;
                            case "Tablet":
                                tablet++;
                                break;
                        }
                    }
                }

                // Update dashboard UI
                txtTotal.setText(String.valueOf(total));
                txtAvailable.setText(String.valueOf(available));
                txtBorrowed.setText(String.valueOf(borrowed));
                txtDamaged.setText(String.valueOf(damaged));

                txtLaptop.setText(String.valueOf(laptop));
                txtCamera.setText(String.valueOf(camera));
                txtWebcam.setText(String.valueOf(webcam));
                txtTablet.setText(String.valueOf(tablet));

                // Loan Status
                txtActiveLoans.setText(String.valueOf(borrowed));
                txtOverdueLoans.setText(String.valueOf(damaged));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}