package com.alli.shoppinglist.models;

import android.database.Cursor;

import com.alli.shoppinglist.data.DatabaseContract;

import static com.alli.shoppinglist.data.DatabaseContract.getColumnInt;
import static com.alli.shoppinglist.data.DatabaseContract.getColumnLong;
import static com.alli.shoppinglist.data.DatabaseContract.getColumnString;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class ShoppingItem {
    long id;
    String item;
    boolean isFulfilled;
    long dateAdded;

    public ShoppingItem(Cursor cursor) {
        this.id = getColumnLong(cursor, DatabaseContract.TableColumns._ID);
        this.item = getColumnString(cursor, DatabaseContract.TableColumns.ITEM);
        this.isFulfilled = getColumnInt(cursor, DatabaseContract.TableColumns.IS_FULFILLED) == 1;
        this.dateAdded = getColumnLong(cursor, DatabaseContract.TableColumns.DATE_ADDED);
    }

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

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
