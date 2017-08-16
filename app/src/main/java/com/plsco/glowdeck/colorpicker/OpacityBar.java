package com.plsco.glowdeck.colorpicker;

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

public class OpacityBar extends View {

	/*
	 * Constants used to save/restore the instance state.
	 */
	//final static String TAG = "OpacityBar" ; 
	private static final String STATE_PARENT = "parent";
	private static final String STATE_COLOR = "color";
	private static final String STATE_OPACITY = "opacity";
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
	//public int getmBarLength() {
	//	return mBarLength;
	//}

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

	//public int getmBarPointerPosition() {
	//	return mBarPointerPosition;
	//}

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

	/**
	 * An array of floats that can be build into a {@code Color} <br>
	 * Where we can extract the color from.
	 */
	private float[] mHSVColor = new float[3];

	/**
	 * Factor used to calculate the position to the Opacity on the bar.
	 */
	private float mPosToOpacFactor;

	/**
	 * Factor used to calculate the Opacity to the postion on the bar.
	 */
	private float mOpacToPosFactor;

    /**
     * Interface and listener so that changes in OpacityBar are sent
     * to the host activity/fragment
     */
    private OnOpacityChangedListener onOpacityChangedListener;

	/**
	 * Opacity of the latest entry of the onOpacityChangedListener.
	 */
	private int oldChangedListenerOpacity;

    public interface OnOpacityChangedListener {
        void onOpacityChanged(int opacity);
    }

    public void setOnOpacityChangedListener(OnOpacityChangedListener listener) {
        this.onOpacityChangedListener = listener;
    }

    public OnOpacityChangedListener getOnOpacityChangedListener() {
        return this.onOpacityChangedListener;
    }

	/**
	 * {@code ColorPicker} instance used to control the ColorPicker.
	 */
	private ColorPicker mPicker = null;

	/**
	 * Used to toggle orientation between vertical and horizontal.
	 */
	private boolean mOrientation;

	public OpacityBar(Context context) {
		super(context);
		init(null, 0);
	}

