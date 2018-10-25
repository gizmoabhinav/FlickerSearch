package com.abhinav.imagesearcher.datamodels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model to store search result from one page
 */
public class SearchResult {

    private static final String JSON_PHOTOS = "photos";
    private static final String JSON_PAGE = "page";
    private static final String JSON_PAGES = "pages";
    private static final String JSON_PERPAGE = "perpage";
    private static final String JSON_TOTAL = "total";
    private static final String JSON_PHOTO = "photo";

    private List<Photo> mImages;
    private int mTotal, mPages, mPage, mPerPage;

    private SearchResult() {}

    /**
     * Deserialize json object from result to get a SearchResult object
     * @param object Json object from search response corresponding to one SearchResult page
     * @return SearchResult object
     */
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


    /**
     * Returns list of Photo objects in the given search result
     * @return ArrayList of Photo object
     */
    public List<Photo> getImages() {
        return mImages;
    }


    /**
     * Returns total count of photos in the given search result
     * @return integer count of total photos
     */
    public int getTotalImages() {
        return mTotal;
    }


    /**
     * Returns total number of pages for the given search query
     * @return integer count of total pages
     */
    public int getTotalPages() {
        return mPages;
    }


    /**
     * Returns current page of the search result
     * @return integer current page number
     */
    public int getPage() {
        return mPage;
    }


    /**
     * Returns photos per page count for the search query
     * @return integer per page photo count
     */
    public int getPerPageCount() {
        return mPerPage;
    }
}
