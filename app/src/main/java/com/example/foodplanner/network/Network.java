package com.example.foodplanner.network;

import retrofit2.Retrofit;

public class Network {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static ApiServices apiServices;

    public static ApiServices getApiServices() {
        if (apiServices == null) {
            apiServices = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()
                    .create(ApiServices.class);
        }
        return apiServices;
    }
}
