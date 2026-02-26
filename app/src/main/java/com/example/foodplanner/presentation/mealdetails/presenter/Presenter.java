package com.example.foodplanner.presentation.mealdetails.presenter;

import com.example.foodplanner.data.model.Meal;

public interface Presenter {
    void getMealDetails(String mealId);
    void onDestroy();

    void onFavoriteClicked(Meal meal);
}
