package com.plsco.glowdeck.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.streamdata.StreamsStream;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;

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
import java.util.ArrayList;

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: SendServerSettingsChanged.java
 *
 * � Copyright 2014. PLSCO, Inc. All rights reserved.
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
//

/**
 * 
 * SendServerSettingsChanged extends AsyncTask
 * 
 */
public class SendServerSettingsChanged extends AsyncTask<String, Integer, Long> {


	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Long result) {

		super.onPostExecute(result);
		if (result == 1)
		{
			try{
			ClearServerSettingsChanged() ;
			}catch(Exception e){e.printStackTrace();}

		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {

		super.onPreExecute();
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Long doInBackground(String... readParms) {

		Long retVal = (long)0  ; 

		try{
		for (int i = 0 ; i  < readParms.length ; i++)
		{
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","SendServerSettingsChanged:getting ready to update settings for - " + readParms[i]) ;
			}
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httppost = new HttpPost(UpdaterService.SET_CURRENTUSERINFO);
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
					String prefix = "Token=" + token + "&UserID=" + userId + "&Request={" ;
					String streamName = null ;
					String jsonString = null ;
					String request ;
					if (readParms[i].compareToIgnoreCase(StatusStream.TYPE_FOR_NEWS_TABLE)==0)
					{
						streamName = "\"News\":" ;
						Gson gson = new GsonBuilder().create();
						//jsonString = gson.toJson(mStreamsUser.getNews());
						String jsonStringKeywords = gson.toJson(streamsUser.getNews().getKeywords());
						String jsonStringEnabled =  gson.toJson(streamsUser.getNews().getEnabled());
						//{"Keywords":["israel","google","apple","global","warming","President Obama","economy"],"Color":{"Right":"","Left":"","Front":""},"Light":"","Enabled":"0"}}
						jsonString = "{\"Keywords\":" + jsonStringKeywords + ",\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Enabled\":" + jsonStringEnabled
								+ "}}"
								;
					}
					if (readParms[i].compareToIgnoreCase(StatusStream.TYPE_FOR_EMAIL_TABLE)==0)
					{   // appears that the server is noncomplant re:JSON. Needs to have these in the order below, otherwise wont parse. Ouch.
						streamName = "\"Email\":"  ;
						Gson gson = new GsonBuilder().create();
						jsonString =  gson.toJson(streamsUser.getEmail().getColor());
						jsonString =  gson.toJson(streamsUser.getEmail().getLight());
						String jsonStringAcct =  gson.toJson(streamsUser.getEmail().getAccount());
						String jsonStringEnabled =  gson.toJson(streamsUser.getEmail().getEnabled());
						jsonString = //gson.toJson(mStreamsUser.getEmail());
								"{\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Account\":" +
								jsonStringAcct +  ",\"Enabled\":" + jsonStringEnabled + "}}" ;

						//jsonString = "{\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Account\":[\"digiotastudios@gmail.com\"],\"Enabled\":\"1\"}}"  ;

						//Token=1b433e7336975e6af3e9458a6e02fe95&UserID=1356
						//&Request={"Email":{"Color":{"Right":"","Left":"","Front":""},"Light":"","Account":["digiotastudios@gmail.com"],"Enabled":"0"}}
					}
					if (readParms[i].compareToIgnoreCase(StatusStream.TYPE_FOR_TWITTER_TABLE)==0)
					{
						streamName = "\"Twitter\":" ;
						Gson gson = new GsonBuilder().create();
						//jsonString = gson.toJson(mStreamsUser.getTwitter());
						String jsonStringAcct =  gson.toJson(streamsUser.getTwitter().getAccount());
						String jsonStringEnabled =  gson.toJson(streamsUser.getTwitter().getEnabled());
						jsonString =
								"{\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Account\":" + jsonStringAcct +
								",\"Enabled\":" + jsonStringEnabled + "}}" ;
						//Token=1b433e7336975e6af3e9458a6e02fe95&UserID=1356
						//&Request={"Twitter":{"Color":{"Right":"","Left":"","Front":""},"Light":"","Account":["jdiamand"],"Enabled":"0"}}
					}
					if (readParms[i].compareToIgnoreCase(StatusStream.TYPE_FOR_INSTAGRAM_TABLE)==0)
					{
						streamName = "\"Instagram\":" ;
						Gson gson = new GsonBuilder().create();
						//jsonString = gson.toJson(mStreamsUser.getTwitter());
						String jsonStringAcct =  gson.toJson(streamsUser.getInstagram().getAccount());
						String jsonStringEnabled =  gson.toJson(streamsUser.getInstagram().getEnabled());
						jsonString =
								"{\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Account\":" + jsonStringAcct +
								",\"Enabled\":" + jsonStringEnabled + "}}" ;

						//Token=1b433e7336975e6af3e9458a6e02fe95&UserID=1356
						//&Request={"Twitter":{"Color":{"Right":"","Left":"","Front":""},"Light":"","Account":["jdiamand"],"Enabled":"0"}}
						//

					}
					if (readParms[i].compareToIgnoreCase(StatusStream.TYPE_FOR_WEATHER_TABLE)==0)
					{

						streamName = "\"Weather\":" ;
						Gson gsonSend = new GsonBuilder().create();
						//jsonString = gson.toJson(mStreamsUser.getNews());
						String jsonStringCity = gsonSend.toJson(streamsUser.getWeather().getLocation().getCity());
						String jsonStringZip =  gsonSend.toJson(streamsUser.getWeather().getLocation().getZip());
						//{"Weather":{"Color":{"Right":"","Left":"","Front":""},"Location":{"Zip":"10019","City":"New York"},"Light":"","Enabled":"1"}}
						//{"Weather":{"Light":"","Color":{"Right":"","Left":"","Front":""},"Location":{"Zip":"92014","City":""},"Enabled":"1"}}
						//jsonString = "{\"Keywords\":" + jsonStringKeywords + ",\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Light\":\"\",\"Enabled\":" + jsonStringEnabled
						//	+ "}}"
						jsonString = "{\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Location\":{\"Zip\":\"" +
						//jsonString = "{\"Light\":\"\",\"Color\":{\"Right\":\"\",\"Left\":\"\",\"Front\":\"\"},\"Location\":{\"Zip\":\"" +
								streamsUser.getWeather().getLocation().getZip() + "\",\"City\":\"" + streamsUser.getWeather().getLocation().getCity() +

								"\"}," +
								"\"Light\":\"\"," +
								"\"Enabled\":\"" + streamsUser.getWeather().getEnabled() + "\"}}" ;

					}
					request = prefix + streamName + jsonString ;
					//String request = "Token=" + token + "&UserID=" + userId + "&ID=" + readParms[0] + "&Type=" +
					//		readParms[1] + "&Status=1";
					StringEntity se = new StringEntity( request);

					httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
					httppost.setEntity(se);
					HttpResponse httpResponse = httpclient.execute(httppost);

					BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
					StringBuilder builder = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						line = VerifyCredentialsTask.cleanUpJsonFromServer(line) ;
						builder.append(line).append("\n");
					}
					Gson gson = new GsonBuilder().

							setDateFormat("yyyy-mm-DD HH:mm:ss").create();

					StreamsStream streamsStream =  gson.fromJson( builder.toString(), StreamsStream.class);
					String status1 = streamsStream.getstatus() ;
					String status2 = streamsStream.getStatus() ;
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg", "SendServerSettingsChanged: status =" + status1 + ", Status = " + status2 + " For settings type=" + readParms[i]) ;
					}
					retVal = (long)1 ;

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

		}



		}catch(Exception e){e.printStackTrace();}
		return retVal;
	}

	/**
	 *
	 */
	public static void CheckAndSendServerSettingsChanged()
	{

		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		if (streamsUser != null)
		{   // upon exiting settings mActivity to return to StreamsFragment
			//   we need to see if there are any changes to the setting to communicate back up
			//   to the server
			ArrayList<String> stringArrayList = new ArrayList<String>() ;
			if (  streamsUser.getModifiedSettings().isTwitterSettingsModified() )
			{
				stringArrayList.add(StatusStream.TYPE_FOR_TWITTER_TABLE) ;

			}
			if (  streamsUser.getModifiedSettings().isEmailSettingsModified() )
			{
				stringArrayList.add(StatusStream.TYPE_FOR_EMAIL_TABLE) ;

			}
			if (  streamsUser.getModifiedSettings().isNewsSettingsModified() )
			{
				stringArrayList.add(StatusStream.TYPE_FOR_NEWS_TABLE) ;

			}
			if (  streamsUser.getModifiedSettings().isInstagramSettingsModified() )
			{
				stringArrayList.add(StatusStream.TYPE_FOR_INSTAGRAM_TABLE) ;

			}

			if (  streamsUser.getModifiedSettings().isFacebookSettingsModified() )
			{
				stringArrayList.add(StatusStream.TYPE_FOR_FACEBOOK_TABLE) ;

			}
			if (  streamsUser.getModifiedSettings().isWeatherSettingsModified() )
			{
				stringArrayList.add(StatusStream.TYPE_FOR_WEATHER_TABLE) ;

			}


			if (stringArrayList.size() > 0)
			{
				String[] parmsArray = stringArrayList.toArray(new String[stringArrayList.size()]);

				new SendServerSettingsChanged().execute(parmsArray) ;


			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 *
	 */
	public static void ClearServerSettingsChanged()
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		if (streamsUser != null)
		{   
			streamsUser.getModifiedSettings().setTwitterSettingsModified(false);
			streamsUser.getModifiedSettings().setEmailSettingsModified(false);
			streamsUser.getModifiedSettings().setFacebookSettingsModified(false);
			streamsUser.getModifiedSettings().setNewsSettingsModified(false);
			streamsUser.getModifiedSettings().setInstagramSettingsModified(false);
			streamsUser.getModifiedSettings().setWeatherSettingsModified(false);
		}
		}catch(Exception e){e.printStackTrace();}
	}

}