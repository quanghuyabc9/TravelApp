package com.ygaps.travelapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("userId")
    private int userId;
    @SerializedName("emailVerified")
    private boolean emailVerified;
    @SerializedName("phoneVerified")
    private boolean phoneVerified;
    @SerializedName("token")
    private String token;

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return this.userId;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    public boolean getEmailVerified() {
        return this.emailVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }
    public boolean getPhoneVerified() {
        return this.phoneVerified;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return this.token;
    }
}
