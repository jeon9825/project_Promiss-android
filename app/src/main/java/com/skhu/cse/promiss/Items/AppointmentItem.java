package com.skhu.cse.promiss.Items;

import java.util.ArrayList;

public class AppointmentItem {

    public static AppointmentItem item=new AppointmentItem();

    private  AppointmentItem(){}

    String name; //약속의 이름

    double latitude;
    double longitude;
    String address;//주소
    String address_detail;//상세주소

    String date;
    String time;
    int money;
    int money_cycle;

    int member_num;
    ArrayList<Integer> integers;

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public ArrayList<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(ArrayList<Integer> integers) {
        this.integers = integers;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMoney_cycle() {
        return money_cycle;
    }

    public void setMoney_cycle(int money_cycle) {
        this.money_cycle = money_cycle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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
