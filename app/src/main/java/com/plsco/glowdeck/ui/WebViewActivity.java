package com.plsco.glowdeck.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.plsco.glowdeck.drawer.StreamsFragment;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;
//import android.widget.ShareActionProvider;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.ActionBarActivity;
/*
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
*/

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: WebViewActivity.java
 *
 * (c) Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * 11/1/14 - prepare for Google Play store
 */
/**
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 */

/**
 * The WebViewActivity extends Activity
 * Opens a webview client
 *  
 *
 */
public class WebViewActivity extends   Activity { 
	// Globals (statics)
	//
	static boolean 						mIsActive = false ;  // prevent multiple launches
	static boolean 						mResumeLoadedPage ;
	static WebViewActivity mActivity ;
	static ProgressBar 					mProgressBar;
	static boolean 						mPageLoaded ; 
	
	//
	// 
	// Globals
	WebView 							mWebView = null;
	String                              mContentType = ""  ; 
	String                              mContentSource = "" ; 
	String								mContentSubject = "" ; 
	private ShareActionProvider         mShareActionProvider ;
	//	

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {

		super.onPause();
		try{
		if (mWebView != null)
		{
			mWebView.onPause() ;

		}
		}catch(Exception e){e.printStackTrace();}
	}

	


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

		try{
		if (!mResumeLoadedPage)
		{
			Bundle extras = getIntent().getExtras();
			if (mWebView != null)
			{
				mWebView.onResume() ;
			}
			if (extras != null) {
				String webViewURL = extras.getString(StreamsFragment.W_URL);
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","WebViewActivity::onResume::W_URL=" +webViewURL) ;
				}
				mWebView.loadUrl(webViewURL);
				mResumeLoadedPage = true ;

			}
		}
		}catch(Exception e){e.printStackTrace();}
		super.onResume();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		try{
		Intent intent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, intent) ;

		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

		mResumeLoadedPage = false ;

		finish() ;
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		try{
		if (mIsActive)
		{   // detect and dismiss a double click
			mIsActive = false ;
			finish() ;
			return ;
		}
		mIsActive = true ;
		mPageLoaded = false ;
		mResumeLoadedPage = false ;
		mActivity = this ;
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.webview_layout);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String value = extras.getString(StreamsFragment.W_TITLE);
			this.setTitle(value);
			this.mContentSource = extras.getString(StreamsFragment.W_SOURCE);
			this.mContentType = extras.getString(StreamsFragment.W_TYPE);
			this.mContentSubject = extras.getString(StreamsFragment.W_SUBJECT);
		}



		mWebView = (WebView) findViewById(R.id.web_view);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true) ;
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.setVisibility(View.INVISIBLE) ;

		mWebView.setWebViewClient(new WebViewClient(){

			/* (non-Javadoc)
			 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
			 */
			@Override
			public void onPageFinished(WebView view, String url) {

				super.onPageFinished(view, url);


			}

			/* (non-Javadoc)
			 * @see android.webkit.WebViewClient#doUpdateVisitedHistory(android.webkit.WebView, java.lang.String, boolean)
			 */
			@Override
			public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
				if(!mPageLoaded)
				{
					mPageLoaded = true ;
					mProgressBar.setVisibility(View.GONE);
					mWebView.setVisibility(View.VISIBLE) ;
				}
				super.doUpdateVisitedHistory(view, url, isReload);
			}

		});

		}catch(Exception e){e.printStackTrace();}
	}




	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		try{
		getMenuInflater().inflate(R.menu.stream_webview, menu);

		menu.findItem(R.id.action_settings).setVisible(false);

		 // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.menu_item_share);

	    mShareActionProvider = new ShareActionProvider(this);
	    MenuItemCompat.setActionProvider(item,  mShareActionProvider) ;


		}catch(Exception e){e.printStackTrace();}


	    return super.onCreateOptionsMenu(menu) ;
		//return true;
	}
		/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent mActivity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id)
		{

		case android.R.id.home:
			try{
			MainActivity.setStreamState(StreamsScreenState.STREAMS_VIEW) ;
			Intent intent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, intent) ;
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			mResumeLoadedPage = false ;
			}catch(Exception e){e.printStackTrace();}
			//return true;
			 break ;

		case  R.id.menu_item_share:
			try{
			onShareAction();
			}catch(Exception e){e.printStackTrace();}
			//return true;
			 break ;

		}

		return super.onOptionsItemSelected(item);

	}
	private void onShareAction(){
		try{
	    // Create the share Intent
		String openingText = "The following " ;
		String afterOpeningText = "post " ;
		String typeShare = mContentType ;
		String sourceShare = mContentSource ;
		if (sourceShare.length() > 0)
		{
			sourceShare = " '" + sourceShare + "' " ;
		}
		if (typeShare.length() > 0)
		{
			String cap =  typeShare.charAt(0) + "" ;
			typeShare  = cap.toUpperCase() + typeShare.substring(1) ;
			typeShare = " " + typeShare + " " ;
			if (mContentType.compareTo(StatusStream.TYPE_FOR_NEWS_TABLE) == 0)
			{
				typeShare = //typeShare +
						" article " ;
				afterOpeningText = sourceShare  + typeShare ;
			}
			if (mContentType.compareTo(StatusStream.TYPE_FOR_TWITTER_TABLE) == 0)
			{
				typeShare = typeShare + "post " ;
				afterOpeningText = sourceShare + typeShare ;
			}
			if (mContentType.compareTo(StatusStream.TYPE_FOR_INSTAGRAM_TABLE) == 0)
			{
				typeShare = typeShare + "post " ;
				afterOpeningText = sourceShare + typeShare ;
			}
			if (mContentType.compareTo(StatusStream.TYPE_FOR_WEATHER_TABLE) == 0)
			{
				typeShare = typeShare + "post " ;
				afterOpeningText = "weather forecast for" + sourceShare ;
			}
		}
		String intentText =
				"The following link of a " + afterOpeningText +
	    		"was sent to you from the Android Glowdeck Streams application." +
	    		" \n \n" +
	    		mWebView.getUrl() +
	    		"\n \n" +
	    		"To download the Glowdeck Streams application for Android, select the following link:" +
	    		"\n \n" +
	    		"https://play.google.com/store/apps/details?id=com.glowdeck.streams" ;
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, intentText);
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mContentSubject);
		
		
	    // Set the share Intent
	    if (mShareActionProvider != null) {
	    	mShareActionProvider.setShareIntent(sharingIntent);
	    }
		}catch(Exception e){e.printStackTrace();}
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {

		
		try{
		if (mWebView != null) 
		{
		//	mWebView.destroy() ;
			mWebView = null ;
		}
		if (mIsActive)
		{
			finish() ;
			mIsActive = false ; 
		}
		}catch(Exception e){e.printStackTrace();}
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","WebViewActivity::onKeyUp::keyCode" + keyCode) ;
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		}catch(Exception e){e.printStackTrace();}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 
	 * MyWebView class extends Webview
	 * 
	 * used by email client
	 */
	class MyWebView extends WebView {

		public MyWebView(Context context) {
			super(context);

		}

	}
	 

	

}
