package com.example.networkcalltest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    ImageView iv_book_cover;
    TextView tv_book_title, tv_book_subtitle;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_book_subtitle = itemView.findViewById(R.id.tv_item_subtitle);
        tv_book_title = itemView.findViewById(R.id.tv_item_title);
        iv_book_cover = itemView.findViewById(R.id.iv_item);
    }

    public void bindViewHolder(final BookItem item,
                               final CustomListener listener) {
        tv_book_title.setText(item.volumeInfo.title);
        tv_book_subtitle.setText(item.volumeInfo.subtitle);
        Picasso.get().load(item.volumeInfo.imageLinks.smallThumbnail)
                .placeholder(R.drawable.ic_error_outline_black_24dp)
                .into(
                iv_book_cover);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemClicked(item);
            }
        });
    }
}








