package com.example.laboratoriumcomputer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoriumcomputer.R;
import com.example.laboratoriumcomputer.models.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    public void updateList(List<History> list) {
        this.historyList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.tvSerialNumber.setText(history.getSerialNumber());
        holder.tvBorrowerName.setText("Borrower: " + history.getBorrowerName());
        holder.tvDate.setText(history.getDate());
        holder.tvType.setText(history.getType());
        holder.tvBorrowReturn.setText(history.getBorrowReturn());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerialNumber, tvBorrowerName, tvDate, tvType, tvBorrowReturn;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerialNumber = itemView.findViewById(R.id.tvSerialNumber);
            tvBorrowerName = itemView.findViewById(R.id.tvBorrowerName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvBorrowReturn = itemView.findViewById(R.id.tvBorrowReturn);
        }
    }
}