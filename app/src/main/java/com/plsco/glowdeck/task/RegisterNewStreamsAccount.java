package com.plsco.glowdeck.task;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.CreateAccount;
import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.auth.StreamsUserLogin;
import com.plsco.glowdeck.auth.StreamsUserSignUp;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;

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
 * FileName: RegisterNewStreamsAccount.java
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

/**
 * Called by CreateAccount to register a new user from the server
 * 
 * server needs the following information:
 * email=name@Address&lastname=xxxxxx&password=yyyyyy&firstname=zzz
 * 
 */
public class RegisterNewStreamsAccount extends AsyncTask<String, Integer, Boolean>{
	// Globals
	
	
	//
	//  Constants
	//
	private final static String	SUCCESS_SERVER_RESPONSE = "SUCCESS";
	// Globals
	//private  StreamsApplication 						mStreamsApplication = null;
	private CreateAccount mFinishedListener = null ;
	String 												mServerErrorMessage = "" ; 


	/**
	 * constructor for the RegisterNewStreamsAccount task
	 * when we are done, return intent to the mFinishedListener
	 * @param  (Activity's callback)  control return to the CreateAccount's callback
	 */
	public RegisterNewStreamsAccount(CreateAccount finishedListener) {

		this.mFinishedListener = finishedListener;
		//this.callBack = mFinishedListener ;
		//Activity activity = (Activity)mFinishedListener ;
		//mStreamsApplication = (StreamsApplication)activity.getApplication() ; 
	}



	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {

		super.onCancelled();
	}

