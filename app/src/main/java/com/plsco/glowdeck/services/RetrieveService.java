package com.plsco.glowdeck.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


/**
 * Created by smartdev on 8/23/17.
 */

public class RetrieveService extends Service {

    static RetrieveService msCurrentContext ;
    public static String uuids = "";
    public  Location userLocation;
    public ArrayList<String> streamArray = new ArrayList();
    public HashMap<String,String> streamDict = new HashMap<String,String>();

    public int weatherUpdated  = 0;
    public boolean firmwareUpdateActive  = false;


    private Handler handler;
    private Runnable runnable;
    private final int runTime = 10*60*10000;

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

        msCurrentContext = this;

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(runnable, runTime);
                updateWeather(true) ;
            }
        };
        handler.post(runnable);

    }


    /*

     */
    public  void updateWeather(boolean tx)
    {
        if (!isWeatherAvailable()) { return; }

        if (userLocation.country.length() == 2 && weatherUpdated > -3) {
            // "&APPID=0dc5670d4037da812f743888ecf5a3e3"
            String weatherString = "http://api.openweathermap.org/data/2.5/weather?q=";
            weatherString += userLocation.city;
            weatherString += ",";
            weatherString += userLocation.country;
            weatherString += "&APPID=0dc5670d4037da812f743888ecf5a3e3";


            try {

                String weatherUrl = URLEncoder.encode(weatherString, "utf-8");

                 getWeatherData(weatherUrl, tx);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public void getWeatherData(String urlString, final boolean tx) {


        if (!isWeatherAvailable()) { return; };

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setWeatherData(response, tx);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });


    }

    public void setWeatherData(JSONObject json, boolean tx)
    {


        try {
            JSONArray weather = (JSONArray) json.get("weather");

            if (weather.length() > 0 && weather != null) {
                userLocation.forecast = weather.getJSONObject (0).getString("main");

            }else {
                userLocation.forecast = "Fair";
            }

            JSONObject main = json.getJSONObject("main");

            int tempConvert = 0;

            if (main.has("temp")) {
                double temp = main.getDouble("temp");

                if (userLocation.country.equals("US"))
                    tempConvert = (int)(temp * (9/5) - 459.67);
                else
                    tempConvert = (int)(temp - 273.15);

                userLocation.temperature = String.valueOf(tempConvert);
            }else
                userLocation.temperature = "72";


//            String currentWeatherString =
            //tickerTextRightToLeft


            String glowdeckTransmit = String.format("WTR:%d|%s|%s^\r", userLocation.temperature, userLocation.forecast, userLocation.city);

            bleSend(glowdeckTransmit);
            weatherUpdated = 1;
        } catch (JSONException e) {
            weatherUpdated -= 1;
        }

    }

    public void bleSend(String sendCmd) {
        if (firmwareUpdateActive && !sendCmd.contains("GFU")) {
            Log.d("[TX CANCEL] %s", sendCmd);
            return;
        }

        Log.d("[SEND] %s", sendCmd);
        final StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication();
        streamsApplication.getBluetoothSppManager().sendMessage(sendCmd);

    }

    public  boolean isWeatherAvailable()
    {
        return true;

//        var zeroAddress = sockaddr_in()
//        zeroAddress.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
//        zeroAddress.sin_family = sa_family_t(AF_INET)
//
//        guard let defaultRouteReachability = SCNetworkReachabilityCreateWithName(kCFAllocatorDefault, ("api.openweathermap.org" as NSString).utf8String!) else { return false }
//        /*
//         guard let defaultRouteReachability = withUnsafePointer(to: &zeroAddress, {
//         $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
//         SCNetworkReachabilityCreateWithAddress(nil, $0)
//         }
//         }) else {
//         return false
//         }
//         */
//
//        var flags: SCNetworkReachabilityFlags = []
//        if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
//        return false
//    }
//
//        let isReachable = flags.contains(.reachable)
//        let needsConnection = flags.contains(.connectionRequired)
//
//        return (isReachable && !needsConnection)
    }












    public void showStream(){
        if (streamArray.size() > 0)
        {
            String pop = streamArray.remove(0);
            bleSend(pop);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //-----------------------------------------------------------------------------------------------

    static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }

    public void getRequestStreams( String query) {
        String urlString = "https://webhose.io/search?token=dc0d4206-31f8-482c-90ce-515b9e312254&format=json&q=";
        ArrayList keywords = new ArrayList();
        int timeInterval = (int)(new Date().getTime() * 1000.0) - (60*60*24*3);

        String timeString = String.format("%d" , timeInterval);


        String suffix = "%20language%3A(english)%20site_category%3Amedia%20(site_type%3Anews)&sort=relevancy";

        if (query.contains(",")) {
            keywords = new ArrayList(Arrays.asList(query.split(",")));
        }
        else {
            keywords.add(query);
        }


        String params = "(";
        try {
            if (keywords.size() == 1) {



                    params += URLEncoder.encode((String) keywords.get(0), "utf-8");
                    params += ")";



            }
            else {
                for (int i = 0; i <keywords.size(); i++) {
                    if (i < keywords.size()-1) {
                        params += URLEncoder.encode((String) keywords.get(0), "utf-8") + "%20OR%20";
                    }
                    else {
                        params += URLEncoder.encode((String) keywords.get(0), "utf-8") + ")";
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        urlString += params;
        urlString += suffix;

        Log.d("dbg", "Stream Query Endpoint URL:" + urlString);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonDict) {

                        try {

                            String uuidAdd = "";
                            String result = "";

                            if (jsonDict.has("totalResults")) {
                                int results = jsonDict.getInt("totalResults");
                                if (results == 0) {
                                    return;
                                } else {

                                    if (jsonDict.has("posts")) {

                                        JSONObject postsDict = jsonDict.getJSONObject("posts");

                                        Iterator<String> keys = postsDict.keys();

                                        while (keys.hasNext()) {
                                            // Get the key
                                            String key = keys.next();

                                            // Get the value
                                            JSONObject postDict = postsDict.getJSONObject(key);

                                            if (postDict.has("uuid")) {

                                                uuidAdd = postDict.getString("uuid");

                                                if (!uuids.contains(uuidAdd)) {
                                                    if (uuids == "") {
                                                        uuids += uuidAdd;
                                                    } else {
                                                        uuids += String.format(",%s", uuidAdd);
                                                    }

                                                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(msCurrentContext);
                                                    SharedPreferences.Editor editor = sharedPrefs.edit();

                                                    editor.putString("uuids", uuids);

                                                    editor.commit();


                                                    if (postDict.has("thread")) {
                                                        JSONObject threadDict = postDict.getJSONObject("thread");
                                                        if (threadDict.has("site") && threadDict.has("title") && threadDict.has("url")) {
                                                            String result1 = "NOT:";
                                                            String source = threadDict.getString("site");
                                                            if (source.contains(".")) {
                                                                String[] srcComps = source.split(".");
                                                                source = srcComps[0];
                                                            }
                                                            if (source.length() > 5) {
                                                                result1 += source.substring(0, 1).toUpperCase() + source.substring(1);
                                                            } else {
                                                                result1 += source.toUpperCase();
                                                            }
                                                            result1 += "|N|";

                                                            String headline = threadDict.getString("title");

                                                            result1 += headline;
                                                            result1 += "|1d^\r";

                                                            String streamUrl = threadDict.getString("url");

                                                            streamArray.add(result);
                                                            streamDict.put(result, streamUrl);

                                                        }
                                                    }
                                                }
                                            }
                                        }


                                        if (!streamArray.isEmpty()) {

                                            Random r = new Random();
                                            int wait = r.nextInt(60 - 10) + 10;

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Do something after 5s = 5000ms
                                                    showStream();
                                                }
                                            }, wait);

                                        } else {

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Do something after 5s = 5000ms
                                                    showStream();
                                                }
                                            }, 180);
                                        }

                                    }
                                }

                            }
                        }catch (JSONException e) {
                            weatherUpdated -= 1;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

    }



}
