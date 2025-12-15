package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.laboratoriumcomputer.adapters.HistoryAdapter;
import com.example.laboratoriumcomputer.models.History;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale; // Digunakan untuk penyesuaian log data

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;
    private DatabaseReference historyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // 1. Inisialisasi RecyclerView
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(historyAdapter);

        // 2. Inisialisasi Firebase Database Reference ke "history" table
        // Sesuai dengan struktur database yang Anda tunjukkan
        historyRef = FirebaseDatabase.getInstance().getReference("history");
        loadHistoryData();

        // Navigasi
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_dashboard) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            } else if (id == R.id.menu_inventory) {
                startActivity(new Intent(HistoryActivity.this, InventoryActivity.class));
            } else if (id == R.id.menu_borrow) {
                startActivity(new Intent(HistoryActivity.this, BorrowActivity.class));
            } else if (id == R.id.menu_return) {
                startActivity(new Intent(HistoryActivity.this, ReturnActivity.class));
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

    private void loadHistoryData() {
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    
                    // Ambil nilai dari child di bawah setiap KEY (misalnya: TST-01[15/12/2025-17:24])
                    String serialNumber = dataSnapshot.child("serialNumber").getValue(String.class);
                    String borrowerName = dataSnapshot.child("borrowerName").getValue(String.class);
                    String date = dataSnapshot.child("date").getValue(String.class);
                    // Sesuaikan dengan nama field di database Anda: "borrowReturn"
                    String borrowReturn = dataSnapshot.child("borrowReturn").getValue(String.class); 
                    String type = dataSnapshot.child("type").getValue(String.class);

                    if (serialNumber != null && borrowerName != null && date != null && borrowReturn != null && type != null) {
                        History history = new History(serialNumber, borrowerName, date, borrowReturn, type);
                        historyList.add(history);
                    } else {
                         Log.w("HistoryActivity", "Data riwayat tidak lengkap untuk key: " + dataSnapshot.getKey());
                    }
                }
                
                // Urutkan riwayat berdasarkan Key (string key berisi tanggal), 
                // atau jika Anda ingin yang terbaru di atas, balikkan daftarnya:
                Collections.reverse(historyList);
                
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Gagal memuat riwayat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Database Error: " + error.getMessage());
            }
        });
    }
}
