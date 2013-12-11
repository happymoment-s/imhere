package org.androidtown.lbs.location;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

public class Preference extends PreferenceActivity implements OnPreferenceClickListener {
	final String packageName = "org.androidtown.lbs.location";	
	// Check box true or false constants:
	static boolean Subway_TF = true;
	static boolean BusStation_TF = false;
	static boolean Disabled_TF = false;
	static boolean CommunityCenter_TF = false;
	static boolean Hospital_TF = false;
	static boolean Bank_TF = false;
	static boolean Department_store_TF = false;
	static boolean Park_TF = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);        
        
        // Load the preferences from an XML resource        
        addPreferencesFromResource(R.xml.preference);
        setTheme(R.style.preferencesTheme);
        
        // OnpreferenceClickListener creation
        CheckBoxPreference cbp_Subway = (CheckBoxPreference)findPreference("checkbox_Subway");
        cbp_Subway.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_BusStation = (CheckBoxPreference)findPreference("checkbox_BusStation");
        cbp_BusStation.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_Disabled = (CheckBoxPreference)findPreference("checkbox_Disabled");
        cbp_Disabled.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_CommunityCenter = (CheckBoxPreference)findPreference("checkbox_CommunityCenter");
        cbp_CommunityCenter.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_Hospital = (CheckBoxPreference)findPreference("checkbox_Hospital");
        cbp_Hospital.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_Park = (CheckBoxPreference)findPreference("checkbox_Park");
        cbp_Park.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_Bank = (CheckBoxPreference)findPreference("checkbox_Bank");
        cbp_Bank.setOnPreferenceClickListener(this);
        CheckBoxPreference cbp_Department_store = (CheckBoxPreference)findPreference("checkbox_Department_store");
        cbp_Department_store.setOnPreferenceClickListener(this);
    }

	@Override
	public boolean onPreferenceClick(android.preference.Preference preference) {	
		SharedPreferences pref = getSharedPreferences(packageName + "_preferences", MODE_PRIVATE);
		// constants:
		boolean bl_Subway = pref.getBoolean("checkbox_Subway", true);
		boolean bl_BusStation = pref.getBoolean("checkbox_BusStation", false);
		boolean bl_Disabled = pref.getBoolean("checkbox_Disabled", false);
		boolean bl_CommunityCenter = pref.getBoolean("checkbox_CommunityCenter", false);
		boolean bl_Hospital = pref.getBoolean("checkbox_Hospital", false);
		boolean bl_Park = pref.getBoolean("checkbox_Park", false);
		boolean bl_Bank = pref.getBoolean("checkbox_Bank", false);
		boolean bl_Department_store = pref.getBoolean("checkbox_Department_store", false);
		// Subway
		if(bl_Subway == true && Subway_TF == false) {
			Subway_TF = true;
			ImHereActivity.mTTS.speak("지하철역 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "지하철역 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_Subway == false && Subway_TF == true) {
			Subway_TF = false;
			ImHereActivity.mTTS.speak("지하철역 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "지하철역 해제", Toast.LENGTH_SHORT).show();
		}
		// BusStation	
		if(bl_BusStation == true && BusStation_TF == false) {
			BusStation_TF = true;
			ImHereActivity.mTTS.speak("버스정류장 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "버스정류장 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_BusStation == false && BusStation_TF == true) {
			BusStation_TF = false;
			ImHereActivity.mTTS.speak("버스정류장 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "버스정류장 해제", Toast.LENGTH_SHORT).show();
		}
		// Disabled	
		if(bl_Disabled == true && Disabled_TF == false) {
			Disabled_TF = true;
			ImHereActivity.mTTS.speak("장애인시설 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "장애인시설 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_Disabled == false && Disabled_TF == true) {
			Disabled_TF = false;
			ImHereActivity.mTTS.speak("장애인시설 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "장애인시설 해제", Toast.LENGTH_SHORT).show();
		}
		// CommunityCenter	
		if(bl_CommunityCenter == true && CommunityCenter_TF == false) {
			CommunityCenter_TF = true;
			ImHereActivity.mTTS.speak("주민센터(동사무소) 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "주민센터(동사무소) 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_CommunityCenter == false && CommunityCenter_TF == true) {
			CommunityCenter_TF = false;
			ImHereActivity.mTTS.speak("주민센터(동사무소) 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "주민센터(동사무소) 해제", Toast.LENGTH_SHORT).show();
		}
		// Hospital	
		if(bl_Hospital == true && Hospital_TF == false) {
			Hospital_TF = true;
			ImHereActivity.mTTS.speak("병원 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "병원 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_Hospital == false && Hospital_TF == true) {
			Hospital_TF = false;
			ImHereActivity.mTTS.speak("병원 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "병원 해제", Toast.LENGTH_SHORT).show();
		}
		// Park	
		if(bl_Park == true && Park_TF == false) {
			Park_TF = true;
			ImHereActivity.mTTS.speak("공원 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "공원 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_Park == false && Park_TF == true) {
			Park_TF = false;
			ImHereActivity.mTTS.speak("공원 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "공원 해제", Toast.LENGTH_SHORT).show();
		}
		// Bank	
		if(bl_Bank == true && Bank_TF  == false) {
			Bank_TF = true;
			ImHereActivity.mTTS.speak("은행 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "은행 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_Bank == false && Bank_TF == true) { 
			Bank_TF = false;
			ImHereActivity.mTTS.speak("은행 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "은행 해제", Toast.LENGTH_SHORT).show();
		}
		// Department store	
		if(bl_Department_store == true && Department_store_TF == false) {
			Department_store_TF = true;
			ImHereActivity.mTTS.speak("백화점 선택", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "백화점 선택", Toast.LENGTH_SHORT).show();
		} else if(bl_Department_store == false && Department_store_TF == true) {
			Department_store_TF = false;
			ImHereActivity.mTTS.speak("백화점 해제", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "백화점 해제", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    ImHereActivity.mTTS.speak("메인페이지 입니다", TextToSpeech.QUEUE_FLUSH, null);
		return super.dispatchKeyEvent(event);
	}
}
