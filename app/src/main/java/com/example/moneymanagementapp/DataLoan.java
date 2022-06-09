package com.example.moneymanagementapp;

public class DataLoan {
    String companion,date,id,notes;
    int amount,moneyLeft;
    public DataLoan(String companion, String date, String id, String notes, int amount,int moneyLeft) {
        this.companion = companion;
        this.date = date;
        this.id = id;
        this.notes = notes;
        this.amount=amount;
        this.moneyLeft=moneyLeft;
    }
    public String getCompanion() {
        return companion;
    }

    public void setCompanion(String item) {
        this.companion = companion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public int getMoneyLeft() {
        return moneyLeft;
    }

    public void setMoneyLeft(int amount) {
        this.amount = moneyLeft;
    }
}
