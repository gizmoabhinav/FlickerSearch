# FlickerSearch
App to display image search results from flicker

## Functions
* Supports endless scrolling, automatically requesting and displaying more images when the user scrolls to the bottom of the view.
* Performs in-memory and on disk caching for reducing network calls

## High level flow
* The MainActivity hosts a RecyclerView with a custom OnScrollListener which provides a callback to load more items when we approach the end of the list. The threshold value dictates how many images ahead do we request for more images.
* SearchManager is a singleton class which handles network calls and updation of UI using callbacks. The network requests are posted to a RequestQueue which executes them on maximum of 4 parallel threads.
* On recieving a callback from OnScrollListener to load more images in the adapter, the activity queues a request for the specific page via SearchManager and waits for a callback.
* SearchManager, after getting a response, deserializes the response and fires a callback for the UI to add the photos to the adapter. The adapter is notified with the range of index of the new images.
* On addition of a new image in the adapter, the image bitmap is set using ImageLoader in the view by providing the image url. ImageLoader uses the same RequestQueue and downloads the Bitmap and sets it inside the view.
* ImageLoader is provided with an in-memory LRU cache of the size of 1/8th of the available memory on device, so that it checks and refreshes the cache with the bitmaps before queuing a request on network.
* The Request queue is initialized with a disk based cache of the size of 20 MB. This provides a second level of caching for image download after the in-memory LRU cache, as this will be persisted and also will be greater in size than the in-memory cache. 
