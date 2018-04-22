package com.alli.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alli.shoppinglist.data.DatabaseContract;
import com.alli.shoppinglist.data.ShoppingItemListAdapter;

import java.util.ArrayList;
// TODO: Handle lifecycle persistence, and remove fixed potrait from manifest
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ShoppingItemListAdapter.OnItemClickListener {

    public String TAG = MainActivity.class.getSimpleName();
    ArrayList selectedItems = new ArrayList();

    private ShoppingItemListAdapter adapter;
    private Cursor cursor;
    String sortOrder = DatabaseContract.DEFAULT_SORT;
    Context context;

    public static final int ID_LIST_LOADER = 1;
    public static final String[] PROJECTION = {
            DatabaseContract.TableColumns._ID,
            DatabaseContract.TableColumns.ITEM,
            DatabaseContract.TableColumns.DATE_ADDED,
            DatabaseContract.TableColumns.IS_FULFILLED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        adapter = new ShoppingItemListAdapter(null);
        adapter.setOnItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(ID_LIST_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (selectedItems != null && selectedItems.size() > 0){
            getMenuInflater().inflate(R.menu.item_menu, menu);
        }else{
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            // TODO: Add a new item to list here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case ID_LIST_LOADER :
                Uri queryUri = DatabaseContract.CONTENT_URI;
                return new CursorLoader(this,
                        queryUri,
                        PROJECTION,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader not implemented for "+ id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0 && data != null) {
            adapter.swapCursor(data);
            cursor = data;
        }else
            Log.e(TAG, "onLoadFinished: No data found in db" );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onItemToggled(boolean active, int position) {
        if (active){
            selectedItems.add(position);
        }else{
            selectedItems.remove(selectedItems.indexOf(position));
        }
        ActivityCompat.invalidateOptionsMenu(this);
    }
}
