package com.ygaps.travelapp.view;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewsTourInfo {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String userName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("review")
    @Expose
    private String review;

    @SerializedName("point")
    @Expose
    private int point;

    @SerializedName("createdOn")
    @Expose
    private long createOn;



    public ReviewsTourInfo(int id, String userName, String phone, String avatar, String review, int point, long createOn){
        this.id = id;
        this.userName = userName;
        this.phone = phone;
        this.avatar =avatar;
        this.review = review;
        this.point = point;
        this.createOn = createOn;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCreateOn(long createOn) {
        this.createOn = createOn;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public int getPoint() {
        return point;
    }

    public long getCreateOn() {
        return createOn;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getReview() {
        return review;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserName() {
        return userName;
    }

}
