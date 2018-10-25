package com.abhinav.imagesearcher.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Extension of the recycler view on scroll listener to provide callback
 * when we approach towards the end of the list
 */
public abstract class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    // initialize the current page and increment index
    private int currentPage = 1;

    // previous value of total item count in the list
    private int previousTotalItemCount = 0;

    // shows that the request for more results is queued
    private boolean loading = true;

    // index of the starting page of search
    private int startingPageIndex = 1;

    private RecyclerView.LayoutManager mLayoutManager;

    public RecyclerViewOnScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If the total number of items is less than what is visible on screen
        // plus an extra threshold value, trigger loading of more items
        int visibleThreshold = 30;
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            // increment the current page count
            currentPage++;
            // provide callback to load more images and set loading to true
            onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    /**
     * This method is for resetting the state
     * at the beginning of a new search
     */
    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

}