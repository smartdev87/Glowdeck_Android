package com.plsco.glowdeck.streamdata;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamsAdapterPersonal.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 * 
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * StreamsAdapterPersonal  extends StreamsAdapterViews
 *                            
 *
 */
public class StreamsAdapterPersonal extends StreamsAdapterViews {

	
	String mType ; 
	StreamsAdapterPersonal(String type)
	{
		mType = type ; 
	}
	/* (non-Javadoc)
	 * @see com.glowdeck.streams.streamdata.StreamsAdapterViews#bind(com.glowdeck.streams.streamdata.StreamAdapter)
	 */
	boolean bind(StreamAdapter streamAdapter)
	{

		boolean retVal = true ; 
		switch (bindViews())
		{
		case 0 :
			mArticleWasReadIcon = "drawable/email_dot1" ;
			break ; 
		case 1 :
			mArticleWasReadIcon = "drawable/email_dot0" ;
			break ;

		case 2:
		default:
			retVal =false ; 
			break;

		}

		if (retVal)
		{
			try{
			mTypeIcon.setImageResource(
					mContext.getResources().
					getIdentifier("drawable/email_mini", null, mContext.getPackageName()));

			getTheIcon() ;
			}catch(Exception e){e.printStackTrace();}
		}
		return retVal ;


	}

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.streamdata.StreamsAdapterViews#bindViews()
	 */
	int bindViews()
	{

		try{
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


		}catch(Exception e){e.printStackTrace();}
		return super.bindViews(); 





	}

}
