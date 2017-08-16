package com.plsco.glowdeck.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.task.TaskCompleted;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.FormsPersistentData;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.task.LoadingTask.LoadingTaskFinishedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: LoginActivity.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * The LoginActivity() mActivity is called to verify the user's credentials.
 * The user is presented with a form that requires filling in the fields
 * userid, password 
 * 
 * in the event the network is unavailable, user the userid stired in the preferences
 * 
 *  TODO serialize userINfo
 *  
 *
 */
public class LoginActivity extends Activity implements OnItemClickListener ,  LoadingTaskFinishedListener , TaskCompleted {

	// Globals (static, public)
	//
	public static final String 	DELETE_DATABASE 		= 		"Delete the Database" ;
	// sent when login screen is invoked to insure that when a new user logs 
	//   in they can't see the 
	//   previous user's data
	public static final String  RESTART_POLLING = "LoginActivity Restart Polling" ;
	public static final String  RETURN_STAT_LOGIN   	=    	"RETURN_STAT_LOGIN" ; 
	public static final String  LOGIN_SUCCESSFUL     	=     	"LOGIN_SUCCESSFUL" ;
	public static final String  CREATE_USER_SUCCESSFUL  =     	"CREATE_USER_SUCCESSFUL" ;
	public final static  String PrefsUserid = "Userid" ;
	public final static  String PrefsPassword = "Password" ;
	public final static  String PrefsDefaultString = "" ;
	public final static  String PrefsStreamsUserVersion = "StreamsUserVersion" ;
	//
	//  Globals (static, private)
	//
	// 
	FormsPersistentData mFormsPersistenceData = FormsPersistentData.getInstance() ;
	//   used to store com.glowdeck.streams.streamdata on the 
	// forms, in case we need to re-populate the fields upon a return
	//private static LoginActivity mLoginActivity ; 

	private static boolean mCreateAccountInProgress = false  ; 
	private static boolean mLoginInProgress = false  ; 
	// login screen has multiple buttons. Insure button is not pressed while a
	//    request is in progress
	//
	private static  boolean mUnverifiedLogin = false ;  
	// in the event that the network is down, allow access, 
	//  but try to get latest userInfo as soon as network available and 
	//   info is needed
	public static boolean isUnverifiedLogin() {
		return mUnverifiedLogin;
	}
	public static void setUnverifiedLogin(boolean unverifiedLogin) {
		mUnverifiedLogin = unverifiedLogin;
	}

	//the currently used userid/password
	private static String mCurrentUserID = PrefsDefaultString ;
	private static String mCurrentPassword  = PrefsDefaultString ; 
	public static String getCurrentUserID() {
		return mCurrentUserID;
	}
	public static void setCurrentUserID(String currentUserID) {
		mCurrentUserID = currentUserID;
	}
	public static String getCurrentPassword() {
		return mCurrentPassword;
	}
	public static void setCurrentPassword(String currentPassword) {
		mCurrentPassword = currentPassword;
	}
	private static StreamsApplication mStreamsApplication ;
	public static void setStreamsApplication (StreamsApplication streamsApplication)
	{
		mStreamsApplication = streamsApplication ;
	}
	private static SharedPreferences mSharedPrefsStatic = null  ;
	public static SharedPreferences getSharedPrefs(Context context)
	{
		if (mSharedPrefsStatic == null)
		{
			mSharedPrefsStatic = PreferenceManager.getDefaultSharedPreferences(context);

		}
		return mSharedPrefsStatic ;
	}

	//
	//  Globals
	//

	private boolean mAutoLogin = true ;
	// try to logon using saved credentials

	private ProgressDialog mProgressDialog = null ;
	// uses spinner theme, customized with Streams logo as background



	// Global Form widgets
	private EditText mUserNameEditText = null; 
	private EditText mPasswordEditText = null ;
	private CheckBox mRememberMeCheckBox = null ; 

