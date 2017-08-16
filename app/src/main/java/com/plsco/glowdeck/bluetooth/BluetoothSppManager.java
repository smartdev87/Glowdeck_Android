package com.plsco.glowdeck.bluetooth;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.bluecreation.melody.SppService;
import com.bluecreation.melody.SppService.ConnectionState;
import com.plsco.glowdeck.colorpicker.AppConfig;
import com.plsco.glowdeck.drawer.AboutFragment;
import com.plsco.glowdeck.drawer.StreamsDrawerListAdapter;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.colorpicker.AppConfig.PrefsFragment;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GLOWDECK_COMMANDS;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BluetoothSppManager {

    //private  BluetoothSppManager mBluetoothSppManager ;
	private ArrayList<String> mDiscoveredDevicesList;
	private static BluetoothAdapter 				    msBluetoothAdapter = null;
	public static BluetoothAdapter getMsBluetoothAdapter() {
		return msBluetoothAdapter;
	}
	/*
	private static int COUNT_BLUETOOTH_SIGNATURES = 6 ; 
	private static String glowdeckBTsignature1 = "00001108-0000-1000-8000-00805f9b34fb" ; 
	private static String glowdeckBTsignature2 = "0000111e-0000-1000-8000-00805f9b34fb" ; 
	private static String glowdeckBTsignature3 = "0000110b-0000-1000-8000-00805f9b34fb" ; 
	private static String glowdeckBTsignature4 = "0000110e-0000-1000-8000-00805f9b34fb" ; 
	private static String glowdeckBTsignature5 = "00001133-0000-1000-8000-00805f9b34fb" ; 
	private static String glowdeckBTsignature6 = "00000000-0000-1000-8000-00805f9b34fb" ; 
	private static String GLOWDECK = "GLOWDECK" ;
	 */
	// 2015-01-31 03:04:55
	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private MainActivity mMainActivity ;
	private String mCurrentGlowdeckName ; 
	private boolean networkSSIDsReceived ; 
	
	/*
	public void setNetworkSSIDsReceived(boolean networkSSIDsReceived) {
		this.networkSSIDsReceived = networkSSIDsReceived;
	}
	*/
	public boolean isNetworkSSIDsReceived() {
		return networkSSIDsReceived;
	}
	SppService mSppService ;

	public BluetoothSppManager() 
	{
		 try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","BluetoothSppManager::BluetoothSppManager::mSppService value is " + mSppService ) ;
		}
		mDiscoveredDevicesList = new ArrayList<String>();
		networkSSIDsReceived = false ; 
		startSppService() ;
		 }catch(Exception e){e.printStackTrace();}

	}
	public void initBluetoothSppManager(MainActivity mainActivity)
	{
		mMainActivity = mainActivity ;
	}
	public static boolean isBTavailable()
	{
		try{
		if (msBluetoothAdapter == null)
		{
			msBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		}
		}catch(Exception e){e.printStackTrace();}
		return (msBluetoothAdapter == null)  ?   false : true ; 
	}
	public static boolean isBTturnedOn()
	{
		try{
		if (msBluetoothAdapter != null)
		{
			return msBluetoothAdapter.isEnabled();
		}
		}catch(Exception e){e.printStackTrace();}
		return false ; 
	}
	/*
	public void setGlowdeckDevices()
	{
		glowdeckDevices.clear() ;
		if (isBTavailable())
		{
			Set<BluetoothDevice> pairedDevices = msBluetoothAdapter.getBondedDevices();


			for(BluetoothDevice bt : pairedDevices)
			{

				String upperCaseName = bt.getName().toUpperCase() ;
				if (upperCaseName.indexOf(GLOWDECK) > -1)
				{


					GlowdeckDevice glowdeckDevice = new GlowdeckDevice(bt.getType(), bt.getName(), bt.getUuids(), bt.getAddress(), bt.getBondState()) ;

					glowdeckDevices.add(glowdeckDevice) ;
					break ; 
				}

			}

		}


	}
	 */
	public String getGlowdeckName_DEPRICATED()
	{
		String name = "" ; 
		//for (GlowdeckDevice glowdeckDevice : glowdeckDevices)
		//{
		//	name = glowdeckDevice.getName() ;
		//}
		/*
		boolean test = false ; 
		if (msConnectionState == ConnectionState.STATE_CONNECTING) 
		{
			return "Connecting ..." ; 
		}
		if (msConnectionState == ConnectionState.STATE_CONNECTED) 
		{
			return  mConnectedDeviceName ; 
		}
		if (test) return "" ; 
		if (glowDeckDevice.length() > 0)
		{
			return glowDeckDevice ;
		}
		 */
		/*
		if (isBTavailable())
		{
			Set<BluetoothDevice> pairedDevices = msBluetoothAdapter.getBondedDevices();


			for(BluetoothDevice bt : pairedDevices)
			{

				String upperCaseName = bt.getName().toUpperCase() ;
				if (upperCaseName.indexOf(GLOWDECK) > -1)
				{

					glowDeckDevice = bt.getName() ;
					break ; 
				}

			}
			if (glowDeckDevice.length() == 0)
			{
				for(BluetoothDevice bt : pairedDevices)
				{
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg","BlueToothManger::getGlowDeckBlueTooth::" + bt.getName() + ", " + bt.describeContents()  + ", " + 
								bt.describeContents()) ;
					}
					ParcelUuid[] pUuids = bt.getUuids() ;
					int matchCount = 0 ; 
					if (pUuids.length == COUNT_BLUETOOTH_SIGNATURES)
					{

						String[] matchUuids = new String[COUNT_BLUETOOTH_SIGNATURES] ; 
						matchUuids [0] =  glowdeckBTsignature1 ;
						matchUuids [1] =  glowdeckBTsignature2 ;
						matchUuids [2] =  glowdeckBTsignature3 ;
						matchUuids [3] =  glowdeckBTsignature4 ;
						matchUuids [4] =  glowdeckBTsignature5 ;
						matchUuids [5] =  glowdeckBTsignature6 ;

						for (int i = 0 ;  i < COUNT_BLUETOOTH_SIGNATURES ; i++)
						{
							String uuidStr = pUuids[i].toString() ;
							if (StreamsApplication.DEBUG_MODE)
							{
								Log.d("dbg","BlueToothManger::getGlowDeckBlueTooth::uuidStr=" + uuidStr) ;
							}
							for (int j = 0 ; j < COUNT_BLUETOOTH_SIGNATURES ; j++)
							{

								if (uuidStr.compareTo(matchUuids[j]) == 0 ) 
								{
									matchCount++ ; 
									break ;
								}
							}
						}
						if (StreamsApplication.DEBUG_MODE)
						{
							Log.d("dbg","BlueToothManger::getGlowDeckBlueTooth::matchCount" + matchCount) ;
						}
						if (matchCount == COUNT_BLUETOOTH_SIGNATURES) 
						{
							glowDeckDevice = bt.getName() ;
						}
					}
				}
			}	
			//s.add(bt.getName());

				//if (StreamsApplication.DEBUG_MODE)
				//{

					//Log.d("dbg","MainActivity::getBlueToothList::" + bt.getName() + ", " + bt.describeContents()  + ", " + 
					//		bt.describeContents()) ;
					//ParcelUuid[] x = bt.getUuids() ; 

					//Log.d("dbg","MainActivity::getBlueToothList::" + "length = " + x.length + ", " +
						//	"x[0] = " + x[0]  + ", " + "\n" +
							//"x[1] = " + x[1]  + ", " +	  "\n" +
							//"x[2] = " + x[2]  + ", "  + "\n" +
							//"x[3] = " + x[3]  + ", "  + "\n" +
							//"x[4] = " + x[4]  + ", " + "\n" +
							//"x[5] = " + x[5]  + ", "  + "\n" +
							//bt.getName() + ", "+  
							//bt.getAddress() + ", " + bt.getType()) ;
				//}
			//}


		}
		 */
		//return glowDeckDevice ; 
		return name ; 
	}
	SppService getSppService()
	{
		return mSppService ;
	}
	public void startSppService()
	{
		try{
		mSppService = SppService.getInstance();
		mSppService.registerListener(sppListener);
		}catch(Exception e){e.printStackTrace();}
	}

	public void connectToGlowdeck(int index) 
	{

		try{
		String addressConnectedGlowdeck="" ;

		GlowdeckDevice glowdeckDevice = mMainActivity.getCurrentGlowdecks().getDeviceAtPosition(index)     ;//       //getmListGlowdecks().get(index) ; 
		addressConnectedGlowdeck = glowdeckDevice.getAddress() ;


		if (BluetoothAdapter.checkBluetoothAddress(addressConnectedGlowdeck)) 
		{
			// Get the BluetoothDevice object
			BluetoothDevice btDevice = msBluetoothAdapter.getRemoteDevice(addressConnectedGlowdeck);
			// Attempt to connect to the device
			if (btDevice != null)
			{
				mCurrentGlowdeckName = glowdeckDevice.getName() ;

				mSppService.connect(btDevice);

			}

		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void disconnect() 
	{
		try{
		if (mSppService != null)
		{
			ConnectionState state = mSppService.getState() ;
			if (state == ConnectionState.STATE_CONNECTED)
			{
				mSppService.disconnect() ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	 public void requestSSID()
     {
		 try{
		if (!AppConfig.isRSDpend())
		{
     	sendMessage(CurrentGlowdecks.RSD_COMMAND) ;
     	AppConfig.setRSDpend(true) ;
		}
		 }catch(Exception e){e.printStackTrace();}
     }
	SppService.Listener sppListener = new SppService.Listener() {
		@Override
		public void onStateChanged(final ConnectionState state) {

			//msConnectionState = state ; 
			/*
			if (D) {
				Log.i(TAG, "MESSAGE_STATE_CHANGE: " + state);
			}

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateConnectedText();

					if (D && state == ConnectionState.STATE_CONNECTED) {
						mInEditText.setText("", TextView.BufferType.EDITABLE);
						if (D) { // send the time and date upon connection when
							// in debug mode
							String s = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
							sendMessage(s);
						}
					}
				}
			});
			 */
		}

		@Override
		public void onRemoteDeviceConnected(String deviceName) {

			try{
			deviceName = deviceName.replace("\n", "") ;
			final MainActivity mainActivity = MainActivity.getMainActivity() ;
			if (mainActivity == null)
			{
				return ;   
			}
			 

			if ( MainActivity.getStreamsState() ==  StreamsScreenState.ABOUT_VIEW )
			{
				Fragment aboutFragement = mainActivity.getmCurrentFragment()  ; 
				aboutFragement =  new AboutFragment();
				mainActivity.setmCurrentFragment(aboutFragement)  ; 
			}


			// this should be an index into the list for the current glowdeck we are connecting to :
			// save the name of the glowdeck and then match as we iterate thru the list 
			GlowdeckDevice glowdeckDevice = mMainActivity.getCurrentGlowdecks().getGlowdeckWithName(mCurrentGlowdeckName) ;
			if (glowdeckDevice != null)
			{
				glowdeckDevice.setConnecting(  false ) ; 
				glowdeckDevice.setConnected( true ) ;
				sendMessage(CurrentGlowdecks.INIT_COMMAND) ;
				glowdeckDevice.setCurrentRequest(GLOWDECK_COMMANDS.INIT) ;
				glowdeckDevice.setMsgInprogress(true) ; 
				 
			}

			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					 
					StreamsDrawerListAdapter streamsDrawerListAdapter = mainActivity.getmStreamsDrawerListAdapter() ;
					if (streamsDrawerListAdapter != null)
					{
						streamsDrawerListAdapter.notifyDataSetChanged() ;
					}

				} 
			});
			}catch(Exception e){e.printStackTrace();}
		}
       
		@Override
		public void onDataReceived(final byte[] data, final int length) {
			try{
			final MainActivity mainActivity = MainActivity.getMainActivity()  ;
			GlowdeckDevice glowdeckDevice = mMainActivity.getCurrentGlowdecks().getCurrentlySelected() ;

			final String readMessage = new String(data, 0, length);
			if (StreamsApplication.DEBUG_MODE)
			{

				Log.d("dbg","BluetoothSppManager::onDataReceived::" + readMessage) ;
			}
			Log.d("xxx","BluetoothSppManager::onDataReceived::" + readMessage) ;
			if ( MainActivity.getStreamsState() ==  StreamsScreenState.ABOUT_VIEW )
			{
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AboutFragment aboutFragement = (AboutFragment) mainActivity.getmCurrentFragment()  ;
						aboutFragement.processSppResponse(readMessage) ;

					}
				});
			}
			if (readMessage.startsWith(CurrentGlowdecks.SOCIAL_REQUEST))
			{
				
				 String theSocialStream = UpdaterService.getSocialStream() ;
				 int currentStart = 0 ; 
				 int stringLength = theSocialStream.length() ;
				 String blankBuff = "                    " ;
				 try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 	sendMessage(blankBuff) ;	 
					 try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
				 
				while ((stringLength - currentStart)  > 0)		
				 {
					int sendLen ; 
					    if ( (stringLength - currentStart) > 20)
					    {
					    	sendLen = 20 ; 
					    }
					    else
					    {
					    	sendLen = stringLength - currentStart ;
					    }
					    String buf = theSocialStream.substring(currentStart, currentStart + sendLen) ;
					    Log.d("xxx" , theSocialStream.substring(currentStart, currentStart + sendLen)) ;
					
						 sendMessage(buf) ;	 
						 try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 currentStart += sendLen ; 
				 } 
				 
				return ; 
			} 
			if (readMessage.startsWith(CurrentGlowdecks.PERSONAL_REQUEST))
			{
				
				 String thePersonalStream = UpdaterService.getPersonalStream() ;
				 int currentStart = 0 ; 
				 int stringLength = thePersonalStream.length() ;
				 String blankBuff = "                    " ;
				 try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 	sendMessage(blankBuff) ;	 
					 try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 
				 
				while ((stringLength - currentStart)  > 0)		
				 {
					int sendLen ; 
					    if ( (stringLength - currentStart) > 20)
					    {
					    	sendLen = 20 ; 
					    }
					    else
					    {
					    	sendLen = stringLength - currentStart ;
					    }
					    String buf = thePersonalStream.substring(currentStart, currentStart + sendLen) ;
					    Log.d("xxx" , thePersonalStream.substring(currentStart, currentStart + sendLen)) ;
					
						 sendMessage(buf) ;	 
						 try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 currentStart += sendLen ; 
				 } 
				 
				return ; 
			} 
			if (readMessage.startsWith(CurrentGlowdecks.WEATHER_REQUEST))
			{
				 String theWeather = UpdaterService.getWeatherStream() ;
				 if (theWeather.length() > 0)		
				 {
					     
						sendMessage(theWeather) ;	 
						
				 } 
				 
				return ; 
			}
			 
			
			if (readMessage.startsWith(CurrentGlowdecks.INIT_RESPONSE))
			{
				if (glowdeckDevice != null )
				{
					if (glowdeckDevice.getCurrentRequest() == GLOWDECK_COMMANDS.INIT)
					{
						 
						sendMessage(CurrentGlowdecks.EQUALIZER_COMMAND) ;
						glowdeckDevice.fillInit(readMessage) ;
						glowdeckDevice.setReceivedInit(true) ;
						
					}
				}
				return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.RSD_RESPONSE))
			{
				if (glowdeckDevice != null )
				{
					glowdeckDevice.processRSD(readMessage) ;
					AppConfig.setRSDpend(false) ;
					
				}
				return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.DL_RESPONSE))
			{
				if (glowdeckDevice != null )
				{
					final String readMessageF = readMessage ;
					final GlowdeckDevice glowdeckF = glowdeckDevice ;
					MainActivity.getMainActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							glowdeckF.processDL(readMessage) ;
						}
					});
					
				}
				
				return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.BA_RESPONSE))
			{
				 
				String msg = CurrentGlowdecks.BA_RESPONSE + ":" + MainActivity.getmBatteryLevel() + "^" ;
				sendMessage(msg) ;
				return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.LS_RESPONSE))
			{
				if (glowdeckDevice != null )
				{
					final String readMessageF = readMessage ;
					final GlowdeckDevice glowdeckF = glowdeckDevice ;
					MainActivity.getMainActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							glowdeckF.processLS(readMessageF) ; 
						}
					});
					
				}
				return ; 
			}
			
			if (readMessage.startsWith(CurrentGlowdecks.GLOWDECK_TIME_REQUEST))
			{
				// 2015-01-31 03:04:55
				// TIM:2015-01-31 03:04:55^
				String date = sdf.format(new Date());
				String msg = CurrentGlowdecks.TIME_COMMAND_PREFIX + date + "^" ;
				//msg =  CurrentGlowdecks.TIME_COMMAND_PREFIX + "2015-01-31 03:04:55" + "^" ;
				sendMessage(msg) ;
				return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.INIT_RESPONSE_EQZ))
			{
				int subMsgLen = CurrentGlowdecks.INIT_RESPONSE_EQZ.length() ;
				 if (readMessage.length() > subMsgLen)
				 {
					 String value = readMessage.substring(subMsgLen, subMsgLen + 1) ; 
					 mMainActivity.getCurrentGlowdecks().setCurrentEqualizerSetting(value) ;
					 sendMessage(CurrentGlowdecks.WIFI_CONNECTED_COMMAND) ;
					 //requestSSID() ; 
					   
				 }
				 return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.INIT_RESPONSE_WIO))
			{
				 int subMsgLen = CurrentGlowdecks.INIT_RESPONSE_WIO.length() ;
				 if (readMessage.length() > subMsgLen)
				 {
					 String value = readMessage.substring(subMsgLen, subMsgLen + 1) ; 
					 mMainActivity.getCurrentGlowdecks().setCurrentWifiConnectedSetting(value) ;
					 requestSSID() ; 
					   
				 }
				 return ; 
			}
			if (readMessage.startsWith(CurrentGlowdecks.WIK_NETWORK_CONNECT_MSG))
			{

				String toastMsg = "" ;
				int start = CurrentGlowdecks.WIK_NETWORK_CONNECT_MSG.length() ;
				final String OK = "OK^" ;
				final String ERR = "ERR^" ;
				int end   = start + OK.length() ;
				CurrentGlowdecks glowdecks = MainActivity.getMainActivity().getCurrentGlowdecks();
				if (readMessage.substring(start, end).equals(OK))
				{
					toastMsg = "Glowdeck Wifi connection succeeded." ;
					
					glowdecks.setCurrentWifiConnectedSetting(CurrentGlowdecks.on) ;
					glowdecks.setCurrentProvisioningSetting(CurrentGlowdecks.on) ;
					new  AppConfig.SendDevicesStream().execute() ;
				}
				else
				{
					end = start + ERR.length() ; 
					if (readMessage.substring(start, end).equals(ERR))
					{
						toastMsg = "Glowdeck Wifi connection failed.\nCheck SSID/Password" ;
					}
					glowdecks.setCurrentWifiConnectedSetting(CurrentGlowdecks.off) ;
				}
				final String toastMsgF = toastMsg ; 
				
				final AlertDialog alertDialog =  AppConfig.getWifiConnectDialog() ;
				
				if (alertDialog != null)
				{
					 
					MainActivity.getMainActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							 alertDialog.setMessage(toastMsgF) ;
							 alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true) ;
							 AppConfig appConfig = AppConfig.getAppconfig() ;
							 if (appConfig != null)
							 {
								 PrefsFragment prefsFragment = AppConfig.getmPrefsFragment() ;
								 if (prefsFragment != null)
								 {
									 prefsFragment.resetElementValue() ; 
									 prefsFragment.updateNetworkPassordTitleSummary( false) ;
								 }
							 }
						}
					});
					
				}
				 return ; 
			}
			}catch(Exception e){e.printStackTrace();}

		}

		@Override
		public void onConnectionLost() {
			try{
			final MainActivity mainActivity = MainActivity.getMainActivity()  ;
			if (StreamsApplication.DEBUG_MODE)
			{

				Log.d("dbg","On CONNECTION lost====== "   ) ;
			}
			if (mainActivity == null)
			{
				return ; 
			}

			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					//Toast.makeText(MainActivity.getAppContext(), "Connection Lost",
					//Toast.LENGTH_SHORT).show();
					/*
					GlowdeckDevice device  = mMainActivity.getCurrentGlowdecks().getCurrentlyConnected() ;
					if (device != null)
					{

						if (!device.isReconnectOnRestart()) 
						{
							device.setConnecting (false ); 
							device.setConnected (false) ;
						}
					}
					*/
					StreamsDrawerListAdapter streamsDrawerListAdapter = mainActivity.getmStreamsDrawerListAdapter() ;
					if (streamsDrawerListAdapter != null)
					{
						streamsDrawerListAdapter.notifyDataSetChanged() ;
					}
					if ( MainActivity.getStreamsState() ==  StreamsScreenState.ABOUT_VIEW )
					{
						if (!MainActivity.isMsPaused())
						{
							mainActivity.displayView(MainActivity.DRAWER_ABOUT_FRAGMENT) ;
						}
					}

				}
			});
			}catch(Exception e){e.printStackTrace();}
		}

		@Override
		public void onConnectionFailed() {

			try{
			final MainActivity mainActivity = MainActivity.getMainActivity()  ;
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					GlowdeckDevice device = mMainActivity.getCurrentGlowdecks().getCurrentlyConnecting()  ;
					if (device != null)
					{
						device.setConnecting ( false ) ; 
					}

					StreamsDrawerListAdapter streamsDrawerListAdapter = mainActivity.getmStreamsDrawerListAdapter() ;
					if (streamsDrawerListAdapter != null)
					{
						streamsDrawerListAdapter.notifyDataSetChanged() ;
					}
				}
			});
			}catch(Exception e){e.printStackTrace();}
		}
	};



	public SppService.Listener getSppListener() {
		return sppListener;
	}
	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	public void sendMessage(String message) {
		try{
		// Check that we're actually connected before trying anything
		MainActivity mainActivity = MainActivity.getMainActivity()  ;
		if (mSppService.getState() != ConnectionState.STATE_CONNECTED) {
			
			MainActivity.getMainActivity().runOnUiThread(new Runnable() {

                public void run() {
                	Toast.makeText(MainActivity.getMainActivity(), com.plsco.glowdeck.R.string.glowdeck_disconnected, Toast.LENGTH_SHORT)
        			.show();
                }
            });
			
			
			
			 
			GlowdeckDevice currentGD = mMainActivity.getCurrentGlowdecks().getCurrentlyConnected() ;
			if (currentGD != null) 
			{
				currentGD.setConnected(false) ;
				currentGD.setCurrentlySelected(false) ;
				mMainActivity.getCurrentGlowdecks().isConnected() ;
				StreamsDrawerListAdapter streamsDrawerListAdapter = mainActivity.getmStreamsDrawerListAdapter() ;
				if (streamsDrawerListAdapter != null)
				{
					streamsDrawerListAdapter.notifyDataSetChanged() ;
				}
				mCurrentGlowdeckName = "" ; 
			}
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the MelodyService to write
			byte[] send = message.getBytes();

			try {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","Sending ...:" + message) ;
				}
				mSppService.send(send);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void sendMessage(byte[] message) {
		try{
		// Check that we're actually connected before trying anything
		MainActivity mainActivity = MainActivity.getMainActivity()  ;
		if (mSppService.getState() != ConnectionState.STATE_CONNECTED) {
			
			MainActivity.getMainActivity().runOnUiThread(new Runnable() {

                public void run() {
                	Toast.makeText(MainActivity.getMainActivity(), com.plsco.glowdeck.R.string.glowdeck_disconnected, Toast.LENGTH_SHORT)
        			.show();
                }
            });
			
			
			
			
			
			
			
			
			
			GlowdeckDevice currentGD = mMainActivity.getCurrentGlowdecks().getCurrentlyConnected() ;
			if (currentGD != null) 
			{
				currentGD.setConnected(false) ;
				currentGD.setCurrentlySelected(false) ;
				mMainActivity.getCurrentGlowdecks().isConnected() ;
				StreamsDrawerListAdapter streamsDrawerListAdapter = mainActivity.getmStreamsDrawerListAdapter() ;
				if (streamsDrawerListAdapter != null)
				{
					streamsDrawerListAdapter.notifyDataSetChanged() ;
				}
				mCurrentGlowdeckName = "" ; 
			}
			return;
		}

		// Check that there's actually something to send
		if (message.length > 0) {
			// Get the message bytes and tell the MelodyService to write
			//byte[] send = message.getBytes();

			try {
				if (StreamsApplication.DEBUG_MODE)
				{
					//Log.d("dbg","Sending ...:" + message) ;
				}
				//Log.d("dbg","Sending " + message.length + " bytes.") ;
				mSppService.send(message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public boolean isConnected()
	{

		return mMainActivity.getCurrentGlowdecks().isConnected() ;

	}
	public void setConnecting(int index)
	{
		try{
		GlowdeckDevice device = mMainActivity.getCurrentGlowdecks().getDeviceAtPosition(index)   ;
		device.setConnecting (true) ; 
		}catch(Exception e){e.printStackTrace();}

	}
	public boolean isConnecting(int index)
	{
		GlowdeckDevice device = mMainActivity.getCurrentGlowdecks().getDeviceAtPosition(index)     ;        //  getmListGlowdecks().      get(0) ;
		return device.isConnecting () ; 

	}
	public void registerGlowdeckDiscovery()
	{
		try{
		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mMainActivity.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mMainActivity.registerReceiver(mReceiver, filter);
		}catch(Exception e){e.printStackTrace();}

	}
	public void stopDiscovery()
	{
		try{
		// Make sure we're not doing discovery anymore
		if (msBluetoothAdapter != null) {
			msBluetoothAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		mMainActivity.unregisterReceiver(mReceiver);
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void doDiscovery() {



		try{

		// If we're already discovering, stop it
		if (msBluetoothAdapter.isDiscovering()) {
			msBluetoothAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		msBluetoothAdapter.startDiscovery();
		}catch(Exception e){e.printStackTrace();}
	}
	// The BroadcastReceiver that listens for discovered devices and

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				// If it's already paired, skip it, because it's been listed already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mDiscoveredDevicesList.add(device.getName() + "\n" + device.getAddress() );
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {


				if (mDiscoveredDevicesList.isEmpty() ) {
					// no devices
				}
			}
			}catch(Exception e){e.printStackTrace();}
		}
	};

}
