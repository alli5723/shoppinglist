package com.alli.shoppinglist.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alli.shoppinglist.R;
import com.alli.shoppinglist.models.ShoppingItem;
import com.alli.shoppinglist.views.ShoppingItemTitleView;

import java.util.Calendar;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class ShoppingItemListAdapter extends RecyclerView.Adapter<ShoppingItemListAdapter.ItemHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemToggled(boolean active, int position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ShoppingItemTitleView nameView;
        public TextView dateView;
        public CheckBox checkBox;

        public ItemHolder(View itemView) {
            super(itemView);

            nameView = (ShoppingItemTitleView) itemView.findViewById(R.id.text_description);
            dateView = (TextView) itemView.findViewById(R.id.text_date);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == checkBox) {
                fulfilledToggled(this);
            } else {
                postItemClick(this);
            }
        }
    }

    private Cursor mCursor;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public ShoppingItemListAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void fulfilledToggled(ItemHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemToggled(holder.checkBox.isChecked(), holder.getAdapterPosition());
        }
    }

    private void postItemClick(ItemHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.shopping_item_view, parent, false);

        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        ShoppingItem shoppingItem = getItem(position);

        holder.nameView.setTag(shoppingItem.getId());
        holder.nameView.setText(shoppingItem.getItem());
        holder.nameView.setState(ShoppingItemTitleView.NORMAL);

        if (shoppingItem.getDateAdded() != Long.MAX_VALUE) {
            CharSequence formatted = DateUtils.getRelativeTimeSpanString(mContext,
                    shoppingItem.getDateAdded());
            holder.dateView.setText(formatted);
            holder.dateView.setVisibility(View.VISIBLE);
        }

        if (shoppingItem.isFulfilled()) {
            holder.nameView.setState(ShoppingItemTitleView.DONE);
        }
        holder.checkBox.setChecked(shoppingItem.isFulfilled());
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    /**
     * Retrieve a {@link ShoppingItem} for the data at the given position.
     *
     * @param position Adapter item position.
     * @return A new {@link ShoppingItem} filled with the position's attributes.
     */
    public ShoppingItem getItem(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }

        return new ShoppingItem(mCursor);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }
}