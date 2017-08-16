package com.plsco.glowdeck.orders;
/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: OrdersSectionItem.java
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
 * The OrdersSectionItem  implements OrdersItem
 * 
 *
 */
public class OrdersSectionItem implements OrdersItem {

	private final String orderNumber;
	private final String orderStatus;
	private final String trackingNumber;

	/**
	 * @param orderNum  - the order number
	 * @param orderStat - status (shipping?)
	 * @param trackingNum - the tracking #
	 */
	public OrdersSectionItem(String orderNum, String orderStat, String trackingNum) {
		this.orderNumber = orderNum;
		this.orderStatus = orderStat ; 
		this.trackingNumber = trackingNum ; 
	}

	public String getOrderNumber(){
		return orderNumber;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	@Override
	public boolean isSection() {
		return true;
	}

}