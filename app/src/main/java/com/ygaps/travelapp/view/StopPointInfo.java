package com.ygaps.travelapp.view;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopPointInfo {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("provinceId")
    @Expose
    private int provinceId;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("long")
    @Expose
    private String longitude;

    @SerializedName("arrivalAt")
    @Expose
    private long arriveAt;

    @SerializedName("leaveAt")
    @Expose
    private long leaveAt;

    @SerializedName("serviceTypeId")
    @Expose
    private int serviceTypeId;

    @SerializedName("minCost")
    @Expose
    private long minCost;

    @SerializedName("maxCost")
    @Expose
    private long maxCost;

    @SerializedName("serviceId")
    @Expose
    private int serviceId;

    StopPointInfo(String name, String address, int provinceId, String lat, String longitude, long arriveAt, long leaveAt, int serviceTypeId, long minCost, long maxCost){
        this.name = name;
        this.address = address;
        this.provinceId = provinceId;
        this.lat = lat;
        this.longitude = longitude;
        this.arriveAt = arriveAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.minCost = minCost;
        this.maxCost = maxCost;
    }

    StopPointInfo(int id, String name, String address, int provinceId, String lat, String longitude, long arriveAt, long leaveAt, int serviceTypeId, long minCost, long maxCost){
        this.id = id;
        this.name = name;
        this.address = address;
        this.provinceId = provinceId;
        this.lat = lat;
        this.longitude = longitude;
        this.arriveAt = arriveAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.minCost = minCost;
        this.maxCost = maxCost;
    }

    StopPointInfo(String name, String address, int provinceId, String lat, String longitude, long arriveAt, long leaveAt, int serviceTypeId, long minCost, long maxCost, int serviceId){
        this.name = name;
        this.address = address;
        this.provinceId = provinceId;
        this.lat = lat;
        this.longitude = longitude;
        this.arriveAt = arriveAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.serviceId = serviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLongitude(String longitude) {
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

    public void setId(int id) {
        this.id = id;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
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

    public String getLat() {
        return lat;
    }

    public String getLongitude() {
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

    public int getId() {
        return id;
    }

    public int getServiceId() {
        return serviceId;
    }
}