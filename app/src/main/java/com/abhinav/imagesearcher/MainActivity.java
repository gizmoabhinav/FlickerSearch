package com.abhinav.imagesearcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.managers.SearchManager;
import com.abhinav.imagesearcher.view.RecyclerViewAdapter;
import com.abhinav.imagesearcher.view.RecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Photo> photoList = new ArrayList<>();

    private RecyclerViewOnScrollListener scrollListener;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private EditText mSearchTerm;
    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchTerm = findViewById(R.id.editBox);
        mSearchButton = findViewById(R.id.searchButton);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchManager.getInstance().clearRequestQueue();
                initScrollView(mRecyclerView, mAdapter, photoList, scrollListener, mSearchTerm.getText().toString());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchTerm.getWindowToken(), 0);
            }
        });
        // Configure the RecyclerView
    }

    private void initScrollView(RecyclerView recyclerView, RecyclerView.Adapter adapter,
                                final List<Photo> photoList, RecyclerViewOnScrollListener scrollListener,
                                final String query) {
        adapter = new RecyclerViewAdapter(photoList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        scrollListener = new RecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextResults(query, page, view.getAdapter(), photoList);
            }
        };
        photoList.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        recyclerView.addOnScrollListener(scrollListener);
        loadNextResults(query, 1, adapter, photoList);
    }

    private void loadNextResults(String query, int page, final RecyclerView.Adapter adapter, final List<Photo> photoList) {
        SearchManager.getInstance().getResult(query, page, this, new SearchManager.ISearchResultListener() {
            @Override
            public void onResultReceived(List<Photo> photos) {
                photoList.addAll(photos);
                adapter.notifyItemRangeChanged(photoList.size() - photos.size(), photos.size());
                queueImageDownload(photos, photoList.size() - photos.size(), adapter, photoList);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void queueImageDownload(List<Photo> photos, int startIndex, final RecyclerView.Adapter adapter, final List<Photo> photoList) {
        SearchManager.getInstance().getBitmaps(photos, startIndex, this, new SearchManager.IimageDownloadResultListener() {
            @Override
            public void onResultReceived(Bitmap bitmap, int index) {
                photoList.get(index).setBitmap(bitmap);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
