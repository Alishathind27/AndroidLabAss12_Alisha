package com.example.alisha_androidlabassign;

import java.util.ArrayList;

public class FavPlace_Address {
    private  String address;
    private double latitude;
    private  double longitude;
    private String date;
    private String visited;
    private  int id;

    public FavPlace_Address(int id,double latitude, double longitude, String address,String date) {
       this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.date = date;
    }

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getDate(){
        return date;
    }

    public int getId(){
        return id;
    }

    public  static ArrayList<FavPlace_Address> selected_place = new ArrayList<>();


}
