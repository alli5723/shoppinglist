package com.alli.shoppinglist.models;

import android.database.Cursor;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class ShoppingItem {
    long id;
    String item;
    boolean isFulfilled;
    long timeAdded;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public boolean isFulfilled() {
        return isFulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        isFulfilled = fulfilled;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public ShoppingItem(Cursor cursor) {
        //TODO: Create a constructor that accepts a db cursor and returns a Shopping item
    }
}
