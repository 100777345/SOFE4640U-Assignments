// Defines location object.
// Consists of id, latitude, longitude, and address.
// Includes get/set methods for all attributes.

package com.example.assignment2;

public class Location {
    private	int	id;
    private	double latitude;
    private	double longitude;
    private String address;

    public Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public Location(int id, double latitude, double longitude, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setLongitude(double latitude) { this.longitude = longitude; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

}