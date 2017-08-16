package com.plsco.glowdeck.streamdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.ui.MainActivity.StreamsType;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: StatusStream.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 * 
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * StatusStream  
 *                           
 *
 */


public class StatusStream  {

	//    Constants
	static final String TAG = "dbg";
	static final String DB_NAME = "stream.db";  
	static final int DB_VERSION =  StreamsApplication.streamsDBversion ;
	//     
	//
	public static final String  PERSONAL_STREAMS_TABLE = "PersonalStream";  
	public static final String  TYPE_FOR_PERSONAL_STREAMS_TABLE = "Personal"; 
	//
	public static final String  SOCIAL_STREAMS_TABLE = "SocialStream";  
	public static final String  TYPE_FOR_SOCIAL_STREAMS_TABLE = "Social"; 
	//
	public static final String  PUBLIC_STREAMS_TABLE = "PublicStream";  
	public static final String  TYPE_FOR_PUBLIC_STREAMS_TABLE = "Public"; 
	//
	//public static final String  NEWS_TABLE = "NewsStream";  
	public static final String  TYPE_FOR_NEWS_TABLE = "news"; 
	public static final String  PUBLIC_NEWS_TYPE = TYPE_FOR_PUBLIC_STREAMS_TABLE + TYPE_FOR_NEWS_TABLE; 
	//
	public static final String  WEATHER_TABLE = "WeatherStream";  
	public static final String  TYPE_FOR_WEATHER_TABLE = "weather"; 
	public static final String  PUBLIC_WEATHER_TYPE = TYPE_FOR_PUBLIC_STREAMS_TABLE + TYPE_FOR_WEATHER_TABLE; 
	//
	//public static final String  EMAILS_TABLE = "EmailStream"; 
	public static final String  TYPE_FOR_EMAIL_TABLE = "email"; 
	public static final String  PERSONAL_EMAIL_TYPE = TYPE_FOR_PERSONAL_STREAMS_TABLE + TYPE_FOR_EMAIL_TABLE; 
	//
	//public static final String  TWITTER_TABLE = "TwitterStream"; 
	public static final String  TYPE_FOR_TWITTER_TABLE = "twitter"; 
	public static final String  SOCIAL_TWITTER_TYPE = TYPE_FOR_SOCIAL_STREAMS_TABLE + TYPE_FOR_TWITTER_TABLE; 

	//public static final String  INSTAGRAM_TABLE = "InstagramStream"; 
	public static final String  TYPE_FOR_INSTAGRAM_TABLE = "instagram"; 
	public static final String  SOCIAL_INSTAGRAM_TYPE = TYPE_FOR_SOCIAL_STREAMS_TABLE + TYPE_FOR_INSTAGRAM_TABLE; 

	//public static final String  FACEBOOK_TABLE = "FacebookStream"; 
	public static final String  TYPE_FOR_FACEBOOK_TABLE = "facebook"; 

	public static final String C_ID = "_id";
	public static final String C_TYPE = "Type";
	public static final String C_SOURCE = "Source";
	public static final String C_ARTICLE = "Article";
	public static final String C_KEYWORD = "Keyword";
	public static final String C_TIMESTAMP = "Timestamp";
	public static final String C_READ = "Read";
	public static final String C_URL = "URL";
	public static final String C_TIMESTAMP_UPDATED = "TimestampUpdated";


	// mail specific
	public static final String E_FROM = "Sender"; // can't use from, sql keyword
	public static final String E_MESSAGESNIPPET = "Msgsnippet" ;
	public static final String E_SUBJECT = "Subject" ;
	public static final String E_CONTENT = "Content" ;
	// twitter specific
	public static final String T_SCREEN_NAME = "ScreenName" ; 
	public static final String T_USER = E_FROM ; // overload the from field
	public static final String T_TWEET = "Tweet" ;
	//private static String[] columns  = {"*"} ;
	// instagram specific
	public static final String I_USER = E_FROM ; // Instagram User 
	public static final String I_CONTENT = E_CONTENT ; // Instagram content:: secondline of the mListView 
	public static final String I_URL_JPG = C_URL ; // Instagram photo url
	public static final String I_FULL_URL = "FullURL" ; // Instagram photo url
	// social specific
	public static final String S_FROM_USER = "FromUser" ;
	public static final String S_URL = "URL" ;
	public static final String S_SCREEN_JPG = "ScreenJPG" ;
	public static final String S_CONTENT    = "Content" ; 
	public static final String C_CREATED_AT = "created_at";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	// public specific
	public static final String P_CITY_SOURCE  = "CitySource" ;
	public static final String P_ARTICLE_UNITS  = "ArticleUnits" ; 
	public static final String P_KEYWORD_TEMPERATURE  = "KeyTemp" ; 
	public static final String P_URL_CONDITIONS = "URLConditions" ;
	// Globals
	Context 										mContext;
	static DbStream 								mDataBaseStream = null;   
	final  SQLiteDatabase 							mDataBase ;

