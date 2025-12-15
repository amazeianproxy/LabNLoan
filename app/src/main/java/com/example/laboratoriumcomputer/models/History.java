package com.example.laboratoriumcomputer.models;

public class History {
    private String serialNumber;
    private String borrowerName;
    private String date;
    private String type; // e.g., "Borrow" or "Return"

    // Constructor kosong wajib untuk Firebase
    public History() {
    }

    public History(String serialNumber, String borrowerName, String date, String type) {
        this.serialNumber = serialNumber;
        this.borrowerName = borrowerName;
        this.date = date;
        this.type = type;
    }

    // Getters and Setters
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}