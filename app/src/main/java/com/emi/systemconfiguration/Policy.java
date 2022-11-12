package com.emi.systemconfiguration;

public class Policy {
    String policyId;
    String policyNumber;

    public Policy(String policyId, String policyNumber) {
        this.policyId = policyId;
        this.policyNumber = policyNumber;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
