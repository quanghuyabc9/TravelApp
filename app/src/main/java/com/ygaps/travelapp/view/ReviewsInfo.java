package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewsInfo {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("name")
    @Expose
    private String userName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("feedback")
    @Expose
    private String feedback;

    @SerializedName("point")
    @Expose
    private int point;

    @SerializedName("createdOn")
    @Expose
    private long createOn;

    public ReviewsInfo(int id, String userId, String userName, String phone, String avatar, String feedback, int point, long createOn){
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.avatar =avatar;
        this.feedback = feedback;
        this.point = point;
        this.createOn = createOn;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCreateOn(long createOn) {
        this.createOn = createOn;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
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

    public String getFeedback() {
        return feedback;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserName() {
        return userName;
    }

}
