package com.plsco.glowdeck.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;

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
 * FileName: WeatherSettingsAdapter.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
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
 * The WeatherSettingsAdapter extends BaseAdapter
 *  
 *  
 *
 */
public class WeatherSettingsAdapter extends BaseAdapter {
	//
	//	Globals
	//
	
	String [] 									mResult;
	Context 									mContext;
	int [] 									mImageId;
	DialogInterface  mZipCodeDialog = null; 
	ProgressDialog   mProgressDialog = null;
	private  LayoutInflater 					mInflater=null;

	// statics
	static boolean 								msOnClickAddGuardRowView = false; 
	static boolean 								msOnClickDelGuardRowView = false; 
	//
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mResult.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {

		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {

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
			ImageView  img2=(ImageView) rowView.findViewById(R.id.icon_indset_del); 
			img2.setVisibility(View.INVISIBLE) ;
			rowView.setTag(holder);
		}
		Holder holder = (Holder) rowView.getTag();

		final int fPosition = position ; 



		holder.tv.setText(mResult[position]);


		holder.img.setImageResource(mImageId[position]); 



		rowView.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				//final View fV = v ; 

				if (msOnClickAddGuardRowView)
				{
					return ;
				}
				else
				{
					msOnClickAddGuardRowView = true ; 
				}

				AlertDialog.Builder completeDialog = new AlertDialog.Builder(mContext);

				Spannable span;

				
				span = new SpannableString("\nEnter Location\nEnter a zip code\n");
				int spanLen = span.length();
				span.setSpan(new RelativeSizeSpan(0.7f), 15, spanLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				 


				TextView resultMessage = new TextView(mContext);
				resultMessage.setTextSize(18);
				resultMessage.setText(span);
				resultMessage.setGravity(Gravity.CENTER);

				final EditText input = new EditText(mContext);

				input.setSingleLine() ;
				 
				input.setText(mResult[fPosition]) ;
				input.setSelection(mResult[fPosition].length());
				 

				LinearLayout childLayout = new LinearLayout(mContext);
				childLayout.setOrientation(LinearLayout.VERTICAL);

				childLayout.addView(resultMessage);  
				childLayout.addView(input); 

				completeDialog.setView(childLayout);
				childLayout.setPadding(40, 0,40, 0);

				completeDialog.setCancelable(false) ;
				completeDialog.setPositiveButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Done</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString() ;

						editZipCodeStart(value,fPosition, dialog) ;
 
					}
				});

				completeDialog.setNegativeButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Cancel</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						msOnClickAddGuardRowView = false ; 
						dialog.dismiss(); 
						//mLoginInProgress = false ;
					}
				});

				completeDialog.show() ;

			}
		});  
		}catch(Exception e){e.printStackTrace();}
		return rowView;
	} 
	/**
	 * @param position - in the listview
	 */
	

	/**
	 * @param value - the updated keyword
	 * @param position - the position in the list
	 */
	void editZipCodeStart(String value, int position,DialogInterface dialog)
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		/*
		if (streamsUser.getWeather().getLocation().getZip().compareTo(value) == 0 )
		{
			// no change
			mProgressDialog = null ; 
			editZipCodeComplete(true) ;
			return ;

		}
		*/
		streamsUser.getWeather().getLocation().setZip(value) ;


		//streamsUser.getModifiedSettings().setNewsSettingsModified(true) ;
		WeatherSettingsAdapter weatherSettingsAdapter =
				(WeatherSettingsAdapter) IndividualSettingsActivity.mIndividualSettingsLV.getAdapter() ;
		mResult[position] = value ;
		weatherSettingsAdapter.notifyDataSetChanged();
		
		mZipCodeDialog = dialog ;
		// start a spinner
		// start async process to send server updated zip code in weather 
		SendServerWeatherZipChanged sendServerWeatherZipChanged = new SendServerWeatherZipChanged(this)  ;// 

		sendServerWeatherZipChanged.execute() ;

		mProgressDialog=new ProgressDialog(mContext,R.style.SpinnerTheme);  // R.style.SpinnerTheme)


		mProgressDialog.setCancelable(false);

		mProgressDialog.setIcon(R.drawable.streams_beta)  ;

		mProgressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar) ;      //          android.R.style.Widget_ProgressBar_Small);

		mProgressDialog.show() ;
		}catch(Exception e){e.printStackTrace();}
	}
	void editZipCodeComplete(boolean results)
	{
		try{
		if (!results)
		{
			Toast.makeText(mContext,"Change of zip did not update server.\nPlease try again later", 
					Toast.LENGTH_LONG).show();

		}
		else
		{
			SettingsTopFragment.settingsAdapter.notifyDataSetChanged() ;
		}
		if (mProgressDialog != null)
		{
			mProgressDialog.dismiss() ;
			mProgressDialog = null ; 
		}
		if (mZipCodeDialog != null)
		{
			mZipCodeDialog.dismiss() ; 
			mZipCodeDialog = null ; 
		}
		msOnClickAddGuardRowView = false ; 
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param value - the news keyword to add
	 */
	
	/**
	 * 
	 * Constructor - set up global values for invocation
	 * @param individualSettingsActivity = mContext
	 * @param prgmNameList
	 * @param prgmImages
	 * @param prgmImages2
	 */
	public WeatherSettingsAdapter(IndividualSettingsActivity individualSettingsActivity,
			String[] prgmNameList, 
			int[] prgmImages) {
		mResult=prgmNameList;
		mContext=individualSettingsActivity;
		mImageId=prgmImages;

		mInflater = ( LayoutInflater )mContext.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	public class Holder
	{
		TextView tv;
		ImageView img;

	}
	public class SendServerWeatherZipChanged extends AsyncTask<String, Integer, Long> {

		WeatherSettingsAdapter mWeatherSettingsAdapter = null ;


		SendServerWeatherZipChanged(WeatherSettingsAdapter weatherSettingsAdapter)
		{
			mWeatherSettingsAdapter = weatherSettingsAdapter ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {

			super.onCancelled();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
		 */
		@Override
		protected void onCancelled(Long result) {

			super.onCancelled(result);
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			try{
			boolean success = (result ==  0 ) ? false : true ; 
			mWeatherSettingsAdapter.editZipCodeComplete(success); 
			}catch(Exception e){e.printStackTrace();}
			super.onPostExecute(result);
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {

			long retVal = 0 ; 
			try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(UpdaterService.SET_CURRENTUSERINFO);
			String token = null ; 
			String userId = null ;

			StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
			if (streamsUser != null)
			{
				StreamsAccount streamsAccount = streamsUser.getStreamsAccount() ;
				if (streamsAccount != null)
				{
					userId = streamsAccount.getID() ;
					token  = streamsAccount.getToken() ;

				}
			}

			if ( (userId != null) && (token != null ) )
			{

				try {
					String prefix = "Token=" + token + "&UserID=" + userId + "&Request={" ;
					String streamName = null ; 
					String jsonString = null ; 
					String request ; 
					
					streamName = "\"Weather\":" ;
					Gson gsonSend = new GsonBuilder().create();
					//jsonString = gson.toJson(mStreamsUser.getNews());
					String jsonStringCity = gsonSend.toJson(streamsUser.getWeather().getLocation().getCity());
					String jsonStringZip =  gsonSend.toJson(streamsUser.getWeather().getLocation().getZip());
					//{"Weather":{"Light":"","Color":{"Right":"","Left":"","Front":""},"Location":{"Zip":"92014","City":""},"Enabled":"1"}}
					//jsonString = "{\"Keywords\":" + jsonStringKeywords + ",\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Enabled\":" + jsonStringEnabled 
					//	+ "}}"
					jsonString = "{\"Light\":\"\",\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Location\":{\"Zip\":\"" +
							streamsUser.getWeather().getLocation().getZip() + "\",\"City\":\"" + streamsUser.getWeather().getLocation().getCity() +
							"\"},\"Enabled\":\"" + streamsUser.getWeather().getEnabled() + "\"}}" ;
					//} 



					request = prefix + streamName + jsonString ;

					//String request = "Token=" + token + "&UserID=" + userId + "&ID=" + readParms[0] + "&Type=" +
					//		readParms[1] + "&Status=1";
					StringEntity se = new StringEntity( request);	

					httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
					httppost.setEntity(se);
					HttpResponse httpResponse = httpclient.execute(httppost);

					BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
					StringBuilder builder = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						line = VerifyCredentialsTask.cleanUpJsonFromServer(line) ;
						builder.append(line).append("\n");
					}
					Gson gson = new GsonBuilder(). 

							setDateFormat("yyyy-mm-DD HH:mm:ss").create();
					
					
					VerifyCredentialsTask.setStreamsUser((gson.fromJson( builder.toString(), StreamsUser.class) ) );
				
					
					retVal = (long)1 ; 

				} 

				catch (IOException e) {
					e.printStackTrace();
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", "IOException Error in SendServerWeatherZipChanged = " + e ) ;
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", "Exception Error in SendServerWeatherZipChanged = " + e ) ;
					}

				} 
				finally
				{
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", "Finally in SendServerWeatherZipChanged." ) ;
					}

				}
			}
			}catch(Exception e){e.printStackTrace();}
			return retVal;
		}

	}
}
