
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
 * FileName:  SignOutFragment.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *   
 */

/**
 * History
 * Prepare for Google Play Store 11/1/14
 */


/**
 * SignOutFragment extends Fragment
 * 
 *
 */
public class SignOutFragment extends Fragment {

	public SignOutFragment(){}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_signout, container, false);

		return rootView;
	}
}
