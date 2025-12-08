package com.example.laboratoriumcomputer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashBoardActivity extends AppCompatActivity {

    private DatabaseReference database;

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

        database = FirebaseDatabase.getInstance().getReference("equipment");

        // Link to XML IDs
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
    }

    private void loadDashboardData() {
        database.addValueEventListener(new ValueEventListener() {
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
