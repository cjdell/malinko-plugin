/**
 */
package org.liquidbronze;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.content.Intent;

import java.util.Date;

public class MalinkoPlugin extends CordovaPlugin {
    private static final String TAG = "MalinkoPlugin";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing MalinkoPlugin");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("getSilentAlarmStatus")) {

            boolean status = isMyServiceRunning(VolumeListenerService.class);

            final PluginResult result = new PluginResult(PluginResult.Status.OK, status);
            callbackContext.sendPluginResult(result);

        } else if (action.equals("enableSilentAlarm")) {

            String accessToken = args.getString(0);

            this.enableSilentAlarm(accessToken);

            final PluginResult result = new PluginResult(PluginResult.Status.OK);
            callbackContext.sendPluginResult(result);

        } else if (action.equals("disableSilentAlarm")) {

            this.disabledSilentAlarm();

            final PluginResult result = new PluginResult(PluginResult.Status.OK);
            callbackContext.sendPluginResult(result);
        }

        return true;
    }

    private void enableSilentAlarm(String accessToken) {
        if (!isMyServiceRunning(VolumeListenerService.class)) {
            Intent notificationIntent = new Intent(this.webView.getContext(), LocationActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra(VolumeListenerService.ACCESS_TOKEN, accessToken);
            this.webView.getContext().startActivity(notificationIntent);
        } else {
            Log.d(TAG, "VolumeListenerService already running");
        }
    }

    private void disabledSilentAlarm() {
        if (isMyServiceRunning(VolumeListenerService.class)) {
            Intent i = new Intent(this.webView.getContext(), VolumeListenerService.class);
            this.webView.getContext().stopService(i);
        } else {
            Log.d(TAG, "VolumeListenerService already offline");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.disabledSilentAlarm();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.webView.getContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
