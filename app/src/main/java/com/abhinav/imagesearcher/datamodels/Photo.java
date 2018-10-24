package com.abhinav.imagesearcher.datamodels;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {

    private static final String URL_FORMAT = "http://farm%d.static.flickr.com/%s/%s_%s.jpg";
    private static final String JSON_ID = "id";
    private static final String JSON_OWNER = "owner";
    private static final String JSON_SECRET = "secret";
    private static final String JSON_SERVER = "server";
    private static final String JSON_FARM = "farm";
    private static final String JSON_TITLE = "title";
    private static final String JSON_ISPUBLIC = "ispublic";
    private static final String JSON_ISFRIEND = "isfriend";
    private static final String JSON_ISFAMILY = "isfamily";

    private String mId, mOwner, mSecret, mServer, mTitle;
    private int mFarm, mIspublic, mIsfriend, mIsfamily;
    private Bitmap mBitmap;

    private Photo() {

    }

    private Photo(String title, String id, String server, String secret, int farm) {
        this.mTitle = title;
        this.mId = id;
        this.mServer = server;
        this.mSecret = secret;
        this.mFarm = farm;
    }

    public static Photo deserialize(JSONObject object) throws JSONException {
        Photo photo = new Photo();
        photo.mTitle = object.getString(JSON_TITLE);
        photo.mId = object.getString(JSON_ID);
        photo.mServer = object.getString(JSON_SERVER);
        photo.mSecret = object.getString(JSON_SECRET);
        photo.mFarm = object.getInt(JSON_FARM);
        return photo;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return String.format(URL_FORMAT, mFarm, mServer, mId, mSecret);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

}
