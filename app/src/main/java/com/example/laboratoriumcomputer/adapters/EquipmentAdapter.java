package com.example.laboratoriumcomputer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laboratoriumcomputer.EquipmentActivity;
import com.example.laboratoriumcomputer.R;
import com.example.laboratoriumcomputer.models.Equipment;

import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private List<Equipment> equipmentList;
    private Context context;

    public EquipmentAdapter(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public void updateList(List<Equipment> list) {
        this.equipmentList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.equipment_layout, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);
        holder.tvEquipmentName.setText(equipment.getName());

        String status = equipment.getStatus();
        if (status != null) {
            if (status.equalsIgnoreCase("Available")) {
                holder.bgEquipment.setCardBackgroundColor(Color.parseColor("#68D78E"));
            } else if (status.equalsIgnoreCase("Borrowed")) {
                holder.bgEquipment.setCardBackgroundColor(Color.parseColor("#FFB560"));
            } else if (status.equalsIgnoreCase("Damaged")) {
                holder.bgEquipment.setCardBackgroundColor(Color.parseColor("#D76894"));
            } else {
                holder.bgEquipment.setCardBackgroundColor(Color.WHITE);
            }
        } else {
            holder.bgEquipment.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EquipmentActivity.class);
            intent.putExtra("equipment", equipment);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    public static class EquipmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvEquipmentName;
        CardView bgEquipment;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquipmentName = itemView.findViewById(R.id.tvEquipmentName);
            bgEquipment = itemView.findViewById(R.id.bgEquipment);
        }
    }
}