	// Static Selects

	private static final String queryPersonalStreamsTable =
			"SELECT  " + 
					C_ID  + ", " +
					C_TYPE   + ", " +
					E_FROM   + ", " +
					E_MESSAGESNIPPET   + ", " +
					E_SUBJECT + ", " +
					C_TIMESTAMP  + ", " +
					C_READ   + ", " +
					E_CONTENT  + ", " + 
					C_TIMESTAMP_UPDATED + "  " + 
					"FROM " + PERSONAL_STREAMS_TABLE ;


	private static final String querySocialStreamsTable =
			"SELECT  " + 
					C_ID  + ", " +
					C_TYPE   + ", " +
					S_FROM_USER   + ", " +
					S_URL   + ", " +
					S_SCREEN_JPG + ", " +
					C_TIMESTAMP  + ", " +
					C_READ   + ", " +
					S_CONTENT  + ", " + 
					C_TIMESTAMP_UPDATED + "  " + 
					"FROM " + SOCIAL_STREAMS_TABLE ;

	private static final String queryPublicStreamsTable =
			"SELECT  " + 
					C_ID  + ", " +
					C_TYPE   + ", " +
					P_CITY_SOURCE   + ", " +
					P_ARTICLE_UNITS   + ", " +
					P_KEYWORD_TEMPERATURE   + ", " +
					P_URL_CONDITIONS  + ", " + 
					C_TIMESTAMP  + ", " +
					C_READ   + ", " +
					C_TIMESTAMP_UPDATED + "  " + 
					"FROM " + PUBLIC_STREAMS_TABLE ;
	/*
	private static final String queryNewsTable =
			"SELECT  " + 
					C_ID  + ", " +
					C_TYPE   + ", " +
					C_SOURCE   + ", " +
					C_ARTICLE   + ", " +
					"NULL AS " + E_FROM + ", " +
					"NULL AS " + T_SCREEN_NAME + ", " +
					"NULL AS " + T_TWEET + ", " +
					"NULL AS " + E_MESSAGESNIPPET + ", " +
					C_KEYWORD   + ", " +
					"NULL AS " + E_SUBJECT + ", " +
					C_TIMESTAMP  + ", " +
					C_READ   + ", " +
					C_URL  + ", " + 
					"NULL AS " + E_CONTENT  + ", " +
					"NULL AS " +  I_FULL_URL  + "  " + 
					"FROM " + NEWS_TABLE ;
	 */	
	/*
	private static final String queryTwitterTable =
			"SELECT  " + 
					C_ID  + ", " +
					C_TYPE   + ", " +
					"NULL AS " + C_SOURCE + ", " +
					"NULL AS " + C_ARTICLE + ", " +
					E_FROM   + ", " +
					T_SCREEN_NAME   + ", " +
					T_TWEET   + ", " +
					"NULL AS " + E_MESSAGESNIPPET   + ", " +
					"NULL AS " + C_KEYWORD + ", " +
					"NULL AS " + E_SUBJECT + ", " +
					C_TIMESTAMP  + ", " +
					C_READ   + ", " +
					C_URL + ", " +
					"NULL AS " +  E_CONTENT  + ", " + 
					"NULL AS " +  I_FULL_URL  + "  " + 
					"FROM " + TWITTER_TABLE ;
	 */
	/*
	private static final String queryInstagramTable =
			"SELECT  " + 
					C_ID  + ", " +
					C_TYPE   + ", " +
					"NULL AS " + C_SOURCE + ", " +
					"NULL AS " + C_ARTICLE + ", " +
					I_USER   + ", " +
					"NULL AS " + T_SCREEN_NAME   + ", " +
					"NULL AS " + T_TWEET   + ", " +
					"NULL AS " + E_MESSAGESNIPPET   + ", " +
					"NULL AS " + C_KEYWORD + ", " +
					"NULL AS " + E_SUBJECT + ", " +
					C_TIMESTAMP  + ", " +
					C_READ   + ", " +
					I_URL_JPG + ", " +
					I_CONTENT + ", " +
					I_FULL_URL + " " + 
					"FROM " + INSTAGRAM_TABLE ;
	 */

