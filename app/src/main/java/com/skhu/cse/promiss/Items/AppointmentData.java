package com.skhu.cse.promiss.Items;

public class AppointmentData {

    int id;
    String name;

    public static AppointmentData data=new AppointmentData();
    private AppointmentData(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
