package org.androidtown.lbs.location;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Help extends Activity {
	private String help = "도움말 버튼입니다.\n현재 보여지고 있는 페이지입니다.";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.help);
	    
	    new Handler().postDelayed(new Runnable() {
			public void run() {
			    ImHereActivity.mTTS.speak(help, TextToSpeech.QUEUE_FLUSH, null);
			}
		}, 2000);
	    
	    
	    // Help button
 		ImageView helpbtn = (ImageView) findViewById(R.id.ImageHelp);
 		helpbtn.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
			    TextView helptxt = (TextView)findViewById(R.id.TextHelp);
			    String S_helptxt = helptxt.getText().toString();
 				ImHereActivity.mTTS.speak(S_helptxt, TextToSpeech.QUEUE_FLUSH, null);
 			}
 		});
 		// Current address button
 		ImageView addressbtn = (ImageView) findViewById(R.id.ImageAddress);
 		addressbtn.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
			    TextView addresstxt = (TextView)findViewById(R.id.TextAddress);
			    String S_addresstxt = addresstxt.getText().toString();
 				ImHereActivity.mTTS.speak(S_addresstxt, TextToSpeech.QUEUE_FLUSH, null);
 			}
 		});
 		// Search  button
 		ImageView searchbtn = (ImageView) findViewById(R.id.ImageSearch);
 		searchbtn.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
			    TextView searchtxt = (TextView)findViewById(R.id.TextSearch);
			    String S_searchtxt = searchtxt.getText().toString();
 				ImHereActivity.mTTS.speak(S_searchtxt, TextToSpeech.QUEUE_FLUSH, null);
 			}
 		});
 		// Preference  button
 		ImageView preferencebtn = (ImageView) findViewById(R.id.ImagePreference);
 		preferencebtn.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
			    TextView preferencetxt = (TextView)findViewById(R.id.TextPreference);
			    String S_preferencetxt = preferencetxt.getText().toString();
 				ImHereActivity.mTTS.speak(S_preferencetxt, TextToSpeech.QUEUE_FLUSH, null);
 			}
 		});
 		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    ImHereActivity.mTTS.speak("메인페이지 입니다", TextToSpeech.QUEUE_FLUSH, null);
		return super.dispatchKeyEvent(event);
	}
}