	//
	//
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * standard routine called when mActivity (re)created
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
		//mLoginActivity = this ; 
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","LoginActivity:OnCreate()") ;
		}

		// force cleanup of previous user  
		SharedPreferences sharedPrefs = getSharedPrefs(this) ;
		String userIDPref = sharedPrefs.getString(PrefsUserid, PrefsDefaultString);
		String passwordPref = sharedPrefs.getString(PrefsPassword, PrefsDefaultString);
		VerifyCredentialsTask.setStreamsUser(null) ;
		// delete the database
		Intent sendIntent = new Intent(this, UpdaterService.class) ;
		Bundle bundle = new Bundle();
		bundle.putString(DELETE_DATABASE,DELETE_DATABASE );
		sendIntent.putExtras(bundle);
		startService( sendIntent); 

		if (checkIfCredentialsExist(this))
		{
			mAutoLogin = true ;
		}

		// if autologin is true, try to login using saved credentials
		if (!mAutoLogin)
		{
			loginScreen() ; 

		}
		else
		{
			// screen goes dark when logging in automatically
			setContentView(R.layout.blank_screen);
			//getActionBar().hide();
			attemptLogin(userIDPref,passwordPref) ;
		}

		}catch(Exception e){e.printStackTrace();}
	}
	public static boolean checkIfCredentialsExist(Activity activity)
	{
		boolean retVal = false ; 
		try{
		// check if UserInfo can be initialized. This will force a check to see if 
		// the UserInfo database has been invalidated
		// make sure that LoginActivity has access to a application mContext

		VerifyCredentialsTask.getStreamsUser(mStreamsApplication) ;

		SharedPreferences sharedPrefs = getSharedPrefs(activity) ;
		String userIDPref = sharedPrefs.getString(PrefsUserid, PrefsDefaultString);
		String passwordPref = sharedPrefs.getString(PrefsPassword, PrefsDefaultString);

		if ( (userIDPref.compareTo(PrefsDefaultString)!=0) &&
				(passwordPref.compareTo(PrefsDefaultString)!=0)	)
		{  // userId/Password both length > 0
			retVal = true ;
		} 
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	/**
	 * Sets up the listener for the login button and the create account
	 * hides the action bar for this screen. 
	 * When the user click the login button, call attemptLogin() 
	 * When the user clicks the the create_account button, 
	 * call the createAccount mActivity
	 * <p>
	 * 
	 */
	void loginScreen()
	{
		try{
		setContentView(R.layout.activity_login);
		//getActionBar().hide();

		// get the forms wideget ids
		mUserNameEditText = (EditText) findViewById(R.id.login_userid_edit);
		mRememberMeCheckBox = (CheckBox) findViewById(R.id.login_remember_CB) ;
		mPasswordEditText  = (EditText) findViewById(R.id.login_password_edit);

		// programmatically adjust the checkbox background
		int offWhiteColor = getResources().getColor(R.color.color_login_edittext); 
		mRememberMeCheckBox.setBackgroundColor(offWhiteColor) ;

		findViewById(R.id.login_loginButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				attemptLogin();
			}
		});
		TextView buildNumberTV  = (TextView)  findViewById(R.id.login_version_textview)  ;
		
		buildNumberTV.setText("Build " + getResources().getString(  R.string.build_version) );
		findViewById(R.id.create_account_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				// don't process button request if already in  progress
				if (mCreateAccountInProgress)
				{
					return ; 
				}

				mCreateAccountInProgress = true ;

				// start the create account mActivity 
				Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Attempt to login with the userId and Password
	 * <p>
	 * Setup the progress-spinner while trying to auth the user over the network
	 * Start the verifyCredentials task to attempt the network connection asynchronously.
	 *
	 * @param  String userid
	 * @param  String Password
	 * @return      the image at the specified URL
	 * @see         Image
	 */
	void attemptLogin(String userId,String passWord)
	{
		try{
		mProgressDialog=new ProgressDialog(this,R.style.SpinnerTheme);  // R.style.SpinnerTheme)


		mProgressDialog.setCancelable(false);

		mProgressDialog.setIcon(R.drawable.streams_beta)  ;

		mProgressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar) ;      //          android.R.style.Widget_ProgressBar_Small);

		mProgressDialog.show() ;
		mCurrentUserID = "User"; // userId;
		mCurrentPassword = "Pass"; // passWord;
		VerifyCredentialsTask verifyCredentialsTask = new VerifyCredentialsTask(this); //

		verifyCredentialsTask.execute(userId, passWord) ;
		}catch(Exception e){e.printStackTrace();}
	}

	void attemptLogin()
	{
		try{
		boolean invalid = false ;
		if (mLoginInProgress)
		{
			return ;
		}
		mLoginInProgress = true ;

		AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);

		Spannable span = null ;
            /*
		if(mUserNameEditText.getText().toString().length() == 0 )
		{
			invalid = true ;
			span = new SpannableString("\nTry Again\nYou must enter a username\n");
		}
		else
		{
			if(mPasswordEditText.getText().toString().length() == 0 )
			{
				invalid = true ;
				span = new SpannableString("\nTry Again\nYou must enter a password\n");
			}
		}*/

		if (invalid)
		{
			TextView resultMessage = new TextView(this);
			resultMessage.setTextSize(18);

			span.setSpan(new RelativeSizeSpan(0.7f), 11, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			resultMessage.setText(span);
			resultMessage.setGravity(Gravity.CENTER);
			completeDialog.setView(resultMessage);
			completeDialog.setCancelable(false) ;
			completeDialog.setPositiveButton(Html.fromHtml(
					"<font  color=\"#0088ff\"><b>OK</></font>"),
					new DialogInterface.OnClickListener() {
				// @SuppressLint("DefaultLocale")
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					mLoginInProgress = false ;
				}
			});

			completeDialog.show() ;
		}
		else
		{
			// attemptLogin(mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString()) ;
            attemptLogin("User", "Pass"); //  mPasswordEditText.getText().toString()) ;

        }

		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop()
	{
		super.onStop() ;
		try{
		if (mRememberMeCheckBox != null)
		{
			mFormsPersistenceData.setLoginRememberMeCB(mRememberMeCheckBox.isChecked()) ;
		}
		if (mUserNameEditText != null)
		{
			mFormsPersistenceData.setLoginUserNameEdit(mUserNameEditText.getText().toString()) ;
		}
		if (mPasswordEditText != null)
		{
			mFormsPersistenceData.setPasswordEdit(mPasswordEditText.getText().toString()) ;
		}
		mCreateAccountInProgress = false ;
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{

		try{

		saveSharedPreferences() ;

		moveTaskToBack(true);
		}catch(Exception e){e.printStackTrace();}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume() ;
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","LoginActivity::onResume()") ;
		}
		// check to see if we came back from onCreategetStreamsUser
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		if (streamsUser != null)
		{

			if (streamsUser.isCreateNewUserInProgress())
			{
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","LoginActivity::mStreamsUser.isCreateNewUserInProgress()") ;
				}
				Intent returnIntent = new Intent();

				returnIntent.putExtra(RETURN_STAT_LOGIN, CREATE_USER_SUCCESSFUL);
				setResult(RESULT_OK,returnIntent);
				// save the successful login info the prefs
				saveSharedPreferences() ;

				//overridePendingTransition(R.anim.fade_out,R.anim.fade_in) ;

				mLoginInProgress = false ;
				finish() ;
				return ;
			}
		}

		if (mUserNameEditText != null)
		{
			mUserNameEditText.setSelection(0) ;
		}
		String emailEditTextPrev  = "" ;
		String passwordTextPrev  = "" ;

		emailEditTextPrev = mFormsPersistenceData.getLoginUserNameEdit() ;
		passwordTextPrev = mFormsPersistenceData.getPasswordEdit() ;
		if (emailEditTextPrev.length() > 0)
		{
			if (mUserNameEditText != null)
			{
				mUserNameEditText. setText(emailEditTextPrev) ;

				mFormsPersistenceData.setLoginUserNameEdit("") ;
				if (mPasswordEditText != null)
				{

					mPasswordEditText.requestFocus();

				}
			}
		}
		if (passwordTextPrev.length() > 0)
		{
			if (mPasswordEditText != null)
			{
				mPasswordEditText. setText(passwordTextPrev) ;

				mFormsPersistenceData.setPasswordEdit("") ;
			}
		}
		if (mRememberMeCheckBox != null)
		{
			mRememberMeCheckBox.setChecked(mFormsPersistenceData.getLoginRememberMeCB()) ;
		}
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public  void onDestroy()
	{
		super.onDestroy() ;
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onDestroy in LoginActivity") ;
		}
		if (mProgressDialog != null)
		{
			mProgressDialog.dismiss() ;
			mProgressDialog = null ;
		}
		}catch(Exception e){e.printStackTrace();}
	}


	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.LoadingTask.LoadingTaskFinishedListener#onTaskFinished()
	 */
	@Override
	public void onTaskFinished() {
		// no post processing needed


	}

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.TaskCompleted#onTaskComplete(java.lang.Boolean)
	 */
	@Override
	public void onTaskComplete(Boolean result) {
		boolean success = true ;

		try{

		/*
		if (!result.booleanValue() )
		{   // credentials failed
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss() ;
				mProgressDialog = null ;
			}
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			Spannable span = null ;
			TextView resultMessage = new TextView(this);
			resultMessage.setTextSize(18);
			//Typeface tf = Typeface.DEFAULT_BOLD ;
			AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
			success = false ;

			// failure to login occurred either because of:
			// 1) either because the server said credentials were invalid, or
			// 2) an (JSON) error occurred in parsing the server response, or
			// 3) a network error occurred (no network)

			// first check if error occurred in VerifyCredentials
			if (VerifyCredentialsTask.didErrorOccur())
			{
				// yes, error did occur
				// check to see if the error was due to the network
				if (VerifyCredentialsTask.isNetworkUnavailable())
				{ // yes, we failed on network availability
					if (checkIfCredentialsExist(this))
					{
						// OK, allow the login because the credentials have been prev verified but the network is unavailable
						// but set the unverfied_login indicator ;
						// but first we have to load the userInfo from the last saved copy.
						//  and we know that we cant communicate with the
						//  server because mUnverifiedLogin is set.
						//   If we can't load the prev user Info, then set success to false

						if (!recoverSavedStreamsUser(this))
						{
							success = false ;
						}
						else
						{

							success = true ;
							String toastMsg =
									"No Network" ;
							Toast.makeText(this,toastMsg,
									Toast.LENGTH_SHORT).show();
						}
					}

				}
				if (!success)
				{
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putString(PrefsUserid, PrefsDefaultString);
					editor.putString(PrefsPassword, PrefsDefaultString);
					editor.commit();

					String abortMsg = "\nError\n" + VerifyCredentialsTask.getErrorMsg() ;
					span = new SpannableString(abortMsg);
					span.setSpan(new RelativeSizeSpan(0.7f), 7, abortMsg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

			}
			else
			{
				span = new SpannableString("\nError\nInvalid email and/or password\n");
				span.setSpan(new RelativeSizeSpan(0.7f), 7, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (!success)
			{
				resultMessage.setText(span);
				resultMessage.setGravity(Gravity.CENTER);

				completeDialog.setView(resultMessage);
				completeDialog.setCancelable(false) ;
				completeDialog.setPositiveButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>OK</></font>"),
						new DialogInterface.OnClickListener() {
					// @SuppressLint("DefaultLocale")
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						mLoginInProgress = false ;
						if ( mAutoLogin)
						{
							mAutoLogin = false ;
							loginScreen() ;
						}
					}
				});

				completeDialog.show() ;
			}


		}
		*/
		if (success)
		{
			// user has successfully logged in, return to MainActivity
			Intent returnIntent = new Intent();

			returnIntent.putExtra(RETURN_STAT_LOGIN, LOGIN_SUCCESSFUL);
			setResult(RESULT_OK,returnIntent);
			// save the successful login info the prefs
			saveSharedPreferences() ;

			overridePendingTransition(R.anim.fade_out,R.anim.fade_in) ;

			mLoginInProgress = false ;
			//recentLogin = true ; // expedite getting streams by updater
			// now inform updater to resume polling
			Intent sendIntent = new Intent(this, UpdaterService.class) ;

			Bundle bundle = new Bundle();

			bundle.putString(RESTART_POLLING,RESTART_POLLING );
			//VerifyCredentialsTask.setStreamsUser(null) ;


			sendIntent.putExtras(bundle);

			startService( sendIntent);

			finish();
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Saves the userid & password as a shared pref.
	 * <p>
	 * Contingent on the "remember me" checkbox
	 *
	 * @param  n/a
	 * @return n/a
	 */
		//}
	void saveSharedPreferences()
	{
		try{
		// save the successful login info the prefs
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		boolean savePrefs = true ;
		if (mRememberMeCheckBox!= null)
		{
			if (!mRememberMeCheckBox.isChecked())
			{
				savePrefs = false ;
			}
		}
		if (savePrefs)
		{
			editor.putString(PrefsUserid, mCurrentUserID);
			editor.putString(PrefsPassword, mCurrentPassword);
		}
		else
		{
			editor.putString(PrefsUserid, PrefsDefaultString);
			editor.putString(PrefsPassword, PrefsDefaultString);
		}
		editor.commit();
		}catch(Exception e){e.printStackTrace();}
	}
	public static  void clearSavedStreamsUser(Context callersActivity)
	{
		try{
		File file = new File(callersActivity.getFilesDir(), VerifyCredentialsTask.getStreamsUserFileName());
		if (file != null)
		{
			file.delete() ;
		}

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(callersActivity);
		Editor editor = sharedPrefs.edit();
		editor.clear();
		editor.commit();
		}catch(Exception e){e.printStackTrace();}
	}

	public static boolean recoverSavedStreamsUser(Context context)
	{
		// get the prefs and check to make sure the versions match
		boolean retVal = false ;
		try{
		if (mStreamsApplication == null)
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","mStreamsApplication is false!") ;
			}
			return false ;
		}
		String programStreamsUserVersionNumber = StreamsApplication.streamsUserVersion ;
		String fileStreamsUserVersionNumber =getSharedPrefs(context).
				getString(PrefsStreamsUserVersion, PrefsDefaultString);

		if (programStreamsUserVersionNumber.compareTo(fileStreamsUserVersionNumber) == 0 )
		{
			BufferedReader reader = null;
			//String fileName = VerifyCredentialsTask.getStreamsUserFileName() ;

			File file = new File(context.getFilesDir(), VerifyCredentialsTask.getStreamsUserFileName());
			try {
				StringBuilder builder = new StringBuilder();
				reader = new BufferedReader(new FileReader(file));
				String line ;
				if (reader != null)
				{
					int lineCount = 0 ;
					line = reader.readLine() ;
					while (line != null  )
					{
						lineCount++ ;
						builder.append(line).append("\n");
						line = reader.readLine() ;
					}
					if (lineCount > 0 )
					{
						Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-DD HH:mm:ss").create();

						StreamsUser streamsUser = gson.fromJson( builder.toString(), StreamsUser.class) ;
						VerifyCredentialsTask.setStreamsUser(streamsUser);

						retVal = true ; 

					}
					setUnverifiedLogin(true) ; // flag that this login may be stale 
					reader.close();
				}
			}catch (IOException e) {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "IOException Error in recoverSavedStreamsUser = " + e ) ;
				}
				e.printStackTrace();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "Exception Error in recoverSavedStreamsUser = " + e ) ;
				}
				e.printStackTrace();
			} 

			finally{
				file = null ; 
			}
		}

		else
		{
			// they don't match clear , dump the file 
			clearSavedStreamsUser(context) ;
			retVal = false ;
		}



		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}


}
