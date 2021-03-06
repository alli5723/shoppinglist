package com.alli.shoppinglist.views;

/**
 * Created by omo_lanke on 21/04/2018.
 */
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.alli.shoppinglist.R;

public class ShoppingItemTitleView extends android.support.v7.widget.AppCompatTextView {
    public static final int NORMAL = 0;
    public static final int DONE = 1;
    public static final int OVERDUE = 2;
    private int mState;

    public ShoppingItemTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShoppingItemTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShoppingItemTitleView(Context context) {
        super(context);
    }

    /**
     * Return the current display state of this view.
     *
     * @return One of {@link #NORMAL}, {@link #DONE}, or {@link #OVERDUE}.
     */
    public int getState() {
        return mState;
    }

    /**
     * Update the text display state of this view.
     * Normal status shows black text. Overdue displays in red.
     * Completed draws a strikethrough line on the text.
     *
     * @param state New state. One of {@link #NORMAL}, {@link #DONE}, or {@link #OVERDUE}.
     */
    public void setState(int state) {
        switch (state) {
            case DONE:
                setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                break;
            case NORMAL:
                setPaintFlags(0);
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                break;
            case OVERDUE:
                setPaintFlags(0);
                setTextColor(ContextCompat.getColor(getContext(), R.color.danger));
                break;
            default:
                return;
        }

        mState = state;
    }
}