package com.plsco.glowdeck.streamdata;



/**
*
* @author Joe Diamand 
* @version 1.0   08/27/14
* 
* Project: Streams Android Implementation
* 
* file: StreamsAdapterPublic.java
* 
*   (c) Copyright 2014. PLSCO, Inc. All rights reserved.
* 
*/
/**
* History
* Prepare for Google Play Store 11/1/14
*/

/**
* StreamsAdapterPublic  extends StreamsAdapterViews
*                            
*
*/
public class StreamsAdapterPublic  extends StreamsAdapterViews {

	String mType ;
	StreamsAdapterPublic(String type)
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
		boolean weatherType = false ;
		String readIcon = null ;
		String notReadIcon = null ;
		String mini = null ;
		if (mType.compareToIgnoreCase(StatusStream.PUBLIC_NEWS_TYPE)==0)
		{
			readIcon = "news_dot1" ;
			notReadIcon = "news_dot0" ;
			mini = "news_mini" ;
		}
		else
		{
			if (mType.compareToIgnoreCase(StatusStream.PUBLIC_WEATHER_TYPE)==0)
			{
				//readIcon = "news_dot1" ;
				//notReadIcon = "news_dot0" ;
				weatherType = true ;
				mini = "weather_mini" ;
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
        if (weatherType)
        {
        	mArticleWasReadIcon = "" ;
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
		if (mType.compareToIgnoreCase(StatusStream.PUBLIC_NEWS_TYPE)==0)
		{

			String source = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_CITY_SOURCE)) ;
			String keyword = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_KEYWORD_TEMPERATURE)) ;


			mStreamsItemTopLine.setText(source + " | " + keyword) ;

			String article = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_ARTICLE_UNITS)  ) ;
			mStreamsItemBottomLine.setText(article) ;

		}
		if (mType.compareToIgnoreCase(StatusStream.PUBLIC_WEATHER_TYPE)==0)
		{

			String city = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_CITY_SOURCE)) ;
			String temperature = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_KEYWORD_TEMPERATURE)) ;
			String units = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_ARTICLE_UNITS)) ;
			String conditions = mCursor.getString(mCursor.getColumnIndex(StatusStream.P_URL_CONDITIONS)) ;
			

			mStreamsItemTopLine.setText(city + " - Temp: " + temperature + (char) 0x00B0 +   units  ) ;
			String space = "    -    " ; 
			if ( conditions.length() > 10 )
			{
				 space = " - " ;
			}
			
			mStreamsItemBottomLine.setText("Conditions: "+ conditions +space + "Click for more info.") ;	
			
		 
			
			
			
			
		}
		
		

		}catch(Exception e){e.printStackTrace();}
		return super.bindViews(); 


	}

}
