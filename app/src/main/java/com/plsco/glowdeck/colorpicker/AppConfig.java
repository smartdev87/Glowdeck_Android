package com.plsco.glowdeck.colorpicker;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.drawer.PickerFragment;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.auth.StreamsUser.StreamsAccount;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice.SSID_PAIR;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AppConfig extends PreferenceActivity {
	//final static String TAG = "AppConfig";
	static AlertDialog sWifiConnectDialog ;
	public static AlertDialog getWifiConnectDialog() {
		return sWifiConnectDialog;
	}

	public static void setWifiConnectDialog(AlertDialog wifiConnectDialog) {
		sWifiConnectDialog = wifiConnectDialog;
	}
	private static boolean sRSDpend ;
	public static boolean isRSDpend() {
		return sRSDpend;
	}

	public static void setRSDpend(boolean rSDpend) {
		sRSDpend = rSDpend;
	}
	static PrefsFragment mPrefsFragment ;
	static public PrefsFragment getmPrefsFragment() {
		return mPrefsFragment;
	}
	static boolean mWarningList ;
	static final String toastMsgNotConnected = "Not Connected.\nPlease return and connect.";
	static String network;
	static String password;
	static String wirelessCharger;
	static String smartCharging;
	static String autoBrightness;
	static String lightSystem;
	static String musicSync;
	static String musicInfo;
	static String equalizer;
	static String privacy;
	static AppConfig appconfig = null ;
	//static boolean SSIDListInitialized = false ;
	/*
	public static boolean isSSIDListInitialized() {
		return SSIDListInitialized;
	}
	 */
	public static AppConfig getAppconfig() {
		return appconfig;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "Restore Defaults");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
		appconfig = this;
		if (MainActivity.getMainActivity() == null )
		{
			//Log.d(TAG,"onCreate::mainActivity::return") ;

			try{
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish() ; 
			}catch(Exception e){e.printStackTrace();}
			return  ; 
		}
		mWarningList = false ; 
		
		password = appconfig.getResources().getString(R.string.WifiPassword);
		network = appconfig.getResources().getString(R.string.SSID);
		wirelessCharger = appconfig.getResources().getString(R.string.WirelessCharger);
		smartCharging = appconfig.getResources().getString(R.string.SmartCharging);
		autoBrightness = appconfig.getResources().getString(R.string.AutoBrightness);
		lightSystem = appconfig.getResources().getString(R.string.LightSystem);
		musicSync = appconfig.getResources().getString(R.string.MusicSync);
		musicInfo = appconfig.getResources().getString(R.string.ShowMusicInfo);
		equalizer = appconfig.getResources().getString(R.string.Equalizer);
		privacy = appconfig.getResources().getString(R.string.Privacy);
		String title = MainActivity.getMainActivity().getCurrentGlowdecks().getCurrentlyConnectedName();
		if (title.length() == 0) {
			title = "Glowdeck";
		}

		setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		setTheme(R.style.ThemePrefs);

		setTitle(title);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Display the fragment as the main content.
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
		mPrefsFragment = new PrefsFragment();
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();
		sWifiConnectDialog = null ; 
		}catch(Exception e){e.printStackTrace();}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			try{
			onBackPressed();
			}catch(Exception e){e.printStackTrace();}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() { // return to the calling mActivity

		super.onBackPressed();
		try{
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mPrefsFragment = null ; 
		appconfig = null ; 
		super.onDestroy();
	}

	public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			try{
			StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication() ;
			streamsApplication.getBluetoothSppManager().requestSSID() ;
			}catch(Exception e){e.printStackTrace();}
			super.onDestroy();
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

			try{
			if(  ( preference instanceof ListPreference) && preference.getKey().equals(getResources().getString(R.string.SSID)))
			{

				CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks() ;
				GlowdeckDevice glowdeckDevice = currentGlowdecks.getCurrentlySelected()  ;
				if (glowdeckDevice.isGlowdeckSSIDListChanged())
				{

					if (glowdeckDevice.getCurrentSSIDs() == null) // only do it once (if the list is empty)
					{

						int size = glowdeckDevice.copySSIDs();

						glowdeckDevice.setGlowdeckSSIDListChanged(false) ;
						int count = requestSSIDforNameList(glowdeckDevice) ; // update the settings list

					}
				}


			} 
			}catch(Exception e){e.printStackTrace();}
			return super.onPreferenceTreeClick(preferenceScreen, preference);


		}

		public PrefsFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub




			return super.onCreateView(inflater, container, savedInstanceState);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);

			if (MainActivity.getMainActivity() == null )
			{
				//Log.d(TAG,"PrefsFragment::onCreate::return") ;

				try{
				Intent intent = new Intent(this.getActivity(), MainActivity.class);
				startActivity(intent);
				this.getActivity().finish() ; 
				}catch(Exception e){e.printStackTrace();}
				return  ; 
			}




			try{
			addPreferencesFromResource(R.xml.preferences);

			resetPreferenceValue();
			resetElementValue();


			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks() ;
			GlowdeckDevice glowdeckDevice = currentGlowdecks.getCurrentlySelected()  ;
			if (glowdeckDevice.isGlowdeckSSIDListChanged())
			{ 
				glowdeckDevice.copySSIDs() ; // get latest list
				glowdeckDevice.setGlowdeckSSIDListChanged(false) ; // set no new data flag


			}
			StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication() ;

			}catch(Exception e){e.printStackTrace();}
		}
		void ssidNameListSelect( )
		{
			try{
			ListPreference target = (ListPreference)findPreference(network);
			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks() ;
			GlowdeckDevice glowdeckDevice = currentGlowdecks.getCurrentlySelected()  ;
			ArrayList<SSID_PAIR> ssidsFordevice = glowdeckDevice.getCurrentSSIDs() ;
			String targetName = null ; 
			if (ssidsFordevice != null)
			{
				String currentNetworkName = glowdeckDevice.getCurrentNetworkName() ; 
				//currentNetworkName = "U10C02236" ;

				for (SSID_PAIR ssidPair : ssidsFordevice)
				{
					if (ssidPair.getSSIDname().compareToIgnoreCase(currentNetworkName) == 0)
					{

						targetName = currentNetworkName  ;
						//if (ssidPair.isPasswordRequired())
						//	password required
						break ; 
					}
				}
			}
			target.setValue(targetName) ;
			}catch(Exception e){e.printStackTrace();}
		}
		int requestSSIDforNameList(GlowdeckDevice glowdeckDevice)
		{

			String prefsElement = getResources().getString(R.string.SSID)  ;
			ListPreference  target  = (ListPreference ) findPreference(prefsElement);
			int numEntries = 0 ; 
			try{
			ArrayList<SSID_PAIR> ssidsFordevice = glowdeckDevice.getCurrentSSIDs() ; 
			CharSequence[] entries ;
			CharSequence[] entryValues ;
			if (ssidsFordevice != null)
			{
				entries =  new  CharSequence[ssidsFordevice.size()]     ; 
				entryValues   = new  CharSequence[ssidsFordevice.size()]     ;

				for (int i = 0 ; i < ssidsFordevice.size() ; i ++)
				{
					SSID_PAIR ssidPair = ssidsFordevice.get(i) ;
					entryValues[i] = entries[i] = ssidPair.getSSIDname() ;
				}
				numEntries = ssidsFordevice.size() ;
				//target.setShouldDisableView(false) ;
				//target.setEnabled(true) ;

			}
			else
			{
				entries =  new  CharSequence[1]     ; 
				entryValues   = new  CharSequence[1]     ;
				entryValues[0] = entries[0] = getResources().getString(R.string.no_glowdeck_response) ;
				//target.setShouldDisableView(true) ;
				// target.setEnabled(false) ;
				target.setSummary(getResources().getString(R.string.no_glowdeck_response)) ;
				numEntries = 1 ; 
				mWarningList = true ;
			}
			target.setEntries( entries);
			target.setEntryValues( entryValues) ;
			//Dialog dlg = target.getDialog() ;
			//dlg.setTitle(getResources().getString(R.string.no_glowdeck_response)) ;
			ssidNameListSelect() ;
			}catch(Exception e){e.printStackTrace();}
			return numEntries ; 
		}

		@Override
		public void onResume() {
			super.onResume();
			try{
			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks() ;
			GlowdeckDevice glowdeckDevice = currentGlowdecks.getCurrentlySelected()  ;
			requestSSIDforNameList(glowdeckDevice) ; // update the settings list
			// now get updated list from glowdeck
			//StreamsApplication streamsApplication = (StreamsApplication)MainActivity.getMainActivity().getApplication() ;
			//streamsApplication.getBluetoothSppManager().requestSSID() ;
			//
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

			updateNetworkPassordTitleSummary(true) ; 
			}catch(Exception e){e.printStackTrace();}
		}

		@Override
		public void onPause() {
			try{
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			}catch(Exception e){e.printStackTrace();}
			super.onPause();
		}
		public void updateNetworkPassordTitleSummary(boolean initial)
		{
			try{
			Preference networkVal = findPreference(network);
			String networkChoice = networkVal.getSharedPreferences().getString(network,"");
			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks();
			networkChoice = currentGlowdecks.getCurrentNetworkSetting() ;
			if (networkChoice.toLowerCase().contains("glowdeck"))
			{
				networkChoice = "" ;
				initial = false ; 
			}
			if (networkChoice.length() == 0)
			{
				networkChoice = getResources().getString(R.string.network_summary) ; 
			}
			networkVal.setSummary(networkChoice) ;
			Preference networkPasswordVal = findPreference(password);
			String passwordReq = null ;

			if (!initial)
			{
				if ( (currentGlowdecks != null) && (currentGlowdecks.getCurrentlyConnected() != null) )
				{

					if (currentGlowdecks.getCurrentlyConnected().currentGlowdeckSSIDPassRequired())
					{
						passwordReq = CurrentGlowdecks.DISABLE_ENTER_PASSWORD ;
						networkPasswordVal.setEnabled(true) ;
					}
					else
					{
						passwordReq = CurrentGlowdecks.OPEN_NETWORK ;
						networkPasswordVal.setEnabled(false) ;
					}

					networkPasswordVal.setSummary(passwordReq) ;
				}

			}
			else
			{
				passwordReq = CurrentGlowdecks.DISABLE_ENTER_PASSWORD ;
				networkPasswordVal.setEnabled(true) ;
				networkPasswordVal.setSummary(passwordReq) ;
			}
			}catch(Exception e){e.printStackTrace();}
		}



		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","onSharedPreferenceChanged::top"  ) ;
			}
			try{
			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks();
			GlowdeckDevice glowdeckDevice = currentGlowdecks.getCurrentlySelected()  ;

			if (currentGlowdecks == null) {

				Toast.makeText(appconfig, toastMsgNotConnected, Toast.LENGTH_LONG).show();
				return;
			}


			if (key.equals(password)) {

				Preference passwordVal = findPreference(key);
				String passwordEdit = passwordVal.getSharedPreferences().getString(key,"");
				if (passwordEdit.length() > 0)
				{
					sendGlowdeckMessageString(password, passwordEdit, true);
					//new  SendDevicesStream().execute() ;
					AlertDialog.Builder builder = new AlertDialog.Builder(mPrefsFragment.getActivity());
					builder.setMessage("Connecting to " + currentGlowdecks.getCurrentNetworkSetting() + " ... please wait ...");
					builder.setTitle("Glowdeck WiFi") ;
					builder.setPositiveButton("OK", null) ;

					builder.setCancelable(false) ;
					sWifiConnectDialog = builder.show();
					sWifiConnectDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false) ;


				}
				EditTextPreference passwordEditText  =  (EditTextPreference)passwordVal  ;
				passwordEditText.setText("") ;
				return;
			}
			if (key.equals(network)) {


				Preference networkVal = findPreference(key);
				String networkChoice = networkVal.getSharedPreferences().getString(key,"");
				String noNetworks = getResources().getString(R.string.no_glowdeck_response) ;

				if (mWarningList)
				{

					mWarningList = false ;
					return ; 
				}
				if ( (networkChoice.length()== 0)     ||     (networkChoice.equals(noNetworks))  )
				{

					return ; 
				}
				else
				{

					currentGlowdecks.setCurrentNetworkSetting(networkChoice);
					String passwordReq = currentGlowdecks.getCurrentlyConnected().passwordRequired() ;
					boolean startSpinner = false ; 
					boolean sendUUID = false ; 
					if (passwordReq.equals(CurrentGlowdecks.PASSWORD_REQUIRED))
					{
						currentGlowdecks.setCurrentPasswordSetting(CurrentGlowdecks.on) ;

					}
					else
					{
						currentGlowdecks.setCurrentPasswordSetting(CurrentGlowdecks.off) ;

						startSpinner = true ;
						sendUUID = true ; 
					}
					sendGlowdeckMessageString(network, networkChoice, sendUUID);
					updateNetworkPassordTitleSummary(false) ; 
					networkVal.setTitle(getResources().getString(R.string.ssidFeature_undetermined)) ;

					if (startSpinner)
					{
					 
							AlertDialog.Builder builder = new AlertDialog.Builder(mPrefsFragment.getActivity());
							builder.setMessage("Connecting to " + networkChoice + " ... please wait");
							builder.setTitle("Glowdeck WiFi") ;
							builder.setPositiveButton("", null) ;
							builder.setCancelable(false) ;
							sWifiConnectDialog = builder.show();
							sWifiConnectDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false) ;
						 

					}
				}


				glowdeckDevice.copySSIDs() ; // get latest list
				glowdeckDevice.setGlowdeckSSIDListChanged(false) ; // set no new data flag
				requestSSIDforNameList(glowdeckDevice) ;
				/*
				if (glowdeckDevice.isGlowdeckSSIDListChanged())
				{
					glowdeckDevice.copySSIDs() ; // get latest list
					glowdeckDevice.setGlowdeckSSIDListChanged(false) ; // set no new data flag
					requestSSIDforNameList(glowdeckDevice) ;
				}
				else
				{

				}
				 */
				return;
			}
			if (key.equals(wirelessCharger)) {

				Preference wirelessChargerVal = findPreference(key);
				Boolean wirelessChargerOnOff = wirelessChargerVal.getSharedPreferences().getBoolean(key, true);

				currentGlowdecks.setCurrentWirelessChargerSetting(wirelessChargerOnOff);
				sendGlowdeckMessageBool(wirelessCharger, wirelessChargerOnOff);
				return;
			}
			if (key.equals(smartCharging)) {

				Preference smartChargingVal = findPreference(key);
				Boolean smartChargingOnOff = smartChargingVal.getSharedPreferences().getBoolean(key, true);

				currentGlowdecks.setCurrentSmartChargingSetting(smartChargingOnOff);
				sendGlowdeckMessageBool(smartCharging, smartChargingOnOff);
				return;
			}
			if (key.equals(autoBrightness)) {

				Preference autoBrightnessVal = findPreference(key);
				Boolean autoBrightnessOnOff = autoBrightnessVal.getSharedPreferences().getBoolean(key, true);

				currentGlowdecks.setCurrentAutoBrightnessSetting(autoBrightnessOnOff);
				sendGlowdeckMessageBool(autoBrightness, autoBrightnessOnOff);
				return;
			}
			if (key.equals(lightSystem)) {

				Preference lightSystemVal = findPreference(key);
				Boolean lightSystemOnOff = lightSystemVal.getSharedPreferences().getBoolean(key, true);

				currentGlowdecks.setCurrentLightSystemSetting(lightSystemOnOff);
				sendGlowdeckMessageBool(lightSystem, lightSystemOnOff);
				if (!lightSystemOnOff)
				{
					MainActivity.getMainActivity().getCurrentGlowdecks().updateCurrentGlowdeckColor(PickerFragment.getRGBStringColors(0)) ;

					ColorPicker colorPicker = PickerFragment.getPicker() ;
					if (colorPicker != null)
					{
						/*
						colorPicker.mColorGlowdeck = 0 ; 
						//colorPicker.getmSVbar().initColor() ;
						//colorPicker.getmSVbar().invalidate() ;
						colorPicker.setOldCenterColor(0) ;
						colorPicker.getmOpacityBar().initColor() ;
						colorPicker.getmOpacityBar().invalidate() ;
						 */
					}
				}
				return;
			}
			if (key.equals(musicSync)) {

				Preference musicSyncVal = findPreference(key);
				Boolean musicOnOff = musicSyncVal.getSharedPreferences().getBoolean(key, true);

				currentGlowdecks.setCurrentMusicSyncSetting(musicOnOff);
				sendGlowdeckMessageBool(musicSync, musicOnOff);
				return;
			}
			if (key.equals(musicInfo)) {

				Preference musicInfoVal = findPreference(key);
				Boolean musicInfoOnOff = musicInfoVal.getSharedPreferences().getBoolean(key, true);

				currentGlowdecks.setCurrentMusicSyncSetting(musicInfoOnOff);
				sendGlowdeckMessageBool(musicInfo, musicInfoOnOff);
				return;
			}
			if (key.equals(equalizer)) {

				Preference equalizerVal = findPreference(key);
				String equalizerChoice = equalizerVal.getSharedPreferences().getString(key,"0");
				equalizerVal.setSummary( getEqualizerSetting( )) ;
				currentGlowdecks.setCurrentEqualizerSetting(equalizerChoice);
				sendGlowdeckMessageString(equalizer, equalizerChoice);
				return;
			}
			if (key.equals(privacy)) {

				Preference privacyVal = findPreference(key);
				String privacyChoice =privacyVal.getSharedPreferences().getString(key,"0");
				privacyVal.setSummary( getPrivacySetting( )) ;
				currentGlowdecks.setCurrentPrivacySetting(privacyChoice);
				sendGlowdeckMessageString(privacy, privacyChoice);
				return;
			}
			}catch(Exception e){e.printStackTrace();}
		}
		String getEqualizerSetting()
		{
			Preference equalizerVal = findPreference(equalizer);
			String equalizerChoice =equalizerVal.getSharedPreferences().getString(equalizer,"0");
			String [] arrayVals = getResources().getStringArray(R.array.equalizerOptions) ;
			String summary = getResources().getString(R.string.customize_sound) ;
			try{
			for (int i = 0 ; i < arrayVals.length ; i++)
			{
				if (equalizerChoice.equals(Integer.toString(i)))
				{
					summary = arrayVals[i]  ;
					break ; 
				}
			}

			}catch(Exception e){e.printStackTrace();}
			return summary ; 
		}
		String getPrivacySetting()
		{
			Preference privacyVal = findPreference(privacy);
			String privacyChoice =privacyVal.getSharedPreferences().getString(privacy,"0");
			String [] arrayVals = getResources().getStringArray(R.array.privacyOptions) ;
			String summary = getResources().getString(R.string.streams_disp_policy) ;
			try{
			for (int i = 0 ; i < arrayVals.length ; i++)
			{
				if (privacyChoice.equals(Integer.toString(i)))
				{
					summary = arrayVals[i] + "\n" ;
					break ; 
				}
			}

			}catch(Exception e){e.printStackTrace();}
			return summary ; 
		}
		static public void sendGlowdeckMessageBool(String key, boolean value) {
			try{
			String message = "";
			String valueString = (value) ? "1" : "0";
			while (true) {
				if (key.equals(wirelessCharger)) {
					message = "CS:" + valueString + "^";
					break;
				}
				if (key.equals(smartCharging)) {
					message = "CS:" + valueString + "^";
					break;
				}
				if (key.equals(autoBrightness)) {
					message = "LS:" + valueString + "^";
					break;
				}
				if (key.equals(lightSystem)) {
					message = "MB:" + valueString + "^";
					break;
				}
				if (key.equals(musicSync)) {
					message = "MS:" + valueString + "^";
					break;
				}
				if (key.equals(musicInfo)) {
					message = "SM:" + valueString + "^";
					break;
				}
				break ;
			}
			if (message.length() > 0) {
				StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity()
						.getApplication();
				streamsApplication.getBluetoothSppManager().sendMessage(message);
			}
			}catch(Exception e){e.printStackTrace();}
		}
		void sendGlowdeckMessageString(String key, String value) 
		{
			try{
			sendGlowdeckMessageString( key,  value,false)  ;
			}catch(Exception e){e.printStackTrace();}
		}
		void sendGlowdeckMessageString(String key, String value, boolean uuidSend) {
			try{
			String message = "";
			boolean sendUUID = false ; 
			while (true) {
				if (key.equals(equalizer)) {
					message = "EQZ:" + value + "^";


					break;
				}
				if (key.equals(privacy)) {
					message = "PV:" + value + "^";
					break;
				}
				if (key.equals(network)) {
					message = "WI:" + value + "^";
					sendUUID = uuidSend ; 
					break;
				}
				if (key.equals(password)) {
					message = "WP:" + value + "^";
					sendUUID = uuidSend ; 
					break;
				}
				break ;

			}
			if (message.length() > 0) {
				StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity()
						.getApplication();
				streamsApplication.getBluetoothSppManager().sendMessage(message);
				if (sendUUID)
				{
					String userId = null ; 
					StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
					if (streamsUser != null)
					{
						StreamsAccount streamsAccount = streamsUser.getStreamsAccount() ;
						if (streamsAccount != null)
						{
							userId = streamsAccount.getID() ;
							//token  = streamsAccount.getToken() ;
							message = "UID:" + userId + "^" ; 
							streamsApplication.getBluetoothSppManager().sendMessage(message);
						}
					}





					//StreamsUserLogin streamsUserLogin = VerifyCredentialsTask.getStreamsUserLogin() ;
					//message = "UID:" + streamsUserLogin.getStreamsAccount().getID() + "^" ;  

				}
			}
			}catch(Exception e){e.printStackTrace();}
		}


		private void resetPreferenceValue() {
			if (appconfig == null)
			{
				return ; 
			}
			try{
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appconfig
					.getApplicationContext());
			SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
			prefEditor.putString("your_edit_text_pref_key", "DEFAULT-VALUE"); // set your default value  here (could be empty as well)
			prefEditor.commit(); // finally save changes
			// Now we have updated shared preference value, but in activity it still hold the old value
			}catch(Exception e){e.printStackTrace();}
			this.resetElementValue();
		}

		public void resetElementValue() {

			try{
			CurrentGlowdecks glowdecks = MainActivity.getMainActivity().getCurrentGlowdecks();
			if (glowdecks == null) {

				Toast.makeText(appconfig, toastMsgNotConnected, Toast.LENGTH_LONG).show();
				return;
			}

			boolean val ; 
			String  valStr ; 
			SwitchPreference myPrefText ;
			EditTextPreference myPrefEditText ;
			ListPreference myPrefStr ;
			ListPreference   myPrefTextList ;
			String prefKey = getResources().getString(R.string.SSID_Category) ;
			PreferenceCategory prefCat=(PreferenceCategory)findPreference(prefKey);
			if (glowdecks.getCurrentProvisioningSetting() )
			{

				prefCat.setTitle(getResources().getString(R.string.wifi_settings_provisioned)) ;
			}
			else
			{
				prefCat.setTitle(getResources().getString(R.string.wifi_settings_unprovisioned)) ;
			}


			valStr = glowdecks.getCurrentNetworkSetting() ; 
			myPrefStr = (ListPreference) super.findPreference(network);
			if (glowdecks.getCurrentNetConnectedSetting() )
			{

				myPrefStr.setTitle(getResources().getString(R.string.ssidFeature_connected)) ;
			}
			else
			{
				myPrefStr.setTitle(getResources().getString(R.string.ssidFeature_not_connected)) ;
			}



			if (valStr.equals("0"))
			{
				valStr = getResources().getString(R.string.my_router) ;
				glowdecks.setCurrentNetworkSetting(valStr) ;
			}
			myPrefStr.setSummary(valStr) ;


			myPrefEditText = (EditTextPreference) super.findPreference(password);
			if (!glowdecks.getCurrentNetworkSetting().equals(getResources().getString(R.string.my_router)))
			{
				myPrefEditText.setText("") ;
				myPrefEditText.setSummary("") ;
			}
			val = glowdecks.getCurrentWirelessChargerSetting();
			myPrefText = (SwitchPreference) super.findPreference(wirelessCharger);
			myPrefText.setChecked(val);

			val = glowdecks.getCurrentSmartChargingSetting();
			myPrefText = (SwitchPreference) super.findPreference(smartCharging);
			myPrefText.setChecked(val);

			val = glowdecks.getCurrentAutoBrightnessSetting();
			myPrefText = (SwitchPreference) super.findPreference(autoBrightness);
			myPrefText.setChecked(val);

			val = glowdecks.getCurrentLightSystemSetting();
			myPrefText = (SwitchPreference) super.findPreference(lightSystem);
			myPrefText.setChecked(val); 


			val = glowdecks.getCurrentMusicSyncSetting();
			myPrefText = (SwitchPreference) super.findPreference(musicSync);
			myPrefText.setChecked(val); 


			val = glowdecks.getCurrentMusicInfoSetting();
			myPrefText = (SwitchPreference) super.findPreference(musicInfo);
			myPrefText.setChecked(val); 

			valStr = glowdecks.getCurrentEqualizerSetting();
			myPrefTextList = (ListPreference) super.findPreference(equalizer);
			myPrefTextList.setValue(valStr);
			myPrefTextList.setSummary(getEqualizerSetting()) ;

			valStr = glowdecks.getCurrentPrivacySetting();
			myPrefTextList = (ListPreference) super.findPreference(privacy);
			myPrefTextList.setValue(valStr);
			myPrefTextList.setSummary(getPrivacySetting()) ;
			}catch(Exception e){e.printStackTrace();}
		}



	}

	public static class SendDevicesStream extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			try{
			HttpClient httpclient = new DefaultHttpClient();
			final String setStreamsDevice =    "http://glowdeck.com/api/devicestream/pd/?U=" ; //957&D=15FFC ;
			//final String setStreamsDevice =    "http://streams.io/api/devicestream/pd/?U=" ; //957&D=15FFC ;
			final String deviceSub = "&D="	 ;
			StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
			StreamsAccount streamsAccount = streamsUser.getStreamsAccount() ;

			String		userId = streamsAccount.getID() ;		

			CurrentGlowdecks glowdecks = MainActivity.getMainActivity().getCurrentGlowdecks();
			String macSuffic = glowdecks.getCurrentlySelected().getCurrentMacSuffix()	;

			String deviceStream = setStreamsDevice + userId	+ deviceSub + macSuffic ;	
			HttpPost httppost = new HttpPost(deviceStream);

			try {
				HttpResponse httpResponse = httpclient.execute(httppost);
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {

					builder.append(line).append("\n");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			}catch(Exception e){e.printStackTrace();}
			return null;
		}

	}
	/*
	public static class ProvisionWifi extends AsyncTask<String, Integer, Long> {

		ProgressDialog mProgressDialog ;


		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onCancelled(Long result) {
			// TODO Auto-generated method stub
			//Toast.makeText(context, "Glowdeck Firmware\nUpdate has been canceled.", Toast.LENGTH_LONG).show();

			MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			//mProcessingClick = false ; 
			mProgressDialog.dismiss() ;
			super.onCancelled(result);
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			//mProcessingClick = false ; 
			mProgressDialog.dismiss() ;
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			String ssidName = "Waiting for Glowdeck to connect to " ;
			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks();
			GlowdeckDevice glowdeckDevice = currentGlowdecks.getCurrentlySelected()  ;
			if (glowdeckDevice != null)
			{
				ssidName += currentGlowdecks.getCurrentNetworkSetting() ;
			}
			else
			{
				ssidName += "network" ;
			}
			mProgressDialog  = ProgressDialog.show(MainActivity.getMainActivity(), "Connect Glowdeck Wifi", ssidName, true, false) ;



			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			int currentBytes = values[0] ;

			if (currentBytes == 0)
			{
				mProgressDialog.setMessage("Starting firmware upload to Glowdeck ...") ;
			}
			else
			{
				int totalBytes   = values[1] ;
				int percent = currentBytes*100/totalBytes ;

				if ( (percent > 0 ) && (percent % 10) == 0)
				{
					mProgressDialog.setMessage("Uploading to Glowdeck - " + percent + "% completed") ;
				}
			}
			super.onProgressUpdate(values);
		}

		@Override
		protected Long doInBackground(String... params) {
			mProgressDialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog){
					cancel(true) ; 
				}});
			mProgressDialog.setOnKeyListener( new DialogInterface.OnKeyListener() {
				public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
					switch(arg1) {
					case KeyEvent.KEYCODE_BACK:
						cancel(true) ; 
						return true;

					default:
						break;
					}
					return true;
				}
			});        

			return null;

		}

	}
	 */
}
