package com.skhu.cse.promiss.Items;

public class UserItem {

    int id; //기본키
    String name; // 이름
    boolean invite; //초대여부

    public UserItem(int id,String name,boolean invite)
    {
        this.id=id;
        this.name=name;
        this.invite=invite;
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

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }
}
