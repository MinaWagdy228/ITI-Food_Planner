package com.example.foodplanner.presentation.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.databinding.FragmentHomeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding bindings;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bindings = FragmentHomeBinding.inflate(inflater, container, false);
        return bindings.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Network.getApiServices().getRandomMeal().enqueue(
//                new Callback<MealsResponse>() {
//                    @Override
//                    public void onResponse(Call<MealsResponse> call, Response<MealsResponse> response) {
//                        if(response.isSuccessful() && response.body() != null) {
//                            Meal mealName = response.body().getMeals().get(0);
//                            Log.d("HomeFragment", "Random Meal: " + mealName.toString()
//                            );
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<MealsResponse> call, Throwable t) {
//
//                    }
//                }
//        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bindings = null;
    }
}