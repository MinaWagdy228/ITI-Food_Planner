package com.example.foodplanner.presentation.search.presenter;

import android.content.Context;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.mapper.FavoriteMapper;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.search.view.ViewSearch;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImp implements SearchPresenter {

    private final ViewSearch view;
    private final Repository repository;
    private final CompositeDisposable compositeDisposable;

    // Cache for search results
    private List<Meal> cachedAllMeals = null;
    private String lastSearchQuery = null;
    private List<Meal> lastSearchResults = null;

    public SearchPresenterImp(ViewSearch view, Context context) {
        this.view = view;
        this.repository = new Repository(context);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadAllMeals() {
        // Return cached data if available
        if (cachedAllMeals != null && !cachedAllMeals.isEmpty()) {
            checkAndUpdateFavoriteStatus(cachedAllMeals);
            return;
        }

        view.showLoading();

        // Load meals with single API call for better performance
        compositeDisposable.add(
                repository.searchMealsByFirstLetter("a")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    view.hideLoading();
                                    if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
                                        cachedAllMeals = response.getMeals();
                                        checkAndUpdateFavoriteStatus(cachedAllMeals);
                                    } else {
                                        view.showEmptyState();
                                    }
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError("Failed to load meals");
                                }
                        )
        );
    }

    @Override
    public void searchMeals(String query) {

        if (query == null || query.trim().isEmpty()) {
            loadAllMeals();
            return;
        }

        // Return cached search results if query matches
        if (query.equals(lastSearchQuery) && lastSearchResults != null) {
            if (lastSearchResults.isEmpty()) {
                view.showEmptyState();
            } else {
                checkAndUpdateFavoriteStatus(lastSearchResults);
            }
            return;
        }

        view.showLoading();

        compositeDisposable.add(
                repository.searchMealsByName(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    view.hideLoading();
                                    lastSearchQuery = query;
                                    if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
                                        lastSearchResults = response.getMeals();
                                        checkAndUpdateFavoriteStatus(lastSearchResults);
                                    } else {
                                        lastSearchResults = new ArrayList<>();
                                        view.showEmptyState();
                                    }
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError("Search failed: " + throwable.getMessage());
                                }
                        )
        );
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
                                    // Update cached meals if they exist
                                    if (cachedAllMeals != null) {
                                        updateMealInCache(cachedAllMeals, meal.getIdMeal(), newState);
                                    }
                                    if (lastSearchResults != null) {
                                        updateMealInCache(lastSearchResults, meal.getIdMeal(), newState);
                                        view.showMeals(lastSearchResults);
                                    } else if (cachedAllMeals != null) {
                                        view.showMeals(cachedAllMeals);
                                    }
                                },
                                error -> {
                                    // Optionally handle error
                                }
                        )
        );
    }

    private void updateMealInCache(List<Meal> meals, String mealId, boolean favorite) {
        for (Meal cachedMeal : meals) {
            if (cachedMeal.getIdMeal().equals(mealId)) {
                cachedMeal.setFavorite(favorite);
                break;
            }
        }
    }

    /**
     * Check favorite status for all meals in the list and update UI
     */
    private void checkAndUpdateFavoriteStatus(List<Meal> meals) {
        if (meals == null || meals.isEmpty()) {
            view.showMeals(meals);
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
                                updatedMeals -> view.showMeals(updatedMeals),
                                error -> view.showMeals(meals)
                        )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        cachedAllMeals = null;
        lastSearchQuery = null;
        lastSearchResults = null;
    }
}