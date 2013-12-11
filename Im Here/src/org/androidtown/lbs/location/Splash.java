package org.androidtown.lbs.location;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class Splash extends Activity {
	 @Override
	 	public void onCreate(Bundle savedInstanceState) {
		 	super.onCreate(savedInstanceState);
		 	requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	setContentView(R.layout.splash);
		 	initialize();
	    }
	    private void initialize() {
	        Handler handler = new Handler() {
		        @Override
		        public void handleMessage(Message msg) {
		            finish();
		        }
			};
	   handler.sendEmptyMessageDelayed(0, 2000);	
	   }
	   public void onBackPressed(){}
}
