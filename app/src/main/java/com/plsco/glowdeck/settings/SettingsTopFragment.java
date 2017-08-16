package com.plsco.glowdeck.settings;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;

/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: SettingsTopFragment.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * 11/1/14 - prepare for Google Play store
 */
/**
 * The SettingsTopFragment extends ListFragment
 *  
 *  
 *
 */

/**
 * The SettingsTopFragment manages the mCurrentFragment  presents the main setting screen.
 * The user is presented with ListView of all the streams categories items
 *  
 *
 */
public class SettingsTopFragment extends ListFragment {
	// statics
	static boolean toggleInProgress = false ; 
	public final static String STREAM_TYPE_SETTING = "STREAM_TYPE_SETTING" ;
	public final static String NEWS_STREAM_TYPE = "News" ;
	public final static String EMAIL_STREAM_TYPE = "Email" ;
	public final static String INSTAGRAM_STREAM_TYPE = "Instagram" ;
	public final static String TWITTER_STREAM_TYPE = "Twitter" ;
	public final static String FACEBOOK_STREAM_TYPE = "Facebook" ;
	public final static String WEATHER_STREAM_TYPE = "Weather" ;

	// globals
	StreamsApplication mStreamsApplication = null;
	private String[] 											mStreamsSettingsTitles = null;
	private TypedArray 											mStreamsSettingsIconsOn = null ;
	private TypedArray 											mStreamsSettingsIconsOff = null ;
	StreamsUser mStreamsUser ;
	//
	//   Global statics
	//
	public static ArrayAdapter<String> settingsAdapter ;
	/**
	 * 
	 */
	public SettingsTopFragment(){

	}
	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		/*
		if (IndividualSettingsActivity.mIndividualSettingsLV != null)
		{
			IndividualSettingsActivity.mIndividualSettingsLV.invalidateViews() ;
		}
		*/
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try{
		mStreamsApplication = (StreamsApplication)this.getActivity().getApplication() ;
		mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
		if (mStreamsUser == null) 
		{
			Activity thisActivity = this.getActivity() ; 
			StreamsApplication streamsApplication = (StreamsApplication)thisActivity.getApplication() ;
			LoginActivity.setStreamsApplication(streamsApplication) ;
			LoginActivity.recoverSavedStreamsUser(thisActivity) ;
			mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
		}
		mStreamsSettingsTitles = getResources().getStringArray(R.array.streams_settings_items);
		mStreamsSettingsIconsOn = getResources()
				.obtainTypedArray(R.array.streams_settings_icons_on) ; 
		mStreamsSettingsIconsOff = getResources()
				.obtainTypedArray(R.array.streams_settings_icons_off) ; 

		settingsAdapter = new SettingsArrayAdapter(getActivity(),
				mStreamsSettingsTitles);
		setListAdapter(settingsAdapter);

		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.ListFragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {

		super.onDestroyView();

	}


	/* (non-Javadoc)
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		switch (position)
		{
		
		case 2: // email
		case 3: // Weather
		case 4: // news
		case 5: // twitter
		case 7: // instagram

			Intent intent = new Intent(this.getActivity(), IndividualSettingsActivity.class);
			switch (position)
			{
			case 2:
				intent.putExtra(STREAM_TYPE_SETTING,EMAIL_STREAM_TYPE );
				break;
			case 3:
				intent.putExtra(STREAM_TYPE_SETTING,WEATHER_STREAM_TYPE );
				break;
			case 4:
				intent.putExtra(STREAM_TYPE_SETTING,NEWS_STREAM_TYPE );
				break; 
			case 5:
				intent.putExtra(STREAM_TYPE_SETTING,TWITTER_STREAM_TYPE );
				break;
			case 7:
				intent.putExtra(STREAM_TYPE_SETTING,INSTAGRAM_STREAM_TYPE );
				break;

			}

			try{
			startActivity(intent);
			this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}catch(Exception e){e.printStackTrace();}
			break;

		default:
			try{
			int viewId =  v.getId() ;
			String tagL= (String) l.getTag() ;
			String tagV= (String) v.getTag(); 
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","onListItemClick:" + " position=" + position + ", id=" + viewId + ", tagL=" + tagL  + ", tagV=" + tagV) ;

			}
			}catch(Exception e){e.printStackTrace();}
			break ; 


		}


	}



	/**
	 * 
	 *
	 */
	public class SettingsArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;

		public SettingsArrayAdapter(Context context, String[] values) {
			super(context, R.layout.fragment_setting_top, values);
			this.context = context;
			this.values = values;
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = null ;
			try{
			mStreamsUser = VerifyCredentialsTask.getStreamsUser() ; // reload it case individual setting we made and notify's were set
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    rowView = inflater.inflate(R.layout.fragment_setting_top, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.settings_label);
			TextView textView2 = (TextView) rowView.findViewById(R.id.settings_sublabel);
			ImageView imageViewArrow = (ImageView) rowView.findViewById(R.id.icon_more);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			imageView.setImageResource(mStreamsSettingsIconsOn.getResourceId(position, -1));

			textView.setText(values[position]);


			final int positionF = position ; 
			imageView.setOnClickListener(new OnClickListener() {  
				/* (non-Javadoc)
				 * @see android.view.View.OnClickListener#onClick(android.view.View)
				 * 
				 * the click handler will toggle the com.glowdeck.streams.settings items icons on or off
				 */
				@Override
				public void  onClick(View v) {
					synchronized (this) {
						if (!toggleInProgress)
						{
							toggleInProgress = true ; 
							setTheImage(  positionF, (ImageView)v, true) ;
							toggleInProgress = false ; 
						}
					}


				}
			});

			imageView.setOnTouchListener(new View.OnTouchListener() {

				/* (non-Javadoc)
				 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
				 * onTouch is used with onClick to effect the look and feel of the user
				 *    clicking on the right most icon in the com.glowdeck.streams.settings listview
				 */
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						v.setAlpha((float)0.5) ;
						break;

					case MotionEvent.ACTION_UP:

						v.setAlpha((float)1.0) ;


						break;

					case MotionEvent.ACTION_CANCEL  :
						v.setAlpha((float)1.0) ;
						break ;
					}
					return false;
				}
			}) ;

			setTheImage(position,imageView, false) ;
			switch (position)
			{
			case 0 : 
				String firstNameCallsMsg = mStreamsUser.getStreamsAccount().getFirstName() ;
				if (firstNameCallsMsg.length() == 0 )
				{
					firstNameCallsMsg = "Person" ;
				}
				String callsSubText = firstNameCallsMsg + "\'" + "s Phone" ;

				textView2.setText(callsSubText) ;
				textView.setAlpha((float)0.5) ;
				textView2.setAlpha((float)0.5) ;
				imageViewArrow.setAlpha((float)0.25) ;
				imageView.setEnabled(false) ;
				imageView.setAlpha((float)0.33) ;
				break ;
			case 1:
				String firstNameSubMsg = mStreamsUser.getStreamsAccount().getFirstName() ;
				if (firstNameSubMsg.length() == 0 )
				{
					firstNameSubMsg = "Person" ;
				}
				String phoneSubText = firstNameSubMsg + "\'" + "s Phone" ;
				textView2.setText(phoneSubText) ;
				textView.setAlpha((float)0.5) ;
				textView2.setAlpha((float)0.5) ;
				imageViewArrow.setAlpha((float)0.25) ;
				imageView.setEnabled(false) ;
				imageView.setAlpha((float)0.33) ;
				break ;
			case 2: // emails 
				String[] emails = mStreamsUser.getEmail().getAccount() ;
				// test //String[] emails = 
				//{"Account1@mail.com","Account2@mail.com","Account3@mail.com", "Account4@mail.com"} ;
				String emailSubMsg = "";
				if (emails.length == 0 )
				{
					emailSubMsg = "No Account" ;
				}
				else
				{
					emailSubMsg = emails[0] ; 
					for (int i=1 ; i < emails.length ; i++)
					{
						emailSubMsg += "," + " " + emails[i] ;
					}
				}
				textView2.setText(emailSubMsg) ;

				break;
			case 3: // weather

				String weatherCity = "No location provided" ;
				if (!mStreamsUser.getWeather().getLocation().getCity().isEmpty())
				{
					weatherCity = mStreamsUser.getWeather().getLocation().getCity() ;
				}
				textView2.setText(weatherCity) ; 
				//imageViewArrow.setAlpha((float)0.25) ;
				imageViewArrow.setVisibility(View.VISIBLE);
				break ; 

			case 4: // news

				String newsKeywordMsgPre = "No" ;
				String newsKeywordMsgPlu = "s";
				String[] newsKeywords = mStreamsUser.getNews().getKeywords() ; 
				switch (newsKeywords.length)
				{
				case 0:
					break ; 
				case 1:
					newsKeywordMsgPre = "One" ; 
					newsKeywordMsgPlu = "" ; 
					break ;
				case 2:
					newsKeywordMsgPre = "Two" ;
					break ;
				case 3:
					newsKeywordMsgPre = "Three" ;
					break ;
				case 4:
					newsKeywordMsgPre = "Four" ;
					break ;
				case 5:
					newsKeywordMsgPre = "Five" ;
					break ;
				case 6:
					newsKeywordMsgPre = "Six" ;
					break ;
				case 7:
					newsKeywordMsgPre = "Seven" ;
					break ;
				case 8:
					newsKeywordMsgPre = "Eight" ;
					break ;
				case 9:
					newsKeywordMsgPre = "Nine" ;
					break ;
				case 10:
					newsKeywordMsgPre = "Ten" ;
					break ;
				default:
					newsKeywordMsgPre = "More Than Ten" ;
					break ;
				}
				String newsKeywordsMsg = newsKeywordMsgPre + " Keyword" + newsKeywordMsgPlu ; 
				textView2.setText(newsKeywordsMsg) ; 
				break ; 

			case 5: // twitter
				String[] twitterAccts = mStreamsUser.getTwitter().getAccount()  ;

				String twitterSubMsg = "";
				if (twitterAccts.length == 0 )
				{
					twitterSubMsg = "No Account" ;
				}
				else
				{
					twitterSubMsg = twitterAccts[0] ; 
					for (int i=1 ; i < twitterAccts.length ; i++)
					{
						twitterSubMsg += "," + " " + twitterAccts[i] ;
					}
				}
				textView2.setText(twitterSubMsg) ;
				break;


			case 6 :  // facebook

				String[] facebookAccts = mStreamsUser.getFacebook().getAccount()  ;

				//imageViewArrow.setVisibility(View.INVISIBLE) ; alpha
				imageViewArrow.setAlpha((float)0.25) ;
				String facebookSubMsg = "";
				if (facebookAccts.length == 0 )
				{
					facebookSubMsg = "No Account" ;
				}
				else
				{
					facebookSubMsg = facebookAccts[0] ; 
					for (int i=1 ; i < facebookAccts.length ; i++)
					{
						facebookSubMsg += "," + " " + facebookAccts[i] ;
					}
				}
				textView2.setText(facebookSubMsg) ;
				textView.setAlpha((float)0.5) ;
				textView2.setAlpha((float)0.5) ;
				imageView.setEnabled(false) ;
				imageView.setAlpha((float)0.33) ;
				break;


			case 7 : // instagram
				String[] instagramAccts = mStreamsUser.getInstagram().getAccount()  ;

				String instagramSubMsg = "";
				if (instagramAccts.length == 0 )
				{
					instagramSubMsg = "No Account" ;
				}
				else
				{
					instagramSubMsg = instagramAccts[0] ; 
					for (int i=1 ; i < instagramAccts.length ; i++)
					{
						instagramSubMsg += "," + " " + instagramAccts[i] ;
					}
				}
				textView2.setText(instagramSubMsg) ;
				break;
			case 8 : // add new streams
				textView2.setText("") ;
				ImageView imageView2 = (ImageView) rowView.findViewById(R.id.icon_more);
				imageView2.setVisibility(View.INVISIBLE) ;
				imageView2.setEnabled(false) ;
				imageView.setEnabled(false) ;
				imageView.setAlpha((float)0.33) ;
				textView.setAlpha((float)0.5) ;
				textView2.setAlpha((float)0.5) ;
			}
			}catch(Exception e){e.printStackTrace();}
			return rowView;
		}

	}

	/**
	 * @param position  - which icon for which setting
	 * @param imageView  - the imageview at that position for that row
	 * @param toggle     - is this a click (changes the icon) 
	 *                          or is it just a display (the initial rendering) 
	 */
	void setTheImage(int position,ImageView imageView, boolean toggle) 
	{

		try{
		if (mStreamsUser == null) 
		{

			return ; 
		}

		boolean onOrOff = false ;
		int resId = 0 ;
		switch(position)
		{
		case 0 : // calls

			boolean unSupportedCall = true ;
			if (unSupportedCall)
			{
				mStreamsUser.getCalls().setEnabled("0") ;
			}
			else
			{

				onOrOff = (mStreamsUser.getCalls().getEnabled().compareTo("1")==0) ? true : false ; 
				if (toggle)
				{
					onOrOff = !onOrOff ; 
					mStreamsUser.getCalls().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				}
			}
			break ; 

		case 1: // messages
			boolean unSupportedMsg = true ;
			if (unSupportedMsg)
			{
				mStreamsUser.getMessages().setEnabled("0") ;
			}
			else
			{

				onOrOff = (mStreamsUser.getMessages().getEnabled().compareTo("1")==0) ? true : false ; 
				if (toggle)
				{
					onOrOff = !onOrOff ;
					mStreamsUser.getMessages().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				}
			}
			break ; 

		case 2 : //email 

			onOrOff = (mStreamsUser.getEmail().getEnabled().compareTo("1")==0) ? true : false ; 
			if (toggle)
			{
				onOrOff = !onOrOff ;
				mStreamsUser.getEmail().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				mStreamsUser.getModifiedSettings().setEmailSettingsModified(true) ;
			}

			break ;

		case 3 : //weather 

			onOrOff = (mStreamsUser.getWeather().getEnabled().compareTo("1")==0) ? true : false ; 
			if (toggle)
			{
				onOrOff = !onOrOff ;
				mStreamsUser.getWeather().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				mStreamsUser.getModifiedSettings().setWeatherSettingsModified(true) ; 
			}

			break ; 

		case 4 : //News

			onOrOff = (mStreamsUser.getNews().getEnabled().compareTo("1")==0) ? true : false ;  
			if (toggle)
			{
				onOrOff = !onOrOff ;
				mStreamsUser.getNews().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				mStreamsUser.getModifiedSettings().setNewsSettingsModified(true) ;
			}

			break ; 

		case 5 : //Twitter 

			onOrOff = (mStreamsUser.getTwitter().getEnabled().compareTo("1")==0) ? true : false ; 
			if (toggle)
			{
				onOrOff = !onOrOff ;
				mStreamsUser.getTwitter().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				mStreamsUser.getModifiedSettings().setTwitterSettingsModified(true) ;
			}

			break ; 

		case 6 : //Facebook 

			boolean unSupportedFB = true ;
			if (unSupportedFB)
			{
				mStreamsUser.getCalls().setEnabled("0") ;
			}
			else
			{
				onOrOff = (mStreamsUser.getFacebook().getEnabled().compareTo("1")==0) ? true : false ; 
				if (toggle)
				{
					onOrOff = !onOrOff ;
					mStreamsUser.getFacebook().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				}
			}
			break ; 

		case 7 : //Instagram

			onOrOff = (mStreamsUser.getInstagram().getEnabled().compareTo("1")==0) ? true : false ; 
			if (toggle)
			{
				onOrOff = !onOrOff ;
				mStreamsUser.getInstagram().setEnabled(   (onOrOff) ? "1" : "0" ) ;
				String[] iAccount = new String[]{"digiotajoe"} ;
				mStreamsUser.getInstagram().setAccount(iAccount) ;
				mStreamsUser.getModifiedSettings().setInstagramSettingsModified(true) ;
			}


			break ; 
		}
		resId = onOrOff ? mStreamsSettingsIconsOn.getResourceId(position, -1) :
			mStreamsSettingsIconsOff.getResourceId(position, -1) ;
		imageView.setImageResource(resId) ; 
		}catch(Exception e){e.printStackTrace();}
	}


}
