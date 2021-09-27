package com.emi.systemconfiguration;

public class password {
    String pass = "69691";
    Boolean lockState = true;

    private static final password ourInstance = new password();
    public static password getInstance() {
        return ourInstance;
    }
    private password() {
    }
    public void setData(String pass) {
        this.pass = pass;
    }
    public String getData() {
        return pass;
    }

    public  void setLockState(Boolean lockState){
        this.lockState = lockState;
    }
    public Boolean getLockState(){return  lockState;}
}