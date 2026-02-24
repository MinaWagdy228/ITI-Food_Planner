package com.example.foodplanner.network;

import com.example.foodplanner.data.model.MealsResponse;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiServices {
    @GET("random.php")
    Call<MealsResponse> getRandomMeal();
}
