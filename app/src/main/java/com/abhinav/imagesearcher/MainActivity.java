package com.abhinav.imagesearcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.managers.SearchManager;
import com.abhinav.imagesearcher.utils.NetworkUtils;
import com.abhinav.imagesearcher.view.RecyclerViewAdapter;
import com.abhinav.imagesearcher.view.RecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private List<Photo> photoList = new ArrayList<>();

    private RecyclerViewOnScrollListener scrollListener;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mNoNetworkView;

    private EditText mSearchTerm;
    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchTerm = findViewById(R.id.editBox);
        mSearchButton = findViewById(R.id.searchButton);
        mRecyclerView = findViewById(R.id.recycler_view);
        mNoNetworkView = findViewById(R.id.no_network_view);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchManager.getInstance().clearRequestQueue();
                initScrollView(mRecyclerView, mAdapter, photoList, scrollListener, mSearchTerm.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchTerm.getWindowToken(), 0);
            }
        });
    }

    private void initScrollView(RecyclerView recyclerView, RecyclerView.Adapter adapter,
                                final List<Photo> photoList, RecyclerViewOnScrollListener scrollListener,
                                final String query) {
        Log.i(LOG_TAG, "requested search for query " + query);
        if (!NetworkUtils.isNetworkConnected(this)) {
            Log.i(LOG_TAG, "no network connection, aborting search");
            mRecyclerView.setVisibility(View.GONE);
            mNoNetworkView.setVisibility(View.VISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoNetworkView.setVisibility(View.GONE);
        }
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

    private void loadNextResults(final String query, final int page, final RecyclerView.Adapter adapter, final List<Photo> photoList) {
        Log.d(LOG_TAG, "Fetching search result page " + page + " for query " + query);
        SearchManager.getInstance().getResult(query, page, this, new SearchManager.ISearchResultListener() {
            @Override
            public void onResultReceived(List<Photo> photos) {
                photoList.addAll(photos);
                adapter.notifyItemRangeChanged(photoList.size() - photos.size(), photos.size());
                queueImageDownload(photos, photoList.size() - photos.size(), adapter, photoList);
            }

            @Override
            public void onError() {
                Log.e(LOG_TAG, "Couldn't load page " + page + " for query " + query);
            }
        });
    }

    private void queueImageDownload(List<Photo> photos, final int startIndex, final RecyclerView.Adapter adapter, final List<Photo> photoList) {
        SearchManager.getInstance().getBitmaps(photos, startIndex, this, new SearchManager.IimageDownloadResultListener() {
            @Override
            public void onResultReceived(Bitmap bitmap, int index) {
                photoList.get(index).setBitmap(bitmap);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onError(int index) {
                Log.e(LOG_TAG, "Couldn't load image at index " + index);
                photoList.get(index).setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.error));
                adapter.notifyItemChanged(index);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
