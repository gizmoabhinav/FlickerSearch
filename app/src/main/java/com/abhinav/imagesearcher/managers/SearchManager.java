package com.abhinav.imagesearcher.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.datamodels.SearchResult;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchManager {

    private static final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";
    private static final int RESULTS_PER_PAGE = 30;
    private static volatile SearchManager sInstance;

    private RequestQueue requestQueue;


    private SearchManager() {
    }

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

    public void getResult(String query, int page, final Context context, final ISearchResultListener resultListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                createUrl(query, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            resultListener.onResultReceived(SearchResult.deserialize(responseObject).getImages());
                        } catch (JSONException e) {
                            resultListener.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resultListener.onError();
                    }
                });
        requestQueue.add(stringRequest);
    }

    public void getBitmaps(List<Photo> photos, int startIndex, Context context, final IimageDownloadResultListener resultListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        for(final Photo photo : photos) {
            final int photoIndex = startIndex;
            String url = photo.getUrl();
            ImageRequest imageRequest = new ImageRequest(
                    url, // Image URL
                    new Response.Listener<Bitmap>() { // Bitmap listener
                        @Override
                        public void onResponse(Bitmap response) {
                            photo.setBitmap(response);
                            resultListener.onResultReceived(response, photoIndex);
                        }
                    },
                    0, // Image width
                    0, // Image height
                    ImageView.ScaleType.CENTER_CROP, // Image scale type
                    Bitmap.Config.RGB_565, //Image decode configuration
                    new Response.ErrorListener() { // Error listener
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            resultListener.onError();
                        }
                    }
            );
            imageRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES * 2,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(imageRequest);
            startIndex++;
        }
    }

    private String createUrl(String query, int page) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.flickr.com")
                .appendPath("services")
                .appendPath("rest")
                .appendQueryParameter("method", "flickr.photos.search")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("safesearch", "1")
                .appendQueryParameter("text", query)
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("per_page", String.valueOf(RESULTS_PER_PAGE));
        return builder.build().toString();
    }

    public void clearRequestQueue() {
        if (requestQueue != null) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    public interface ISearchResultListener {
        void onResultReceived(List<Photo> photo);
        void onError();
    }

    public interface IimageDownloadResultListener {
        void onResultReceived(Bitmap bitmap, int index);
        void onError();
    }
}
