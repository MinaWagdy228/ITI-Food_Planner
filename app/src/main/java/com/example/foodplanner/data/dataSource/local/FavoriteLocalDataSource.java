package com.example.foodplanner.data.dataSource.local;

import com.example.foodplanner.data.db.FavoriteMealDao;
import com.example.foodplanner.data.entity.FavoriteMealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public class FavoriteLocalDataSource {

    private final FavoriteMealDao dao;

    public FavoriteLocalDataSource(FavoriteMealDao dao) {
        this.dao = dao;
    }

    public void insertFavorite(FavoriteMealEntity meal) {
        dao.insertFavorite(meal);
    }

    public void deleteFavorite(String mealId, int userId) {
        dao.deleteFavorite(mealId, userId);
    }

    public Flowable<Boolean> isFavorite(String mealId, int userId) {
        return dao.isFavorite(mealId, userId);
    }

    public Flowable<List<FavoriteMealEntity>> getFavoritesByUser(int userId) {
        return dao.getFavoritesByUser(userId);
    }
}