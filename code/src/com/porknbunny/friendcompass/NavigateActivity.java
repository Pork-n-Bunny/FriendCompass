package com.porknbunny.friendcompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NavigateActivity extends FragmentActivity implements LocationListener, SensorEventListener {
    private static final String TAG = "friendCompass.NavigateActivity";
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private Criteria criteria;
    private TextView distBiz, distFriend, bearBiz, bearFriend, bizName,bizAddress,bizSuburb, time,bearing;
    private Location myLocation, bizLocation, friendLocation;
    private float[] mValues;
    private float compassBearing;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate);
        
        //lots of textviews
        distBiz = (TextView) findViewById(R.id.distance_to_business);
        distFriend = (TextView) findViewById(R.id.distance_to_friend);
        bearBiz = (TextView) findViewById(R.id.bearing_to_business);
        bearFriend = (TextView) findViewById(R.id.bearing_to_friend);
        bizName = (TextView) findViewById(R.id.business_name);
        bizAddress = (TextView) findViewById(R.id.business_address);
        bizSuburb = (TextView) findViewById(R.id.business_suburb);
        time = (TextView) findViewById(R.id.time);
        bearing = (TextView) findViewById(R.id.bearing);



        //location stuff
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);

        List<String> providers = locationManager.getAllProviders();
        double accuracy = -1;
        if (providers.size() == 0) {
            Log.w(TAG, "No providers available");
        }
        for (String provider : providers) {
            Location tempLoc = locationManager.getLastKnownLocation(provider);
            if (tempLoc != null && (accuracy < 0 || tempLoc.getAccuracy() < accuracy)) {
                myLocation = tempLoc;
            }
        }

        //sensor stuff
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        
        //TODO DEBUG REMOVE
        bizLocation = new Location("TEST");
        bizLocation.setLatitude(-37.8133895);
        bizLocation.setLongitude(144.9628322);
        friendLocation = new Location("TEST");
        friendLocation.setLatitude(-33.8133895);
        friendLocation.setLongitude(142.9628322);

        locatonUpdate();
    }

    private void locatonUpdate(){
        time.setText(""+ myLocation.getTime());
        distBiz.setText(""+myLocation.distanceTo(bizLocation));
        distFriend.setText(""+myLocation.distanceTo(friendLocation));
        bearing.setText(""+compassBearing);
        bearBiz.setText(""+(myLocation.bearingTo(bizLocation)+compassBearing));
        bearFriend.setText(""+(myLocation.bearingTo(friendLocation)+compassBearing));
        //bearing.setText(""+myLocation.getBearing());
    }
    
    @Override
    public void onStart() {
        super.onStart();
        locationManager.requestLocationUpdates(0, 0, criteria, this, null);
        sensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
       myLocation = location;
        locatonUpdate();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    //--- bearings ---
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mValues = sensorEvent.values;

        compassBearing = mValues[0];
        locatonUpdate();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
