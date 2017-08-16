package com.plsco.glowdeck.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
*
* Project : GlowDeck/STREAMS
* FileName: VollyTask.java
*
* Copyright 2014. PLSCO, Inc. All rights reserved.
*
*/

/**
* @author Joe Diamand 
* @version 1.0   08/27/14
*
*/
/**
* History
* Prepare for Google Play Store 11/1/14
*/
//

/**
* 
* 
*/
public class VollyTask {
	//
	//  Globals 
	//
	
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    
    //
	//  Globals (static)
	//
    private static Context mCtx;
    private static VollyTask mInstance;
	
    private VollyTask(Context context) {
    	try{
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    	}catch(Exception e){e.printStackTrace();}
    }

    public static synchronized VollyTask getInstance(Context context) {
    	try{
        if (mInstance == null) {
            mInstance = new VollyTask(context);
        }
    	}catch(Exception e){e.printStackTrace();}
        return mInstance;
    }
    
    public RequestQueue getRequestQueue() {
    	try{
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
    	}catch(Exception e){e.printStackTrace();}
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
    	try{
        getRequestQueue().add(req);
    	}catch(Exception e){e.printStackTrace();}
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


	

}
