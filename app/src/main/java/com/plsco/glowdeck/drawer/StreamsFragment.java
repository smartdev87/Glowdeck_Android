package com.plsco.glowdeck.drawer;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.CharArrayBuffer;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.streamdata.StreamAdapter;
import com.plsco.glowdeck.task.SendServerArticleWasRead;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.EmailViewActivity;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.ui.WebViewActivity;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.ui.MainActivity.StreamsType;
//import android.support.v4.app.FragmentTransaction;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: StreamsFragment.java
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
 * History
 * Prepare for Google Play Store 11/1/14
 */


/**
 * The StreamsFragment extends the EnhancedStreamsListFragment
 *  
 *
 */
public class StreamsFragment extends EnhancedStreamsListFragment {
	// Constants
	public final static String STREAM_TYPE_PREFERENCE = "StreamTypePreference" ;
	public final static String W_TITLE = "WebView Title" ; 
	public final static String W_URL   = "WebView URL" ; 
	public final static String W_SOURCE   = "WebView Source" ; 
	public final static String W_TYPE   = "WebView Type" ;
	public final static String W_SUBJECT   = "WebView Subject" ;
	public final static String E_FROM   = "EmailView From" ; 
	public final static String E_CONTENT   = "EmailView Content" ; 
	public final static String E_SUBJECT   = "EmailView Subject" ; 
	//
	public final static int PAGE_PERSONAL   = 0 ; 
	public final static int PAGE_SOCIAL   = 1 ; 
	public final static int PAGE_PUBLIC   = 2 ; 
	//
	private final static String WEATHER_UNAVILABLE_MSG = 
			" -- " +"Weather Info : "   + UpdaterService.STREAMS_WEATHER_CITY_UNAVAILABLE + " -- " ;
	//
	public final static String STREAMS_LISTVIEW_ACTIVE = "STREAMS_LISTVIEW_ACTIVE" ;
	public final static String STREAMS_LISTVIEW_INACTIVE = "STREAMS_LISTVIEW_INACTIVE" ;
	public final static int   NETWORK_UNAVAILABLE_MESSAGES_THRESHHOLD = 2 ; 
	public final static int  WEATHER_UNAVAILABLE_MESSAGES_THRESHHOLD = 3 ; 
	public final static String   NETWORK_UNAVAILABLE_MESSAGES_THRESHHOLD_MSG = "Weather update unavailable ... no network" ;
	public final static String   WEATHER_UNAVAILABLE_MESSAGES_THRESHHOLD_MSG = "Weather information unavailable" ;
	public final static String   ACQUIRING_WEATHER_INFO = " ... Acquiring current weather conditions" ;
	//
	//   Local Statics
	//
	private static boolean msFirstRecieve = true ; 
	private static Parcelable msState = null;
	private static TextView msListViewHeader = null ;
	
	//
	//   Globals
	//

	private ListView 									mListView ;
	public ListView getmListView() {
		return mListView;
	}

	private int                                         mPositionPersonal       = 0 ; 
	private int                                         mPositionSocial         = 0 ; 
	private int                                         mPositionPublic         = 0 ; 
	public void setmPositionPersonal(int mPositionPersonal) {
		this.mPositionPersonal = mPositionPersonal;
	}
	public void setmPositionSocial(int mPositionSocial) {
		this.mPositionSocial = mPositionSocial;
	}
	public void setmPositionPublic(int mPositionPublic) {
		this.mPositionPublic = mPositionPublic;
	}
	
