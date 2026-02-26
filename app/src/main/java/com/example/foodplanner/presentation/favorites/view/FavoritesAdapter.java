package com.example.foodplanner.presentation.favorites.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.databinding.ItemFavoriteMealBinding;
import com.example.foodplanner.data.entity.FavoriteMealEntity;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    public interface OnFavoriteMealClick {
        void onMealClick(FavoriteMealEntity meal);
    }

    public interface OnFavoriteRemove {
        void onRemoveFavorite(FavoriteMealEntity meal);
    }

    private List<FavoriteMealEntity> list;
    private final OnFavoriteMealClick clickListener;
    private final OnFavoriteRemove removeListener;

    public FavoritesAdapter(List<FavoriteMealEntity> list, OnFavoriteMealClick clickListener, OnFavoriteRemove removeListener) {
        this.list = list;
        this.clickListener = clickListener;
        this.removeListener = removeListener;
    }

    public void setList(List<FavoriteMealEntity> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteMealBinding binding = ItemFavoriteMealBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteMealEntity meal = list.get(position);

        holder.binding.tvMealName.setText(meal.getStrMeal());

        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .placeholder(android.R.color.transparent)
                .error(android.R.color.transparent)
                .into(holder.binding.imgMeal);

        // Favorite icon is always filled red (these are favorites)
        // Clicking removes from favorites
        holder.binding.imgFavorite.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemoveFavorite(meal);
            }
        });

        // Meal item click
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFavoriteMealBinding binding;

        ViewHolder(ItemFavoriteMealBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}