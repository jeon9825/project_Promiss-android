package com.skhu.cse.promiss.Items;

public class AppointmentItem {

    public static AppointmentItem item=new AppointmentItem();

    private  AppointmentItem(){}

    String name; //약속의 이름

    double latitude;
    double longitude;
    String address;//주소
    String address_detail;//상세주소

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_detail() {
        return address_detail;
    }

    public void setAddress_detail(String address_detail) {
        this.address_detail = address_detail;
    }
}
