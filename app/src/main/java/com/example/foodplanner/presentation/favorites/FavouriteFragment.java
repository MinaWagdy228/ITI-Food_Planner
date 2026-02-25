package com.example.foodplanner.presentation.favorites;

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


import com.example.foodplanner.databinding.FragmentFavouriteBinding;
import com.example.foodplanner.db.FavoriteMealEntity;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment implements ViewFavourite {

    private FragmentFavouriteBinding binding;
    private FavouritePresenter presenter;
    private FavoritesAdapter adapter;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecycler();

        presenter = new FavouritePresenterImp(this, requireContext());
    }

    private void setupRecycler() {
        adapter = new FavoritesAdapter(new ArrayList<>(), mealEntity -> {
            FavouriteFragmentDirections.ActionFavouriteFragmentToMealDetailsFragment action =
                    FavouriteFragmentDirections
                            .actionFavouriteFragmentToMealDetailsFragment(mealEntity.getIdMeal());

            NavHostFragment.findNavController(this).navigate(action);
        });

        binding.rvFavorites.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.rvFavorites.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        presenter.loadFavorites();
    }

    @Override
    public void showFavorites(List<FavoriteMealEntity> meals) {
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvFavorites.setVisibility(View.VISIBLE);
        adapter.setList(meals);
    }


    @Override
    public void showEmptyState() {
        binding.rvFavorites.setVisibility(View.GONE);
        binding.tvEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy(); // Prevent memory leaks (RxJava best practice)
        }
        binding = null;
    }
}