package com.example.foodplanner.presentation.filteredmeals.presenter;

import android.content.Context;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.data.mapper.FavoriteMapper;
import com.example.foodplanner.presentation.filteredmeals.view.ViewFilteredMeal;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PresenterImp implements Presenter {
    private ViewFilteredMeal viewFilteredMeal;
    private final Repository repository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Cache to avoid redundant network calls
    private String lastFilterValue = null;
    private String lastFilterType = null;
    private List<Meal> cachedMeals = null;

    public PresenterImp(ViewFilteredMeal viewFilteredMeal, Context context) {
        this.viewFilteredMeal = viewFilteredMeal;
        this.repository = new Repository(context);
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
                checkAndUpdateFavoriteStatus(cachedMeals);
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
                return repository.filterMealsByArea(filterValue);
            case "ingredient":
                return repository.filterMealsByIngredient(filterValue);
            case "category":
            default:
                return repository.filterMealsByCategory(filterValue);
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

            checkAndUpdateFavoriteStatus(filteredMealList);
        }
    }

    private void handleError(Throwable throwable) {
        if (viewFilteredMeal != null) {
            viewFilteredMeal.hideLoading();
            viewFilteredMeal.showError(throwable.getMessage());
        }
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        boolean newState = !meal.isFavorite();
        meal.setFavorite(newState);

        int currentUserId = repository.getCurrentUserId();

        compositeDisposable.add(
                io.reactivex.rxjava3.core.Completable.fromAction(() -> {
                            if (newState) {
                                repository.insertFavorite(
                                        FavoriteMapper.toEntity(meal, currentUserId)
                                ).blockingAwait();
                            } else {
                                repository.deleteFavorite(
                                        meal.getIdMeal(),
                                        currentUserId
                                ).blockingAwait();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Update cached meal if it exists
                                    if (cachedMeals != null) {
                                        for (Meal cachedMeal : cachedMeals) {
                                            if (cachedMeal.getIdMeal().equals(meal.getIdMeal())) {
                                                cachedMeal.setFavorite(newState);
                                                break;
                                            }
                                        }
                                        viewFilteredMeal.showMeals(cachedMeals);
                                    }
                                },
                                error -> viewFilteredMeal.showError(error.getMessage())
                        )
        );
    }

    /**
     * Check favorite status for all meals in the list and update UI
     */
    private void checkAndUpdateFavoriteStatus(List<Meal> meals) {
        if (meals == null || meals.isEmpty()) {
            viewFilteredMeal.showMeals(meals);
            return;
        }

        int currentUserId = repository.getCurrentUserId();

        // Check favorite status for each meal
        compositeDisposable.add(
                io.reactivex.rxjava3.core.Observable.fromIterable(meals)
                        .flatMapSingle(meal ->
                                repository.isMealFavorite(meal.getIdMeal(), currentUserId)
                                        .take(1)
                                        .defaultIfEmpty(false)
                                        .singleOrError()
                                        .map(isFavorite -> {
                                            meal.setFavorite(isFavorite);
                                            return meal;
                                        })
                                        .onErrorReturnItem(meal)
                        )
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updatedMeals -> viewFilteredMeal.showMeals(updatedMeals),
                                error -> viewFilteredMeal.showMeals(meals)
                        )
        );
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
