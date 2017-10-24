package com.example.wojtek.timeorganizer;

/**
 * Created by Wojtek on 22.10.2017.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import com.codexpedia.list.viewholder.R;
import java.util.ArrayList;


public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    private OnItemClicked listener;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList, OnItemClicked  listener) {
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.listener = listener;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getName());

        TextView item2 = holder.item2;

        item2.setText(itemList.get(listPosition).getDate());

        if( listPosition!=0 && itemList.get(listPosition).getDate().equals( itemList.get(listPosition-1).getDate() ) ) {
            item2.setVisibility(View.GONE);
        }


        // Add click listener for root view
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, listPosition);
            }
        });
    }
    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public TextView item2;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.taskTextView);
            item2 = (TextView) itemView.findViewById(R.id.dateTextView);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());

            view.setSelected(!view.isSelected());

        }
    }

    public interface OnItemClicked {
        void onItemClick(View view, int position);
    }
}