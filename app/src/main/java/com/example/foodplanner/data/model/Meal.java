package com.example.foodplanner.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Meal implements Serializable {

    @SerializedName("idMeal")
    private String idMeal;

    @SerializedName("strMeal")
    private String strMeal;

    @SerializedName("strMealAlternate")
    private String strMealAlternate;

    @SerializedName("strCategory")
    private String strCategory;

    @SerializedName("strArea")
    private String strArea;

    @SerializedName("strInstructions")
    private String strInstructions;

    @SerializedName("strMealThumb")
    private String strMealThumb;

    @SerializedName("strTags")
    private String strTags;

    @SerializedName("strYoutube")
    private String strYoutube;

    @SerializedName("strIngredient1")
    private String strIngredient1;

    @SerializedName("strIngredient2")
    private String strIngredient2;

    @SerializedName("strIngredient3")
    private String strIngredient3;

    @SerializedName("strIngredient4")
    private String strIngredient4;

    @SerializedName("strIngredient5")
    private String strIngredient5;

    @SerializedName("strIngredient6")
    private String strIngredient6;

    @SerializedName("strMeasure1")
    private String strMeasure1;

    @SerializedName("strMeasure2")
    private String strMeasure2;

    @SerializedName("strMeasure3")
    private String strMeasure3;

    @SerializedName("strMeasure4")
    private String strMeasure4;

    @SerializedName("strMeasure5")
    private String strMeasure5;

    @SerializedName("strMeasure6")
    private String strMeasure6;

    // Getters & Setters

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public String getStrYoutube() {
        return strYoutube;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "idMeal='" + idMeal + '\'' +
                ", strMeal='" + strMeal + '\'' +
                ", strMealAlternate='" + strMealAlternate + '\'' +
                ", strCategory='" + strCategory + '\'' +
                ", strArea='" + strArea + '\'' +
                ", strInstructions='" + strInstructions + '\'' +
                ", strMealThumb='" + strMealThumb + '\'' +
                ", strTags='" + strTags + '\'' +
                ", strYoutube='" + strYoutube + '\'' +
                ", strIngredient1='" + strIngredient1 + '\'' +
                ", strIngredient2='" + strIngredient2 + '\'' +
                ", strIngredient3='" + strIngredient3 + '\'' +
                ", strIngredient4='" + strIngredient4 + '\'' +
                ", strIngredient5='" + strIngredient5 + '\'' +
                ", strIngredient6='" + strIngredient6 + '\'' +
                ", strMeasure1='" + strMeasure1 + '\'' +
                ", strMeasure2='" + strMeasure2 + '\'' +
                ", strMeasure3='" + strMeasure3 + '\'' +
                ", strMeasure4='" + strMeasure4 + '\'' +
                ", strMeasure5='" + strMeasure5 + '\'' +
                ", strMeasure6='" + strMeasure6 + '\'' +
                '}';
    }
}