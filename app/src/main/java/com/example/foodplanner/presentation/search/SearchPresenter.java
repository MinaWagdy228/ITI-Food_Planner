package com.example.foodplanner.presentation.search;

public interface SearchPresenter {
    void loadAllMeals(); // default state
    void searchMeals(String query);
    void onDestroy();
}