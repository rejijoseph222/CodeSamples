package me.cexi.tweetmaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Handles the asynchronous download of drawables.
 * 
 * @author federicobaseggio
 */
public class BlisdDrawableManager {
	private final static String TAG ="DrawableManager";
    private static class DrawableHolder {
    	Drawable drawable;
    	boolean fetchComplete;
    	List<Handler> completeHandlers = new ArrayList<Handler>();
    }
	
	private final Map<String, DrawableHolder> drawableMap;

    /**
     * Constructor
     */
    public BlisdDrawableManager() {
        drawableMap = new HashMap<String, DrawableHolder>();
    }

    /**
     * Downloads a the image specified by the URL.
     * 
     * @param urlString The URL of the image to download
     * @return A Drawable representing the downloaded image
     */
    private Drawable fetchDrawable(String urlString) {
        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");
            if(drawable!= null)
            {
            Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                    + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                    + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            }
            return drawable;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

	/*
	 * Performs an asynchronous download of the image specified by the URL hence
	 * this method returns immediately. When the download is complete the image
	 * is set to the specified ImageView.
	 * 
	 * @param urlString
	 *            The URL of the image to download
	 * @param imageView
	 *            The view for the downloaded image
	 */
    public void fetchDrawableAsync(final String urlString, final DrawableFetchedHandler fetchedHandler) {
    	final DrawableHolder drawableHolder;
        
    	final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
            	fetchedHandler.setDrawable((Drawable) message.obj);
            }
        };

        DrawableHolder dh = null;
    	synchronized(drawableMap)
        {
    		dh = drawableMap.get(urlString);
    		if (dh != null) {
    			if (dh.fetchComplete)
    			{
    				Log.i(TAG, "FOUND COMPLETE: " + urlString);
    				fetchedHandler.setDrawable(dh.drawable);
    			}
    			else
    			{
    				Log.i(TAG, "FOUND DOWNLOADING: " + urlString);
    				dh.completeHandlers.add(handler);
    			}
	            return;
	        }
			Log.i(TAG, "START DOWNLOADING: " + urlString);
    		dh = new DrawableHolder();
	        drawableMap.put(urlString, dh);
	        dh.completeHandlers.add(handler);
        }
    	
    	drawableHolder = dh;

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
            	drawableHolder.drawable = fetchDrawable(urlString);
            	synchronized(drawableMap)
                {
            		Log.i(TAG, "PROCESSING QUEUE("
							+ drawableHolder.completeHandlers.size() + "): "
							+ urlString);
            		drawableHolder.fetchComplete = true;
	                for (Handler handler : drawableHolder.completeHandlers)
	                {
		                Message message = handler.obtainMessage(1, drawableHolder.drawable);
		                handler.sendMessage(message);
	                }
                }
            }
        };
        thread.start();
    }
    
    public static interface DrawableFetchedHandler
    {
    	void setDrawable(Drawable drawable);
    }

	/**
	 * Performs an HTTP GET request to retrieve the resource specified by the
	 * URL and returns the InputStream of the response payload.
	 * 
	 * @param urlString The URL of the resource to retrieve.
	 * @return The InputStream of the response payload.
	 * @throws MalformedURLException
	 * @throws IOException
	 */
    private InputStream fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

}
