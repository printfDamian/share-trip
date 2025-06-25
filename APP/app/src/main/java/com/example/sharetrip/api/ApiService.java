package com.example.sharetrip.api;

import com.example.sharetrip.api.auth.LoginRequest;
import com.example.sharetrip.api.auth.LoginResponse;
import com.example.sharetrip.api.auth.SignupRequest;
import com.example.sharetrip.api.auth.SignupResponse;
import com.example.sharetrip.api.chatbot.ChatbotRequest;
import com.example.sharetrip.api.chatbot.ChatbotResponse;
import com.example.sharetrip.api.markers.MarksResponse;
import com.example.sharetrip.api.post.AddPostRequest;
import com.example.sharetrip.api.post.AddPostResponse;
import com.example.sharetrip.api.post.PostsResponse;
import com.example.sharetrip.api.user.UpdateRequest;
import com.example.sharetrip.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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

    @GET("/api/posts")
    Call<PostsResponse> getAllPosts();

    @GET("map/markers")
    Call<MarksResponse> getAllMarkers();

    @Headers("Content-Type: application/json")
    @POST("posts")
    Call<AddPostResponse> newPost(@Body AddPostRequest addPostRequest);

    @Headers("Content-Type: application/json")
    @POST("chatbot")
    Call<ChatbotResponse> askQuestion(@Body ChatbotRequest chatbotRequest);

    @DELETE("users")
    Call<Void> deleteUser();

    @Headers("Content-Type: application/json")
    @PUT("users")
    Call<SignupResponse> updateUser(@Body UpdateRequest updateRequest);
}
