package com.plsco.glowdeck.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.plsco.glowdeck.ui.StreamsApplication;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: NetStatusReceiver.java
 *
 * Copyright 2014. PLSCO, Inc. All rights reserved.
 * @author jdiamand
 *
 */

/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * NetStatusReciever is BroadcastReceiver.
 * In the manifest it is registered to receive "android.net.conn.CONNECTIVITY_CHANGE"
 * This should happen each time the network connectivity changes
 * if the change is deemed to be different then what was previously registered (or if this is the first change)
 * then an intent to that effect will be sent to the updaterService 
 * 
 */
public class NetStatusReceiver extends BroadcastReceiver {

	// constants
	public static final String NET_STATUS = "Network Status" ;
	public static final String NET_STATUS_UP = "Network Status Up" ;
	public static final String NET_STATUS_DOWN = "Network Status Down" ;
	public static final String TAG = "dbg";
	// globals
	public static int msPrevState = 0 ; // 0 = unknown, 1 = up , 2= down ; 
	//

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		try{
		boolean isNetworkDown = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);   
		int currentState ; 

		if (isNetworkDown) {
			if (msPrevState == -1 )
			{ // down prev, no change
				return ; 
			}
			// 
			currentState = -1 ; 

			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d(TAG, "NetStatusReceiver:onReceive: NOT connected");
			}

		} 
		else 
		{
			if (msPrevState == 1) // network is up and previously up
			{
				return ; 
			}
			// else , set the currentState as up
			currentState = 1 ; 
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d(TAG, "NetStatusReceiver:onReceive: connected");
			}

		}
		// if we get this far, we have a msState change, inform the updater
		Intent sendIntent = new Intent(context, UpdaterService.class) ;

		Bundle bundle = new Bundle();
		if (currentState == 1)
		{
			bundle.putString(NET_STATUS,NET_STATUS_UP );
		}
		else
		{
			bundle.putString(NET_STATUS,NET_STATUS_DOWN);
		}


		sendIntent.putExtras(bundle);

		context.startService( sendIntent); 
		msPrevState = currentState ; 
		 }catch(Exception e){e.printStackTrace();}
	}

}

