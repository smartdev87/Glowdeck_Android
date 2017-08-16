package com.plsco.glowdeck.orders;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.plsco.glowdeck.R;

import java.util.ArrayList;
/**
*
* Project : GlowDeck/STREAMS
* FileName: OrdersEntryAdapter.java
*
* Copyright 2014. PLSCO, Inc. All rights reserved.
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
* The OrdersEntryAdapter extends ArrayAdapter
* 
*
*/

public class OrdersEntryAdapter extends ArrayAdapter<OrdersItem>{

	// Globals
	//
	//private Context mContext;
	private ArrayList<OrdersItem> mItems;
	private LayoutInflater mVI;
	private TypedArray mStreamsOrderIcons  = null ;
	private String[]   mOrderIconsKeywords = null;

	/**
	 * @param mContext - passed to super constructor
	 * @param items - stored in global arraylist
	 */
	public OrdersEntryAdapter(Context context,ArrayList<OrdersItem> items) {
		super(context,0, items);
		//this.mContext = mContext;
		try{
		this.mItems = items;

		mStreamsOrderIcons = context.getResources()
				.obtainTypedArray(R.array.streams_orders_icons) ;
		mOrderIconsKeywords = context.getResources().getStringArray(R.array.streams_orders_icons_keywords);

		mVI = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}catch(Exception e){e.printStackTrace();}
	}


	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		try{
		final OrdersItem i = mItems.get(position);
		if (i != null) {
			if(i.isSection()){
				OrdersSectionItem si = (OrdersSectionItem)i;
				v = mVI.inflate(R.layout.orders_section_list_item, null);


				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				
				
				final TextView orderNumber = (TextView) v.findViewById(R.id.list_item_section_order_number);
				final TextView orderTracking = (TextView) v.findViewById(R.id.list_item_section_order_tracking);
				final TextView orderStatus = (TextView) v.findViewById(R.id.list_item_section_order_status);
				
				
				if ((si.getOrderNumber().compareTo("") == 0) && 
						(si.getOrderStatus().compareTo("") == 0))
				{
					
					orderNumber.setText("---");
					orderStatus.setText("You have no open orders");
									}
				else
				{
					orderNumber.setText("Order number: " + si.getOrderNumber());
					orderStatus.setText("Order Status: " + si.getOrderStatus());
					String trNum = (si.getTrackingNumber().compareTo("") == 0 ) ? "N/A" : si.getTrackingNumber() ;
					orderTracking.setText("Tracking: " + trNum ) ;

				}

			}else{

				OrdersEntryItem ei = (OrdersEntryItem)i;
				v = mVI.inflate(R.layout.orders_entry_list_item, null);
				final TextView desc = (TextView)v.findViewById(R.id.list_item_entry_desc);
				final TextView count = (TextView)v.findViewById(R.id.list_item_entry_count);
				int resId = mStreamsOrderIcons.getResourceId(0, -1) ;
				if (desc != null) 
				{
					desc.setText(ei.getDesc());
					for (int j = 0 ; j <  mStreamsOrderIcons.length() ; j++)
					{
						if (ei.getDesc().startsWith(mOrderIconsKeywords[j]))
						{
							resId = mStreamsOrderIcons.getResourceId(j, -1) ;
							break ;
						}
					}

				}
				if (count != null)
				{
					count.setText("Qty: " + ei.getCount());
				}


				ImageView itemImage  = (ImageView) v.findViewById(R.id.list_item_entry_product_image); 
				if (itemImage != null)
				{
					itemImage.setImageResource(resId) ;
				}

			}
		}
		}catch(Exception e){e.printStackTrace();}
		return v;
	}

}
