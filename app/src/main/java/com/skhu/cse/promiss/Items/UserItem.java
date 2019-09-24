package com.skhu.cse.promiss.Items;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class UserItem implements Parcelable {

    int id; //기본키
    String name; // 이름
    boolean invite; //초대여부

    public UserItem(){

    }

    public UserItem(Parcel parcel)
    {
            id = parcel.readInt();
            name = parcel.readString();
            invite = true;
    }
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

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);

    }
    public static final Creator<UserItem> CREATOR = new Creator<UserItem>() {

        @Override
        public UserItem createFromParcel(Parcel parcel) {
            return new UserItem(parcel);
        }

        @Override
        public UserItem[] newArray(int i) {
            return new UserItem[i];
        }
    };

    }
