package com.plsco.glowdeck.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.plsco.glowdeck.auth.StreamsUser;
import com.plsco.glowdeck.task.VerifyCredentialsTask;
import com.plsco.glowdeck.R;

import java.util.Arrays;

/**
*
* Project : GlowDeck/STREAMS
* FileName: NewsSettingsAdapter.java
*
* Copyright 2014. PLSCO, Inc. All rights reserved.
*
*/
/**
* History
* 11/1/14 - prepare for Google Play store
*/
/**
* @author Joe Diamand 
* @version 1.0   08/27/14
* 
*/

/**
* The NewsSettingsAdapter extends BaseAdapter
*  
*  
*
*/
public class NewsSettingsAdapter extends BaseAdapter{
	//
	//	Globals
	//
	String [] 									mResult;
	Context 									mContext;
	 int [] 									mImageId;
	 int [] 									mImageId2;
	private  LayoutInflater 					mInflater=null;

	// statics
	static boolean 								msOnClickAddGuardRowView = false; 
	static boolean 								msOnClickDelGuardRowView = false; 
	//
	//
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResult.length;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView ;
		try{
		if (rowView == null)
		{

			rowView = mInflater.inflate(R.layout.activity_individual_setting_listview, null);
			Holder holder=new Holder();
			holder.tv=(TextView) rowView.findViewById(R.id.textview_ind_set);

			holder.img=(ImageView) rowView.findViewById(R.id.icon_indset); 
			holder.img2=(ImageView) rowView.findViewById(R.id.icon_indset_del); 
			rowView.setTag(holder);
		}
		Holder holder = (Holder) rowView.getTag();
		
		final int fPosition = position ; 
		
		holder.img2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (msOnClickDelGuardRowView)
				{
					return ;
				}
				else 
				{
					msOnClickDelGuardRowView = true ; 
				}
				AlertDialog.Builder completeDialog = new AlertDialog.Builder(mContext);

				Spannable span = new SpannableString("\nRemove Item\nAre you sure you want to remove\n"
						+ mResult[fPosition] + "?");
				int spanLen = span.length();

				span.setSpan(new RelativeSizeSpan(0.7f), 13, spanLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				TextView resultMessage = new TextView(mContext);
				resultMessage.setTextSize(18);
				resultMessage.setText(span);
				resultMessage.setGravity(Gravity.CENTER);
				completeDialog.setView(resultMessage);
				completeDialog.setCancelable(false) ;
				completeDialog.setPositiveButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Yes</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						removeNewsKeyword(   fPosition) ;
						msOnClickDelGuardRowView = false  ;
						dialog.dismiss(); 

					}
				});

				completeDialog.setNegativeButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Cancel</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						msOnClickDelGuardRowView = false ; 
						dialog.dismiss(); 

					}
				});

				completeDialog.show() ;


			}
		});

		holder.tv.setText(mResult[position]);


		holder.img.setImageResource(mImageId[position]); 
		if (mImageId2[position] == -1)
		{
			holder.img2.setVisibility(View.INVISIBLE) ;
		}
		else
		{
			holder.img2.setImageResource(mImageId2[position]); 
			holder.img2.setVisibility(View.VISIBLE) ;
		}


		rowView.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				//final View fV = v ; 
				
				if (msOnClickAddGuardRowView)
				{
					return ;
				}
				else
				{
					msOnClickAddGuardRowView = true ; 
				}

				AlertDialog.Builder completeDialog = new AlertDialog.Builder(mContext);

				Spannable span;
				if (mImageId2[fPosition] == -1)
				{
					span = new SpannableString("\nAdd Keyword\nEnter a news keyword\n");
					int spanLen = span.length();
					span.setSpan(new RelativeSizeSpan(0.7f), 13, spanLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				else
				{
					span = new SpannableString("\nEdit Keyword\nEnter a news keyword\n");
					int spanLen = span.length();
					span.setSpan(new RelativeSizeSpan(0.7f), 14, spanLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 1, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				

				TextView resultMessage = new TextView(mContext);
				resultMessage.setTextSize(18);
				resultMessage.setText(span);
				resultMessage.setGravity(Gravity.CENTER);

				final EditText input = new EditText(mContext);

				input.setSingleLine() ;
				if (mImageId2[fPosition] != -1)
				{
					input.setText(mResult[fPosition]) ;
					input.setSelection(mResult[fPosition].length());
				}

				LinearLayout childLayout = new LinearLayout(mContext);
				childLayout.setOrientation(LinearLayout.VERTICAL);

				childLayout.addView(resultMessage);  
				childLayout.addView(input); 

				completeDialog.setView(childLayout);
				childLayout.setPadding(40, 0,40, 0);

				completeDialog.setCancelable(false) ;
				completeDialog.setPositiveButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Done</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString() ;
						// new keyword, is it an add or an edit ?
						if (mImageId2[fPosition] == -1)
						{
							addNewNewsKeyword(value) ;
						}
						else
						{

							editNewsKeyword(value,fPosition) ;

						}
						msOnClickAddGuardRowView = false ; 
						dialog.dismiss(); 
						//mLoginInProgress = false ;
					}
				});

				completeDialog.setNegativeButton(Html.fromHtml(
						"<font  color=\"#0088ff\"><b>Cancel</></font>"), 
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						msOnClickAddGuardRowView = false ; 
						dialog.dismiss(); 
						//mLoginInProgress = false ;
					}
				});

				completeDialog.show() ;

			}
		});  
		}catch(Exception e){e.printStackTrace();}
		return rowView;
	} 
	/**
	 * @param position - in the listview
	 */
	void removeNewsKeyword( int position)
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		String[] newsKeyWord = streamsUser.getNews().getKeywords() ;


		streamsUser.getModifiedSettings().setNewsSettingsModified(true) ;

		int numKeywords = newsKeyWord.length ;
		String[] newNewsKeywords = new String[numKeywords -1] ; // ok if zero

		for (int i = 0 ; i < position; i++ )
		{
			newNewsKeywords[i] = newsKeyWord[i] ;
		}
		for (int i = position+1 ; i < numKeywords  ; i++ )
		{
			newNewsKeywords[i-1] = newsKeyWord[i] ;
		}
		streamsUser.getNews().setKeywords(newNewsKeywords) ;


		int sizeResult  = mResult.length ;
		String[] newResult  = new String[sizeResult  -1] ;  

		for (int i = 0 ; i < position ; i++)
		{
			newResult [i] = mResult[i] ;
		}
		for (int i = position +1  ; i < sizeResult; i++)
		{
			newResult [i-1] = mResult[i] ;
		}
		mResult = newResult ; 
		int imageSize = mImageId.length ;

		int [] newImageId =  new int[imageSize-1] ;



		for (int i = 0 ; i < position ; i++)
		{
			newImageId [i] = mImageId[i] ;

		}
		for (int i = position +1   ; i < imageSize; i++)
		{
			newImageId [i-1] = mImageId[i] ;
		}
		mImageId = newImageId ; 

		int image2Size = mImageId2.length ;
		int [] newImageId2 = new int[image2Size-1] ;
		for (int i = 0 ; i < position  ; i++)
		{

			newImageId2 [i] = mImageId2[i] ;
		}
		for (int i = position + 1   ; i < image2Size; i++)
		{

			newImageId2 [i-1] = mImageId2[i] ;

		}
		mImageId2 = newImageId2 ; 

		NewsSettingsAdapter newsSettingsAdapter =
				(NewsSettingsAdapter) IndividualSettingsActivity.mIndividualSettingsLV.getAdapter() ;
		newsSettingsAdapter.notifyDataSetChanged();
		SettingsTopFragment.settingsAdapter.notifyDataSetChanged() ;
		}catch(Exception e){e.printStackTrace();}
	}




	/**
	 * @param value - the updated keyword
	 * @param position - the position in the list
	 */
	void editNewsKeyword(String value, int position)
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		String[] newsKeyWord = streamsUser.getNews().getKeywords() ;
		newsKeyWord[position] = value ;

		streamsUser.getModifiedSettings().setNewsSettingsModified(true) ;
		NewsSettingsAdapter newsSettingsAdapter =
				(NewsSettingsAdapter) IndividualSettingsActivity.mIndividualSettingsLV.getAdapter() ;
		mResult[position] = value ;
		newsSettingsAdapter.notifyDataSetChanged();
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * @param value - the news keyword to add
	 */
	void addNewNewsKeyword(String value) 
	{
		try{
		StreamsUser streamsUser = VerifyCredentialsTask.getStreamsUser() ;
		String[] newsKeyWord = streamsUser.getNews().getKeywords() ;

		int keywordSize = newsKeyWord.length  + 1;

		String[] newNewsKeywords = new String[keywordSize] ;
		newNewsKeywords = Arrays.copyOf(newsKeyWord, keywordSize ) ;
		newNewsKeywords[keywordSize-1] = value ;
		streamsUser.getModifiedSettings().setNewsSettingsModified(true) ;
		streamsUser.getNews().setKeywords(newNewsKeywords) ;

		int newArraySize = mResult.length  + 1;
		String[] newResult = new String[newArraySize] ;
		newResult = Arrays.copyOf(mResult, newArraySize ) ;
		newResult[newArraySize-1] = mResult[newArraySize-2] ; 
		newResult[newArraySize-2] = value ; 
		mResult = newResult ; 

		int [] newImageId = new int[newArraySize] ;

		newImageId = Arrays.copyOf(mImageId, newArraySize) ;

		newImageId[newArraySize-2] = R.drawable.news_on; 

		newImageId[newArraySize-1] = mImageId[newArraySize-2] ; 

		mImageId = newImageId ; 

		int [] newImageId2 = new int[newArraySize] ;
		newImageId2 = Arrays.copyOf(mImageId2, newArraySize) ;

		newImageId2[newArraySize-2] =  R.drawable.red_x; 
		newImageId2[newArraySize-1] = mImageId2[newArraySize-2] ; 

		mImageId2 = newImageId2 ; 



		NewsSettingsAdapter newsSettingsAdapter =
				(NewsSettingsAdapter) IndividualSettingsActivity.mIndividualSettingsLV.getAdapter() ;
		newsSettingsAdapter.notifyDataSetChanged();
		SettingsTopFragment.settingsAdapter.notifyDataSetChanged() ;
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * 
	 * Constructor - set up global values for invocation
	 * @param individualSettingsActivity = mContext
	 * @param prgmNameList
	 * @param prgmImages
	 * @param prgmImages2
	 */
	public NewsSettingsAdapter(IndividualSettingsActivity individualSettingsActivity,
			String[] prgmNameList, 
			int[] prgmImages,
			int[] prgmImages2) {
		mResult=prgmNameList;
		mContext=individualSettingsActivity;
		mImageId=prgmImages;
		mImageId2=prgmImages2;
		mInflater = ( LayoutInflater )mContext.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	public class Holder
	{
		TextView tv;
		ImageView img;
		ImageView img2 ;
	}
}
