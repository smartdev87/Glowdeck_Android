package com.plsco.glowdeck.services;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: BootReceiver.java
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



/**
 * BootReceiver is BroadcastReceiver.
 * 
 * In the manifest it is registered to receive "android.intent.action.BOOT_COMPLETED"
 * 
 * This should happen when the phone is first powered up and will insure that the
 * streams service has been started
 * 
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.plsco.glowdeck.ui.StreamsApplication;

public class BootReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) { 
		try{
		context.startService(new Intent(context, UpdaterService.class));
		if (StreamsApplication.DEBUG_MODE)
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "BootReceiver:onReceived ... sent intent to UpdaterService to start it");
			}
		}
	 }catch(Exception e){e.printStackTrace();}
	}
}

