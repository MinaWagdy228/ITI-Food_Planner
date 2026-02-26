package com.example.foodplanner.presentation.filteredmeals;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.ItemMealBinding;
import com.example.foodplanner.presentation.common.OnMealClickListener;
import com.example.foodplanner.presentation.mealdetails.OnFavoriteClickListener;

import java.util.ArrayList;
import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private final OnMealClickListener onMealClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public MealsAdapter(OnMealClickListener onMealClickListener) {
        this(onMealClickListener, null);
    }

    public MealsAdapter(OnMealClickListener onMealClickListener, OnFavoriteClickListener onFavoriteClickListener) {
        this.onMealClickListener = onMealClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealBinding binding = ItemMealBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        holder.binding.tvMealName.setText(meal.getStrMeal());

        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .placeholder(android.R.color.transparent)
                .error(android.R.color.transparent)
                .into(holder.binding.imgMeal);

        // Set favorite icon state
        if (meal.isFavorite()) {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite_filled);
            holder.binding.imgFavorite.setColorFilter(
                    holder.itemView.getContext().getColor(android.R.color.holo_red_light)
            );
        } else {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite_border);
            holder.binding.imgFavorite.setColorFilter(
                    holder.itemView.getContext().getColor(android.R.color.white)
            );
        }

        // Favorite icon click
        holder.binding.imgFavorite.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteClicked(meal);
            }
        });

        // Meal item click
        holder.itemView.setOnClickListener(v -> {
            if (onMealClickListener != null) {
                onMealClickListener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void submitList(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        ItemMealBinding binding;

        public MealViewHolder(ItemMealBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}