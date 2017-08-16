package com.plsco.glowdeck.auth;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 *
 * Project: Streams Android Implementation
 *
 * file: StreamsUserLogin.java
 *
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * The StreamsUserLogin() supports the StreamsUSerLogin info.
 * This is the user account info 
 * the info is transmitted in json and the class members are marked up so that 
 * they are automatically populated by gson.
 * 
 *  
 *
 */

/**
 * 
 *
 */
public class StreamsUserLogin {

	@Expose
	String  status ; 
	@Expose
	String  Status ;

	public static class StreamsAccount
	{
		@Expose
		private String ID ;
		@Expose
		private String Email ;
		@Expose
		private Date Registered;
		@Expose
		@SerializedName("First Name") private String FirstName;
		@Expose
		@SerializedName("Last Name") private String LastName;
		@Expose
		private String Phone;
		@Expose
		private String Token;
		@Expose
		private String Address_1;
		@Expose
		private String Address_2;
		@Expose
		private String Address_3;
		@Expose
		private String City;
		@Expose
		private String State;
		@Expose
		private String ZipCode ;
		@Expose
		private String Country ;



		StreamsAccount()
		{

		}

		public String getFirstName() {
			return FirstName;
		}

		public void setFirstName(String firstName) {
			FirstName = firstName;
		}

		public String getLastName() {
			return LastName;
		}

		public void setLastName(String lastName) {
			LastName = lastName;
		}

		public String getPhone() {
			return Phone;
		}

		public void setPhone(String phone) {
			Phone = phone;
		}

		public String getToken() {
			return Token;
		}

		public void setToken(String token) {
			Token = token;
		}

		public String getAddress_1() {
			return Address_1;
		}

		public void setAddress_1(String address_1) {
			Address_1 = address_1;
		}

		public String getAddress_2() {
			return Address_2;
		}

		public void setAddress_2(String address_2) {
			Address_2 = address_2;
		}

		public String getAddress_3() {
			return Address_3;
		}

		public void setAddress_3(String address_3) {
			Address_3 = address_3;
		}

		public String getCity() {
			return City;
		}

		public void setCity(String city) {
			City = city;
		}

		public String getState() {
			return State;
		}

		public void setState(String state) {
			State = state;
		}

		public String getZipCode() {
			return ZipCode;
		}

		public void setZipCode(String zipCode) {
			ZipCode = zipCode;
		}

		public String getCountry() {
			return Country;
		}

		public void setCountry(String country) {
			Country = country;
		}

		public String getID() {
			return ID;
		}
		public void setID(String iD) {
			ID = iD;
		}
		public String getEmail() {
			return Email;
		}
		public void setEmail(String email) {
			Email = email;
		}
		public Date getRegistered() {
			return Registered;
		}
		public void setRegistered(Date registered) {
			Registered = registered;
		}

	}
	@Expose 
	@SerializedName("Streams Account")  private StreamsAccount streamsAccount;

	public String getstatus() {
		return status;
	}
	public void setstatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		this.Status = status;
	}

	public StreamsAccount getStreamsAccount() {
		return streamsAccount;
	}
	public void setStreamsAccount(StreamsAccount streamsAccount) {
		this.streamsAccount = streamsAccount;
	} 


}



