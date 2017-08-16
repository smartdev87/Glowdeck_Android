
package com.plsco.glowdeck.drawer;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.plsco.glowdeck.glowdeck.CurrentGlowdecks;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: AboutFragment.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * The AboutFragment() extends mCurrentFragment
 * 
 *  
 *
 */

/**
 * 
 *
 */

public class AboutFragment extends Fragment {
	private int numLinesSPPResponse ;
	private TextView mTextViewSPPResponse = null ;
	static Context context = null; 


	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}
	private static boolean mProcessingClick = false ;
	private boolean productionRelease = true ; 
	private boolean mDebugMode = !productionRelease ; // diagnostic window in about screen

	private boolean connectedToGlowdeck = false ; 
	public AboutFragment(){



	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		context = this.getActivity() ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","AboutFragment onCreateView") ;
		}
		numLinesSPPResponse = 0 ; 
		final View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		try{
		MainActivity mainActivity = MainActivity.getMainActivity() ;
		if (mainActivity == null )
		{
			return rootView ; 
		}
		ScrollView parentScroll=(ScrollView)rootView.findViewById(R.id.aboutParentScroll);


		connectedToGlowdeck = MainActivity.getMainActivity().getCurrentGlowdecks().isConnected() ;
		TextView buildNumberTV  = (TextView)  rootView.findViewById(R.id.about_version_textview)  ;
		buildNumberTV.setText("Build " + getResources().getString(  R.string.build_version));
		TextView checkUpdatesTV = (TextView) rootView.findViewById(R.id.check_updates_textView);
		final int currentColor = checkUpdatesTV.getCurrentTextColor() ;
		final TextView tVf = checkUpdatesTV ; 


		final EditText  editTextGlowdeckSPP =  (EditText)  rootView.findViewById(R.id.editTextGlowdeckSPP)  ;
		Button  buttonSendSPP =  (Button) rootView.findViewById(R.id.buttonSendSPP)  ;
		Button  buttonClearResponse =  (Button) rootView.findViewById(R.id.buttonClearResponse)  ;
		Button  buttonCopyResponse =  (Button) rootView.findViewById(R.id.buttonCopyResponse)  ;
		mTextViewSPPResponse = (TextView)  rootView.findViewById(R.id.textViewSPPResponse)  ;
		mTextViewSPPResponse.setMovementMethod(new ScrollingMovementMethod());
		mTextViewSPPResponse.setText("") ;
		int debugVisibility = View.INVISIBLE ; 
		if (connectedToGlowdeck && mDebugMode)
		{

			debugVisibility = View.VISIBLE ;

		}

		if (editTextGlowdeckSPP != null)
		{

			editTextGlowdeckSPP.setVisibility(debugVisibility) ;
			editTextGlowdeckSPP.setFocusable(true) ;
			editTextGlowdeckSPP.requestFocus() ;

		}
		if (buttonSendSPP != null)
		{
			buttonSendSPP.setVisibility(debugVisibility) ;
		}
		if (buttonClearResponse != null)
		{
			buttonClearResponse.setVisibility(debugVisibility) ;
		}

		if (buttonCopyResponse != null)
		{
			buttonCopyResponse.setVisibility(debugVisibility) ;
		}

		if (mTextViewSPPResponse != null)
		{
			mTextViewSPPResponse.setVisibility(debugVisibility) ;
		}

		if (debugVisibility == View.VISIBLE )
		{
			checkUpdatesTV.setVisibility(View.INVISIBLE) ;
			buildNumberTV.setVisibility(View.INVISIBLE) ;
		}
		else
		{
			checkUpdatesTV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!mProcessingClick)
					{
						mProcessingClick = true ; 


						tVf.setTextColor(currentColor) ;

						checkForFirmwareUpdate() ;
					}

				}
			});
			checkUpdatesTV.setOnTouchListener(new TextView.OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEvent.ACTION_DOWN == event.getAction()) {

						int downColor = 0x00ffffff & currentColor ;
						downColor |= 0x66000000 ; 
						tVf.setTextColor(downColor) ;

					} else if (MotionEvent.ACTION_UP == event.getAction()) {
						v.performClick();
					}

					return true;
				}
			});

		}

		parentScroll.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				rootView.findViewById(R.id.textViewSPPResponse).getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		mTextViewSPPResponse.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) 
			{

				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});


		buttonSendSPP.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {

				String msg = editTextGlowdeckSPP.getText().toString() ;
				if (msg.length() > 0)
				{
					StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication() ;
					streamsApplication.getBluetoothSppManager().sendMessage(msg) ;

					editTextGlowdeckSPP.setText("") ;
				}
			}
		});


		buttonCopyResponse.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {

				String msg = mTextViewSPPResponse.getText().toString() ;
				if (msg.length() > 0)
				{

					ClipData.Item item=new ClipData.Item(msg);
					String[] mimeType={"text/plain"};
					ClipData clipData=new ClipData(msg,mimeType,item);

					ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
					clipboard.setPrimaryClip(clipData);

				}
			}
		});


		buttonClearResponse.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {

				mTextViewSPPResponse.setText("") ;
				numLinesSPPResponse = 0 ; 

			}
		});
		}catch(Exception e){e.printStackTrace();}
		return rootView;
	}

	public void processSppResponse(String msg)
	{

		try{
		String rows[] = msg.split("\\^") ;
		for (String row : rows)
		{
			if (numLinesSPPResponse++ > 0)
			{
				mTextViewSPPResponse.append("\n") ;
			}
			mTextViewSPPResponse.append(row) ;
		}
		int lineHeight = mTextViewSPPResponse.getLineHeight();

		mTextViewSPPResponse.setScrollY(lineHeight*numLinesSPPResponse - mTextViewSPPResponse.getHeight()) ;
		}catch(Exception e){e.printStackTrace();}
	}

	void checkForUpdate()
	{

		final String appPackageName = this.getActivity().getPackageName(); // getPackageName() from Context or Activity object
		try {
			this.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
		}
		mProcessingClick = false ; 
	}
	void checkForFirmwareUpdate()
	{

		try{
		CurrentGlowdecks currentGlowdecks ;
		MainActivity mainActivity = MainActivity.getMainActivity() ;
		GlowdeckDevice glowdeckDevice = mainActivity.getCurrentGlowdecks().getCurrentlyConnected() ;
		if (glowdeckDevice==null)
		{

			Toast.makeText(context, "Not Connected to Glowdeck", Toast.LENGTH_LONG).show();

			mProcessingClick = false ; 
		}
		else
		{
			new  GetLatestFirmware().execute() ;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public static class GetLatestFirmware extends AsyncTask<String, Integer, Long> {

		ProgressDialog mProgressDialog ;
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onCancelled(Long result) {
			// TODO Auto-generated method stub
			try{
			Toast.makeText(context, "Glowdeck Firmware\nUpdate has been canceled.", Toast.LENGTH_LONG).show();
			
			MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			mProcessingClick = false ; 
			mProgressDialog.dismiss();
			}catch(Exception e){e.printStackTrace();}
			super.onCancelled(result);
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			try{
			MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			mProcessingClick = false ; 
			mProgressDialog.dismiss();
			}catch(Exception e){e.printStackTrace();}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			try{
			MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			mProgressDialog  = ProgressDialog.show(MainActivity.getMainActivity(), "Wait", "Downloading Firmware from Server...", true, false) ;

			}catch(Exception e){e.printStackTrace();}
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			try{
			int currentBytes = values[0] ;
			
			if (currentBytes == 0)
			{
			mProgressDialog.setMessage("Starting Glowdeck firmware upload...") ;
			}
			else
			{
				int totalBytes   = values[1] ;
				int percent = currentBytes*100/totalBytes ;
				 
				if ( (percent > 0 ) && (percent % 10) == 0)
				{
					mProgressDialog.setMessage("Updating Glowdeck - " + percent + "% Complete") ;
				}
			}
			}catch(Exception e){e.printStackTrace();}
			super.onProgressUpdate(values);
		}

		@Override
		protected Long doInBackground(String... params) {
			try{
			mProgressDialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog){
					cancel(true) ; 
				}});
			mProgressDialog.setOnKeyListener( new DialogInterface.OnKeyListener() {
		        public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
		            switch(arg1) {
		            case KeyEvent.KEYCODE_BACK:
		            	cancel(true) ; 
		                return true;
		                        
		            default:
		                    break;
		            }
		            return true;
		        }
		    });        
			
			HttpClient httpclient = new DefaultHttpClient();

			final String firmwareUrl = "https://streams.io/glowdeck/firmware/images/glowdeck.bin";

			int firmwareSize = 0 ; 
			String line = null ;
			URL u = null;
			try {
				u = new URL(firmwareUrl);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpURLConnection c = null;
			try {
				c = (HttpURLConnection) u.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				c.setRequestMethod("GET");
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				c.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			


			InputStream in = null;
			try {
				in = c.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int maxSize = 1024 * 256 ; 
			byte[] imageBuffer = new byte[maxSize];
			byte[] buffer = new byte[1024];
			int len1 = 0;
			int currentBufPtr = 0 ;
			
			try {
				while ( (len1 = in.read(buffer)) > 0 ) {
					
					for (int i = 0 ; i < len1 ; i++)
					{
						imageBuffer[i + currentBufPtr] = buffer[i] ; 

					}
					
					currentBufPtr += len1 ; 
					firmwareSize += len1 ; 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			publishProgress(0);
			String imageSize = Integer.toString(firmwareSize);

			final int maxSizeF = maxSize ; 
			final int firmwareSizeF = firmwareSize ;
			final String imageSizef = imageSize ; 
			StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity()
					.getApplication();
			MainActivity.getMainActivity().runOnUiThread(new Runnable() {

				public void run() {
					String msg1 = "Downloaded firmware image.\nImage size: " + imageSizef + " bytes.\nStarting upload to Glowdeck ..." ;
					if (firmwareSizeF == 0 )
					{ 
						msg1 = "Failed to download firmware image" ;
						Toast.makeText(context, msg1, Toast.LENGTH_LONG).show();
					}
					if (firmwareSizeF > maxSizeF)
					{
						msg1 = "Failed firmware download - buffer overflow" ;
						Toast.makeText(context, msg1, Toast.LENGTH_LONG).show();
					}
					

				}
			});
			if ((firmwareSize == 0) || (firmwareSize > maxSize))
			{
				mProcessingClick = false ; 
				return null;
			}
			// send GFU^
			if (isCancelled())
			{
				return null ;
			}
			else
			{
				streamsApplication.getBluetoothSppManager().sendMessage("GFU^");
			}
			try {
				Thread.sleep(3000) ;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int glowdeckMsgSize = 20 ; 
			byte[] glowdeckBuff = new byte[glowdeckMsgSize] ; 
			

			//String firmwareSizeString=Integer.toHexString(firmwareSize);
			byte[] firmwareSizeByte = new byte[20] ;
			Arrays.fill(firmwareSizeByte, (byte) ' ');
			firmwareSizeByte[0] = 'G' ;
			firmwareSizeByte[1] = 'L' ;
			firmwareSizeByte[2] = 'O' ;
			firmwareSizeByte[3] = 'W' ;
			firmwareSizeByte[4] = (byte)( (firmwareSize >> 24) &  0xFF) ;
			firmwareSizeByte[5] = (byte)( ( firmwareSize >> 16) &  0xFF);
			firmwareSizeByte[6] = (byte)( ( firmwareSize >> 8) &  0xFF);
			firmwareSizeByte[7] = (byte)(  firmwareSize  &  0xFF);
			
			

			if (isCancelled())
			{
				return null ;
			}
			else
			{
				streamsApplication.getBluetoothSppManager().sendMessage(firmwareSizeByte);
				try {
					Thread.sleep(3000) ;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			int msgs = firmwareSize/glowdeckMsgSize ;
			
			int bytesSent = 0  ;
			
			for (int sendMsg = 0 ; sendMsg < msgs ; ++sendMsg )
			{
				int offSet = sendMsg*glowdeckMsgSize ;
				for (int i = 0 ; i < glowdeckMsgSize ; i++)
				{
					
					glowdeckBuff[i] = imageBuffer[i + offSet ] ;




				}
				if (isCancelled())
				{
					return null ;
				}
				else
				{
					streamsApplication.getBluetoothSppManager().sendMessage(glowdeckBuff);
					publishProgress(sendMsg,msgs);
				}
				//Log.d("dbg", "Sending message : " + sendMsg) ;


				try {
					Thread.sleep(50) ;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bytesSent += glowdeckMsgSize ;
				
			}
			int remainder = firmwareSize % glowdeckMsgSize ;

			if (remainder > 0)
			{
				byte[] glowdeckBuffRem = new byte[glowdeckMsgSize] ;
				Arrays.fill( glowdeckBuffRem, (byte) ' ' );
				int offSet = msgs*glowdeckMsgSize ;
				for (int i = 0 ; i < remainder ; i++)
				{

					glowdeckBuffRem[i] = imageBuffer[offSet + i ] ;

				}
				if (isCancelled())
				{
					return null ;
				}
				else
				{
					streamsApplication.getBluetoothSppManager().sendMessage(glowdeckBuffRem);
				}
				bytesSent += remainder ;
			}
			}catch(Exception e){e.printStackTrace();}
			return null;
		}


	}
	/*
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
    */
}