	@Override
	protected void onCancelled(Boolean result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try{
		if (mFinishedListener != null)
		{
			if (!result)
			{
				mFinishedListener.setServerErrorMsg(mServerErrorMessage) ;
			}
			mFinishedListener.onTaskComplete(result); 
			mFinishedListener = null ; 
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		boolean retVal = true ; 
		try{
		String userId = params[0] ;     
		String password = params[1] ;  
		String firstName = params[2] ; 
		String lastName = params[3] ; 
		retVal = newAccount( userId,  password, firstName, lastName) ;
		}catch(Exception e){e.printStackTrace();}
		return retVal;
	}

	/**
	 * @param userId  userid for the new account
	 * @param password password for the new userid
	 * @param firstName  new user's first  name
	 * @param lastName   new usr's last name 
	 * @return
	 */
	boolean newAccount(String userId,String password,String  firstName,String  lastName) 
	{
		boolean retVal = false ; 

		try{
		StreamsUserSignUp streamsUserSignUp = null ;

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(UpdaterService.HTTP_REGISTER);

		String newAccountString = "email=" + userId + "&lastname=" + lastName + "&password=" + password + "&firstname=" + firstName;

		try {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "password =" + password + ", &email= = " + userId ) ;
			}
			StringEntity se = new StringEntity(newAccountString);		 
			httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
			httppost.setEntity(se);


			HttpResponse httpResponse = httpclient.execute(httppost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {

				builder.append(line).append("\n");
			}

			Gson gson = new GsonBuilder(). 

					setDateFormat("yyyy-mm-DD HH:mm:ss").create();



			//StreamsUser 

			streamsUserSignUp =  gson.fromJson( builder.toString(), StreamsUserSignUp.class);

			if (streamsUserSignUp == null)
			{
				mServerErrorMessage = "Server Error" ;
				return retVal ;
			}



			String statusSignUp = streamsUserSignUp.getStatus() ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "newAccount::status =" + statusSignUp ) ;
			}
			if (statusSignUp == null)
			{
				mServerErrorMessage = "Server Error" ;
				return retVal ;

			}

			if (statusSignUp.compareTo(SUCCESS_SERVER_RESPONSE)!=0)
			{
				// gather the error codes
				mServerErrorMessage = streamsUserSignUp.getMsg() ;
				return retVal ; 
			}
			// new user creation was successful
			// now 1) login
			//     2) get the current userInfo 
			//     3) go to the profile entry form to get the shipping info
			httppost = new HttpPost(UpdaterService.AUTH_COOKIE);
			String useridPass = "password=" + password + "&email=" + userId ;
			try {
				se = new StringEntity(useridPass);		 
				httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
				httppost.setEntity(se);


				httpResponse = httpclient.execute(httppost);

				reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {

					builder.append(line).append("\n");
				}

				gson = new GsonBuilder(). 

						setDateFormat("yyyy-mm-DD HH:mm:ss").create();



				//StreamsUser 

				StreamsUserLogin streamsUserLogin =  gson.fromJson( builder.toString(), StreamsUserLogin.class);

				String status1 = streamsUserLogin.getstatus() ;
				String status2 = streamsUserLogin.getStatus() ;
				if ((status1 != null) && (status2 != null))
				{
					if ( (status1.compareTo("ok")==0) &&  (status2.compareTo("SUCCESS")==0) )
					{
						VerifyCredentialsTask.setStreamsUserLogin(streamsUserLogin) ;




						httppost = new HttpPost(UpdaterService.GET_CURRENTUSERINFO) ;
						String useridToken = "UserID=" + streamsUserLogin.getStreamsAccount().getID()  + "&Token=" + streamsUserLogin.getStreamsAccount().getToken() ;
						try {
							se = new StringEntity(useridToken);
							httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
							httppost.setEntity(se);
							httpResponse = httpclient.execute(httppost);
							reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
							builder = new StringBuilder();
							for (String line = null; (line = reader.readLine()) != null;) {
								line = VerifyCredentialsTask.cleanUpJsonFromServer(line) ;
								builder.append(line).append("\n");
							}
							gson = new GsonBuilder().

									setDateFormat("yyyy-mm-DD HH:mm:ss").create();
							VerifyCredentialsTask.setStreamsUser( gson.fromJson( builder.toString(), StreamsUser.class)) ;
							// now we have a valid, most recent, streamUser object
							// save the contents builder to disk

							if (VerifyCredentialsTask.serializeStreamsUserInfo( builder) )
							{
								//StreamsApplication mStreamsApplication = (StreamsApplication)this.streamsApplication ; 
								String streamsUserVersion = StreamsApplication.streamsUserVersion ;
								//mStreamsApplication.getStreamsUserVersion() ;  
								SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
								SharedPreferences.Editor editor = sharedPrefs.edit();

								LoginActivity.setCurrentUserID(userId) ;
								LoginActivity.setCurrentPassword(password) ;


								editor.putString(LoginActivity.PrefsStreamsUserVersion, streamsUserVersion);
								editor.putString(LoginActivity.PrefsUserid,userId );
								editor.putString(LoginActivity.PrefsPassword,password );
								editor.commit();
							}

							retVal = true ; // finally we can return true 
						}
						catch (IOException e) {
							if (StreamsApplication.DEBUG_MODE)
							{

								Log.d("dbg", "IOException Error getting userinfo = " + e ) ;
							}
							mServerErrorMessage = "Network Error" ;


							return retVal ;  
						} 
						catch (Exception e) {
							if (StreamsApplication.DEBUG_MODE)
							{

								Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
							}
							mServerErrorMessage = "Unknown Error" ;

							return retVal ;  
						} 
					}
				}
			} catch (IOException e) {
				if (StreamsApplication.DEBUG_MODE)
				{

					Log.d("dbg", "IOException Error in checkCredentials = " + e ) ;
				}
				mServerErrorMessage = "Network Error" ;

				return retVal ;  
			} 
			catch (Exception e) {
				if (StreamsApplication.DEBUG_MODE)
				{

					Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
				}
				mServerErrorMessage = "Unknown Credentials Error" ;				
				return retVal ;  
			} 


		}

		catch (IOException e) {
			if (StreamsApplication.DEBUG_MODE)
			{

				Log.d("dbg", "IOException Error in Create New Account = " + e ) ;
			}
			mServerErrorMessage = "Network Error" ;


			return retVal ;  
		} 
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{

				Log.d("dbg", "Exception Error in Create New Account = " + e ) ;
			}
			mServerErrorMessage = "Unknown Error Creating Account" ;
			return retVal ;  
		} 
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 


	}

}
