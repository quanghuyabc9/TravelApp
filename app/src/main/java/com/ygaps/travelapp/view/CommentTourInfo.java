package com.ygaps.travelapp.view;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentTourInfo {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String userName;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("createdOn")
    @Expose
    private long createOn;



    public CommentTourInfo(int id, String userName, String avatar, String comment, long createOn){
        this.id = id;
        this.userName = userName;
        this.avatar =avatar;
        this.createOn = createOn;
        this.comment = comment;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreateOn(long createOn) {
        this.createOn = createOn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public long getCreateOn() {
        return createOn;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

}
