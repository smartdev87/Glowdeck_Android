package com.plsco.glowdeck.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.bluecreation.melody.SppService.ConnectionState;
import com.plsco.glowdeck.drawer.StreamsDrawerListAdapter;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;


public class   BluetoothBleManager            {
	GBleWrapper mGBleWrapper ;
	private static ConnectionState   msConnectionState  = null ;
	public static ConnectionState getMsConnectionState() {
		return msConnectionState;
	}

	public static void setMsConnectionState(ConnectionState msConnectionState) {
		BluetoothBleManager.msConnectionState = msConnectionState;
	}

	private static String mDeviceName;
	public String getmDeviceName() {
		return mDeviceName;
	}

	public void setmDeviceName(String mDeviceName) {
		BluetoothBleManager.mDeviceName = mDeviceName;
	}

	public String getmDeviceAddress() {
		return mDeviceAddress;
	}

	public void setmDeviceAddress(String mDeviceAddress) {
		this.mDeviceAddress = mDeviceAddress;
	}

	private String mDeviceAddress;
	private String mDeviceRSSI = null ;

	public BluetoothBleManager()
	{

		//mGBleWrapper =  new GBleWrapper(MainActivity.getMainActivity(), new GBleWrapperUiCallbacks.Null() ) ;

		try{
		mGBleWrapper = new GBleWrapper(MainActivity.getMainActivity(), new GBleWrapperUiCallbacks.Null() {
			@Override
			public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
				handleFoundDevice(device, rssi, record);
			}
		});
		mGBleWrapper.setBluetoothBleManager(this) ; 
		}catch(Exception e){e.printStackTrace();}
	}
	private void handleFoundDevice(final BluetoothDevice device,
			final int rssi,
			final byte[] scanRecord)
	{
		try{
		// adding to the UI have to happen in UI thread
		MainActivity.getMainActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//mDevicesListAdapter.addDevice(device, rssi, scanRecord);
				//mDevicesListAdapter.notifyDataSetChanged();
			}
		});
		}catch(Exception e){e.printStackTrace();}
	}	
	public boolean initialize()
	{
		return mGBleWrapper.initialize() ;
	}
	public void connectToBTdevice()
	{


		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "BluetoothBleManager::connectToBTdevice") ; 
		}
		if (mGBleWrapper.connect(mDeviceAddress) )
		{
			msConnectionState = ConnectionState.STATE_CONNECTING ;
			MainActivity mainActivity = MainActivity.getMainActivity() ;
			if (mainActivity == null)
			{
				return ; 
			}
			StreamsDrawerListAdapter streamsDrawerListAdapter = mainActivity.getmStreamsDrawerListAdapter() ;
			if (streamsDrawerListAdapter != null)
			{
				streamsDrawerListAdapter.notifyDataSetChanged() ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public boolean bleAvailable(MainActivity mainActivity)
	{
		try{
		final BluetoothManager manager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
		if(manager == null) return false;
		// .. and then get adapter from manager
		final BluetoothAdapter adapter = manager.getAdapter();
		if(adapter == null) return false;
		}catch(Exception e){e.printStackTrace();}
		// and then check if BT LE is also available
		boolean hasBle = mainActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		
		return hasBle;
	}
	public void initBleWrapper(MainActivity mainActivity) {
		mGBleWrapper.setmParent(mainActivity) ;

	}
	public static String getGlowDeckBlueTooth()
	{
		
		boolean test = true ; 
		String retVal = "" ; 
		try{
		if (msConnectionState == ConnectionState.STATE_CONNECTING) 
		{
			retVal =  "Connecting ..." ; 
		}
		if (msConnectionState == ConnectionState.STATE_CONNECTED) 
		{
			retVal =  mDeviceName ; 
		}
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "BluetoothBleManager::getGlowDeckBlueTooth::retString=" + retVal) ; 
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
}
