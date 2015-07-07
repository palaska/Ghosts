package com.virginia.edu.cs2110.ghosts;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class MainActivity extends FragmentActivity implements LocationListener {

	private GoogleMap theMap;
	private int userIcon1, ghostIcon1, difficulty;
	private LocationManager locMan;
	private Marker userMarker;
	private double myLat;
	private double myLng;
	private ArrayList<Marker> ghosts;
	private int killCount;
	private Toast ghostNear;
	private int distance;
	private static final String PREFS_NAME = "Kill_Count";
	private String diffStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Define all of the variables
		if (this.getIntent().getStringExtra("gameType").equals("savedGame")){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		killCount = settings.getInt("saved_Count", -1);

		SharedPreferences diff_setting = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		difficulty = diff_setting.getInt("difficulty", -1);
		} else {
			difficulty = this.getIntent().getIntExtra("difficulty", 0);
			killCount = 0;
		}
		userIcon1 = R.drawable.character1;
		ghostIcon1 = R.drawable.ghost1;
		if (difficulty == 5) {
			distance = 50;
			diffStr = "Easy";
		} else if (difficulty == 7) {
			distance = 35;
			diffStr = "Medium";
		} else {
			distance = 15;
			diffStr = "Hard";
		}
		ghosts = new ArrayList<Marker>();
		ghostNear = Toast.makeText(getApplicationContext(), "A ghost is near!", Toast.LENGTH_SHORT);

		if (theMap == null) {
			// map not instantiated yet
			theMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		if (theMap != null) {
			// ok - proceed, set up the map and location manager
			theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			theMap.setOnMarkerClickListener(new myOMCL(ghosts, killCount));
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.setText("Score: 0" + killCount + "  Difficulty: " + diffStr);
			locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
			Location lastLoc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			// set the user location and marker, and move the camera to the player
			
			myLat = lastLoc.getLatitude();
			myLng = lastLoc.getLongitude();
			LatLng lastLatLng = new LatLng(myLat, myLng);
			userMarker = theMap.addMarker(new MarkerOptions()
					.position(lastLatLng)
					.title("You are here")
					.icon(BitmapDescriptorFactory.fromResource(userIcon1))
					.snippet("Your last recorded location"));
			theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat, myLng), 18.0f));

			//Spawn ghosts!
			
			spawnGhosts(difficulty - killCount%difficulty);
			isGhostNear();
		}
	}

	/**
	 * onResume
	 * Resumes requesting location updates
	 */
	
	@Override
	protected void onResume() {
		super.onResume();
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
	}

	/**
	 * onPause
	 * Pauses the location updater while the app is paused
	 */
	
	@Override
	protected void onPause() {
		super.onPause();
		locMan.removeUpdates(this);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.putInt("difficulty", difficulty);
		editor.putInt("saved_Count", killCount);
		editor.commit();
	}
	
	@Override
	public void onBackPressed() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.putInt("saved_Count", killCount);
		editor.putInt("difficulty", difficulty);
		editor.commit();
		super.onBackPressed();
	}
	
	

	/**
	 * onLocationChanged
	 * updates the lat and lng of the player, sets the marker, and checks
	 * to see if ghosts are near
	 */
	
	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		myLat = lat;
		double lng = location.getLongitude();
		myLng = lng;
		LatLng coordinate = new LatLng(myLat, myLng);
		userMarker.setPosition(coordinate);
		theMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
		isGhostNear();
	}
	
	/**
	 * isGhostNear
	 * Takes the players coordinates and compares it to the coordinates of
	 * all the ghosts
	 */
	
	public void isGhostNear(){
		for (Marker m : ghosts) {
			if (findDistanceToGhost(m) < distance) {
				if (findDistanceToGhost(m) < 8){
					startActivity(new Intent(this, GameOver.class));
				}
				if (!ghostNear.getView().isShown()){
					ghostNear.show();
				}
			}
		}
	}

	/**
	 * findDistanceToGhost
	 * @param m the ghost we want to know the distance to
	 * @return the distance to this ghost
	 */
	
	public double findDistanceToGhost(Marker m) {
		double markerLat = m.getPosition().latitude;
		double markerLng = m.getPosition().longitude;
		double dist = Math.sqrt(Math.pow((markerLat - myLat) * 110996, 2) + Math.pow((markerLng - myLng) * 87832, 2));
		return dist;
	}

	/**
	 * myOMCL
	 * @author barispalaska
	 * called when a marker (ghost) is clicked on
	 */
	
	public void increaseKillCount(){
		killCount++;
	}
	
	public void spawnGhosts(int d){
		if (ghosts.size() == 0){
			for (int i = 0; i < d; i++) {
				double x1 = myLat + (2 * Math.random() - 1) * 0.001;
				double y1 = myLng + (2 * Math.random() - 1) * 0.001;
				LatLng position = new LatLng(x1, y1);
				Marker g = theMap.addMarker(new MarkerOptions()
								.position(position)
								.title("")
								.icon(BitmapDescriptorFactory.fromResource(ghostIcon1)));
				ghosts.add(g);
			}
		}
	}
	
	public class myOMCL implements OnMarkerClickListener {

		ArrayList<Marker> ghosts;
		int killCount;

		public myOMCL(ArrayList<Marker> m, int k) {
			ghosts = m;
			killCount = k;
		}

		/**
		 * onMarkerClick
		 * decides if you are close enough to kill the ghost
		 * displays a toast stating the outcome
		 */

		@Override
		public boolean onMarkerClick(Marker marker) {
			boolean removed = false;
			for (Marker m : ghosts) {
				if (m.equals(marker)) {
					if (findDistanceToGhost(m) > distance) {
						Toast notCloseEnough = Toast.makeText(
								getApplicationContext(),
								"You are not close enough to this ghost.",
								Toast.LENGTH_SHORT);
						notCloseEnough.show();
					} else {
						killCount++;
						increaseKillCount();
						TextView textView = (TextView) findViewById(R.id.textView1);
						textView.setText("Score: 0" + killCount + "  Difficulty: " + diffStr);
						m.remove();
						removed = true;
						Toast killGhost = Toast.makeText(
								getApplicationContext(), "Your kill count is: " + killCount, Toast.LENGTH_SHORT);
						killGhost.show();
					}
				}
			}
			if (removed) {
				ghosts.remove(marker);
			}
			spawnGhosts(difficulty);
			return false;
		}
	}
	
	/**
	 * onProviderDisabled
	 * onProviderEnabled
	 * onStatusChanged
	 * These methods are not used.
	 */

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
