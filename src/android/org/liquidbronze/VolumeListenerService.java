package org.liquidbronze;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public class VolumeListenerService extends Service {
    private static final String TAG = "MalinkoPlugin";

    private static final int GPS_UPDATE_INTERVAL = 3 * 60 * 1000;
    private static final int GPS_UPDATE_DISTANCE = 10;;
    private static final int MIN_DELTA_TIME = 5 * 1000;

    public static final String ACCESS_TOKEN = "access_token";
    public static final String LOCATION = "location";

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private MediaPlayer mMediaPlayer;
    private ContentObserver mSettingsContentObserver;

    private int mPreviousVolume;
    private long mPreviousVolumeTime = 0;
    private int mMidVolume;

    private String mAccessToken;
    private Location mLocation;

    private int mOngoingNotificationId;
    private int mLastNotificationId;

    private float mLastTimeTriggered = -1;

    private PendingIntent mPendingIntent;

    public static VolumeListenerService CurrentInstance = null;

    private class LocationListener implements android.location.LocationListener {
        private LocationListener() {
            Log.e(TAG, "LocationListener");
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLocation = location;

            updateOngoingNotification();
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

    public VolumeListenerService() {
        Log.d(TAG, "VolumeListenerService");

        Random r = new Random();

        mOngoingNotificationId = r.nextInt();
        mLastNotificationId = r.nextInt();

        mLocationListener = new LocationListener();

        CurrentInstance = this;
    }

    public float getLastTimeTriggered() {
        return mLastTimeTriggered;
    }

    public void clearLastTimeTriggered() {
        mLastTimeTriggered = -1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cleanUp();

        setupLocationUpdates();

        mAccessToken = intent.getStringExtra(ACCESS_TOKEN);
        mLocation = intent.getParcelableExtra(LOCATION);

        if (mAccessToken == null || mLocation == null) return Service.START_REDELIVER_INTENT;

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), notification);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(0, 0);
        mMediaPlayer.start();

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mMidVolume = max / 2;

        // Set to halfway so we aren't bounded
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, mMidVolume, 0);
        mPreviousVolume = mMidVolume;

        mSettingsContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                long time = (new Date()).getTime();

                // Stop double events caused by resetting the volume every time we detect a change
                if (currentVolume == mMidVolume) return;

                int delta = mPreviousVolume - currentVolume;
                long deltaTime = time - mPreviousVolumeTime;

                if (deltaTime > MIN_DELTA_TIME) {
                    if (delta > 0) {
                        Log.d("volume", "volume " + currentVolume);
                        Log.d("volume", "down");
                        mPreviousVolume = currentVolume;

                        sendSilentAlarm();
                    } else if (delta < 0) {
                        Log.d("volume", "volume " + currentVolume);
                        Log.d("volume", "up");
                        mPreviousVolume = currentVolume;

                        sendSilentAlarm();
                    }
                }

                // Set to halfway so we aren't bounded
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, mMidVolume, 0);
                mPreviousVolume = mMidVolume;
                mPreviousVolumeTime = time;
            }
        };

        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);

//        showToastInIntentService("VolumeListenerService active: " + mAccessToken);

        String xId = Long.valueOf((new Date()).getTime()).toString();

        Intent notificationIntent = new Intent(this, LocationActivity.class);
        notificationIntent.setAction(xId);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(VolumeListenerService.ACCESS_TOKEN, mAccessToken);

        mPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification n = getOngoingNotification();

        startForeground(mOngoingNotificationId, n);

        return Service.START_STICKY;
    }

    private void updateOngoingNotification() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn = pm.isScreenOn();

        if (isScreenOn) {
            Notification n = getOngoingNotification();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(mOngoingNotificationId, n);
        }
    }

    private Notification getOngoingNotification() {
        Date date = (new Date(mLocation.getTime()));
        @SuppressLint("DefaultLocale")
        String dateString = String.format("%tT", date);;

        return new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Silent Alarm Enabled")
                .setContentText("Tap to update. Last GPS fix at " + dateString)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(mPendingIntent)
                .getNotification();
    }

    private void setupLocationUpdates() {
        try {
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_INTERVAL, GPS_UPDATE_DISTANCE, mLocationListener);

        } catch (SecurityException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    private void sendSilentAlarm() {
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL("https://my.malinkoapp.com/api/v3/silent");

                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("POST");

                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("Authorization", "Bearer " + mAccessToken);

                    urlConnection.setUseCaches(false);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    JSONObject request = new JSONObject();

                    request.put("access_token", mAccessToken);
                    request.put("alert", mLocation.getTime());
                    request.put("longitude", String.valueOf(mLocation.getLongitude()));
                    request.put("latitude", String.valueOf(mLocation.getLatitude()));
                    request.put("gps_accuracy", String.valueOf(mLocation.getAccuracy()));

                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(request.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String responseString = convertStreamToString(inputStream);

                    JSONObject response = new JSONObject(responseString);
                    String success = response.getString("success");

                    successNotification(mLocation, success);

                    showToastInIntentService(success);
                } catch (Exception ex) {
                    failureNotification(ex);

                    showToastInIntentService(ex.toString());
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();
    }

    private void successNotification(Location loc, String success) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        v.vibrate(500);

        Date date = (new Date(mLocation.getTime()));
        @SuppressLint("DefaultLocale")
        String dateString = String.format("GPS fix at %tF %tT", date, date);;

        Notification n = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_send)
                .setContentTitle(success)
                .setContentText(dateString)
                .setAutoCancel(true)
                .getNotification();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mLastNotificationId++;

        notificationManager.notify(mLastNotificationId, n);
    }

    private void failureNotification(Exception ex) {
        Notification n = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle("Silent Alarm FAILED")
                .setContentText(ex.toString())
                .setAutoCancel(true)
                .getNotification();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mLastNotificationId++;

        notificationManager.notify(mLastNotificationId, n);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CurrentInstance = null;

        cleanUp();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(mOngoingNotificationId);

        showToastInIntentService("Silent alarm disabled");
    }

    private void cleanUp() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);

            mLocationManager = null;
        }

        if (mSettingsContentObserver != null) {
            getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
            mSettingsContentObserver = null;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
    }

    private void showToastInIntentService(final String sText) {
        final Context context = this;

        Log.d(TAG, sText);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, sText, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String convertStreamToString(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
