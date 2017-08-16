package com.plsco.glowdeck.drawer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plsco.glowdeck.bluetooth.BluetoothSppManager;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;

import java.util.ArrayList;
//import com.glowdeck.streams.bluetooth.BluetoothSppManager.GlowdeckDevice;
/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * FileName:  StreamsDrawerListAdapter.java
 * 
 *  (c) Copyright 2014. PLSCO, Inc. All rights reserved.
 *  
 */

/**
 * History
 * Prepare for Google Play Store 11/1/14
 */


/**
 * StreamsDrawerListAdapter extends BaseAdapter  
 * 
 *
 */
public class StreamsDrawerListAdapter extends  BaseAdapter 
{
	//
	//  Globals 
	private Context mContext;
	private ArrayList<StreamsDrawerItem> mStreamsDrawerItems;
	private final static int COLOR_OFF_WHITE = 0xFFc0c0c0 ; 
	private final static int COLOR_WHITE = 0xFFffffff ; 
	//

	/**
	 * @param mContext - constructor populates global
	 * @param streamsDrawerItems - constructor populates global
	 */
	public StreamsDrawerListAdapter(Context context, ArrayList<StreamsDrawerItem> streamsDrawerItems){
		this.mContext = context;
		this.mStreamsDrawerItems = streamsDrawerItems;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mStreamsDrawerItems.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {		
		return mStreamsDrawerItems.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		try{
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		ProgressBar progressBar1 = (ProgressBar) convertView.findViewById(R.id.progressBar1);


		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		ImageView arrowIcon = (ImageView) convertView.findViewById(R.id.expandable_arrow) ; 


		imgIcon.setImageResource(mStreamsDrawerItems.get(position).getIcon());        
		txtTitle.setText(mStreamsDrawerItems.get(position).getTitle());
		ListView listView = (ListView) parent ;
		View lloView = convertView.findViewById(R.id.llo_menu);

		listView.setDivider(null);
		listView.setDividerHeight(0);
		arrowIcon.setVisibility(View.GONE) ;
		if (position == 1)
		{
			String theArrowIconStr = "drawable/expand_arrow_down" ;
			if (!MainActivity.msDevicesClicked)
			{
				theArrowIconStr = "drawable/expand_arrow_up" ;

			}
			arrowIcon.setImageResource(mContext.getResources().
					getIdentifier(theArrowIconStr, null, mContext.getPackageName()));
			arrowIcon.setVisibility(View.VISIBLE) ;
		}
		if ( (position == MainActivity.DRAWER_DEVICES_GETMORE_FRAGMENT) || (position == MainActivity.DRAWER_GLOWDECK_DEVICES0) )
		{


			lloView.setPadding(50, 0, 0, 0) ;

			if (!MainActivity.msMenusInitialized)
			{

				imgIcon.setVisibility(View.GONE) ;
				txtTitle.setVisibility(View.GONE) ;
				convertView.setVisibility(View.GONE) ;


			}
			else
			{
				if ((MainActivity.msDevicesClicked))
				{

					imgIcon.setVisibility(View.VISIBLE) ;
					txtTitle.setVisibility(View.VISIBLE) ;
					convertView.setVisibility(View.VISIBLE) ;
					int textColor = mContext.getResources().getColor(R.color.list_item_title) ;
					if (position == MainActivity.DRAWER_GLOWDECK_DEVICES0)
					{
						if ( !BluetoothSppManager.isBTavailable() )

						{
							imgIcon.setVisibility(View.GONE) ;
							txtTitle.setVisibility(View.GONE) ;
							convertView.setVisibility(View.GONE) ;

						}
						else
						{
							if ((!BluetoothSppManager.isBTavailable()) || (!BluetoothSppManager.isBTturnedOn()))
							{
								
								txtTitle.setText(R.string.not_connected) ;
								imgIcon.setImageResource(R.drawable.btunavail) ;
								txtTitle.setTextColor( textColor) ;
								imgIcon.setAlpha(1.0f) ;
								GlowdeckDevice glowdeckDevice = MainActivity.getMainActivity().getCurrentGlowdecks().getDeviceAtPosition
										(position- MainActivity.DRAWER_GLOWDECK_DEVICES0)  ;
								if (glowdeckDevice != null)
								{   // if glowdeck was prev connected make sure it knows that it is no longer
									glowdeckDevice.setConnected(false) ;
									glowdeckDevice.setConnecting(false) ;
									glowdeckDevice.setMsgInprogress(false) ;
									glowdeckDevice.setReceivedInit(false) ;
								}
							}
							else
							{
								StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication() ;
								boolean validFound = false ;
								int elements =  MainActivity.getMainActivity().getCurrentGlowdecks().getmListGlowdecks().size() ;

								GlowdeckDevice glowdeckDevice = MainActivity.getMainActivity().getCurrentGlowdecks().getDeviceAtPosition
										(position- MainActivity.DRAWER_GLOWDECK_DEVICES0)  ;


								if (glowdeckDevice !=null)
								{

									validFound = true ;
									txtTitle.setText(glowdeckDevice.getName()) ;
									txtTitle.setTextColor( textColor) ;
									imgIcon.setAlpha(1.0f) ;
									if (glowdeckDevice.isConnected())
									{
										imgIcon.setImageResource(R.drawable.power_on_icon) ;
										progressBar1.setVisibility(View.GONE) ;
										if (StreamsApplication.DEBUG_MODE)
										{
											Log.d("dbg","GLOWDECK DEVICE IS ***** CONNECTED") ;
										}
									}
									else
									{
										if (StreamsApplication.DEBUG_MODE)
										{
											Log.d("dbg","GLOWDECK DEVICE IS *NOT* CONNECTED") ;
										}
										if (MainActivity.getStreamsState() == StreamsScreenState.DEVICES_VIEW_DEVICES )
										{
											imgIcon.setImageResource(R.drawable.power_off_icon) ;
										}
										else
										{
											imgIcon.setImageResource(R.drawable.power_off_icon_dark) ;
										}
									}

									if (glowdeckDevice.isConnecting())
									{
										if (MainActivity.msDevicesClicked)
										{
											progressBar1.setVisibility(View.VISIBLE) ;
										}
									}
									else
									{
										progressBar1.setVisibility(View.GONE) ;

									}
								}


								if (!validFound)
								{
									txtTitle.setTextColor( COLOR_OFF_WHITE) ;
									imgIcon.setAlpha(0.0f) ; 

								}
							}
						}
					}
				}
				else

				{
					imgIcon.setVisibility(View.GONE) ;
					txtTitle.setVisibility(View.GONE) ;
					convertView.setVisibility(View.GONE) ;
					progressBar1.setVisibility(View.GONE) ;
				}
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return convertView;


	}

}
