
package com.plsco.glowdeck.drawer;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plsco.glowdeck.R;

/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: DevicesFragment.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * The DevicesFragment() extends mCurrentFragment
 * 
 *  
 *
 */

/**
 * 
 *
 */
public class DevicesFragment extends Fragment {

	public DevicesFragment(){}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_stream_settings, container, false);


		return rootView;
	}
}
