package com.example.foodplanner.presentation.profile;

import android.content.Context;


import com.example.foodplanner.data.model.dataSource.local.SessionManager;
import com.example.foodplanner.db.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenterImp implements ProfilePresenter {

    private final ViewProfile view;
    private final AppDatabase db;
    private final SessionManager sessionManager;
    private final CompositeDisposable disposable;

    public ProfilePresenterImp(ViewProfile view, Context context) {
        this.view = view;
        this.db = AppDatabase.getInstance(context);
        this.sessionManager = new SessionManager(context);
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void loadUserData() {
        int userId = sessionManager.getUserId();

        disposable.add(
                db.userDao().getUserById(userId)
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
        sessionManager.logout();
        view.navigateToLogin();
    }

    @Override
    public void deleteAccount() {
        int userId = sessionManager.getUserId();

        disposable.add(
                io.reactivex.rxjava3.core.Completable.fromAction(() -> {
                            db.favoriteMealDao().deleteFavoritesByUser(userId);
                            db.userDao().deleteUserById(userId);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            sessionManager.logout();
                            view.navigateToLogin();
                        }, throwable -> view.showError("Delete failed"))
        );
    }

    @Override
    public void onDestroy() {
        disposable.clear();
    }
}