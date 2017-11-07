package com.example.wojtek.timeorganizer;

/**
 * Created by Wojtek on 22.10.2017.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.codexpedia.list.viewholder.R;
import java.util.ArrayList;


public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    private OnItemClicked listener;
    private int lastSelectedPos = -1;
    private static TextView lastSelectedItem = null;

    RelativeLayout relativeLayoutBottom;

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

    public String getItemIdFromText(ItemArrayAdapter.ViewHolder viewHolder){
        return viewHolder.item3.getText().toString();
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getName());

        TextView item2 = holder.item2;
        item2.setText(itemList.get(listPosition).getDate());
        item2.setClickable(false);

        TextView item3 = holder.item3;
        item3.setText(itemList.get(listPosition).getId());

        if( listPosition!=0 && itemList.get(listPosition).getDate().equals( itemList.get(listPosition-1).getDate() ) ) {
            item2.setVisibility(View.GONE);
        }

        if(lastSelectedPos!=-1 && listPosition!=lastSelectedPos){
            lastSelectedItem = holder.item;
        }


        // Add click listener for root view
        item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listener.onItemClick(view, listPosition);

                if(listPosition!=lastSelectedPos && lastSelectedPos!=-1){
                    lastSelectedItem.setSelected(false);
                }

                //view.setSelected(!view.isSelected());
                if(!view.isSelected()){
                    view.setSelected(true);
                }
                else {
                    view.setSelected(false);
                }

                lastSelectedPos = listPosition;
                lastSelectedItem = holder.item;

            }
        });
    }
    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public TextView item2;
        public TextView item3;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.taskTextView);
            item2 = (TextView) itemView.findViewById(R.id.dateTextView);
            item3 = (TextView) itemView.findViewById(R.id.itemNumberTextView);
        }

        @Override
        public void onClick(View view) {
            //Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
            //view.setSelected(!view.isSelected());
        }
    }

    public interface OnItemClicked {
        void onItemClick(View view, int position);
    }
}