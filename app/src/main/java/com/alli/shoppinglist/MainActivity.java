package com.alli.shoppinglist;

import android.content.ContentValues;
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
import com.alli.shoppinglist.models.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

// TODO: Handle lifecycle persistence, and remove fixed potrait from manifest
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ShoppingItemListAdapter.OnItemClickListener {

    public String TAG = MainActivity.class.getSimpleName();
    List<Integer> selectedItems = new ArrayList();

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
        switch (id){
            case R.id.action_add :
                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_done :
                updateDatabase();
                return true;
            case R.id.action_delete :
                deleteSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
        //TODO: Add a function to show details and remove from fulfilled
    }

    @Override
    public void onItemToggled(boolean active, int position) {
        if (active){
            selectedItems.add(position);
        }else{
            if (wasFulfilled(position)){
                showAlertMessage(position);
            }else{
                selectedItems.remove(selectedItems.indexOf(position));
            }
        }
        ActivityCompat.invalidateOptionsMenu(this);
    }

    private void showAlertMessage(int position){
        //TODO: Inform user that this item has been previously fulfilled
    }

    private boolean wasFulfilled(int position){
        cursor.moveToPosition(position);
        ShoppingItem shoppingItem = new ShoppingItem(cursor);
        return shoppingItem.isFulfilled();
    }

    public void updateDatabase(){

        for(int position : selectedItems){
            cursor.moveToPosition(position);
            ShoppingItem shoppingItem = new ShoppingItem(cursor);

            Uri uri = DatabaseContract.CONTENT_URI;
            uri = uri.buildUpon().appendPath((shoppingItem.getId())+"").build();
            ContentValues values = new ContentValues(1);
            int complete = 1;
            values.put(DatabaseContract.TableColumns.IS_FULFILLED, complete);
            getContentResolver().update(uri, values, null, null);
        }
    }

    public void deleteSelected(){

        for(int position : selectedItems){
            cursor.moveToPosition(position);
            ShoppingItem shoppingItem = new ShoppingItem(cursor);

            Uri uri = DatabaseContract.CONTENT_URI;
            uri = uri.buildUpon().appendPath((shoppingItem.getId())+"").build();
            ContentValues values = new ContentValues(1);
            getContentResolver().delete(uri, null, null);
        }
    }
}
