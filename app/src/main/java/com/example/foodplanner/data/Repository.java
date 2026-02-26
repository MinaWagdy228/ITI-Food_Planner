package com.example.foodplanner.data;

import android.content.Context;

import com.example.foodplanner.data.dataSource.local.FavoriteLocalDataSource;
import com.example.foodplanner.data.dataSource.local.SessionManager;
import com.example.foodplanner.data.dataSource.local.UserLocalDataSource;
import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.entity.FavoriteMealEntity;
import com.example.foodplanner.data.entity.UserEntity;
import com.example.foodplanner.data.model.CategoriesResponse;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.data.network.MealApiServices;
import com.example.foodplanner.data.network.Network;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class Repository {

    // Data Sources
    private final FavoriteLocalDataSource favoriteLocalDataSource;
    private final UserLocalDataSource userLocalDataSource;
    private final SessionManager sessionManager;
    private final MealApiServices apiServices;

    public Repository(Context context) {
        // Initialize database
        AppDatabase db = AppDatabase.getInstance(context);

        // Initialize local data sources
        this.favoriteLocalDataSource = new FavoriteLocalDataSource(db.favoriteMealDao());
        this.userLocalDataSource = new UserLocalDataSource(db.userDao());
        this.sessionManager = new SessionManager(context);

        // Initialize remote data source
        this.apiServices = Network.getApiService();
    }

    public Single<MealsResponse> getRandomMeal() {
        return apiServices.getRandomMeal();
    }

    public Single<MealsResponse> searchMealsByName(String name) {
        return apiServices.searchMealsByName(name);
    }

    public Single<MealsResponse> searchMealsByFirstLetter(String letter) {
        return apiServices.searchMealsByFirstLetter(letter);
    }

    public Single<MealsResponse> getMealDetailsById(String id) {
        return apiServices.getMealDetailsById(id);
    }

    public Single<MealsResponse> filterMealsByArea(String area) {
        return apiServices.filterByArea(area);
    }

    public Single<MealsResponse> filterMealsByCategory(String category) {
        return apiServices.filterByCategory(category);
    }

    public Single<MealsResponse> filterMealsByIngredient(String ingredient) {
        return apiServices.filterByIngredient(ingredient);
    }

    public Single<CategoriesResponse> getCategories() {
        return apiServices.getCategories();
    }

    public Single<CategoriesResponse> getCategoryList() {
        return apiServices.getCategoryList();
    }

    public Single<CategoriesResponse> getAreaList() {
        return apiServices.getAreaList();
    }

    public Single<CategoriesResponse> getIngredientList() {
        return apiServices.getIngredientList();
    }

    public Flowable<List<FavoriteMealEntity>> getFavoritesByUser(int userId) {
        return favoriteLocalDataSource.getFavoritesByUser(userId);
    }

    public Completable insertFavorite(FavoriteMealEntity meal) {
        return Completable.fromAction(() -> favoriteLocalDataSource.insertFavorite(meal));
    }

    public Completable deleteFavorite(String mealId, int userId) {
        return Completable.fromAction(() -> favoriteLocalDataSource.deleteFavorite(mealId, userId));
    }

    public Flowable<Boolean> isMealFavorite(String mealId, int userId) {
        return favoriteLocalDataSource.isFavorite(mealId, userId);
    }

    public Single<Long> registerUser(UserEntity user) {
        return userLocalDataSource.registerUser(user);
    }

    public Maybe<UserEntity> login(String email, String password) {
        return userLocalDataSource.login(email, password);
    }

    public Maybe<UserEntity> getUserByEmail(String email) {
        return userLocalDataSource.getUserByEmail(email);
    }

    public void saveUserId(int userId) {
        sessionManager.saveUserId(userId);
    }

    public int getCurrentUserId() {
        return sessionManager.getUserId();
    }

    public boolean isUserLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void logout() {
        sessionManager.logout();
    }
}
