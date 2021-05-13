package driver.transporterimenval.com.utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import driver.transporterimenval.com.R;

public class GeoLocationService extends Service {

    private static final String MY_PREFS_NAME = "Fooddelivery";

    public static final String LOCATION_UPDATE =
            GeoLocationService.class.getSimpleName();
    public static final String LOCATION_DATA = "location_data";

    private static final String TAG = GeoLocationService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000; // 10 Seconds
    private static final float LOCATION_DISTANCE = 1f; // meters = 100 m
    RequestQueue requestQueue;

    public static void start(Context context) {
        try {
            context.startService(new Intent(context, GeoLocationService.class));
        } catch (Exception e) {

        }
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, GeoLocationService.class));
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);

            sendLocation(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {

        } catch (IllegalArgumentException ex) {
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
        } catch (IllegalArgumentException ex) {
        }

        //volley cache
        //requestQueue = MySingleton.getInstance(GeoLocationService.this).getRequestQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (checkLocationPermission()) {
                        mLocationManager.removeUpdates(mLocationListeners[i]);
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void sendLocation(final Location location) {

        String deliverboy_id = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", "");
        if (deliverboy_id.equals("")) return;

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_position.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("~~Success.Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("~~Error.Response", error.toString());
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("deliverboy_id", deliverboy_id);
                MyData.put("lat", String.valueOf(location.getLatitude()));
                MyData.put("lon", String.valueOf(location.getLongitude()));
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                headers.put("Authorization", "Bearer " + pref.getString("regId", null));
                return headers;
            }
        };

        //MySingleton.getInstance(GeoLocationService.this).addToRequestQueue(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(GeoLocationService.this);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 2 * 1024 * 1024);
        RequestQueue queue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        queue.add(new ClearCacheRequest(cache, null));
        requestQueue.add(new ClearCacheRequest(cache, null));
        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        requestQueue.getCache().clear();
        requestQueue.getCache().remove(hp);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }
}