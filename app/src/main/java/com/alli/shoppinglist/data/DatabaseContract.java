package com.alli.shoppinglist.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class DatabaseContract {
    //Database schema information
    public static final String TABLE_SHOPPING_ITEM = "shopping";

    public static final class TableColumns implements BaseColumns {
        //Shopping list item
        public static final String ITEM = "item";
        //Completed marker
        public static final String IS_FULFILLED = "is_fulfilled";
        //Date added
        public static final String DATE_ADDED = "date_added";
    }

    //Unique authority string for the content provider
    public static final String CONTENT_AUTHORITY = "com.alli.shoppinglist.shoppinglist";

    /* Sort order constants */
    //Priority first, Completed last, the rest by date
    public static final String DEFAULT_SORT = String.format("%s DESC, %s ASC",
            TableColumns.DATE_ADDED, TableColumns.IS_FULFILLED);

    //Completed last, then by date, followed by priority
    public static final String DATE_SORT = String.format("%s ASC, %s ASC",
            TableColumns.DATE_ADDED, TableColumns.IS_FULFILLED);

    //Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_SHOPPING_ITEM)
            .build();

    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt( cursor.getColumnIndex(columnName) );
    }

    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong( cursor.getColumnIndex(columnName) );
    }
}
