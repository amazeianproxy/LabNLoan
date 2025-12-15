package com.example.laboratoriumcomputer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.laboratoriumcomputer.R;
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
        // Asumsikan Anda membuat layout item bernama 'item_history'
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        
        holder.txtSerialNumber.setText("Serial: " + history.getSerialNumber());
        holder.txtBorrowerName.setText("Peminjam: " + history.getBorrowerName());
        holder.txtDate.setText("Tgl: " + history.getDate());
        holder.txtType.setText(history.getBorrowReturn());
        
        // Atur warna berdasarkan status (pinjam/kembali)
        int colorResId;
        if ("Returned".equalsIgnoreCase(history.getBorrowReturn())) {
            colorResId = android.R.color.holo_green_dark; 
        } else if ("Borrowed".equalsIgnoreCase(history.getBorrowReturn())) {
            colorResId = android.R.color.holo_red_dark;
        } else {
            colorResId = android.R.color.tab_indicator_text; // Default
        }
        
        holder.txtType.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));
        holder.txtEquipmentType.setText("Tipe: " + history.getType());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtSerialNumber, txtBorrowerName, txtDate, txtType, txtEquipmentType;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Pastikan ID ini sesuai dengan yang ada di item_history.xml
            txtSerialNumber = itemView.findViewById(R.id.txtSerialNumber); 
            txtBorrowerName = itemView.findViewById(R.id.txtBorrowerName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtType = itemView.findViewById(R.id.txtType); // Untuk Borrow/Return Status
            txtEquipmentType = itemView.findViewById(R.id.txtEquipmentType);
        }
    }
}
