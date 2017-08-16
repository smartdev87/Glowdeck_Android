package com.plsco.glowdeck.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plsco.glowdeck.auth.LoginActivity;
import com.plsco.glowdeck.bluetooth.BTDeviceListActivity;
import com.plsco.glowdeck.bluetooth.BluetoothBleManager;
import com.plsco.glowdeck.bluetooth.BluetoothSppManager;
import com.plsco.glowdeck.colorpicker.AppConfig;
import com.plsco.glowdeck.drawer.AboutFragment;
import com.plsco.glowdeck.drawer.PickerFragment;
import com.plsco.glowdeck.drawer.ProfileFragment;
import com.plsco.glowdeck.drawer.ProvisionFragment;
import com.plsco.glowdeck.drawer.StreamsDrawerItem;
import com.plsco.glowdeck.drawer.StreamsDrawerListAdapter;
import com.plsco.glowdeck.drawer.StreamsFragment;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.settings.StreamsSettingsActivity;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice;

import java.util.ArrayList;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: MainAcivity.java
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
 * The MainActivity manages multiple features, such as the main pull out drawer (left-to-right)
 *
 *
 */
public class MainActivity extends   Activity  {

    private static int mBatteryLevel ;
    public static int getmBatteryLevel() {
        return mBatteryLevel;
    }
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            try{
                mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            }catch(Exception e){e.printStackTrace();}
        }
    };





    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        msPaused = true ;
        super.onPause();
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        try{
            mainActivity = this ;
        }catch(Exception e){e.printStackTrace();}
        super.onStart();
    }
    //
    // Globals
    //
    private int											mCurrentGlowdeckIndex   ;
    private Fragment 									mCurrentFragment = null;
    public void setmCurrentFragment(Fragment mCurrentFragment) {
        this.mCurrentFragment = mCurrentFragment;
    }
    public Fragment getmCurrentFragment() {
        return mCurrentFragment;
    }
    private Fragment 									mPreviousFragment = null;
    private StreamsApplication mStreamsApplication = null;
    private ActionBarDrawerToggle 						mDrawerToggle ;
    private DrawerLayout 								mDrawerLayout;
    private ListView 									mDrawerList;
    private ActionBar 									mActionBar   ;
    private ActionBar.Tab                               mTabWifi ;
    private ActionBar.Tab                               mTabLights ;
    private Menu                                        mStreamsMenu  ;
    private static CurrentGlowdecks msCurrentGlowdecks = null ;

    private static BlueToothdeviceTab                   mBlueToothdeviceTab = BlueToothdeviceTab.WIFI_TAB ;

    private CharSequence 								mDrawerTitle;// drawer title
    private boolean 									mStreamSettingsOn = true ; // initially, the setting is visible

    private String[] 									mStreamsMenuTitles = null; // slide menu items
    private TypedArray 									mStreamsMenuIcons = null ;
    private ArrayList<StreamsDrawerItem> 				mStreamsDrawerItem = null;
    private StreamsDrawerListAdapter mStreamsDrawerListAdapter = null ;

    public CurrentGlowdecks getCurrentGlowdecks() {
        return msCurrentGlowdecks;
    }
    public StreamsDrawerListAdapter getmStreamsDrawerListAdapter() {
        return mStreamsDrawerListAdapter;
    }
    private CharSequence 								mTitle;// store the app title
    //
    //  Globals (static)
    private static final int                                    REQUEST_CONNECT_DEVICE = 998;
    private final static int 									REQUEST_ENABLE_BT     = 999 ;
    private static Context 								msApplicationContext ;
    private static MainActivity mainActivity = null ;
    private static boolean                              mSettabs = false ;
    private static boolean 								msIsLoggedIn = false ; // used as global to check of a login is required
    //   we always try to avoid a login requirements
    public static boolean 								msMenusInitialized = false ;
    public static boolean 								msDevicesClicked = false ;
    //
    private static StreamsType                          msStreamsType = StreamsType.STREAM_TYPE_PERSONAL ; // for now default to personal each time , next add to serializable in onPostCreate;
    private static StreamsScreenState 					msStreamsState = StreamsScreenState.STARTING ;
    // initial stream screen msState
    private static StreamsScreenState 					msNextStreamsState = StreamsScreenState.INVALID ;
    // only used when screenState is "Devices_view"
    private static StreamsScreenState 					msPrevStreamsState = StreamsScreenState.INVALID ;
    private static StreamsScreenState 					msPrevStreamsStateDevices = StreamsScreenState.INVALID ;

    private static boolean                              msPaused ;
    public static boolean isMsPaused() {
        return msPaused;
    }
    public static BlueToothdeviceTab getmBlueToothdeviceTab() {
        return mBlueToothdeviceTab;
    }
    public static void setmBlueToothdeviceTab(BlueToothdeviceTab mBlueToothdeviceTab) {
        MainActivity.mBlueToothdeviceTab = mBlueToothdeviceTab;
    }
    public static MainActivity getMainActivity() {
        return mainActivity;
    }
    public static void setMainActivity(MainActivity mainActivity) {
        MainActivity.mainActivity = mainActivity;
    }
    // The string to use for
    // the bundle InstanceState for the StreamsScreenState
    public static StreamsType getStreamsType()
    {
        return msStreamsType ;
    }
    public static void setStreamType(StreamsType type)
    {
        msStreamsType = type ;
    }
    public static StreamsScreenState getStreamsState()
    {
        return msStreamsState ;
    }
    public static void setStreamState(StreamsScreenState state)
    {
        msStreamsState= state ;
    }
    public static StreamsScreenState getNextStreamsState()
    {
        return msNextStreamsState ;
    }
    public static void setNextStreamState(StreamsScreenState state)
    {
        msNextStreamsState= state ;
    }
    public static Context getAppContext()
    {
        return msApplicationContext ;
    }
    //	Constants
    public  static final String STREAMS_STATE = "msStreamsState" ;
    public  static final String STREAMS_TYPE = "msStreamsType" ;
    //

    public  static final   int DRAWER_STREAM_FRAGMENT = 0 ;
    public  static final   int DRAWER_DEVICES_NOFRAGMENT =  1 + DRAWER_STREAM_FRAGMENT;
    public  static final   int DRAWER_GLOWDECK_DEVICES0  = 1 + DRAWER_DEVICES_NOFRAGMENT;
    public  static final   int DRAWER_DEVICES_GETMORE_FRAGMENT =  1 + DRAWER_GLOWDECK_DEVICES0;
    public  static final   int DRAWER_PROFILE_FRAGMENT =  1 + DRAWER_DEVICES_GETMORE_FRAGMENT   ;
    public  static final   int DRAWER_ABOUT_FRAGMENT   =  1 + DRAWER_PROFILE_FRAGMENT;
    public  static final   int DRAWER_SIGNOUT_FRAGMENT =  1 + DRAWER_ABOUT_FRAGMENT ;
    public  static final   int DRAWER_START_PROVISION =  1000 ;
    public  static final   int DRAWER_SHOW_MAIN_PROVISION =  DRAWER_START_PROVISION + 1 ;
    public  static final   int DRAWER_SHOW_PICKER =  DRAWER_SHOW_MAIN_PROVISION + 1 ;
    //
    public static final String Register_New_Device = "REGISTER NEW DEVICE";

    public static final String Lights_Controller = "Glowdeck";
    //
    //
    public enum BlueToothdeviceTab {
        WIFI_TAB, //0
        LIGHTS_TAB  //1

    }

    public enum StreamsScreenState {
        STARTING, //0
        SPLASH_SCREEN, //1
        LOGIN_SCREEN, //2
        CREATE_USERID_SCREEN,//3
        STREAM_SETTINGS,//4
        STREAMS_VIEW,//5
        DEVICES_VIEW, //6
        DEVICES_VIEW_DEVICES, //7
        PROFILE_VIEW, //8
        ABOUT_VIEW, //9
        PROVISIONING_VIEW, //10
        PICKER_VIEW, //11
        GLOWDECK_CONFIG_VIEW, //12
        SIGNOUT_VIEW, //13
        INVALID     // 14
    }

    StreamsScreenState intToStreamsState(int value)
    {
        StreamsScreenState retVal ;
        switch (value)
        {
            case 1 :
                retVal =  StreamsScreenState.SPLASH_SCREEN;
                break ;
            case 2 :
                retVal =  StreamsScreenState.LOGIN_SCREEN;
                break ;
            case 3 :
                retVal =  StreamsScreenState.CREATE_USERID_SCREEN;
                break ;
            case 4 :
                retVal =  StreamsScreenState.STREAM_SETTINGS;
                break ;
            case 5 :
                retVal =  StreamsScreenState.STREAMS_VIEW;
                break ;
            case 6 :
                retVal =  StreamsScreenState.DEVICES_VIEW;
                break ;
            case 7 :
                retVal =  StreamsScreenState.DEVICES_VIEW_DEVICES;
                break ;
            case 8 :
                retVal =  StreamsScreenState.PROFILE_VIEW;
                break ;
            case 9 :
                retVal =  StreamsScreenState.ABOUT_VIEW;
                break ;
            case 10 :
                retVal =  StreamsScreenState.PROVISIONING_VIEW;
                break ;
            case 11 :
                retVal =  StreamsScreenState.PICKER_VIEW;
                break ;
            case 12 :
                retVal =  StreamsScreenState.GLOWDECK_CONFIG_VIEW;
                break ;
            case 13 :
                retVal =  StreamsScreenState.SIGNOUT_VIEW;
                break ;
            default:
            case 0 :
                retVal =  StreamsScreenState.STARTING;
                break ;
        }
        return retVal ;
    }
    public enum StreamsType {
        STREAM_TYPE_PERSONAL  ,
        STREAM_TYPE_SOCIAL,
        STREAM_TYPE_PUBLIC
    }

    //flag variable for first open
    public static boolean mFirstOpen = true;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstOpen = true;

        try{
            mStreamsApplication = (StreamsApplication)this.getApplication() ;
            if (StreamsApplication.BLUETOOTH_SUPPORT_BLE)
            {
                BluetoothBleManager bluetoothBleManager = StreamsApplication.getBluetoothBleManager() ;
                if (bluetoothBleManager != null)
                {
                    bluetoothBleManager.initBleWrapper(this) ;
                }
            }
            BluetoothSppManager bluetoothSppManager = null ;
            if  (StreamsApplication.BLUETOOTH_SUPPORT_SPP)
            {
                bluetoothSppManager = mStreamsApplication.getBluetoothSppManager() ;
                if (bluetoothSppManager != null)
                {
                    bluetoothSppManager.initBluetoothSppManager(this) ;
                }
            }

            if (msCurrentGlowdecks == null)
            {

                msCurrentGlowdecks = new CurrentGlowdecks(bluetoothSppManager) ;
            }

            msApplicationContext = getBaseContext() ;

            setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
            setContentView(R.layout.activity_main);

            mTitle =  getTitle();

            mDrawerTitle = getResources().getString( R.string.drawer_name) ;

            // load slide menu items
            mStreamsMenuTitles = getResources().getStringArray(R.array.streams_drawer_items);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.list_slidermenu);


            if (mStreamsDrawerItem == null)
            {
                // nav drawer icons from resources
                mStreamsMenuIcons = getResources()
                        .obtainTypedArray(R.array.streams_drawer_icons) ;


                mStreamsDrawerItem = new ArrayList<StreamsDrawerItem>();

                int numMenuItems = mStreamsMenuTitles.length ;
                // adding drawer items to array
                // Streams
                for (int i = 0 ; i < numMenuItems ; i++)
                {
                    mStreamsDrawerItem.add(new StreamsDrawerItem(mStreamsMenuTitles[i], mStreamsMenuIcons.getResourceId(i, -1)));
                }

                // Recycle the typed array
                mStreamsMenuIcons.recycle();
                mStreamsDrawerListAdapter = new StreamsDrawerListAdapter(getApplicationContext(),mStreamsDrawerItem);
                mDrawerList.setAdapter(mStreamsDrawerListAdapter);
            }
            mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
            // enabling action bar app icon and behaving it as toggle button
            mActionBar = getActionBar();



            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.drawable.ic_drawer, //menu toggle icon
                    R.string.drawer_name, //  com.glowdeck.streams.drawer open - description for accessibility
                    R.string.drawer_name  // com.glowdeck.streams.drawer open - description for accessibility
            ) {
                public void onDrawerClosed(View view) {
                    String menuTitle = mTitle.toString();
                    getActionBar().setTitle(menuTitle);

                    // calling onPrepareOptionsMenu() to show action bar icons
                    invalidateOptionsMenu();
                    // stop polling for bluetooth devices
                }

                public void onDrawerOpened(View drawerView) {
                    String menuTitle = mDrawerTitle.toString();
                    getActionBar().setTitle(menuTitle);


                    invalidateOptionsMenu();
                    // start immediate alarm for polling for bluetooth devices
                    // then poll every 5 seconds while drawer is opened
                }
            };

            mDrawerLayout.addDrawerListener(mDrawerToggle);


            mDrawerLayout.openDrawer(mDrawerList);

            mBatteryLevel = 0 ;
            registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }catch(Exception e){e.printStackTrace();}
    } // done with onCreate()



    /* (non-Javadoc)
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle msState after onRestoreInstanceState has occurred.
        try{
            mDrawerToggle.syncState();
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            if (StreamsApplication.DEBUG_MODE)
            {
                Log.d("dbg","MainActivity::onPostCreate::savedInstanceState=" + savedInstanceState) ;
            }
            if (savedInstanceState != null)
            {
                Integer streamsState  = 11; //  (Integer) savedInstanceState.getSerializable(STREAMS_STATE);
                if (streamsState != null)
                {
                    msStreamsState = intToStreamsState(streamsState.intValue()) ;
                }
                Integer streamsType  =  (Integer) savedInstanceState.getSerializable(STREAMS_TYPE);

                if (streamsType != null)
                {
                    if (StreamsApplication.DEBUG_MODE)
                    {

                        Log.d("dbg","MainActivity::onPostCreate::msStreamsType=" + streamsType.intValue() ) ;
                    }
                    msStreamsType = intToStreamsType(streamsType.intValue()) ;
                    invalidateOptionsMenu();
                }
            }

            if (msStreamsState == StreamsScreenState.STREAMS_VIEW)
            {
                displayView( DRAWER_STREAM_FRAGMENT );
                // displayView(DRAWER_SHOW_PICKER);
            }

            if (msStreamsState == StreamsScreenState.ABOUT_VIEW)
            {

                displayView(DRAWER_ABOUT_FRAGMENT)  ;
                return ;

            }
            if (msStreamsState == StreamsScreenState.DEVICES_VIEW)
            {
                //displayView(DRAWER_DEVICES_NOFRAGMENT)  ;
                displayView( DRAWER_STREAM_FRAGMENT );
                return ;

            }
            if (msStreamsState == StreamsScreenState.DEVICES_VIEW_DEVICES )
            {

                displayView( DRAWER_GLOWDECK_DEVICES0  );
			/*
			if (mSettabs   )
			{
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			}
			 */
                return ;

            }
            if (msStreamsState == StreamsScreenState.PROFILE_VIEW)
            {
                displayView(DRAWER_PROFILE_FRAGMENT)  ;
                return ;

            }
            if (msStreamsState == StreamsScreenState.PICKER_VIEW)
            {
                displayView(DRAWER_SHOW_PICKER)  ;
                return ;

            }

            boolean launchLoginScreen = !checkIfSignedIn() ;

            if (launchLoginScreen)
            {
                msStreamsState = StreamsScreenState.LOGIN_SCREEN ;
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent,  0);
            }
            else
            {


                if (!UpdaterService.isServiceRunning())
                {
                    if (StreamsApplication.DEBUG_MODE)
                    {
                        Log.d("dbg","MainActivity:starting UpdaterService") ;
                    }
                    startService(new Intent(this, UpdaterService.class)); // make sure its running
                }


                if (msStreamsState == StreamsScreenState.STREAM_SETTINGS)
                {
                    Intent intent = new Intent(MainActivity.this, StreamsSettingsActivity.class);
                    startActivityForResult(intent,  0);
                }
                else
                {
                    if (msStreamsState == StreamsScreenState.STARTING)
                    {
                        // starting out, but did a pseudo-login, so default to
                        // the streams_screen
                        // msStreamsState = StreamsScreenState.STREAMS_VIEW ;

                        msStreamsState = StreamsScreenState.PICKER_VIEW ;
                        displayView(DRAWER_STREAM_FRAGMENT) ;
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements  ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display mView for selected streams drawer item

            try{
                displayView(position);
            }catch(Exception e){e.printStackTrace();}
        }
    }





    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if ( msStreamsState ==  StreamsScreenState.PICKER_VIEW)
        {
            return super.onCreateOptionsMenu(menu);
        }
        try{
            getMenuInflater().inflate(R.menu.main, menu);
            menu.findItem(R.id.action_settings).setVisible(false);
            mStreamsMenu = menu ;
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_activity_actions, menu);
        }catch(Exception e){e.printStackTrace();}
        return super.onCreateOptionsMenu(menu);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            if (StreamsApplication.DEBUG_MODE)
            {

                Log.d("dbg","MainActivity::onOptionsItemSelected::home baby") ;

            }
            if (msStreamsState == StreamsScreenState.DEVICES_VIEW_DEVICES)
            {
                if (StreamsApplication.DEBUG_MODE)
                {
                    Log.d("dbg","MainActivity::onOptionsItemSelected::DEVICES_VIEW_DEVICES") ;
                }

                onBackPressed() ;
            }
            if (msStreamsState == StreamsScreenState.DEVICES_VIEW)
            {
                if (StreamsApplication.DEBUG_MODE)
                {
                    Log.d("dbg","MainActivity::onOptionsItemSelected::DEVICES_VIEW") ;
                }

                onBackPressed() ;
            }

        }



        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if (StreamsApplication.DEBUG_MODE)
            {

                Log.d("dbg","MainActivity::onOptionsItemSelected::mDrawerToggle.onOptionsItemSelected(item) is true") ;
            }
            return true;
        }

        // Handle action bar actions click
        switch (item.getItemId()) {

            case R.id.ic_stream_setting:

                if ( msStreamsState ==  StreamsScreenState.PICKER_VIEW)
                {
                    return super.onOptionsItemSelected(item);
                }

                try{
                    msStreamsState = StreamsScreenState.STREAM_SETTINGS ;
                    Intent intent = new Intent(MainActivity.this, StreamsSettingsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }catch(Exception e){e.printStackTrace();}
                return true;
            case R.id.ic_stream_type:
                try{
                    switchStreamPage(  item) ;
                }catch(Exception e){e.printStackTrace();}


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void switchStreamPage( )
    {
        try{
            switchStreamPage(mStreamsMenu.findItem(R.id.ic_stream_type)) ;
        }catch(Exception e){e.printStackTrace();}
    }
    /*
     void restoreStreamPage()
        {
            MenuItem item = mStreamsMenu.findItem(R.id.ic_stream_type) ;
            //getResources().getString( R.string. personal_stream ) ;
            if (msStreamsType == StreamsType.STREAM_TYPE_PERSONAL)
            {
                item.setTitle(getResources().getString( R.string. personal_stream )) ;

                ((StreamsFragment) mCurrentFragment).processNumSocialChanged(1) ;
                ((StreamsFragment) mCurrentFragment).setPageDots(StreamsFragment.PAGE_PERSONAL) ;
            }
            else
            {


                if (msStreamsType == StreamsType.STREAM_TYPE_SOCIAL )
                {
                    item.setTitle(getResources().getString( R.string. social_stream )) ;

                    msStreamsType = StreamsType.STREAM_TYPE_SOCIAL ;
                    ((StreamsFragment) mCurrentFragment).processNumPublicChanged(1) ;
                    ((StreamsFragment) mCurrentFragment).setPageDots(StreamsFragment.PAGE_SOCIAL) ;
                }
                else
                {
                    if (msStreamsType == StreamsType.STREAM_TYPE_PUBLIC  )
                    {
                        item.setTitle(getResources().getString( R.string. public_stream )) ;

                        msStreamsType = StreamsType.STREAM_TYPE_PUBLIC ;
                        ((StreamsFragment) mCurrentFragment).processNumPersonalChanged(1) ;
                        ((StreamsFragment) mCurrentFragment).setPageDots(StreamsFragment.PAGE_PUBLIC) ;
                    }
                }
            }
        }
     */
    void switchStreamPage(MenuItem item)
    {
        try{
            if (StreamsApplication.DEBUG_MODE)
            {
                Log.d("dbg","MainActivity::switchStreamPage::msStreamsType=" + msStreamsType.ordinal()) ;
            }
            ListView lv = null ;
            if (mCurrentFragment != null)
            {
                try{
                    lv =  ((StreamsFragment) mCurrentFragment).getmListView();
                }catch(Exception e){e.printStackTrace();}
            }
            else
            {
                return ;
            }
            int position = 0 ;
            if (lv != null)
            {
                try{
                    position = lv.getFirstVisiblePosition() ;
                }catch(Exception e){e.printStackTrace();}
            }
            if (msStreamsType == StreamsType.STREAM_TYPE_PERSONAL)
            {
                try{
                    ((StreamsFragment) mCurrentFragment).setmPositionPersonal(position) ;
                    item.setTitle(getResources().getString( R.string. social_stream )) ;
                    item.setIcon( getResources().getDrawable(R.drawable.social_button)) ;
                    msStreamsType = StreamsType.STREAM_TYPE_SOCIAL ;
                    ((StreamsFragment) mCurrentFragment).processNumSocialChanged(1) ;
                    ((StreamsFragment) mCurrentFragment).setPageDots(StreamsFragment.PAGE_SOCIAL) ;
                }catch(Exception e){e.printStackTrace();}
            }
            else
            {


                if (msStreamsType == StreamsType.STREAM_TYPE_SOCIAL )
                {
                    try{
                        ((StreamsFragment) mCurrentFragment).setmPositionSocial(position) ;
                        item.setTitle(getResources().getString( R.string. public_stream )) ;
                        item.setIcon( getResources().getDrawable(R.drawable.group_button)) ;
                        msStreamsType = StreamsType.STREAM_TYPE_PUBLIC ;
                        ((StreamsFragment) mCurrentFragment).processNumPublicChanged(1) ;
                        ((StreamsFragment) mCurrentFragment).setPageDots(StreamsFragment.PAGE_PUBLIC) ;
                    }catch(Exception e){e.printStackTrace();}
                }
                else
                {
                    if (msStreamsType == StreamsType.STREAM_TYPE_PUBLIC  )
                    {
                        try{
                            ((StreamsFragment) mCurrentFragment).setmPositionPublic(position) ;
                            item.setTitle(getResources().getString( R.string. personal_stream )) ;
                            item.setIcon( getResources().getDrawable(R.drawable.single_button)) ;
                            msStreamsType = StreamsType.STREAM_TYPE_PERSONAL ;
                            ((StreamsFragment) mCurrentFragment).processNumPersonalChanged(1) ;
                            ((StreamsFragment) mCurrentFragment).setPageDots(StreamsFragment.PAGE_PERSONAL) ;
                        }catch(Exception e){e.printStackTrace();}
                    }
                }
            }
            ((StreamsFragment) mCurrentFragment).listViewToPosition(msStreamsType) ;
        }catch(Exception e){e.printStackTrace();}
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if streams drawer is opened, then hide the streams setting items
        if ( msStreamsState ==  StreamsScreenState.PICKER_VIEW)
        {
            return super.onPrepareOptionsMenu(menu);
        }
        try{
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            boolean streamSettingsOn = false ;
            if ( (msStreamsState == StreamsScreenState.DEVICES_VIEW) )
            {
                msStreamsState = msPrevStreamsState ;
                mCurrentFragment  = mPreviousFragment  ;
            }


            if ((msStreamsState == StreamsScreenState.STREAMS_VIEW) || mStreamSettingsOn)
            {
                streamSettingsOn = true ;
            }
            if (drawerOpen)
            {
                // ic_stream_seetings in the streamSetting option on the right hand side of the
                // of the menu bar. its only visible when we are in the Streams screen
                menu.findItem(R.id.ic_stream_setting).setVisible(!drawerOpen);
                menu.findItem(R.id.ic_stream_type).setVisible(!drawerOpen);
            }
            else
            {
                menu.findItem(R.id.ic_stream_setting).setVisible(streamSettingsOn);
                menu.findItem(R.id.ic_stream_type).setVisible(streamSettingsOn);
                if (streamSettingsOn)
                {
                    MenuItem menuItemStreamType = menu.findItem(R.id.ic_stream_type);
                    switch(msStreamsType)
                    {
                        case STREAM_TYPE_PUBLIC :
                            menuItemStreamType.setTitle(getResources().getString( R.string. public_stream )) ;
                            menuItemStreamType.setIcon( getResources().getDrawable(R.drawable.group_button)) ;
                            break ;
                        case STREAM_TYPE_SOCIAL :
                            menuItemStreamType.setTitle(getResources().getString( R.string. social_stream )) ;
                            menuItemStreamType.setIcon( getResources().getDrawable(R.drawable.social_button)) ;

                            break ;
                        case STREAM_TYPE_PERSONAL :
                            menuItemStreamType.setTitle(getResources().getString( R.string. personal_stream )) ;
                            menuItemStreamType.setIcon( getResources().getDrawable(R.drawable.single_button)) ;
                            break ;

                    }
                }
            }

            menu.findItem(R.id.action_settings).setVisible(false);// This is the android "Settings" menu option ,
            // for this app, its always off
        }catch(Exception e){e.printStackTrace();}
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Displaying mCurrentFragment mView for selected  com.glowdeck.streams.drawer list item
     * */
    public void displayView(int position) {
        // update the main content by replacing fragments
        //Fragment mCurrentFragment = null;
        try{
            boolean noOp = false ;
            int offset = 0 ;
            int timeout = 0 ;
            mSettabs = false ;
            mStreamSettingsOn = false ;  // insure only turned on if the current screen is "Streams"
            msNextStreamsState = StreamsScreenState.INVALID ;
            switch (position) {
                case DRAWER_STREAM_FRAGMENT     :

//			if ( (msCurrentGlowdecks == null) || (msCurrentGlowdecks.getCurrentlyConnected() == null) )
//			{
//				//noOp = true ;
//				//break ;
//			}
//			if (!msCurrentGlowdecks.getCurrentlyConnected().isReceivedInit())
//			{
//				//noOp = true ;
//				//break ;
//			}
                    msStreamsState =  StreamsScreenState.PICKER_VIEW ;
                    mCurrentFragment = new PickerFragment();
                    mStreamSettingsOn = false ;
                    timeout = 100 ;
                    break;


//
//    /*
//			msStreamsState =  StreamsScreenState.STREAMS_VIEW ;
//			mCurrentFragment = new StreamsFragment();
//
//			timeout = 100 ;
//			mStreamSettingsOn = true ;
//            */
//
//            if (msStreamsState == StreamsScreenState.DEVICES_VIEW)
//            {
//                msPrevStreamsState = msPrevStreamsStateDevices ;
//            }
//            else
//            {
//                msPrevStreamsStateDevices = msStreamsState ;
//                msPrevStreamsState = msStreamsState ;
//                mPreviousFragment =  mCurrentFragment ;
//            }
//            msStreamsState =  StreamsScreenState.DEVICES_VIEW ;
//            msMenusInitialized = true ;
//            msDevicesClicked = !msDevicesClicked ;
//
//            mCurrentFragment = null ; //new DevicesFragment();
//            mStreamsDrawerListAdapter.notifyDataSetChanged() ;
//
//			break;

                case DRAWER_DEVICES_NOFRAGMENT:

                    if (msStreamsState == StreamsScreenState.DEVICES_VIEW)
                    {
                        msPrevStreamsState = msPrevStreamsStateDevices ;
                    }
                    else
                    {
                        msPrevStreamsStateDevices = msStreamsState ;
                        msPrevStreamsState = msStreamsState ;
                        mPreviousFragment =  mCurrentFragment ;
                    }
                    msStreamsState =  StreamsScreenState.DEVICES_VIEW ;
                    msMenusInitialized = true ;
                    msDevicesClicked = !msDevicesClicked ;

                    mCurrentFragment = null ; //new DevicesFragment();
                    mStreamsDrawerListAdapter.notifyDataSetChanged() ;

                    break;

                case DRAWER_DEVICES_GETMORE_FRAGMENT:
                    msPrevStreamsStateDevices = msStreamsState ;
                    msPrevStreamsState = msPrevStreamsStateDevices ;
                    msStreamsState =  StreamsScreenState.DEVICES_VIEW_DEVICES ;


                    mCurrentFragment = null ;
                    try {
                        //Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.plsound.com"));
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://streams.io"));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    break;

                case DRAWER_GLOWDECK_DEVICES0:

                    mCurrentGlowdeckIndex = 0;

                    getCurrentGlowdecks().setCurrentlySelected(mCurrentGlowdeckIndex);

                    if (StreamsApplication.DEBUG_MODE) {

                        Log.d("dbg","msPrevStreamsState=" + msPrevStreamsState.toString() + "msStreamsState=" + msStreamsState.toString() ) ;

                    }

                    if (msStreamsState != StreamsScreenState.DEVICES_VIEW_DEVICES) {

                        msPrevStreamsState  = msStreamsState;

                    }

                    msStreamsState = StreamsScreenState.DEVICES_VIEW_DEVICES;

                    if (BluetoothSppManager.isBTavailable() && !BluetoothSppManager.isBTturnedOn()) {

                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                        //noOp = true;

                        //break ;

                    }
                    else {

                        BluetoothSppManager bluetoothSppManager = mStreamsApplication.getBluetoothSppManager();

                        int countCurrentGlowdecks = msCurrentGlowdecks.getNumGlowdecksFound();

                        if (countCurrentGlowdecks == 0) {

                            Intent serverIntent = new Intent(this, BTDeviceListActivity.class);

                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

                            // return;

                        }
                        else {

                            mStreamSettingsOn = false;

                            // noOp = true;

                            if (!bluetoothSppManager.isConnected() && (!bluetoothSppManager.isConnecting(mCurrentGlowdeckIndex))) {

                                bluetoothSppManager.setConnecting(mCurrentGlowdeckIndex);

                                bluetoothSppManager.connectToGlowdeck(mCurrentGlowdeckIndex);

                                mStreamsDrawerListAdapter.notifyDataSetChanged();

                            }
                            else {

                                if (bluetoothSppManager.isConnected()) {

                                    if (!msCurrentGlowdecks.getCurrentlyConnected().isReceivedInit()) {

                                        // noOp = true;

                                        // bluetoothSppManager.sendMessage(CurrentGlowdecks.EQUALIZER_COMMAND);

                                        // break;

                                    }

                                    msStreamsState = StreamsScreenState.PICKER_VIEW;

                                    mCurrentFragment = new PickerFragment();

                                    position = DRAWER_SHOW_PICKER;

                                    // noOp = false;

                                    timeout = 100;

                                    AppConfig.setRSDpend(false);

                                    // break;

                                }

                            }

                        }

                    }

                    break;

                case DRAWER_PROFILE_FRAGMENT:
                    msStreamsState =  StreamsScreenState.PROFILE_VIEW ;
                    mCurrentFragment = new ProfileFragment();
                    timeout = 100 ; // because the screen contents require it

                    break;

                case DRAWER_ABOUT_FRAGMENT:
                    msStreamsState =  StreamsScreenState.ABOUT_VIEW ;
                    mCurrentFragment = new AboutFragment();
                    timeout = 100 ;
                    break;
                case DRAWER_SIGNOUT_FRAGMENT:

                    FormsPersistentData.clear() ;
                    LoginActivity.clearSavedStreamsUser(this) ;
                    StreamsApplication streamsApplication = (StreamsApplication)getApplication() ;
                    streamsApplication.reInitWeatherInfo() ;
                    msStreamsState =  StreamsScreenState.LOGIN_SCREEN ;

                    mStreamSettingsOn = true ; // restore to the "Streams" menu upon return
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPrefs.edit();

                    editor.putString(LoginActivity.PrefsUserid, LoginActivity.PrefsDefaultString);
                    editor.putString(LoginActivity.PrefsPassword, LoginActivity.PrefsDefaultString);
                    editor.commit();
                    msIsLoggedIn = false ;
                    StreamsFragment.cleanUpAdapter() ;

                    break;
                case DRAWER_START_PROVISION :

                    boolean bypassProvisioning = true ;

                    GlowdeckDevice glowdeckDevice = getCurrentGlowdecks().getCurrentlyConnected() ;

                    if (!bypassProvisioning)
                    {
                        AlertDialog.Builder provisionDialog = new AlertDialog.Builder(this);
                        TextView resultMessage = new TextView(this);
                        resultMessage.setTextSize(18);


                        String dialogMsgStr = null;

                        String fillInAllFieldsStr = 		"\nGlowdeck Setup\n" + glowdeckDevice.getName() + " Has not been configured.\n" + "Do you want to set it up now?" ;
                        dialogMsgStr = fillInAllFieldsStr ;



                        Spannable span = new SpannableString(dialogMsgStr);

                        span.setSpan(new RelativeSizeSpan(0.7f), 16, fillInAllFieldsStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        resultMessage.setText(span);
                        resultMessage.setGravity(Gravity.CENTER);
                        provisionDialog.setView(resultMessage);
                        provisionDialog.setCancelable(false) ;
                        provisionDialog.setPositiveButton(Html.fromHtml(
                                "<font  color=\"#0088ff\"><b>OK</></font>"),
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        displayView(DRAWER_SHOW_MAIN_PROVISION) ;

                                        dialog.dismiss();
                                    }
                                });

                        provisionDialog.setNegativeButton(Html.fromHtml(
                                "<font  color=\"#0088ff\"><b>Cancel</></font>"),
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        dialog.dismiss();

                                    }

                                });
                        provisionDialog.show() ;

                    }
                    else
                    {
                        displayView(DRAWER_SHOW_PICKER) ;
                    }

                    break ;

                case 	DRAWER_SHOW_MAIN_PROVISION:
                    msStreamsState =  StreamsScreenState.PROVISIONING_VIEW ;
                    mCurrentFragment = new ProvisionFragment();
                    mStreamSettingsOn = false ;
                    break;
                case 	DRAWER_SHOW_PICKER:
                    if ( (msCurrentGlowdecks == null) || (msCurrentGlowdecks.getCurrentlyConnected() == null) )
                    {
                        //noOp = true ;
                        //break ;
                    }
                    if (!msCurrentGlowdecks.getCurrentlyConnected().isReceivedInit())
                    {
                        //noOp = true ;
                        //break ;
                    }
                    msStreamsState =  StreamsScreenState.PICKER_VIEW ;
                    mCurrentFragment = new PickerFragment();
                    mStreamSettingsOn = false ;
                    timeout = 100 ;
                    break;

                default:
                    break ;
            }
            if (noOp)
            {
                return ;
            }
            if (mCurrentFragment != null) {


                final int finalTimeout = timeout ;
                final Fragment finalFragment = mCurrentFragment;
                final int finalPosition = position ;

                try{
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, finalFragment).commit();
                }catch(Exception e){e.printStackTrace();}
                try{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // update selected item and title, then close the drawer
                            if (mDrawerList != null)
                            {
                                try{
                                    mDrawerList.setItemChecked(finalPosition, true);
                                    mDrawerList.setSelection(finalPosition);
                                }catch(Exception e){e.printStackTrace();}
                            }
                            else
                            {
                                return ;
                            }


                            int updatedPosition = finalPosition ;
                            if (mStreamSettingsOn)
                            {
                                updatedPosition = 0 ;
                            }
                            String menuTitle = "" ;
                            if (updatedPosition >= DRAWER_START_PROVISION)
                            {
                                try{
                                    if (updatedPosition == DRAWER_SHOW_MAIN_PROVISION)
                                    {
                                        menuTitle = Register_New_Device  ;
                                    }
                                    if (updatedPosition == DRAWER_SHOW_PICKER)
                                    {
                                        menuTitle = Lights_Controller  ;
                                    }
                                }catch(Exception e){e.printStackTrace();}
                            }
                            else
                            {
                                menuTitle = mStreamsMenuTitles[updatedPosition].toUpperCase() ;
                            }
                            if (updatedPosition == DRAWER_GLOWDECK_DEVICES0 )
                            {
                                try{
                                    BluetoothSppManager bluetoothSppManager = mStreamsApplication.getBluetoothSppManager() ;
                                    if (bluetoothSppManager != null)
                                    {

                                    }
                                }catch(Exception e){e.printStackTrace();}
                            }

                            setTitle(menuTitle);

                            if (mFirstOpen == true)
                            {
                                mFirstOpen = false;
                            }else
                                mDrawerLayout.closeDrawer(mDrawerList);
                        }
                    }, finalTimeout);

                }catch(Exception e){e.printStackTrace();}

            } else {
                // error in creating mCurrentFragment
                // Log.e("MainActivity", "Error in creating mCurrentFragment");
            }
            if (msStreamsState == StreamsScreenState.LOGIN_SCREEN)
            {
                try{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent,  0);
                }catch(Exception e){e.printStackTrace();}
            }
        }catch(Exception e){e.printStackTrace();}
    }




    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     *
     *
     *  If called, this method will occur before onStop().
     *  There are no guarantees about whether it will occur before or after onPause().
     *
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        try{
            if (savedInstanceState != null)
            {
                if (msStreamsState != StreamsScreenState.STREAMS_VIEW)
                {
                    Integer state  =  (Integer) savedInstanceState.getSerializable(STREAMS_STATE);
                    if (state != null)
                    {
                        msStreamsState = intToStreamsState(state.intValue()) ;
                    }
                }

                Integer streamsType  =  (Integer) savedInstanceState.getSerializable(STREAMS_TYPE);
                if (streamsType != null)
                {
                    if (StreamsApplication.DEBUG_MODE)
                    {

                        Log.d("dbg","MainActivity::onRestoreInstanceState::msStreamsType=" + streamsType.intValue() ) ;
                    }
                    msStreamsType = intToStreamsType(streamsType.intValue()) ;
                }


            }
        }catch(Exception e){e.printStackTrace();}
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (StreamsApplication.DEBUG_MODE)
        {

            Log.d("dbg","MainActivity::onSaveInstanceState::msStreamsType=" + msStreamsType.ordinal()) ;
        }

        try{
            state.putSerializable(STREAMS_STATE, msStreamsState.ordinal());
            state.putSerializable(STREAMS_TYPE, msStreamsType.ordinal());
        }catch(Exception e){e.printStackTrace();}
    }



    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public  void onDestroy()
    {
        super.onDestroy() ;

        try{
            unregisterReceiver(this.mBatInfoReceiver);
            cleanUpGlobals() ;
            if (StreamsApplication.DEBUG_MODE)
            {

                Log.d("dbg","onDestroy in MainActivity") ;
            }
        }catch(Exception e){e.printStackTrace();}

    }
    @Override
    public void onDetachedFromWindow() {

        super.onDetachedFromWindow();
    }
    /* (non-Javadoc)
     * @see android.app.Activity#setTitle(java.lang.CharSequence)
     */
    @Override
    public void setTitle(CharSequence title) {
        try{
            mTitle = title;
            getActionBar().setTitle(mTitle);
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */



	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls

        try{
            mDrawerToggle.onConfigurationChanged(newConfig);
        }catch(Exception e){e.printStackTrace();}

    }


    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    public void  onResume()
    {
        msPaused = false ;
        super.onResume() ;
        try{
            switch (msStreamsState)
            {
                case STREAMS_VIEW :
                    displayView(DRAWER_STREAM_FRAGMENT) ;
                    break ;
                case LOGIN_SCREEN :
                case SPLASH_SCREEN :
                case STARTING :
                default :
                    break ;
            }
        }catch(Exception e){e.printStackTrace();}
    }




    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                try{
                    mStreamsDrawerListAdapter.notifyDataSetChanged() ;
                }catch(Exception e){e.printStackTrace();}
            }
            return ;
        }
        if (requestCode == REQUEST_CONNECT_DEVICE)
        {
            if (resultCode == RESULT_OK)
            {
                try{
                    String deviceAddress = intent.getExtras().getString(
                            BTDeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    String deviceName = intent.getExtras().getString(
                            BTDeviceListActivity.EXTRA_DEVICE_NAME);
                    StreamsApplication streamsApplication = (StreamsApplication)getApplication() ;
                }catch(Exception e){e.printStackTrace();}
            }
            return ;
        }


        if (resultCode == RESULT_OK)
        {
            msIsLoggedIn = true ;
            try{
                if (intent.hasExtra(LoginActivity.RETURN_STAT_LOGIN))
                {
                    String loginOrCreate = intent.getStringExtra(LoginActivity.RETURN_STAT_LOGIN);
                    if (loginOrCreate.compareTo(LoginActivity.CREATE_USER_SUCCESSFUL) == 0)
                    {
                        msStreamsState = StreamsScreenState.PROFILE_VIEW ;
                        displayView(DRAWER_PROFILE_FRAGMENT) ;
                    }

                    else
                    {
                        msStreamsState = StreamsScreenState.STREAMS_VIEW ;
                        displayView(DRAWER_STREAM_FRAGMENT) ;
                    }
                }
            }catch(Exception e){e.printStackTrace();}
        }
        else
        {
            try{
                displayView(DRAWER_SIGNOUT_FRAGMENT) ;
            }catch(Exception e){e.printStackTrace();}
        }

    }
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        try{
            if ( (msStreamsState == StreamsScreenState.DEVICES_VIEW) ||  (msStreamsState == StreamsScreenState.DEVICES_VIEW_DEVICES) )
            {
                if (mDrawerLayout.isDrawerOpen(mDrawerList))
                {

                    if (msPrevStreamsState == StreamsScreenState.DEVICES_VIEW)
                    {
                        try{
                            msPrevStreamsState = StreamsScreenState.STREAMS_VIEW ;
                        }catch(Exception e){e.printStackTrace();}
                    }

                    msStreamsState = msPrevStreamsState ;

                    if (msStreamsState == StreamsScreenState.STREAMS_VIEW)
                    {
                        mCurrentFragment  = mPreviousFragment  ;
                        mStreamSettingsOn = true  ;
                    }
                    else
                    {
                        if (msPrevStreamsState == StreamsScreenState.ABOUT_VIEW)
                        {
                            try{
                                displayView(DRAWER_ABOUT_FRAGMENT) ;
                                mStreamSettingsOn = false   ;
                            }catch(Exception e){e.printStackTrace();}
                        }
                    }
                }
                else
                if (msStreamsState == StreamsScreenState.DEVICES_VIEW_DEVICES)
                {
                    try{
                        msStreamsState = msPrevStreamsState ;

                        mDrawerLayout.openDrawer(mDrawerList);
                        invalidateOptionsMenu();
                    }catch(Exception e){e.printStackTrace();}
                    return ;
                }
            }

            if (msStreamsState == StreamsScreenState.STREAMS_VIEW)
            {
                if (mDrawerLayout.isDrawerOpen(mDrawerList))
                {
                    try{
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }catch(Exception e){e.printStackTrace();}

                }
                else
                {
                    try{
                        super.onBackPressed() ;
                    }catch(Exception e){e.printStackTrace();}
                }

            }
            else
            {
                if ((msStreamsState == StreamsScreenState.PROFILE_VIEW) && (msNextStreamsState == StreamsScreenState.STREAM_SETTINGS))
                {
                    try{
                        msStreamsState = StreamsScreenState.STREAM_SETTINGS ;
                        Intent intent = new Intent(MainActivity.this, StreamsSettingsActivity.class);
                        startActivityForResult(intent,  0);
                    }catch(Exception e){e.printStackTrace();}
                }
                else
                {
                    try{
                        if (!mDrawerLayout.isDrawerOpen(mDrawerList))
                        {
                            mDrawerLayout.openDrawer(mDrawerList);
                        }
                        else
                        {
                            mDrawerLayout.closeDrawer(mDrawerList);
                        }

                        invalidateOptionsMenu();
                    }catch(Exception e){e.printStackTrace();}
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * @return  true if signed in
     */
    boolean checkIfSignedIn()
    {

        try{
            if (! msIsLoggedIn )
            {
                // haven't logged in yet
                //  see if there are credentials stored in prefs
                // make sure LoginActivity has access to the application mContext
                LoginActivity.setStreamsApplication(mStreamsApplication) ;
                if  (   LoginActivity.checkIfCredentialsExist(this) &&
                        LoginActivity.recoverSavedStreamsUser(this)    )
                {
                    msIsLoggedIn = true ;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return msIsLoggedIn ;
    }
    public static StreamsType intToStreamsType(int value)
    {

        StreamsType retVal = null ;
        try{
            switch (value)
            {
                case 1 :
                    retVal =  StreamsType.STREAM_TYPE_SOCIAL ;
                    break ;
                case 2 :
                    retVal =  StreamsType.STREAM_TYPE_PUBLIC ;
                    break ;
                default:
                case 0 :
                    retVal =  StreamsType.STREAM_TYPE_PERSONAL ;
                    break ;

            }
        }catch(Exception e){e.printStackTrace();}
        return retVal ;
    }

`
    /**
     *
     */
    void cleanUpGlobals()
    {

        try{
            mCurrentFragment = null;
            mStreamsApplication = null;
            mDrawerToggle = null;
            mDrawerLayout = null ;
            mDrawerList = null ;

            // drawer title
            mDrawerTitle = null;

            mStreamsMenuTitles = null;
            mStreamsMenuIcons = null ;
            mStreamsDrawerItem = null;
            mStreamsDrawerListAdapter = null;
            // store the app title
            mTitle = null ;

        }catch(Exception e){e.printStackTrace();}
    }

}
