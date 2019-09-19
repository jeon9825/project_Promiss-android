package com.skhu.cse.promiss.Items;

public class SearchAddressItem {
    String name;
    String detail;

    double latitude;
    double longitude;

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

    public SearchAddressItem(String name, String detail,double latitude,double longitude) {
        this.name = name;
        this.detail = detail;
        this.latitude=latitude;
        this.longitude =longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
