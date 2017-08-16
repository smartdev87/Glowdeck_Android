package com.plsco.glowdeck.settings;

import android.content.Intent;
import android.os.Bundle;

import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.task.SendServerSettingsChanged;
/**
*
* Project : GlowDeck/STREAMS
* FileName: SendDeleteAccountRequest.java
*
* Copyright 2014. PLSCO, Inc. All rights reserved.
*
*/
/**
* History
* 11/1/14 - prepare for Google Play store
*/
/**
* @author Joe Diamand 
* @version 1.0   08/27/14
* 
*/

/**
* The SendDeleteAccountRequest extends SendServerSettingsChanged
*  
*  
*
*/
public class SendDeleteAccountRequest extends SendServerSettingsChanged
{
	//
	//  Globals
	//
	public AccountSettingsAdapter mFinishedListener = null ;
	
	
	
	
	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.SendServerSettingsChanged#doInBackground(java.lang.String[])
	 */
	@Override
	protected Long doInBackground(String... readParms) {
		// TODO Auto-generated method stub
		return super.doInBackground(readParms);
	}

	
	
	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		// now reset the database to 
		// remove any records from the deleted account
		try{
		Intent sendIntent = new Intent(mFinishedListener.mContext, UpdaterService.class) ;
		Bundle bundle = new Bundle();
		bundle.putString(LoginActivity.DELETE_DATABASE, LoginActivity.DELETE_DATABASE );
		sendIntent.putExtras(bundle);
		mFinishedListener.mContext.startService( sendIntent); 
		
		boolean resultBool = (result ==1) ? true : false ;
		mFinishedListener.onTaskComplete(resultBool); 
		mFinishedListener = null ; 
		}catch(Exception e){e.printStackTrace();}
	}
	
	/**
	 * @param finishedListenerParm - for the callback
	 */
	public SendDeleteAccountRequest(AccountSettingsAdapter finishedListenerParm) {

		mFinishedListener = finishedListenerParm;


	}
	
}
