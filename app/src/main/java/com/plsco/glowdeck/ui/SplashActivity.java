package com.plsco.glowdeck.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.task.LoadingTask;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.task.LoadingTask.LoadingTaskFinishedListener;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: SplashActivity.java
 *
 * Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 *
 */

/**
 * 
 *  SplashActivity called when the Streams is first started. 
 *  Purpose : to display the Streams splash screen
 *  Calls   : LoadingTask to insure the app is ready to go
 *             
 *
 */


/**
 * SplashActivity extends ActionBarActivity
 *  				implements LoadingTaskFinishedListener 
 *
 */
public class SplashActivity extends Activity  implements LoadingTaskFinishedListener {

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","SPlashActivity stopped") ;
		}
	}

	LoadingTask loadingTask = null ;
	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		try{
		setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
		//getSupportActionBar().hide();
		boolean showSpashScreen = true ;
		if(StreamsApplication.DEBUG_MODE)
		{

			Log.d("dbg","splashActivity invoked");
		}
		if (VerifyCredentialsTask.getStreamsUser(this) != null)
		{
			if(StreamsApplication.DEBUG_MODE)
			{

				Log.d("dbg","Streams previously active, mStreamsUser is valid");
			}
			if (MainActivity.getStreamsState() == StreamsScreenState.STARTING)
			{
				MainActivity.setStreamState(StreamsScreenState.STREAMS_VIEW);

			}
			showSpashScreen = false ; 
			if (!UpdaterService.isServiceRunning())
			{
				if(StreamsApplication.DEBUG_MODE)
				{

					Log.d("dbg","SplashActivity:starting UpdaterService") ;
				}
				startService(new Intent(this, UpdaterService.class)); // make sure service running/restarted
			}
		}
		else
		{
			SharedPreferences sharedPrefs  = PreferenceManager.getDefaultSharedPreferences(this);
			String userIDPref = sharedPrefs.getString(LoginActivity.PrefsUserid, LoginActivity.PrefsDefaultString);
			String passwordPref = sharedPrefs.getString(LoginActivity.PrefsPassword, LoginActivity.PrefsDefaultString);
			if ( (userIDPref.compareTo(LoginActivity.PrefsDefaultString)!=0) &&
					(passwordPref.compareTo(LoginActivity.PrefsDefaultString)!=0)	)
			{  // userId/Password both != ""
				showSpashScreen = false ; 
			}
		}
		if (!showSpashScreen)
		{
			startApp();
			finish() ; 
			return ; 
		}
		// Show the splash screen
	//	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	//	getActionBar().hide();
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		setContentView(R.layout.activity_splash);

		// Start  loading
		if(StreamsApplication.DEBUG_MODE)
		{

			Log.d("dbg","splashActivity starting loading task");
		}
		loadingTask =  new LoadingTask( this) ;
		loadingTask.execute() ; 
		}catch(Exception e){e.printStackTrace();}
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 * 
	 * called between onStart() and onPostCreate(), 
	 * 
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);


	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 * 
	 * 
	 *  If called, this method will occur before onStop().  
	 *  There are no guarantees about whether it will occur before or after onPause().
	 * 
	 */
	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);

	}

	// callback for when async task has finished
	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.LoadingTask.LoadingTaskFinishedListener#onTaskFinished()
	 */
	@Override
	public void onTaskFinished() {
		try{
		MainActivity.setStreamState(StreamsScreenState.LOGIN_SCREEN);
		completeSplash();
		}catch(Exception e){e.printStackTrace();}
		return ;
	}

	/**
	 * 
	 */
	private void completeSplash(){
		try{
		startApp();
		loadingTask.cancel(true) ;
		loadingTask = null ;
		finish(); //  finish  so will not return
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * 
	 */
	private void startApp() {
		try{
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart()
	{
		super.onStart(); 


	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		try{
		getMenuInflater().inflate(R.menu.main, menu);
		}catch(Exception e){e.printStackTrace();}
		return false;
	}
}