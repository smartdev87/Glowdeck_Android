package com.plsco.glowdeck.glowdeck;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.ParcelUuid;
import android.util.Log;

import com.plsco.glowdeck.bluetooth.BluetoothSppManager;
import com.plsco.glowdeck.drawer.PickerFragment;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CurrentGlowdecks {
	public static  final String PASSWORD_REQUIRED = "Password required" ;
	public static  final String DISABLE_ENTER_PASSWORD = "*****" ;
	public static  final String OPEN_NETWORK =  "Open network" ;
	public static final String on = "1" ;
	public static final String off= "0" ;
	public static int currentGlowdeckIndx = 0 ; 
	public static final String    INIT_COMMAND = "INIT^" ;
	public static final String    INIT_RESPONSE = "IN:" ;
	public static final String    RSD_COMMAND = "RSD^" ;
	public static final String    RSD_RESPONSE = "SSID:" ;
	public static final String    DL_RESPONSE = "DBR:" ;
	public static final String    LS_RESPONSE = "DBA:" ;
	public static final String    BA_RESPONSE = "BAT" ;
	public static final String    RSD_RESPONSE_DONE = "SSID:_DONE_" ;
	public static final String    EQUALIZER_COMMAND = "EQZ^" ;
	public static final String    WIFI_CONNECTED_COMMAND = "WIO^" ;
	public static final String    WEATHER_REQUEST = "WTR^" ;
	public static final String    SOCIAL_REQUEST = "SOC^" ;
	public static final String    PERSONAL_REQUEST = "PER^" ;
	public static final String    INIT_RESPONSE_ID = "ID:" ;
	public static final String    INIT_RESPONSE_CL = "CL:" ;
	public static final String    INIT_RESPONSE_CS = "CHG:" ;
	//public static final String    INIT_RESPONSE_LT = "LT:" ;
	public static final String    INIT_RESPONSE_COL = "COL:" ;
	//public static final String    INIT_RESPONSE_EQZ = "EQZ:" ;
	public static final String    INIT_RESPONSE_WIO = "WIO:" ;
	public static final String    WIK_NETWORK_CONNECT_MSG = "WIC:" ;
	public static final String    INIT_RESPONSE_DL = "DBR:" ;
	public static final String    INIT_RESPONSE_LS = "DBA:" ;
	public static final String    INIT_RESPONSE_MB = "MB:" ;
	//public static final String    INIT_RESPONSE_LC = "LC:" ;
	public static final String    INIT_RESPONSE_MS = "MSA:" ;
	public static final String    INIT_RESPONSE_PB = "PB:" ;
	public static final String    INIT_RESPONSE_PV = "PV:" ;
	public static final String    INIT_RESPONSE_QI = "QI:" ;
	public static final String    INIT_RESPONSE_SM = "MUS:" ;
	public static final String    INIT_RESPONSE_ST = "STR:" ;
	public static final String    INIT_RESPONSE_SX = "SX:" ;
	public static final String    INIT_RESPONSE_UI = "UI:" ;
	public static final String    INIT_RESPONSE_WE = "WE:" ;
	public static final String    INIT_RESPONSE_WI = "WI:" ;
	public static final String    INIT_RESPONSE_WP = "WP:" ;
	public static final String	  INIT_RESPONSE_EQZ = "EQZ:" ;
	public static final String    GLOWDECK_TIME_REQUEST     = "TIM^" ;
	public static final String    TIME_COMMAND_PREFIX     = "TIM:" ;

	/*
	public static final String    INIT_RESPONSE_DB = "DB:" ;
	public static final String    INIT_RESPONSE_DA = "DA:" ;
	public static final String    INIT_RESPONSE_WF = "WF:" ;

	public static final String    INIT_RESPONSE_FP = "FP:" ;
	public static final String    INIT_RESPONSE_FH = "FH:" ;
	public static final String    INIT_RESPONSE_LP = "LP:" ;
	public static final String    INIT_RESPONSE_LH = "LH:" ;
	public static final String    INIT_RESPONSE_RP = "RP:" ;
	public static final String    INIT_RESPONSE_RH = "RH:" ;

	 */


	public static final String    INIT_RESPONSE_NM = "NM:" ;


	public enum GLOWDECK_COMMANDS { INIT }

	private final static String 										GLOWDECK = "GLOWDECK" ;
	private final static String 										BLUECREATION = "BLUECREATION-0" ;
	private final static int 											NUM_TRAIL_HEXCHARS = 5 ; 
	BluetoothSppManager mBluetoothSppManager ;
	static HashMap<String, GlowdeckDevice> mListGlowdecks = null ;

	public HashMap<String, GlowdeckDevice> getmListGlowdecks() {
		return mListGlowdecks;
	}



	public class GlowdeckSetting
	{
		String gsIN ; // Provisioned status

		String gsID ; // UDID
		String gsCL ; // Wireless charger
		String gsCS ; // Smart charging
		//String gsLT ; //
		String gsCOL ;  // COLOR
		String gsWIO ;  // Wifi connected
		String gsDL ;  // display brightness
		//String gsLC ;   
		String gsLS ;  // Switches auto-brightness  
		String gsMB ;  // Light master switch 
		String gsMS ;  //  Music sync lights
		String gsPB ;  //  Toggle night mode
		String gsPV ;  // Privacy mode
		String gsQI ;  // Wireless charging
		String gsSM ;  // show music stream
		String gsST ;  // streams on off
		String gsSX ; // Scroll stream text
		String gsUI ; // ????
		String gsWE ; // Wifi Enabled
		String gsWI ; // Wifi SSID currently connected
		String gsWP ; // Wifi password
		String gsEQZ ;  // Equalizer
		/*
		String gsDB ;
		String gsDA ; 
		String gsWF ; 

		String gsFP ;
		String gsFH ;
		String gsLP ;
		String gsLH ; 
		String gsRP ; 
		String gsRH ; 




		String gsNM ; 
		 */
		public GlowdeckSetting()
		{
			gsIN = "" ; 
			gsID = ""  ;
			gsCL = ""  ;
			gsCS = ""  ;
			gsCOL = ""  ;
			gsDL = ""  ;
			gsLS = ""  ;
			gsMB = ""  ;
			gsMS = ""  ;
			gsPB = ""  ;
			gsPV = ""  ;
			gsWIO = ""  ;
			gsQI = ""  ;
			gsSM = ""  ; 
			gsST = ""  ;
			gsSX = ""  ; 
			gsUI = "" ; 
			gsWE = "" ; 
			gsWI = ""  ;
			gsWP = ""  ;
			gsEQZ = ""  ;
			//			gsLT = ""  ;
			//	gsLC = ""  ;
			/*
			gsDB = ""  ;
			gsDA = ""  ;
			gsWF = ""  ;

			gsFP = ""  ;
			gsFH = ""  ;
			gsLP = ""  ;
			gsLH = ""  ;
			gsRP = ""  ;
			gsRH = ""  ;




			gsNM = ""  ;
			 */
		}
	}
	public class GlowdeckDevice 
	{
		public  class SSID_PAIR {
			String SSIDname ;
			public String getSSIDname() {
				return SSIDname;
			}
			boolean passwordRequired ;
			public boolean isPasswordRequired() {
				return passwordRequired;
			} 
		}
		String name ; 

		int btType ; 
		boolean lastConnected ;
		boolean connected     ;
		boolean valid ; 
		boolean isConnecting  ; 
		ArrayList<SSID_PAIR> glowdeckSSIDList ; 
		ArrayList<SSID_PAIR> settingsSSIDList ;
		boolean  glowdeckSSIDListChanged ; 
		public boolean isGlowdeckSSIDListChanged() {
			synchronized (this) {
				return glowdeckSSIDListChanged;
			}
		}
		public void setGlowdeckSSIDListChanged(boolean newSSIDList) {
			synchronized (this) {
				this.glowdeckSSIDListChanged = newSSIDList;
			}

		}
		public ArrayList<SSID_PAIR> getCurrentSSIDs() {
			return settingsSSIDList;
		}
		boolean msgInprogress ; 
		boolean receivedInit  ; 
		int    menuPosition      ;

		GLOWDECK_COMMANDS currentRequest ;
		boolean  currentlySelected ; 

		boolean initialized ; 
		String address ; 
		GlowdeckSetting glowdeckSettings ;
		ParcelUuid[] pUuids   ;
		int bondState ; 
		public GlowdeckDevice(int btType, String name, ParcelUuid[] pUuids, String address, int bondState, int drawerMenuPosition )
		{
			try{
			this.btType = btType ; 
			this.name = name ; 
			lastConnected = false ; 
			connected = false ; 
			isConnecting = false ; 
			msgInprogress = false ; 
			this.address = address ;
			this.pUuids= pUuids ; 
			this.bondState = bondState ; 
			this.valid = true ;
			//reconnectOnRestart = false ; 
			receivedInit = false ; 
			currentlySelected = false ; 
			menuPosition = drawerMenuPosition ;
			glowdeckSettings = new GlowdeckSetting()  ;
			glowdeckSSIDListChanged = false ; 
			settingsSSIDList = null ; 
			}catch(Exception e){e.printStackTrace();}
		}
		public int  copySSIDs()
		{
			int retVal = 0 ; 
			try{
			if (glowdeckSSIDList != null)
			{
				settingsSSIDList = new  ArrayList<SSID_PAIR>(glowdeckSSIDList) ; 
				retVal = glowdeckSSIDList.size() ;
			}
			}catch(Exception e){e.printStackTrace();}
			return retVal ; 
		}
		public boolean currentGlowdeckSSIDPassRequired()
		{
			boolean retVal = false ;
			try{
			if (glowdeckSettings.gsWP.equals(on))
			{
				retVal = true ;
			}
			}catch(Exception e){e.printStackTrace();}
			return retVal ; 
			
		}
		/*
		public String passwordRequiredFromCurrentGlowdeck()
		{
			String retVal = "" ; 
			if (isConnected())
			{

				if (glowdeckSettings.gsWP.length() > 0 )
				{
					if (glowdeckSettings.gsWI.length() > 0 )
						if (glowdeckSettings.gsWI.equals(MainActivity.getMainActivity().getResources().getString(R.string.my_router)))
						{
							return MainActivity.getMainActivity().getResources().getString(R.string.opt_net_password) ;
						}
					// currently not working properly so just make it blank
					//boolean notWorking = true ;
					//// (notWorking)
					///{
						//return DISABLE_ENTER_PASSWORD ; 
					//}
					if (glowdeckSettings.gsWP.equals(on))
					{
						retVal = PASSWORD_REQUIRED ;
					}
					else
					{
						retVal = OPEN_NETWORK ;

					}
				}
			}

			return retVal ; 
		}
		*/
		public String passwordRequired()
		{
			String retVal  = null ; 
			try{
			if ((isConnected()) &&  (settingsSSIDList != null) )
			{
				for (SSID_PAIR ssidPair : settingsSSIDList)
				{

					if (ssidPair.getSSIDname().compareToIgnoreCase(glowdeckSettings.gsWI) == 0 )
					{
						// 
						if (ssidPair.passwordRequired)
						{
							retVal = PASSWORD_REQUIRED ;
						}
						else
						{
							retVal = OPEN_NETWORK ;
						}
						break ; 
					}
				}
			}




			}catch(Exception e){e.printStackTrace();}
			return retVal ; 
		}
		public boolean isReceivedInit() {
			return receivedInit;
		}
		public void setReceivedInit(boolean receivedInit) {
			this.receivedInit = receivedInit;
		}
		public boolean isCurrentlySelected() {
			return currentlySelected;
		}
		public void setCurrentlySelected(boolean currentlySelected) {
			this.currentlySelected = currentlySelected;
		}
		public boolean isMsgInprogress() {
			return msgInprogress;
		}
		public void setMsgInprogress(boolean msgInprogress) {
			this.msgInprogress = msgInprogress;
		}
		public GLOWDECK_COMMANDS getCurrentRequest() {
			return currentRequest;
		}

		public void setCurrentRequest(GLOWDECK_COMMANDS currentRequest) {
			this.currentRequest = currentRequest;
		}
		public boolean isConnecting() {
			return isConnecting;
		}
		public void setConnected(boolean connected) {
			this.connected = connected;
		}
		public void setConnecting(boolean isConnecting) {
			this.isConnecting = isConnecting;
		}
		public boolean isConnected() {
			return connected;
		}
		public boolean isBTClassic()
		{
			return (btType == BluetoothDevice.DEVICE_TYPE_DUAL) ||
					(btType == BluetoothDevice.DEVICE_TYPE_CLASSIC);
		}
		public boolean isBTBle()
		{
			return (btType == BluetoothDevice.DEVICE_TYPE_DUAL) ||
					(btType == BluetoothDevice.DEVICE_TYPE_LE);
		}
		public boolean isBonded()
		{
			return bondState == BluetoothDevice.BOND_BONDED;

		}
		public String getName() {
			return name;
		}
		public String getAddress() {
			return address;
		}
		public boolean isValid() {
			return valid;
		}
		public int getCurrentDisplayBrightness()
		{
			return Integer.parseInt(glowdeckSettings.gsDL);
		}
		public String getCurrentMacSuffix()
		{
			return glowdeckSettings.gsID; 
		}
		public String getCurrentNetworkName()
		{
			return glowdeckSettings.gsWI ; 
		}
		public void setCurrentDisplayBrightness(int brightnessValue)
		{

			glowdeckSettings.gsDL = Integer.toString(brightnessValue) ;
		}
		public boolean getCurrentDisplayStreamsSwitch()
		{
			return glowdeckSettings.gsST.equals("1");
		}
		public void setCurrentDisplayStreamsSwitch(boolean streamsSwitch)
		{
			if (streamsSwitch)
			{
				glowdeckSettings.gsST = "1"  ;
			}
			else
			{
				glowdeckSettings.gsST = "0"  ;
			}
		}
		public void processDL(String dlMsg)
		{
			try{
			int startIndex = DL_RESPONSE.length() ;
			int endIndex   = dlMsg.indexOf('^') ;
			if (endIndex > startIndex)
			{
				glowdeckSettings.gsDL = dlMsg.substring(startIndex, endIndex ) ;
				int progress = getCurrentDisplayBrightness() * 10 ;
				PickerFragment.setLightsControl(progress) ;


			}
			}catch(Exception e){e.printStackTrace();}

		}
		public void processLS(String lsMsg)
		{

			try{
			glowdeckSettings.gsLS = lsMsg.substring(LS_RESPONSE.length(), LS_RESPONSE.length() +1 ) ;
			}catch(Exception e){e.printStackTrace();}

			//boolean isChecked = getCurrentDisplayNightSwitch()   ;
			//PickerFragment.setNightSwitch(isChecked) ;
		}
		public void processRSD(String rsdMsg) 
		{

			try{
			rsdMsg = rsdMsg.replace("\n","").replace(" ","");

			String[] rsdMessages = rsdMsg.split("\\^") ;
			for (String msg : rsdMessages)
			{

				if (msg.startsWith(CurrentGlowdecks.RSD_RESPONSE_DONE))
				{

					glowdeckSSIDListChanged = true ; 


				}
				if (msg.startsWith(CurrentGlowdecks.RSD_RESPONSE))
				{
					String[] rsdFields = msg.split(":") ;
					if (rsdFields.length < 4)
					{
						break  ; 
					}
					if (rsdFields[1].equals("0") )
					{
						glowdeckSSIDList = new ArrayList<SSID_PAIR>(100) ; 
					}

					SSID_PAIR ssidPair = new SSID_PAIR() ; 
					ssidPair.SSIDname = rsdFields[2] ;
					String boolVal = rsdFields[3].substring(0, 1) ;
					ssidPair.passwordRequired = Integer.parseInt(boolVal) != 0;
					glowdeckSSIDList.add(ssidPair) ;
				}
			}
			}catch(Exception e){e.printStackTrace();}
		}

		public void fillInit(String theInitMsg) 
		{
			try{
			receivedInit = true ; 
			String values[] = theInitMsg.split("\\^") ;
			for (String param : values)
			{
				if (param.startsWith(INIT_RESPONSE))
				{
					glowdeckSettings.gsIN = param.substring(INIT_RESPONSE.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_ID))
				{
					glowdeckSettings.gsID = param.substring(INIT_RESPONSE_ID.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_CL))
				{
					glowdeckSettings.gsCL = param.substring(INIT_RESPONSE_CL.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_CS))
				{
					glowdeckSettings.gsCS = param.substring(INIT_RESPONSE_CS.length() ) ;
					continue ;
				}
				//if (param.startsWith(INIT_RESPONSE_LT))
				//{
				//	glowdeckSettings.gsLT = param.substring(INIT_RESPONSE_LT.length() ) ;
				//	continue ;
				//}
				if (param.startsWith(INIT_RESPONSE_COL))
				{
					glowdeckSettings.gsCOL = param.substring(INIT_RESPONSE_COL.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_DL))
				{
					glowdeckSettings.gsDL = param.substring(INIT_RESPONSE_DL.length() ) ;
					continue ;
				}
				//if (param.startsWith(INIT_RESPONSE_LC))
				//{
				//	glowdeckSettings.gsLC = param.substring(INIT_RESPONSE_LC.length() ) ;
				//	continue ;
				//}
				if (param.startsWith(INIT_RESPONSE_LS))
				{
					glowdeckSettings.gsLS = param.substring(INIT_RESPONSE_LS.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_MB))
				{
					glowdeckSettings.gsMB = param.substring(INIT_RESPONSE_MB.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_MS))
				{
					glowdeckSettings.gsMS = param.substring(INIT_RESPONSE_MS.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_PB))
				{
					glowdeckSettings.gsPB = param.substring(INIT_RESPONSE_PB.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_PV))
				{
					glowdeckSettings.gsPV = param.substring(INIT_RESPONSE_PV.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_QI))
				{
					glowdeckSettings.gsQI = param.substring(INIT_RESPONSE_QI.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_SM))
				{
					glowdeckSettings.gsSM = param.substring(INIT_RESPONSE_SM.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_ST))
				{
					glowdeckSettings.gsST = param.substring(INIT_RESPONSE_ST.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_SX))
				{
					glowdeckSettings.gsSX = param.substring(INIT_RESPONSE_SX.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_UI))
				{
					glowdeckSettings.gsUI = param.substring(INIT_RESPONSE_UI.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_WE))
				{
					glowdeckSettings.gsWE = param.substring(INIT_RESPONSE_WE.length() ) ;
					continue ;
				}

				if (param.startsWith(INIT_RESPONSE_WI))
				{
					glowdeckSettings.gsWI = param.substring(INIT_RESPONSE_WI.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_WP))
				{
					glowdeckSettings.gsWP = param.substring(INIT_RESPONSE_WP.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_EQZ))
				{
					glowdeckSettings.gsEQZ = param.substring(INIT_RESPONSE_EQZ.length() ) ;
				}
				/*

				if (param.startsWith(INIT_RESPONSE_DB))
				{
					glowdeckSettings.gsDB = param.substring(INIT_RESPONSE_DB.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_DA))
				{
					glowdeckSettings.gsDA = param.substring(INIT_RESPONSE_DA.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_WF))
				{
					glowdeckSettings.gsWF = param.substring(INIT_RESPONSE_WF.length() ) ;
					continue ;
				}


				if (param.startsWith(INIT_RESPONSE_FP))
				{
					glowdeckSettings.gsFP = param.substring(INIT_RESPONSE_FP.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_FH))
				{
					glowdeckSettings.gsFH = param.substring(INIT_RESPONSE_FH.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_LP))
				{
					glowdeckSettings.gsLP = param.substring(INIT_RESPONSE_LP.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_LH))
				{
					glowdeckSettings.gsLH = param.substring(INIT_RESPONSE_LH.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_RP))
				{
					glowdeckSettings.gsRP = param.substring(INIT_RESPONSE_RP.length() ) ;
					continue ;
				}
				if (param.startsWith(INIT_RESPONSE_RH))
				{
					glowdeckSettings.gsRH = param.substring(INIT_RESPONSE_RH.length() ) ;
					continue ;
				}




				if (param.startsWith(INIT_RESPONSE_NM))
				{
					glowdeckSettings.gsNM = param.substring(INIT_RESPONSE_NM.length() ) ;
					continue ;
				}
				 */
			}
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", 
						glowdeckSettings.gsIN + "," +
								glowdeckSettings.gsID  + "," +
								glowdeckSettings.gsCL  + "," +
								glowdeckSettings.gsCS  + "," +
								glowdeckSettings.gsCOL  + "," +
								glowdeckSettings.gsDL  + "," +
								glowdeckSettings.gsLS  + "," +
								glowdeckSettings.gsMB  + "," +
								glowdeckSettings.gsMS  + "," +
								glowdeckSettings.gsPB  + "," +
								glowdeckSettings.gsPV  + "," +
								glowdeckSettings.gsQI  + "," +
								glowdeckSettings.gsSM + "," +
								glowdeckSettings.gsST  + "," +
								glowdeckSettings.gsSX  + "," +
								glowdeckSettings.gsUI  + "," +
								glowdeckSettings.gsWE  + "," +
								glowdeckSettings.gsWI  + "," +
								glowdeckSettings.gsWP  + "," +
								glowdeckSettings.gsEQZ ) ;
			}

			final MainActivity mainActivity = MainActivity.getMainActivity()  ;
			if (mainActivity == null)
			{
				return ; 
			}

			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {



					mainActivity.displayView(MainActivity.DRAWER_START_PROVISION) ;


				}
			});

			}catch(Exception e){e.printStackTrace();}
		}

	}

	public CurrentGlowdecks(BluetoothSppManager bluetoothSppManager)
	{ 
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","CurrentGlowdecks constructor") ;
		}
		mBluetoothSppManager = bluetoothSppManager ;
		if (mBluetoothSppManager == null)
		{
			return ; 
		}
		if (mListGlowdecks == null)
		{
			mListGlowdecks= new HashMap<String, GlowdeckDevice>();
			
			getPairedGlowdeckDevices() ;
		}

		// start scan ; 
		}catch(Exception e){e.printStackTrace();}
		return ; 
	}
	private void startScan()
	{
		try{
		startScanForNearbyGlowdecks(0) ;
		}catch(Exception e){e.printStackTrace();}
	}
	private void getPairedGlowdeckDevices()
	{

		try{
		if (BluetoothSppManager.isBTavailable())
		{
			Set<BluetoothDevice> pairedDevices = BluetoothSppManager.getMsBluetoothAdapter().getBondedDevices();
			//Set<BluetoothDevice> pairedDevices = BluetoothSppManager.getMsBluetoothAdapter().getDevices();

			for(BluetoothDevice bt : pairedDevices)
			{

				String upperCaseName = bt.getName().toUpperCase() ;
				//NO LONGER EXPERIMENTING WITH NOT FILTERING BASED ON NAME
				boolean goodPrefix = false  ;
				//boolean goodPrefix = true ;


				if ( (upperCaseName.contains(GLOWDECK)) || (upperCaseName.contains("DECK")) || (upperCaseName.contains(BLUECREATION)) || (upperCaseName.contains("GD")) )
				{
					goodPrefix = true ;
				}


				boolean goodSuffix = true ;
				/*
				if ( (upperCaseName.indexOf(GLOWDECK) == 0) && 
						(upperCaseName.length() == GLOWDECK.length() + NUM_TRAIL_HEXCHARS))
				{
					goodPrefix = true ; 
					for (int i = 0 ; i < NUM_TRAIL_HEXCHARS; i++)
					{
						if (!isHexChar(upperCaseName.charAt(i+GLOWDECK.length())     ))
						{
							goodSuffix = false ; 
							break ; 
						}
					}

				}

				if (!goodPrefix)
				{
					if ( (upperCaseName.indexOf(BLUECREATION) == 0) && 
							(upperCaseName.length() == BLUECREATION.length() + NUM_TRAIL_HEXCHARS))
					{
						goodPrefix = true ; 
						for (int i = 0 ; i < NUM_TRAIL_HEXCHARS; i++)
						{
							if (!isHexChar(upperCaseName.charAt(i+BLUECREATION.length())     ))
							{
								goodSuffix = false ; 
								break ; 
							}
						}

					}
				}
				 */
				int position = 0 ; //to find next open slot 
				if (goodPrefix && goodSuffix)
				{
					GlowdeckDevice glowdeckDevice = 
							new GlowdeckDevice(bt.getType(), 
									bt.getName().replace("\n", ""), 
									bt.getUuids(), 
									bt.getAddress(), 
									bt.getBondState(),
									position) ;

					mListGlowdecks.put (bt.getName().replace("\n", ""),glowdeckDevice) ;
				} 

			}

		}
		}catch(Exception e){e.printStackTrace();}

	}
	public int getNumGlowdecksFound()
	{
		return mListGlowdecks.size() ; 
	}
	public String getFirstGlowdeckName()
	{
		String retVal = "" ; 
		try{
		Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			GlowdeckDevice glowdeckDevice = (GlowdeckDevice)pair.getValue();

			retVal =  glowdeckDevice.getName() ;
			break ; 
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	boolean isHexChar(char x)
	{
		if  ( ( x >= 'a' ) && (x <= 'f'))
		{
			return true ; 
		} 
		if  ( ( x >= 'A' ) && (x <= 'F'))
		{
			return true ; 
		}
		return (x >= '0') && (x <= '9');

	}


	public boolean getCurrentNetConnectedSetting()
	{
		boolean retVal = false ; 
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		
		if (glowdeckDevice != null)
		{
			if  (glowdeckDevice.glowdeckSettings.gsWIO.equals(on) )  
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 


	}
	public void setCurrentProvisioningSetting(String onOff)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		 
		if (glowdeckDevice != null)
		{
			
			if (onOff.equals(on))
			{
				glowdeckDevice.glowdeckSettings.gsID = on ;
			}
			else
			{
				glowdeckDevice.glowdeckSettings.gsID = off ;
			}
			 
		}
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public boolean getCurrentProvisioningSetting()
	{
		boolean retVal = false ;
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		 
		if (glowdeckDevice != null)
		{
			if  (glowdeckDevice.glowdeckSettings.gsIN.equals(on) ) 
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}

	public String getCurrentPrivacySetting()
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			return glowdeckDevice.glowdeckSettings.gsPV  ;
		}
		}catch(Exception e){e.printStackTrace();}
		return "Off" ;
	}
	public boolean getCurrentWifiConnectedSetting()
	{
		boolean retVal = false ; 
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			if ( glowdeckDevice.glowdeckSettings.gsWIO.equals(on))
				{
				retVal = true ;
				}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ;
	}
	public String getCurrentEqualizerSetting()
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			return glowdeckDevice.glowdeckSettings.gsEQZ  ;
		}
		}catch(Exception e){e.printStackTrace();}
		return "Off" ;
	}
	public void  setCurrentPrivacySetting(String privacyValue)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			glowdeckDevice.glowdeckSettings.gsPV = privacyValue;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void  setCurrentWifiConnectedSetting (String   wifiSettingsValue)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			glowdeckDevice.glowdeckSettings.gsWIO = wifiSettingsValue;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void  setCurrentEqualizerSetting(String   equalizerValue)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			glowdeckDevice.glowdeckSettings.gsEQZ = equalizerValue;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public boolean getCurrentSmartChargingSetting()
	{
		boolean retVal = false ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsCS.equals(on))
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	public String getCurrentNetworkSetting()
	{
		String netName= "" ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsWI != null)
			{
				netName = glowdeckDevice.glowdeckSettings.gsWI ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return netName ; 
	}
	public boolean getCurrentWirelessChargerSetting()
	{
		boolean retVal = false ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsCL.equals(on))
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	public boolean getCurrentAutoBrightnessSetting()
	{
		boolean retVal = false ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsLS.equals(on))
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	public boolean getCurrentLightSystemSetting()
	{
		boolean retVal = false ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsMB.equals(on))
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	public boolean getCurrentMusicInfoSetting()
	{
		boolean retVal = false ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsSM.equals(on))
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	public boolean getCurrentMusicSyncSetting()
	{
		boolean retVal = false ; 

		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			if (glowdeckDevice.glowdeckSettings.gsMS.equals(on))
			{
				retVal = true ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}
	public void setCurrentNetworkSetting(String ssid)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			glowdeckDevice.glowdeckSettings.gsWI = ssid; 

		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void setCurrentPasswordSetting(String onOff)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{

			glowdeckDevice.glowdeckSettings.gsWP = onOff; 

		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void setCurrentWirelessChargerSetting(boolean val)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			if (val)
			{
				glowdeckDevice.glowdeckSettings.gsCL = on ; 
			}
			else
			{
				glowdeckDevice.glowdeckSettings.gsCL = off ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void setCurrentSmartChargingSetting(boolean val)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			if (val)
			{
				glowdeckDevice.glowdeckSettings.gsCS = on ; 
			}
			else
			{
				glowdeckDevice.glowdeckSettings.gsCS = off ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void setCurrentAutoBrightnessSetting(boolean val)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			if (val)
			{
				glowdeckDevice.glowdeckSettings.gsLS = on ; 
			}
			else
			{
				glowdeckDevice.glowdeckSettings.gsLS = off ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void setCurrentLightSystemSetting(boolean val)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			if (val)
			{
				glowdeckDevice.glowdeckSettings.gsMB = on ; 
			}
			else
			{
				glowdeckDevice.glowdeckSettings.gsMB = off ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void setCurrentMusicSyncSetting(boolean val)
	{
		try{
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			if (val)
			{
				glowdeckDevice.glowdeckSettings.gsMS = on ; 
			}
			else
			{
				glowdeckDevice.glowdeckSettings.gsMS = off ; 
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public Integer getColorFromCurrentGlowdeck()
	{
		Integer retVal = null ; 
		try{
		String colorStrVal  = null ; 
		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;
		if (glowdeckDevice != null)
		{
			colorStrVal = glowdeckDevice.glowdeckSettings.gsCOL  ; 
			String[] rgb = colorStrVal.split("\\:") ;
			if (rgb.length == 3)
			{
				retVal = Color.rgb(Integer.parseInt(rgb[0]),Integer.parseInt(rgb[1]) , Integer.parseInt(rgb[2]))  ;
			}
		}
		}catch(Exception e){e.printStackTrace();}
		return retVal ; 
	}

	public void  updateCurrentGlowdeckColor(String updatedColor)
	{
		try{
		Integer retVal = null ; 
		String colorStrVal  = null ; 

		GlowdeckDevice glowdeckDevice = getCurrentlyConnected() ;


		if ( (glowdeckDevice != null) && (glowdeckDevice.isConnected()) )
		{
			glowdeckDevice.glowdeckSettings.gsCOL = updatedColor ;
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public GlowdeckDevice getGlowdeckWithName(String glowdeckName)
	{
		return mListGlowdecks.get(glowdeckName);
	}

	public boolean isConnected()

	{
		return getCurrentlyConnected() != null;
	}
	public GlowdeckDevice getCurrentlyConnecting() 
	{
		
		GlowdeckDevice glowdeckDevice = null ;
		try{
		Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			glowdeckDevice = (GlowdeckDevice)pair.getValue();

			if (glowdeckDevice.isConnecting )
			{
				break ; 
			}
			else
			{
				glowdeckDevice = null ; 
			}

		}
		}catch(Exception e){e.printStackTrace();}
		return glowdeckDevice ; 
	}
	public GlowdeckDevice getCurrentlyConnected() 
	{
		GlowdeckDevice glowdeckDevice = null ;
		try{
		Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			glowdeckDevice = (GlowdeckDevice)pair.getValue();

			if (glowdeckDevice.isConnected() )
			{
				break ; 
			}
			else
			{
				glowdeckDevice = null ; 
			}

		}
		}catch(Exception e){e.printStackTrace();}
		return glowdeckDevice ; 
	}
	public GlowdeckDevice getCurrentlySelected() 
	{
		GlowdeckDevice glowdeckDevice = null ;
		try{
		Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			glowdeckDevice = (GlowdeckDevice)pair.getValue();

			if (glowdeckDevice.isCurrentlySelected())
			{
				break ; 
			}
			else
			{
				glowdeckDevice = null ; 
			}

		}
		}catch(Exception e){e.printStackTrace();}
		return glowdeckDevice ; 
	}
	public void  setCurrentlySelected(int index) 
	{

		try{
		Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			GlowdeckDevice glowdeckDevice = (GlowdeckDevice)pair.getValue();

			glowdeckDevice.currentlySelected = glowdeckDevice.menuPosition == index;

		}
		}catch(Exception e){e.printStackTrace();}
	}
	public String getCurrentlyConnectedName() 
	{

		if (getCurrentlyConnected() != null )
		{
			return getCurrentlyConnected().getName() ;
		}
		else
		{
			return "" ;
		}
	}
	public GlowdeckDevice getDeviceAtPosition(int position)
	{
		GlowdeckDevice glowdeckDevice = null ;

		try{
		Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			glowdeckDevice = (GlowdeckDevice)pair.getValue();

			if (glowdeckDevice.menuPosition == position)
			{

				break ; 
			}
			else
			{
				glowdeckDevice = null ; 
			}

		}		

		}catch(Exception e){e.printStackTrace();}
		return 		glowdeckDevice ;
	}
	void startScanForNearbyGlowdecks(int when)
	{
		try{
		// start alarm 
		Intent intent = new Intent(MainActivity.getMainActivity(), GDscanBroadcastReceiver.class);
		}catch(Exception e){e.printStackTrace();}
		/*
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(
    	        this.getApplicationContext(), 234324243, intent, 0);
    	AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    	alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
    	        + (i * 1000), pendingIntent);
    	Toast.makeText(this, "Alarm set in " + i + " seconds",
    	        Toast.LENGTH_LONG).show();
		 */
	}
	/*
    public void checkReconnect()
    {
    	Iterator it = mListGlowdecks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			GlowdeckDevice glowdeckDevice = (GlowdeckDevice)pair.getValue();

			if (glowdeckDevice.reconnectOnRestart)
			{
				//glowdeckDevice.reconnectOnRestart = false ; 
				StreamsApplication streamsApplication   =  (StreamsApplication)MainActivity.getMainActivity().getApplication() ;
				BluetoothSppManager bluetoothSppManager =   streamsApplication.getBluetoothSppManager() ; 

				if (bluetoothSppManager != null)
				{
				bluetoothSppManager.connectToGlowdeck(glowdeckDevice.menuPosition) ;
				}
			    break ; 
			}

		}	
    }
	 */
}


