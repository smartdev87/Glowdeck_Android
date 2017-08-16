package com.plsco.glowdeck.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.plsco.glowdeck.drawer.StreamsFragment;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.ui.MainActivity.StreamsScreenState;


/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: EmailViewActivity.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * EmailViewActivity extends Activity
 *                            
 *
 */
public class EmailViewActivity extends Activity {





	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

		try{
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

		}
		}catch(Exception e){e.printStackTrace();}
		super.onResume();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		try{
		Intent intent = new Intent(this, MainActivity.class);
		NavUtils.navigateUpTo(this, intent) ;
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}catch(Exception e){e.printStackTrace();}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		try{
		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.emailview_layout);


		Bundle extras = getIntent().getExtras();
		String fromStr = "" ;
		String subjectStr = "" ;
		String contentStr = "" ;
		String title = "" ; 
		if (extras != null) {
			fromStr = extras.getString(StreamsFragment.E_FROM,"");
			subjectStr = " | " + extras.getString(StreamsFragment.E_SUBJECT,"");
			contentStr = extras.getString(StreamsFragment.E_CONTENT,"") ;

		}
		int indx = fromStr.indexOf('<') ;
		if (indx > 0)
		{


			fromStr = fromStr.substring(0, indx -1) ;
			title = fromStr +  subjectStr ; 
		}
		else
		{
			title = fromStr ; 

		}


		this.setTitle(title);
		TextView theContentTV = (TextView) findViewById(R.id.email_content_textView);

		theContentTV.setText(contentStr) ;
		}catch(Exception e){e.printStackTrace();}
	}




	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

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
			return true;

		}



		return super.onOptionsItemSelected(item);

	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}




}
