package com.example.foodplanner.presentation.filteredmeals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodplanner.databinding.FragmentFilteredMealsBinding;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.presentation.common.OnMealClickListener;

import java.util.List;

public class FilteredMealsFragment extends Fragment implements ViewFilteredMeal, OnMealClickListener {

    private FragmentFilteredMealsBinding binding;
    private Presenter presenter;
    private MealsAdapter mealsAdapter;
    private String filterValue;
    private String filterType;

    public FilteredMealsFragment() {
    }

    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater,
                                          ViewGroup container,
                                          Bundle savedInstanceState) {
        binding = FragmentFilteredMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new PresenterImp(this);
        FilteredMealsFragmentArgs args =
                FilteredMealsFragmentArgs.fromBundle(getArguments());
        filterValue = args.getFilterValue();
        filterType = args.getFilterType();

        // Set appropriate title based on filter type
        binding.tvTitle.setText(getTitleForFilter(filterValue, filterType));

        setupRecyclerView();
        loadMeals();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload meals when returning from back stack (e.g., from MealDetails)
        if (filterValue != null && presenter != null) {
            loadMeals();
        }
    }

    private void loadMeals() {
        switch (filterType) {
            case "area":
                presenter.getMealsByArea(filterValue);
                break;
            case "ingredient":
                presenter.getMealsByIngredient(filterValue);
                break;
            case "category":
            default:
                presenter.getMealsByCategory(filterValue);
                break;
        }
    }

    private String getTitleForFilter(String value, String type) {
        switch (type) {
            case "area":
                return value + " Cuisine";
            case "ingredient":
                return value + " Dishes";
            case "category":
            default:
                return value + " Meals";
        }
    }

    private void setupRecyclerView() {
        mealsAdapter = new MealsAdapter(this);

        binding.rvMeals.setAdapter(mealsAdapter);
        binding.rvMeals.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
    }

    @Override
    public void showMeals(List<Meal> meals) {
        mealsAdapter.setMeals(meals);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        if (binding != null && binding.loadingCard != null) {
            binding.loadingCard.setVisibility(View.VISIBLE);
            binding.rvMeals.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null && binding.loadingCard != null) {
            binding.loadingCard.setVisibility(View.GONE);
            binding.rvMeals.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        binding = null;
    }

    @Override
    public void onMealClick(Meal meal) {
        FilteredMealsFragmentDirections.ActionFilteredMealsFragmentToMealDetailsFragment action =
                FilteredMealsFragmentDirections
                        .actionFilteredMealsFragmentToMealDetailsFragment(meal.getIdMeal());

        NavHostFragment.findNavController(this).navigate(action);
    }
}