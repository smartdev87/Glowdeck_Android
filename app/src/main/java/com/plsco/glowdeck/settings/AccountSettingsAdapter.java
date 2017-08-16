package com.plsco.glowdeck.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.task.TaskCompleted;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.NewWebAccountActivity;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;
/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: AccountSettingsAdapter.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
 * @author Joe Diamand
 *
 */


/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 *  AccountSettingsAdapter extends BaseAdapter
 *      implements  TaskCompleted
 *
 */
public class AccountSettingsAdapter extends BaseAdapter   implements TaskCompleted {
	//
	// Globals
	//
	String [] 	mResult;
	Context 	mContext;
	int [] 		mImageId;
	int [] 		mImageId2;
	String 		mStreamAccountType ;
	int 		mNumberAccounts ; 
	LayoutInflater mInflater=null;
	ProgressDialog mProgressDialog ;
	//
	// Constants
	//
	final static String GMAIL_HTTP_PREFIX = "http://glowdeck.com/test/gmail/?mobile=1" ; 
	final static String INSTAGRAM_HTTP_PREFIX = "http://glowdeck.com/test/instagram/?mobile=1" ;
	final static String TWITTER_HTTP_PREFIX = "http://glowdeck.com/test/twitter/?mobile=1" ; 
	//final static String GMAIL_HTTP_PREFIX = "http://streams.io/test/gmail/?mobile=1" ; 
	//final static String INSTAGRAM_HTTP_PREFIX = "http://streams.io/test/instagram/?mobile=1" ;
	//final static String TWITTER_HTTP_PREFIX = "http://streams.io/test/twitter/?mobile=1" ; 
	public final static String OAUTH_URL  = "OAUTH_URL" ;  
	public static final String STREAM_TYPE = "STREAM_TYPE" ;
	public static final int RESULT_CODE_NEW_ACCOUNT = 21 ; 
	public static final String INTENT_EXTRA_NEW_ACCOUNT = "INTENT_EXTRA_NEW_ACCOUNT" ; 
	//
	//   Globals (static)
	//
	static boolean mOnClickNewAccountGuardRowView = false; 
	static boolean mOnClickDelGuardRowView = false; 

	//
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResult.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView ;
		try{
		if (rowView == null)
		{

			rowView = mInflater.inflate(R.layout.activity_individual_setting_listview, null);
			Holder holder=new Holder();
			holder.tv=(TextView) rowView.findViewById(R.id.textview_ind_set);

			holder.img=(ImageView) rowView.findViewById(R.id.icon_indset); 
			holder.img2=(ImageView) rowView.findViewById(R.id.icon_indset_del); 
			rowView.setTag(holder);
		}
		Holder holder = (Holder) rowView.getTag();

		final int fPosition = position ; 

		if (mNumberAccounts > 0)
		{ // support ability to  delete account, if one exists
			holder.img2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnClickDelGuardRowView)
					{
						return ;
					}
					else
					{
						mOnClickDelGuardRowView = true ;
					}
					AlertDialog.Builder completeDialog = new AlertDialog.Builder(mContext);

					Spannable span = new SpannableString("\nRemove Item\nAre you sure you want to remove\n"
							+ mResult[fPosition] + "?");
					int spanLen = span.length();

					span.setSpan(new RelativeSizeSpan(0.7f), 13, spanLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					TextView resultMessage = new TextView(mContext);
					resultMessage.setTextSize(18);
					resultMessage.setText(span);
					resultMessage.setGravity(Gravity.CENTER);
					completeDialog.setView(resultMessage);
					completeDialog.setCancelable(false) ;
					completeDialog.setPositiveButton(Html.fromHtml(
							"<font  color=\"#0088ff\"><b>Yes</></font>"),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							//deleteTaskInProgress = true ;
							removeAccount(   fPosition) ;
							mOnClickDelGuardRowView = false  ;
							dialog.dismiss();

						}
					});

