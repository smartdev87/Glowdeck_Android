package com.plsco.glowdeck.orders;

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: OrdersEntryItem.java
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
 * The OrdersEntryItem implements OrdersItem
 * 
 *
 */
public class OrdersEntryItem implements OrdersItem {

	public String getDesc() {
		return desc;
	}

	public String getCount() {
		return count;
	}

	private final String desc;
	private final String count;

	public OrdersEntryItem(String desc, String count) {
		this.desc = desc;
		this.count = count;
	}

	@Override
	public boolean isSection() {
		return false;
	}

}