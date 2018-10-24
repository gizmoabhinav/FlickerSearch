package com.abhinav.imagesearcher.datamodels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abmukh on 10/23/2018.
 */

public class SearchResult {

    private static final String JSON_PHOTOS = "photos";
    private static final String JSON_STAT = "stat";
    private static final String JSON_PAGE = "page";
    private static final String JSON_PAGES = "pages";
    private static final String JSON_PERPAGE = "perpage";
    private static final String JSON_TOTAL = "total";
    private static final String JSON_PHOTO = "photo";

    private ArrayList<Photo> mImages;
    private int mTotal, mPages, mPage, mPerPage;

    private SearchResult() {

    }

    public static SearchResult deserialize(JSONObject object) throws JSONException {
        SearchResult searchResult = new SearchResult();
        searchResult.mImages = new ArrayList<>();
        JSONObject photosJson = object.getJSONObject(JSON_PHOTOS);
        searchResult.mPage = photosJson.getInt(JSON_PAGE);
        searchResult.mPages = photosJson.getInt(JSON_PAGES);
        searchResult.mPerPage = photosJson.getInt(JSON_PERPAGE);
        searchResult.mTotal = photosJson.getInt(JSON_TOTAL);
        JSONArray photos = photosJson.getJSONArray(JSON_PHOTO);
        for (int i=0;i<photos.length();i++) {
            searchResult.mImages.add(Photo.deserialize((JSONObject) photos.get(i)));
        }
        return searchResult;
    }

    public ArrayList<Photo> getImages() {
        return mImages;
    }

    public int getTotalImages() {
        return mTotal;
    }

    public int getTotalPages() {
        return mPages;
    }

    public int getPage() {
        return mPage;
    }

    public int getPerPageCount() {
        return mPerPage;
    }
}
