package com.abhinav.imagesearcher.datamodels;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Datamodel to store one photo object from search result
 */
public class Photo {

    private static final String URL_FORMAT = "http://farm%d.static.flickr.com/%s/%s_%s.jpg";
    private static final String JSON_ID = "id";
    private static final String JSON_SECRET = "secret";
    private static final String JSON_SERVER = "server";
    private static final String JSON_FARM = "farm";
    private static final String JSON_TITLE = "title";

    private String mId, mSecret, mServer, mTitle;
    private int mFarm;

    private Photo() {}

    /*
     * Deserialize json object from result to get a Photo object
     * @param object Json object from search response corresponding to one photo
     * @return Photo object
     */
    public static Photo deserialize(JSONObject object) throws JSONException {
        Photo photo = new Photo();
        photo.mTitle = object.getString(JSON_TITLE);
        photo.mId = object.getString(JSON_ID);
        photo.mServer = object.getString(JSON_SERVER);
        photo.mSecret = object.getString(JSON_SECRET);
        photo.mFarm = object.getInt(JSON_FARM);
        return photo;
    }

    /*
     * Return title of the photo
     * @return String title
     */
    public String getTitle() {
        return mTitle;
    }

    /*
     * Constructs a url from Photo properties to fetch the photo bitmap
     * @return String url for the given photo object
     */
    public String getUrl() {
        return String.format(URL_FORMAT, mFarm, mServer, mId, mSecret);
    }

    /*
     * Return id of the photo
     * @return String id
     */
    public String getId() {
        return mId;
    }

}
