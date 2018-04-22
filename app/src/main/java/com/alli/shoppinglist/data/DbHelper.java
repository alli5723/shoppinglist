package com.alli.shoppinglist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alli.shoppinglist.R;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE = String.format("CREATE TABLE %s"
                    +" (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER, %s INTEGER)",
            DatabaseContract.TABLE_SHOPPING_ITEM,
            DatabaseContract.TableColumns._ID,
            DatabaseContract.TableColumns.ITEM,
            DatabaseContract.TableColumns.IS_FULFILLED,
            DatabaseContract.TableColumns.DATE_ADDED
    );

    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        onboardInstruction(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_SHOPPING_ITEM);
        onCreate(db);
    }

    private void onboardInstruction(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TableColumns.ITEM, mContext.getResources().getString(R.string.onboard));
        values.put(DatabaseContract.TableColumns.IS_FULFILLED, 0);
        values.put(DatabaseContract.TableColumns.DATE_ADDED, Long.MAX_VALUE);

        db.insertOrThrow(DatabaseContract.TABLE_SHOPPING_ITEM, null, values);
    }
}