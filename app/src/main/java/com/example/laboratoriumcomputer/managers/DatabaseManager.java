package com.example.laboratoriumcomputer.managers;

import com.example.laboratoriumcomputer.models.Equipment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseManager {
    private DatabaseReference databaseReference;

    public DatabaseManager() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("equipment");
    }

    public void addEquipment(Equipment equipment) {
        String serialNumber = equipment.getSerialNumber();
        if (serialNumber != null && !serialNumber.isEmpty()) {
            databaseReference.child(serialNumber).setValue(equipment);
        } else {
            // Fallback
            String id = databaseReference.push().getKey();
            if (id != null) {
                equipment.setSerialNumber(id);
                databaseReference.child(id).setValue(equipment);
            }
        }
    }

    public void getEquipmentList(ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }
}