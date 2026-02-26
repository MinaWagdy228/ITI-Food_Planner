package com.example.foodplanner.presentation.search.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.FragmentSearchBinding;
import com.example.foodplanner.presentation.common.OnMealClickListener;
import com.example.foodplanner.presentation.filteredmeals.view.MealsAdapter;
import com.example.foodplanner.presentation.home.view.FilterBottomSheetDialog;
import com.example.foodplanner.presentation.mealdetails.view.OnFavoriteClickListener;
import com.example.foodplanner.presentation.search.presenter.SearchPresenter;
import com.example.foodplanner.presentation.search.presenter.SearchPresenterImp;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ViewSearch, OnMealClickListener, OnFavoriteClickListener {

    private FragmentSearchBinding binding;
    private SearchPresenter presenter;
    private MealsAdapter adapter;
    private String currentSearchQuery = ""; // Preserve search query

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRecycler();

        presenter = new SearchPresenterImp(this, getContext());
        presenter.loadAllMeals(); // Load meals by default

        setupSearchListener();
        setupFilterButton();
        setupSearchFocusListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restore search results when returning from back stack
        if (presenter != null) {
            if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
                presenter.searchMeals(currentSearchQuery);
            } else {
                presenter.loadAllMeals();
            }
        }
    }


    private void setupSearchFocusListener() {
        binding.etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && currentSearchQuery.isEmpty()) {
                // Search bar focused and empty - show initial search state
                showInitialSearchState();
            } else if (!hasFocus && currentSearchQuery.isEmpty()) {
                // Search bar unfocused and empty - reload meals
                presenter.loadAllMeals();
            }
        });
    }

    private void setupRecycler() {
        adapter = new MealsAdapter(this, this);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearch.setAdapter(adapter);
    }

    private void setupSearchListener() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString();
                presenter.searchMeals(currentSearchQuery);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void setupFilterButton() {
        // Open filter bottom sheet when clicking the filter icon
        binding.ivFilterIcon.setOnClickListener(v -> {
            FilterBottomSheetDialog filterDialog = new FilterBottomSheetDialog();
            filterDialog.show(getChildFragmentManager(), "FilterBottomSheet");
        });
    }

    @Override
    public void showMeals(List<Meal> meals) {
        binding.initialSearchState.setVisibility(View.GONE);
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvSearch.setVisibility(View.VISIBLE);
        adapter.submitList(meals);
    }

    @Override
    public void showEmptyState() {
        binding.initialSearchState.setVisibility(View.GONE);
        binding.rvSearch.setVisibility(View.GONE);
        binding.tvEmpty.setVisibility(View.VISIBLE);
        adapter.submitList(new ArrayList<>());
    }

    private void showInitialSearchState() {
        if (binding != null) {
            binding.initialSearchState.setVisibility(View.VISIBLE);
            binding.rvSearch.setVisibility(View.GONE);
            binding.tvEmpty.setVisibility(View.GONE);
            binding.loadingCard.setVisibility(View.GONE);
            adapter.submitList(new ArrayList<>());
        }
    }

    @Override
    public void onMealClick(Meal meal) {
        SearchFragmentDirections.ActionSearchFragmentToMealDetailsFragment action =
                SearchFragmentDirections
                        .actionSearchFragmentToMealDetailsFragment(meal.getIdMeal());

        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void showLoading() {
        if (binding != null && binding.loadingCard != null) {
            binding.initialSearchState.setVisibility(View.GONE);
            binding.loadingCard.setVisibility(View.VISIBLE);
            binding.rvSearch.setVisibility(View.GONE);
            binding.tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null && binding.loadingCard != null) {
            binding.loadingCard.setVisibility(View.GONE);
            binding.rvSearch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String message) {
        if (binding != null) {
            android.widget.Toast.makeText(requireContext(),
                    "Error: " + message,
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        binding = null;
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        if (meal.isFavorite()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove From Favorites")
                    .setMessage("You are removing this meal from Favorites, Are you sure?")
                    .setPositiveButton("Remove", (dialog, which) -> presenter.onFavoriteClicked(meal))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            presenter.onFavoriteClicked(meal);
        }
    }
}

