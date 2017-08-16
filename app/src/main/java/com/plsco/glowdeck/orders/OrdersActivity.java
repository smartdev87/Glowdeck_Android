package com.plsco.glowdeck.orders;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount.OrderClass;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount.OrdersClass;

import java.util.ArrayList;

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: OrdersActivity.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */

/**
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 */

/**
 * History
 * Prepare for Google Play Store 11/1/14
 */


/**
 * The OrdersActivity extends ListActivity
 * 
 *
 */
public class OrdersActivity extends ListActivity{

	// Globals
	ArrayList<OrdersItem> mOrderItems ;
	Activity mActivity ;
	StreamsUser mStreamsUser = null ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			try{
			onBackPressed() ;
			}catch(Exception e){e.printStackTrace();}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{    //  return to the calling mActivity

		super.onBackPressed() ;
		try{
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
		mOrderItems  = new ArrayList<OrdersItem>();
		mActivity = this ;


		setTheme(R.style.Theme_Holo_Black_DarkActionBar); //    Theme_Holo_Dark_DarkActionBar);
		setTitle("Orders");
	//	getActionBar().setDisplayHomeAsUpEnabled(true);

		boolean ordersAdded = false ;
		if (mStreamsUser == null)
		{

			mStreamsUser = VerifyCredentialsTask.getStreamsUser() ;
			ArrayList<OrdersClass> orders =
					mStreamsUser.getStreamsAccount().getOrders() ;
			if (orders != null)
			{

				for (int i = 0 ; i < orders.size() ; i++)
				{
					ordersAdded = true ;
					mOrderItems.add(new OrdersSectionItem(orders.get(i).getOrderNumber(),orders.get(i).getStatus(), orders.get(i).getTrackingNumber() ));
					ArrayList<OrderClass> order = orders.get(i).getOrder() ;
					if (order != null)
					{
						for (int j = 0 ; j < order.size() ; j++)
						{
							try{
							mOrderItems.add(new OrdersEntryItem(order.get(j).getItem(), order.get(j).getQuantity()));
							}catch(Exception e){e.printStackTrace();}
						}
					}

				}


			}
			if (!ordersAdded)
			{
				try{
				mOrderItems.add(new OrdersSectionItem("","","")) ;
				}catch(Exception e){e.printStackTrace();}
			}
			try{
			OrdersEntryAdapter adapter = new OrdersEntryAdapter(this, mOrderItems);

			setListAdapter(adapter);
			}catch(Exception e){e.printStackTrace();}
		}
		}catch(Exception e){e.printStackTrace();}
	}


}
