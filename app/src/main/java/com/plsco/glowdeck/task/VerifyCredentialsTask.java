package com.plsco.glowdeck.task;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.auth.StreamsUserLogin;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;
import com.plsco.glowdeck.task.LoadingTask.LoadingTaskFinishedListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: VerifyCredentialsTask.java
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
 * VerifyCredentialsTask extends AsyncTask
 * 
 */

/**
 * VerifyCredentialsTask is Called by LoginActivity to verify the userid and password 
 * If the userid/password & password were available in the preferences, then that is 
 * what is being passed in to this task, Otherwise the userid/password were entered in the 
 * logiActivity's form . 
 * <p>
 *  
 *
 * @param  String[]  userid,password
 * 
 * @return StreamUser - the com.glowdeck.streams.streamdata that profiles the current user. 
 * 							if unable to get the SteramUser info, return null
 * 							the status of the operation is kept in the
 * 							VC_*** static variables
 * @see      N/A
 */
public class VerifyCredentialsTask  extends AsyncTask<String, Integer, Boolean> {



	//
	//    Global (statics)
	//
	private final  static String 						msStreamsUserFileName = "streamsuser.info" ;
	// file name for saving the StreamsUser info
	public static String getStreamsUserFileName()
	{
		return msStreamsUserFileName ;
	}
	//
	private static boolean 			msErrorOccurredInVerifyCredentials = false ; // this is set if an error occurs
	private static String  			msErrorMessageInVerifyCredentials = "" ; 
	private static  boolean 		msErrorNoNetwork = false ; 
	public static boolean didErrorOccur()
	{
		return msErrorOccurredInVerifyCredentials ; 
	}
	public static boolean isNetworkUnavailable()
	{
		return msErrorNoNetwork ; 
	}
	public static String getErrorMsg()
	{
		return msErrorMessageInVerifyCredentials ; 
	}
	private static StreamsUserLogin mStreamsUserLogin = null;
	public static StreamsUserLogin getStreamsUserLogin() {
		return mStreamsUserLogin;
	}
	public static void setStreamsUserLogin(StreamsUserLogin streamsUserLogin) {
		VerifyCredentialsTask.mStreamsUserLogin = streamsUserLogin;
	}
	private static StreamsUser mStreamsUserPrivate = null ;

	public static StreamsUser getStreamsUser(Context context)
	{
		if (mStreamsUserPrivate == null)
		{
			try{
			LoginActivity.recoverSavedStreamsUser(context) ;
			}catch(Exception e){e.printStackTrace();}
		}
		return mStreamsUserPrivate;
	}
	public static StreamsUser getStreamsUser()
	{

		return mStreamsUserPrivate;
	}



	public static void setStreamsUser(StreamsUser streamsUserParm)
	{
		mStreamsUserPrivate= streamsUserParm;
	}


	//
	// Constants
	//
	static final  String  					MSG_0001 = "STRA001 - No Network" ;
	static final  String  					MSG_0002 = "STRA002 - No Network" ;
	static final  String  					MSG_0011 = "STRA011 - Server msg error" ;
	static final  String  					MSG_0012 = "STRA012 - Server msg error" ;
	private final LoadingTaskFinishedListener FINISHED_LISTENER;
	//
	//  Globals
	//
	private TaskCompleted mCallBack;
	//
	// the JSON response from the server is stored in these two classes
	// they are kept as statics and their accessors are located just below their
	// definitions


