package com.example.foodplanner.presentation.favorites.view;

import com.example.foodplanner.data.entity.FavoriteMealEntity;

import java.util.List;

public interface ViewFavourite {
    void showFavorites(List<FavoriteMealEntity> meals);
    void showEmptyState();
    void showError(String message);
    void showLoading();
    void hideLoading();
}