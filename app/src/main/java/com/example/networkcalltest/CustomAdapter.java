package com.example.networkcalltest;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter
        extends RecyclerView.Adapter<CustomViewHolder> {

    public void setDataSet(BookPojo dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public void setListener(CustomListener listener){
        this.listener = listener;
    }

    private BookPojo dataSet;
    private CustomListener listener;


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout,
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bindViewHolder(dataSet.items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return dataSet != null ? dataSet.items.size() : 0;
    }
}
