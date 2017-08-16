package com.plsco.glowdeck.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.task.SendServerSettingsChanged;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;
/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamsSettingsActivity.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * 11/1/14 - prepare for Google Play store
 */

/**
 * The StreamsSettingsActivity extends Activity
 *  
 *  
 *
 */

public class StreamsSettingsActivity extends Activity {
	StreamsUser streamsUser ;
	static boolean stub_note = false ; 

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		SendServerSettingsChanged.CheckAndSendServerSettingsChanged() ;
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
		streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_stream_settings);


		//ArrayList<String> list = new ArrayList<String>();

		//final StableArrayAdapter adapter = new StableArrayAdapter(this,
			//	R.layout.fragment_setting_top, list);

		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 *  
	 
	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}
	}
	*/
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		try{
		getMenuInflater().inflate(R.menu.stream_settings, menu);
		menu.findItem(R.id.action_settings).setVisible(false);
		}catch(Exception e){e.printStackTrace();}
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent mActivity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) 
		{

		case android.R.id.home:
			try{
			MainActivity.setStreamState(StreamsScreenState.STREAMS_VIEW) ;
			Intent intent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, intent) ;
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}catch(Exception e){e.printStackTrace();}
			//savePrefs() ; 
			return true;

		}



		return super.onOptionsItemSelected(item);

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle state) {
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","StreamSettingActivity::onSaveInstanceState") ;
		}
		super.onSaveInstanceState(state);
		try{
		state.putSerializable(MainActivity.STREAMS_STATE, MainActivity.getStreamsState().ordinal());
		state.putSerializable(MainActivity.STREAMS_TYPE, MainActivity.getStreamsType().ordinal());
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{

		try{
		MainActivity.setStreamState(StreamsScreenState.STREAMS_VIEW) ;

		Intent intent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, intent) ;

		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}catch(Exception e){e.printStackTrace();}



	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public  void onDestroy()
	{
		super.onDestroy() ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","onDestroy in StreamSettingActivity") ;
		}

	}

}
