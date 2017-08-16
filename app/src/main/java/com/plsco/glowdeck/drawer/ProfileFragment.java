

package com.plsco.glowdeck.drawer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.orders.OrdersActivity;
import com.plsco.glowdeck.task.TaskCompleted;
import com.plsco.glowdeck.task.UpdateProfileTask;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount.OrdersClass;
import com.plsco.glowdeck.task.LoadingTask.LoadingTaskFinishedListener;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * FileName:  ProfileFragment.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *   
 */

/**
 * History
 * Prepare for Google Play Store 11/1/14
 */


/**
 * This mCurrentFragment is displayed when the user selects the "Profile" 
 * menu item.
 * 
 * User can update their profile, then click the update button, and 
 * the changes are sent to server over network
 * Progress bar-(circle) is displayed during transmission 
 * If an error occurs a dialog is displayed
 * Upon success we stay on the page. 
 *  
 *
 */

/**
 * ProfileFragment extends Fragment
 * implements LoadingTaskFinishedListener and TaskCompleted
 *
 */
public class ProfileFragment extends Fragment  implements   LoadingTaskFinishedListener , TaskCompleted
{

	//globals (final)
	final ProfileFragment mProfileFragment = this;
	//globals 
	EditText mPhoneNumberEditView ;
	EditText mAddressOneEditView  ;
	EditText mAddressTwoEditView  ;
	EditText mAddressThreeEditView ;
	EditText mCityEditView ;
	EditText mFirstNameEditview ;
	EditText mLastNameEditview ;
	TextView mStateDescriptionTextView ; 
	Spinner mCountrySpinner ;
	Spinner mStateSpinner ;
	StreamsUser mStreamsUser ;
	StreamsAccount mStreamsAccount ;
	EditText mPostalCodeEditView ;
	ProgressDialog mProgressDialog = null ;
	View mRootView ;
	StreamsApplication mStreamsApplication = null;
	//
	// globals (static)
	static boolean msUpdateInProgress = false ; // prevent button being pressed multiple times
	static ArrayList<LocCodeSubLoc> msSubLocationNamesFullList = null ;
	static ArrayAdapter<String> msCountryAdapter = null ;
	static ArrayAdapter<String> msStateAdapter = null ;
	static String msCountryCodePrevious = "" ; 
	static ArrayList<String> msCountriesUNECE     = null ;
	static ArrayList<String> msCountriesCodesUNECE = null ;


	// constants
	private static final String PLEASE_SELECT_COUNTRY =  "... Please select a country" ;
	private static final String PLEASE_SELECT_STATE  =  "... Please select a msState" ;
	private static final String  CHAR_SET = "ISO-8859-1";  
	private static final String PROFILE_OK = "OK" ; 
	private static final String COUNTRY_US = "US" ; 
	private static final String STATE_NEW_YORK = "New York" ;

	/**
	 * Constructor
	 */
	public ProfileFragment()
	{

	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 try{
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }catch(Exception e){e.printStackTrace();}
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","ProfileFragment:onActivityCreated") ;
		}

