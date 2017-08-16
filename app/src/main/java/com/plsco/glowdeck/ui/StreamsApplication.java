package com.plsco.glowdeck.ui;

import android.app.Application;
import android.content.Context;

import com.plsco.glowdeck.bluetooth.BluetoothBleManager;
import com.plsco.glowdeck.bluetooth.BluetoothSppManager;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.R;

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: StreamsApplication.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 */
/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: SplashActivity.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 * Version 1.01 for GP Store 11/19/14
 */

/**
 * The StreamsApplication extends the Android Application
 *  
 *
 */
public class StreamsApplication extends Application {
	//
	//     Constants
	//
	//public static final boolean DEBUG_MODE = true ; // set to false for production code
	public static final boolean DEBUG_MODE = true ; // set to false for production code
	public static final boolean BLUETOOTH_SUPPORT_BLE = false ; // set to false currently
	public static final boolean BLUETOOTH_SUPPORT_SPP = true ; // set to true currently

	// globals
	private static StatusStream mStatusStream = null ;
	private static BluetoothSppManager mBluetoothSppManager ;
	private static BluetoothBleManager mBluetoothBleManager ;
	public static BluetoothBleManager getBluetoothBleManager() {
		return mBluetoothBleManager;
	}
	public BluetoothSppManager getBluetoothSppManager()
	{
		return mBluetoothSppManager ; 
	}
	public void  clearStatusStream() {

		mStatusStream = null ;  

	}

	public StatusStream getStatusStream() {

		try{
		if (mStatusStream == null)
		{
			mStatusStream = new StatusStream(this) ;
		}
		}catch(Exception e){e.printStackTrace();}
		return mStatusStream ; 

	}
	private static StatusStream statusStreamUpdater = null ;

	public StatusStream getStatusStreamUpdater() {

		try{
		if (statusStreamUpdater == null)
		{
			statusStreamUpdater = new StatusStream(this) ;
		}
		}catch(Exception e){e.printStackTrace();}
		return statusStreamUpdater ; 

	}

	public WeatherInfo weatherInfo ; 
	public static String streamsUserVersion ;
	public static int    streamsDBversion ; 
	public static  void setVersionInfo(Context context) 
	{
		try{
		streamsUserVersion = context.getString(R.string.streams_user_version) ;
		}catch(Exception e){e.printStackTrace();}
	}
	public static  void setDBVersion(Context context) 
	{
		try{
		String streamsDBversionStr =     context.getString(R.string.streams_db_version) ; 
		streamsDBversion = Integer.parseInt(streamsDBversionStr) ;
		}catch(Exception e){e.printStackTrace();}
	}
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {  
		try{
		setVersionInfo(this) ;
		setDBVersion(this) ; 
		weatherInfo = new WeatherInfo () ; 
		if (BLUETOOTH_SUPPORT_SPP)
		{
		 mBluetoothSppManager = new BluetoothSppManager() ;
		}
		else
		{
			mBluetoothSppManager = null;
		}
		if (BLUETOOTH_SUPPORT_BLE)
		{
			mBluetoothBleManager = new BluetoothBleManager() ;
		}
		else
		{
			mBluetoothBleManager = null;
		}
		}catch(Exception e){e.printStackTrace();}
		super.onCreate();

	}

	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {  
		super.onTerminate();

	}
	/**
	 * 
	 */
	public void reInitWeatherInfo()
	{
		try{
		String stat = "" ; 
		if (weatherInfo != null)
		{
			stat = weatherInfo.WeatherStatus ;
		}
		weatherInfo = new WeatherInfo () ; 
		if (stat.length() > 0)
		{
			weatherInfo.WeatherStatus = stat ; 
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public class WeatherInfo
	{
		public String WeatherStatus   ; 
		public String UI_City ;
		public String UI_Temp ;
		public  String UI_Units ; 
		public String UI_Conditions ; 

		public String Streams_City ;
		public String Streams_Temp ;
		public String Streams_Units ; 
		public String Streams_Conditions ; 

		WeatherInfo()
		{

			UI_City = "" ; 
			UI_Temp = "";
			UI_Units = ""; 
			UI_Conditions = ""; 

			Streams_City = "";
			Streams_Temp = "";
			Streams_Units = ""; 
			Streams_Conditions = "" ; 
			WeatherStatus = "" ;
		}

	}


}
