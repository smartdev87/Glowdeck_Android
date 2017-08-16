package com.plsco.glowdeck.auth;
import com.google.gson.annotations.Expose;
/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamsUserSignUp.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * The StreamsUserSignUp() supports the StreamsUserSignUp info.
 * This info is transmitted in json and the class members are marked up so that 
 * they are automatically populated by gson.
 * 
 *  
 *
 */

/**
 * 
 *
 */
public class StreamsUserSignUp {


	
	@Expose 
	private String status ;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Expose 
	private String msg ;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	
	
}


