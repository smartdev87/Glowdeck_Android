package com.plsco.glowdeck.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamsUser.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */
/**
 * The StreamsUser() supports the StreamsUSer info.
 * This is the user info that come down from the server that identifies the info for the current user
 *  such as account, orders, etc.
 * 
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
public class StreamsUser   {

	private static boolean createNewUserInProgress = false  ;


	public boolean isCreateNewUserInProgress() {
		return createNewUserInProgress;
	}
	public void setCreateNewUserInProgress(boolean createNewUserInProgress) {
		StreamsUser.createNewUserInProgress = createNewUserInProgress;
	}

	private ModifiedSettings modifiedSettings ; 

	public ModifiedSettings getModifiedSettings() {
		return modifiedSettings;
	}
	public void setModifiedSettings(ModifiedSettings modifiedSettings) {
		this.modifiedSettings = modifiedSettings;
	}

	/**
	 * ModifiedSettings is used internally to detect changes to the settings
	 * when the settings have been changed, they need to be synchoronized with the 
	 * server. 
	 *
	 */
	public class ModifiedSettings 
	{
		private boolean emailSettingsModified ; 
		private boolean twitterSettingsModified ; 
		private boolean newsSettingsModified ; 
		private boolean instagramSettingsModified ; 
		private boolean facebookSettingsModified ; 
		private boolean weatherSettingsModified ; 
		

		public boolean isWeatherSettingsModified() {
			return weatherSettingsModified;
		}
		public void setWeatherSettingsModified(boolean weatherSettingsModified) {
			this.weatherSettingsModified = weatherSettingsModified;
		}
		public boolean isFacebookSettingsModified() {
			return facebookSettingsModified;
		}
		public void setFacebookSettingsModified(boolean facebookSettingsModified) {
			this.facebookSettingsModified = facebookSettingsModified;
		}
		public boolean isInstagramSettingsModified() {
			return instagramSettingsModified;
		}
		public void setInstagramSettingsModified(boolean instagramSettingsModified) {
			this.instagramSettingsModified = instagramSettingsModified;
		}
		public boolean isEmailSettingsModified() {
			return emailSettingsModified;
		}
		public void setEmailSettingsModified(boolean emailSettingsModified) {
			this.emailSettingsModified = emailSettingsModified;
		}
		public boolean isTwitterSettingsModified() {
			return twitterSettingsModified;
		}
		public void setTwitterSettingsModified(boolean twitterSettingsModified) {
			this.twitterSettingsModified = twitterSettingsModified;
		}
		public boolean isNewsSettingsModified() {
			return newsSettingsModified;
		}
		public void setNewsSettingsModified(boolean newsSettingsModified) {
			this.newsSettingsModified = newsSettingsModified;
		}

		public boolean anyModificationsPending()
		{


			return emailSettingsModified || twitterSettingsModified || newsSettingsModified || instagramSettingsModified || facebookSettingsModified ;
		}

	}



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

		@Expose
		private ArrayList<OrdersClass> Orders ;

		public ArrayList<OrdersClass> getOrders() {
			return Orders;
		}

		public void setOrders(ArrayList<OrdersClass> orders) {
			Orders = orders;
		}

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


		public class OrdersClass
		{


			@Expose
			private String OrderNumber ;

			@Expose
			private String Status ;

			@Expose
			private String TrackingNumber ;

			@Expose
			private ArrayList<OrderClass> Order ;


			public String getOrderNumber() {
				return OrderNumber;
			}

			public String getStatus() {
				return Status;
			}

			public void setStatus(String status) {
				Status = status;
			}

			public void setOrderNumber(String orderNumber) {
				OrderNumber = orderNumber;
			}

			public String getTrackingNumber() {
				return TrackingNumber;
			}

			public void setTrackingNumber(String trackingNumber) {
				TrackingNumber = trackingNumber;
			}

			public ArrayList<OrderClass> getOrder() {
				return Order;
			}

			public void setOrder(ArrayList<OrderClass> order) {
				Order = order;
			}
		}
		public class OrderClass
		{
			@Expose
			private String Quantity ;

			@Expose
			private String Item ;



			public String getQuantity() {
				return Quantity;
			}

			public void setQuantity(String quantity) {
				Quantity = quantity;
			}

			public String getItem() {
				return Item;
			}

			public void setItem(String item) {
				Item = item;
			}


		}

	}
	@Expose 
	@SerializedName("Streams Account")  private StreamsAccount streamsAccount;


	public static class Calls
	{
		static class Color
		{
			@Expose 
			private String Left ;
			@Expose 
			private String Right ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}

		@Expose 
		private String Enabled ; 
		@Expose 
		private String  Light ;
		@Expose 
		@SerializedName("Color")  private Color color;
		Calls()
		{

		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}


		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}

		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 


	}
	@Expose 
	@SerializedName("Calls")  private Calls calls;

	public Calls getCalls() {
		return calls;
	}
	public void setCalls(Calls calls) {
		this.calls = calls;
	}

	public static class Messages
	{
		static class Color
		{
			@Expose 
			private String Left ;
			@Expose 
			private String Right ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}

		@Expose 
		private String Enabled ; 

		@Expose 
		private String  Light ;
		@Expose 
		@SerializedName("Color")  private Color color;


		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}
		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}


		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 
		Messages()
		{

		}


	}
	@Expose 
	@SerializedName("Messages")  private Messages messages;

	public Messages getMessages() {
		return messages;
	}
	public void setMessages(Messages messages) {
		this.messages = messages;
	}



	public static class Email 
	{
		static class Color
		{
			@Expose 
			private String Left ;
			@Expose 
			private String Right ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}
		@Expose 
		@SerializedName("Color")  private Color color;
		@Expose 
		private String  Light ;
		@Expose 
		private String[]  Account  ;
		@Expose 
		private String Enabled ; 



		public Color getColor() {
			return color;
		}


		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}
		public void setColor(Color color) {
			this.color = color;
		}


		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 



		public String[] getAccount() {
			return Account;
		}

		public void setAccount(String[] account) {
			Account = account;
		}

		Email()
		{

		}
	}
	@Expose 
	@SerializedName("Email")  private Email email;

	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}

	public static class News 
	{
		@Expose 
		private String[]  Keywords  ;
		static class Color
		{
			@Expose 
			private String Left ;
			@Expose 
			private String Right ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}
		@Expose 
		@SerializedName("Color")  private Color color;
		@Expose 
		private String  Light ;
		@Expose 
		private String Enabled ; 




		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}


		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 



		public void setKeywords(String[] keywords) {
			this.Keywords = keywords;
		}

		public String[] getKeywords() {
			return Keywords;
		}

		News()
		{

		}
	}
	@Expose 
	@SerializedName("News")  private News news;

	public News getNews() {
		return news;
	}
	public void setNews(News news) {
		this.news = news;
	}

	public static class Weather 
	{
		@Expose 
		private String Enabled ; 
		public String getEnabled() {
			return Enabled;
		}
		public void setEnabled(String enabled) {
			Enabled = enabled;
		}

		@Expose 
		private String  Light ;

		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 
		public static class Location
		{
			@Expose 
			private String City ; 
			@Expose 
			private String Zip ; 

			public String getCity() {
				return City;
			}

			public void setCity(String city) {
				City = city;
			}

			public String getZip() {
				return Zip;
			}

			public void setZip(String zip) {
				Zip = zip;
			}

			Location()
			{

			}

		}
		@Expose 
		@SerializedName("Location")  private Location location;

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}
		static class Color
		{ 

			@Expose 
			private String Left ;
			@Expose 
			private String Right ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}



		@Expose 
		@SerializedName("Color")  private Color color;

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		Weather()
		{

		}
	}
	@Expose 
	@SerializedName("Weather")  private Weather weather;

	public Weather getWeather() {
		return weather;
	}
	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public static class Twitter 
	{
		static class Color
		{
			@Expose 
			private String Right ;
			@Expose 
			private String Left ;

			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}
		@Expose 
		@SerializedName("Color")  private Color color;
		@Expose 
		private String  Light ;
		@Expose 
		private String[]  Account  ;
		@Expose 
		private String Enabled ; 

		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}


		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 



		public String[] getAccount() {
			return Account;
		}

		public void setAccount(String[] account) {
			Account = account;
		}

		Twitter()
		{

		}
	}
	@Expose 
	@SerializedName("Twitter")  private Twitter twitter;

	public Twitter getTwitter() {
		return twitter;
	}
	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}


	public static class Facebook 
	{
		static class Color
		{
			@Expose 
			private String Right ;
			@Expose 
			private String Left ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}

		@Expose 
		private String Enabled ; 
		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}

		@Expose 
		private String  Light ;
		@Expose 
		private String[]  Account  ;
		@Expose 
		@SerializedName("Color")  private Color color;

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}


		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 



		public String[] getAccount() {
			return Account;
		}

		public void setAccount(String[] account) {
			Account = account;
		}

		Facebook()
		{

		}
	}
	@Expose 
	@SerializedName("Facebook")  private Facebook facebook;

	public Facebook getFacebook() {
		return facebook;
	}
	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}

	public static class Instagram 
	{
		static class Color
		{
			@Expose 
			private String Left ;
			@Expose 
			private String Right ;
			@Expose 
			private String Front ;
			Color()
			{

			}
			public String getLeft() {
				return Left;
			}
			public void setLeft(String left) {
				Left = left;
			}
			public String getRight() {
				return Right;
			}
			public void setRight(String right) {
				Right = right;
			}
			public String getFront() {
				return Front;
			}
			public void setFront(String front) {
				Front = front;
			}

		}

		@Expose 
		private String Enabled ; 
		public String getEnabled() {
			return Enabled;
		}

		public void setEnabled(String enabled) {
			Enabled = enabled;
		}

		@Expose 
		private String  Light ;
		@Expose 
		private String[]  Account  ;
		@Expose 
		@SerializedName("Color")  private Color color;

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}


		public String getLight() {
			return Light;
		}
		public void setLight(String light) {
			this.Light = light;
		} 



		public String[]  getAccount() {
			return Account;
		}

		public void setAccount(String[]  account) {
			Account = account;
		}

		Instagram()
		{

		}
	}
	@Expose 
	@SerializedName("Instagram")  private Instagram instagram;

	public Instagram getInstagram() {
		return instagram;
	}
	public void setInstagram(Instagram instagram) {
		this.instagram = instagram;
	}

	StreamsUser()
	{
		try{
		modifiedSettings = new ModifiedSettings() ; 

		modifiedSettings.setEmailSettingsModified (false) ; 
		modifiedSettings.setNewsSettingsModified (false) ; 
		modifiedSettings.setTwitterSettingsModified (false) ; 
		modifiedSettings.setInstagramSettingsModified (false) ; 
		}catch(Exception e){e.printStackTrace();}


	}
	@Expose
	String  status ; 
	@Expose
	String  Status ;


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

