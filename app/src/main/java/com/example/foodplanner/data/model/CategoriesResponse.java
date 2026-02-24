package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoriesResponse {

    @SerializedName("categories")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }
}