package com.skhu.cse.promiss.Items;

public class UserData {
    int id; //기본키
    String name; //이름
    String date; //마지막 접속 일자

    public static UserData shared = new UserData();

    private UserData(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
