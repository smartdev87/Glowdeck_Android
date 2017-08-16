package com.plsco.glowdeck.drawer;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.plsco.glowdeck.services.UpdaterService;
import com.plsco.glowdeck.streamdata.StatusStream;
import com.plsco.glowdeck.streamdata.StreamAdapter;
import com.plsco.glowdeck.ui.StreamsApplication;
import com.plsco.glowdeck.R;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 * 
 * @author Joe Diamand 
 * @version 1.0   08/27/14
 * 
 * Project: Streams Android Implementation
 * 
 * file: EnhancedStreamsListFragment.java
 * 
 *  � Copyright 2014. PLSCO, Inc. All rights reserved.
 *
 */
/**
 * History
 * Prepare for Google Play Store 11/1/14
 */

/**
 * The EnhancedStreamsListFragment() extends ListFragment and implements OnTouchListener
 * 
 *  
 *
 */
public class EnhancedStreamsListFragment extends ListFragment implements OnTouchListener {


	// Constants
	public final static String ENHANCED_STREAM_ARCHIVE = "Enhanced Streams Archive" ;
	public final static String ENHANCED_STREAM_ARCHIVE_ID = "Enhanced Streams Archive ID" ;
	public static Activity mActivity ; 
	// Globals
	private float mSlop;
	private int mMinFlingVelocity;
	private int mMaxFlingVelocity;
	private int mDismissAnimationRefCount;
	private long mAnimationTime;
	private final Object[] mAnimationLock = new Object[0];
	private List<View> mAnimatedViews = new LinkedList<View>();
	private SortedSet<PendingDismissData> mPendingDismisses = new TreeSet<PendingDismissData>();
	private boolean mSwipeEnabled; 
	private boolean mSwipePaused;
	private int mSwipingLayout;
	private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero
	private View mSwipeDownView; 
	private View mSwipeDownChild;
	private float mDownX;
	private float mDownY;
	private int mDownPosition;
	private VelocityTracker mVelocityTracker;
	private boolean mSwiping;
	private Cursor mCursor = null ;
	//
	//

	//
	// static Globals
	//
	public enum CLICK_STATE { NOT_STARTED,STARTED,DONE }

	private static CLICK_STATE disableOnClickListener = CLICK_STATE.NOT_STARTED ;
	private static float  distanceMoved  ; 
	protected static Cursor msStreamsCursor ;
	//
	//  protected static Globals
	//
	protected static StreamAdapter msStreamAdapter = null ;
	//protected static ListView msTheStreamListview ;
	//
	//
	//
	// protected Globals
	//

