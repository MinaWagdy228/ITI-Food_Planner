package com.example.foodplanner.presentation.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.BottomSheetSearchBinding;
import com.example.foodplanner.presentation.common.OnMealClickListener;
import com.example.foodplanner.presentation.filteredmeals.MealsAdapter;
import com.example.foodplanner.presentation.mealdetails.OnFavoriteClickListener;
import com.example.foodplanner.presentation.search.SearchPresenter;
import com.example.foodplanner.presentation.search.SearchPresenterImp;
import com.example.foodplanner.presentation.search.ViewSearch;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class SearchBottomSheetDialog extends BottomSheetDialogFragment implements ViewSearch, OnMealClickListener, OnFavoriteClickListener {

    private BottomSheetSearchBinding binding;
    private SearchPresenter presenter;
    private MealsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupPresenter();
        setupSearchListener();

        // Load all meals initially
        presenter.loadAllMeals();

        // Auto-focus the search input
        binding.etSearch.requestFocus();
    }

    private void setupRecyclerView() {
        adapter = new MealsAdapter(this, this);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(adapter);
    }

    private void setupPresenter() {
        presenter = new SearchPresenterImp(this, requireContext());
    }

    private void setupSearchListener() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    presenter.loadAllMeals();
                } else {
                    presenter.searchMeals(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                presenter.searchMeals(query);
            }
            return true;
        });
    }

    @Override
    public void showMeals(List<Meal> meals) {
        adapter.setMeals(meals);
        binding.rvSearchResults.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyState() {
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        hideLoading();
        showEmptyState();
    }

    @Override
    public void onMealClick(Meal meal) {
        dismiss();
        // Navigate to meal details - need to navigate from MainActivity's NavHostFragment
        if (getActivity() != null) {
            androidx.navigation.NavController navController =
                androidx.navigation.Navigation.findNavController(getActivity(), com.example.foodplanner.R.id.nav_host_fragment);
            HomeFragmentDirections.ActionHomeFragmentToMealDetailsFragment action =
                    HomeFragmentDirections.actionHomeFragmentToMealDetailsFragment(meal.getIdMeal());
            navController.navigate(action);
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
        presenter.onFavoriteClicked(meal);
    }
}