	private StreamsFragment mStreamsFragment ;
	//private int 										mPrevFirstVisable = -1 ; 
	StreamsUpdateReceiver mStreamsUpdateReceiver ;
	IntentFilter mIntentFilter; 
	String mNetworkStatus ; 
	static int   mNetworkStatusUnavail = 0 ; 
	static int   mWeatherInfoUnavail = 0 ; 
	ImageSpan    mPage1Dots    = null ; 
	ImageSpan    mPage2Dots    = null ; 
	ImageSpan    mPage3Dots    = null ; 
	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onAttache in streamsfragement----") ;
		}
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {

		super.onDestroy();
		try{
		cleanUpGlobals() ; 
		}catch(Exception e){e.printStackTrace();}
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onDestroy in streamsfragement----") ;
		}
		
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {

		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onStart in streamsfragement----") ;
		}
		super.onStart();
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onStop in streamsfragement----") ;
		}
		 try{
		if (msStreamsCursor != null)
		{
			msStreamsCursor.close() ;
			msStreamsCursor = null;
		}
		 }catch(Exception e){e.printStackTrace();}
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (StreamsApplication.DEBUG_MODE)
		{

			Log.d("dbg","onActivityCreated in streamsfragement----") ;
		}
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		try{
		init(this.getActivity()) ;
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		msFirstRecieve = true ; 
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d ("dbg","StreamFragment OnCreate, savedInstanceState = " + savedInstanceState);
		}
		
		if (savedInstanceState != null)
		{
			Integer streamsType  =  (Integer) savedInstanceState.getSerializable(MainActivity.STREAMS_TYPE);
			if (streamsType != null)
			{
				MainActivity.setStreamType( MainActivity.intToStreamsType(streamsType.intValue()) );
			}
		}
		else
		{
			// try to get the stream type from preferences
			SharedPreferences settings = this.getActivity().getPreferences(0) ;
		    int streamsType = settings.getInt(STREAM_TYPE_PREFERENCE, 0);
		    MainActivity.setStreamType( MainActivity.intToStreamsType(streamsType));
		}
		
		
		
		
		mStreamsUpdateReceiver = new StreamsUpdateReceiver() ; 
		mIntentFilter = new IntentFilter(UpdaterService.STREAMS_UPDATED_INTENT);
		mStreamsFragment = this ; 
		setHasOptionsMenu(true); 
		mActivity = this.getActivity() ;
		if (mPage1Dots == null)
		{
			mPage1Dots = new ImageSpan(this.getActivity(), R.drawable.page1_dots);
		}
		if (mPage2Dots == null)
		{
			mPage2Dots = new ImageSpan(this.getActivity(), R.drawable.page2_dots);
		}
		if (mPage3Dots == null)
		{
			mPage3Dots = new ImageSpan(this.getActivity(), R.drawable.page3_dots);
		}
		}catch(Exception e){e.printStackTrace();}
	}



	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		try{
		StreamsApplication sApplication  = (StreamsApplication) this.getActivity().getApplication() ;
		sApplication.clearStatusStream() ; 
		StatusStream statusStream = sApplication.getStatusStream() ;



		msStreamsCursor = statusStream.getStatusUpdates() ;
		if (msStreamsCursor == null)
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d ("dbg","StreamsFragment::OnResume msStreamsCursor is null, bye");
			}
			return ; 
		}
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d ("dbg","StreamFragment Resume retrievd " + msStreamsCursor.getCount() 
					+ " records" );
		}
		if (msStreamAdapter == null)
		{
			msStreamAdapter = new StreamAdapter(this.getActivity(),msStreamsCursor) ;
		}
		else
		{
			msStreamAdapter.changeCursor(msStreamsCursor) ;
		}
		setListAdapter(msStreamAdapter);



		this.getActivity().registerReceiver(mStreamsUpdateReceiver, mIntentFilter);
		if ((mListView != null) && (msState != null) )
		{

			mListView.onRestoreInstanceState(msState);
		}

		// get the updaterService to poll immediately 
		Intent startUpdaterService= new Intent();
		startUpdaterService.setClass(this.getActivity(),UpdaterService.class);
		startUpdaterService.putExtra(STREAMS_LISTVIEW_ACTIVE, true);
 
		this.getActivity().startService(startUpdaterService);
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();

		// UNregister the receiver
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onPause in streamsfragement----") ;
		}
		try{
		this.getActivity().unregisterReceiver(mStreamsUpdateReceiver);  
		//msStreamAdapter = null ; 
		// get the updaterService to poll infrequently 
		Intent startUpdaterService= new Intent();
		startUpdaterService.setClass(this.getActivity(),UpdaterService.class);
		startUpdaterService.putExtra(STREAMS_LISTVIEW_INACTIVE, true);
		this.getActivity().startService(startUpdaterService);
		if (mListView != null)
		{
			msState   = mListView.onSaveInstanceState();
		}
		else
		{
			msState = null ; 
		}
		SharedPreferences sharedPrefs = this.getActivity().getPreferences(0) ;
	    SharedPreferences.Editor editor = sharedPrefs.edit();
	    editor.putInt(STREAM_TYPE_PREFERENCE,  MainActivity.getStreamsType().ordinal());
	    editor.commit() ;
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Constructor
	 */
	public StreamsFragment()
	{

	}

	/* (non-Javadoc)
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_streams, container, false);
		mListView = (ListView) rootView.findViewById(android.R.id.list);

		try{
		
		if (msListViewHeader == null)
		{

			msListViewHeader = new TextView(this.getActivity()); 
			msListViewHeader.setTextColor(Color.WHITE) ;
			//msListViewHeader.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY) ;
			//msListViewHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER) ;
			msListViewHeader.setGravity(Gravity.CENTER);
			setPageDots( MainActivity.getStreamsType().ordinal()) ;

		}



		//String weatherInfo = ACQUIRING_WEATHER_INFO ;
		StreamsApplication streamsApplication = (StreamsApplication)this.getActivity().getApplication() ;
		//if (streamsApplication.weatherInfo.WeatherStatus.compareTo(UpdaterService.STREAMS_WEATHER_CITY_UNAVAILABLE)  == 0 )
		//{
			//weatherInfo = WEATHER_UNAVILABLE_MSG    ; 
		//}

		mListView.addHeaderView(msListViewHeader);
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		/*
		if (streamsUser != null)
		{
			if ( streamsUser.getWeather().getEnabled().compareTo("0") == 0 )
			{ // weather is off 
				mListView.removeHeaderView(msListViewHeader) ;
			}
		}
		*/
		mListView.setOnTouchListener( this) ;
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {


			}

			public void onScrollStateChanged(AbsListView listView,
					int scrollState) {


			}

		});
		}catch(Exception e){e.printStackTrace();}
		return mListView ; 
	}
	public void setPageDots(int pageNumber)
	{
		try{
		SpannableString text = new SpannableString(" ") ;
		ImageSpan is ; 
		
		
		
		if (pageNumber == StreamsType.STREAM_TYPE_PUBLIC.ordinal())
		{
			is = mPage3Dots ; 
		}
		else
		{
			if (pageNumber == StreamsType.STREAM_TYPE_SOCIAL.ordinal())
			{
				is = mPage2Dots ; 
			}
			else
			{
				is = mPage1Dots ; 
			}
		}
		
		text.setSpan(is, 0, 1, 0) ;

		msListViewHeader.setText(text) ;
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @return string with current weather info (if available)
	 */
	String getUpdatedWeatherInfo() 
	{
		StreamsApplication streamsApplication= (StreamsApplication)this.getActivity().getApplication() ;
		String theWeatherInfo = "" ; 

		try{
		if (streamsApplication.weatherInfo.WeatherStatus.compareTo(UpdaterService.STREAMS_WEATHER_CITY_UNAVAILABLE)  == 0 )
		{
			theWeatherInfo = WEATHER_UNAVILABLE_MSG   ; 
		}
		else
		{
			if (streamsApplication.weatherInfo.Streams_Temp.compareTo("")==0)
			{
				theWeatherInfo = ACQUIRING_WEATHER_INFO ;
			}
			else
			{
				theWeatherInfo = " -- " + streamsApplication.weatherInfo.UI_City +  " : " +
						streamsApplication.weatherInfo.Streams_Temp +  (char) 0x00B0 + 
						streamsApplication.weatherInfo.Streams_Units + " and " +
						streamsApplication.weatherInfo.UI_Conditions + " -- "  ; 
			}
		}
		if (mNetworkStatusUnavail > NETWORK_UNAVAILABLE_MESSAGES_THRESHHOLD ) {
			theWeatherInfo = NETWORK_UNAVAILABLE_MESSAGES_THRESHHOLD_MSG ;
		} 
		else
			if (mWeatherInfoUnavail > WEATHER_UNAVAILABLE_MESSAGES_THRESHHOLD)
			{
				theWeatherInfo = WEATHER_UNAVAILABLE_MESSAGES_THRESHHOLD_MSG ; 
			}
		}catch(Exception e){e.printStackTrace();}
		return theWeatherInfo ;
	}

	
	/* (non-Javadoc)
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamsFragment::onListItemClick::value of getDisableOnClickListent" + getDisableOnClickListener() ) ;
		}
		if (getDisableOnClickListener() == CLICK_STATE.STARTED)
		{
			return ; 
		}
		if (getDisableOnClickListener() == CLICK_STATE.DONE)
		{
			setDisableOnClickListener(CLICK_STATE.STARTED) ; 
			if (getDistanceMoved() > 20.0F)
			{
				// if user swiped list item, ignore the click until it settles down
				return ; 
			}

		}
		position -= l.getHeaderViewsCount() ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamsFragment::onListItemClick::value of position:" + position ) ;
		}
		if (position == -1 )
		{  // clicked on header, switch to next page
			
			((MainActivity)getActivity()).switchStreamPage() ;
			return ; 
		}

		msStreamsCursor.moveToPosition(position) ;

		int bufSize = 0 ; // size of the buffer to allocate
		int col = 0 ; 

		col = msStreamsCursor.getColumnIndex(StatusStream.C_TYPE) ;
		bufSize = msStreamsCursor.getString(col).length() + 1 ;
		CharArrayBuffer buffType = new CharArrayBuffer(bufSize) ;
		msStreamsCursor.copyStringToBuffer(col, buffType) ;
		String itemType = convertCABToString(buffType) ;

		
		
		
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamsFragment::onListItemClick::value of item type" + itemType) ;
			Log.d("dbg","StreamsFragment::onListItemClick::value of position" + position) ;
		}
		
		
		if (itemType.compareToIgnoreCase(StatusStream.PERSONAL_EMAIL_TYPE) == 0)
		{
			processPersonalEmailSelection() ;
		}
		
		if (itemType.compareToIgnoreCase(StatusStream.SOCIAL_INSTAGRAM_TYPE) == 0)
		{
			processSocialInstagramSelection() ;
		}
		if (itemType.compareToIgnoreCase(StatusStream.SOCIAL_TWITTER_TYPE) == 0)
		{
			processSocialTwitterSelection() ;
		}
		if (itemType.compareToIgnoreCase(StatusStream.PUBLIC_NEWS_TYPE) == 0)
		{
			processPublicNewsSelection() ;
		}
		if (itemType.compareToIgnoreCase(StatusStream.PUBLIC_WEATHER_TYPE) == 0)
		{
			processPublicWeatherSelection() ;
		}
		}catch(Exception e){e.printStackTrace();}
	}


	/**
	 * 
	 */
	void processPublicNewsSelection()
	{

		try{
		String urlStr = getDataAtColumn(StatusStream.P_URL_CONDITIONS) ;
		String sourceStr = getDataAtColumn(StatusStream.P_CITY_SOURCE) ;
		String keywordStr = getDataAtColumn(StatusStream.P_KEYWORD_TEMPERATURE) ;
		String subjectStr = getDataAtColumn(StatusStream.P_ARTICLE_UNITS) ;

		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamsFragment::processPublicNewsSelection::value of URL" + urlStr) ;
		}
		// start the display news story mActivity 
		Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
		intent.putExtra(W_TITLE,sourceStr + " | " + keywordStr);
		intent.putExtra(W_URL,urlStr );
		intent.putExtra(W_SOURCE,sourceStr );
		intent.putExtra(W_SUBJECT,subjectStr );
		intent.putExtra(W_TYPE, StatusStream.TYPE_FOR_NEWS_TABLE );
		startActivity(intent);
		this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		StreamsApplication sApplication  = (StreamsApplication) this.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;
		//String idStr = getDataAtColumn(StatusStream.C_ID) ;
		String[] parmsArray = new String[] {getDataAtColumn(StatusStream.C_ID), StatusStream.TYPE_FOR_NEWS_TABLE} ;
		statusStream.setArticleWasRead( parmsArray[0], StatusStream.PUBLIC_STREAMS_TABLE) ;
		new SendServerArticleWasRead().execute(parmsArray) ;

		
		}catch(Exception e){e.printStackTrace();}

	}
	/**
	 * 
	 */
	void processPublicWeatherSelection()
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		String streamsUserCity = "" ;
		String streamsUserZip  = "" ;
		String city = getDataAtColumn(StatusStream.P_CITY_SOURCE) ;
		if (streamsUser.getWeather().getLocation().getCity().length() > 0 )
		{
			streamsUserCity = streamsUser.getWeather().getLocation().getCity() ;
			if (streamsUserCity.compareToIgnoreCase(city) == 0)
			{ // match , ok to use zip
				if (streamsUser.getWeather().getLocation().getZip().length() > 0 )
				{
					streamsUserZip = streamsUser.getWeather().getLocation().getZip() ;
				}
			}
			 
		}
		if (city.length() == 0)
		{
			return ;
		}
		String urlPrefixStr = "https://www.google.com/search?q=weather" ; 
		 
		
		String urlCity =  city.replace(" ", "%20");
		String urlZip = "" ; 
		if (streamsUserZip.length() > 0)
		{
			urlZip = "%20" + streamsUserZip ;
		}
		String urlStr = urlPrefixStr +  "%20" + urlCity +  urlZip;
		String keywordStr = getDataAtColumn(StatusStream.P_KEYWORD_TEMPERATURE) ;
		

		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamsFragment::processPublicWeatherSelection::value of URL" + urlStr) ;
		}
		// start the display news story mActivity 
		Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
		intent.putExtra(W_TITLE,"Weather | " + city);
		intent.putExtra(W_URL,urlStr );
		intent.putExtra(W_SOURCE,city );
		intent.putExtra(W_SUBJECT,"Weather for " + city );
		intent.putExtra(W_TYPE, StatusStream.TYPE_FOR_WEATHER_TABLE );
		startActivity(intent);
		this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		
		}catch(Exception e){e.printStackTrace();}
		


	}
	/**
	 * 
	 */
	void processSocialTwitterSelection()
	{

		try{
		String urlStr = getDataAtColumn(StatusStream.S_URL) ;
		String title = getDataAtColumn(StatusStream.S_FROM_USER) ;
		String subTitle = getDataAtColumn(StatusStream.S_SCREEN_JPG) ;
		String subject = getDataAtColumn(StatusStream.S_CONTENT)  ;
		subject = subjectTrim(subject) ; 

		// start the display news story mActivity 
		Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
		intent.putExtra(W_TITLE,title + " | @" + subTitle);
		intent.putExtra(W_URL,urlStr );
		intent.putExtra(W_SOURCE,"@" + subTitle );
		intent.putExtra(W_SUBJECT,subject );
		intent.putExtra(W_TYPE, StatusStream.TYPE_FOR_TWITTER_TABLE );
		startActivity(intent);
		this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		StreamsApplication sApplication  = (StreamsApplication) this.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;
		String idStr = getDataAtColumn(StatusStream.C_ID) ;
		String[] parmsArray = new String[] {idStr, StatusStream.TYPE_FOR_TWITTER_TABLE} ;
		statusStream.setArticleWasRead( parmsArray[0], StatusStream.SOCIAL_STREAMS_TABLE) ;
		new SendServerArticleWasRead().execute(parmsArray) ;
		}catch(Exception e){e.printStackTrace();}
	}

	
	/**
	 * 
	 */
	void processSocialInstagramSelection()
	{ 
		try{
		// open in its own webview so that the user can add comments
		String webUrl = getDataAtColumn(StatusStream.S_URL) ;
		String user = getDataAtColumn(StatusStream.S_FROM_USER) ;
		String subject = //getDataAtColumn(StatusStream.S_CONTENT)  ;
				"Photo from Instagram user " + user ; 
		//subject = subjectTrim(subject) ; 
		 
		
		Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
		intent.putExtra(W_TITLE,user + " | Instagram");
		intent.putExtra(W_URL,webUrl );
		intent.putExtra(W_SOURCE,user );
		intent.putExtra(W_SUBJECT,subject );
		intent.putExtra(W_TYPE, StatusStream.TYPE_FOR_INSTAGRAM_TABLE );
		startActivity(intent);
		this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		StreamsApplication sApplication  = (StreamsApplication) this.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;
		String idStr = getDataAtColumn(StatusStream.C_ID) ;
		String[] parmsArray = new String[] {idStr, StatusStream.TYPE_FOR_INSTAGRAM_TABLE} ;
		statusStream.setArticleWasRead( parmsArray[0], StatusStream.SOCIAL_STREAMS_TABLE) ;
		new SendServerArticleWasRead().execute(parmsArray) ;
		}catch(Exception e){e.printStackTrace();}
	}
	
	String subjectTrim(String subject) 
	{
		try{
		if (subject.length() > 50)
		{
			int blankIndx = subject.indexOf(" ", 35) ;
			if (blankIndx == -1)
			{
				blankIndx = 40 ; 
			}
			subject = subject.substring(0, blankIndx) + " ..." ;
		}
		}catch(Exception e){e.printStackTrace();}
		return subject ; 
	}
	/**
	 * 
	 */
	void processPersonalEmailSelection()
	{
		try{
		String fromStr = "" ;
		String subjectStr = "" ;
		String contentStr = "" ; 

		fromStr = getDataAtColumn(StatusStream.E_FROM) ;
		subjectStr = getDataAtColumn(StatusStream.E_SUBJECT) ;
		contentStr = getDataAtColumn(StatusStream.E_CONTENT) ;


		Intent intent = new Intent(this.getActivity(), EmailViewActivity.class);
		intent.putExtra(E_FROM,fromStr) ;
		intent.putExtra(E_SUBJECT,subjectStr) ;
		intent.putExtra(E_CONTENT,contentStr );
		startActivity(intent);

		this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		StreamsApplication sApplication  = (StreamsApplication) this.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;


		String[] parmsArray = new String[] {getDataAtColumn(StatusStream.C_ID),"email"} ;
		statusStream.setArticleWasRead( parmsArray[0], StatusStream.PERSONAL_STREAMS_TABLE) ;
		new SendServerArticleWasRead().execute(parmsArray) ;
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param columnId - get the data at this column
	 * @return the data as String at the column
	 */
	String getDataAtColumn(String columnId)
	{

		try{
		int column = msStreamsCursor.getColumnIndex(columnId) ;
		if (column != -1)
		{

			int col = msStreamsCursor.getColumnIndex(columnId) ;
			CharArrayBuffer buffURL = new CharArrayBuffer(msStreamsCursor.getString(col).length() + 1) ;
			msStreamsCursor.copyStringToBuffer(col, buffURL) ;
			return convertCABToString(buffURL) ;
		}
		}catch(Exception e){e.printStackTrace();}
		return "" ; 

	}

	/**
	 * 
	 */
	public  void   cleanUpGlobals()
	{ 
		try{
		mStreamsFragment = null; 

		mStreamsUpdateReceiver = null;
		mIntentFilter   = null ; 
		mServerArchiveProgressDialog = null ; 
		//mServerDataProgressDialog = null ; 
		cleanUpAdapter() ;
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * clear the adapter . for example called on logout, ondestroy/cleanupGlobals, ect.
	 */
	public static void   cleanUpAdapter()
	{
		try{
		msStreamAdapter = null ; 
		if (msStreamsCursor != null)
		{
			msStreamsCursor.close() ;
			msStreamsCursor = null;
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Class StreamsUpdateReceiver
	 * receives data alerts from UodaterReceiver indicating there is new data to process in the 
	 *   dB or its time to recalc then times on the streams list items 
	 *
	 */
	class StreamsUpdateReceiver extends BroadcastReceiver {  
		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {  



			try{
			mNetworkStatus = intent.getStringExtra(UpdaterService.STREAMS_UPDATED_INTENT_NETWORK_STATUS)  ;

			if (mNetworkStatus != null)
			{

				if (mNetworkStatus.compareTo(UpdaterService.STREAMS_UPDATED_INTENT_NETWORK_AVAILABLE) == 0 )
				{
					mNetworkStatusUnavail = 0 ; 
				}
				else
				{
					if (mNetworkStatus.compareTo(UpdaterService.STREAMS_UPDATED_INTENT_NETWORK_UNAVAILABLE) == 0 )
					{
						mNetworkStatusUnavail++ ; 
					}
				}
			}
			//int numChanged = intent.getIntExtra(UpdaterService.STREAMS_UPDATED_INTENT_EXTRA_COUNT, -1)  ; 
			int numArchived = intent.getIntExtra(UpdaterService.STREAMS_UPDATED_INTENT_ARCHIVE_RESULT, -1)  ;
			int numPersonalChanged = intent.getIntExtra(UpdaterService.STREAMS_UPDATED_INTENT_PERSONAL_COUNT, -1)  ;
			int numSocialChanged = intent.getIntExtra(UpdaterService.STREAMS_UPDATED_INTENT_SOCIAL_COUNT, -1)  ;
			int numPublicChanged = intent.getIntExtra(UpdaterService.STREAMS_UPDATED_INTENT_PUBLIC_COUNT, -1)  ;
			
			if (numPersonalChanged != -1)
			{
				processNumPersonalChanged(numPersonalChanged) ;
			}
			if (numSocialChanged != -1)
			{
				processNumSocialChanged(numSocialChanged) ;
			}
			if (numPublicChanged != -1)
			{
				processNumPublicChanged(numPublicChanged) ;
			}
			if (numArchived != -1)
			{
				processNumArchived(numArchived) ;

			}
			}catch(Exception e){e.printStackTrace();}
		}
	}
	/**
	 * @param numArchived - forwarded to processServerArchiveResponse()
	 */
	void processNumArchived(int numArchived)
	{
		try{
		if (mServerArchiveProgressDialog != null)
		{
			mServerArchiveProgressDialog.dismiss() ; 

			mServerArchiveProgressDialog = null ; 

		}
		processServerArchiveResponse(numArchived) ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "StreamsUpdateReceiver:onReceived:processNumArchived");
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param numChanged - number of personal records changed in DB
	 *                       even if 0, still run notifyDataSetChanged
	 *                       so that times are updated
	 */
	public void processNumPersonalChanged(int numChanged)
	{
		try{
		StreamsApplication sApplication  = (StreamsApplication) mStreamsFragment.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;
		if ( (msStreamAdapter != null)  && (MainActivity.getStreamsType() == StreamsType.STREAM_TYPE_PERSONAL) )
		{

			if (numChanged > 0 || msFirstRecieve)
			{
				msStreamsCursor = statusStream.getPersonalStatusUpdates() ; 
				msStreamAdapter.changeCursor(msStreamsCursor) ;
				msFirstRecieve = false ; 
			}

			msStreamAdapter.notifyDataSetChanged(); 
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param numChanged - number of personal records changed in DB
	 *                       even if 0, still run notifyDataSetChanged
	 *                       so that times are updated
	 */
	public void processNumPublicChanged(int numChanged)
	{
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "STREAMSFragment::processNumPublicChanged numchanged=" + numChanged) ;
		 
		 
		}
		StreamsApplication sApplication  = (StreamsApplication) mStreamsFragment.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;
		if ( (msStreamAdapter != null)  && (MainActivity.getStreamsType() == StreamsType.STREAM_TYPE_PUBLIC) )
		{

			if (numChanged > 0 || msFirstRecieve)
			{
				msStreamsCursor = statusStream.getPublicStatusUpdates() ; 
				msStreamAdapter.changeCursor(msStreamsCursor) ;
				msFirstRecieve = false ; 
			}

			msStreamAdapter.notifyDataSetChanged(); 
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param numChanged - number of social records changed in DB
	 *                       even if 0, still run notifyDataSetChanged
	 *                       so that times are updated
	 */
	public void processNumSocialChanged(int numChanged)
	{
		try{
		if (mStreamsFragment == null)
		{
			return ; 
		}
		StreamsApplication sApplication  = (StreamsApplication) mStreamsFragment.getActivity().getApplication() ;
		StatusStream statusStream = sApplication.getStatusStream() ;
		if ( (msStreamAdapter != null)  && (MainActivity.getStreamsType() == StreamsType.STREAM_TYPE_SOCIAL) )
		{

			if (numChanged > 0 || msFirstRecieve)
			{
				msStreamsCursor = statusStream.getSocialStatusUpdates() ; 
				msStreamAdapter.changeCursor(msStreamsCursor) ;
				msFirstRecieve = false ; 
			}

			msStreamAdapter.notifyDataSetChanged(); 


		}

		}catch(Exception e){e.printStackTrace();}

	}
	
	

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.drawer.EnhancedStreamsListFragment#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		return super.onTouch(  v,   ev) ;
	}
	 
	/**
	 * @param streamType
	 */
	public void listViewToPosition(StreamsType streamType)
	{
		int selection = 0 ; 
		switch(streamType) 
		{
		case  STREAM_TYPE_PERSONAL :
			selection = mPositionPersonal ;
			break ; 
		case  STREAM_TYPE_SOCIAL :
			selection = mPositionSocial ;
			break ;	
		case  STREAM_TYPE_PUBLIC :
			selection = mPositionPublic ;
			break ;	
		}
		try{
		if (mListView != null)
		{
		 
		mListView.setSelection(selection);
		}
		}catch(Exception e){e.printStackTrace();}
	}
	 
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamFragment::onSaveInstanceState") ;
		}
		try{
		state.putSerializable(MainActivity.STREAMS_STATE, MainActivity.getStreamsState().ordinal());
		state.putSerializable(MainActivity.STREAMS_TYPE, MainActivity.getStreamsType().ordinal());
		}catch(Exception e){e.printStackTrace();}
	}
	
}



