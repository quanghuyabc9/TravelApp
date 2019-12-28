package com.ygaps.travelapp.network;

import com.ygaps.travelapp.model.LoginRequest;
import com.ygaps.travelapp.model.LoginResponse;
import com.ygaps.travelapp.view.User;
import com.ygaps.travelapp.view.UserResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/user/register")
    Call<UserResponse> createAccount(@Body User user);

    @FormUrlEncoded
    @POST("/tour/response/invitation")
    Call<ResponseBody> acceptTourInvitation(
            @Header("Authorization") String Authorization,
            @Field("tourId") String tourId,
            @Field("isAccepted") Boolean isAccepted
    );
}
