package com.emi.systemconfiguration;

public class LockModal {

    // variables for our coursename,
    // description, tracks and duration, id.
    private String status;
    private String vendorNumber;

    private int id;

    // creating getter and setter methods
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public LockModal(String status, String vendorNumber) {
        this.status = status;
        this.vendorNumber = vendorNumber;

    }
}
