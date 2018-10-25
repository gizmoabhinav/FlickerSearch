package com.abhinav.imagesearcher.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;

import com.abhinav.imagesearcher.R;
import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.datamodels.SearchResult;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This class manages the flicker images search request and image load request queuing
 * and provides callback to view when the response arrives. This class
 * also handles caching of the responses and images to prevent duplicate
 * network calls
 */
public class SearchManager {

    private static final String LOG_TAG = "SearchManager";

    // API key for flicker search api
    private static final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";

    // The number of photos needed per page/request
    private static final int RESULTS_PER_PAGE = 30;

    // Search Manager singleton object
    private static volatile SearchManager sInstance;

    // Queue for search requests and image download requests
    // This queue executes the requests in maximum of 4 threads
    // This also keeps a disk based cache for preventing duplicate
    // network requests
    private RequestQueue mRequestQueue;

    // Image downloader which keeps an in-memory LRU cache for
    // images already downloaded
    private ImageLoader mImageLoader;

    private SearchManager() {}

    /**
     * Returns SearchManager singleton object
     */
    public static SearchManager getInstance() {
        if (sInstance == null) {
            synchronized (SearchManager.class) {
                if (sInstance == null) {
                    sInstance = new SearchManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * This method queues request for search for a given query and page
     * and returns a callback after getting the result images
     * @param query String for search
     * @param page integer page value
     * @param context activity context
     * @param resultListener ISearchResultListener object to give callback
     */
    public void queueForSearchResult(final String query, final int page, final Context context, final ISearchResultListener resultListener) {
        // initialize queue
        if (mRequestQueue == null) {
            createRequestQueue(context);
        }

        // create request object and set callbacks on receiving response callback
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                createUrl(query, page),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Deserialize the search result response
                            SearchResult result = SearchResult.deserialize(response);
                            // provide callback with list of images and if there are more pages ahead to search
                            resultListener.onResultReceived(result.getImages(), (result.getPage() < result.getTotalPages()));

                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "exception while processing result json for " + page +
                                    " for query " + query + " error: " + e.getMessage());
                            // provide error callback if deserialization fails
                            resultListener.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // provide error callback if request returns error response
                        resultListener.onError();
                    }
                });

        // set request timeout to 5secs and request retries to 2
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // queue the request
        mRequestQueue.add(request);
    }

    /**
     * This function loads the image from a given url after getting a response
     * and caching the value in an in-memory LRU cache in the given view.
     * The image download request is not queued if the image is present in the cache.
     * @param url String url for the image
     * @param view image view to load the image in
     */
    public void setBitmap(String url, NetworkImageView view) {
        view.setImageUrl(url, mImageLoader);
        // set default value till the image is not loaded
        view.setDefaultImageResId(R.drawable.placeholder);
        // set drawable for when the image request fails
        view.setErrorImageResId(R.drawable.error);
    }

    /**
     * This function builds a request url for the given search query and page.
     * @param query String query for search
     * @param page integer page value
     * @return url request string for image search
     */
    public String createUrl(String query, int page) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.flickr.com")
                .appendPath("services")
                .appendPath("rest")
                .appendQueryParameter("method", "flickr.photos.search")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("safe_search", "1")
                .appendQueryParameter("text", query)
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("per_page", String.valueOf(RESULTS_PER_PAGE));
        return builder.build().toString();
    }

    /**
     * This function initializes the request queue with disk based cache
     * and the image loader with an in-memory LRU cache
     * @param context Activity context
     */
    private void createRequestQueue(Context context) {
        // Create disk based cache of 20MB and initialize request queue
        Cache cache = new DiskBasedCache(context.getCacheDir(), 20 * 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        // Create an in-memory LRU cache of size 1/8th of available memory
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(cacheSize);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    /**
     * This function cancels all the requests in the request queue
     */
    public void clearRequestQueue() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    /**
     * Interface for listening to search result response
     */
    public interface ISearchResultListener {
        void onResultReceived(List<Photo> photo, boolean hasNext);
        void onError();
    }
}
