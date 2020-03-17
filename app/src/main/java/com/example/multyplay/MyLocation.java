package com.example.multyplay;

public class MyLocation {

    private double longitute;
    private double latitude;
    private String address;

    public MyLocation() {
    }

    public MyLocation(double longitute, double latitude, String address) {
        setLongitute(longitute);
        setLatitude(latitude);
        setAddress(address);
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
