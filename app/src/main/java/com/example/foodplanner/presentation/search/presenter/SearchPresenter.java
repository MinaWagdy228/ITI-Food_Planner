package com.example.foodplanner.presentation.search.presenter;

import com.example.foodplanner.data.model.Meal;

public interface SearchPresenter {
    void loadAllMeals(); // default state
    void searchMeals(String query);
    void onFavoriteClicked(Meal meal);
    void onDestroy();
}

