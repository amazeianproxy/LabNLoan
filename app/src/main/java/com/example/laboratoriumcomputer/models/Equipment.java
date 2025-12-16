package com.example.laboratoriumcomputer.models;

import java.io.Serializable;

public class Equipment implements Serializable {
    private String serialNumber;
    private String name;
    private String type;
    private String lastBorrowed;
    private String status;

    public Equipment() {
    }

    public Equipment(String serialNumber, String name, String type, String lastBorrowed, String status) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.type = type;
        this.lastBorrowed = lastBorrowed;
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastBorrowed() {
        return lastBorrowed;
    }

    public void setLastBorrowed(String lastBorrowed) {
        this.lastBorrowed = lastBorrowed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}