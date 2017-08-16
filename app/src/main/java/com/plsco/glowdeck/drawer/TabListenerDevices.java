package com.plsco.glowdeck.drawer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.ui.MainActivity.BlueToothdeviceTab;


/**
 * @author jdiamand
 * NOTE : The TabListenerDevices Class is no longer being used in the Streams APP
 */
public class TabListenerDevices implements ActionBar.TabListener {

	Fragment fragment;
	enum BT_TAB_SELECTION {
		Select,
		UnSelect,
		Reselect 
	}
	public TabListenerDevices(Fragment fragment) {
		// TODO Auto-generated constructor stub
		this.fragment = fragment;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//ft.replace(R.id.fragment_container, fragment);
		/*
		if (!fragment.isAdded())
		{
			return ; 
		}
		*/		
		if (StreamsApplication.DEBUG_MODE)
		{
			
			Log.d("dbg","TabListenerDevices::onTabSelected::" + tab.getText().toString());
		}
		try{
		processSelection(BT_TAB_SELECTION.Select,  tab) ;
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//ft.remove(fragment);
		
		if (StreamsApplication.DEBUG_MODE)
		{
			
			Log.d("dbg","TabListenerDevices::onTabUnselected::" + tab.getText().toString());
		}
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		try{
		if (!fragment.isAdded())
		{
			return ; 
		}
		if (StreamsApplication.DEBUG_MODE)
		{
			
			Log.d("dbg","TabListenerDevices::onTabReselected::" + tab.getText().toString());
		}
		processSelection(BT_TAB_SELECTION.Reselect,  tab) ;
		}catch(Exception e){e.printStackTrace();}
	}
	void processSelection(BT_TAB_SELECTION selection, Tab tab)
	{
		try{
		String wifiTab = MainActivity.getMainActivity().getResources()
				.getString(R.string.device_WiFi_tab) ;
		String lightsTab = MainActivity.getMainActivity().getResources()
				.getString(R.string.device_Lights_tab) ;

		if (tab.getText().toString().compareTo(wifiTab)==0)
		{    // current selected tab = wifi
			if (MainActivity.getmBlueToothdeviceTab() == BlueToothdeviceTab.WIFI_TAB )
			{
				
			}
			if (MainActivity.getmBlueToothdeviceTab() == BlueToothdeviceTab.LIGHTS_TAB )
			{
				MainActivity.setmBlueToothdeviceTab(BlueToothdeviceTab.WIFI_TAB) ;
				
				MainActivity.getMainActivity().displayView(MainActivity.DRAWER_GLOWDECK_DEVICES0) ;
			}
		}
		if (tab.getText().toString().compareTo(lightsTab)==0)
		{
			if (MainActivity.getmBlueToothdeviceTab() == BlueToothdeviceTab.LIGHTS_TAB )
			{
				
			}
			if (MainActivity.getmBlueToothdeviceTab() == BlueToothdeviceTab.WIFI_TAB )
			{
				MainActivity.setmBlueToothdeviceTab(BlueToothdeviceTab.LIGHTS_TAB) ;
				 
				MainActivity.getMainActivity().displayView(MainActivity.DRAWER_GLOWDECK_DEVICES0) ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
}
