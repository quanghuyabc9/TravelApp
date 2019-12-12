package com.example.travelguide.view;

public class StopPointInfo {
    private String spname;
    private String address;
    private int provinceId;
    private double latitude;
    private double longitude;
    private long arriveAt;
    private long leaveAt;
    private int serviceTypeId;
    private long minCost;
    private long maxCost;
    StopPointInfo(String spname, String address, int provinceId, double latitude, double longitude, long arriveAt, long leaveAt, int serviceTypeId, long minCost, long maxCost){
        this.spname = spname;
        this.address = address;
        this.provinceId = provinceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arriveAt = arriveAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.minCost = minCost;
        this.maxCost = maxCost;
    }

    public void setSpname(String spname) {
        this.spname = spname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setArriveAt(long arriveAt) {
        this.arriveAt = arriveAt;
    }

    public void setLeaveAt(long leaveAt) {
        this.leaveAt = leaveAt;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public void setMinCost(long minCost) {
        this.minCost = minCost;
    }

    public void setMaxCost(long maxCost) {
        this.maxCost = maxCost;
    }

    public String getSpname() {
        return spname;
    }

    public String getAddress() {
        return address;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getArriveAt() {
        return arriveAt;
    }

    public long getLeaveAt() {
        return leaveAt;
    }

    public long getMaxCost() {
        return maxCost;
    }

    public long getMinCost() {
        return minCost;
    }
}