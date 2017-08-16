package com.plsco.glowdeck.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.task.SendServerSettingsChanged;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: IndividualSettingsActivity.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
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
 * The IndividualSettingsActivity extends Activity
 *  
 *  
 *
 */
public class IndividualSettingsActivity extends Activity {


	@Override
	protected void onResume() {
		
		super.onResume();
	}

	// Constants
	private static final String 				AddNewsKeyword = "Add News Keyword" ;
	// Globals
	//	private Activity mThisActivity ; 
	String 										mCurrentStream = null ; 

	// globals (static)
	public static ListView 						mIndividualSettingsLV  = null ;
	enum LED_INDEX {LED_NEWS,LED_EMAIL,LED_TWITTER,LED_INSTAGRAM,LED_WEATHER}  // note if LED_INDEX increases, add more "true"'s
	static boolean[] 							mLEDisOn = new boolean[]{true,true,true,true,true} ; // note if LED_INDEX increases, add more "true"'s
	private static StreamsUser mStreamsUser = null  ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","Indiviudal Settings:OnMeuItemSelected") ;
		}
		return super.onMenuItemSelected(featureId, item);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {

		super.onDestroy();
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","IndividualSettingsActivity::onDestroy()") ;
		}
		SendServerSettingsChanged.CheckAndSendServerSettingsChanged() ;

		mIndividualSettingsLV.setAdapter(null) ;
		mIndividualSettingsLV = null ; 
		mStreamsUser = null ; 
		//mThisActivity = null ; 
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","Indiviudal Settings:OnOptionsItemSelected:mLEDisOn=" + mLEDisOn) ;
		}
		switch (item.getItemId()) {
		case R.id.individual_stream_settings:
			  
			if ( toggleLED(true) )
			{
				// item.setIcon(R.drawable.led_on_half) ;
				item.setIcon(R.drawable.led_on) ;
			}
			else
			{
				item.setIcon(R.drawable.led_on_half) ;
			}

			return true ;
		case android.R.id.home:


			onBackPressed() ;


			return true;	

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		try{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.individual_settings, menu);
		menu.findItem(R.id.individual_action_settings).setVisible(false);

		inflater.inflate(R.menu.individual_settings_activity, menu);
		MenuItem menuItemSettings = menu.findItem(R.id.individual_stream_settings) ;
		if (toggleLED(false))
		{

			menuItemSettings.setIcon(R.drawable.led_on) ;
		}
		else
		{

			menuItemSettings.setIcon(R.drawable.led_on_half) ;
		}
		menuItemSettings.setVisible(true);
		menuItemSettings.setEnabled(true) ;

		}catch(Exception e){e.printStackTrace();}
		return super.onCreateOptionsMenu(menu);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{    //  return to the calling mActivity: the login screen


		super.onBackPressed() ;
		try{
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		try{
		menu.findItem(R.id.individual_action_settings).setVisible(false);
		}catch(Exception e){e.printStackTrace();}
		return super.onPrepareOptionsMenu(menu);
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			String value = extras.getString(SettingsTopFragment.STREAM_TYPE_SETTING );
			if ((value == null) || (value.compareTo("")==0))
			{
				return; 
			}
			this.setTitle(value);
			mCurrentStream = value ; 
		}
		else
		{
			return ; 
		}


		//getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true) ;
		setContentView(R.layout.activity_individual_setting);

		mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
		if (mStreamsUser == null) 
		{
			Activity thisActivity = this ; 
			StreamsApplication streamsApplication = (StreamsApplication)thisActivity.getApplication() ;
			LoginActivity.setStreamsApplication(streamsApplication) ;
			LoginActivity.recoverSavedStreamsUser(thisActivity) ;
			mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
		}
		mIndividualSettingsLV = (ListView)findViewById(R.id.individual_settings_list);

		if (mCurrentStream.compareTo(SettingsTopFragment.NEWS_STREAM_TYPE)==0)
		{

			int sizeKeyWords  = mStreamsUser.getNews().getKeywords().length ;
			int[] iconsResource = new int[sizeKeyWords  + 1] ;
			int[] iconsResource2 = new int[sizeKeyWords  + 1] ;
			String[] keyWordsStr = new String[sizeKeyWords  + 1] ;

			preProcessNews(sizeKeyWords,
					iconsResource,iconsResource2,keyWordsStr) ;

			mIndividualSettingsLV.setAdapter(new NewsSettingsAdapter(this,
					keyWordsStr,
					iconsResource,
					iconsResource2)) ;
			return ;

		}
		if (mCurrentStream.compareTo(SettingsTopFragment.WEATHER_STREAM_TYPE)==0)
		{

			//int sizeKeyWords  = mStreamsUser.getNews().getKeywords().length ;
			int numZipCodes = 1 ;
			//int[] iconsResource = new int[sizeKeyWords  + 1] ;
			int[] iconsResource = new int[numZipCodes] ;
			//int[] iconsResource2 = new int[numZipCodes] ;
			String[] zipCodesStr = new String[numZipCodes] ;

			//preProcessNews(sizeKeyWords,
					//iconsResource,iconsResource2,keyWordsStr) ;
			preProcessWeather(numZipCodes,
			  iconsResource,zipCodesStr) ;
			mIndividualSettingsLV.setAdapter(new WeatherSettingsAdapter(this,
					zipCodesStr,
					iconsResource)) ;
			return ;

		}
		setUpAccountThings() ;

		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 *
	 * setUpAccountThings()
	 *
	 * Creates and initializers a new AccountThings object
	 */
	void setUpAccountThings()
	{
		try{
		boolean setUpAccountSetingsAdapter = false ;
		AccountThings accountThings = new AccountThings() ;
		if (mCurrentStream.compareTo(SettingsTopFragment.EMAIL_STREAM_TYPE)==0)
		{
			if ( (accountThings.numAccts = mStreamsUser.getEmail().getAccount().length) > 0 )
			{
				accountThings.streamAccounts = mStreamsUser.getEmail().getAccount() ;
			}
			preProcessAccounts( accountThings,R.drawable.email_on) ;
			setUpAccountSetingsAdapter = true ;
		}
		if (mCurrentStream.compareTo(SettingsTopFragment.TWITTER_STREAM_TYPE)==0)
		{
			if ( (accountThings.numAccts = mStreamsUser.getTwitter().getAccount().length) > 0 )
			{
				accountThings.streamAccounts = mStreamsUser.getTwitter().getAccount() ;
			}
			preProcessAccounts( accountThings,R.drawable.twitter_on) ;

			setUpAccountSetingsAdapter = true ;
		}

		if (mCurrentStream.compareTo(SettingsTopFragment.INSTAGRAM_STREAM_TYPE)==0)
		{
			if ( (accountThings.numAccts = mStreamsUser.getInstagram().getAccount().length) > 0 )
			{
				accountThings.streamAccounts = mStreamsUser.getInstagram().getAccount() ;
			}
			preProcessAccounts( accountThings,R.drawable.instagram_on) ;

			setUpAccountSetingsAdapter = true ;
		}
		if (setUpAccountSetingsAdapter)
		{
			if (accountThings.numAccts == 0)
			{
				accountThings.acctStr[0] =  "Add " + mCurrentStream + " account" ;
			}
			mIndividualSettingsLV.setAdapter(new AccountSettingsAdapter(this,
					mCurrentStream,
					accountThings.numAccts,
					accountThings.acctStr,
					accountThings.iconsResource,
					accountThings.iconsResource2)) ;
		}
		}catch(Exception e){e.printStackTrace();}
	}


	/**
	 *
	 * Class AccountThings
	 */
	class AccountThings {
		boolean setUpAccountSetingsAdapter = false ;
		int[] iconsResource = null;
		int[] iconsResource2 = null;
		String[] acctStr= null ;
		int numAccts = 0;
		String[] streamAccounts = null ;

	}


	/**
	 * @param sizeKeyWords - # of news keywrods
	 * @param iconsResource - icon read/news
	 * @param iconsResource2 - icon read/news
	 * @param keyWordsStr - the news keywords
	 */
	void preProcessNews(int sizeKeyWords,int[] iconsResource,int[] iconsResource2,
			String[] keyWordsStr)
	{

		try{


		for (int i = 0  ; i < sizeKeyWords ; i++)
		{
			keyWordsStr[i] = mStreamsUser.getNews().getKeywords()[i] ;
			iconsResource[i] = R.drawable.news_on ;
			iconsResource2[i] = R.drawable.red_x ;
		}

		keyWordsStr[sizeKeyWords] = AddNewsKeyword ;
		iconsResource[sizeKeyWords] = R.drawable.plus_icon ;
		iconsResource2[sizeKeyWords] = -1 ;  // dont draw x on last item
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param sizeKeyWords - # of news keywrods
	 * @param iconsResource - icon read/news
	 * @param iconsResource2 - icon read/news
	 * @param keyWordsStr - the news keywords
	 */
	void preProcessWeather(int sizeZipCodes,int[] iconsResource,//int[] iconsResource2,
			String[] keyWordsStr)
	{


		try{

		for (int i = 0  ; i < sizeZipCodes ; i++)
		{
			keyWordsStr[i] = mStreamsUser.getWeather().getLocation().getZip();
			iconsResource[i] = R.drawable.weather_on ;
			//iconsResource2[i] = R.drawable.red_x ;
		}
		}catch(Exception e){e.printStackTrace();}
		//keyWordsStr[sizeZipCodes] = AddNewsKeyword ;
		//iconsResource[sizeZipCodes] = R.drawable.plus_icon ;
		//iconsResource2[sizeZipCodes] = -1 ;  // dont draw x on last item
	}
	/**
	 * @param accountThings - set up accountThings for rendering this seting
	 * @param resource   - the icon for this account
	 */
	void preProcessAccounts(AccountThings accountThings, int resource)
	{

		try{
		accountThings.iconsResource = new int[1] ;
		accountThings.iconsResource2 = new int[1] ;
		accountThings.acctStr = new String[1] ;
		if (accountThings.numAccts == 0 )
		{
			accountThings.iconsResource[0]   =   R.drawable.plus_icon ;
			accountThings.iconsResource2[0]  =   -1  ;
		}
		else
		{
			accountThings.acctStr[0] = accountThings.streamAccounts[0];
			accountThings.iconsResource[0]   =   resource ;
			accountThings.iconsResource2[0]  =   R.drawable.red_x  ;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		// this gets called when we return from the NewWebViewAccount Activity
		// set up the dialog to announce the account, then update the adapter
		//  for the listview (which is currently in mView)
		try{
		if (requestCode == AccountSettingsAdapter.RESULT_CODE_NEW_ACCOUNT)
		{
			if (resultCode == Activity.RESULT_OK)
			{



				//	String mNewAccountName = data.getStringExtra(AccountSettingsAdapter.INTENT_EXTRA_NEW_ACCOUNT) ;

				setUpAccountThings() ;

				AccountSettingsAdapter accountSettingsAdapter =
						(AccountSettingsAdapter) IndividualSettingsActivity.mIndividualSettingsLV.getAdapter() ;
				accountSettingsAdapter.notifyDataSetChanged();
				SettingsTopFragment.settingsAdapter.notifyDataSetChanged() ;
				boolean displayDialog = true ; 
				if (displayDialog)
				{
					AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
					TextView resultMessage = new TextView(this);
					resultMessage.setTextSize(18);
					//	Typeface tf = Typeface.DEFAULT_BOLD ;

					String acctAdded = 		"\nAccount added successfully\n" ;

					Spannable span = new SpannableString(acctAdded); 

					//	span.setSpan(new RelativeSizeSpan(0.7f), 11, 50, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					resultMessage.setText(span);
					resultMessage.setGravity(Gravity.CENTER);
					completeDialog.setView(resultMessage);
					completeDialog.setCancelable(false) ;
					completeDialog.setPositiveButton(Html.fromHtml(
							"<font  color=\"#0088ff\"><b>OK</></font>"), 
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();               
						}
					});

					completeDialog.show() ;
				}


			}
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param changeValue - if true toggle the value, else just return the current value
	 * @return boolean value of the LED
	 */
	boolean toggleLED(boolean changeValue)
	{
		boolean retVal = true ;
		try{
		if (mCurrentStream != null)
		{
			if (mCurrentStream.compareTo(SettingsTopFragment.NEWS_STREAM_TYPE)==0)
			{
				retVal = mLEDisOn[LED_INDEX.LED_NEWS.ordinal()]  ;

				if (changeValue)
				{
					retVal = mLEDisOn[LED_INDEX.LED_NEWS.ordinal()] = !retVal;
				}

			}
			if (mCurrentStream.compareTo(SettingsTopFragment.WEATHER_STREAM_TYPE)==0)
			{
				retVal = mLEDisOn[LED_INDEX.LED_WEATHER.ordinal()]  ;

				if (changeValue)
				{
					retVal = mLEDisOn[LED_INDEX.LED_WEATHER.ordinal()] = !retVal;
				}

			}
			if (mCurrentStream.compareTo(SettingsTopFragment.EMAIL_STREAM_TYPE)==0)
			{

				retVal = mLEDisOn[LED_INDEX.LED_EMAIL.ordinal()]  ;
				if (changeValue)
				{
					retVal = mLEDisOn[LED_INDEX.LED_EMAIL.ordinal()] = !retVal;
				}
			}
			if (mCurrentStream.compareTo(SettingsTopFragment.TWITTER_STREAM_TYPE)==0)
			{
				retVal = mLEDisOn[LED_INDEX.LED_TWITTER.ordinal()]  ;

				if (changeValue)
				{
					retVal = mLEDisOn[LED_INDEX.LED_TWITTER.ordinal()] = !retVal;
				}
			}

			if (mCurrentStream.compareTo(SettingsTopFragment.INSTAGRAM_STREAM_TYPE)==0)
			{
				retVal = mLEDisOn[LED_INDEX.LED_INSTAGRAM.ordinal()]  ;

				if (changeValue)
				{
					retVal = mLEDisOn[LED_INDEX.LED_INSTAGRAM.ordinal()] = !retVal;
				}
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
}













