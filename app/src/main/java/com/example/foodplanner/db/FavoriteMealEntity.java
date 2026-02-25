package com.example.foodplanner.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_meals")
public class FavoriteMealEntity {

    @PrimaryKey
    private String idMeal;

    private int userId;
    private String strMeal;
    private String strMealThumb;

    public FavoriteMealEntity(String idMeal, int userId, String strMeal, String strMealThumb) {
        this.idMeal = idMeal;
        this.userId = userId;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public int getUserId() {
        return userId;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }
}