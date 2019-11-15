package com.example.travelguide.network;

import com.example.travelguide.model.LoginRequest;
import com.example.travelguide.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
