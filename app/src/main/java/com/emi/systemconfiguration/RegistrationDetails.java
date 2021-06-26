package com.emi.systemconfiguration;

public class RegistrationDetails {

    //    private final customer_active Boolean;
    // variables for storing our data.
    private String customer_uid, customer_name, customer_contact, customer_email, customer_mobile_brand, customer_payment, customer_loan,customer_pincode,vendorId, policyNo , startDate,endDate;
    private  Boolean customer_active;

    public RegistrationDetails() {
        // empty constructor
        // required for Firebase.
    }



    public RegistrationDetails(String customer_uid,String customer_name, String customer_contact,
                               String customer_email, String customer_mobile_brand, String customer_payment,String customer_loan,
                               String startDate, String endDate ) {
        this.customer_uid = customer_uid;
        this.customer_name = customer_name;
        this.customer_contact = customer_contact;
        this.customer_email = customer_email;
        this.customer_mobile_brand = customer_mobile_brand;
        this.customer_payment = customer_payment;
        this.customer_loan = customer_loan;
        this.customer_active = java.lang.Boolean.TRUE;
        this.customer_pincode = "NotSet";
        this.vendorId = vendorId;
        this.policyNo = policyNo;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    // getter methods for all variables.
    public String getCustomer_uid() {
        return customer_uid;
    }

    public void setCustomer_uid(String customer_uid) {
        this.customer_uid = customer_uid;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_contact() {
        return customer_contact;
    }

    public void setCustomer_contact(String customer_contact) {
        this.customer_contact = customer_contact;
    }

    public String getCustomer_mobile_brand() {
        return customer_mobile_brand;
    }

    public void setCustomer_mobile_brand(String customer_mobile_brand) {
        this.customer_mobile_brand = customer_mobile_brand;
    }

    public String getCustomer_payment() {
        return customer_payment;
    }

    public void setCustomer_payment(String customer_payment) {
        this.customer_payment = customer_payment;
    }

    public String getCustomer_loan() {
        return customer_loan;
    }

    public void setCustomer_loan(String customer_loan) {
        this.customer_loan = customer_loan;
    }

    public Boolean getCustomer_active() {
        return customer_active;
    }

    public void setCustomer_active(Boolean customer_active) {
        this.customer_active = customer_active;
    }

    public String getCustomer_pincode() {
        return customer_pincode;
    }

    public void setCustomer_pincode(String customer_pincode) {
        this.customer_pincode = customer_pincode;
    }
//    public String getVendorId() {
//        return vendorId;
//    }
//
//    public void setVendorId(String vendorId) {
//        this.vendorId = vendorId;
//    }

//    public String getPolicyNo() {
//        return policyNo;
//    }
//    public void setPolicyNo(String policyNo) {
//        this.policyNo = policyNo;
//    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}