	protected ProgressDialog mServerArchiveProgressDialog = null ; // start the spinner when sending archive request to updaterService
	//
	/**
	 * 
	 */
	public EnhancedStreamsListFragment()
	{
		// swipe always on 

		mSwipeEnabled = true; // differs from how it was done prev
		disableOnClickListener = CLICK_STATE.STARTED ; 

	}
	protected  static CLICK_STATE  getDisableOnClickListener()
	{
		return disableOnClickListener ;
	}
	protected static void setDisableOnClickListener(CLICK_STATE clickState)
	{
		disableOnClickListener = clickState ; 
	}
	protected  static float  getDistanceMoved()
	{
		return distanceMoved ;
	}
	/**
	 * @param ctx - needed to get the animation time
	 */
	protected void init(Context ctx) 
	{

		try{
		mAnimationTime = ctx.getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		ViewConfiguration vc =ViewConfiguration.get(ctx);
		mSlop = getResources().getDimension(R.dimen.elv_touch_slop);
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Animate the dismissed list item to zero-height and fire the dismiss callback when
	 * all dismissed list item animations have completed.
	 *
	 * @param dismissView The mView that has been slided out.
	 * @param listItemView The list item mView. This is the whole mView of the list item, and not just
	 *                     the part, that the user swiped.
	 * @param dismissPosition The position of the mView inside the list.
	 */
	private void performDismiss(final View dismissView, final View listItemView, final int dismissPosition) {
      try{
		final ViewGroup.LayoutParams lp = listItemView.getLayoutParams();
		final int originalLayoutHeight = lp.height;

		int originalHeight = listItemView.getHeight();
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","performDismiss::started") ;
		}
		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				// Make sure no other animation is running. Remove animation from running list, that just finished
				boolean noAnimationLeft;
				synchronized(mAnimationLock) {
					--mDismissAnimationRefCount;
					mAnimatedViews.remove(dismissView);
					noAnimationLeft = mDismissAnimationRefCount == 0;
				}

				if (noAnimationLeft) {
					ViewGroup.LayoutParams lp;
					if (StreamsApplication.DEBUG_MODE)
					{
						Log.d("dbg","performDismiss::onAnimationEnd") ;
					}
					for (PendingDismissData pendingDismiss : mPendingDismisses) {
						if (StreamsApplication.DEBUG_MODE)
						{
							Log.d("dbg","performDismiss::PendingDismissData") ;
						}
						ViewHelper.setAlpha(pendingDismiss.view, 1f);
						ViewHelper.setTranslationX(pendingDismiss.view, 0);
						lp = pendingDismiss.childView.getLayoutParams();
						lp.height = originalLayoutHeight;
						pendingDismiss.childView.setLayoutParams(lp);
					}

					mPendingDismisses.clear();
					disableOnClickListener = CLICK_STATE.NOT_STARTED ; 

				}
			}
		});
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","performDismiss::addUpdateListener") ;
		}
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				listItemView.setLayoutParams(lp);
			}
		});

		mPendingDismisses.add(new PendingDismissData(dismissPosition, dismissView, listItemView));
		animator.start();
	}catch(Exception e){}
	}

	/**
	 * Slide out a mView to the right or left of the list. After the animation has finished, the
	 * mView will be dismissed by calling {@link #performDismiss(View, View, int)}.
	 *
	 * @param mView The mView, that should be slided out.
	 * @param childView The whole mView of the list item.
	 * @param position The item position of the item.
	 * @param toRightSide Whether it should slide out to the right side.
	 */
	private void slideOutView(final View view, final View childView, final int position, boolean toRightSide) {

		try{
		// Only start new animation, if this mView isn't already animated (too fast swiping bug)
		if (StreamsApplication.DEBUG_MODE)
		{
			Log.d("dbg","EnhancedStreamsListFragment::slideOutView") ;
			 
		}

		synchronized(mAnimationLock) {
			try{
			if(mAnimatedViews.contains(view)) {
				return;
			}
			++mDismissAnimationRefCount;
			mAnimatedViews.add(view);


			ViewPropertyAnimator.animate(view)
			.translationX(toRightSide ? mViewWidth : -mViewWidth)
			.alpha(0)
			.setDuration(mAnimationTime)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					performDismiss(view, childView, position);
				}
			});
			}catch(Exception e){e.printStackTrace();}
		}
		}catch(Exception e){}
	}

	/**
	 *
	 *
	 */
	private class PendingDismissData implements Comparable<PendingDismissData> {

		public int position;
		/**
		 * The mView that should get swiped out.
		 */
		public View view;
		/**
		 * The whole list item mView.
		 */
		public View childView;

		PendingDismissData(int position, View view, View childView) {
			this.position = position;
			this.view = view;
			this.childView = childView;
		}

		@Override
		public int compareTo(PendingDismissData other) {
			// Sort by descending position
			return other.position - position;
		}

	}
	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent ev) {

		
			

		try{
		if (!mSwipeEnabled)
		{
			 
			if (ev.getActionMasked() == MotionEvent.ACTION_UP)
			{
				mSwipeEnabled = true ; 
			}
			 
			return false ;  
		}
		mCursor = msStreamAdapter.getCursor();
		int cursorCount = mCursor.getCount() ;
		if (cursorCount== 0)
		{
			setDisableOnClickListener(CLICK_STATE.DONE) ;
			return false ;
		}
		if (mCursor.isBeforeFirst())
		{
			setDisableOnClickListener(CLICK_STATE.DONE) ;
			return false ;
		}
		int colIndexType = mCursor.getColumnIndex(StatusStream.C_TYPE);
		if (mCursor.getString(colIndexType).compareTo(StatusStream.PUBLIC_WEATHER_TYPE) == 0 )
		{   // weather not archive-able 
			mSwipeEnabled = false ; 
			distanceMoved =   0 ; // need this for selection ; 
			setDisableOnClickListener(CLICK_STATE.DONE) ;
			return false ; 
		}

		// Store width of this list for usage of swipe distance detection
		if (mViewWidth < 2) {
			mViewWidth = v.getWidth(); // added v.
		}

		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {

			
			distanceMoved =   ev.getRawX() ;
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "EnhancedStreamsListFragment::PendingDismissData::onTouch::ACTION_DOWN") ;
			}
			if (mSwipePaused) {

				return false ; 
			}

			// TODO: ensure this is a finger, and set a flag

			// Find the child mView that was touched (perform a hit test)
			Rect rect = new Rect();
			int childCount = ((ListView)v).getChildCount(); // the v and listview cast were added in
			int[] listViewCoords = new int[2];
			v.getLocationOnScreen(listViewCoords); // the v and listview cast were added in
			int x = (int) ev.getRawX() - listViewCoords[0];
			int y = (int) ev.getRawY() - listViewCoords[1];
			View child;
			for (int i = ((ListView)v).getHeaderViewsCount(); // added listview cast
					i < childCount; i++) {
				child = ((ListView)v).getChildAt(i); // added cast

				if(child != null) {
					child.getHitRect(rect);
					if (rect.contains(x, y)) {
						// if a specific swiping layout has been giving, use this to swipe.
						if(mSwipingLayout > 0) {
							View swipingView = child.findViewById(mSwipingLayout);
							if(swipingView != null) {
								mSwipeDownView = swipingView;
								mSwipeDownChild = child;

								break;
							}
						}
						// If no swiping layout has been found, swipe the whole child
						mSwipeDownView = mSwipeDownChild = child;

						break;
					}
				}
			}

			if ((mSwipeDownView != null) && (v != null)) {
				// test if the item should be swiped
				int position = ((ListView)v).getPositionForView(mSwipeDownView) - // ListView added 
						((ListView)v).getHeaderViewsCount(); // ListView added 

				mDownX = ev.getRawX();
				mDownY = ev.getRawY();
				mDownPosition = position;

				mCursor = msStreamAdapter.getCursor();
				mCursor.moveToPosition(mDownPosition) ;





				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(ev);

			}

			disableOnClickListener = CLICK_STATE.DONE ; 
			return true;
		}

		case MotionEvent.ACTION_UP: {
			//mSwipeEnabled = true ; 
			distanceMoved =   Math.abs(distanceMoved - ev.getRawX() );
			if (StreamsApplication.DEBUG_MODE)
			{
				Log.d("dbg", "EnhancedStreamsListFragment::PendingDismissData::onTouch::ACTION_UP::distanceMOved=" + distanceMoved) ;
			}
			if (mVelocityTracker == null) {
				break;
			}

			float deltaX = ev.getRawX() - mDownX;
			mVelocityTracker.addMovement(ev);
			mVelocityTracker.computeCurrentVelocity(1000);
			float velocityX = Math.abs(mVelocityTracker.getXVelocity());
			float velocityY = Math.abs(mVelocityTracker.getYVelocity());
			boolean dismiss = false;
			boolean dismissRight = false;
			if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
				dismiss = true;
				dismissRight = deltaX > 0;
			} else if (mMinFlingVelocity <= velocityX && velocityX <= mMaxFlingVelocity
					&& velocityY < velocityX && mSwiping && isSwipeDirectionValid(mVelocityTracker.getXVelocity())
					&& deltaX >= mViewWidth * 0.2f) {
				dismiss = true;
				dismissRight = mVelocityTracker.getXVelocity() > 0;
			}
			boolean netWorkDown = (UpdaterService.getNetStat().
					compareTo(ConnectivityManager.EXTRA_NO_CONNECTIVITY) == 0 ) ? true : false  ;  
			if (netWorkDown && dismiss)
			{ 
				String toastMsg = 
						"ERROR:Network Unavailable\nArchive requires nework connection." ;
				Toast.makeText(this.getActivity(),toastMsg, 
						Toast.LENGTH_LONG).show();
				dismiss = false ; 

			}
			if (dismiss) {


				checkWithServer((Cursor)mSwipeDownView.getTag()) ; 

			} 

			else
			{
				swipeBackToRegularPostion() ;
			}

			break;
		}

		case MotionEvent.ACTION_MOVE: {
			
			if (mVelocityTracker == null || mSwipePaused) {
				break;
			}

			mVelocityTracker.addMovement(ev);
			
			float deltaX = ev.getRawX() - mDownX;
			// Only start swipe in correct direction
			if(isSwipeDirectionValid(deltaX)) {
				ViewParent parent = v.getParent(); // added v
				if(parent != null) {
					// If we swipe don't allow parent to intercept touch (e.g. like NavigationDrawer does)
					// otherwise swipe would not be working.
					parent.requestDisallowInterceptTouchEvent(true);
				}
				if (Math.abs(deltaX) > mSlop) {
					mSwiping = true;
					((ListView)v).requestDisallowInterceptTouchEvent(true); // added cast

					// Cancel ListView's touch (un-highlighting the item)
					MotionEvent cancelEvent = MotionEvent.obtain(ev);
					cancelEvent.setAction(MotionEvent.ACTION_CANCEL
							| (ev.getActionIndex()
									<< MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					//super.onTouchEvent(cancelEvent); // commented out on touch event
				}
			} else {
				// If we swiped into wrong direction, act like this was the new
				// touch down point
				mDownX = ev.getRawX();
				deltaX = 0;
			}

			if (mSwiping) {
				ViewHelper.setTranslationX(mSwipeDownView, deltaX);
				ViewHelper.setAlpha(mSwipeDownView, Math.max(0f, Math.min(1f,
						1f - 2f * Math.abs(deltaX) / mViewWidth)));
				return true;
			}
			break;
		}
		}
		}catch(Exception e){e.printStackTrace();}
		return false;
	}
	/**
	 * 
	 */
	public void swipeBackToRegularPostion()
	{
		try{
		if(mSwiping) {
			// Swipe back to regular position
			ViewPropertyAnimator.animate(mSwipeDownView)
			.translationX(0)
			.alpha(1)
			.setDuration(mAnimationTime)
			.setListener(null);
		}
		mVelocityTracker = null;
		mDownX = 0;
		mSwipeDownView = null;
		mSwipeDownChild = null;
		mDownPosition = AbsListView.INVALID_POSITION;
		mSwiping = false;
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Sets the id of the mView, that should be moved, when the user swipes an item.
	 * Only the mView with the specified id will move, while all other views in the list item, will
	 * stay where they are. This might be usefull to have a background behind the mView that is swiped
	 * out, to stay where it is (and maybe explain that the item is going to be deleted).
	 * If you never call this method (or call it with 0), the whole mView will be swiped. Also if there
	 * is no mView in a list item, with the given id, the whole mView will be swiped.
	 * <p>
	 * <b>Note:</b> This method requires the <i>Swipe to Dismiss</i> feature enabled. Use
	 * {@link #enableSwipeToDismiss()} to enable the feature.
	 *
	 * @param swipingLayoutId The id (from R.id) of the mView, that should be swiped.
	 * @return This {@link de.timroes.android.listview.EnhancedListView}
	 */
	public EnhancedStreamsListFragment setSwipingLayout(int swipingLayoutId) {
		mSwipingLayout = swipingLayoutId;
		return this;
	}


	/**
	 * Checks whether the delta of a swipe indicates, that the swipe is in the
	 * correct direction, regarding the direction set via
	 * {@link #setSwipeDirection(de.timroes.android.listview.EnhancedListView.SwipeDirection)}
	 *
	 * @param deltaX The delta of x coordinate of the swipe.
	 * @return Whether the delta of a swipe is in the right direction.
	 */
	private boolean isSwipeDirectionValid(float deltaX) {

		int rtlSign = 1;
		// On API level 17 and above, check if we are in a Right-To-Left layout
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

		}


		return rtlSign * deltaX < 0;


	}
	/**
	 * send server a message requesting archive for this 
	 * list item 
	 * 
	 * @param mCursor - the mCursor for the archived item , used to get the _id of the stream 
	 */
	void checkWithServer(Cursor cursor)
	{


		try{
		mServerArchiveProgressDialog=new ProgressDialog(mActivity,R.style.SpinnerTheme);  // R.style.SpinnerTheme)


		mServerArchiveProgressDialog.setCancelable(false);

		mServerArchiveProgressDialog.setIcon(R.drawable.streams_beta)  ;

		mServerArchiveProgressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar) ;      //          android.R.style.Widget_ProgressBar_Small);


		BackgroundSpinner backgroundSpinner =  new BackgroundSpinner( ) ;
		backgroundSpinner.execute() ; 
		Intent startUpdaterService= new Intent();
		startUpdaterService.setClass(mActivity,UpdaterService.class);

		int col = cursor.getColumnIndex(StatusStream.C_TYPE) ;
		int bufSize = cursor.getString(col).length() + 1 ;
		CharArrayBuffer buffType = new CharArrayBuffer(bufSize) ;
		cursor.copyStringToBuffer(col, buffType) ;
		String itemType = convertCABToString(buffType) ;

		col = cursor.getColumnIndex(StatusStream.C_ID) ;
		bufSize = cursor.getString(col).length() + 1 ;
		buffType = new CharArrayBuffer(bufSize) ;
		cursor.copyStringToBuffer(col, buffType) ;
		String itemID = convertCABToString(buffType) ;

		startUpdaterService.putExtra(ENHANCED_STREAM_ARCHIVE,  itemType);
		startUpdaterService.putExtra(ENHANCED_STREAM_ARCHIVE_ID,  itemID);

		mActivity.startService(startUpdaterService);
		}catch(Exception e){}

	}
	/**
	 * start a spinner during the archive
	 *
	 */
	protected class BackgroundSpinner extends AsyncTask<Void, Void , Void>
	{
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute()
		{


			mServerArchiveProgressDialog.show() ;

		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			while(true)
			{
				try {
					Thread.    sleep(250) ;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mServerArchiveProgressDialog == null)
				{
					break ; 
				}
			}
			return null;
		}

	}
	/**
	 * @param numArchived - the number of archived items , if 0 then swipe back and toast failure
	 * 														if 1 , then complete the removal of this item from thelistview
	 */
	void processServerArchiveResponse(int numArchived)
	{
		try{

		if (numArchived == 0 )
		{
			swipeBackToRegularPostion() ;
			String toastMsg = 
					"Failed to complete request.\nPlease check network connection\n .... Archive canceled." ;
			Toast.makeText(this.getActivity(),toastMsg, 
					Toast.LENGTH_LONG).show();
		}
		else
		{
			String toastMsg = 
					"Stream successfully archived." ;
			Toast.makeText(this.getActivity(),toastMsg, 
					Toast.LENGTH_LONG).show();


			StreamsApplication sApplication  = (StreamsApplication) this.getActivity().getApplication() ;
			StatusStream statusStream = sApplication.getStatusStream() ;
			msStreamsCursor = statusStream.getStatusUpdates() ; 
			if (msStreamsCursor == null)
			{
				if (StreamsApplication.DEBUG_MODE)
				{
				 	Log.d("dbg", "EnhancedStreamsListFragment::processServerArchiveResponse" + "-- null cursor --") ;
				}
				return ; 
			}
			msStreamAdapter.changeCursor(msStreamsCursor) ;
			msStreamAdapter.notifyDataSetChanged();



			new Handler().post(new Runnable() {
				@Override
				public void run() {

					boolean dismissRight = false;
					if ( (mSwipeDownView == null) || (mSwipeDownChild == null )  )
					{
						return ; 
					}
					slideOutView(mSwipeDownView, mSwipeDownChild, mDownPosition, dismissRight);
				}
			});




		}
		}catch(Exception e){e.printStackTrace();}

	}
	/**
	 * @param s - a chararraybuffer
	 * @return   - the string equivalent
	 */
	public String convertCABToString(CharArrayBuffer s) {
		String value = String.valueOf(s.data[0]);
		try{
		for (int i = 1 ; i <  (s.sizeCopied) ; i++)
		{
			value += s.data[i];
		}
		}catch(Exception e){e.printStackTrace();}
		return value;
	} 

}
