package com.abhinav.imagesearcher.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhinav.imagesearcher.R;
import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.managers.SearchManager;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Extension of RecyclerView adapter for handling images in the view
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    //The list of photos in the RecyclerView
    private List<Photo> mPhotoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        private NetworkImageView image;

        private MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleView);
            image = view.findViewById(R.id.imageView);
        }
    }

    public RecyclerViewAdapter(List<Photo> photoList) {
        this.mPhotoList = photoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Photo photo = mPhotoList.get(position);

        // set the title from the photo object
        holder.title.setText(photo.getTitle());

        // load the image in the view from the URL after checking cache
        SearchManager.getInstance().setBitmap(photo.getUrl(), holder.image);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }
}
