package com.ygaps.travelapp.view;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageItem {
    @SerializedName("name")
    @Expose
    private String userName;

    @SerializedName("notification")
    @Expose
    private String notification;

    public MessageItem(String userName, String notification){
        this.userName = userName;
        this.notification = notification;
    }

    public String getUserName() {
        return userName;
    }

    public String getNotification() {
        return notification;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
