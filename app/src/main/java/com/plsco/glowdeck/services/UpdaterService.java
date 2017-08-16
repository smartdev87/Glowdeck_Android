package com.plsco.glowdeck.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.auth.StreamsUserLogin;
import com.plsco.glowdeck.drawer.EnhancedStreamsListFragment;
import com.plsco.glowdeck.drawer.StreamsFragment;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.streamdata.StreamsStream;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.auth.StreamsUser.ModifiedSettings;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;
import com.plsco.glowdeck.streamdata.StreamsStream.Stream;
import com.plsco.glowdeck.streamdata.StreamsStream.Weather;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: UpdaterService.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
 * @author jdiamand
 *
 */


/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * UpdaterService is an android service.
 * Its purpose is to run all the time and to connect with the GlowDeck service to acquire streams and to 
 * update status.
 * 
 * It wakes up a specific intervals and informs the UI if updates have 
 * been made to the database
 * 
 * It also responds to specifc requests from the UI regarding the Glowdeck server or the DB.
 *
 */
public class UpdaterService extends Service {

	// HTTP URLS
	public static final String         GET_STREAMS                           = "http://glowdeck.com/api/streams/get_streams" ;
	//public static final String         GET_STREAMS                           = "http://streams.io/api/streams/get_streams" ;
	public static final String         AUTH_COOKIE                           = "http://glowdeck.com/api/auth/generate_auth_cookie" ;
	//public static final String         AUTH_COOKIE                           = "http://streams.io/api/auth/generate_auth_cookie" ;
	public static final String         GET_CURRENTUSERINFO                   = "http://glowdeck.com/api/user/get_currentuserinfo" ;
	//public static final String         GET_CURRENTUSERINFO                   = "http://streams.io/api/user/get_currentuserinfo" ;
	public static final String         SET_STATUS                            = "http://glowdeck.com/api/streams/set_status" ;
	//public static final String         SET_STATUS                            = "http://streams.io/api/streams/set_status" ;
	public static final String         SET_CURRENTUSERINFO                   = "http://glowdeck.com/api/user/set_currentuserinfo" ;
	//public static final String         SET_CURRENTUSERINFO                   = "http://streams.io/api/user/set_currentuserinfo" ;
	public static final String         HTTP_REGISTER                         = "http://glowdeck.com/api/user/register" ;
	//public static final String         HTTP_REGISTER                         = "http://streams.io/api/user/register" ;
	public static final String         HTTP_GLOWDECK_STORE                   = "http://www.plsound.com" ; // "http://glowdeck.com/store/" ;

	// ALARM DATA CONSTANTS
	static final int NO_WAIT_POLL = 0; // Now
	static final int QUICK_POLL = 5*1; // five seconds, when an update is sent , for example
	static final int SHORT_POLL = 60*1; // one minute , when in the streams mActivity
	static final int LONG_POLL = 60*60*1 ; //  60*1*60; // one hour, when leaving streams mActivity

	// Other constants
	public  static final String WEATHER_CID 								=  "com.glowdeck.streams.drawer.WEATHER_CID"  ; 
	public  static final String STREAMS_WEATHER_CITY_UNAVAILABLE           = "Weather City Unavailable" ;
	private static final int    SHORTPOLL_GETUSERINO_INTERVAL              = 5 ;  // if in short poll, then refresh the userInfo every 5 intervals 
	public 	static final String STREAMS_UPDATED_INTENT                     =  "com.glowdeck.streams.drawer.STREAMS_UPDATED"  ; 
	public 	static final String STREAMS_UPDATED_INTENT_PERSONAL_COUNT         = "com.glowdeck.streams.drawer.STREAMS_UPDATED_PERSONAL_COUNT" ;
	public 	static final String STREAMS_UPDATED_INTENT_SOCIAL_COUNT         = "com.glowdeck.streams.drawer.STREAMS_UPDATED_SOCIAL_COUNT" ;
	public 	static final String STREAMS_UPDATED_INTENT_PUBLIC_COUNT         = "com.glowdeck.streams.drawer.STREAMS_UPDATED_PUBLIC_COUNT" ;
	public 	static final String STREAMS_UPDATED_INTENT_ARCHIVE_RESULT      = "com.glowdeck.streams.drawer.STREAMS_ARCHIVE_RESULT" ; 
	public 	static final String STREAMS_UPDATED_INTENT_NETWORK_STATUS      = "com.glowdeck.streams.drawer.STREAMS_NEWORK_STATUS" ;
	public 	static final String STREAMS_UPDATED_INTENT_NETWORK_AVAILABLE   = "AVAILABLE" ;
	public 	static final String STREAMS_UPDATED_INTENT_NETWORK_UNAVAILABLE = "UNAVAILABLE" ;
	//
	//
	// constants (error codes for MSG)
	final static String  MSG_0001 = "STRA301 - No Network" ;
	final static String  MSG_0002 = "STRA302 - No Network" ;
	final static String  MSG_0011 = "STRA311 - Server msg error" ;
	final static String  MSG_0012 = "STRA312 - Server msg error" ;


	// Globals (statics)
	static String                msNetworkStatus                   = NetStatusReceiver.NET_STATUS_UP ; // assume up at startup
	static UpdaterService msCurrentContext ;
	static boolean               msNeedToGetUserInfo 		       = true ; // manage the user info data
	static int                   msRefreshUserInfoCountDown        = SHORTPOLL_GETUSERINO_INTERVAL ; // user info will not be updated if writes are pending
	static boolean               msRunUpdater                      = true ; // if user is not logged in,
	static int                   msLastPollInterval ; // either set to SHORT or LONG poll when the previous alarm was set
	//
	private boolean deleteBeforeAddingPersonal = false ;
	private boolean deleteBeforeAddingSocial = false ;
	private boolean deleteBeforeAddingPublic = false ;
	static AlarmManager msAlarmManager ;
	static Calendar msCalendar ;
	private static StreamsStream msStreamsStream = null;
	private static Updater msUpdater;
	private static boolean msServiceIsRunning = false ;
	private static boolean msStreamsFragmentClientIsActive = false ;
	private static boolean msQuickPoll = false ;
	private static  String msUseridString = null ;
	private static  String msTokenString  = null ; // don't bother running as credentials needed are unavailable
	//		                                msRunFlag is initially false, is set to true
	//    									right after msUpdater.start() and stays true until onDestroy
	static private boolean msRunFlag = false;
	// Globals
	//

