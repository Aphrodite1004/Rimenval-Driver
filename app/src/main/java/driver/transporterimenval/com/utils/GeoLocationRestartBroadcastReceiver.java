package driver.transporterimenval.com.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by arvi on 12/11/17.
 */

public class GeoLocationRestartBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = GeoLocationRestartBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Service Stops, let's restart again.");
        context.startService(new Intent(context, GeoLocationService.class));
    }
}
