package org.liquidbronze;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationActivity;
import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.location.LocationRequest;

import org.liquidbronze.malinkapp.R;

import java.util.Date;

public class LocationActivity extends EasyLocationActivity {

    private static final int REQUEST_SEND_SMS = 321321321;

    private String mOptionsJson;

    private Location mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOptionsJson = getIntent().getStringExtra(VolumeListenerService.OPTIONS);

        requestNewLocation();
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListener();
            } else {
                Toast.makeText(this, "Failed to acquire SMS sending permissions", Toast.LENGTH_LONG).show();

                startListener();
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);

        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(5000)
                .build();

        requestSingleLocationFix(easyLocationRequest);
    }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationReceived(Location loc) {
        if (mLocation == null) {
            mLocation = loc;

            requestSmsPermission();
        }
    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }

    private void startListener() {
        Intent i = new Intent(this, VolumeListenerService.class);
        i.putExtra(VolumeListenerService.OPTIONS, mOptionsJson);
        i.putExtra(VolumeListenerService.LOCATION, mLocation);
        this.startService(i);

        finish();
    }
}
