package com.plsco.glowdeck.streamdata;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StreamsStream.java
 * 
 *  Copyright 2014. PLSCO, Inc. All rights reserved.
 * 
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * StreamsStream   
 *                            
 *
 */
public class StreamsStream {

	@Expose
	private String  status ; 
	public String getstatus() {
		return status;
	}
	public void setstatus(String status) {
		this.status = status;
	}

	@Expose
	private String  Status ;
	public String getStatus() {
		return Status;
	}
	public void setStatus(String Status) {
		this.Status = Status;
	}
	public static class Weather 
	{
		@Expose 
		private String  City ;

		@Expose 
		private String  Temp ;

		@Expose 
		private String  Units ;

		@Expose 
		private String  Conditions;


		public String getCity() {
			return City;
		}

		public void setCity(String city) {
			City = city;
		}

		public String getTemp() {
			return Temp;
		}

		public void setTemp(String temp) {
			Temp = temp;
		}

		public String getUnits() {
			return Units;
		}

		public void setUnits(String units) {
			Units = units;
		}

		public String getConditions() {
			return Conditions;
		}

		public void setConditions(String conditions) {
			Conditions = conditions;
		}


	}
	public static class Stream 
	{
		@Expose 
		private String  Type ;

		public String getType() {
			return Type;
		}

		public void setType(String type) {
			Type = type;
		}

		@Expose 
		private String  User ;

		public String getUser() {
			return User;
		}

		public void setUser(String user) {
			User = user;
		}

		@Expose 
		private String  Source ;

		public String getSource() {
			return Source;
		}

		public void setSource(String source) {
			Source = source;
		}

		@Expose 
		private String  From ;



		public String getFrom() {
			return From;
		}

		public void setFrom(String from) {
			From = from;
		}

		@Expose 
		private String  Article ;

		public String getArticle() {
			return Article;
		}

		public void setArticle(String article) {
			Article = article;
		}

		@Expose 
		private String  Subject ;

		public String getSubject() {
			return Subject;
		}

		public void setSubject(String subject) {
			Subject = subject;
		}

		@Expose 
		private String  MessageSnippet ;

		public String getMessageSnippet() {
			return MessageSnippet;
		}

		public void setMessageSnippet(String messageSnippet) {
			MessageSnippet = messageSnippet;
		}

		@Expose 
		private String  ScreenName ;

		public String getScreenName() {
			return ScreenName;
		}

		public void setScreenName(String screenName) {
			ScreenName = screenName;
		}

		@Expose 
		private String  Tweet ;

		public String getTweet() {
			return Tweet;
		}

		public void setTweet(String tweet) {
			Tweet = tweet;
		}

		@Expose 
		private int  Read ;


		public int getRead() {
			return Read;
		}

		public void setRead(int read) {
			Read = read;
		}

		@Expose 
		private String  Keyword ;


		public String getKeyword() {
			return Keyword;
		}

		public void setKeyword(String keyword) {
			Keyword = keyword;
		}

		@Expose 
		private Integer  Timestamp ;

		public Integer getTimestamp() {
			return Timestamp;
		}

		public void setTimestamp(Integer timestamp) {
			Timestamp = timestamp;
		}
		@Expose 
		private String  URL ;

		public String getURL() {
			return URL;
		}

		public void setURL(String uRL) {
			URL = uRL;
		}
		@Expose 
		private String  FullURL ;

		public String getFullURL() {
			return FullURL;
		}

		public void setFullURL(String fullURL) {
			FullURL = fullURL;
		}
		@Expose 
		private String  Content ;

		public String getContent() {
			return Content;
		}

		public void setContent(String content) {
			Content = content;
		}

		@Expose 
		private String  ID ;

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}
	}
	@Expose 
	@SerializedName("Weather")  private Weather theWeather;


	public Weather getTheWeather() {
		return theWeather;
	}
	public void setTheWeather(Weather theWeather) {
		this.theWeather = theWeather;
	}

	@Expose 
	@SerializedName("Stream")  private Stream theStream[];
	public Stream[] getTheStream() {
		return theStream;
	}
	public void setTheStream(Stream[] theStream) {
		this.theStream = theStream;
	}

}
