package com.example.sharetrip.api;

import com.example.sharetrip.api.auth.LoginRequest;
import com.example.sharetrip.api.auth.LoginResponse;
import com.example.sharetrip.api.auth.SignupRequest;
import com.example.sharetrip.api.auth.SignupResponse;
import com.example.sharetrip.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("user")
    Call<User> getUser(@Query("id") int userId);

    @Headers("Content-Type: application/json")
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @POST("/api/users/register")
    Call<SignupResponse> register(@Body SignupRequest signupRequest);
}
