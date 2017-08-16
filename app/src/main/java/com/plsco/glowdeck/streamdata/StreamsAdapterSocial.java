package com.plsco.glowdeck.streamdata;

import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.plsco.glowdeck.task.VollyTask;
/**
*
* @author Joe Diamand 
* @version 1.0   08/27/14
* 
* Project: Streams Android Implementation
* 
* file: StreamsAdapterSocial.java
* 
*   (c) Copyright 2014. PLSCO, Inc. All rights reserved.
* 
*/
/**
* History
* Prepare for Google Play Store 11/1/14
*/

/**
* StreamsAdapterSocial  extends StreamsAdapterViews
*                            
*
*/
public class StreamsAdapterSocial  extends StreamsAdapterViews{
	
	String mType ; 
	StreamsAdapterSocial(String type)
	{
		mType = type ; 
	}
	/* (non-Javadoc)
	 * @see com.glowdeck.streams.streamdata.StreamsAdapterViews#bind(com.glowdeck.streams.streamdata.StreamAdapter)
	 */
	boolean bind(StreamAdapter streamAdapter)
	{

		boolean retVal = true ; 
		try{
		String readIcon = null ; 
		String notReadIcon = null ; 
		String mini = null ;
		if (mType.compareToIgnoreCase(StatusStream.SOCIAL_INSTAGRAM_TYPE)==0)
		{
			readIcon = "instagram_dot1" ;
			notReadIcon = "instagram_dot0" ;
			mini = "instagram_mini" ;
		}
		else
		{
			if (mType.compareToIgnoreCase(StatusStream.SOCIAL_TWITTER_TYPE)==0)
			{
				readIcon = "twitter_dot1" ;
				notReadIcon = "twitter_dot0" ;
				mini = "twitter_mini" ;
			}
		}
		switch (bindViews())
		{
		case 0 :
			mArticleWasReadIcon = "drawable/" + readIcon ;
			break ;
		case 1 :
			mArticleWasReadIcon = "drawable/" + notReadIcon ;
			break ;

		case 2:
		default:
			retVal =false ;
			break;

		}

		if (retVal)
		{
			mTypeIcon.setImageResource(
					mContext.getResources().
					getIdentifier("drawable/" + mini, null, mContext.getPackageName()));

			getTheIcon() ;
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ;


	}

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.streamdata.StreamsAdapterViews#bindViews()
	 */
	int bindViews()
	{
		try{
		if (mType.compareToIgnoreCase(StatusStream.SOCIAL_INSTAGRAM_TYPE)==0)
		{

			String source = mCursor.getString(mCursor.getColumnIndex(StatusStream.S_FROM_USER)) ;
			ImageLoader mImageLoader = VollyTask.getInstance(this.mContext).getImageLoader();




			mNetworkImage.setImageUrl(mCursor.getString(mCursor.getColumnIndex(StatusStream.S_SCREEN_JPG)), mImageLoader);

			mNetworkImage.setVisibility(View.VISIBLE) ;


			mStreamsItemTopLine.setText(source + " | Instagram" ) ;

			String instagramStr = mCursor.getString(mCursor.getColumnIndex(StatusStream.S_CONTENT)) ;
			mStreamsItemBottomLine.setText(instagramStr) ;






		}
		else
		{
			if (mType.compareToIgnoreCase(StatusStream.SOCIAL_TWITTER_TYPE)==0)
			{

				String source = mCursor.getString(mCursor.getColumnIndex(StatusStream.S_FROM_USER)) ;

				String screenName = mCursor.getString(mCursor.getColumnIndex(StatusStream.S_SCREEN_JPG)) ;

				mStreamsItemTopLine.setText(source + " | @" + screenName) ;

				String tweetStr = mCursor.getString(mCursor.getColumnIndex(StatusStream.S_CONTENT)) ;
				mStreamsItemBottomLine.setText(tweetStr) ;
				
				
				
			}
		}
		
		/*
		
		String longEmail = "" ; 
		String title = "" ;
		String subTitle = "" ; 

		int colSubject = mCursor.getColumnIndex(StatusStream.E_SUBJECT) ; 
		String subject = "" ;
		if (colSubject != -1)
		{
			subject = mCursor.getString(colSubject) ;
		}


		int colFrom = mCursor.getColumnIndex(StatusStream.E_FROM) ;  
		String from = "" ;
		if (colFrom != -1)
		{
			from = mCursor.getString(colFrom) ;
			int indx = from.indexOf('<') ;
			if (indx > 0)
			{

				longEmail = from.substring(indx) ; 
				from = from.substring(0, indx -1) ;
				title = from + " | " + subject ; 
				subTitle = longEmail ; 
			}
			else
			{
				title = from ; 
				subTitle = subject ; 
			}
		}
		 
		mStreamsItemTopLine.setText(title) ;


		mStreamsItemBottomLine.setText(subTitle) ;
        */

		}catch(Exception e){e.printStackTrace();}
		return super.bindViews(); 





	}

}
