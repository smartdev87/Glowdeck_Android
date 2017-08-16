package com.plsco.glowdeck.drawer;

/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * FileName:  StreamsDrawerItem.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *   
 */

/**
 * History
 * Prepare for Google Play Store 11/1/14
 */



/**
 * StreamsDrawerItem  
 * 
 *
 */
public class StreamsDrawerItem {



	private String title;
	private int icon;
	//private String count = "0";
	//private boolean isCounterVisible = false;

	public StreamsDrawerItem(){}

	public StreamsDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}

	/**
	 * @param title - title to use
	 * @param icon  - icon to use
	 * @param isCounterVisible - for devices
	 * @param count - # of devices
	 */
	public StreamsDrawerItem(String title, int icon, boolean isCounterVisible, String count){
		this.title = title;
		this.icon = icon;
		//this.isCounterVisible = isCounterVisible;
		//this.count = count;
	}

	public String getTitle(){
		return this.title;
	}

	public int getIcon(){
		return this.icon;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public void setIcon(int icon){
		this.icon = icon;
	}

}
