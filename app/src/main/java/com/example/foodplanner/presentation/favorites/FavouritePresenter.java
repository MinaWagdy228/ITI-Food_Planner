package com.example.foodplanner.presentation.favorites;

import com.example.foodplanner.db.FavoriteMealEntity;

public interface FavouritePresenter {
    void loadFavorites();
    void removeFavorite(FavoriteMealEntity meal);
    void onDestroy();
}