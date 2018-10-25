package com.abhinav.imagesearcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.managers.SearchManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SearchManagerInstrumentedTest {
    @Test
    public void searchManagerTests() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.abhinav.imagesearcher", appContext.getPackageName());

        String searchUrl = SearchManager.getInstance().createUrl("kittens",25);
        assertEquals("https://api.flickr.com/services/rest?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1&text=kittens&page=25&per_page=30",
                searchUrl);

        final List<Photo> photoList = new ArrayList<>();
        SearchManager.getInstance().getResult("kittens", 1, appContext, new SearchManager.ISearchResultListener() {
            @Override
            public void onResultReceived(List<Photo> photo, boolean hasNext) {
                assertNotNull(photo);
                assertNotNull(photo.get(0));
                assertTrue(hasNext);
                photoList.add(photo.get(0));
            }

            @Override
            public void onError() {

            }
        });

        SearchManager.getInstance().getBitmaps(photoList, 0, appContext, new SearchManager.IimageDownloadResultListener() {
                    @Override
                    public void onResultReceived(Bitmap bitmap, int index) {
                        assertNotNull(bitmap);
                        assertEquals(0, index);
                    }

                    @Override
                    public void onError(int index) {
                        assertEquals(0, index);
                    }
                }
        );
    }
}