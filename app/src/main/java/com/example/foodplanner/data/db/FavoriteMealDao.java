package com.example.foodplanner.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.foodplanner.data.entity.FavoriteMealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface FavoriteMealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteMealEntity meal);

    @Query("DELETE FROM favorite_meals WHERE idMeal = :mealId AND userId = :userId")
    void deleteFavorite(String mealId, int userId);

    @Query("SELECT * FROM favorite_meals WHERE userId = :userId")
    Flowable<List<FavoriteMealEntity>> getFavoritesByUser(int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE idMeal = :mealId AND userId = :userId)")
    Flowable<Boolean> isFavorite(String mealId, int userId);
    @Query("DELETE FROM favorite_meals WHERE userId = :userId")
    void deleteFavoritesByUser(int userId);
}