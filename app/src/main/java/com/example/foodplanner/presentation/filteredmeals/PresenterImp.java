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
    private String lastFilterValue = null;
    private String lastFilterType = null;
    private List<Meal> cachedMeals = null;

    public PresenterImp(ViewFilteredMeal viewFilteredMeal) {
        this.viewFilteredMeal = viewFilteredMeal;
    }

    @Override
    public void getMealsByCategory(String category) {
        getMealsWithFilter(category, "category");
    }

    @Override
    public void getMealsByArea(String area) {
        getMealsWithFilter(area, "area");
    }

    @Override
    public void getMealsByIngredient(String ingredient) {
        getMealsWithFilter(ingredient, "ingredient");
    }

    private void getMealsWithFilter(String filterValue, String filterType) {
        // Return cached data if same filter
        if (filterValue != null && filterValue.equals(lastFilterValue)
                && filterType.equals(lastFilterType) && cachedMeals != null) {
            if (viewFilteredMeal != null) {
                viewFilteredMeal.showMeals(cachedMeals);
            }
            return;
        }

        if (viewFilteredMeal != null) {
            viewFilteredMeal.showLoading();
        }

        // Choose the correct API endpoint based on filter type
        compositeDisposable.add(
                getApiCall(filterValue, filterType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> handleSuccess(response, filterValue, filterType),
                                this::handleError
                        )
        );
    }

    private io.reactivex.rxjava3.core.Single<MealsResponse> getApiCall(String filterValue, String filterType) {
        switch (filterType) {
            case "area":
                return Network.getApiService().filterByArea(filterValue);
            case "ingredient":
                return Network.getApiService().filterByIngredient(filterValue);
            case "category":
            default:
                return Network.getApiService().filterByCategory(filterValue);
        }
    }

    private void handleSuccess(MealsResponse mealsResponse, String filterValue, String filterType) {
        if (viewFilteredMeal != null) {
            viewFilteredMeal.hideLoading();
        }
        if (mealsResponse != null && mealsResponse.getMeals() != null) {
            List<Meal> filteredMealList = mealsResponse.getMeals();

            // Cache the data
            lastFilterValue = filterValue;
            lastFilterType = filterType;
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
        lastFilterValue = null;
        lastFilterType = null;
        viewFilteredMeal = null;
    }
}
