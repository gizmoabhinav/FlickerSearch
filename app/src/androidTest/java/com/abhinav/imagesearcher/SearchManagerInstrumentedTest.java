package com.abhinav.imagesearcher;

import android.content.Context;
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
 * Instrumented test, which will execute on an Android device to verify
 * functions of search manager
 */
@RunWith(AndroidJUnit4.class)
public class SearchManagerInstrumentedTest {
    @Test
    public void searchManagerTests() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.abhinav.imagesearcher", appContext.getPackageName());

        // test flicker search url generation
        String searchUrl = SearchManager.getInstance().createUrl("kittens",25);
        assertEquals("https://api.flickr.com/services/rest?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1&text=kittens&page=25&per_page=30",
                searchUrl);

        // test search result parsing after receiving
        final List<Photo> photoList = new ArrayList<>();
        SearchManager.getInstance().queueForSearchResult("kittens", 1, appContext, new SearchManager.ISearchResultListener() {
            @Override
            public void onResultReceived(List<Photo> photo, boolean hasNext) {
                assertNotNull(photo);
                assertNotNull(photo.get(0));
                assertTrue(hasNext);
                photoList.add(photo.get(0));
            }

            @Override
            public void onError() {}
        });
    }
}