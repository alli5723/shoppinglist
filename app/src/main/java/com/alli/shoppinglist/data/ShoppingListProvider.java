package com.alli.shoppinglist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class ShoppingListProvider extends ContentProvider {
    private static final String TAG = ShoppingListProvider.class.getSimpleName();

    private static final int ITEMS = 100;
    private static final int ITEMS_WITH_ID = 101;

    private DbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_SHOPPING_ITEM,
                ITEMS);

        sUriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_SHOPPING_ITEM + "/#",
                ITEMS_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null; /* Not used */
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case ITEMS_WITH_ID : {
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};

                cursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.TABLE_SHOPPING_ITEM,
                        projection,
                        DatabaseContract.TableColumns._ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ITEMS : {
                cursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.TABLE_SHOPPING_ITEM,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Undefined uri");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (sUriMatcher.match(uri)){
            case ITEMS :
                long id = db.insert(DatabaseContract.TABLE_SHOPPING_ITEM, null, values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, id);
                }else {
                    throw new android.database.SQLException("Failed to insert shopping item");
                }
                break;
            default:
                throw new UnsupportedOperationException("Undefined Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int update = 0;
        switch (sUriMatcher.match(uri)){
            case ITEMS_WITH_ID :
                String id = uri.getLastPathSegment();
                String[] whereArguments = new String[]{id};
                update = db.update(
                        DatabaseContract.TABLE_SHOPPING_ITEM,
                        values,
                        DatabaseContract.TableColumns._ID + " = ?",
                        whereArguments
                );

                break;
            default:
                throw new UnsupportedOperationException("Undefined Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return update;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                //Rows aren't counted with null selection
                selection = (selection == null) ? "1" : selection;
                break;
            case ITEMS_WITH_ID:
                long id = ContentUris.parseId(uri);
                selection = String.format("%s = ?", DatabaseContract.TableColumns._ID);
                selectionArgs = new String[]{String.valueOf(id)};
                break;
            default:
                throw new IllegalArgumentException("Illegal delete URI");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.delete(DatabaseContract.TABLE_SHOPPING_ITEM, selection, selectionArgs);

        if (count > 0) {
            //Notify observers of the change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}