package com.abhinav.imagesearcher.view;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhinav.imagesearcher.R;
import com.abhinav.imagesearcher.datamodels.Photo;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<Photo> photoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleView);
            image = view.findViewById(R.id.imageView);
        }
    }

    public RecyclerViewAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.title.setText(photo.getTitle());
        if (photo.getBitmap() == null) {
            holder.image.setImageResource(R.drawable.placeholder);
        } else {
            holder.image.setImageBitmap(photo.getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
