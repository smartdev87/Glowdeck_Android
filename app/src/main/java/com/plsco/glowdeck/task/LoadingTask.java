package com.plsco.glowdeck.task;
import android.os.AsyncTask;


/**
 *
 * Project : GlowDeck/STREAMS
 * FileName: LoadingTask.java
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
 *  This task is run a startup and only on a complete startup.
 *  it will insure that the Splash screen is up for the specified amount of time
 *   
 */
public class LoadingTask extends AsyncTask<Void, Integer, Integer> {
	//
	//
	//
	public enum StreamsLoadState {FIRST_TIME, RESOURCES_LOADED}

	//
	// Globals (Static)
	//
	private static StreamsLoadState mStreamsState = StreamsLoadState.FIRST_TIME ; 
	//
	//  Constants
	//
	private final LoadingTaskFinishedListener finishedListener;
															// callback to listener when task is finished
	/**
	 *  
	 *
	 */
	public interface LoadingTaskFinishedListener {
		void onTaskFinished(); // can also be used to pass param back to listener
	}

	/**
	 * This Loading task 
	 * @param mFinishedListener - the listener that will be told when this task is finished
	 */
	public LoadingTask(LoadingTaskFinishedListener finishedListener) {
		this.finishedListener = finishedListener;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Integer doInBackground(Void... params) {

		if(resourcesDontAlreadyExist()){
			try{
			downloadResources();
			}catch(Exception e){e.printStackTrace();}
		}
		// Return value to post execute
		return 0;
	}

	/**
	 * @return value of mStreamsState
	 */
	private boolean resourcesDontAlreadyExist() {
		return mStreamsState == StreamsLoadState.FIRST_TIME;
	}


	/**
	 *  downloadResources()
	 *  insure that the splash screen is up specified time
	 *  	count * milliSecs
	 */
	private void downloadResources() {
		try{
		// simulated by waiting for "count" secs
		int count = 2;
		int milliSecs = 750 ;
		for (int i = 0; i < count; i++) {

			// Update the progress bar after every step
			int progress = (int) ((i / (float) count) * 100);
			publishProgress(progress);

			// Do some long loading things
			try { 
				Thread.sleep(milliSecs); 
			} catch (InterruptedException ignore) {}
		}
		mStreamsState =  StreamsLoadState.RESOURCES_LOADED ;
		}catch(Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		try{
		finishedListener.onTaskFinished(); // Inform  listener (caller) of completion 
		}catch(Exception e){e.printStackTrace();}
	}

}