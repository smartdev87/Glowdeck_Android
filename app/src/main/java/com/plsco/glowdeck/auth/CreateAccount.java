package com.plsco.glowdeck.auth;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.plsco.glowdeck.task.RegisterNewStreamsAccount;
import com.plsco.glowdeck.task.TaskCompleted;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.FormsPersistentData;
import com.plsco.glowdeck.R;

/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: CreateAccount.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * The CreateAccount() mActivity support the creation of new accounts for the glowdeck system
 * The user is presented with a form that requires filling in the fields
 * first name, last name, email, password (and confirm)
 * 
 *  TODO communicate create account request with server and complete the functionality of this mActivity
 *  
 *
 */
public class CreateAccount extends Activity implements OnItemClickListener, TaskCompleted {


	// Persistent com.glowdeck.streams.streamdata for the create account form
	//
	// used to save username(email), firstname, lastname on form to restore if user 
	// navigates away and then back
	FormsPersistentData formsPersistenceData = FormsPersistentData.getInstance() ;

	//
	// Globals
	//
	private EditText mEmailEditText = null; 
	private EditText mFirstNameEditText = null ;
	private EditText mLastNameEditText = null ;
	private EditText mPasswordEditText = null ;
	private EditText mPasswordConfirmEditText = null ;
	private ProgressDialog mProgressDialog = null ;
	private String mErrorMessageTryingToCreateAcct = "" ;  
	//
	//
	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		setTitle("Create Account");
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_create_account);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// get the fields for the create account screen
		mEmailEditText = (EditText) findViewById(R.id.create_account_userid_edit);
		mFirstNameEditText = (EditText) findViewById(R.id.create_account_first_name_edit);
		mLastNameEditText  = (EditText) findViewById(R.id.create_account_last_name_edit);
		mPasswordEditText  =   (EditText) findViewById(R.id.create_account_password_edit);
		mPasswordConfirmEditText =   (EditText) findViewById(R.id.create_account_confirm_password_edit);
		/*
		 *  setOnClickListener() 
		 *  called when user clicks the create account button
		 *  
		 *  calls registerNewUser()
		 *  
		 *  
		 */
		findViewById(R.id.create_account_register_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newUserForm();
			}
		});
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume() ; 
		try{
		restoreFormsPrefs() ;
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * When user hits the return button, return to the login screen 
	 * <p>
	 */
	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{    //  return to the calling mActivity: the login screen

		//NavUtils.navigateUpFromSameTask(this);
		super.onBackPressed() ;
		try{
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	public void onStop()
	{
		super.onStop() ;
		// save all the information, including passwords 
		try{
		saveFormPrefs() ; 
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * 
	 */
	private void saveFormPrefs()
	{
		try{
		if (formsPersistenceData != null)
		{

			formsPersistenceData.setCreateAcctEmailEdit(mEmailEditText.getText().toString()) ;
			formsPersistenceData.setCreateAcctFirstNameEdit(mFirstNameEditText.getText().toString()) ;
			formsPersistenceData.setCreateAcctLastNameEdit(mLastNameEditText.getText().toString()) ;
			formsPersistenceData.setCreateAcctPasswordEdit(mPasswordEditText.getText().toString()) ;
			formsPersistenceData.setCreateAcctPasswordConfirmEdit(mPasswordConfirmEditText.getText().toString()) ;

		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * 
	 */
	private void restoreFormsPrefs()
	{
		try{
		String emailEditTextPrev  = "" ;
		String firstNameEditTextPrev  = "" ;
		String lastNameEditTextPrev  = "" ;
		String passwordEditTextPrev  = "" ;
		String passwordConfirmEditTextPrev  = "" ;

		if (formsPersistenceData != null)
		{
			emailEditTextPrev = formsPersistenceData.getCreateAcctEmailEdit() ;
			firstNameEditTextPrev = formsPersistenceData.getCreateAcctFirstNameEdit() ;
			lastNameEditTextPrev = formsPersistenceData.getCreateAcctLastNameEdit() ;
			passwordEditTextPrev = formsPersistenceData.getCreateAcctPasswordEdit() ;
			passwordConfirmEditTextPrev = formsPersistenceData.getCreateAcctPasswordConfirmEdit() ;
		}

		boolean cursorFreeze = false ; 
		if (mEmailEditText != null)
		{
			mEmailEditText.requestFocus();
		}
		if (emailEditTextPrev.length() > 0)
		{
			if (mEmailEditText != null)
			{
				mEmailEditText. setText(emailEditTextPrev) ;
				formsPersistenceData.setCreateAcctEmailEdit("") ;
				if (mFirstNameEditText != null)
				{
					mFirstNameEditText.requestFocus();
				}
			}
		}
		else
		{
			cursorFreeze = true ; 
		}
		if (firstNameEditTextPrev.length() > 0)
		{
			if (mFirstNameEditText != null)
			{
				mFirstNameEditText.setText(firstNameEditTextPrev) ;

				if ( (mLastNameEditText != null) && (!cursorFreeze) )
				{
					mLastNameEditText.requestFocus();
				}
			}
		}
		else
		{
			cursorFreeze = true ;
		}
		if (lastNameEditTextPrev.length() > 0)
		{
			if (mLastNameEditText != null)
			{
				mLastNameEditText. setText(lastNameEditTextPrev) ;

				if (( mPasswordEditText != null) && (!cursorFreeze) )
				{
					mPasswordEditText.requestFocus();
				}
			}
		}

		if (passwordEditTextPrev.length() > 0)
		{
			if (mPasswordEditText != null)
			{
				mPasswordEditText. setText(passwordEditTextPrev) ;

				if (( mPasswordEditText != null) && (!cursorFreeze) )
				{
					mPasswordEditText.requestFocus();
				}
			}
		}

		if (passwordConfirmEditTextPrev.length() > 0)
		{
			if (mPasswordConfirmEditText != null)
			{
				mPasswordConfirmEditText. setText(passwordConfirmEditTextPrev) ;

				if (( mPasswordConfirmEditText != null) && (!cursorFreeze) )
				{
					mPasswordConfirmEditText.requestFocus();
				}
			}
		}
		}catch(Exception e){e.printStackTrace();}

	}
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			try{
			saveFormPrefs() ; 
			//NavUtils.navigateUpFromSameTask(this);
			//overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			onBackPressed() ;

			}catch(Exception e){e.printStackTrace();}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 
	 */
	void newUserForm()
	{
		try{
		AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
		TextView resultMessage = new TextView(this);
		resultMessage.setTextSize(18);
		
		String dialogMsgStr = null;
		String fillInAllFieldsStr = 		"\nTry Again\nYou must fill in all fields to register\n" ;
		String passNotMatchStr = 	"\n  Error  \n        Passwords do not match         \n" ;

		if ( (mPasswordEditText.getText().length()==0) || (mPasswordConfirmEditText.getText().length()==0) ||
				(mEmailEditText.getText().length()==0) || (mFirstNameEditText.getText().length()==0) ||
				(mLastNameEditText.getText().length()==0) )
		{
			dialogMsgStr = fillInAllFieldsStr ;
		}
		else
		{
			if (mPasswordEditText.getText().toString().compareTo(mPasswordConfirmEditText.getText().toString()) != 0 )
			{
				dialogMsgStr = passNotMatchStr ;
			}

			else
			{
				registerTheUser() ;
				return ; 
			}
		}			

		Spannable span = new SpannableString(dialogMsgStr); 

		span.setSpan(new RelativeSizeSpan(0.7f), 11, 50, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		resultMessage.setText(span);
		resultMessage.setGravity(Gravity.CENTER);
		completeDialog.setView(resultMessage);
		completeDialog.setCancelable(false) ;
		completeDialog.setPositiveButton(Html.fromHtml(
				"<font  color=\"#0088ff\"><b>OK</></font>"), 
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();               
			}
		});

		completeDialog.show() ;
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * 
	 */
	void registerTheUser()
	{
		try{
		mProgressDialog=new ProgressDialog(this,R.style.SpinnerTheme);  // R.style.SpinnerTheme)


		mProgressDialog.setCancelable(false);

		mProgressDialog.setIcon(R.drawable.streams_beta)  ;

		mProgressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar) ;      //          android.R.style.Widget_ProgressBar_Small);

		mProgressDialog.show() ;
		RegisterNewStreamsAccount registerNewStreamsAccount = new RegisterNewStreamsAccount(this)  ;

		registerNewStreamsAccount.execute(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString(),
				mFirstNameEditText.getText().toString(),mFirstNameEditText.getText().toString() ) ;
		}catch(Exception e){e.printStackTrace();}

	}

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.TaskCompleted#onTaskComplete(java.lang.Boolean)
	 */
	@Override
	public void onTaskComplete(Boolean result) {
		// TODO Auto-generated method stub
		try{
		if (mProgressDialog != null)
		{
			mProgressDialog.dismiss() ;
			mProgressDialog = null ; 
		}

		if (!result)
		{

			AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
			Spannable span  = new SpannableString("\nError\n" + mErrorMessageTryingToCreateAcct + "\n");
			TextView resultMessage = new TextView(this);
			resultMessage.setTextSize(18);

			int spanLen = span.length() ;
			span.setSpan(new RelativeSizeSpan(0.7f), 6, spanLen , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			resultMessage.setText(span);
			resultMessage.setGravity(Gravity.CENTER);
			completeDialog.setView(resultMessage);
			completeDialog.setCancelable(false) ;
			completeDialog.setPositiveButton(Html.fromHtml(
					"<font  color=\"#0088ff\"><b>OK</></font>"), 
					new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss(); 

				}
			});

			completeDialog.show() ;
		}
		else
		{
			// go to the modified profile screen to complete the 
			// new account creation process
			VerifyCredentialsTask.getStreamsUser().setCreateNewUserInProgress(true);
			finish() ; 
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param theErrMsg
	 */
	public void setServerErrorMsg(String theErrMsg)
	{
		try{
		if (theErrMsg != null)
		{
			mErrorMessageTryingToCreateAcct = theErrMsg ; 
		}
		}catch(Exception e){e.printStackTrace();}
	}

}