					completeDialog.setNegativeButton(Html.fromHtml(
							"<font  color=\"#0088ff\"><b>Cancel</></font>"),
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							mOnClickDelGuardRowView = false ;
							dialog.dismiss();

						}
					});

					completeDialog.show() ;



				}
			});
		}

		holder.tv.setText(mResult[position]);


		holder.img.setImageResource(mImageId[position]);
		if (mImageId2[position] == -1)
		{
			holder.img2.setVisibility(View.INVISIBLE) ;
		}
		else
		{
			holder.img2.setImageResource(mImageId2[position]);
			holder.img2.setVisibility(View.VISIBLE) ;
		}

		if (mNumberAccounts == 0)
			// no accounts, add ability to add one account
		{
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {


					if (mOnClickNewAccountGuardRowView)
					{
						return ;
					}
					else
					{
						mOnClickNewAccountGuardRowView = true ;
					}

					addNewAccount() ;


				}
			});
		}
		}catch(Exception e){e.printStackTrace();}
		return rowView;
	}

	/**
	 *
	 */
	void addNewAccount()
	{

		try{
		String requestOauth = null ;

		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		String userId = null ;
		String token  = null ;
		if (streamsUser != null)
		{
			StreamsAccount streamsAccount = streamsUser.getStreamsAccount() ;
			if (streamsAccount != null)
			{
				userId = streamsAccount.getID() ;
				token  = streamsAccount.getToken() ;

			}
		}



		if (mStreamAccountType.compareToIgnoreCase("instagram")==0)
		{

			requestOauth = INSTAGRAM_HTTP_PREFIX + "&UserID=" + userId +
					"&Token=" + token  ;

		}
		if (mStreamAccountType.compareToIgnoreCase("email")==0)
		{

			requestOauth = GMAIL_HTTP_PREFIX + "&UserID=" + userId +
					"&Token=" + token  ;

		}
		if (mStreamAccountType.compareToIgnoreCase("twitter")==0)
		{

			requestOauth = TWITTER_HTTP_PREFIX + "&UserID=" + userId +
					"&Token=" + token  ;

		}

		newAccountTaskCompleted(requestOauth)  ;
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param position - currently only support 1 account (position 0 )
	 */
	void removeAccount( int position)
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		String[] accounts  = null;
		if ( mStreamAccountType.compareTo(SettingsTopFragment.EMAIL_STREAM_TYPE) == 0)
		{
			accounts = streamsUser.getEmail().getAccount() ;
			// dont do this just yet ... only one account support for now
			/*
			mStreamsUser.getModifiedSettings().setEmailSettingsModified(true) ;
			 */
		}

		if ( mStreamAccountType.compareTo(SettingsTopFragment.TWITTER_STREAM_TYPE) == 0)
		{
			accounts = streamsUser.getTwitter().getAccount() ;
			// dont do this just yet
			/*
			mStreamsUser.getModifiedSettings().setTwitterSettingsModified(true) ;
			 */
		}
		if ( mStreamAccountType.compareTo(SettingsTopFragment.INSTAGRAM_STREAM_TYPE) == 0)
		{
			accounts = streamsUser.getInstagram().getAccount() ;
			// dont do this just yet
			/*
			mStreamsUser.getModifiedSettings().setTwitterSettingsModified(true) ;
			 */
		}
		if ( position == 0 )
		{
			if (( accounts != null) && (accounts.length == 1 ) )
			{
				// currently this is the only configuration supported
				// Normally we would copy the array,excluding the deleted member,
				// see the news keywords in the parallel adapter.
				// soon will need to support multiple email accounts ...

				//StreamsUser mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
				mImageId[0] =  R.drawable.plus_icon ;
				mImageId2[0] = -1 ;
				if ( mStreamAccountType.compareTo(SettingsTopFragment.EMAIL_STREAM_TYPE) == 0)
				{
					mResult[0] =  "Add Email account" ;
					// delete the account from the userInfo
					streamsUser.getEmail().setAccount(new String[0]) ;
					streamsUser.getEmail().setEnabled("0") ;
					mNumberAccounts = 0 ;
				}
				if ( mStreamAccountType.compareTo(SettingsTopFragment.TWITTER_STREAM_TYPE) == 0)
				{
					mResult[0] =  "Add Twitter account" ;
					// delete the account from the userInfo
					streamsUser.getTwitter().setAccount(new String[0]) ;
					streamsUser.getTwitter().setEnabled("0") ;
					mNumberAccounts = 0 ;
				}

				if ( mStreamAccountType.compareTo(SettingsTopFragment.INSTAGRAM_STREAM_TYPE) == 0)
				{
					mResult[0] =  "Add Instagram account" ;
					// delete the account from the userInfo
					streamsUser.getInstagram().setAccount(new String[0]) ;
					streamsUser.getInstagram().setEnabled("0") ;
					mNumberAccounts = 0 ;
				}


				String[] parmsArray = {mStreamAccountType} ;
				new SendDeleteAccountRequest(this).execute(parmsArray) ;
				mProgressDialog=new ProgressDialog(mContext,R.style.SpinnerTheme);  


				mProgressDialog.setCancelable(false);

				mProgressDialog.setIcon(R.drawable.streams_beta)  ;

				mProgressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar) ;    

				mProgressDialog.show() ;

			}
		}
		}catch(Exception e){e.printStackTrace();}
	}



	/**
	 * Constructor
	 * 
	 * @param individualSettingsActivity - the calling contecxt
	 * @param accountTypeParm - initialize the global account type string
	 * @param numAccountsParm - initialize the global number of accounts int
	 * @param prgmNameList - used to intialize the results strings
	 * @param prgmImages - icon1
	 * @param prgmImages2 - icon2
	 */
	public AccountSettingsAdapter(IndividualSettingsActivity individualSettingsActivity, 
			String accountTypeParm,
			int    numAccountsParm,
			String[] prgmNameList, 
			int[] prgmImages,
			int[] prgmImages2) {
		mResult=prgmNameList;
		mContext=individualSettingsActivity;
		mImageId=prgmImages;
		mImageId2=prgmImages2;
		mStreamAccountType = accountTypeParm;
		mNumberAccounts = numAccountsParm ; 
		mInflater = ( LayoutInflater )mContext.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	public class Holder
	{
		TextView tv;
		ImageView img;
		ImageView img2 ;
	}

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.TaskCompleted#onTaskComplete(java.lang.Boolean)
	 */
	@Override
	public void onTaskComplete(Boolean resultVal) {
		// TODO Auto-generated method stub
		try{
		if (mProgressDialog != null)
		{
			mProgressDialog.cancel() ;
			mProgressDialog = null ; 
		}

		deleteAccountTaskCompleted(resultVal) ; 
		}catch(Exception e){e.printStackTrace();}


	}
	/**
	 * @param resultVal
	 */
	void deleteAccountTaskCompleted(boolean resultVal)
	{
		try{
		if (resultVal)
		{


			AccountSettingsAdapter accountSettingsAdapter =
					(AccountSettingsAdapter)IndividualSettingsActivity.mIndividualSettingsLV.getAdapter() ;
			accountSettingsAdapter.notifyDataSetChanged();
			SettingsTopFragment.settingsAdapter.notifyDataSetChanged() ;
		}
		else
		{
			AlertDialog.Builder completeDialog = new AlertDialog.Builder(mContext);
			TextView resultMessage = new TextView(mContext);
			resultMessage.setTextSize(18);
			//Typeface tf = Typeface.DEFAULT_BOLD ;


			String dialogMsgStr = 	"\n   Error  \n        Account Delete Failed         \n" ;		

			Spannable span = new SpannableString(dialogMsgStr); 

			span.setSpan(new RelativeSizeSpan(0.7f), 11, span.length()   , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param url
	 */
	void newAccountTaskCompleted(String url ) 
	{


		try{
		if (url == null)
		{
			return ; 
		}

		Intent intent = new Intent(mContext, NewWebAccountActivity.class);
		intent.putExtra(STREAM_TYPE,mStreamAccountType);
		intent.putExtra(OAUTH_URL,url);
		((Activity) mContext).startActivityForResult(intent,RESULT_CODE_NEW_ACCOUNT);
		((Activity) mContext).
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		mOnClickNewAccountGuardRowView = false ;
		}catch(Exception e){e.printStackTrace();}
	}
		
}