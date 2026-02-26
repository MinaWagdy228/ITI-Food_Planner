package com.example.foodplanner.presentation.register.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.entity.UserEntity;
import com.example.foodplanner.presentation.register.view.ViewRegister;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterPresenterImp implements RegisterPresenter {

    private final ViewRegister view;
    private final Repository repository;
    private final CompositeDisposable compositeDisposable;

    public RegisterPresenterImp(ViewRegister view, Context context) {
        this.view = view;
        this.repository = new Repository(context);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void register(String name, String email, String password, String confirmPassword) {

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            view.showError("All fields are required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Invalid email format");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.showError("Passwords do not match");
            return;
        }

        view.showLoading();

        // Check if email already exists via Repository
        compositeDisposable.add(
                repository.getUserByEmail(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    view.hideLoading();
                                    view.showError("Email already registered");
                                },
                                throwable -> {},
                                () -> insertNewUser(name, email, password)
                        )
        );
    }

    private void insertNewUser(String name, String email, String password) {
        compositeDisposable.add(
                repository.registerUser(new UserEntity(name, email, password))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userId -> {
                            view.hideLoading();
                            view.onRegisterSuccess();
                            Log.d("RegisterPresenter", "User registered with ID: " + userId);
                        }, throwable -> {
                            view.hideLoading();
                            view.showError("Registration failed: " + throwable.getMessage());
                            Log.e("RegisterPresenter", "Error: " + throwable.getMessage());
                        })
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}