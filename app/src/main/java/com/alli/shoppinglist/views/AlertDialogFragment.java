package com.alli.shoppinglist.views;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alli.shoppinglist.R;

/**
 * Created by omo_lanke on 23/04/2018.
 */

public class AlertDialogFragment extends DialogFragment {
    static AlertDialogFragment newInstance() {
        return new AlertDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_details_view, container, false);
        View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText("This is an instance of MyDialogFragment");
        return v;
    }
}
