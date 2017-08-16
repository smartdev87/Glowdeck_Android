package com.plsco.glowdeck.ui;

/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: FormsPersistentData.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * FormsPersistentData - keeps fields active for form entry
 *                           
 *
 */
public class FormsPersistentData {
	//
	//  Globals 
	//
	// Persistent com.glowdeck.streams.streamdata for login screen 
	private String  					mLoginUserNameEdit  ; 
	private String  					mPasswordEdit  ; 
	private Boolean 					mLoginRememberMeCB   ;
	// Persistent com.glowdeck.streams.streamdata for create Account screen 
	private String  					mCreateAcctEmailEdit  ; 
	private String  					mCreateAcctFirstNameEdit  ; 
	private String  					mCreateAcctLastNameEdit  ; 
	private String  					mCreateAcctPasswordEdit  ; 
	private String  					mCreateAcctPasswordConfirmEdit  ; 

	public String getCreateAcctPasswordEdit() {
		return mCreateAcctPasswordEdit;
	}
	public void setCreateAcctPasswordEdit(String createAcctPasswordEdit) {
		this.mCreateAcctPasswordEdit = createAcctPasswordEdit;
	}
	public String getCreateAcctPasswordConfirmEdit() {
		return mCreateAcctPasswordConfirmEdit;
	}
	public void setCreateAcctPasswordConfirmEdit(String createAcctPasswordConfirmEdit) {
		this.mCreateAcctPasswordConfirmEdit = createAcctPasswordConfirmEdit;
	}
	//
	private static FormsPersistentData self = null ;
	public  static FormsPersistentData getInstance()
	{

		try{
		if (self == null)
		{
			self = new FormsPersistentData() ;
		}
		}catch(Exception e){e.printStackTrace();}
		return self ; 

	}
	public static void clear()
	{
		if (self != null)
		{
			self = null ; 
		}
	}
	private FormsPersistentData()
	{
		try{
		//
		mLoginUserNameEdit = new String("") ; 
		mPasswordEdit = new String("") ; 
		mLoginRememberMeCB = new Boolean(true) ;
		//
		mCreateAcctEmailEdit = new String("") ; 
		mCreateAcctFirstNameEdit  = new String("")  ; 
		mCreateAcctLastNameEdit  = new String("")  ; 
		mCreateAcctPasswordEdit  = new String("") ; 
		mCreateAcctPasswordConfirmEdit = new String("")  ; 
		}catch(Exception e){e.printStackTrace();}
	}

	// getters & setters for login screen values
	public Boolean getLoginRememberMeCB() {
		return mLoginRememberMeCB;
	}
	public void setLoginRememberMeCB(Boolean loginRememberMeCB) {
		this.mLoginRememberMeCB = loginRememberMeCB;
	}

	public String getPasswordEdit() {
		return mPasswordEdit;
	}
	public void setPasswordEdit(String passwordEdit) {
		this.mPasswordEdit = passwordEdit;
	}
	public String getLoginUserNameEdit() {
		return mLoginUserNameEdit;
	}
	public void setLoginUserNameEdit(String loginUserNameEdit) {
		this.mLoginUserNameEdit = loginUserNameEdit;
	}
	// getters & setters for new account screen values
	public String getCreateAcctEmailEdit() {
		return mCreateAcctEmailEdit;
	}
	public void setCreateAcctEmailEdit(String createAcctEmailEdit) {
		this.mCreateAcctEmailEdit = createAcctEmailEdit;
	}
	public String getCreateAcctFirstNameEdit() {
		return mCreateAcctFirstNameEdit;
	}
	public void setCreateAcctFirstNameEdit(String createFirstNameEdit) {
		this.mCreateAcctFirstNameEdit = createFirstNameEdit;
	}
	public String getCreateAcctLastNameEdit() {
		return mCreateAcctLastNameEdit;
	}
	public void setCreateAcctLastNameEdit(String createLastNameEdit) {
		this.mCreateAcctLastNameEdit = createLastNameEdit;
	}




}
