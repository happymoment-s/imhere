package org.androidtown.lbs.location;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImHereActivity extends Activity implements OnInitListener {
	 // TTS service and initialization constant:
	static TextToSpeech mTTS;
	boolean isInit;
	
	//Network Checking constants:
	static boolean isWifiConn;
	static boolean isMobileConn;
	
	// LBS constants:
	static Geocoder geoCoder;
	static LocationManager manager;
	static GPSListener gpsListener;
	static StringBuffer sb_address;
	static Location lastLocation;
	
	static String tts_notnetworkgps = "네트워크 및 GPS 서비스가 작동하지 않습니다. 서비스를 다시 확인하세요";
	
	// Location constants:
	static Location locationA = new Location("point A");
	static Location locationB = new Location("point B");
	static Location locationC = new Location("point C");	
	static Location oldLocation = new Location("point old"); 	 
	static Double latitude;
	static Double longitude;
	static Double stanDist = 20.0;
	
	// Check box true or false constants:
	static boolean Subway_TF = true;
	static boolean BusStation_TF = true;
	static boolean Disabled_TF = true;
	static boolean CommunityCenter_TF = true;
	static boolean Hospital_TF = true;
	static boolean Bank_TF = true;
	static boolean Department_store_TF = true;
	static boolean Park_TF = true;
	static boolean Search = false;
	
	// BusSearch method use xml:
	String xml;	

	// Test constants:
	TextView preference;		
	final String packageName = "org.androidtown.lbs.location";
	
    // Screen time out 
    private int mPrevTimeout;
    	
	//	static String titlebuffer;
	float reDist = 0;
	StringBuffer ttsBuffer;
	
	public void onCreate(Bundle savedInstanceState) {		
		// Set the splash screen image to display during initialization
		startActivity(new Intent(this, Splash.class));		
		
		super.onCreate(savedInstanceState);
		// Title bar delete
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		// Splash initialize method
		initialize();

		// Checking network (Wifi and 3G)
		new Handler().postDelayed(new Runnable() {
			public void run() {
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo ni_wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		        NetworkInfo ni_mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		        isWifiConn = ni_wifi.isConnected();
		        isMobileConn = ni_mobile.isConnected();
		        Log.i("wifi, 3G", String.valueOf(isWifiConn) + String.valueOf(isMobileConn));
		        if (!isWifiConn && !isMobileConn) {
		        	Toast.makeText(getApplicationContext(), "네트워크가 작동하지 않습니다.", Toast.LENGTH_SHORT).show();
		             mTTS.speak(tts_notnetworkgps, TextToSpeech.QUEUE_FLUSH, null);
		       }
			}
		}, 3000);

		// Set TTS service + set korea language
		mTTS = new TextToSpeech(this, this);
		mTTS.setLanguage(Locale.KOREA);
		
		// LocationService start
		startLocationService();
		
		// Help button
		ImageView helpbtn = (ImageView) findViewById(R.id.ImageHelp);
		helpbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTTS.speak("도움말", TextToSpeech.QUEUE_FLUSH, null);
				Toast.makeText(getApplicationContext(), "도움말", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ImHereActivity.this, Help.class);
				startActivity(intent);
			}
		});
		// Current address button
		ImageView addressbtn = (ImageView) findViewById(R.id.ImageAddress);
		Log.i("Address : ", String.valueOf(sb_address));
		addressbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTTS.speak("현재위치", TextToSpeech.QUEUE_FLUSH, null);
				Toast.makeText(getApplicationContext(), "현재위치", Toast.LENGTH_SHORT).show();
				final String s_address = String.valueOf(sb_address);
				if( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || (!isWifiConn && !isMobileConn) ) {
					mTTS.speak(tts_notnetworkgps, TextToSpeech.QUEUE_FLUSH, null); 
					Toast.makeText(getApplicationContext(), "네트워크, GPS가 작동하지 않습니다.", Toast.LENGTH_SHORT).show();
				} else {
					// Delay 2000ms
					new Handler().postDelayed(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(), s_address, Toast.LENGTH_SHORT).show();
							mTTS.speak(s_address, TextToSpeech.QUEUE_FLUSH, null);
						}
					}, 2000);	
				}
			}
		});
		// Search button
		ImageView searchBtn = (ImageView) findViewById(R.id.ImageSearch);
		searchBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || (!isWifiConn && !isMobileConn) ) {
					mTTS.speak(tts_notnetworkgps, TextToSpeech.QUEUE_FLUSH, null); 
					Toast.makeText(getApplicationContext(), "네트워크, GPS가 작동하지 않습니다.", Toast.LENGTH_SHORT).show();
				} else {
					if(Search == true) {
						Search = false;
						mTTS.speak("주변시설탐색을 중지합니다.", TextToSpeech.QUEUE_FLUSH, null);   
						Toast.makeText(getApplicationContext(), "주변시설탐색 중지", Toast.LENGTH_SHORT).show();
					} else {
						Search = true;
						mTTS.speak("주변시설탐색을 시작합니다.", TextToSpeech.QUEUE_FLUSH, null);   
						Toast.makeText(getApplicationContext(), "주변시설탐색 시작", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		// Preference button
		ImageView preferencebtn = (ImageView)findViewById(R.id.ImagePreference);
	    preferencebtn.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v) {
				mTTS.speak("환경설정", TextToSpeech.QUEUE_FLUSH, null);   
				Toast.makeText(getApplicationContext(), "환경설정", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ImHereActivity.this, Preference.class);
				startActivity(intent);
			}
	    });	    
        // Screen time out 
        setScreenTimeoutOff();
	}
	
	// Splash initialize
	private void initialize() {
		InitializationRunnable init = new InitializationRunnable();
		new Thread(init).start();
	}
	class InitializationRunnable implements Runnable {
	    public void run() {
			for (int i=0; i<5;i++) {
				try {
					Thread.sleep(1000);					
					Log.d("SPLASH", "------------- initialize..........");
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	    }
	}
	// (End) splash initialize
	
	private void startLocationService() {
		// get manager instance
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// set listener
		gpsListener = new GPSListener();
		long minTime = 10000;
		float minDistance = 0;
		geoCoder = new Geocoder(this, Locale.KOREAN);
		manager.requestLocationUpdates
			(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
		manager.requestLocationUpdates
			(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
		// get last known location first
		try {
			lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(lastLocation == null){
				lastLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (lastLocation != null) {
				latitude = lastLocation.getLatitude();
				longitude = lastLocation.getLongitude();

				oldLocation.setLatitude(latitude);
				oldLocation.setLongitude(longitude);
			} else {
				oldLocation.setLatitude(0.0);
				oldLocation.setLongitude(0.0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i("Location Service", "Location Service started.\nyou can test using DDMS.");
//		Toast.makeText(getApplicationContext(), 
//				"Location Service started.\nyou can test using DDMS.", Toast.LENGTH_SHORT).show();
	}

	private class GPSListener implements LocationListener {
		public void onLocationChanged(Location location) {
			// capture location data sent by current provider
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			sb_address = new StringBuffer();
			try {
				List<Address> addresses;
				addresses = geoCoder.getFromLocation(latitude, longitude, 1);
				for (Address addr : addresses) {
					int index = addr.getMaxAddressLineIndex();
					for (int i = 0; i <= index; i++) {
						sb_address.append(addr.getAddressLine(i));
//						sb_address.append(" ");
					}
//					sb_address.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			locationA.setLatitude(latitude);
			locationA.setLongitude(longitude);	
			
			if (Search == true && oldLocation.distanceTo(locationA) >= reDist) {
//				Toast.makeText(getApplicationContext(), "일정거리이상 움직임", Toast.LENGTH_SHORT).show();
				PrefSearch();
				oldLocation.set(locationA);
			}
		}
		public void onProviderDisabled(String provider) {
		}
		public void onProviderEnabled(String provider) {
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	
	private void PrefSearch() {
		Log.i("PrefSearch State", "PrefSearch Start");
//		Toast.makeText(getApplicationContext(), "주변검색 시작", Toast.LENGTH_SHORT).show();
		ttsBuffer = new StringBuffer();
		SharedPreferences pref = getSharedPreferences(packageName + "_preferences", MODE_PRIVATE);
		String search1[] = { "Subway", "BusStation", "Disabled", "CommunityCenter", "Hospital", "Bank", "Department_store", "Park" };
		String search2[] = { "지하철", "버스정류장", "장애인시설", "주민센터", "병원", "은행", "백화점", "공원" };
		
		for (int i = 0; i < search1.length; i++) {
			if (pref.getBoolean("checkbox_" + search1[i], false) == true) {
				if (i == 1) {
					busSearch(Double.toString(stanDist));					
				} else {
					startSearch(search2[i]);
				}
			}
		}
		if (ttsBuffer.length() != 0) {
			Log.i("ttsBuffer",ttsBuffer.toString());
			mTTS.speak("주변에 " + ttsBuffer + " 있습니다", TextToSpeech.QUEUE_FLUSH, null);
			Toast.makeText(getApplicationContext(), "주변에 " + ttsBuffer + " 있습니다", Toast.LENGTH_SHORT).show();
		}
		Log.i("PrefSearch State", "PrefSearch End");
//		Toast.makeText(getApplicationContext(), "주변검색 끝남", Toast.LENGTH_SHORT).show();
	}
	
	void startSearch(String search) {
		StringBuilder responseBuilder = new StringBuilder();
		try {
			// search URL creation
			URL url = new URL("http://ajax.googleapis.com/ajax/services/search/local?v=1.0&q=" + URLEncoder.encode(search, "UTF-8") + "&sll="
					+ latitude + "," + longitude + "&rsz=8" + "&hl=ko");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				responseBuilder.append(inputLine);
			}
			in.close();
			// JSONObject(JavaScript Object Notation) use
			JSONObject json = new JSONObject(responseBuilder.toString());
			json = json.getJSONObject("responseData");
			JSONArray jarray = json.getJSONArray("results");
//			String result = new String();
			Log.d("jaary.length", Integer.toString(jarray.length()));

			for (int i = 0; i < jarray.length(); i++) {
				// ------ Object to obtain the results by the results ------
				JSONObject jtmp = jarray.getJSONObject(i);
				String title = jtmp.getString("titleNoFormatting");

				// ------ Full address parsing ------
				// Get addressLines(key) values
				String addr_tmp = jtmp.getString("addressLines");
				// ["...."] Only get middle strings bring
				String[] addr_tmp2 = addr_tmp.split("\"");
				// { "[" , ".....", "]" } Only get middle strings bring
				String addr = addr_tmp2[1];

				// ------ Get latitude and longitude using the address obtained
				// Get a single address information
				List<Address> addrList = geoCoder.getFromLocationName(addr, 1);
				// Brings the list available in the Address Object
				Address adr_tmp = addrList.get(0);
				String[] location = new String[2];

				location[0] = Double.toString(adr_tmp.getLatitude());
				location[1] = Double.toString(adr_tmp.getLongitude());
				locationB.setLatitude(adr_tmp.getLatitude());
				locationB.setLongitude(adr_tmp.getLongitude());

				double dist = locationA.distanceTo(locationB);

				if (dist < stanDist) {
					ttsBuffer.append( title +" ");
				}	
			}
		} catch (MalformedURLException me) {
			me.printStackTrace();
		} catch (UnsupportedEncodingException ue) {
			ue.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void busSearch(String radius) {
		String serviceKey = "a8AYjPDJmMstBd%2Ba4sawsFiSZ3s94XF4M9fE5f4Z0%2BsYFfhPUnsCAfk77rrTugtkTI1yPO4f3L4zvMv9KBHGUg%3D%3D";
		StringBuffer sBuffer = new StringBuffer();
		try {
			String urlAddr = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?serviceKey=" 
					+ serviceKey + "&tmX=" + longitude + "&tmY=" + latitude + "&radius=" + radius;				
			URL url = new URL(urlAddr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn != null) {
				conn.setConnectTimeout(2000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Reading from a stream object for the server
					InputStreamReader isr = new InputStreamReader(conn.getInputStream());
					// To read the line-by-line, used BufferReader
					BufferedReader br = new BufferedReader(isr);
					// ReadLine
					while (true) {
						String line = br.readLine();
						if (line == null) {
							break;
						}
						sBuffer.append(line);
					}
					br.close();
					conn.disconnect();
				}
			}
			// Add the result to the variable
			xml = sBuffer.toString(); 
		} catch (Exception e) {
			Log.e("downloading error", e.getMessage());
		}
		parse();
	}

	// xml parsing method
	public void parse() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			// Transformed into xml to InputStream
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			//  Import w3c dom (document, element)
			Document doc = documentBuilder.parse(is);
			Element element = doc.getDocumentElement();
			// Determine the name of the tag to be read
			NodeList items1 = element.getElementsByTagName("gpsX");
			NodeList items2 = element.getElementsByTagName("gpsY");
			NodeList items3 = element.getElementsByTagName("stationNm");
			// The number of read data
			int n = items1.getLength();
			// Reading all of the data
			for (int i = 0; i < n; i++) {
				// String is the index number of the transmit.
				Node item1 = items1.item(i);
				Node item2 = items2.item(i);
				Node item3 = items3.item(i);

				Node text1 = item1.getFirstChild();
				Node text2 = item2.getFirstChild();
				Node text3 = item3.getFirstChild();
				// Reading a string from the node
				String gpsX = text1.getNodeValue();
				String gpsY = text2.getNodeValue();
				String stationNm = text3.getNodeValue();

				locationC.setLongitude(Double.valueOf(gpsX));
				locationC.setLatitude(Double.valueOf(gpsY));
				
				double dist = locationA.distanceTo(locationC);
				if (dist < stanDist) {
					ttsBuffer.append("버스정류장 "  +  stationNm +" ");
				}
			}
		} catch (Exception e) {
			Log.e("parsing error", e.getMessage());
		}
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		useOfPreferences();
	}
	
	void useOfPreferences(){
		SharedPreferences pref = getSharedPreferences(packageName+"_preferences", MODE_PRIVATE);
		// constants:
		boolean bl_Subway = pref.getBoolean("checkbox_Subway", false);
		boolean bl_BusStation = pref.getBoolean("checkbox_BusStation", false);
		boolean bl_Disabled = pref.getBoolean("checkbox_Disabled", false);
		boolean bl_CommunityCenter = pref.getBoolean("checkbox_CommunityCenter", false);
		boolean bl_Hospital = pref.getBoolean("checkbox_Hospital", false);
		boolean bl_Park = pref.getBoolean("checkbox_Park", false);
		boolean bl_Bank = pref.getBoolean("checkbox_Bank", false);
		boolean bl_Department_store = pref.getBoolean("checkbox_Department_store", false);
		
		// Checking all value false
		if(bl_Subway==false && bl_BusStation==false && bl_Disabled==false && bl_CommunityCenter==false
				&& bl_Hospital==false && bl_Park==false && bl_Bank==false && bl_Department_store==false ) {
			mTTS.speak("환경설정에서 주변시설을 체크해주시기 바랍니다." , TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	 // TextToSpeech Initialization method
	@Override
	public void onInit(int status) {
		// TextToSpeech service Initialization
		isInit = status == TextToSpeech.SUCCESS;
		Log.i("TTS service", "TTS_success");
//		Toast.makeText(this, "TTS success", Toast.LENGTH_SHORT).show(); 
	}
	
	@Override
	protected void onDestroy() {
		manager.removeUpdates(gpsListener);
		if (mTTS != null) {
			mTTS.stop();
			mTTS.shutdown();
		}
		super.onDestroy();
		restoreScreenTimeout();
	}	
	
	private void restoreScreenTimeout() {
		ContentResolver cr = getContentResolver();
		if(mPrevTimeout != 0 ) {
			Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, mPrevTimeout);
		}
	}
	private void setScreenTimeoutOff() {
		ContentResolver cr = getContentResolver();
		mPrevTimeout = 0;
		try {
			mPrevTimeout = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, -1);
	}
}

