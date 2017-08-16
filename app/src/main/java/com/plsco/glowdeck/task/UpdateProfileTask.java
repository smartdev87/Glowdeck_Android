package com.plsco.glowdeck.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.task.LoadingTask.LoadingTaskFinishedListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: UpdateProfileTask.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
//
/**
 * 
 * UpdateProfileTask extends AsyncTask
 * 
 */

/**
 *  
 * UpdateProfileTask.java
 * called from the ProfileFragment mActivity when the user presses the "UPDATE" button
 *  @parms a string that will be sent to the server with the "set_currentuserinfo" request
 *         if the input string is null, sleep for 1 second
 *         otherwise send the request to server and wait for a response
 *  @return when done indicate success or failure
 *          upon success, the calling task resumes in place and the progress-mCountrySpinner is disabled
 *          
 *  @errors  - network error 
 *           - malformed JSON response 
 *           - update request is rejected 
 *   
 *
 */

public class UpdateProfileTask  extends AsyncTask<String, Integer, Boolean> {
	//  
	//

	// Globals (statics)
	private static boolean 				msUpErrorOccurred = false ; // this is set if an error occurs
	private static String  				msUpErrorMessage = "" ; 
	private static  boolean 			msUpErrorNoNetwork = false ; 

	//
	//   Constants
	//
	static final String  ERROR_MSG_0101 = "STRA101 - No Network" ;

	static final String  ERROR_MSG_0111 = "STRA111 - Server msg error" ;


	//
	//  Globals
	//
	private  LoadingTaskFinishedListener 	mFinishedListener = null ;
	private TaskCompleted mCallBack;



	public static boolean didUpErrorOccur()
	{
		return msUpErrorOccurred ;
	}
	public static boolean isUpNetworkUnavailable()
	{
		return msUpErrorNoNetwork ;
	}
	public static String getUpErrorMsg()
	{
		return msUpErrorMessage ;
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(String... params) {

		try{
		//int count = params.length;
		String request = params[0] ;
		if (request == null)
		{
			int incr = 1;

			for (int i = 0; i < incr; i++) {

				// Update the progress bar after every step
				//int progress = (int) ((i / (float) incr) * 100);
				//publishProgress(progress);

				// Do some long loading things
				try { Thread.sleep(250); } catch (InterruptedException ignore) {}
			}
			return true ;
		}
		else
		{
			try {
				return updateServerWithUserParms( request) ;
			}
			finally {

			}
		}
		}catch(Exception e){e.printStackTrace();}
		return null;
	}
	/**
	 * Constructor
	 * @param mFinishedListener - where to send the return value
	 */
	public UpdateProfileTask(LoadingTaskFinishedListener finishedListener) {

		try{
		this.mFinishedListener = finishedListener;
		this.mCallBack = (TaskCompleted)finishedListener ;
		}catch(Exception e){e.printStackTrace();}
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try{
		mCallBack.onTaskComplete(result);
		mFinishedListener.onTaskFinished(); // Inform  listener (caller) of completion
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param userId - either from the login screen or from the saved prefs
	 * @param password - either from the login screen or from the saved prefs
	 * @return boolean - return true if the credentials were correctly verified
	 *                 - return false if were not able to verify. this could happen because
	 *                    the parameter are incorrect
	 *
	 * @throws JSONException - this usually results in an unrecoverable error
	 *
	 * TODO  	1. need to add code for checking when network is unavailable
	 * 			2. in case of a JSONException, goto login screen, and issue message (STRAND001: Uknown Response from ... etc)
	 *
	 */

	/**
	 * @param theRequest - what is sent to the server
	 * @return  true - only if succeeded
	 */
	private boolean updateServerWithUserParms(String theRequest)
	{

		boolean retVal = false ;
		try{
		msUpErrorOccurred = false ;
		msUpErrorNoNetwork     = false ;
		msUpErrorMessage = "" ;
		if (theRequest == null)
		{
			return retVal ;
		}
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(UpdaterService.SET_CURRENTUSERINFO);

		try {

			StringEntity se = new StringEntity(theRequest);
			httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
			httppost.setEntity(se);


			HttpResponse httpResponse = httpclient.execute(httppost);




			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {

				line = VerifyCredentialsTask.cleanUpJsonFromServer(line) ;

				builder.append(line).append("\n");
			}
			Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-mm-DD HH:mm:ss").create();

			StreamsUser streamsUser = gson.fromJson( builder.toString(), StreamsUser.class) ;

			VerifyCredentialsTask.setStreamsUser(streamsUser);

			//String status1 = mStreamsUser.getstatus() ;
			//String status2 = mStreamsUser.getStatus() ;
			retVal = true ; 

		}

		catch (IOException e) {
			e.printStackTrace();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "IOException Error in checkCredentials = " + e ) ;
			}
			msUpErrorOccurred = true ; 
			msUpErrorNoNetwork = true ; 
			msUpErrorMessage = ERROR_MSG_0101 ; 
			return retVal ;  
		} 
		catch (Exception e) {
			e.printStackTrace();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
			}
			msUpErrorOccurred = true ; 
			msUpErrorMessage = ERROR_MSG_0111 ; 
			return retVal ;  
		} 
		finally
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Finally in updateServerWithUserParms." ) ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	} 
}