		super.onActivityCreated(savedInstanceState);
		try{
		mStreamsApplication = (StreamsApplication)this.getActivity().getApplication() ;
		}catch(Exception e){e.printStackTrace();}
	}
	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","ProfileFragment:onResume") ;
		}
		super.onResume();
	}
	/* (non-Javadoc)
	 * @see android.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","ProfileFragment:onStart") ;
		}
		super.onStart();
	}



	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try{
		mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
		mStateSpinner = (Spinner) mRootView.findViewById(R.id.state_spinner);
		mCountrySpinner = (Spinner) mRootView.findViewById(R.id.country_spinner);
		mStateDescriptionTextView = (TextView) mRootView.findViewById(R.id.state_textview);
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","ProfileFragment:onCreateView") ;
		}
		TextView userID_tv = (TextView) mRootView.findViewById(R.id.profile_userid_textview);

		userID_tv.setText(LoginActivity.getCurrentUserID()) ;

		mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;

		if (mStreamsUser == null)
		{

			Activity thisActivity = this.getActivity() ; 
			StreamsApplication streamsApplication = (StreamsApplication)thisActivity.getApplication() ;
			LoginActivity.setStreamsApplication(streamsApplication) ;
			LoginActivity.recoverSavedStreamsUser(thisActivity) ;
			mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
			if (mStreamsUser == null)
			{

				return mRootView ; 
			}
		}


		mStreamsAccount = mStreamsUser.getStreamsAccount() ;
		if (mStreamsAccount == null)
		{
			return mRootView ; 
		}


		initializeCountrySpinner() ; 


		initializeStateSpinner(COUNTRY_US) ; 


		mCountrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","Clicked onItemSelected at position:" + position + ", country code : " + msCountriesCodesUNECE.get(position)) ;
				}
				mCountrySpinner.showContextMenu() ;
				initializeStateSpinner(msCountriesCodesUNECE.get(position)) ; 
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

				mCountrySpinner.showContextMenu() ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","Clicked onNothingSelected") ;
				}
			}

		});


		mStateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				mStateSpinner.showContextMenu() ;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mStateSpinner.showContextMenu() ;
			}



		});





		userID_tv.setText(mStreamsAccount.getEmail()) ;

		TextView orders_textview =   (TextView) mRootView.findViewById(R.id.profile_orders_edittext);
		if (orders_textview != null)
		{
			ArrayList<OrdersClass> orders = mStreamsAccount.getOrders() ;
			int numberOfOrders = 0 ;
			if (orders != null)
			{
				numberOfOrders = mStreamsAccount.getOrders().size() ;
			}
			//Toast.makeText(this.getActivity(), mStreamsAccount.getOrders().size(), Toast.LENGTH_SHORT).show();
			String ordersString = "Orders (" +   numberOfOrders  + ")" ;
			orders_textview.setText(ordersString) ;
		}



		mFirstNameEditview =  (EditText) mRootView.findViewById(R.id.first_name_edittext);
		if (mFirstNameEditview != null)
		{
			String firstName = mStreamsAccount.getFirstName() ;
			mFirstNameEditview.setText(firstName) ;
		}

		mLastNameEditview =  (EditText) mRootView.findViewById(R.id.last_name_editText);
		if (mLastNameEditview != null)
		{

			String lastName = mStreamsAccount.getLastName() ;
			mLastNameEditview.setText(lastName) ;
		}
		mAddressOneEditView = (EditText) mRootView.findViewById(R.id.address_one_editText) ;
		mAddressTwoEditView = (EditText) mRootView.findViewById(R.id.address_two_editText) ;
		mAddressThreeEditView = (EditText) mRootView.findViewById(R.id.address_three_editText) ;

		if ( (mAddressOneEditView != null) && 
				(mAddressTwoEditView != null) && 
				(mAddressThreeEditView != null) )
		{
			mAddressOneEditView.setText(mStreamsAccount.getAddress_1()) ;
			mAddressTwoEditView.setText(mStreamsAccount.getAddress_2()) ;
			mAddressThreeEditView.setText(mStreamsAccount.getAddress_3()) ;
		}

		mCityEditView = (EditText) mRootView.findViewById(R.id.city_editText) ;

		if ( mCityEditView != null)  
		{
			mCityEditView.setText(mStreamsAccount.getCity()) ;
		}

		mPostalCodeEditView = (EditText) mRootView.findViewById(R.id.postalcode_editText) ;
		if ( mPostalCodeEditView != null)  
		{
			mPostalCodeEditView.setText(mStreamsAccount.getZipCode()) ;
		}


		mPhoneNumberEditView = (EditText) mRootView.findViewById(R.id.phone_editText) ;
		if ( mPhoneNumberEditView != null)  
		{
			mPhoneNumberEditView.setText(mStreamsAccount.getPhone()) ;
		}


		orders_textview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// generated update string
				ArrayList<OrdersClass> orders = mStreamsAccount.getOrders() ;
				int numOrders = 0 ;
				if (orders != null)
				{
					numOrders = mStreamsAccount.getOrders().size() ;
				}
				if (numOrders >= 0 ) // show even empty orrders 
				{
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg","ProfileFragment:clicked on the com.glowdeck.streams.orders Button") ;
					}

					Intent intent = new Intent(mProfileFragment.getActivity(), OrdersActivity.class);
					startActivity(intent);
					mProfileFragment.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

				}
			}
		});


		Button updateButton = (Button) mRootView.findViewById(R.id.profile_update_button);


		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// generated update string

				if (mRootView == null)
				{
					return ; 
				}
				if (msUpdateInProgress)
				{
					return ;
				}
				msUpdateInProgress = true ; 
				String isOK = profileValueValid() ;
				if (isOK.compareTo(PROFILE_OK) != 0)
				{ // something is missing 
					AlertDialog.Builder needMoreInfoDialog = new AlertDialog.Builder(mProfileFragment.getActivity());
					Spannable span = new SpannableString("\nError\n" +
							isOK);  
					TextView resultMessage = new TextView(mProfileFragment.getActivity());
					resultMessage.setTextSize(18);
					int spanStrLength = span.length() ;
					span.setSpan(new RelativeSizeSpan(0.7f), 7, spanStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					resultMessage.setText(span);
					resultMessage.setGravity(Gravity.CENTER);
					needMoreInfoDialog.setView(resultMessage);
					needMoreInfoDialog.setCancelable(false) ;
					needMoreInfoDialog.setPositiveButton(Html.fromHtml(
							"<font  color=\"#0088ff\"><b>OK</></font>"), 
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss(); 
							msUpdateInProgress = false ; 
						}
					});

					needMoreInfoDialog.show() ;
				}
				else
				{
					mProgressDialog=new ProgressDialog(mRootView.getContext(),R.style.SpinnerTheme);  


					mProgressDialog.setCancelable(false);

					mProgressDialog.setIcon(R.drawable.streams_beta)  ;

					mProgressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar) ;     

					mProgressDialog.show() ;			

					String updateRequest = profileHasChanged( mStreamsAccount) ;

					UpdateProfileTask updateProfileTask = new UpdateProfileTask(mProfileFragment)  ;//

					updateProfileTask.execute(updateRequest) ;
				}

			}
		});
		// now check to see if we are in the final phase of defining 
		//  a new user

		if (mStreamsUser.isCreateNewUserInProgress() )
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","ProfileFragment::Create Account is in Progress");
			}
			AlertDialog.Builder completeDialog = new AlertDialog.Builder(this.getActivity());
			Spannable span = new SpannableString("\nSTEP 2\nPlease enter your shipping information\n" +
					"Make sure all fields are accurate, as\n" +
					"this is the address we will use to ship\n" + 
					"your orders.");  
			TextView resultMessage = new TextView(this.getActivity());
			resultMessage.setTextSize(18);
			int spanStrLength = span.length() ;
			span.setSpan(new RelativeSizeSpan(0.7f), 7, spanStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","ProfileFragment::Create Account is NOT in Progress");
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return mRootView;
	}
	/**
	 * @return
	 */
	String profileValueValid()
	{
		String retVal = PROFILE_OK ;

		try{
		if (mCityEditView.getText().toString().length() == 0 )
		{
			retVal = "Please enter a city." ;
		}
		int addrSize = mAddressOneEditView.getText().toString().length() +
				mAddressTwoEditView.getText().toString().length() +
				mAddressThreeEditView.getText().toString().length() ;
		if (addrSize == 0)
		{
			retVal =  "Please enter an address." ;
		}

		int nameLength = mFirstNameEditview.getText().toString().length() + 
				mLastNameEditview.getText().toString().length()  ;
		if (nameLength == 0)
		{
			retVal = "Please provide a name." ;
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	/**
	 * @param mStreamsAccount
	 * @return
	 */
	String profileHasChanged(StreamsAccount streamsAccount)
	{
		boolean updatedRequired = false ; // set if a change detected
		String prefix1 = "UserID=" + streamsAccount.getID() + "&Request={\"Streams Account\":{\"Phone\":" ;
		String updateRequest = null ;
		if (mPhoneNumberEditView.getText().toString().compareTo(
				streamsAccount.getPhone()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest = prefix1 + "\"" + mPhoneNumberEditView.getText() + "\"," ;
		String prefixAddr = "\"Address_3\":" ;
		if (mAddressThreeEditView.getText().toString().compareTo(
				streamsAccount.getAddress_3()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += prefixAddr + "\"" + mAddressThreeEditView.getText().toString() + "\"," ;


		prefixAddr = "\"Address_1\":" ;
		if (mAddressOneEditView.getText().toString().compareTo(
				streamsAccount.getAddress_1()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += prefixAddr + "\"" + mAddressOneEditView.getText().toString() + "\"," ;



		String cityState = "\"City\":" ;
		if (mCityEditView.getText().toString().compareTo(
				streamsAccount.getCity()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += cityState + "\"" + mCityEditView.getText().toString() + "\"," ;

		String name = "\"First Name\":" ;
		if (mFirstNameEditview.getText().toString().compareTo(
				streamsAccount.getFirstName()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += name + "\"" + mFirstNameEditview.getText().toString() + "\"," ;

		prefixAddr = "\"Address_2\":" ;
		if (mAddressTwoEditView.getText().toString().compareTo(
				streamsAccount.getAddress_2()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += prefixAddr + "\"" + mAddressTwoEditView.getText().toString() + "\"," ;	

		cityState = "\"State\":" ;
		int statePos = mStateSpinner.getSelectedItemPosition() ;

		String stateNameString = "" ;
		if (statePos != ListView.INVALID_POSITION)
		{
			stateNameString = msStateAdapter.getItem(statePos);
			if (stateNameString.compareTo(PLEASE_SELECT_STATE) == 0)
			{
				stateNameString="" ; 
			}
		}
		if ( stateNameString.compareTo(
				streamsAccount.getCountry()) != 0)
		{
			updatedRequired = true ;
		}



		updateRequest += cityState + "\"" + stateNameString + "\"," ;

		name = "\"Last Name\":" ;
		if (mLastNameEditview.getText().toString().compareTo(
				streamsAccount.getLastName()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += name + "\"" + mLastNameEditview.getText().toString() + "\"," ;

		String country = "\"Country\":" ;
		int countryPos = mCountrySpinner.getSelectedItemPosition() ;
		String countryNameString = "" ; 
		if (countryPos != ListView.INVALID_POSITION)
		{
			countryNameString = msCountryAdapter.getItem(countryPos);
			if (countryNameString.compareTo(PLEASE_SELECT_COUNTRY) == 0)
			{
				countryNameString="" ; 
			}
		}
		if ( countryNameString.compareTo(
				streamsAccount.getCountry()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += country + "\"" + countryNameString + "\"," ;

		String zip = "\"ZipCode\":" ;
		if ( mPostalCodeEditView.getText().toString().compareTo(
				streamsAccount.getZipCode()) != 0)
		{
			updatedRequired = true ;
		}
		updateRequest += zip + "\"" + mPostalCodeEditView.getText().toString() + "\"}}&Token=" 
				+ streamsAccount.getToken() ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg",updateRequest) ;
		}
		if (!updatedRequired)
		{
			return null ;
		}
		else
		{
			return updateRequest ; 
		}

	}



	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.LoadingTask.LoadingTaskFinishedListener#onTaskFinished()
	 */
	@Override
	public void onTaskFinished() {
		// TODO Auto-generated method stub
		try{
		mProgressDialog.dismiss() ;
		msUpdateInProgress = false  ; 
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see com.glowdeck.streams.task.TaskCompleted#onTaskComplete(java.lang.Boolean)
	 */
	@Override
	public void onTaskComplete(Boolean result) {
		try{
		boolean success = true ;  
		if (!result.booleanValue() )
		{   // update profile failed	
			Spannable span = null ;
			TextView resultMessage = new TextView(mRootView.getContext());
			resultMessage.setTextSize(18);
			//Typeface tf = Typeface.DEFAULT_BOLD ;
			AlertDialog.Builder completeDialog = new AlertDialog.Builder(mRootView.getContext());
			success = false ; 
			// failure to update occurred either because of: 
			// 1) either because the server said the request was invalid, or
			// 2) an (JSON) error occurred in parsing the server response, or
			// 3) a network error occurred ( network is temp unavailable)
			// first check if error occurred in VerifyCredentials
			if (UpdateProfileTask.didUpErrorOccur())
			{	
				// yes, error did occur
				// check to see if the error was due to the network
				if (UpdateProfileTask.isUpNetworkUnavailable())
				{ // yes, we failed on network availability
					String abortMsg = "\nError\n" + UpdateProfileTask.getUpErrorMsg() ;
					span = new SpannableString(abortMsg);
					span.setSpan(new RelativeSizeSpan(0.7f), 7, abortMsg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				}
				else
				{
					span = new SpannableString("\nError\nInvalid server request reject\n");                                    
					span.setSpan(new RelativeSizeSpan(0.7f), 7, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				resultMessage.setText(span);
				resultMessage.setGravity(Gravity.CENTER);

				completeDialog.setView(resultMessage);

				completeDialog.setPositiveButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>OK</></font>"), 
						new DialogInterface.OnClickListener() {
					// @SuppressLint("DefaultLocale")
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();  

					}
				});

				completeDialog.show() ;
			}


		}



		if (success)
		{
			// user has successfully updated
			// if not completing a create, then stay on the same screen
			// else, show dialog for creatuser complete

			if (mStreamsUser.isCreateNewUserInProgress() )
			{
				mStreamsUser.setCreateNewUserInProgress(false) ; 
				AlertDialog.Builder completeDialog = new AlertDialog.Builder(this.getActivity());
				Spannable span = new SpannableString("\nSuccess\nYou have sucessfully created a\n" +
						"Streams account. To update your\n" +
						"shipping address, click Profile menu at\n" + 
						"any time.\n\n" +
						"Now it's time to setup your Streams.");  
				TextView resultMessage = new TextView(this.getActivity());
				resultMessage.setTextSize(18);
				int spanStrLength = span.length() ;
				span.setSpan(new RelativeSizeSpan(0.7f), 8, spanStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				resultMessage.setText(span);
				resultMessage.setGravity(Gravity.CENTER);
				completeDialog.setView(resultMessage);
				completeDialog.setCancelable(false) ;
				completeDialog.setPositiveButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>OK</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss(); 
						// navigate back to the main mActivity and indicate a progression to 
						// the settings mActivity .
						MainActivity.setNextStreamState(StreamsScreenState.STREAM_SETTINGS) ;
						getActivity().onBackPressed() ;
						return  ;
					}
				});

				completeDialog.setNegativeButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Later</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss(); 

					}
				});

				completeDialog.show() ;



			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * 
	 */
	void initializeCountrySpinner() 
	{
		try{
		msCountriesUNECE     = new ArrayList<String>();
		msCountriesCodesUNECE = new ArrayList<String>();

		int posOfUS = getCountryCodes(msCountriesUNECE,msCountriesCodesUNECE) ; 


		if (msCountryAdapter == null)
		{


		//	ArrayList<String> countries = new ArrayList<String>();

			msCountryAdapter = new ArrayAdapter<String>(mRootView.getContext(),
					R.layout.spinner_item, msCountriesUNECE);

		}
		msCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCountrySpinner.setAdapter(msCountryAdapter);

		int posOfCountry = msCountryAdapter.getPosition(mStreamsAccount.getCountry()) ;
		if (posOfCountry == -1)
		{  // default to us a 

			mCountrySpinner.setSelection(posOfUS) ;
		}
		else
		{
			mCountrySpinner.setSelection(posOfCountry) ;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param currentCountryCode
	 */
	void initializeStateSpinner(String currentCountryCode) 
	{
		try{
		ArrayList<String> listOfStates = new ArrayList<String>(); 
		Set<String> locDescriptors = new HashSet<String>();
		int countLocations = getLocalLocation(currentCountryCode,listOfStates,locDescriptors ) ;

		String stateDesc = "" ;
		if (countLocations == 0)
		{
			mStateDescriptionTextView.setText( "" ) ;
			mStateSpinner.setVisibility(View.GONE) ;
		}
		else
		{
		//	int count = 0 ; 
			mStateSpinner.setVisibility(View.VISIBLE) ;


			for (String locDesc : locDescriptors) 
			{
				stateDesc += locDesc ; 
				if (stateDesc.length() > 0)
				{
					break ; 
				}
			}

			if (stateDesc.length() == 0)
			{
				stateDesc = "State" ; 
			}

			String [] noParen = stateDesc.split("\\(");

			mStateDescriptionTextView.setText( noParen[0] ) ;
			msStateAdapter = 
					new ArrayAdapter<String>(mRootView.getContext(), R.layout.spinner_item, listOfStates );


			msStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mStateSpinner.setAdapter(msStateAdapter);
			int posOfState = -1 ; 
			if (currentCountryCode.compareToIgnoreCase(COUNTRY_US) == 0)
			{
				if (mStreamsAccount.getState().length() == 0 )
				{
					// default to NY
					posOfState = msStateAdapter.getPosition(STATE_NEW_YORK) ;
				}
				else
				{
					posOfState = msStateAdapter.getPosition(mStreamsAccount.getState()) ;
				}
			}
			else
			{
				posOfState = msStateAdapter.getPosition(mStreamsAccount.getState()) ;
			}
			if (posOfState == -1)
			{   

				mStateSpinner.setSelection(0) ;
			}
			else
			{
				mStateSpinner.setSelection(posOfState) ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param countryNames
	 * @param countryCodes
	 * @return
	 */
	int getCountryCodes(ArrayList<String> countryNames, ArrayList<String> countryCodes)
	{

		int USloc = 0 ; 
		try{
		ArrayList<String> combinedNamesAndCodes = new ArrayList<String>(); 
		//AssetManager assetManager = mProfileFragment.getActivity().getAssets();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(
							mProfileFragment.getActivity().getAssets().open("country_codes.csv"),
							CHAR_SET));


			String mLine = reader.readLine();
			while (mLine != null) {
				//process line
				String deAccented = deAccent(mLine) ; 



				String[] parts = deAccented.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

				if (parts.length >= 2)
				{
					boolean dontReplaceParts0 = false ; 
					if ( (parts[0].length() > 0) && (parts[2].length() > 0) )
					{ // k, we have a country/code 

						if (parts[2].compareToIgnoreCase("KP") == 0 )
						{
							parts[0] = "North Korea" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("KR") == 0 )
						{
							parts[0] = "South Korea" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("VI") == 0 )
						{
							parts[0] = "US Virgin Islands" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("RU") == 0 )
						{
							parts[0] = "Russia" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("VG") == 0 )
						{
							parts[0] = "British Virgin Islands" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("BS") == 0 )
						{
							parts[0] = "The Bahamas" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("GM") == 0 )
						{
							parts[0] = "The Gambia" ;
							dontReplaceParts0 = true ; 
						}

						if (parts[2].compareToIgnoreCase("VA") == 0 )
						{
							parts[0] = "Vatican City" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("CC") == 0 )
						{
							parts[0] = "Cocos (Keeling) Islands" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("CD") == 0 )
						{
							parts[0] = "Democratic Replublic of Congo" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("MO") == 0 )
						{
							parts[0] = "Macau" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("SY") == 0 )
						{
							parts[0] = "Syria" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("LA") == 0 )
						{
							parts[0] = "Laos" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("CV") == 0 )
						{
							parts[0] = "Cape Verde" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("VN") == 0 )
						{
							parts[0] = "Vietnam" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("AX") == 0 )
						{
							parts[0] = "Aland Islands" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("CW") == 0 )
						{
							parts[0] = "Curacao" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("RE") == 0 )
						{
							parts[0] = "Reunion" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("BL") == 0 )
						{
							parts[0] = "Saint Barthelemy" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("BQ") == 0 )
						{
							parts[0] = "Caribbean Netherlands" ;
							dontReplaceParts0 = true ; 
						}
						if (parts[2].compareToIgnoreCase("CI") == 0 )
						{
							parts[0] = "Ivory Coast" ;
							dontReplaceParts0 = true ; 
						}
						String cName ; 
						if (!dontReplaceParts0)
						{
							cName = parts[0].replaceAll("^\"|\"$", "").
									replaceAll("\\*", "").
									replaceAll("\\(.*\\)", "").
									replaceAll("\\[.*\\]", "");
						}
						else
						{
							cName = parts[0] ; 
						}
						String[] cNameNoCommas = cName.split(",") ;
						String[] cNameNoParen = cNameNoCommas[0].split("\\(") ;
						combinedNamesAndCodes.add(cNameNoParen[0] + " |" + parts[2]) ;
					}
				}
				mLine =reader.readLine(); 

			}

		} catch (IOException ioe) {
			ioe.printStackTrace ();
		}

		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//log the exception
				}
			}

		}
		Collections.sort(combinedNamesAndCodes, String.CASE_INSENSITIVE_ORDER); // sort includes the country codes
		int count = 0 ; 
		for ( String combinedEntry : combinedNamesAndCodes)
		{
			String[] namesAndCodes = combinedEntry.split("\\|") ;
			String countryNameTrimmed = namesAndCodes[0].trim();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg",countryNameTrimmed) ;
			}
			countryNames.add(countryNameTrimmed) ;
			countryCodes.add(namesAndCodes[1]);
			if (namesAndCodes[1].compareToIgnoreCase(COUNTRY_US)==0)
			{
				USloc = count ;
			}
			else
			{
				++count;
			}
		}
	}catch(Exception e){e.printStackTrace();}
		return USloc ; 


	}
	/**
	 * @param primaryCode
	 * @param localNames
	 * @param locDescriptors
	 * @return
	 */
	int getLocalLocation(String primaryCode, ArrayList<String> localNames , Set<String> locDescriptors)
	{
		int count = 0 ; 
		try{
		if (msSubLocationNamesFullList == null)
		{ // load the full list 
			msSubLocationNamesFullList = new ArrayList<LocCodeSubLoc>(); 


			BufferedReader reader = null;
			try {
				reader = new BufferedReader(
						new InputStreamReader(
								mProfileFragment.getActivity().getAssets().open("location_codelist.csv"),
								CHAR_SET));
				String mLine = reader.readLine();
				while (mLine != null) {
					//process line

					String deAccented = deAccent(mLine) ;
					String removeQuotes = deAccented.replaceAll("\"", "") ; 


					String[] parts = removeQuotes.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
					if (parts.length > 2)
					{
						LocCodeSubLoc locCodeSubLoc = new LocCodeSubLoc() ;

						locCodeSubLoc.locCode = parts[0] ; 


						char c1 = 0xf0 ;
						String repSpecialChar1  = parts[2].replace(c1, 'd') ;
						char c2 = 0xe6 ;
						String repSpecialChar2  = repSpecialChar1.replace(c2, 'a').replace("'","") ;
						String[] noParen = repSpecialChar2.split("\\(") ;
						String[] noBracket = noParen [0].split("\\[") ;
						String[] noForSlash = noBracket [0].split("/") ;
						String noQuestion = noForSlash[0].replace("?", "a").replace("*","") ;
						if ((noQuestion.charAt(0) < '0') || (noQuestion.charAt(0) > '9') )
						{
							locCodeSubLoc.subLocation = noQuestion ;
							if (parts.length > 3)
							{	
								locCodeSubLoc.sublocCategory = parts [3] ; 
							}
							else
							{
								locCodeSubLoc.sublocCategory = "Region" ;
							}
							msSubLocationNamesFullList.add(locCodeSubLoc) ;
						}
						else
						{

						}

					}


					mLine =reader.readLine(); 

				}

			} catch (IOException ioe) {
				ioe.printStackTrace ();
			}

			finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						//log the exception
					}
				}

			}

		}
		// run thru msSubLocationNamesFullList and if loc codes match, build the localNames array 
		// since the loc codes are in alpha order, once we break from a run of matches, we can exit
		boolean startRun = false ; 
		for (LocCodeSubLoc subLocationCode : msSubLocationNamesFullList  )
		{

			if (subLocationCode.locCode.compareToIgnoreCase(primaryCode)==0)
			{
				locDescriptors.add(subLocationCode.sublocCategory) ; 
				localNames.add(subLocationCode.subLocation) ; 
				++count ; 
				startRun = true ; 
			}
			else
			{
				if (startRun)
				{ // done  
					break ; 

				}
			}
		}
		// Now sort the list before returning 


		Collections.sort(localNames, String.CASE_INSENSITIVE_ORDER);
		}catch(Exception e){e.printStackTrace();}
		return count ; 

	}
	/**
	 * @param str
	 * @return
	 */
	public String deAccent(String str) {

		String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD) ;
		String normalized2 = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "") ; 
		return normalized2 ; 

	}
	public class LocCodeSubLoc
	{
		String locCode ; 
		String subLocation ; 
		String sublocCategory ; 

	}
}
