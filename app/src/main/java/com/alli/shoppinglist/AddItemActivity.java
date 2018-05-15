package com.alli.shoppinglist;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alli.shoppinglist.data.DatabaseContract;
import com.alli.shoppinglist.data.DatabaseContract.TableColumns;
import com.alli.shoppinglist.utils.Utils;

import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    Button addItem;
    EditText item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        item = (EditText) findViewById(R.id.item_name);
        addItem = (Button) findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_remind:
                createPhoneReminder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveItem() {
        //Insert a new item
        ContentValues values = new ContentValues(4);
        values.put(TableColumns.ITEM, item.getText().toString());
        values.put(TableColumns.IS_FULFILLED, 0);
        values.put(TableColumns.DATE_ADDED, Utils.getCurrentDate());

        Uri uri = DatabaseContract.CONTENT_URI;
        getContentResolver().insert(uri, values);
        finish();
    }

    private void createPhoneReminder() {
        if ((item.getText().toString()).equalsIgnoreCase("")) {
            Snackbar mySnackbar = Snackbar.make(item,
                    R.string.add_error, Snackbar.LENGTH_SHORT);
            mySnackbar.show();
            return;
        }
        insertToCalendar();
    }

    public void insertToCalendar() {
        Calendar beginTime = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime.getTimeInMillis() + 60 * 60 * 1000)
                .putExtra(CalendarContract.Events.TITLE, item.getText().toString())
                .putExtra(CalendarContract.Events.DESCRIPTION, "Reminder for " + item.getText().toString());
        startActivity(intent);
    }
}
