package com.example.foodplanner.presentation.filteredmeals;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.network.Network;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PresenterImp implements Presenter {
    private ViewFilteredMeal viewFilteredMeal;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Cache to avoid redundant network calls
    private String lastCategory = null;
    private List<Meal> cachedMeals = null;

    public PresenterImp(ViewFilteredMeal viewFilteredMeal) {
        this.viewFilteredMeal = viewFilteredMeal;
    }

    @Override
    public void getMealsByCategory(String category) {
        // Return cached data if same category
        if (category != null && category.equals(lastCategory) && cachedMeals != null) {
            if (viewFilteredMeal != null) {
                viewFilteredMeal.showMeals(cachedMeals);
            }
            return;
        }

        if (viewFilteredMeal != null) {
            viewFilteredMeal.showLoading();
        }
        compositeDisposable.add(
                Network.getApiService()
                        .filterByCategory(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> handleSuccess(response, category),
                                this::handleError
                        )
        );
    }

    private void handleSuccess(MealsResponse mealsResponse, String category) {
        if (viewFilteredMeal != null) {
            viewFilteredMeal.hideLoading();
        }
        if (mealsResponse != null && mealsResponse.getMeals() != null) {
            List<Meal> filteredMealList = mealsResponse.getMeals();

            // Cache the data
            lastCategory = category;
            cachedMeals = filteredMealList;

            viewFilteredMeal.showMeals(filteredMealList);
        }
    }

    private void handleError(Throwable throwable) {
        if (viewFilteredMeal != null) {
            viewFilteredMeal.hideLoading();
            viewFilteredMeal.showError(throwable.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        cachedMeals = null;
        lastCategory = null;
        viewFilteredMeal = null;
    }
}
