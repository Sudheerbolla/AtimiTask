package com.atimitask.wsutils;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface WSInterface {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET(".")
    Call<JsonElement> getNewJoke();

}