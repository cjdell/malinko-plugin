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
        if (action.equals("getSilentAlertStatus")) {

            boolean status = getSilentAlertStatus();

            final PluginResult result = new PluginResult(PluginResult.Status.OK, status);
            callbackContext.sendPluginResult(result);

        } else if (action.equals("getSilentAlertSet")) {

            float time = getSilentAlertSet();

            final PluginResult result = new PluginResult(PluginResult.Status.OK, time);
            callbackContext.sendPluginResult(result);

        } else if (action.equals("enableSilentAlert")) {

            JSONObject options = args.getJSONObject(0);

            this.enableSilentAlert(options);

            final PluginResult result = new PluginResult(PluginResult.Status.OK);
            callbackContext.sendPluginResult(result);

        } else if (action.equals("disableSilentAlert")) {

            this.disableSilentAlert();

            final PluginResult result = new PluginResult(PluginResult.Status.OK);
            callbackContext.sendPluginResult(result);

        } else if (action.equals("cancelSilentAlert")) {

            this.cancelSilentAlert();

            final PluginResult result = new PluginResult(PluginResult.Status.OK);
            callbackContext.sendPluginResult(result);

        }

        return true;
    }

    private boolean getSilentAlertStatus() {
        ActivityManager manager = (ActivityManager) this.webView.getContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (VolumeListenerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    private float getSilentAlertSet() {
        if (VolumeListenerService.CurrentInstance != null) {
            return VolumeListenerService.CurrentInstance.getLastTimeTriggered();
        }

        return -1;
    }

    private void enableSilentAlert(JSONObject options) {
        if (!getSilentAlertStatus()) {
            Intent notificationIntent = new Intent(this.webView.getContext(), LocationActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra(VolumeListenerService.OPTIONS, options.toString());
            this.webView.getContext().startActivity(notificationIntent);
        } else {
            Log.d(TAG, "VolumeListenerService already running");
        }
    }

    private void disableSilentAlert() {
        if (getSilentAlertStatus()) {
            Intent i = new Intent(this.webView.getContext(), VolumeListenerService.class);
            this.webView.getContext().stopService(i);
        } else {
            Log.d(TAG, "VolumeListenerService already offline");
        }
    }

    private void cancelSilentAlert() {
        if (VolumeListenerService.CurrentInstance != null) {
            VolumeListenerService.CurrentInstance.clearLastTimeTriggered();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.disableSilentAlert();
    }
}
