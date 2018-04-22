package com.alli.shoppinglist.models;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class ShoppingItem {
    String item;
    boolean isFulfilled;
    long timeAdded;

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
}
