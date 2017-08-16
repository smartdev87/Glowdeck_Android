package com.plsco.glowdeck.streamdata;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.plsco.glowdeck.R;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamsAdapterViews.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 * 
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * StreamsAdapterViews  extends StreamsAdapterViews
 *                            
 *
 */
public class StreamsAdapterViews {
	//  Globals
	//
	protected View 							mView ; 
	protected Context 						mContext ;
	protected Cursor 						mCursor ; 

	protected  boolean 						mArticleWasRead  ; 
	protected  String 						mArticleWasReadIcon  ; 
	protected ImageView 					mTypeIcon ;

	protected TextView 						mStreamsItemBottomLine ;
	protected TextView 						mStreamsItemTopLine ;
	protected TextView 						mStreamsItemElapsedTime ;
	protected ImageView 					mReadIcon ;
	protected NetworkImageView  			mNetworkImage ;
	protected String                        mType ; 

	/**
	 * 
	 */
	StreamsAdapterViews()
	{

	}

	/**
	 * @param streamAdapter  - adapter for the listitems
	 * @return true
	 */
	boolean bind(StreamAdapter streamAdapter)
	{
		mArticleWasRead = false ;
		mArticleWasReadIcon = null ;
		return true ;
	}


	/**
	 *
	 */
	void getTheIcon()
	{
		try{
		ImageView readIcon = (ImageView) mView.findViewById(R.id.fs_icon_2);
		if (mArticleWasReadIcon.length() > 0)
		{

		readIcon.setImageResource(
				mContext.getResources().
				getIdentifier(mArticleWasReadIcon, null, mContext.getPackageName()));
		readIcon.setVisibility(View.VISIBLE) ;
		}
		else
		{
			readIcon.setVisibility(View.INVISIBLE) ;
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param type   type of object to create
	 * @param view   view id of the object
	 * @param context  the context for the view
	 * @param cursor   cursor for the listitem
	 * @return
	 */
	static StreamsAdapterViews factory(String type, View view, Context context, Cursor cursor)
	{


		StreamsAdapterViews streamsAdapterViews = null ;
		try{
		if (type.compareToIgnoreCase(StatusStream.PERSONAL_EMAIL_TYPE) == 0 )
		{
			streamsAdapterViews = new StreamsAdapterPersonal(type) ;

		}
		else
		{
			if ((type.compareToIgnoreCase(StatusStream.SOCIAL_TWITTER_TYPE) == 0 ) || (type.compareToIgnoreCase(StatusStream.SOCIAL_INSTAGRAM_TYPE) == 0 ) )
			{
				streamsAdapterViews = new StreamsAdapterSocial(type) ;

			}
			else
			{
				if ((type.compareToIgnoreCase(StatusStream.PUBLIC_NEWS_TYPE) == 0 ) || (type.compareToIgnoreCase(StatusStream.PUBLIC_WEATHER_TYPE) == 0 ) )
				{
					streamsAdapterViews = new StreamsAdapterPublic(type) ;

				}
			}
		}

		if (streamsAdapterViews != null)
		{
			streamsAdapterViews.mView = view ;
			streamsAdapterViews.mContext = context ;
			streamsAdapterViews.mCursor = cursor ;

			streamsAdapterViews.mTypeIcon = (ImageView) view.findViewById(R.id.fs_icon );

			streamsAdapterViews.mStreamsItemBottomLine=(TextView)view.findViewById(R.id.fs_settings_sublabel);
			streamsAdapterViews.mStreamsItemTopLine=(TextView)view.findViewById(R.id.fs_settings_label);
			streamsAdapterViews.mStreamsItemElapsedTime = (TextView)view.findViewById(R.id.frag_stream_time);
			streamsAdapterViews.mReadIcon = (ImageView) view.findViewById(R.id.fs_icon_2);
			streamsAdapterViews.mNetworkImage = (NetworkImageView) view.findViewById(R.id.fs_network_image);
			streamsAdapterViews.mNetworkImage.setVisibility(View.GONE) ;
		}
		}catch(Exception e){e.printStackTrace();}
		return streamsAdapterViews ;
	}


	/**
	 * @return index of the read value on the cursor
	 */
	int bindViews()
	{

		try{
		String type = mCursor.getString(mCursor.getColumnIndex(StatusStream.C_TYPE)) ;
		if (type.compareTo(StatusStream.PUBLIC_WEATHER_TYPE) == 0)
		{
			mStreamsItemElapsedTime.setText("") ;
		}
		else
		{
			int colTime = mCursor.getColumnIndex(StatusStream.C_TIMESTAMP) ;

			mStreamsItemElapsedTime.setText(convertToTimeDiff(mCursor.getInt(colTime))) ; 
		}
		}catch(Exception e){e.printStackTrace();}
		return (mCursor.getInt(mCursor.getColumnIndex(StatusStream.C_READ))) ;

	}



	/**
	 * @param timeValue  - int val of time
	 * @return  string equivalent
	 */
	String convertToTimeDiff(int timeValue)
	{
		// timeValue is in secs
		String numValStr = null ;
		String units = null  ; 
		try{
		Time now = new Time();
		now.setToNow();
		long theCurrentTime = now.toMillis(true) ;
		theCurrentTime /= 1000 ; 
		long  timeVal  = (theCurrentTime - timeValue)/60 ;   

		if (timeVal<60)
		{

			units = "m" ;
		}
		else
		{
			timeVal /= 60 ;
			if (timeVal < 24)
			{

				units = "h" ;
			}
			else
			{
				timeVal /= 24 ;
				units = "d" ; 
			}

		}
		if (timeVal == 0)
		{
			numValStr = "Now" ;
			units = "" ;

		}
		else
		{
			numValStr = Long.toString(timeVal ) ;
		}
		}catch(Exception e){e.printStackTrace();}
		return numValStr + units; 
	}

}
