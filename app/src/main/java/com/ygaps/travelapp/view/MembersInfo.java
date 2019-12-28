package com.ygaps.travelapp.view;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MembersInfo {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String userName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("isHost")
    @Expose
    private boolean isHost;

    MembersInfo(int id, String userName, String phone, boolean isHost){
        this.id = id;
        this.userName = userName;
        this.phone = phone;
        this.isHost = isHost;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }
    public  boolean getIsHost(){
        return  isHost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
