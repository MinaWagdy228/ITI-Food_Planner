package com.example.foodplanner.data.mapper;

import com.example.foodplanner.data.entity.FavoriteMealEntity;
import com.example.foodplanner.data.model.Meal;

public class FavoriteMapper {

    public static FavoriteMealEntity toEntity(Meal meal, int userId) {
        return new FavoriteMealEntity(
                meal.getIdMeal(),
                userId,
                meal.getStrMeal(),
                meal.getStrMealThumb()
        );
    }
}