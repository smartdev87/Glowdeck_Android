

/*
 * Copyright 2012 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.larswerkman.holocolorpicker;
package com.plsco.glowdeck.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.plsco.glowdeck.bluetooth.BluetoothSppManager;
import com.plsco.glowdeck.drawer.PickerFragment;
import com.plsco.glowdeck.glowdeck.CurrentGlowdecks;
import com.plsco.glowdeck.ui.MainActivity;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;
import com.plsco.glowdeck.colorpicker.AppConfig.PrefsFragment;


//import com.larswerkman.holocolorpicker.R;

public class SVBar extends View {

	/*
	 * Constants used to save/restore the instance state.
	 */
	//final static String TAG = "SVBar" ;
	private static final String STATE_PARENT = "parent";
	private static final String STATE_COLOR = "color";
	private static final String STATE_SATURATION = "saturation";
	private static final String STATE_VALUE = "value";
	private static final String STATE_ORIENTATION = "orientation";

	/**
	 * Constants used to identify orientation.
	 */
	private static final boolean ORIENTATION_HORIZONTAL = true;
	private static final boolean ORIENTATION_VERTICAL = false;

	/**
	 * Default orientation of the bar.
	 */
	private static final boolean ORIENTATION_DEFAULT = ORIENTATION_HORIZONTAL;

	/**
	 * The thickness of the bar.
	 */
	private int mBarThickness;

	/**
	 * The length of the bar.
	 */
	private int mBarLength;
	private int mPreferredBarLength;

	/**
	 * The radius of the pointer.
	 */
	private int mBarPointerRadius;

	/**
	 * The radius of the halo of the pointer.
	 */
	private int mBarPointerHaloRadius;

	/**
	 * The position of the pointer on the bar.
	 */
	private int mBarPointerPosition;

	/**
	 * {@code Paint} instance used to draw the bar.
	 */
	private Paint mBarPaint;

	/**
	 * {@code Paint} instance used to draw the pointer.
	 */
	private Paint mBarPointerPaint;

	/**
	 * {@code Paint} instance used to draw the halo of the pointer.
	 */
	private Paint mBarPointerHaloPaint;

	/**
	 * The rectangle enclosing the bar.
	 */
	private RectF mBarRect = new RectF();

	/**
	 * {@code Shader} instance used to fill the shader of the paint.
	 */
	private Shader shader;

	/**
	 * {@code true} if the user clicked on the pointer to start the move mode. <br>
	 * {@code false} once the user stops touching the screen.
	 * 
	 * @see #onTouchEvent(MotionEvent)
	 */
	private boolean mIsMovingPointer;

	/**
	 * The ARGB value of the currently selected color.
	 */
	private int mColor;
	//private Integer mCurrentGlowdeckColor ; 
	static boolean  newCircleInitialized   ; 

	/**
	 * An array of floats that can be build into a {@code Color} <br>
	 * Where we can extract the Saturation and Value from.
	 */
	private float[] mHSVColor = new float[3];

	/**
	 * Factor used to calculate the position to the Saturation/Value on the bar.
	 */
	private float mPosToSVFactor;

	/**
	 * Factor used to calculate the Saturation/Value to the postion on the bar.
	 */
	private float mSVToPosFactor;

	/**
	 * {@code ColorPicker} instance used to control the ColorPicker.
	 */
	private ColorPicker mPicker = null;

	/**
	 * Used to toggle orientation between vertical and horizontal.
	 */
	private boolean mOrientation;

	public SVBar(Context context) {
		super(context);
		init(null, 0);
	}

	public SVBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public SVBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {

		try{
		MainActivity mainActivity =  MainActivity.getMainActivity() ;


		newCircleInitialized = false ; 




		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.ColorBars, defStyle, 0);
		final Resources b = getContext().getResources();

		mBarThickness = a.getDimensionPixelSize(
				R.styleable.ColorBars_bar_thickness,
				b.getDimensionPixelSize(R.dimen.bar_thickness));
		mBarLength = a.getDimensionPixelSize(R.styleable.ColorBars_bar_length,
				b.getDimensionPixelSize(R.dimen.bar_length));
		mPreferredBarLength = mBarLength;
		mBarPointerRadius = a.getDimensionPixelSize(
				R.styleable.ColorBars_bar_pointer_radius,
				b.getDimensionPixelSize(R.dimen.bar_pointer_radius));
		mBarPointerHaloRadius = a.getDimensionPixelSize(
				R.styleable.ColorBars_bar_pointer_halo_radius,
				b.getDimensionPixelSize(R.dimen.bar_pointer_halo_radius));
		mOrientation = a.getBoolean(
				R.styleable.ColorBars_bar_orientation_horizontal, ORIENTATION_DEFAULT);

		a.recycle();

		mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPaint.setShader(shader);

		mBarPointerPosition = (mBarLength / 2 ) + mBarPointerHaloRadius    ;

		mBarPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPointerHaloPaint.setColor(Color.BLACK);
		mBarPointerHaloPaint.setAlpha(0x50);

		mBarPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPointerPaint.setColor(0xff81ff00);

		mPosToSVFactor = 1 / ((float) mBarLength / 2);
		mSVToPosFactor = ((float) mBarLength / 2) / 1;

		}catch(Exception e){e.printStackTrace();}
	}
	public void initColor()
	{
		try{
		if (mPicker.getColorGlowdeck() != null )
		{
			int colorAlpha = mPicker.getColorGlowdeck() | ColorPicker.ALPHA ;
			mBarPointerPosition =  calculateSVBarPositionFromColor(colorAlpha) ; 

			setColor(colorAlpha) ;

		}
		}catch(Exception e){e.printStackTrace();}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try{
		final int intrinsicSize = mPreferredBarLength
				+ (mBarPointerHaloRadius * 2);

		// Variable orientation
		int measureSpec;
		if (mOrientation == ORIENTATION_HORIZONTAL) {
			measureSpec = widthMeasureSpec;
		}
		else {
			measureSpec = heightMeasureSpec;
		}
		int lengthMode = MeasureSpec.getMode(measureSpec);
		int lengthSize = MeasureSpec.getSize(measureSpec);

		int length;
		if (lengthMode == MeasureSpec.EXACTLY) {
			length = lengthSize;
		}
		else if (lengthMode == MeasureSpec.AT_MOST) {
			length = Math.min(intrinsicSize, lengthSize);
		}
		else {
			length = intrinsicSize;
		}

		int barPointerHaloRadiusx2 = mBarPointerHaloRadius * 2;
		mBarLength = length - barPointerHaloRadiusx2;
		if(mOrientation == ORIENTATION_VERTICAL) {
			setMeasuredDimension(barPointerHaloRadiusx2,
					(mBarLength + barPointerHaloRadiusx2));
		}
		else {
			setMeasuredDimension((mBarLength + barPointerHaloRadiusx2),
					barPointerHaloRadiusx2);
		}
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		try{
		// Fill the rectangle instance based on orientation
		int x1, y1;
		if (mOrientation == ORIENTATION_HORIZONTAL) {
			x1 = (mBarLength + mBarPointerHaloRadius);
			y1 = mBarThickness;
			mBarLength = w - (mBarPointerHaloRadius * 2);
			mBarRect.set(mBarPointerHaloRadius,
					(mBarPointerHaloRadius - (mBarThickness / 2)),
					(mBarLength + (mBarPointerHaloRadius)),
					(mBarPointerHaloRadius + (mBarThickness / 2)));
		}
		else {
			x1 = mBarThickness;
			y1 = (mBarLength + mBarPointerHaloRadius);
			mBarLength = h - (mBarPointerHaloRadius * 2);
			mBarRect.set((mBarPointerHaloRadius - (mBarThickness / 2)),
					mBarPointerHaloRadius,
					(mBarPointerHaloRadius + (mBarThickness / 2)),
					(mBarLength + (mBarPointerHaloRadius)));
		}

		// Update variables that depend of mBarLength.
		if(!isInEditMode()){
			shader = new LinearGradient(mBarPointerHaloRadius, 0,
					x1, y1, new int[] {
					0xffffffff, Color.HSVToColor(mHSVColor), 0xff000000 },
					null, Shader.TileMode.CLAMP);
		} else {
			shader = new LinearGradient(mBarPointerHaloRadius, 0,
					x1, y1, new int[] {
					0xffffffff, 0xff81ff00, 0xff000000 }, null,
					Shader.TileMode.CLAMP);
			Color.colorToHSV(0xff81ff00, mHSVColor);
		}

		mBarPaint.setShader(shader);
		mPosToSVFactor = 1 / ((float) mBarLength / 2);
		mSVToPosFactor = ((float) mBarLength / 2) / 1;
		float[] hsvColor = new float[3];
		Color.colorToHSV(mColor, hsvColor);
		if (hsvColor[1] < hsvColor[2]) {
			mBarPointerPosition = Math.round((mSVToPosFactor * hsvColor[1])
					+ mBarPointerHaloRadius);
		} else {
			mBarPointerPosition = Math
					.round((mSVToPosFactor * (1 - hsvColor[2]))
							+ mBarPointerHaloRadius + (mBarLength / 2));
		}
		if(isInEditMode()){
			mBarPointerPosition = (mBarLength / 2) + mBarPointerHaloRadius;
		}
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try{
		// Draw the bar.
		canvas.drawRect(mBarRect, mBarPaint);

		// Calculate the center of the pointer.
		int cX, cY;
		if (mOrientation == ORIENTATION_HORIZONTAL) {
			cX = mBarPointerPosition;
			cY = mBarPointerHaloRadius;
		}
		else {
			cX = mBarPointerHaloRadius;
			cY = mBarPointerPosition;
		}

		// Draw the pointer halo.
		canvas.drawCircle(cX, cY, mBarPointerHaloRadius, mBarPointerHaloPaint);
		// Draw the pointer.
		canvas.drawCircle(cX, cY, mBarPointerRadius, mBarPointerPaint);
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try{
		getParent().requestDisallowInterceptTouchEvent(true);

		// Convert coordinates to our internal coordinate system
		float dimen;
		if (mOrientation == ORIENTATION_HORIZONTAL) {
			dimen = event.getX();
		}
		else {
			dimen = event.getY();
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsMovingPointer = true;
			// Check whether the user pressed on the pointer
			if (dimen >= (mBarPointerHaloRadius)
					&& dimen <= (mBarPointerHaloRadius + mBarLength)) {
				mBarPointerPosition = Math.round(dimen);
				if  (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","SV mBarPosition = " + mBarPointerPosition) ; 
				}
				calculateColor(Math.round(dimen));
				mBarPointerPaint.setColor(mColor);

				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsMovingPointer) {
				// Move the the pointer on the bar.
				if (dimen >= mBarPointerHaloRadius
						&& dimen <= (mBarPointerHaloRadius + mBarLength)) {
					mBarPointerPosition = Math.round(dimen);
					calculateColor(Math.round(dimen));
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
						mPicker.changeOpacityBarColor(mColor);
					}
					invalidate();
				} else if (dimen < mBarPointerHaloRadius) {
					mBarPointerPosition = mBarPointerHaloRadius;
					mColor = Color.WHITE;
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
						mPicker.changeOpacityBarColor(mColor);
					}
					invalidate();
				} else if (dimen > (mBarPointerHaloRadius + mBarLength)) {
					mBarPointerPosition = mBarPointerHaloRadius + mBarLength;
					mColor = Color.BLACK;
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
						mPicker.changeOpacityBarColor(mColor);
					}
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mIsMovingPointer = false;
			StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication() ;
			BluetoothSppManager bluetoothSppManager = streamsApplication.getBluetoothSppManager() ;
			if (bluetoothSppManager == null)
			{
				//Log.d(TAG,"onTouchEvent::MotionEvent.ACTION_UP::bluetoothSppManager::return") ;
				return false; 
			}
			if (mPicker == null)
			{
				return false ; 
			}
			String msg = PickerFragment.getCOLchangeString(mPicker.getColor()) ;
			bluetoothSppManager.sendMessage(msg);

			CurrentGlowdecks currentGlowdecks = MainActivity.getMainActivity().getCurrentGlowdecks() ;
			if (currentGlowdecks != null)
			{
				currentGlowdecks.updateCurrentGlowdeckColor(PickerFragment.getRGBStringColors(mPicker.getColor())) ;
				if (!currentGlowdecks.getCurrentLightSystemSetting())
				{
					currentGlowdecks.setCurrentLightSystemSetting(true) ;
					AppConfig appConfig = AppConfig.getAppconfig() ;
					if (appConfig != null)
					{

						PrefsFragment.sendGlowdeckMessageBool(  AppConfig.lightSystem, true) ;
					}


				}
			}
			mPicker.setOldCenterColor(mPicker.getColor());
		
			break;
		}
		}catch(Exception e){e.printStackTrace();}
		return true;
	}

	/**
	 * Set the pointer on the bar. With the saturation value.
	 * 
	 * @param saturation float between 0 and 1
	 */
	public void setSaturation(float saturation) {
		try{
		mBarPointerPosition = Math.round((mSVToPosFactor * saturation)
				+ mBarPointerHaloRadius);
		calculateColor(mBarPointerPosition);
		/*
		if (mPicker.getColorGlowdeck() != null) 
		{
			mBarPointerPaint.setColor(mPicker.getColorGlowdeck());
		}
		else
		{
			mBarPointerPaint.setColor(mColor);
		}
		 */
		mBarPointerPaint.setColor(mColor);
		// Check whether the Saturation/Value bar is added to the ColorPicker
		// wheel
		if (mPicker != null) {
			/*
			if (mPicker.getColorGlowdeck() != null) 
			{
				int colorAlpha =  mPicker.getColorGlowdeck() | ColorPicker.ALPHA ; 
				mPicker.setNewCenterColor(colorAlpha);
				mPicker.changeOpacityBarColor(colorAlpha);
			}

			else
			{
				mPicker.setNewCenterColor(mColor);
				mPicker.changeOpacityBarColor(mColor);
			}
			 */
			mPicker.setNewCenterColor(mColor);
			mPicker.changeOpacityBarColor(mColor);
		}

		invalidate();
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Set the pointer on the bar. With the Value value.
	 * 
	 * @param value float between 0 and 1
	 */
	public void setValue(float value) {
		try{
		mBarPointerPosition = Math.round((mSVToPosFactor * (1 - value))
				+ mBarPointerHaloRadius + (mBarLength / 2));
		calculateColor(mBarPointerPosition);
		mBarPointerPaint.setColor(mColor);
		// Check whether the Saturation/Value bar is added to the ColorPicker
		// wheel
		if (mPicker != null) {

			mPicker.setNewCenterColor(mColor);
			mPicker.changeOpacityBarColor(mColor);
		}

		invalidate();
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Set the bar color. <br>
	 * <br>
	 * Its discouraged to use this method.
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		try{
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","RGB Color is :" +    Color.alpha(color) + ","
					+ Color.red(color) + "," + Color.green(color) 
					+ "," + Color.blue (color))  ;  
			float[ ] hsvColor = new float[3];
			Color.colorToHSV(color, hsvColor);
			Log.d("dbg","HSV Color is :" +    hsvColor[0] + ","
					+ hsvColor[1] + "," + hsvColor[2] 
					)  ;  
		}
		int x1, y1;
		if(mOrientation) {
			x1 = (mBarLength + mBarPointerHaloRadius);
			y1 = mBarThickness;
		}        else { 
			x1 = mBarThickness;
			y1 = (mBarLength + mBarPointerHaloRadius);
		}

		Color.colorToHSV(color, mHSVColor);
		shader = new LinearGradient(mBarPointerHaloRadius, 0,
				x1, y1, new int[] {Color.WHITE, color, Color.BLACK}, null,
				Shader.TileMode.CLAMP);
		mBarPaint.setShader(shader);
		calculateColor(mBarPointerPosition); 
		mBarPointerPaint.setColor(mColor);
		if (mPicker != null) {
			/*
			if (mPicker.getColorGlowdeck() != null)
			{
				mPicker.setNewCenterColor(mPicker.getColorGlowdeck() | ColorPicker.ALPHA);
			}
			else
			{
				mPicker.setNewCenterColor(mColor);
			}
			 */
			mPicker.setNewCenterColor(mColor);

			if(mPicker.hasOpacityBar())
			{
				/*
				if (mPicker.getColorGlowdeck() != null)
				{
					mPicker.changeOpacityBarColor(mPicker.getColorGlowdeck());
				}
				else
				{
					mPicker.changeOpacityBarColor(mColor);
				}
				 */
				mPicker.changeOpacityBarColor(mColor);
			}

		}
		invalidate();
		}catch(Exception e){e.printStackTrace();}
	}

	Thread.UncaughtExceptionHandler test ;

	int calculateSVBarPositionFromColor(int rgbColor)
	{

		int position = (int)(mBarLength / 2.0 ) ;//+ mBarPointerHaloRadius   ;
		try{
		float positionf = 0f ;
		float []hsv = new float[3] ;

		Color.colorToHSV(rgbColor, hsv) ;
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","calculateBarPositionFromColor::argb a=" + Color.alpha(rgbColor) + ", r=" + Color.red(rgbColor) + ", g="  + Color.green(rgbColor)  
					+ ", b="  + Color.blue(rgbColor)) ;
			Log.d("dbg","calculateBarPositionFromColor::hsv=" + hsv[0] + "," + hsv[1] + ","  + hsv[2]   ) ;
		}


		if (hsv[1] == 1.0)
		{
			if (hsv[2] == 1.0)
			{
				return position ; 
			}
			positionf = position ;

			positionf *= hsv[2] ;
			position =   position*2 - (int)(positionf);

			return position  + mBarPointerHaloRadius; 

		}
		if (hsv[1] == 0.0)
		{
			if (hsv[2] == 1.0)
			{
				return 0 ; 
			}
			if (hsv[2] == 0.0)
			{
				return mBarLength  + mBarPointerHaloRadius ; 
			}
		}
		if (hsv[2] == 1.0)
		{

			positionf = position*hsv[1] ;

			position = (int)positionf  + mBarPointerHaloRadius ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg","positionf=" + positionf + ", position=" + position  ) ;
			}
			return position ; 
		}
		//position  = (int)  ( (mBarLength) - (position) * hsv[1] ) + mBarPointerHaloRadius ;
		position  = (int)  ( (mBarLength/2.0) *  hsv[1] ) + mBarPointerHaloRadius ;
		}catch(Exception e){e.printStackTrace();}
		return position ; 

	}
	/**
	 * Calculate the color selected by the pointer on the bar.
	 * 
	 * @param coord Coordinate of the pointer.
	 */
	private void calculateColor(int coord) {
		try{
		coord = coord - mBarPointerHaloRadius;
		if (coord > (mBarLength / 2) && (coord < mBarLength)) {
			mColor = Color
					.HSVToColor(new float[] {
							mHSVColor[0], 1f, 1 - (mPosToSVFactor * (coord - (mBarLength / 2)))
					});
		} else if (coord > 0 && coord < mBarLength) {
			mColor = Color.HSVToColor(new float[]{
					mHSVColor[0], (mPosToSVFactor * coord), 1f
			});
		} else if(coord == (mBarLength / 2)){
			mColor = Color.HSVToColor(new float[]{
					mHSVColor[0], 1f, 1f
			});
		} else if (coord <= 0) { 
			mColor = Color.WHITE;
		} else if (coord >= mBarLength) {
			mColor = Color.BLACK;
		}

		if ((mColor != Color.WHITE) && (mColor != Color.BLACK))
		{

		}
		if (mPicker != null)
		{
			if (mPicker.getmOpacityBar() != null)
			{
				int alpha = Color.alpha( mPicker.getmOpacityBar().getColor() ) ; 
				int red = Color.red(mColor) ;
				int green = Color.green(mColor)  ;
				int blue = Color.blue (mColor)  ;
				mColor = Color.argb(alpha, red, green, blue);
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Get the currently selected color.
	 * 
	 * @return The ARGB value of the currently selected color.
	 */
	public int getColor() {
		return mColor;
	}
	//public int getCurrentGlowDeckColor() {
	//	return ColorPicker.getColorOnCurrentGlowdeck();
	//}

	/**
	 * Adds a {@code ColorPicker} instance to the bar. <br>
	 * <br>
	 * WARNING: Don't change the color picker. it is done already when the bar
	 * is added to the ColorPicker
	 * 
	 * @see com.larswerkman.holocolorpicker.ColorPicker#addSVBar(SVBar)
	 * @param picker
	 */
	public void setColorPicker(ColorPicker picker) {
		mPicker = picker;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle state = null;
		try{
		Parcelable superState = super.onSaveInstanceState();

		state = new Bundle();
		state.putParcelable(STATE_PARENT, superState);
		state.putFloatArray(STATE_COLOR, mHSVColor);
		float[] hsvColor = new float[3];
		Color.colorToHSV(mColor, hsvColor);
		if (hsvColor[1] < hsvColor[2]) {
			state.putFloat(STATE_SATURATION, hsvColor[1]);
		} else {
			state.putFloat(STATE_VALUE, hsvColor[2]);
		}
		}catch(Exception e){e.printStackTrace();}
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		try{
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable(STATE_PARENT);
		super.onRestoreInstanceState(superState);

		setColor(Color.HSVToColor(savedState.getFloatArray(STATE_COLOR)));
		if (savedState.containsKey(STATE_SATURATION)) {
			setSaturation(savedState.getFloat(STATE_SATURATION));
		} else {
			setValue(savedState.getFloat(STATE_VALUE));
		}
		}catch(Exception e){e.printStackTrace();}
	}
}