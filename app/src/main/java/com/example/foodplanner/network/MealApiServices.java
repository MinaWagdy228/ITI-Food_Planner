package com.example.foodplanner.network;

import com.example.foodplanner.data.model.CategoriesResponse;
import com.example.foodplanner.data.model.MealsResponse;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiServices {

    @GET("random.php")
    Single<MealsResponse> getRandomMeal();

    @GET("search.php")
    Single<MealsResponse> searchMealsByName(@Query("s") String name);

    @GET("search.php")
    Single<MealsResponse> searchMealsByFirstLetter(@Query("f") String letter);

    @GET("lookup.php")
    Single<MealsResponse> getMealDetailsById(@Query("i") String id);

    @GET("filter.php")
    Single<MealsResponse> filterByArea(@Query("a") String area);

    @GET("filter.php")
    Single<MealsResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealsResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("categories.php")
    Single<CategoriesResponse> getCategories();

    @GET("list.php?c=list")
    Single<CategoriesResponse> getCategoryList();

    @GET("list.php?a=list")
    Single<CategoriesResponse> getAreaList();

    @GET("list.php?i=list")
    Single<CategoriesResponse> getIngredientList();

}