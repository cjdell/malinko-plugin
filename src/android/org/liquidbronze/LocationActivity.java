package org.liquidbronze;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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

    private String mOptionsJson;

    private Button btnRefreshLocation;

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
//        Date date = new Date(loc.getTime());
//
//        @SuppressLint("DefaultLocale")
//        String msg = String.format("Lat %f Lng %f Acc %f\nTime %tF %tT", loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), date, date);
//
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        Intent i = new Intent(this, VolumeListenerService.class);
        i.putExtra(VolumeListenerService.OPTIONS, mOptionsJson);
        i.putExtra(VolumeListenerService.LOCATION, loc);
        this.startService(i);

        finish();
    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }
}
