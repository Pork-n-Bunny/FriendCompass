package com.porknbunny.friendcompass;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class TestGPSActivity extends FragmentActivity implements LocationListener {
    private LocationManager locationManger;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
    }

    @Override
    public void onStart() {
        locationManger.requestLocationUpdates(0, 0, new Criteria(), this, null);
    }

    @Override
    public void onStop() {
        locationManger.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Yay got loc! " + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
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
}
