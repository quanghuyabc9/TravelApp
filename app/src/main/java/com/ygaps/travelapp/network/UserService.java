package com.ygaps.travelapp.network;

import com.ygaps.travelapp.model.LoginRequest;
import com.ygaps.travelapp.model.LoginResponse;
import com.ygaps.travelapp.view.User;
import com.ygaps.travelapp.view.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/user/register")
    Call<UserResponse> createAccount(@Body User user);

}
