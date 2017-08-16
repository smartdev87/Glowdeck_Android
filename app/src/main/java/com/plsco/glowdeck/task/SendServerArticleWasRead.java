package com.plsco.glowdeck.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.services.UpdaterService;
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

/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: SendServerArticleWasRead.java
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

/**
 *
 * SendServerArticleWasRead extends AsyncTask
 *
 */
public class SendServerArticleWasRead extends AsyncTask<String, Integer, Long> {
    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Long result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Long doInBackground(String... readParms) {

        Long retVal = (long) 0;

        try{
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(UpdaterService.SET_STATUS);
        String token = null;
        String userId = null;

        StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser();
        if (streamsUser != null) {
            StreamsAccount streamsAccount = streamsUser.getStreamsAccount();
            if (streamsAccount != null) {
                userId = streamsAccount.getID();
                token = streamsAccount.getToken();

            }
        }
        if ((userId != null) && (token != null)) {

            try {
                String request = "Token=" + token + "&UserID=" + userId + "&ID=" + readParms[0] + "&Type="
                        + readParms[1] + "&Status=1";
                StringEntity se = new StringEntity(request);

                httppost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded"));
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

                StreamsStream streamsStream = gson.fromJson(builder.toString(), StreamsStream.class);
                String status1 = streamsStream.getstatus();
                String status2 = streamsStream.getStatus();
                if (StreamsApplication.DEBUG_MODE)
                {
                    Log.d("dbg", "SendServerArticleWasRead: status =" + status1 + ", Status = " + status2);
                }
                retVal = (long) 1;

            }

            catch (IOException e) {
                e.printStackTrace();
                if (StreamsApplication.DEBUG_MODE)
                {
                    Log.d("dbg", "IOException Error in SendServerArticleWasRead = " + e);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (StreamsApplication.DEBUG_MODE)
                {
                    Log.d("dbg", "Exception Error in SendServerArticleWasRead = " + e);
                }

            } finally {
                if (StreamsApplication.DEBUG_MODE)
                {
                    Log.d("dbg", "Finally in updateServerWithUserParms.");
                }
            }
        }
        }catch(Exception e){e.printStackTrace();}
        return retVal;
    }

}