	//private static final String returnEmptyTable =
	//		"SELECT  * FROM " + EMAILS_TABLE + " WHERE " + E_FROM + "='SANITY-CLAUSE' ";






	/**
	 * Constructor
	 * @param pContext
	 */
	public StatusStream(Context pContext) { 
		this.mContext = pContext ; 
		
		if (mDataBaseStream == null)
		{
			try{
			mDataBaseStream = new DbStream(pContext);
			}catch(Exception e){e.printStackTrace();}
		}
		mDataBase = mDataBaseStream.getWritableDatabase();
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d(TAG, "Initialized com.glowdeck.streams.streamdata");
		}
		
	}


	/**
	 * 
	 */
	public void close() {  
		mDataBaseStream.close();
		mDataBaseStream = null ; 
	}

	/**
	 * @param values - key pairs for insert
	 * @param table - table to update 
	 * @return - 0, no insert
	 */
	public long  insertOrIgnore(ContentValues values, String table) {  
		long val = 0 ;
		try {
			val = mDataBase.insertWithOnConflict(table, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);  

		}  
		catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.executeInsert().SQLiteException: Table=" + table + ", Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.executeInsert().Exception: Table=" + table + ", Error=" + e);
			}
		} 
		finally {

		}
		return val ; 
	}

	/**
	 * @param values - key pairs for insert
	 * @param table - table to update 
	 * @return - 0, no insert
	 */
	public long  updateTable(ContentValues values, String table) {  
		long val = 0 ;
		try {
			val = mDataBase.replace(table, null, values);  

		}  
		catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.executeInsert().SQLiteException: Table=" + table + ", Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.executeInsert().Exception: Table=" + table + ", Error=" + e);
			}
		} 
		finally {

		}
		return val ; 
	}
	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */

	public Cursor getPersonalStatusUpdates() {   

		//StreamsType streamsType = MainActivity.getStreamsType() ;

		return getPersonalStreams() ;


	}
	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */

	public Cursor getSocialStatusUpdates() {   

		//StreamsType streamsType = MainActivity.getStreamsType() ;

		return getSocialStreams() ;


	}
	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */

	public Cursor getPublicStatusUpdates() {   

		//StreamsType streamsType = MainActivity.getStreamsType() ;

		return getPublicStreams() ;


	}
	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */

	public Cursor getStatusUpdates() {   

		try{
		StreamsType streamsType = MainActivity.getStreamsType() ;
		if (streamsType == StreamsType.STREAM_TYPE_PERSONAL)
		{
			return getPersonalStreams() ;
		}
		else
		{
			if (streamsType == StreamsType.STREAM_TYPE_SOCIAL)
			{
				return getSocialStreams() ;
			}
			else
			{
				if (streamsType == StreamsType.STREAM_TYPE_PUBLIC)
				{
					return getPublicStreams() ;
				}
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return null ; 
		/*
		StreamsUser streamsUser =  VerifyCredentialsTask.getStreamsUser(mContext)  ;

		ArrayList<String> arStr = new  ArrayList<String>(); 

		if (streamsUser == null)
		{
			arStr.add("news") ;
		}
		else
		{

			if (streamsUser.getNews().getEnabled().compareTo("1")==0)
			{
				arStr.add("news") ;
			}

			if (streamsUser.getEmail().getEnabled().compareTo("1")==0)
			{
				arStr.add("email") ;
			}
			if (streamsUser.getTwitter().getEnabled().compareTo("1")==0)
			{
				arStr.add("twitter") ;
			}
			if (streamsUser.getInstagram().getEnabled().compareTo("1")==0)
			{
				arStr.add("instagram") ;
			}
		}
		if (arStr.isEmpty())
		{
			arStr.add("xxx") ;
		}
		return getStreamsByTypes(arStr) ;
		 */
	}



	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */
	public Cursor getPersonalStreams()
	{
		Cursor cursor = null ;
		String queryString = null ;
		queryString = queryPersonalStreamsTable + " Order By Timestamp DESC" ;

		try {
			cursor =  mDataBase.rawQuery(queryString, null) ;

		} catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.getPersonalStreams(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in getPersonalStreams = " + e ) ;
			}

		} 

		return cursor ; 
	}
	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */
	public Cursor getSocialStreams()
	{
		Cursor cursor = null ;
		String queryString = null ;
		queryString = querySocialStreamsTable + " Order By Timestamp DESC" ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "StatusStream.getSocialStreams(): New Cursor -----" );
		}
		try {
			cursor =  mDataBase.rawQuery(queryString, null) ;

		} catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.getSocialStreams(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in getSocialStreams = " + e ) ;
			}

		} 

		return cursor ; 
	}
	/**
	 *
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */
	public Cursor getPublicStreams()
	{
		Cursor cursor = null ;
		String queryString = null ;
		queryString = queryPublicStreamsTable + " Order By Timestamp DESC" ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "StatusStream.getPublicStreams(): New Cursor -----" );
		}
		try {
			cursor =  mDataBase.rawQuery(queryString, null) ;

		} catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.getPublicStreams(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in getPublicStreams = " + e ) ;
			}

		} 

		return cursor ; 
	}


	/**
	 * @param tableName - name of the table
	 * @return
	 */
	public long getCountRowsInTable(String tableName)
	{
		return DatabaseUtils.queryNumEntries(mDataBase, tableName) ;   
	}

	public void  reInitAllTables()
	{
		try{
		deleteAllRowsFromTable(PERSONAL_STREAMS_TABLE) ;
		deleteAllRowsFromTable(SOCIAL_STREAMS_TABLE) ;
		deleteAllRowsFromTable(PUBLIC_STREAMS_TABLE) ;
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param tableName - name of the table
	 * @return 0. no rows deleted
	 */

	long deleteAllRowsFromTable(String tableName)
	{
		long val = 0 ;

		try {
			val =  mDataBase.delete(tableName, "1", null); 

		}  
		catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.delete(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in deleteAllRows = " + e ) ;
			}

		} 
		finally {



		}
		return val ; 

	}


	/**
	 * @param tableName - name of the table
	 * @param iD - key to delete
	 * @return
	 */
	public  long deleteItemFromTable(String tableName, String iD)
	{
		long val = 0 ;
		String deleteStr = "_id = '" + iD + "'"; 
		try {
			val =  mDataBase.delete(tableName, deleteStr, null); 

		}  
		catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.delete(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in deleteItemFromTable = " + e ) ;
			}

		} 
		finally {



		}
		return val ; 

	}

	/**
	 * @param tableName - name of the table
	 * @param iD - key to delete
	 * @return
	 */
	public  long deleteOldItemsFromTable(String tableName, String updatedTimeStamp)
	{
		long val = 0 ;
		String deleteStr = C_TIMESTAMP_UPDATED + " != '" + updatedTimeStamp + "'"; 
		try {
			val =  mDataBase.delete(tableName, deleteStr, null); 

		}  
		catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "deleteOldItemsFromTable::SQLiteDatabase.delete(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in deleteOldItemsFromTable = " + e ) ;
			}

		} 
		finally {



		}
		return val ; 

	}



	/**
	 * @param selectedItemKey - key of the article that was read
	 * @param table - table for the stream
	 */
	public void setArticleWasRead(String selectedItemKey, String table ) 
	{
		//SQLiteDatabase mDataBase = this.dbStream.getWritableDatabase();   

		try {

			ContentValues cv=new ContentValues();
			cv.put(C_READ, 1) ;
			String whereClause = C_ID+ "=?" ; // + selectedItemKey + "'";
			String[] updateArgs = {selectedItemKey} ; 
			//int len = updateArgs.length ;
			int numRows = mDataBase.update(table, cv, whereClause, updateArgs) ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","setArticleWasRead:numrows=" + numRows) ;
			}
			if (numRows == 0)
			{
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","setArticleWasRead:key=" + selectedItemKey) ;
				}
			}

		}  
		catch (SQLiteException ex) {
			ex.printStackTrace();
			String errorMessage = ex.getMessage();
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "SQLiteDatabase.updateReadField(): Error=" + errorMessage);
			}
		}
		catch (Exception e) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in updateReadFlag = " + e ) ;
			}

		} 
		finally {



		}
	}



	/**
	 * DbStream extends SQLiteOpenHelper
	 *
	 */
	private class DbStream extends SQLiteOpenHelper {



		/**
		 * @param mContext - the mContext
		 */
		public DbStream(Context context) {  

			super(context, DB_NAME, null, DB_VERSION);

		}
		/**
		 * @param mDataBase - the sqlite database
		 * @param tableName - the table name
		 */
		public void deleteTable(SQLiteDatabase db, String tableName)
		{

			try{
			String dropTable = "drop table if exists " +  tableName ; 
			db.execSQL(dropTable); 
			}catch(Exception e){e.printStackTrace();}
		}
		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","Status:Stream::On create mDataBase") ; 
			}
			
			


			try{
			deleteTable(db, PERSONAL_STREAMS_TABLE) ;
			String sqlPersonalStreams = "create table " + PERSONAL_STREAMS_TABLE + " (" + 
					C_ID + " string primary key, "  +  	// ID
					C_TYPE + " text, " +               	// Type {"Email"}
					E_FROM + " text, " + 			   	// From 
					E_SUBJECT + " text, " + 			// Subject
					E_MESSAGESNIPPET + " text, " + 		// MessageSnippet
					C_READ + " int,"  +  				// Read
					C_TIMESTAMP + " int,"    +			// Time stamp
					E_CONTENT + " text," +
					C_TIMESTAMP_UPDATED + " int"    +			")";  					// Content 
			db.execSQL(sqlPersonalStreams); 

			deleteTable(db, SOCIAL_STREAMS_TABLE) ;
			String sqlSocialStreams = "create table " + SOCIAL_STREAMS_TABLE + " (" + 
					C_ID + " string primary key, "  +  	// ID
					C_TYPE + " text, " +               	// Type {"Email"}
					S_FROM_USER + " text, " + 			   	// From 
					S_URL + " text, " + 			// Subject
					S_SCREEN_JPG + " text, " + 		// MessageSnippet
					C_TIMESTAMP + " int,"    +			// Time stamp
					C_READ + " int,"  +  				// Read
					S_CONTENT + " text," +
					C_TIMESTAMP_UPDATED + " int"    +			")";  					// Content 

			db.execSQL(sqlSocialStreams); 

			deleteTable(db, PUBLIC_STREAMS_TABLE) ;
			String sqlPublicStreams = "create table " + PUBLIC_STREAMS_TABLE + " (" + 
					C_ID + " string primary key, "  +  	// ID
					C_TYPE + " text, " +               	// Type {"Email"}
					P_CITY_SOURCE + " text, " + 			   	// From 
					P_ARTICLE_UNITS + " text, " + 			// Subject
					P_KEYWORD_TEMPERATURE + " text," +
					P_URL_CONDITIONS + " text," +
					C_TIMESTAMP + " int,"    +			// Time stamp
					C_READ + " int,"  +  				// Read
					C_TIMESTAMP_UPDATED + " int"    +			")";  					// Content 

			db.execSQL(sqlPublicStreams); 
			}catch(Exception e){e.printStackTrace();}
			/*
			deleteTable(db, TWITTER_TABLE) ;
			String sqlTwitter = "create table " + TWITTER_TABLE + " (" + 
					C_ID + " string primary key, "  +  	// ID
					C_TYPE + " text, " +               	// Type {"Twitter"}
					E_FROM + " text, " + 			   	// User 
					T_SCREEN_NAME + " text, " + 			// ScreenName 
					T_TWEET + " text, " + 			// Tweet
					C_READ + " int,"  +  				// Read
					C_TIMESTAMP + " int,"    +			// Time stamp
					C_URL + " text)";  					// Content 

			db.execSQL(sqlTwitter); 
			 */
			/*
			deleteTable(db, INSTAGRAM_TABLE) ;
			String sqlInstagram = "create table " + INSTAGRAM_TABLE + " (" + 
					C_ID + " string primary key, "  +  	// ID
					C_TYPE + " text, " +               	// Type {"Instagram"}
					I_USER + " text, " + 			   	// User 
					C_READ + " int,"  +  				// Read
					C_TIMESTAMP + " int,"    +			// Time stamp
					I_URL_JPG + " text, " +            // JPG of the pic
					I_CONTENT + " text, " +				// desc accompanying pix
					I_FULL_URL +  " text)";  					// URL for double click 

			db.execSQL(sqlInstagram); 
			 */
		}
		void dropAllTables(SQLiteDatabase db)
		{
			try{
			List<String> tables = new ArrayList<String>();
			Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String tableName = cursor.getString(1);
				if (!tableName.equals("android_metadata") &&
						!tableName.equals("sqlite_sequence"))
					tables.add(tableName);
				cursor.moveToNext();
			}
			cursor.close();

			for(String tableName:tables) {
				db.execSQL("DROP TABLE IF EXISTS " + tableName);
			}
			}catch(Exception e){e.printStackTrace();}
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try{
			dropAllTables(  db) ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d(TAG, "onUpgrade");
			}


			onCreate(db); // run onCreate to get new database
			}catch(Exception e){e.printStackTrace();}
		}
	}


}
