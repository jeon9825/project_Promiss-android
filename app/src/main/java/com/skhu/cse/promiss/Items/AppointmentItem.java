package com.skhu.cse.promiss.Items;

public class AppointmentItem {

    public static AppointmentItem item=new AppointmentItem();

    private  AppointmentItem(){}

    String name; //약속의 이름


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
