package com.example.foodplanner.presentation.mealdetails;

public class DetailItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_INGREDIENT = 1;
    public static final int TYPE_INSTRUCTIONS = 2;
    public static final int TYPE_YOUTUBE = 3;

    private final int type;
    private final Object data;

    public DetailItem(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() { return type; }
    public Object getData() { return data; }
}