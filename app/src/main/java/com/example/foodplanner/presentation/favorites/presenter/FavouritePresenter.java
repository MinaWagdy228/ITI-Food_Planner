package com.example.foodplanner.presentation.favorites.presenter;

import com.example.foodplanner.data.entity.FavoriteMealEntity;

public interface FavouritePresenter {
    void loadFavorites();
    void removeFavorite(FavoriteMealEntity meal);
    void onDestroy();
}