package com.example.laboratoriumcomputer.models;

public class History {
    private String serialNumber;
    private String borrowerName;
    private String date; // 15/12/2025, 17:24
    private String borrowReturn; // Nilai: "Returned" atau "Borrowed"
    private String type; // Tipe peralatan, e.g., "Laptop"

    // Constructor kosong wajib untuk Firebase
    public History() {
    }

    public History(String serialNumber, String borrowerName, String date, String borrowReturn, String type) {
        this.serialNumber = serialNumber;
        this.borrowerName = borrowerName;
        this.date = date;
        this.borrowReturn = borrowReturn;
        this.type = type;
    }

    // Getters
    public String getSerialNumber() {
        return serialNumber;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getDate() {
        return date;
    }

    public String getBorrowReturn() {
        return borrowReturn;
    }

    public String getType() {
        return type;
    }
    
    // Setters dihilangkan untuk model data yang sederhana.
}
