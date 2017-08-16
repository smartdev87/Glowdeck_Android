package com.plsco.glowdeck.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.settings.AccountSettingsAdapter;
import com.plsco.glowdeck.settings.SettingsTopFragment;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.task.SendServerSettingsChanged;
import com.plsco.glowdeck.task.TaskCompleted;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.R;

import java.util.ArrayList;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: NewWebAccountActivity.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * NewWebAccountActivity extends Activity
 *                         implements TaskCompleted    
 *
 */

public class NewWebAccountActivity extends Activity  implements TaskCompleted
{
	//
	//  Globals
	//  
	String 		mAuthUrl  = null ;
	String 		mNewAccountName ;
	boolean 	mAccountAccepted = false ; 
	boolean 	mRequestCanceled = false ; 
	String 		mStreamType ;
	ProgressDialog   				mProgressDialog = null ;
	WebView 						mWebView = null;
	//
	// Globals (statics)
	//
	static boolean 					mResumeLoadedPage = false ;
	//static NewWebAccountActivity 	mNewWebAccountActivity ; 
	// Log.d
	static ProgressBar 				mProgressBar;
	static boolean 					mPageLoaded ; 



	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		try{
		if (!mResumeLoadedPage)
		{
			mResumeLoadedPage = true ; 
			Bundle extras = getIntent().getExtras();
			if (mWebView != null)
			{
				mWebView.onResume() ;
				String  webViewURL = extras.getString(AccountSettingsAdapter.OAUTH_URL);
				if ((webViewURL != null) && ( (webViewURL.compareTo("") != 0 )))
				{
					mWebView.loadUrl(webViewURL);
				}

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
		// TODO Auto-generated method stub
		//
		//  before we go back, pop up dialog asking if we really  want to quit
		//

		try{
		if (confirmExit())
		{
			mResumeLoadedPage = false ; 

			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			Intent data = new Intent();

			if (mAccountAccepted)
			{

				data.putExtra(AccountSettingsAdapter.INTENT_EXTRA_NEW_ACCOUNT, mNewAccountName) ;
				setResult(Activity.RESULT_OK, data);
			}
			else
			{
				setResult(Activity.RESULT_CANCELED, data);
			}
			
			super.onBackPressed() ;
		}
		}catch(Exception e){e.printStackTrace();}
	}


	/**
	 * @return - false user does not want to cancel the account add
	 */
	boolean confirmExit()
	{
		try{
		if ((mAccountAccepted) || (mRequestCanceled))
		{
			return true ; 
		}

		AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
		TextView resultMessage = new TextView(this);
		resultMessage.setTextSize(18);
		//Typeface tf = Typeface.DEFAULT_BOLD ;

		String goBack = 		"\nAccount Manager\nYour account has not been added.\n" +
				"Are you sure you want to go back?";




		Spannable span = new SpannableString(goBack); 

		span.setSpan(new RelativeSizeSpan(0.7f), 17,  span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		resultMessage.setText(span);
		resultMessage.setGravity(Gravity.CENTER);
		completeDialog.setView(resultMessage);
		completeDialog.setCancelable(false) ;
		completeDialog.setPositiveButton(Html.fromHtml(
				"<font  color=\"#0088ff\"><b>Yes</></font>"), 
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss(); 
				mRequestCanceled = true ; 
				onBackPressed() ;
			}
		});
		completeDialog.setNegativeButton(Html.fromHtml(
				"<font  color=\"#0088ff\"><b>Cancel</></font>"), 
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();               
			}
		});

		completeDialog.show() ;
		}catch(Exception e){e.printStackTrace();}
		return false ; 
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
		mPageLoaded = false ; 
		mResumeLoadedPage = false ; 
		//mNewWebAccountActivity = this ; 

		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.webview_layout);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAuthUrl  = extras.getString(AccountSettingsAdapter.OAUTH_URL);
			mStreamType = extras.getString(AccountSettingsAdapter.STREAM_TYPE) ;
			setTitle(mStreamType);
		}




		mWebView = (WebView) findViewById(R.id.web_view);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true) ;
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.setVisibility(View.INVISIBLE) ;


		mWebView.setWebViewClient(new WebViewClient(){

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
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



			/* (non-Javadoc)
			 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
			 */
			@Override
			public void onPageFinished(WebView view, String url) {

				super.onPageFinished(view, url);
				/* This call inject JavaScript into the page which just finished loading. */

				mWebView.loadUrl("javascript:HTMLOUT.processHTML('<html>'+" +
						"document.getElementsByTagName('html')[0].innerHTML+'</html>');");

				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","NewWebAccountActivity::onPageFinished::" + url) ;

				}
			}



		});

		}catch(Exception e){e.printStackTrace();}
	}


	/**
	 * 
	 */
	void updateTheStreamsUserStruct() 
	{


		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;


		ArrayList<String> stringArrayList = new ArrayList<String>() ;

		String[] newAcct = new String[]{mNewAccountName } ; 
		if (mStreamType.compareTo(SettingsTopFragment.EMAIL_STREAM_TYPE) == 0)
		{
			stringArrayList.add(StatusStream.TYPE_FOR_EMAIL_TABLE) ;
			streamsUser.getEmail().setAccount(newAcct) ;
			streamsUser.getEmail().setEnabled("1") ;
		}
		if (mStreamType.compareTo(SettingsTopFragment.INSTAGRAM_STREAM_TYPE) == 0)
		{
			stringArrayList.add(StatusStream.TYPE_FOR_INSTAGRAM_TABLE) ;
			streamsUser.getInstagram().setAccount(newAcct) ;
			streamsUser.getInstagram().setEnabled("1") ;
		}

		if (mStreamType.compareTo(SettingsTopFragment.TWITTER_STREAM_TYPE) == 0)
		{
			stringArrayList.add(StatusStream.TYPE_FOR_TWITTER_TABLE) ;
			streamsUser.getTwitter().setAccount(newAcct) ;
			streamsUser.getTwitter().setEnabled("1") ;
		}
		if (mStreamType.compareTo(SettingsTopFragment.FACEBOOK_STREAM_TYPE) == 0)
		{
			stringArrayList.add(StatusStream.TYPE_FOR_FACEBOOK_TABLE) ;
			streamsUser.getFacebook().setAccount(newAcct) ;
			streamsUser.getFacebook().setEnabled("1") ;
		}

		if (stringArrayList.size() > 0)
		{
			String[] parmsArray = stringArrayList.toArray(new String[stringArrayList.size()]);

			new SendServerSettingsChanged().execute(parmsArray) ;
			// delete the database

		}

		onBackPressed() ; 
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
		}catch(Exception e){e.printStackTrace();}
		return true;
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
			onBackPressed() ;
			}catch(Exception e){e.printStackTrace();}
		}



		return super.onOptionsItemSelected(item);

	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
		mResumeLoadedPage = false ; 
		mWebView.destroy() ;
		mWebView = null ;
		finish() ;
		}catch(Exception e){e.printStackTrace();}
	}


	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.TaskCompleted#onTaskComplete(java.lang.Boolean)
	 */
	@Override
	public void onTaskComplete(Boolean result) {
		try{
		if (mProgressDialog != null)
		{
			mProgressDialog.cancel() ;
			mProgressDialog = null ; 
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/* An instance of this class will be registered as a JavaScript interface */
	class MyJavaScriptInterface
	{

		/**
		 * @param html - looking for "accepted" to confirm that the account was added
		 */
		@JavascriptInterface
		public void processHTML(String html)
		{
			try{
			String clientName = null ; 
			String accepted = "AUTHENTICATED:" ;
			// process the html as needed by the app
			int authIndx = html.indexOf(accepted,0) ;
			if (authIndx != -1)
			{
				int indxAccountNameStart = authIndx + accepted.length() ;
				int indxAccountNameEnd = -1 ; 
				if (indxAccountNameStart < html.length())
				{
					indxAccountNameEnd = html.indexOf("<",indxAccountNameStart) ;
				}
				if (indxAccountNameEnd != -1)
				{
					clientName = html.substring(indxAccountNameStart, indxAccountNameEnd  ) ;
				}
			}
			if (clientName != null)
			{
				mNewAccountName = clientName ; 
				mAccountAccepted =true ; 
			}
			if (mAccountAccepted)
			{
				// we are done
				NewWebAccountActivity.this.mWebView.post(new Runnable() {
					public void run() {
						updateTheStreamsUserStruct() ; 
					}
				});
			}
			}catch(Exception e){e.printStackTrace();}
		}
	}
}



