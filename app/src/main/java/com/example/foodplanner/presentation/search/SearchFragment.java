package com.example.foodplanner.presentation.search;

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
import com.example.foodplanner.presentation.filteredmeals.MealsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ViewSearch, OnMealClickListener {

    private FragmentSearchBinding binding;
    private SearchPresenter presenter;
    private MealsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRecycler();

        presenter = new SearchPresenterImp(this);
        presenter.loadAllMeals(); // 🔥 default load

        setupSearchListener();
    }

    private void setupRecycler() {
        adapter = new MealsAdapter(this);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearch.setAdapter(adapter);
    }

    private void setupSearchListener() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                presenter.searchMeals(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    public void showMeals(List<Meal> meals) {
        binding.tvEmpty.setVisibility(View.GONE);
        adapter.submitList(meals);
    }

    @Override
    public void showEmptyState() {
        binding.tvEmpty.setVisibility(View.VISIBLE);
        adapter.submitList(new ArrayList<>());
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
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String message) {
    }
}