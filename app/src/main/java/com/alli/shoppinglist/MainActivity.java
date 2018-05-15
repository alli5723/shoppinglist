package com.alli.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alli.shoppinglist.data.DatabaseContract;
import com.alli.shoppinglist.data.ShoppingItemListAdapter;
import com.alli.shoppinglist.models.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ShoppingItemListAdapter.OnItemClickListener {

    public static final int ID_LIST_LOADER = 1;
    public static final String[] PROJECTION = {
            DatabaseContract.TableColumns._ID,
            DatabaseContract.TableColumns.ITEM,
            DatabaseContract.TableColumns.DATE_ADDED,
            DatabaseContract.TableColumns.IS_FULFILLED
    };
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private static final String CURRENT_VIEW = "current_view";
    public String TAG = MainActivity.class.getSimpleName();
    List<Integer> selectedItems = new ArrayList();
    boolean listFulfilled = false;
    String sortOrder = DatabaseContract.DEFAULT_SORT;
    Context context;
    private ShoppingItemListAdapter adapter;
    private Cursor cursor;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        adapter = new ShoppingItemListAdapter(null);
        adapter.setOnItemClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(ID_LIST_LOADER, null, this);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            listFulfilled = savedInstanceState.getBoolean(CURRENT_VIEW);
        }
        setActionBarSubtitle();
    }

    private void setActionBarSubtitle() {
        if (listFulfilled == true) {
            getSupportActionBar().setSubtitle("Already in basket");
        } else {
            getSupportActionBar().setSubtitle("Shopping list");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (selectedItems != null && selectedItems.size() > 0) {
            getMenuInflater().inflate(R.menu.item_menu, menu);
        } else if (listFulfilled == true) {
            getMenuInflater().inflate(R.menu.fulfilled_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
                break;
            case R.id.action_done:
                updateDatabase();
                break;
            case R.id.action_delete:
                deleteSelected();
                break;
            case R.id.action_basket:
                listFulfilled = true;
                getSupportLoaderManager().restartLoader(ID_LIST_LOADER, null, this);
                setActionBarSubtitle();
                break;
            case R.id.action_view_all:
                listFulfilled = false;
                getSupportLoaderManager().restartLoader(ID_LIST_LOADER, null, this);
                setActionBarSubtitle();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        ActivityCompat.invalidateOptionsMenu(this);
        return true;
    }

    private String whereFulfilledIs(boolean f) {
        if (f) {
            return DatabaseContract.TableColumns.IS_FULFILLED + " = 1";
        } else {
            return DatabaseContract.TableColumns.IS_FULFILLED + " = 0";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putBoolean(CURRENT_VIEW, listFulfilled);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        adapter.swapCursor(null);
        switch (id) {
            case ID_LIST_LOADER:
                Uri queryUri = DatabaseContract.CONTENT_URI;
                return new CursorLoader(this,
                        queryUri,
                        PROJECTION,
                        whereFulfilledIs(listFulfilled),
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader not implemented for " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0 && data != null) {
            adapter.swapCursor(data);
            cursor = data;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (wasFulfilled(position)) {
            showAlertMessage(position);
        }
    }

    @Override
    public void onItemToggled(boolean active, int position) {
        if (active) {
            selectedItems.add(position);
        } else {
            selectedItems.remove(selectedItems.indexOf(position));
        }
        ActivityCompat.invalidateOptionsMenu(this);
    }

    private void showAlertMessage(int position) {
        cursor.moveToPosition(position);
        Uri uri_base = DatabaseContract.CONTENT_URI;
        final ShoppingItem shoppingItem = new ShoppingItem(cursor);
        final Uri uri = uri_base.buildUpon().appendPath((shoppingItem.getId()) + "").build();

        new AlertDialog.Builder(this)
                .setMessage("Shopping item has been fulfilled, would you like to delete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        getContentResolver().delete(uri, null, null);
                        Toast.makeText(MainActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", null).show();
    }

    private boolean wasFulfilled(int position) {
        cursor.moveToPosition(position);
        ShoppingItem shoppingItem = new ShoppingItem(cursor);
        return shoppingItem.isFulfilled();
    }

    public void updateDatabase() {
        for (int position : selectedItems) {
            cursor.moveToPosition(position);
            ShoppingItem shoppingItem = new ShoppingItem(cursor);

            Uri uri = DatabaseContract.CONTENT_URI;
            uri = uri.buildUpon().appendPath((shoppingItem.getId()) + "").build();
            ContentValues values = new ContentValues(1);
            int complete = 1;
            values.put(DatabaseContract.TableColumns.IS_FULFILLED, complete);
            getContentResolver().update(uri, values, null, null);
        }
        selectedItems.clear();
    }

    public void deleteSelected() {
        for (int position : selectedItems) {
            cursor.moveToPosition(position);
            ShoppingItem shoppingItem = new ShoppingItem(cursor);

            Uri uri = DatabaseContract.CONTENT_URI;
            uri = uri.buildUpon().appendPath((shoppingItem.getId()) + "").build();
            ContentValues values = new ContentValues(1);
            getContentResolver().delete(uri, null, null);
        }
        selectedItems.clear();
    }
}