	public OpacityBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public OpacityBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		try{
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



		mBarPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPointerHaloPaint.setColor(Color.BLACK);
		mBarPointerHaloPaint.setAlpha(0x50);

		mBarPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBarPointerPaint.setColor(0xff81ff00);

		mPosToOpacFactor = 0xFF / ((float) mBarLength);
		mOpacToPosFactor = ((float) mBarLength) / 0xFF;
		}catch(Exception e){e.printStackTrace();}
	}
     int calculateOpacityBarPositionFromColor(int rgbColor)
	{
    	 int retVal = mBarLength + mBarPointerHaloRadius;
    	 try{
    	 float []hsv = new float[3] ;

 		 Color.colorToHSV(rgbColor, hsv) ;

 		 if ((0.0 < hsv[1]) && (hsv[1] < 1.0))
 		 {
 			if ((0.0 < hsv[2]) && (hsv[2] < 1.0))
 			{
 				retVal = (int)(mBarLength*hsv[2]) + mBarPointerHaloRadius ;
 			}
 		 }

    	 }catch(Exception e){e.printStackTrace();}
    	 return retVal ;
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
		if (!isInEditMode()){
			shader = new LinearGradient(mBarPointerHaloRadius, 0,
					x1, y1, new int[] {
							Color.HSVToColor(0x00, mHSVColor),
							Color.HSVToColor(0xFF, mHSVColor) }, null,
					Shader.TileMode.CLAMP);
		} else {
			shader = new LinearGradient(mBarPointerHaloRadius, 0,
					x1, y1, new int[] {
							0x0081ff00, 0xff81ff00 }, null, Shader.TileMode.CLAMP);
			Color.colorToHSV(0xff81ff00, mHSVColor);
		}

		mBarPaint.setShader(shader);
		mPosToOpacFactor = 0xFF / ((float) mBarLength);
		mOpacToPosFactor = ((float) mBarLength) / 0xFF;

		float[] hsvColor = new float[3];
		Color.colorToHSV(mColor, hsvColor);

		if (!isInEditMode()){
			mBarPointerPosition = Math.round((mOpacToPosFactor * Color.alpha(mColor))
					+ mBarPointerHaloRadius);
		} else {
			mBarPointerPosition = mBarLength + mBarPointerHaloRadius;
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
			// Check whether the user pressed on (or near) the pointer
	    	if (dimen >= (mBarPointerHaloRadius)
					&& dimen <= (mBarPointerHaloRadius + mBarLength)) {
				mBarPointerPosition = Math.round(dimen);
				if  (StreamsApplication.DEBUG_MODE)
				{
					Log.d("dbg","Opacity mBarPosition = " + mBarPointerPosition) ;
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
					}
					invalidate();
				} else if (dimen < mBarPointerHaloRadius) {
					mBarPointerPosition = mBarPointerHaloRadius;
					mColor = Color.TRANSPARENT;
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
					}
					invalidate();
				} else if (dimen > (mBarPointerHaloRadius + mBarLength)) {
					mBarPointerPosition = mBarPointerHaloRadius + mBarLength;
					mColor = Color.HSVToColor(mHSVColor);
					mBarPointerPaint.setColor(mColor);
					if (mPicker != null) {
						mPicker.setNewCenterColor(mColor);
					}
					invalidate();
				}
			}
			if(onOpacityChangedListener != null && oldChangedListenerOpacity != getOpacity()){
	            onOpacityChangedListener.onOpacityChanged(getOpacity());
	            oldChangedListenerOpacity = getOpacity();
			}
			break;
		case MotionEvent.ACTION_UP:
			mIsMovingPointer = false;

			StreamsApplication streamsApplication = (StreamsApplication) MainActivity.getMainActivity().getApplication() ;
			BluetoothSppManager bluetoothSppManager  = streamsApplication.getBluetoothSppManager() ;
			if (bluetoothSppManager == null)
			{
				//Log.d(TAG,"onTouchEvent::ACTION_UP::bluetoothSppManager::return") ;
				return false ;
			}
			if (mPicker == null)
			{
				//Log.d(TAG,"onTouchEvent::ACTION_UP::mPicker::return") ;
				return false ;
			}
			bluetoothSppManager.sendMessage(PickerFragment.getCOLchangeString(mPicker.getColor()));

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
			invalidate();

			break;
		}
		}catch(Exception e){e.printStackTrace();}
		return true;
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
		int x1, y1;
		if(mOrientation == ORIENTATION_HORIZONTAL) {
			x1 = (mBarLength + mBarPointerHaloRadius);
			y1 = mBarThickness;
		}
		else {
			x1 = mBarThickness;
			y1 = (mBarLength + mBarPointerHaloRadius);
		}

		Color.colorToHSV(color, mHSVColor);
		shader = new LinearGradient(mBarPointerHaloRadius, 0,
				x1, y1, new int[] {
						Color.HSVToColor(0x00, mHSVColor), color }, null,
				Shader.TileMode.CLAMP);
		mBarPaint.setShader(shader);
		calculateColor(mBarPointerPosition);
		mBarPointerPaint.setColor(mColor);
		if (mPicker != null) {
			mPicker.setNewCenterColor(color);

		}
		invalidate();
		}catch(Exception e){e.printStackTrace();}
	}


	/**
	 * Set the pointer on the bar. With the opacity value.
	 *
	 * @param opacity float between 0 and 255
	 */
	public void setOpacity(int opacity) {
		try{
		mBarPointerPosition = Math.round((mOpacToPosFactor * opacity))
				+ mBarPointerHaloRadius;
		calculateColor(mBarPointerPosition);
		mBarPointerPaint.setColor(mColor);
		if (mPicker != null) {
			mPicker.setNewCenterColor(mColor);
		}
		invalidate();
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Get the currently selected opacity.
	 *
	 * @return The int value of the currently selected opacity.
	 */
	public int getOpacity() {
		int opacity = Math
				.round((mPosToOpacFactor * (mBarPointerPosition - mBarPointerHaloRadius)));
		if (opacity < 5) {
			return 0x00;
		} else if (opacity > 250) {
			return 0xFF;
		} else {
			return opacity;
		}
	}

	/**
	 * Calculate the color selected by the pointer on the bar.
	 *
	 * @param coord Coordinate of the pointer.
	 */
        private void calculateColor(int coord) {
        	try{
    	    coord = coord - mBarPointerHaloRadius;
    	    if (coord < 0) {
    	    	coord = 0;
    	    } else if (coord > mBarLength) {
    	    	coord = mBarLength;
    	    }

    		mColor = Color.HSVToColor(
    			Math.round(mPosToOpacFactor * coord),
    			mHSVColor);
    		if (Color.alpha(mColor) > 250) {
    		    mColor = Color.HSVToColor(mHSVColor);
    		} else if (Color.alpha(mColor) < 5) {
    		    mColor = Color.TRANSPARENT;
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
	public void initColor()
	{
		try{
		if (mPicker.getColorGlowdeck() != null )
		{
			int colorAlpha = mPicker.getColorGlowdeck() | ColorPicker.ALPHA ;
			mBarPointerPosition = calculateOpacityBarPositionFromColor(colorAlpha) ;

			setColor(colorAlpha) ;

		}
		}catch(Exception e){e.printStackTrace();}
	}



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
		state.putInt(STATE_OPACITY, getOpacity());
		}catch(Exception e){e.printStackTrace();}
		return state;
	}
	/*
	if (StreamsApplication.DEBUG_MODE)
	{
	Log.d ("dbg:", "saving state Opacity : " + getOpacity()) ;
	}
	if (StreamsApplication.DEBUG_MODE)
	{
	Log.d ("dbg:", "loading state Opacity : " + savedState.getInt(STATE_OPACITY)) ;
	}
	*/
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		try{
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable(STATE_PARENT);
		super.onRestoreInstanceState(superState);

		setColor(Color.HSVToColor(savedState.getFloatArray(STATE_COLOR)));
		setOpacity(savedState.getInt(STATE_OPACITY));
		}catch(Exception e){e.printStackTrace();}
	}
}