	/**
	 * constructor for the VerifyCredentialsTask task
	 * <p>
	 * when we are done, return intent to the FINISHED_LISTENER
	 *
	 * @param  (Activity's callback)  control return to the LoginActivity's callback
	 */
	public VerifyCredentialsTask(LoadingTaskFinishedListener finishedListener) {


		this.FINISHED_LISTENER = finishedListener;
		this.mCallBack = (TaskCompleted)finishedListener ;
		
		//Activity activity = (Activity)mFinishedListener ;

	}


	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(String... params) {   

		String userId = params[0] ;     
		String password = params[1] ;  
		boolean retVal = checkCredentials( userId,  password) ;

		return(retVal );




	} 
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try{
		mCallBack.onTaskComplete(result);
		FINISHED_LISTENER.onTaskFinished(); // Inform  listener (caller) of completion 
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
	 */
	private boolean checkCredentials(String userId, String password)
	{

		boolean retVal = false ; // set for error, override upon success 
		try{
		msErrorOccurredInVerifyCredentials = false ;
		msErrorNoNetwork     = false ;
		msErrorMessageInVerifyCredentials = "" ; 

		HttpClient httpclient = new DefaultHttpClient();
		
		HttpPost httppost = new HttpPost(UpdaterService.AUTH_COOKIE);
		String useridPass = "password=" + password + "&email=" + userId ;
		try {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "password =" + password + ", &email= = " + userId ) ;
			}
			StringEntity se = new StringEntity(useridPass);		 
			httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
			httppost.setEntity(se);


			HttpResponse httpResponse = httpclient.execute(httppost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","VerifyCredentials:" + line) ;
				}
				builder.append(line).append("\n");
			}

			Gson gson = new GsonBuilder(). 

					setDateFormat("yyyy-mm-DD HH:mm:ss").create();



			//StreamsUser 
			mStreamsUserLogin =  gson.fromJson( builder.toString(), StreamsUserLogin.class);
			String status1 = mStreamsUserLogin.getstatus() ;
			String status2 = mStreamsUserLogin.getStatus() ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "status =" + status1 + ", Status = " + status2 ) ;
			}
			if ((status1 != null) && (status2 != null))
			{
				if ( (status1.compareTo("ok")==0) &&  (status2.compareTo("SUCCESS")==0) )
				{
					// after a successfull login with the GlowDeck server, get the most 
					// current StreamsUser profile information
					String streamsUserID = mStreamsUserLogin.getStreamsAccount().getID() ; 
					String streamsToken = mStreamsUserLogin.getStreamsAccount().getToken() ; 
					
					httppost = new HttpPost(UpdaterService.GET_CURRENTUSERINFO) ;
					String useridToken = "UserID=" + streamsUserID + "&Token=" + streamsToken ;
					try {
						se = new StringEntity(useridToken);		 
						httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
						httppost.setEntity(se);
						httpResponse = httpclient.execute(httppost);
						reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
						builder = new StringBuilder();
						for (String line = null; (line = reader.readLine()) != null;) {
							line = cleanUpJsonFromServer(line) ;
							builder.append(line).append("\n");
						}
						gson = new GsonBuilder().

								setDateFormat("yyyy-mm-DD HH:mm:ss").create();
						setStreamsUser( gson.fromJson( builder.toString(), StreamsUser.class)) ;
						// now we have a valid, most recent, streamUser object
						// save the contents builder to disk

						if (serializeStreamsUserInfo( builder) )
						{

							String streamsUserVersion =    StreamsApplication.streamsUserVersion ; //  mStreamsApplication.getStreamsUserVersion() ;
							SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
							SharedPreferences.Editor editor = sharedPrefs.edit();
							editor.putString(LoginActivity.PrefsStreamsUserVersion, streamsUserVersion);
							editor.commit();
						}

						retVal = true ; 
					}
					catch (IOException e) {

						if (StreamsApplication.DEBUG_MODE)
						{
						Log.d("dbg", "IOException Error getting userinfo = " + e ) ;
						}
						msErrorOccurredInVerifyCredentials = true ; 
						msErrorNoNetwork = true ; 
						msErrorMessageInVerifyCredentials = MSG_0002 ; 

						return retVal ;  
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						if (StreamsApplication.DEBUG_MODE)
						{
							Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
						}
						msErrorOccurredInVerifyCredentials = true ; 
						msErrorMessageInVerifyCredentials = MSG_0012 ; 
						return retVal ;  
					} 

				}
			}

		}

		catch (IOException e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "IOException Error in checkCredentials = " + e ) ;
			}
			msErrorOccurredInVerifyCredentials = true ; 
			msErrorNoNetwork = true ; 
			msErrorMessageInVerifyCredentials = MSG_0001 ; 
			// we failed in no network ... but we should keep the progress bar spinner
			//   up for a little while, to add esthetic value

			try { 
				Thread.sleep(1000); 
			} catch (InterruptedException ignore) {}


			return retVal ;  
		} 
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
			}
			msErrorOccurredInVerifyCredentials = true ; 
			msErrorMessageInVerifyCredentials = MSG_0011 ; 
			return retVal ;  
		} 
		finally
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Finally in checkCredentials." ) ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	} 

	/**
	 * @return  true of comm w/server succeeded
	 */
	public static boolean  getCurrentUserInfo()
	{
		boolean retVal = true ; 
		try{
		String streamsUserID = null ; 
		String streamsToken = null ; 

		if (mStreamsUserLogin != null)
		{
			streamsUserID = mStreamsUserLogin.getStreamsAccount().getID() ; 
			streamsToken = mStreamsUserLogin.getStreamsAccount().getToken() ; 
		}
		else
		{
			if (mStreamsUserPrivate != null)
			{
				StreamsAccount streamsAccount = mStreamsUserPrivate.getStreamsAccount(); 
				if (streamsAccount != null)
				{
					streamsUserID = mStreamsUserPrivate.getStreamsAccount().getID() ;
					streamsToken = mStreamsUserPrivate.getStreamsAccount().getToken() ;

				}
				else
				{
					return false ; 
				}
			}
		}
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpPost httppost = new HttpPost(UpdaterService.GET_CURRENTUSERINFO) ;
		String useridToken = "UserID=" + streamsUserID + "&Token=" + streamsToken ;
		StringBuilder builder = new StringBuilder();
		try {
			StringEntity se = new StringEntity(useridToken);		 
			httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
			httppost.setEntity(se);
			HttpResponse httpResponse = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				line = cleanUpJsonFromServer(line) ;
				builder.append(line).append("\n");
			}
			Gson gson = new GsonBuilder(). 

					setDateFormat("yyyy-mm-DD HH:mm:ss").create();

			setStreamsUser( gson.fromJson( builder.toString(), StreamsUser.class)) ;
			// now we have a valid, most recent, streamUser object
			// save the contents builder to disk

			if (serializeStreamsUserInfo( builder) )
			{

				String streamsUserVersion = 
						StreamsApplication.streamsUserVersion ;
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString(LoginActivity.PrefsStreamsUserVersion, streamsUserVersion);
				editor.commit();
			}

			retVal = true ; 
		}
		catch (IOException e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "IOException Error getting userinfo = " + e ) ;
			}
			msErrorOccurredInVerifyCredentials = true ; 
			msErrorNoNetwork = true ; 
			msErrorMessageInVerifyCredentials = MSG_0002 ; 

			return retVal ;  
		} 
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
			}
			msErrorOccurredInVerifyCredentials = true ; 
			msErrorMessageInVerifyCredentials = MSG_0012 ; 
			return retVal ;  
		} 

		}catch(Exception e){e.printStackTrace();}
		return retVal ; 

	}
	/**
	 * Called after successful login and StreamsUserInfo has been successfully updated. 
	 * saves the JSON com.glowdeck.streams.streamdata so that it can be reused if the network is unavailable
	 * <p>
	 * Serialize  StreamsUserInfo in case is needed in future, such as when trying to 
	 *              (re)start app w/o network
	 *
	 * @param  n/a
	 * @return boolean 	true - write went ok 
	 * 					false - an error occcured on the write
	 */
	public static boolean serializeStreamsUserInfo(StringBuilder builder)
	{
		try{
		BufferedWriter writer = null;
		File file = new File(MainActivity.getAppContext().getFilesDir(), getStreamsUserFileName());
		try {
			writer = new BufferedWriter(new FileWriter(file));
			if (writer != null)
			{
				writer.write(builder.toString());
				writer.close();
			} 
		}catch (IOException e) {

			e.printStackTrace();
			return false ;
		}		
		}catch(Exception e){e.printStackTrace();}
		return true ;  
	}
	/**
	 * @param inPut - the string from the server that needs help so that gson will accept it
	 * @return
	 */
	public static String cleanUpJsonFromServer(String inPut)
	{
		String outPut2 = null;
		try{
		final String accountsEmptyString = "\"Account\":\"\"" ;
		final String accountsEmptyArray = "\"Account\":[]" ;
		final String newsKeywordsEmptyString = "\"Keywords\":\"\"" ;
		final String newsKeywordsEmptyArray = "\"Keywords\":[]" ;
		//final String booleanKeywordsActivityOne = "\"Enabled\":\"1\"" ;
		//final String booleanKeywordsActivityTrue = "\"Enabled\":\"true\"" ;

		String outPut1 = inPut.replaceAll(accountsEmptyString, accountsEmptyArray);
		outPut2 = outPut1.replaceAll(newsKeywordsEmptyString, newsKeywordsEmptyArray);
		}catch(Exception e){e.printStackTrace();}
		return outPut2 ; 
		/* revert bacl to using "1" & "0" as the sever won't accept TRUE / FALSE 
		String outPut3 = outPut2.replaceAll(booleanKeywordsActivityOne, booleanKeywordsActivityTrue);
		return outPut3 ; 
		 */
		
	}



}
