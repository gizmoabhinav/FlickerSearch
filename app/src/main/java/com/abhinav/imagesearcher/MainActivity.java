package com.abhinav.imagesearcher;

import android.content.Context;
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
import android.widget.Toast;

import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.managers.SearchManager;
import com.abhinav.imagesearcher.utils.NetworkUtils;
import com.abhinav.imagesearcher.view.RecyclerViewAdapter;
import com.abhinav.imagesearcher.view.RecyclerViewOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    // List of photos from search
    private List<Photo> mPhotoList = new ArrayList<>();

    // boolean value indicating we have reached the last page of our search
    private boolean mMaxPhotosReached = false;


    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mNoNetworkView;
    private EditText mSearchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchTerm = findViewById(R.id.editBox);
        Button searchButton = findViewById(R.id.searchButton);
        mRecyclerView = findViewById(R.id.recycler_view);
        mNoNetworkView = findViewById(R.id.no_network_view);

        // Initiate search on click of search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchManager.getInstance().clearRequestQueue();
                initSearchAndView(mSearchTerm.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchTerm.getWindowToken(), 0);
            }
        });
    }

    /**
     * This function initializes and resets recycler view adapter and on scroll listener
     * before starting the search.
     * @param query String value of the search query
     */
    private void initSearchAndView(final String query) {
        Log.i(LOG_TAG, "requested search for query " + query);

        // Check for network, if absent display message and return
        if (!NetworkUtils.isNetworkConnected(this)) {
            Log.i(LOG_TAG, "no network connection, aborting search");
            mRecyclerView.setVisibility(View.GONE);
            mNoNetworkView.setVisibility(View.VISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoNetworkView.setVisibility(View.GONE);
        }

        // init adapter
        mAdapter = new RecyclerViewAdapter(mPhotoList);
        mRecyclerView.setAdapter(mAdapter);

        // init and set layout manager with 3 column grid layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        // init OnScrollListener which will provide callback when we reach near the end of the list
        RecyclerViewOnScrollListener scrollListener = new RecyclerViewOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // If we are approaching the end of the list, prepopulate next search results
                if (!mMaxPhotosReached) {
                    loadNextResults(query, page);
                } else {
                    // If we have reached the last page of search, display a message
                    Toast.makeText(getApplicationContext(), R.string.end_of_list_message, Toast.LENGTH_LONG).show();
                }
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        // clear and reset values before starting search
        mPhotoList.clear();
        mAdapter.notifyDataSetChanged();
        mMaxPhotosReached = false;
        scrollListener.resetState();

        // Load the first page results for the search query
        loadNextResults(query, 1);
    }

    /**
     * For a given query and page number, this function loads the results in the recycler view
     * @param query String value of the search query
     * @param page integer value of the search page to load
     */
    private void loadNextResults(final String query, final int page) {
        Log.d(LOG_TAG, "Fetching search result page " + page + " for query " + query);

        // Queuing the search request and notifying adapter on callback
        SearchManager.getInstance().queueForSearchResult(query, page, this, new SearchManager.ISearchResultListener() {
            @Override
            public void onResultReceived(List<Photo> photos, boolean hasNext) {
                // If the result returns no photos, display a message
                if(photos.size() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.no_images_message, Toast.LENGTH_LONG).show();
                }
                mPhotoList.addAll(photos);
                mMaxPhotosReached = !hasNext;
                mAdapter.notifyItemRangeChanged(mPhotoList.size() - photos.size(), photos.size());
            }

            @Override
            public void onError() {
                // Log if we receive an error response
                Log.e(LOG_TAG, "Couldn't load page " + page + " for query " + query);
            }
        });
    }
}
