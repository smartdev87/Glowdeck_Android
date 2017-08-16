package com.plsco.glowdeck.services;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluecreation.melody.SppService;

public class BTstatusReceiver extends BroadcastReceiver {

	 public BTstatusReceiver(){
	        //No initialization code needed
	    }
	 
	 @Override
		public void onReceive(Context context, Intent intent) {
		 try{
			int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

			if (newState == BluetoothAdapter.STATE_OFF) {
				SppService.getInstance().stop();
			} else if (newState == BluetoothAdapter.STATE_ON) {
				SppService.getInstance().start();
			}
		 }catch(Exception e){e.printStackTrace();}
		}
	 
	 
	 

}
