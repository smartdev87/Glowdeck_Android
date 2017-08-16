package com.plsco.glowdeck.streamdata;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
//import android.widget.ImageView;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamAdapter.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 * 
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * StreamAdapter  extends CursorAdapter
 *                            
 *
 */
public class StreamAdapter extends CursorAdapter {

	//
	//    Globals
	//
	//private Cursor mCursor;
	//private Context mContext;
	private final LayoutInflater mInflater;

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return super.getView(position, convertView, parent);
		
	}





	/**
	 * @param mContext
	 * @param c
	 */
	public StreamAdapter(Context context,  Cursor c) {
		super(context,  c);
		mInflater=LayoutInflater.from(context);
		
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamAdapter:mCursor count= "  + c.getCount()) ;
		}

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		try{
		view.setTag(cursor) ; 



		int colType = cursor.getColumnIndex(StatusStream.C_TYPE) ;
		String type = cursor.getString(colType) ;


		if (type == null)
		{
			return ; 
		}
		//
		StreamsAdapterViews streamAdapterViews = StreamsAdapterViews.factory(type, view, context, cursor) ;
		if (streamAdapterViews == null)
		{
			return ; 
		}
		if  (!streamAdapterViews.bind(this))
		{

			view.setVisibility(View.GONE) ;

		}
		//

		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		return mInflater.inflate(R.layout.fragment_stream,parent,false); 

	}


	/**
	 * @param timeValue int time value to convert to string 
	 * @return
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
		numValStr = Long.toString(timeVal ) ;
		}catch(Exception e){e.printStackTrace();}
		return numValStr + units; // + plural ;
	}


}
