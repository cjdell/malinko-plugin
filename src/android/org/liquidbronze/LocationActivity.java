package org.liquidbronze;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.liquidbronze.malinkapp.R;

public class LocationActivity extends Activity {

    private static final String TAG = "LocationActivity";

    private static final int REQUEST_FINE_LOCATION = 432432432;
    private static final int REQUEST_SEND_SMS = 321321321;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private String mOptionsJson;

    private Location mLocation = null;

    private class LocationListener implements android.location.LocationListener {
        private LocationListener() {
            Log.e(TAG, "LocationListener");
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLocation = location;

            startListener();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    public LocationActivity() {
        super();

        mLocationListener = new LocationListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mOptionsJson = getIntent().getStringExtra(VolumeListenerService.OPTIONS);

        requestGpsPermissions();
    }

    private void requestGpsPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        } else {
            requestSmsPermission();
        }
    }

    private void requestSmsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
        } else {
            setupLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestSmsPermission();
            } else {
                Toast.makeText(this, "Failed to acquire GPS sending permissions", Toast.LENGTH_LONG).show();

                requestSmsPermission();
            }

            return;
        } else if (requestCode == REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationUpdates();
            } else {
                Toast.makeText(this, "Failed to acquire SMS sending permissions", Toast.LENGTH_LONG).show();

                setupLocationUpdates();
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupLocationUpdates() {
        try {
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);

//            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if (mLocation != null) {
//                startListener();
//            } else {
//                Toast.makeText(this, "Failed to acquire GPS location", Toast.LENGTH_LONG).show();
//            }

        } catch (SecurityException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    private void cleanUp() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);

            mLocationManager = null;
        }
    }

    private void startListener() {
        Intent i = new Intent(this, VolumeListenerService.class);
        i.putExtra(VolumeListenerService.OPTIONS, mOptionsJson);
        i.putExtra(VolumeListenerService.LOCATION, mLocation);
        this.startService(i);

        cleanUp();

        finish();
    }
}
