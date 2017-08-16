package com.plsco.glowdeck.drawer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;


/**
 * 
 * @author Joe Diamand 
 * @version 1.0   03/10/15
 * 
 * Project: Streams Android Implementation
 * 
 * file: ProvisionFragment.java
 * 
 *  � Copyright 2015. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * created 3/10/15
 */
/**
 * The ProvisionFragment() extends  Fragment
 * 
 *  
 *
 */

/**
 * 
 *
 */
public class ProvisionFragment extends Fragment{




	Context context ; 


	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	public ProvisionFragment(){



	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = this.getActivity() ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","ProvisionFragment onCreateView") ;
		}

		final View rootView = inflater.inflate(R.layout.fragment_provision, container, false);
		try{
		MainActivity mainActivity = MainActivity.getMainActivity() ;
		if (mainActivity == null )
		{
			return rootView ; 
		}
		ScrollView parentScroll=(ScrollView)rootView.findViewById(R.id.aboutParentScroll);


		StreamsApplication streamsApplication = (StreamsApplication)mainActivity.getApplication() ;

		
		/*
		List<GlowdeckDevice> glowdeckDevices = MainActivity.getMainActivity().getmCurrentGlowdecks().getmListGlowdecks()  ;

		for (GlowdeckDevice device : glowdeckDevices) 
		{
			if (device.isConnected())
			{
			//	connectedToGlowdeck = true ; 
			}

		}

		*/
		
		


		
		}catch(Exception e){e.printStackTrace();}
		return rootView;
	}




}
