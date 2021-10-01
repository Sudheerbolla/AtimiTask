package com.atimitask.wsutils;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WSInterface {

//    @Headers("Content-Type: application/json")
    @GET(".")
    Call<JsonElement> getNewJoke();

}