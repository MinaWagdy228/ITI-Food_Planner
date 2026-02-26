package com.example.foodplanner.presentation.favorites;

import com.example.foodplanner.db.FavoriteMealEntity;

import java.util.List;

public interface ViewFavourite {
    void showFavorites(List<FavoriteMealEntity> meals);
    void showEmptyState();
    void showError(String message);
    void showLoading();
    void hideLoading();
}