package com.plsco.glowdeck.drawer;

import android.app.Activity;
import android.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.plsco.glowdeck.colorpicker.AppConfig;
import com.plsco.glowdeck.colorpicker.ColorPicker;
import com.plsco.glowdeck.colorpicker.OpacityBar;
import com.plsco.glowdeck.colorpicker.SVBar;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks.GlowdeckDevice;

import java.lang.reflect.Array;


/**
 * 
 * @author Joe Diamand 
 * @version 1.0   03/10/15
 * 
 * Project: Streams Android Implementation
 * 
 * file: PickerFragment.java
 * 
 *  (c) Copyright 2015. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * created 3/11/15
 */

/**
 * The PickerFragment() extends  Fragment
 * 
 *  
 *
 */
public class PickerFragment extends Fragment  { //implements  OnColorChangedListener


	//static final String TAG = "PickerFragment" ;
	Context context ; 
	public static boolean reEntry = false ; 
	private static ColorPicker picker;
	public static ColorPicker getPicker() {
		return picker;
	}

	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;


	static GlowdeckDevice mCurrentDevice = null ; 
	static private SeekBar mLightsControl = null;
	static private Button mAnimationsButton = null;
	static private Switch  mStreamsSwitch = null ; 
	private static boolean initial ; 





	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initial = true ; 
	}
	public static boolean isInitial() {
		return initial;
	}
	public static void setInitial(boolean initialParm) {
		initial = initialParm;
	}



	public PickerFragment(){



	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		try{
		inflater.inflate(R.menu.color_picker_actions, menu);
		MenuItem menuItem = menu.findItem(R.id.action_settings) ;
		menuItem.setVisible(false); 
		menu.findItem(R.id.ic_stream_setting).setVisible(true);
		}catch(Exception e){e.printStackTrace();}
		super.onCreateOptionsMenu(menu,inflater);
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		//MenuItem menuItem = menu.findItem(R.id.action_settings) ;
		try{
		menu.findItem(R.id.action_settings).setVisible(false);

		menu.findItem(R.id.ic_stream_setting).setVisible(true);
		}catch(Exception e){e.printStackTrace();}
		super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {



		// Handle action bar actions click
		switch (item.getItemId()) {

		case R.id.ic_stream_setting:

			try{
			Intent appConfigIntent = new Intent(context.getApplicationContext(), AppConfig.class);

			startActivity(appConfigIntent);
			MainActivity.getMainActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}catch(Exception e){e.printStackTrace();}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		try{
		if(isVisibleToUser) {
			Activity a = getActivity();
			if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Log.d(TAG,"onCreateView") ;

		View rootView = null;
		try{
		MainActivity mainActivity = MainActivity.getMainActivity() ;
		if (mainActivity == null )
		{
			//Log.d(TAG,"onCreate::mainActivity::return") ;

			Intent intent = new Intent(this.getActivity(), MainActivity.class);
			startActivity(intent);
			this.getActivity().finish() ; 
			return null ; 
		}
		rootView = inflater.inflate(R.layout.fragment_picker, container, false);
		final StreamsApplication streamsApplication = (StreamsApplication)mainActivity.getApplication() ;
		context = this.getActivity() ;
		setHasOptionsMenu(true); 
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","PickerFragment onCreateView") ;
		}





		GlowdeckDevice  glowdeckDevice = MainActivity.getMainActivity().getCurrentGlowdecks().getCurrentlySelected() ;
		if (glowdeckDevice != null)
		{
			mCurrentDevice = glowdeckDevice ; 

		}

		picker =  (ColorPicker) rootView.findViewById(R.id.picker    );
		svBar =  (SVBar) rootView.findViewById(R.id.svbar);
		svBar.setColorPicker(picker) ;
		svBar.initColor() ; 
		opacityBar =  (OpacityBar) rootView.findViewById(R.id.opacitybar);
		opacityBar.setColorPicker(picker) ; 
		opacityBar.initColor() ;
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);


		mLightsControl = (SeekBar) rootView.findViewById(R.id.seekBar1);

		mLightsControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            int progressChanged = 0;

				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

					progressChanged = progress;

                    int brightness = progressChanged/10;

                    String dlMsg = "DBR:" + brightness + "^";

                    streamsApplication.getBluetoothSppManager().sendMessage(dlMsg);

                    if (mCurrentDevice != null) {

                        mCurrentDevice.setCurrentDisplayBrightness(brightness);

                    }

				}

				public void onStartTrackingTouch(SeekBar seekBar) {

					// TODO Auto-generated method stub

				}

				public void onStopTrackingTouch(SeekBar seekBar) {

					int brightness = progressChanged/10;

					String dlMsg = "DBR:" + brightness + "^";

					streamsApplication.getBluetoothSppManager().sendMessage(dlMsg);

					//Toast.makeText(MainActivity.getMainActivity(),"On setOnSeekBarChangeListener: save brighteness:" + dlMsg,
					//Toast.LENGTH_SHORT).show();

					if (mCurrentDevice != null) {

						mCurrentDevice.setCurrentDisplayBrightness(brightness);

					}

				}

			});

		mAnimationsButton = (Button) rootView.findViewById(R.id.animations_button);

		mAnimationsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popAnimationsPicker();
			}
		});

		mStreamsSwitch = (Switch) rootView.findViewById(R.id.nightSwitch);

		mStreamsSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked){

					if (mCurrentDevice != null)
					{
						if (!mCurrentDevice.getCurrentDisplayStreamsSwitch())
						{
							streamsApplication.getBluetoothSppManager().sendMessage("STR:1^");
						}
						mCurrentDevice.setCurrentDisplayStreamsSwitch(true) ;
					}
				}else{

					if (mCurrentDevice != null)
					{
						if (mCurrentDevice.getCurrentDisplayStreamsSwitch())
						{
							streamsApplication.getBluetoothSppManager().sendMessage("STR:0^");
						}
						mCurrentDevice.setCurrentDisplayStreamsSwitch(false) ;
					}
				}

			}
		});

		}catch(Exception e){e.printStackTrace();}
		return rootView ;
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		reEntry = true ; 
		try{
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}catch(Exception e){e.printStackTrace();}
		super.onPause();
	}
	public static void setStreamsSwitch(boolean isChecked) 
	{
		mStreamsSwitch.setChecked(isChecked);
	}

	public void popAnimationsPicker() {

        /*
        if (cmd.indexOf(F("0")) != -1) { type = 0; anmType = F("swirl"); }
        else if (cmd.indexOf(F("1")) != -1) { type = 1; anmType = F("glitter"); }
        else if (cmd.indexOf(F("2")) != -1) { type = 2; anmType = F("confetti"); }
        else if (cmd.indexOf(F("3")) != -1) { type = 3; anmType = F("sentry"); }
        else if (cmd.indexOf(F("4")) != -1) { type = 4; anmType = F("showcase"); }
        else if (cmd.indexOf(F("5")) != -1) { type = 5; anmType = F("party"); }
        else if (cmd.indexOf(F("6")) != -1) { type = 6; anmType = F("twinkle"); }
        */

        CharSequence colors[] = new CharSequence[] {"Off", "Swirl", "Glitter", "Confetti", "Sentry", "Showcase", "Party", "Twinkle"};


        AlertDialog.Builder builder; // = new AlertDialog.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        }
        else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle("Select an animation");

        builder.setItems(colors, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // the user clicked on colors[which]

                int anmInt = which - 1;

                String anmString = "ANM:" + String.valueOf(anmInt) + "^";

                final StreamsApplication streamsApplication = (StreamsApplication)MainActivity.getMainActivity().getApplication();

                if (mCurrentDevice != null) {

                    streamsApplication.getBluetoothSppManager().sendMessage(anmString);

                }

            }

        });

        builder.show();

    }

	public static void setLightsControl(int value) {

		mLightsControl.setProgress(value);

	}

	@Override
	public void onResume() {

		try {

            if (mCurrentDevice != null) {

			    if (mLightsControl != null) {

				    int progress = mCurrentDevice.getCurrentDisplayBrightness() * 10;

				    mLightsControl.setProgress(progress);
			    }

			    if (mStreamsSwitch != null) {

			    	boolean isChecked = mCurrentDevice.getCurrentDisplayStreamsSwitch();

			    	mStreamsSwitch.setChecked(isChecked);

			    }

		    }

		}
		catch(Exception e){e.printStackTrace();

		}

        super.onResume();

	}

	public static String getAnimationString(String animation) {

        if (animation == "Swirl") {
            return "ANM:0^";
        }
        else if (animation == "Glitter") {
            return "ANM:1^";
        }
        else if (animation == "Confetti") {
            return "ANM:2^";
        }
        else if (animation == "Sentry") {
            return "ANM:3^";
        }
        else if (animation == "Showcase") {
            return "ANM:4^";
        }
        else if (animation == "Party") {
            return "ANM:5^";
        }
        else if (animation == "Twinkle") {
            return "ANM:6^";
        }
        else {
            return "ANM:-1^";
        }

    }

	public static String getCOLchangeString(int colorPicker) {

		return "COL:" + getRGBStringColors(colorPicker) + "^";

	}

	public static String getRGBStringColors(int colorPicker) {

		final int theColor = argbTorgb(colorPicker);

		int red   = Color.red(theColor);

		int green  = Color.green(theColor);

		int blue  = Color.blue(theColor);
		
		return (red + ":" + green + ":" + blue);

	}

	public static int argbTorgb(int aRGB) {
		
		int alpha = Color.alpha(aRGB);

		int red   = Color.red(aRGB);

		int green  = Color.green(aRGB);

		int blue  = Color.blue(aRGB);

		try {

            float alphaForeGround = (float)(alpha)/255.0f;

            red = (int)((float)red * alphaForeGround);

            green = (int)((float)green * alphaForeGround);

            blue = (int)((float)blue * alphaForeGround);

		}
		catch(Exception e) {

            e.printStackTrace();

		}

		return Color.rgb(red, green, blue);

	}




}
