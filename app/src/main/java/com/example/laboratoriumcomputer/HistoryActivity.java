package com.example.laboratoriumcomputer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.laboratoriumcomputer.adapters.HistoryAdapter;
import com.example.laboratoriumcomputer.databinding.ActivityHistoryBinding;
import com.example.laboratoriumcomputer.models.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    //Made by: Ian Mulya Chiuandi, 2702218891
    private ActivityHistoryBinding binding;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList);
        binding.rvHistory.setAdapter(historyAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("history");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    History history = dataSnapshot.getValue(History.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });

        binding.menuButton.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
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
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}