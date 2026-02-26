package com.example.foodplanner.presentation.search;

import android.content.Context;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.dataSource.local.FavoriteLocalDataSource;
import com.example.foodplanner.data.model.dataSource.local.SessionManager;
import com.example.foodplanner.data.model.mapper.FavoriteMapper;
import com.example.foodplanner.db.AppDatabase;
import com.example.foodplanner.network.MealApiServices;
import com.example.foodplanner.network.Network;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImp implements SearchPresenter {

    private final ViewSearch view;
    private final MealApiServices api;
    private final CompositeDisposable compositeDisposable;
    private final FavoriteLocalDataSource localDataSource;
    private final SessionManager sessionManager;
    private final int currentUserId;

    // Cache for search results
    private List<Meal> cachedAllMeals = null;
    private String lastSearchQuery = null;
    private List<Meal> lastSearchResults = null;

    public SearchPresenterImp(ViewSearch view, Context context) {
        this.view = view;
        this.api = Network.getApiService();
        this.compositeDisposable = new CompositeDisposable();

        AppDatabase db = AppDatabase.getInstance(context);
        this.localDataSource = new FavoriteLocalDataSource(db.favoriteMealDao());
        this.sessionManager = new SessionManager(context);
        this.currentUserId = sessionManager.getUserId();
    }

    @Override
    public void loadAllMeals() {
        // Return cached data if available
        if (cachedAllMeals != null && !cachedAllMeals.isEmpty()) {
            view.showMeals(cachedAllMeals);
            return;
        }

        view.showLoading();

        List<Meal> allMeals = new ArrayList<>();

        // Load meals by first letters (a–z)
        for (char c = 'a'; c <= 'c'; c++) { // start small for performance
            compositeDisposable.add(
                    api.searchMealsByName(String.valueOf(c))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if (response != null && response.getMeals() != null) {
                                    allMeals.addAll(response.getMeals());
                                    cachedAllMeals = new ArrayList<>(allMeals);
                                    view.showMeals(allMeals);
                                }
                                view.hideLoading();
                            }, throwable -> view.showError("Failed to load meals"))
            );
        }
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
                view.showMeals(lastSearchResults);
            }
            return;
        }

        compositeDisposable.add(
                api.searchMealsByName(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            lastSearchQuery = query;
                            if (response != null && response.getMeals() != null) {
                                lastSearchResults = response.getMeals();
                                view.showMeals(response.getMeals());
                            } else {
                                lastSearchResults = new ArrayList<>();
                                view.showEmptyState();
                            }
                        }, throwable -> view.showError("Search failed"))
        );
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        boolean newState = !meal.isFavorite();
        meal.setFavorite(newState);

        compositeDisposable.add(
                io.reactivex.rxjava3.core.Completable.fromAction(() -> {
                            if (newState) {
                                localDataSource.insertFavorite(
                                        FavoriteMapper.toEntity(meal, currentUserId)
                                );
                            } else {
                                localDataSource.deleteFavorite(
                                        meal.getIdMeal(),
                                        currentUserId
                                );
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

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        cachedAllMeals = null;
        lastSearchQuery = null;
        lastSearchResults = null;
    }
}