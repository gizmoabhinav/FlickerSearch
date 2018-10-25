package com.abhinav.imagesearcher;

import com.abhinav.imagesearcher.datamodels.Photo;
import com.abhinav.imagesearcher.datamodels.SearchResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.*;

/**
 * local unit test, which will test datamodels deserialization.
 */
public class DataModelUnitTest {

    private JSONObject mResponseJson;

    @Before
    public void createJson() {
        // read from dummy flicker search result file and create json
        InputStream inputStream = this.getClass().getResourceAsStream("/response.json");
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            mResponseJson = new JSONObject(responseStrBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readJson_isCorrect() throws Exception {

        // Verify datamodels get initialized with expected values
        SearchResult result = SearchResult.deserialize(mResponseJson);

        assertEquals(200968, result.getTotalImages());
        assertEquals(2010, result.getTotalPages());
        assertEquals(1, result.getPage());
        assertEquals(100, result.getPerPageCount());

        List<Photo> photos = result.getImages();

        assertEquals(96, photos.size());
        assertEquals("http://farm2.static.flickr.com/1961/44812918754_5f162c99e1.jpg", photos.get(0).getUrl());
    }
}