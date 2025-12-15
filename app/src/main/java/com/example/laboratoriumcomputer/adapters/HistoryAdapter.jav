package com.example.laboratoriumcomputer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.laboratoriumcomputer.R; // Pastikan R mengarah ke package Anda
import com.example.laboratoriumcomputer.models.History;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asumsikan Anda memiliki layout item bernama 'item_history'
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.txtSerialNumber.setText("Serial No: " + history.getSerialNumber());
        holder.txtBorrowerName.setText("Peminjam: " + history.getBorrowerName());
        holder.txtDate.setText("Tanggal: " + history.getDate());
        holder.txtType.setText("Tipe: " + history.getType());
        
        // Opsional: set warna berdasarkan tipe
        if ("Borrow".equals(history.getType())) {
            holder.txtType.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else if ("Return".equals(history.getType())) {
            holder.txtType.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtSerialNumber, txtBorrowerName, txtDate, txtType;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Anda perlu memastikan ID ini ada di item_history.xml
            txtSerialNumber = itemView.findViewById(R.id.txtSerialNumber); 
            txtBorrowerName = itemView.findViewById(R.id.txtBorrowerName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtType = itemView.findViewById(R.id.txtType);
        }
    }
}