package com.example.foodplanner.presentation.profile.presenter;

import android.content.Context;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.presentation.profile.view.ViewProfile;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenterImp implements ProfilePresenter {

    private final ViewProfile view;
    private final Repository repository;
    private final Context context;
    private final CompositeDisposable disposable;

    public ProfilePresenterImp(ViewProfile view, Context context) {
        this.view = view;
        this.context = context;
        this.repository = new Repository(context);
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void loadUserData() {
        int userId = repository.getCurrentUserId();

        // Note: Repository doesn't have getUserById yet, would need to add it
        // For now keeping direct access or add method to Repository
        disposable.add(
                AppDatabase.getInstance(context)
                        .userDao().getUserById(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> view.showUserData(user),
                                throwable -> view.showError("Failed to load user data")
                        )
        );
    }

    @Override
    public void logout() {
        repository.logout();
        view.navigateToLogin();
    }

    @Override
    public void deleteAccount() {
        int userId = repository.getCurrentUserId();

        disposable.add(
                io.reactivex.rxjava3.core.Completable.fromAction(() -> {
                            // Delete via direct DB access (could be moved to Repository)
                            AppDatabase db = AppDatabase.getInstance(context);
                            db.favoriteMealDao().deleteFavoritesByUser(userId);
                            db.userDao().deleteUserById(userId);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            repository.logout();
                            view.navigateToLogin();
                        }, throwable -> view.showError("Delete failed"))
        );
    }

    @Override
    public void onDestroy() {
        disposable.clear();
    }
}