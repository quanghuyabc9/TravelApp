package com.ygaps.travelapp.view;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("id")
    private String UserId;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