	public static String getWeatherStream()
	{

		String theWeather = "" ;
		try{
		if (msStreamsStream != null)
		{
			Weather weather = msStreamsStream.getTheWeather() ;
			theWeather = "WTR:"  + weather.getTemp() + "|" + weather.getConditions() + "|" + weather.getCity() + "^" ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return theWeather ;
	}
	public static String getSocialStream()
	{
		StringBuilder theSocialStream = new  StringBuilder("");
		try{
		if (msStreamsStream != null)
		{
			Stream[] streams = msStreamsStream.getTheStream() ;
			theSocialStream.append("SOC") ;
			for ( Stream stream : streams)
			{
				if (stream.getType().equals(StatusStream.TYPE_FOR_NEWS_TABLE))
				{
					theSocialStream.append(":||N|") ;
					theSocialStream.append(stream.getSource() + "|" + stream.getKeyword() + "|" + stream.getArticle() + "|" + "1m" + "||") ;

				}

			}
			theSocialStream.append("^") ;
			Log.d ("xxx", theSocialStream.toString() ) ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return theSocialStream.toString() ;
	}
	public static String getPersonalStream()
	{
		StringBuilder thePersonallStream = new  StringBuilder("");
		try{
		if (msStreamsStream != null)
		{
			Stream[] streams = msStreamsStream.getTheStream() ;
			thePersonallStream.append("PER") ;
			for ( Stream stream : streams)
			{
				if (stream.getType().equals(StatusStream.TYPE_FOR_EMAIL_TABLE))
				{
					thePersonallStream.append(":||E|") ;
					int index = stream.getFrom().indexOf('<') ;
					String from = stream.getFrom().substring(0, index) ;
					String emailAddr = stream.getFrom().substring(index+1, stream.getFrom().length() -1) ;
					thePersonallStream.append(from + "|" + emailAddr + "|" + stream.getSubject() + "|" + "1m" + "||") ;
				}

			}
			thePersonallStream.append("^") ;
			Log.d ("xxx", thePersonallStream.toString() ) ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return thePersonallStream.toString() ;
	}
	public static String getNetStat()
	{
		return msNetworkStatus ;
	}

	public static  boolean isServiceRunning()
	{
		return msServiceIsRunning ;
	}


	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try{
		msCurrentContext = this ;
		msServiceIsRunning = true ;
		msAlarmManager =(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		msCalendar = Utils.getTimeAfterInSecs(NO_WAIT_POLL); // now

		msUpdater = new Updater() ;

		Intent intent =
				new Intent(this,USAlarmReceiver.class);



		PendingIntent pi = getDistinctPendingIntent(intent, 2);
		if (pi != null)
		{
			msLastPollInterval = LONG_POLL ;
			msAlarmManager.setRepeating(AlarmManager.RTC,
					msCalendar.getTimeInMillis(),
					1000*msLastPollInterval, //Poll Again in an Hour.
					// When the StreamsFragment client is resumed, this goes to 1 minute intervals
					//  until the StreamsFragment client pauses
					pi);

		}
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","UpdaterService:onCreate") ;
		}
		if ( VerifyCredentialsTask.getStreamsUser()  == null)
		{
			if ( !LoginActivity.recoverSavedStreamsUser(this.getBaseContext()) )
			{   // see if we have a cached steramsUser ... we'll need it to get the streams
				return ;
			}
		}

		 }catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @return updater factory
	 */
	Updater getNewUpdater()
	{
		Updater updater = new Updater() ;
		return updater ;
	}
	/**
	 *
	 */
	static void  runUpdater()
	{
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "UpdaterService:runUpdater::Started" );
		}

		if (VerifyCredentialsTask.getStreamsUser() != null)
		{
			StreamsAccount streamsAccount = VerifyCredentialsTask.getStreamsUser().getStreamsAccount() ;
			if (streamsAccount != null)
			{
				msUseridString = streamsAccount.getID() ;
				msTokenString  = streamsAccount.getToken() ;

				if (msCurrentContext == null)
				{
					return ;
				}
				StreamsApplication sApplication = (StreamsApplication)msCurrentContext.getApplication() ;
				if (sApplication == null)
				{
					return ;
				}


				UpdaterService updService = msCurrentContext;

				if (updService != null)
				{
					msUpdater = updService.getNewUpdater() ;

					if (msUpdater != null)
					{
						if (StreamsApplication.DEBUG_MODE)
						{
							Log.d("dbg", "UpdaterService:runUpdater::starting msUpdater" );
						}
						msUpdater.start();

						msRunFlag = true; //


					}
				}
			}
		}
		 }catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onTaskRemoved(android.content.Intent)
	 */
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub

		try{
		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent =
				PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.setExact(
				AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + 1000,restartServicePendingIntent);
		 }catch(Exception e){e.printStackTrace();}
		super.onTaskRemoved(rootIntent);

	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {

		super.onDestroy();

		msServiceIsRunning = false ;
		// TODO kill the alarm
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","UpdaterService:onDestroy") ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "UpdaterService:onStartCommand()" );
		}


		try{
		if (intent != null)
		{


			String netStatStr = intent.getStringExtra(NetStatusReceiver.NET_STATUS);
			String newUserStr = intent.getStringExtra(LoginActivity.DELETE_DATABASE);
			String restartPolling = intent.getStringExtra(LoginActivity.RESTART_POLLING);
			boolean streamsListViewActive = intent.getBooleanExtra(StreamsFragment.STREAMS_LISTVIEW_ACTIVE, false) ;

			boolean streamsListViewInActive = intent.getBooleanExtra(StreamsFragment.STREAMS_LISTVIEW_INACTIVE, false) ;
			String archiveStreamListItem = intent.getStringExtra(EnhancedStreamsListFragment.ENHANCED_STREAM_ARCHIVE) ;

			if (netStatStr != null )
			{
				msNetworkStatus = netStatStr ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "UpdaterService:onStartCommand value of netstat [" + msNetworkStatus + "]" );
				}
			}
			if (newUserStr != null )
			{
				// TODO replace with new tables
				msRunUpdater = false ;

				try {
					Thread.sleep(1000) ;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "UpdaterService:onStartCommand LoginActivity Requested Database Delete" );
				}
				StreamsApplication sApplication = (StreamsApplication)msCurrentContext.getApplication() ;
				StatusStream statusStream =  sApplication.getStatusStreamUpdater() ;
				msUseridString = null ;
				msTokenString  = null ;
				statusStream.reInitAllTables() ;

				// wait until the user logs in, then Login will send newlogin restart
				//  which will runUpodater to go to TRUE
			}
			if (restartPolling != null )
			{
				msRunUpdater = true ;
			}
			if (streamsListViewActive)
			{
				msRunUpdater = true ;

				msStreamsFragmentClientIsActive   = true ;
				msQuickPoll = true ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "UpdaterService:onStartCommand streamsListViewActive=true" );
				}
			}
			if (streamsListViewInActive)
			{
				// change the poll time
				msStreamsFragmentClientIsActive   = false ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "UpdaterService:onStartCommand streamsListViewActive=false" );
				}
			}
			if (archiveStreamListItem != null)
			{
				//int sendClientVal = 0 ;
				String archivedItemID = intent.getStringExtra(EnhancedStreamsListFragment.ENHANCED_STREAM_ARCHIVE_ID) ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "UpdaterService:onStartCommand - Archive Request: TYPE:[" + archiveStreamListItem +  "]Item" );
					Log.d("dbg", "UpdaterService:onStartCommand - Archive Request: ID :[" + archivedItemID +  "]Item" );
				}
				String[] parmsArray = new String[] {archiveStreamListItem, archivedItemID} ;

				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
					new AsyncArchive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,parmsArray);
				} else {
					new AsyncArchive().execute(parmsArray);
				}


			}
		}

		if (msRunUpdater)
		{
			// let the alarm take of it
		}
		checkPollTimes(msCurrentContext) ;
		 }catch(Exception e){e.printStackTrace();}
		return Service.START_REDELIVER_INTENT ;


	}
	/**
	 * class Updater extends Thread
	 *
	 */
	public class Updater extends Thread {

		public Updater() {

			super("UpdaterService-Updater");
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Updater service constructor");
			}
		}

		@Override
		public void run() {
			//UpdaterService updaterService = UpdaterService.this;
			if (StreamsApplication.DEBUG_MODE)
			{
				String msg = "Starting Updater thread" + " Value of msRunFlag = " + msRunFlag ;
				Log.d("dbg", msg );
			}
			try{
			if (UpdaterService.msRunFlag) {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "Running background thread");
				}

				if (!getStreamsFromServer())
				{
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", "UpdaterService::run::getStreamsFromServer() return false ");
					}

				}
				if ( msNeedToGetUserInfo && userInfoNotPending())
				{
					getUserInfoFromServer() ;

				}
				msNeedToGetUserInfo = false ;
				return ;

			}
			 }catch(Exception e){e.printStackTrace();}
		}
	}

	/**
	 * @return true if there are no updates indicated in the mStreamsUser struct
	 *           else there is a setting notification pending so skip this
	 *           sync update to the StreamsUser struct from the server
	 */
	boolean userInfoNotPending()
	{



		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;

		if (streamsUser != null)
		{

			ModifiedSettings modifiedSettings = streamsUser.getModifiedSettings() ;

			return  !(modifiedSettings.anyModificationsPending());
		}
		else
		{
			return false ;
		}


	}
	/**
	 * @return boolean - true if no errors occurred
	 */
	private boolean getStreamsFromServer()
	{
		if (msUseridString == null)
		{ // user not logged on
			return false ;
		}

		StreamsApplication sApplication = (StreamsApplication)msCurrentContext.getApplication() ;
		StatusStream statusStream =  sApplication.getStatusStreamUpdater() ;

		//
		long  personalStreamOrigSize = statusStream.getCountRowsInTable(StatusStream.PERSONAL_STREAMS_TABLE) ;
		long  personalStreamsAdds = 0 ;
		long  personalStreamsDeletes = 0 ;
		//
		long  socialStreamOrigSize =  statusStream.getCountRowsInTable(StatusStream.SOCIAL_STREAMS_TABLE) ;
		long  socialStreamsAdds = 0 ;
		long  socialStreamsDeletes = 0 ;
		//
		long  publicStreamOrigSize = statusStream.getCountRowsInTable(StatusStream.PUBLIC_STREAMS_TABLE) ;
		long  publicStreamsAdds = 0 ;
		long  publicStreamsDeletes = 0 ;
		String currentUpdateTime = getCurrentTime(0) ;

		boolean retVal = true ;
		//long retStatFromUpdateDb = 0 ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg", "UpdaterService:getStreamsFromServer()" );
		}

		if ( (msNetworkStatus.compareTo(NetStatusReceiver.NET_STATUS_DOWN) == 0 ) || (!msRunUpdater) )
		{
			// network is down, don't run any network code ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","UpdaterService:getStreamsFromServer():Network is down/skip processing") ;
			}
			retVal = false ;
		}
		else
		{ // network is up

			try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(GET_STREAMS);
			// request is of format
			// Token=1b433e7336975e6af3e9458a6e02fe95&UserID=1356
			String request = "Token=" + msTokenString + "&UserID=" + msUseridString ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "getStreamsFromServer:request=" + request);
			}
			try {

				StringEntity se = new StringEntity(request);
				httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "getStreamsFromServer:getting ready to post" );
				}
				httppost.setEntity(se);


				HttpResponse httpResponse = httpclient.execute(httppost);

				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),
						"UTF-8"));
				StringBuilder builder = new StringBuilder();



				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}

				Gson gson = new GsonBuilder().

						setDateFormat("yyyy-mm-DD HH:mm:ss").create();

				msStreamsStream =  gson.fromJson( builder.toString(), StreamsStream.class);
				String status1 = msStreamsStream.getstatus() ;
				String status2 = msStreamsStream.getStatus() ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","UpdaterService::getStreamsFromServer:: calling setTheWeather()"   ) ;
				}


				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "status =" + status1 + ", Status = " + status2 ) ;
				}
				if ((status1 != null) && (status2 != null))
				{
					if ( (status1.compareTo("ok")==0) &&  (status2.compareTo("SUCCESS")==0) )
					{
						Weather theWeather = msStreamsStream.getTheWeather() ;
						if (theWeather == null)
						{
							clearTheWeather() ;
						}
						else
						{
							if (updateTheWeather( theWeather ) )
							{ // the weather has been updated
								//++publicStreamsAdds ;
							}

							if (processStreamPublicWeatherType(theWeather,statusStream,currentUpdateTime) != -1)
							{
								// record was not added due to error
								//--publicStreamsAdds ;
							}

						}
						ContentValues values = new ContentValues() ;
						for (Stream streamLoop : msStreamsStream.getTheStream())
						{
							values.clear() ;
							String type = streamLoop.getType() ;
							if (type == null)
							{
								break ;
							}

							if (type.compareToIgnoreCase(StatusStream.TYPE_FOR_NEWS_TABLE)==0)
							{
								if (streamLoop.getRead() < 2) // 2== archive
								{


									if (processStreamPublicNewsType(streamLoop,statusStream,currentUpdateTime) != -1)
									{
										++publicStreamsAdds ;
									}
								}
							}
							if (type.compareToIgnoreCase(StatusStream.TYPE_FOR_EMAIL_TABLE)==0)
							{
								if (streamLoop.getRead() < 2) // 2== archive
								{


									if (processStreamPersonalType(streamLoop,statusStream,currentUpdateTime) != -1)
									{
										++personalStreamsAdds ;
									}
								}
							}
							if (type.compareToIgnoreCase(StatusStream.TYPE_FOR_TWITTER_TABLE)==0)
							{
								if (streamLoop.getRead() < 2) // 2== archive
								{


									if (processSocialTwitterType(streamLoop,statusStream,currentUpdateTime) != -1)
									{
										++socialStreamsAdds ;
									}

								}
							}
							if (type.compareToIgnoreCase(StatusStream.TYPE_FOR_INSTAGRAM_TABLE)==0)
							{
								if (streamLoop.getRead() < 2) // 2== archive
								{



									if (processSocialInstagramType(streamLoop,statusStream,currentUpdateTime) != -1)
									{
										++socialStreamsAdds ;
									}

								}
							}

						}


					}
				}

			}

			catch (IOException e) {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "IOException Error in getStreamsFromServer = " + e ) ;
				}
				retVal = false ;

			}

			catch (Exception e) {
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "Exception Error in getStreamsFromServer = " + e ) ;
				}
				retVal = false ;

			}

			finally
			{
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "Finally in getStreamsFromServer." ) ;
				}
			}


		 }catch(Exception e){e.printStackTrace();}
		} // network is up

		if (retVal) // retVal will be false if network is down or if an err occurred
		{
			// for personal streams, delete all that are older than the previous update time.
			//if (currentTime == null)
			//{
			//	currentTime = getCurrentTime() ;
			//}
			try{
			personalStreamsAdds =  Math.abs(personalStreamsAdds - personalStreamOrigSize) ; // subtract # of recs ar start to get new adds
			if (personalStreamsAdds>0)
			{
				personalStreamsDeletes = deleteOldRecords(statusStream, StatusStream.PERSONAL_STREAMS_TABLE,currentUpdateTime) ;
			}


			socialStreamsAdds =  Math.abs(socialStreamsAdds - socialStreamOrigSize) ; // subtract # of recs ar start to get new adds
			if (socialStreamsAdds >0)
			{
				socialStreamsDeletes = deleteOldRecords(statusStream, StatusStream.SOCIAL_STREAMS_TABLE,currentUpdateTime) ;
			}



			publicStreamsAdds = Math.abs(publicStreamsAdds - publicStreamOrigSize) ; // subtract # of recs ar start to get new adds
			if (publicStreamsAdds >1)
			{
				publicStreamsDeletes = deleteOldRecords(statusStream, StatusStream.PUBLIC_STREAMS_TABLE,currentUpdateTime) ;
			}
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "getStreamsFromServer::publicStreamsAdds =" + publicStreamsAdds) ;
				Log.d("dbg", "getStreamsFromServer::publicStreamsDeletes =" + publicStreamsDeletes) ;

			}
		 }catch(Exception e){e.printStackTrace();}
		}

		try{
		Intent streamsUpdatedIntent = new Intent(STREAMS_UPDATED_INTENT) ;
		String netAvailability = STREAMS_UPDATED_INTENT_NETWORK_AVAILABLE ;
		if (msNetworkStatus.compareTo(NetStatusReceiver.NET_STATUS_DOWN) == 0 )
		{
			netAvailability = STREAMS_UPDATED_INTENT_NETWORK_UNAVAILABLE ;
		}
		streamsUpdatedIntent.putExtra(STREAMS_UPDATED_INTENT_NETWORK_STATUS, netAvailability);

		int personalStreamsCount = (int)(personalStreamsAdds  + personalStreamsDeletes) ;
		if (personalStreamsCount > 0)
		{
			streamsUpdatedIntent.putExtra(STREAMS_UPDATED_INTENT_PERSONAL_COUNT, personalStreamsCount);
		}


		int socialStreamsCount = (int)(socialStreamsAdds  + socialStreamsDeletes) ;
		if (socialStreamsCount > 0)
		{
			streamsUpdatedIntent.putExtra(STREAMS_UPDATED_INTENT_SOCIAL_COUNT, socialStreamsCount);
		}


		int publicStreamsCount = (int)(publicStreamsAdds  + publicStreamsDeletes) ;
		if (publicStreamsCount > 0)
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "getStreamsFromServer::publicStreamsCount =" + publicStreamsCount) ;


			}


			streamsUpdatedIntent.putExtra(STREAMS_UPDATED_INTENT_PUBLIC_COUNT, publicStreamsCount);
		}
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","return  from Updater after broadcast") ;
		}
		msCurrentContext.sendBroadcast(streamsUpdatedIntent);
		 }catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	/*
	 *
	 */
	void clearTheWeather()
	{
		try{
		StreamsApplication streamsApplication= (StreamsApplication)this.getApplication() ;
		streamsApplication.weatherInfo.WeatherStatus = "";
		streamsApplication.weatherInfo.Streams_City = "" ;
		streamsApplication.weatherInfo.Streams_Temp = "" ;
		streamsApplication.weatherInfo.Streams_Conditions =  "" ;
		streamsApplication.weatherInfo.Streams_Units = "" ;
		streamsApplication.weatherInfo.WeatherStatus = "" ;
		 }catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param theWeather - save the current data info
	 * @return true if the passed in weather info in different from the previous
	 */
	boolean updateTheWeather(Weather theWeather)
	{
		boolean changed = false ;
		try{
		StreamsApplication streamsApplication= (StreamsApplication)this.getApplication() ;
		if (theWeather == null)
		{
			if (theWeather.getCity().length()> 0)
			{
				changed = true ;
			}
			clearTheWeather() ;

		}
		else
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","UpdaterService::setTheWeather:: theWeather city is "  + theWeather.getCity() ) ;
			}
			if (theWeather.getCity().compareToIgnoreCase(streamsApplication.weatherInfo.Streams_City) != 0)
			{
				changed = true ;
			}
			streamsApplication.weatherInfo.Streams_City = theWeather.getCity() ;


			if (theWeather.getTemp().compareToIgnoreCase(streamsApplication.weatherInfo.Streams_Temp) != 0)
			{
				changed = true ;
			}
			streamsApplication.weatherInfo.Streams_Temp = theWeather.getTemp() ;

			if (theWeather.getUnits().compareToIgnoreCase(streamsApplication.weatherInfo.Streams_Units) != 0)
			{
				changed = true ;
			}
			streamsApplication.weatherInfo.Streams_Units = theWeather.getUnits() ;


			if (theWeather.getConditions().compareToIgnoreCase(streamsApplication.weatherInfo.Streams_Conditions) != 0)
			{
				changed = true ;
			}
			streamsApplication.weatherInfo.Streams_Conditions = theWeather.getConditions() ;


		}
		 }catch(Exception e){e.printStackTrace();}
		return changed ;
	}

	/**
	 * @param streamLoop  - the current Stream element (of type news item)
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return how many records where inserted (or an error , such as duplicate key) occurred
	 */
	/*
	long processNewsType(Stream streamLoop,StatusStream statusStream)
	{
		//long retStatFromUpdateDb = 0 ;
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.C_ARTICLE, streamLoop.getArticle()) ;
		values.put(StatusStream.C_KEYWORD, streamLoop.getKeyword()) ;
		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.C_TYPE, streamLoop.getType()) ;
		values.put(StatusStream.C_SOURCE, streamLoop.getSource()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp()) ;
		values.put(StatusStream.C_URL, streamLoop.getURL()) ;
		return statusStream.insertOrIgnore(values,StatusStream.NEWS_TABLE) ;
	}
	 */
	/**
	 * @param streamLoop  - the current Stream element (of type email item)
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return how many records where inserted (or an error , such as duplicate key) occurred
	 */
	/*
	long processEmailType(Stream streamLoop,StatusStream statusStream)
	{
		//long retStatFromUpdateDb = 0 ;
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.E_FROM, streamLoop.getFrom()) ;
		values.put(StatusStream.E_SUBJECT, streamLoop.getSubject()) ;
		values.put(StatusStream.E_MESSAGESNIPPET, streamLoop.getMessageSnippet()) ;
		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.C_TYPE, streamLoop.getType()) ;
		values.put(StatusStream.E_CONTENT, streamLoop.getContent()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp()) ;

		return statusStream.insertOrIgnore(values,StatusStream.EMAILS_TABLE) ;

	}
	 */
	long deleteOldRecords(StatusStream statusStream, String table, String time)
	{
		long retVal = 0 ;
		retVal = statusStream.deleteOldItemsFromTable(table, time) ;
		return retVal ;
	}
	/**
	 * @param streamLoop  - the current Stream element (of type email item)
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return returns 1 if the record was updated (with the latest timestamp info) or the record was added because it did not exists
	 *                 -11 if the record the record was not added
	 */
	long processStreamPersonalType(Stream streamLoop, StatusStream statusStream, String currentTime)
	{
		int retVal = 1  ;
		try{
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.E_FROM, streamLoop.getFrom()) ;
		values.put(StatusStream.E_SUBJECT, streamLoop.getSubject()) ;
		values.put(StatusStream.E_MESSAGESNIPPET, streamLoop.getMessageSnippet()) ;
		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.C_TYPE, StatusStream.TYPE_FOR_PERSONAL_STREAMS_TABLE +  streamLoop.getType()) ;
		values.put(StatusStream.E_CONTENT, streamLoop.getContent()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp()) ;
		values.put(StatusStream.C_TIMESTAMP_UPDATED, currentTime ) ;

		if ( statusStream.updateTable(values, StatusStream.PERSONAL_STREAMS_TABLE ) == -1 )
		{ // err occurred
			//statusStream.insertOrIgnore(values,StatusStream.PERSONAL_STREAMS_TABLE) ;
			retVal = -1 ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	/**
	 * @param theWeather  - the current weather   item)
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return returns 1 if the record was updated (with the latest timestamp info) or the record was added because it did not exists
	 *                 -11 if the record the record was not added
	 */
	long processStreamPublicWeatherType(Weather theWeather, StatusStream statusStream, String currentTime)
	{
		int retVal = 1  ;
		try{
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, WEATHER_CID) ;
		values.put(StatusStream.P_ARTICLE_UNITS, theWeather.getUnits()) ;
		values.put(StatusStream.P_KEYWORD_TEMPERATURE, theWeather.getTemp()) ;

		values.put(StatusStream.C_READ, "0") ;
		values.put(StatusStream.C_TYPE, StatusStream.TYPE_FOR_PUBLIC_STREAMS_TABLE +  StatusStream.TYPE_FOR_WEATHER_TABLE) ;
		values.put(StatusStream.P_CITY_SOURCE,  theWeather.getCity()) ;
		values.put(StatusStream.P_URL_CONDITIONS, theWeather.getConditions()) ;

		values.put(StatusStream.C_TIMESTAMP,currentTime) ;

		values.put(StatusStream.C_TIMESTAMP_UPDATED, currentTime ) ;


		if ( statusStream.updateTable(values, StatusStream.PUBLIC_STREAMS_TABLE ) == -1 )
		{ // err occurred
			retVal = -1 ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	/**
	 * @param streamLoop  - the current Stream element  of type news
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return returns 1 if the record was updated (with the latest timestamp info) or the record was added because it did not exists
	 *                 -11 if the record the record was not added
	 */
	long processStreamPublicNewsType(Stream streamLoop, StatusStream statusStream, String currentTime)
	{
		int retVal = 1  ;
		try{
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.P_ARTICLE_UNITS, streamLoop.getArticle()) ;
		values.put(StatusStream.P_KEYWORD_TEMPERATURE, streamLoop.getKeyword()) ;

		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.C_TYPE, StatusStream.TYPE_FOR_PUBLIC_STREAMS_TABLE +  streamLoop.getType()) ;
		values.put(StatusStream.P_CITY_SOURCE, streamLoop.getSource()) ;
		values.put(StatusStream.P_URL_CONDITIONS, streamLoop.getURL()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp()) ;

		values.put(StatusStream.C_TIMESTAMP_UPDATED, currentTime ) ;


		if ( statusStream.updateTable(values, StatusStream.PUBLIC_STREAMS_TABLE ) == -1 )
		{ // err occurred
			//statusStream.insertOrIgnore(values,StatusStream.PERSONAL_STREAMS_TABLE) ;
			retVal = -1 ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	/**
	 * @param streamLoop  - the current Stream element
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return returns 0 if the record was updated (with the latest timestamp info)
	 *                 1 if the record ws new and had to be added
	 */
	long processSocialInstagramType(Stream streamLoop, StatusStream statusStream, String currentTime)
	{
		int retVal =  1 ;
		try{
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ; //
		values.put(StatusStream.C_TYPE, StatusStream.TYPE_FOR_SOCIAL_STREAMS_TABLE + streamLoop.getType()) ;//
		values.put(StatusStream.S_FROM_USER, streamLoop.getUser()) ; //
		values.put(StatusStream.S_URL, streamLoop.getFullURL()) ;
		values.put(StatusStream.S_SCREEN_JPG, streamLoop.getURL()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp() );//
		values.put(StatusStream.C_READ, streamLoop.getRead()) ;//
		values.put(StatusStream.S_CONTENT, streamLoop.getContent()) ;//
		values.put(StatusStream.C_TIMESTAMP_UPDATED, currentTime ) ;

		if ( statusStream.updateTable(values, StatusStream.SOCIAL_STREAMS_TABLE ) == -1 )
		{ // error occurred
			//statusStream.insertOrIgnore(values,StatusStream.SOCIAL_STREAMS_TABLE) ;
			retVal = 1 ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	/**
	 * @param streamLoop  - the current Stream element
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return returns 0 if the record was updated (with the latest timestamp info)
	 *                 1 if the record ws new and had to be added
	 */
	long processSocialTwitterType(Stream streamLoop, StatusStream statusStream, String currentTime)
	{
		int retVal =  1 ;
		try{
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.C_TYPE, StatusStream.TYPE_FOR_SOCIAL_STREAMS_TABLE + streamLoop.getType()) ;
		values.put(StatusStream.S_FROM_USER, streamLoop.getUser()) ;
		values.put(StatusStream.S_URL, streamLoop.getURL()) ;
		values.put(StatusStream.S_SCREEN_JPG, streamLoop.getScreenName()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp() );
		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.S_CONTENT, streamLoop.getTweet()) ;
		values.put(StatusStream.C_TIMESTAMP_UPDATED, currentTime ) ;






		if ( statusStream.updateTable(values, StatusStream.SOCIAL_STREAMS_TABLE ) == -1 )
		{   // err occurred
			//statusStream.insertOrIgnore(values,StatusStream.SOCIAL_STREAMS_TABLE) ;
			retVal = -1 ;
		}
		 }catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	String getCurrentTime(int offSet)
	{
		Long time =  System.currentTimeMillis() ;
		time += offSet ;
		return  time.toString() ;
	}

	/**
	 * @param streamLoop  - the current Stream element (of type twitter item)
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return how many records where inserted (or an error , such as duplicate key) occurred
	 */
	/*
	long processTwitterType(Stream streamLoop,StatusStream statusStream)
	{
		//long retStatFromUpdateDb = 0 ;
		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.T_USER, streamLoop.getUser()) ;
		values.put(StatusStream.T_SCREEN_NAME, streamLoop.getScreenName()) ;
		values.put(StatusStream.T_TWEET, streamLoop.getTweet()) ;
		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.C_TYPE, streamLoop.getType()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp()) ;
		values.put(StatusStream.C_URL, streamLoop.getURL()) ;
		return statusStream.insertOrIgnore(values,StatusStream.TWITTER_TABLE) ;

	}
	 */

	/**
	 * @param streamLoop  - the current Stream element (of type instagram item)
	 * @param statusStream - keys for the values that will be stored in values in prep of the insert DB call
	 * @return how many records where inserted (or an error , such as duplicate key) occurred
	 */
	/*
	long processInstagramType(Stream streamLoop,StatusStream statusStream)
	{
		// TODO fix for Instagram

		ContentValues values = new ContentValues() ;

		values.put(StatusStream.C_ID, streamLoop.getID()) ;
		values.put(StatusStream.I_USER, streamLoop.getUser()) ;
		values.put(StatusStream.I_CONTENT, streamLoop.getContent()) ;

		values.put(StatusStream.C_READ, streamLoop.getRead()) ;
		values.put(StatusStream.C_TYPE, streamLoop.getType()) ;
		values.put(StatusStream.C_TIMESTAMP, streamLoop.getTimestamp()) ;
		values.put(StatusStream.I_URL_JPG, streamLoop.getURL()) ;
		values.put(StatusStream.I_FULL_URL, streamLoop.getFullURL()) ;

		return statusStream.insertOrIgnore(values,StatusStream.INSTAGRAM_TABLE) ;



	}
	 */


	/**
	 * @return - true if successfully conracted server
	 */
	private boolean getUserInfoFromServer()
	{


		boolean retVal = false ; // set for error, override upon success

		try{
		if (msNetworkStatus.compareTo(NetStatusReceiver.NET_STATUS_DOWN) == 0 )
		{
			// network is down, just return ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","UpdaterService:getUserInfoFromServer():Network is down/skip processing") ;
			}
			return retVal  ; 
		}


		SharedPreferences sharedPrefs = LoginActivity.getSharedPrefs(this) ;

		String passwordPref = sharedPrefs.getString(LoginActivity.PrefsPassword, LoginActivity.PrefsDefaultString);
		String userIDPref = sharedPrefs.getString(LoginActivity.PrefsUserid, LoginActivity.PrefsDefaultString);
		if ( (passwordPref.compareTo(LoginActivity.PrefsDefaultString)==0) ||
				(userIDPref.compareTo(LoginActivity.PrefsDefaultString)==0) )
		{
			msNeedToGetUserInfo = false ; 
			return false ; 
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(AUTH_COOKIE);

		String useridPass = "password=" + passwordPref + "&email=" + userIDPref ;

		try {

			StringEntity se = new StringEntity(useridPass);		 
			httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
			httppost.setEntity(se);


			HttpResponse httpResponse = httpclient.execute(httppost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			Gson gson = new GsonBuilder(). 

					setDateFormat("yyyy-mm-DD HH:mm:ss").create();



			//StreamsUser 
			StreamsUserLogin streamsUserLogin =  gson.fromJson( builder.toString(), StreamsUserLogin.class);
			String status1 = streamsUserLogin.getstatus() ;
			String status2 = streamsUserLogin.getStatus() ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "status =" + status1 + ", Status = " + status2 ) ;
			}
			if ((status1 != null) && (status2 != null))
			{
				if ( (status1.compareTo("ok")==0) &&  (status2.compareTo("SUCCESS")==0) )
				{
					// after a successfull login with the GlowDeck server, get the most 
					// current StreamsUser profile information
					String streamsUserID = streamsUserLogin.getStreamsAccount().getID() ; 
					String streamsToken = streamsUserLogin.getStreamsAccount().getToken() ; 
					httppost = new HttpPost(GET_CURRENTUSERINFO) ; 
					String useridToken = "UserID=" + streamsUserID + "&Token=" + streamsToken ;
					try {
						se = new StringEntity(useridToken);		 
						httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
						httppost.setEntity(se);
						httpResponse = httpclient.execute(httppost);
						reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
						builder = new StringBuilder();
						for (String line = null; (line = reader.readLine()) != null;) {
							line = VerifyCredentialsTask.cleanUpJsonFromServer(line) ;
							builder.append(line).append("\n");
						}
						gson = new GsonBuilder().

								setDateFormat("yyyy-mm-DD HH:mm:ss").create();
						VerifyCredentialsTask.setStreamsUser((gson.fromJson( builder.toString(), StreamsUser.class) ) );
						// now we have a valid, most recent, streamUser object
						// save the contents builder to disk

						if (VerifyCredentialsTask.serializeStreamsUserInfo( builder) )
						{

							String streamsUserVersion = StreamsApplication.streamsUserVersion ;

							sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
							SharedPreferences.Editor editor = sharedPrefs.edit();
							editor.putString(LoginActivity.PrefsStreamsUserVersion, streamsUserVersion);
							editor.commit();
						}


						retVal = true ; 
					}
					catch (IOException e) {
						if (StreamsApplication.DEBUG_MODE)
						{
							Log.d("dbg", "IOException Error getting UpdaterService:getUserInfo-" + e ) ;
						}

						return retVal ;  
					} 
					catch (Exception e) {
						if (StreamsApplication.DEBUG_MODE)
						{
							Log.d("dbg", "Exception Error in UpdaterService:getUserInfo-" + e ) ;
						}
						return retVal ;  
					} 

				}
			}

		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "IOException Error in UpdaterService:getUserInfo-" + e ) ;
			}
			if (retVal)
			{
				msNeedToGetUserInfo = false ; 
			}
			return retVal ;  
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Exception Error in checkCredentials = " + e ) ;
			}
			return retVal ;  
		} 
		finally
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "Finally in checkCredentials." ) ;
			}
		}
		 }catch(Exception e){e.printStackTrace();}
		return retVal ; 
	} 

	public static class Utils {
		public static Calendar getTimeAfterInSecs(int secs) {
			Calendar cal = Calendar.getInstance();
			if (secs == 0)
			{
				return cal ;
			}
			else
			{
				try{
				cal.add(Calendar.SECOND,secs);
				 }catch(Exception e){e.printStackTrace();}
				return cal;
			}
		}
		public static Calendar getCurrentTime(){
			Calendar cal = Calendar.getInstance();
			return cal;
		}
		public static Calendar getTodayAt(int hours){
			Calendar today = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			cal.clear();

			int year = today.get(Calendar.YEAR);
			int month = today.get(Calendar.MONTH);
			//represents the day of the month
			int day = today.get(Calendar.DATE);
			cal.set(year,month,day,hours,0,0);
			return cal;
		}
		public static String getDateTimeString(Calendar cal){
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
			df.setLenient(false);
			String s = df.format(cal.getTime());
			return s;
		}
	}
	protected  static PendingIntent getDistinctPendingIntent(Intent intent, int requestId)
	{
		PendingIntent pi = null ; 
		try
		{
			pi =
					PendingIntent.getBroadcast(
							msCurrentContext,     //mContext, or mActivity
							requestId,     //request id
							intent,         //intent to be delivered
							0);
		} catch (Exception e )
		{
			e.printStackTrace();
		}
		return pi;
	}

	public static class USAlarmReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			try{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", " USAlarmReceiver ********************* Current Poll interval = " + msLastPollInterval) ;
			}
			if (msRunUpdater)
			{
				if ( msLastPollInterval == LONG_POLL )
				{
					msNeedToGetUserInfo = true; 
				}
				if ( msLastPollInterval == SHORT_POLL )
				{
					if (--msRefreshUserInfoCountDown <= 0 )
					{
						msNeedToGetUserInfo = true; 
						msRefreshUserInfoCountDown = SHORTPOLL_GETUSERINO_INTERVAL ;
					}

				}
				runUpdater() ; 
			}

			checkPollTimes( context) ; 
			 }catch(Exception e){e.printStackTrace();}
		}
	}
	/**
	 * @param mContext - required for intent, etc.
	 */
	public static void checkPollTimes(Context context)
	{

		try{
		if (msStreamsFragmentClientIsActive)
		{
			if (msQuickPoll)
			{
				Intent intentAlarm =
						new Intent( 
								context,USAlarmReceiver.class);
				PendingIntent pi = getDistinctPendingIntent(intentAlarm, 2);
				if (pi != null)
				{
					msCalendar = Utils.getTimeAfterInSecs(QUICK_POLL) ;


					msLastPollInterval = QUICK_POLL ; 
					msAlarmManager.setRepeating(AlarmManager.RTC,
							msCalendar.getTimeInMillis(),
							1000*msLastPollInterval, 
							pi);
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", " USAlarmReceiver ********************* New QUICK_POLL interval = " + msLastPollInterval) ;
					}

					msRefreshUserInfoCountDown = SHORTPOLL_GETUSERINO_INTERVAL ;
				}
				msQuickPoll = false ; 
				return ;
			}
			// poll interval should be short
			if (msLastPollInterval != SHORT_POLL)
			{ // reset the alarm to 1 minute polls

				Intent intentAlarm =
						new Intent(//msCurrentContext
								context,USAlarmReceiver.class);
				PendingIntent pi = getDistinctPendingIntent(intentAlarm, 2);
				if (pi != null)
				{
					msCalendar = Utils.getTimeAfterInSecs(SHORT_POLL) ;


					msLastPollInterval = SHORT_POLL ; 
					msAlarmManager.setRepeating(AlarmManager.RTC,
							msCalendar.getTimeInMillis(),
							1000*msLastPollInterval, 
							pi);
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", " USAlarmReceiver ********************* New SHORT_POLL interval = " + msLastPollInterval) ;
					}

					msRefreshUserInfoCountDown = SHORTPOLL_GETUSERINO_INTERVAL ;
				}
			}
		}
		else
		{
			if (msLastPollInterval != LONG_POLL)
			{ // reset the alarm to 1 hour polls

				Intent intentAlarm =
						new Intent(//msCurrentContext,
								context, USAlarmReceiver.class);
				PendingIntent pi = getDistinctPendingIntent(intentAlarm, 2);
				if (pi != null)
				{
					msCalendar = Utils.getTimeAfterInSecs(LONG_POLL) ;



					msLastPollInterval = LONG_POLL ; 
					msAlarmManager.setRepeating(AlarmManager.RTC,
							msCalendar.getTimeInMillis(),
							1000*msLastPollInterval, 
							pi);
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", " USAlarmReceiver ********************* New LONG_POLL interval = " + msLastPollInterval) ;
					}
				}
			}
		}
		 }catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param mStreamType - news, instagram, etc.
	 * @param streamId - the id of the item to be archived
	 * @return
	 */
	long sendServerArchiveRequest(String streamType, String streamId) 
	{
		long retVal = 0  ; 

		try{
		if (streamType.indexOf(StatusStream.TYPE_FOR_SOCIAL_STREAMS_TABLE) == 0)
		{
			streamType = streamType.substring(StatusStream.TYPE_FOR_SOCIAL_STREAMS_TABLE.length()) ;
		}
		if (streamType.indexOf(StatusStream.TYPE_FOR_PERSONAL_STREAMS_TABLE) == 0)
		{
			streamType = streamType.substring(StatusStream.TYPE_FOR_PERSONAL_STREAMS_TABLE.length()) ;
		}
		if (streamType.indexOf(StatusStream.TYPE_FOR_PUBLIC_STREAMS_TABLE) == 0)
		{
			streamType = streamType.substring(StatusStream.TYPE_FOR_PUBLIC_STREAMS_TABLE.length()) ;
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(SET_STATUS);
		String token = null ; 
		String userId = null ;



		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		if (streamsUser != null)
		{
			StreamsAccount streamsAccount = streamsUser.getStreamsAccount() ;
			if (streamsAccount != null)
			{
				userId = streamsAccount.getID() ;
				token  = streamsAccount.getToken() ;

			}
		}
		if ( (userId != null) && (token != null ) )
		{

			try {
				String request = "Token=" + token + "&UserID=" + userId + "&ID=" + streamId + "&Type=" +
						streamType + "&Status=2";
				StringEntity se = new StringEntity( request);	

				httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
				httppost.setEntity(se);
				HttpResponse httpResponse = httpclient.execute(httppost);

				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				Gson gson = new GsonBuilder(). 

						setDateFormat("yyyy-mm-DD HH:mm:ss").create();

				StreamsStream streamsStream =  gson.fromJson( builder.toString(), StreamsStream.class);
				String status1 = streamsStream.getstatus() ;
				String status2 = streamsStream.getStatus() ;
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "SendServerArticleArchiveRequesr: status =" + status1 + ", Status = " + status2 ) ;
				}
				retVal = 1 ; 

			}

			catch (IOException e) {
				e.printStackTrace();
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "IOException Error in SendServerArticleWasRead = " + e ) ;
				}


			} 
			catch (Exception e) {
				e.printStackTrace();
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "Exception Error in SendServerArticleWasRead = " + e ) ;
				}

			} 
			finally
			{
				if (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg", "Finally in updateServerWithUserParms." ) ;
				}
			}
		}


		 }catch(Exception e){e.printStackTrace();}
		return retVal;
	}

	public class AsyncArchive extends AsyncTask<String, Integer, Long> {
		String streamType ; 
		String streamId ;

		@Override
		protected Long doInBackground(String... params) {
			msRunFlag = false ; // no updates while archive in progress
			streamType = params[0] ;
			streamId =		params[1];
			return sendServerArchiveRequest( streamType,  streamId) ;
		}
		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (result == 1)
			{

				try{
				StreamsApplication sApplication = (StreamsApplication)msCurrentContext.getApplication() ;
				StatusStream statusStream =  sApplication.getStatusStreamUpdater() ;

				// now delete the id for the mDataBase ; 
				String sqlTable = "" ;
				/*
				if (streamType.compareToIgnoreCase(StatusStream.TYPE_FOR_NEWS_TABLE)==0)
				{
					sqlTable = StatusStream.NEWS_TABLE ;

				}
				if (streamType.compareToIgnoreCase(StatusStream.TYPE_FOR_EMAIL_TABLE)==0)
				{
					sqlTable = StatusStream.EMAILS_TABLE ;

				}
				if (streamType.compareToIgnoreCase(StatusStream.TYPE_FOR_TWITTER_TABLE)==0)
				{
					sqlTable = StatusStream.TWITTER_TABLE ;

				}
				if (streamType.compareToIgnoreCase(StatusStream.TYPE_FOR_INSTAGRAM_TABLE)==0)
				{
					sqlTable = StatusStream.INSTAGRAM_TABLE ;

				}
				 */
				if ( (streamType.compareToIgnoreCase(StatusStream.SOCIAL_INSTAGRAM_TYPE)==0)
						|| 
						(streamType.compareToIgnoreCase(StatusStream.SOCIAL_TWITTER_TYPE)==0) )
				{
					sqlTable = StatusStream.SOCIAL_STREAMS_TABLE ;

				}
				if ( streamType.compareToIgnoreCase(StatusStream.PUBLIC_NEWS_TYPE)==0)
				{
					sqlTable = StatusStream.PUBLIC_STREAMS_TABLE ;

				}

				if (streamType.compareToIgnoreCase(StatusStream.PERSONAL_EMAIL_TYPE)==0)
				{
					sqlTable = StatusStream.PERSONAL_STREAMS_TABLE ;

				}

				if (sqlTable.length() > 0 )
				{
					statusStream.deleteItemFromTable( sqlTable, streamId ) ; 
				}


				 }catch(Exception e){e.printStackTrace();}
			}

			try{
			Intent streamsUpdatedIntent = new Intent(STREAMS_UPDATED_INTENT) ;
			int resultInt =  result.intValue() ; 
			streamsUpdatedIntent.putExtra(STREAMS_UPDATED_INTENT_ARCHIVE_RESULT, resultInt); 
			msCurrentContext.sendBroadcast(streamsUpdatedIntent);
			msRunFlag = true ;
			 }catch(Exception e){e.printStackTrace();}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

	}


}
