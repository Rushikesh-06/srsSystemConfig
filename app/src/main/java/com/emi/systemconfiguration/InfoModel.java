package com.emi.systemconfiguration;

public class InfoModel {
    // string course_name for storing course_name
    // and imgid for storing image id.
    private String item_name;
    private String title_name;
    private int imgid;

    public InfoModel(String item_name, int imgid, String title_name) {
        this.item_name = item_name;
        this.imgid = imgid;
        this.title_name = title_name;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }
}