package com.example.foodplanner.data;

import java.io.Serializable;

public class Meal implements Serializable {
    public int id;
    public String name;

    public Meal(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
