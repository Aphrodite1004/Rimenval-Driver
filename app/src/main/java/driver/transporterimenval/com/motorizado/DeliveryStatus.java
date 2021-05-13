package driver.transporterimenval.com.motorizado;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import driver.transporterimenval.com.Adapter.DeliveryAdapter;
import driver.transporterimenval.com.Getset.DeliveryGetSet;
import driver.transporterimenval.com.R;
import driver.transporterimenval.com.helperTracking.FirebaseHelper;
import driver.transporterimenval.com.helperTracking.UiHelper;
import driver.transporterimenval.com.model.Driver;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.GeoLocationService;
import driver.transporterimenval.com.utils.check_sesion;


public class DeliveryStatus extends NotificationToAlertActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2161;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static String DRIVER_ID;   // Id must be unique for every driver.


    private FirebaseHelper firebaseHelper;
    private AtomicBoolean driverOnlineFlag = new AtomicBoolean(false);

    private FusedLocationProviderClient locationProviderClient;
    private UiHelper uiHelper;
    private LocationRequest locationRequest;

    private boolean locationFlag = true;
    private String orderNo = "-1";

    //---------------------

    private ArrayList<DeliveryGetSet> data;
    private ListView listView_delivery;
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private static final String MY_PREFS_ACTIVITY = "DeliveryActivity";
    private String status, DeliveryBoyId, regId, level_id, desc_levels;
    private SwitchCompat sw_radius_onoff;
    private static DeliveryStatus instance;
    private Boolean internet = false;
    private TextView txt_header;
    //private TextView txt_name;
    private TextView txt_nameuser;
    private TextView txt_perfil;
    private TextView btn_my_profile;
    private TextView txt_presenceOn;
    private TextView list_order_none;
    private TextView btn_order_history;
    private TextView btn_my_payment;
    //private repartidor.faster.com.ec.utils.RoundedImageView img_user;
    private driver.transporterimenval.com.utils.RoundedImageView img_profile;
    private ImageView img_status;
    private MediaPlayer player;
    private Uri notification;
    private boolean sound;
    private Vibrator vibrator;
    private DrawerLayout mDrawerLayout;

    private LinearLayout ll_historial;
    private LinearLayout ll_mispagos;
    //private LinearLayout ll_ganacias;
    private LinearLayout ll_perfil;
    private LinearLayout ll_work_time;
    private LinearLayout ll_share;
    private LinearLayout ll_aboutus;
    private LinearLayout ll_terms;
    private LinearLayout ll_logout;

    private int dot = 200;
    private int dash = 500;
    private int gap = 200;
    private boolean endVibration = false;
    private long[] pattern = new long[]{
            0,
            dot, gap, dash, gap, dot, gap, dot
    };
    private Handler handler = new Handler();

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;
    private static final int CAMERA_REQUEST = INITIAL_REQUEST + 1;
    private static final int CONTACTS_REQUEST = INITIAL_REQUEST + 2;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private static DatabaseReference mDatabase;
    private static final String ONESIGNAL_APP_ID = "e0845352-fe40-42bb-9c1f-7ef14e84e0f8";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status);
        Objects.requireNonNull(getSupportActionBar()).hide();
        instance = this;
        SharedPreferences prefsDeliveryBoyId = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        DeliveryBoyId = prefsDeliveryBoyId.getString("DeliveryUserId", null);

        //volley cache
        //requestQueue = MySingleton.getInstance(DeliveryStatus.this).getRequestQueue();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        initialization();
        drawer();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", true);
        editor.putString("Activity", "DeliveryStatus");
        editor.apply();
        driverOnlineFlag.set(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }

        initializeView();

        // OneSignal initialize
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        //clearData();
    }

    /**Elimina los datos de Firebase Realtime Database
    public static void clearData(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("online_drivers").setValue(null);
    }*/

    private void initializeView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        data.clear();
        listView_delivery.setAdapter(null);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", true);
        editor.putString("Activity", "DeliveryStatus");
        editor.apply();
        driverOnlineFlag.set(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (data == null || data.size() == 0) {
        } else if (data != null && data.size() > 0) {
            data.clear();
            listView_delivery.setAdapter(null);
        }
        settingData();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", true);
        editor.putString("Activity", "DeliveryStatus");
        editor.apply();
        driverOnlineFlag.set(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSoundVibrate();
        driverOnlineFlag.set(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", false);
        editor.apply();
        driverOnlineFlag.set(false);
        stopSoundVibrate();
        System.exit(0);

        SharedPreferences prefsDeliveryBoyId = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        sound = prefsDeliveryBoyId.getBoolean("sound", false);

        GeoLocationService.stop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSoundVibrate();
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) Objects.requireNonNull(getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DeliveryStatus getInstance() {
        return instance;
    }

    private void drawer() {
        ll_historial = findViewById(R.id.ll_historial);
        // ll_mispagos = findViewById(R.id.ll_mispagos);
        //ll_ganacias = findViewById(R.id.ll_ganacias);
        ll_perfil = findViewById(R.id.ll_perfil);
        ll_work_time = findViewById(R.id.ll_work_time);
        // ll_terms = findViewById(R.id.ll_terms);
        ll_share = findViewById(R.id.ll_share);
        // ll_aboutus = findViewById(R.id.ll_aboutus);
        ll_logout = findViewById(R.id.ll_logout);

        /*ll_mispagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryPayment");
                editor.apply();
                Intent i = new Intent(DeliveryStatus.this, DeliveryPaymentHistory.class);
                startActivity(i);
            }
        });*/

        ll_historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryOrderHistory");
                editor.apply();
                Intent i = new Intent(DeliveryStatus.this, DeliveryOrderHistory.class);
                startActivity(i);
            }
        });

        ll_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryUserProfile");
                editor.apply();
                Intent i = new Intent(DeliveryStatus.this, DeliveryUserProfile.class);
                startActivity(i);

            }
        });

        ll_work_time.setOnClickListener(new View.OnClickListener() {//markmark
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryUserProfile");
                editor.apply();
                Intent i = new Intent(DeliveryStatus.this, DeliveryWorkTime.class);
                startActivity(i);

            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url1 = "Rimenval, una app que usa tecnología de punta para el control logístico, además de la posibilidad de rastrear en tiempo real el proceso, me ha gustado mucho y te la recomiendo ¿Qué esperas para usarla? \n\n _*Rimenval APp*_, DESCÁRGALA GRATIS AQUÍ: \n https://play.google.com/store/apps/details?id=" + DeliveryStatus.this.getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "FASTER - DELIVERY APP");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, url1);

                startActivity(Intent.createChooser(intent, "Compartir Rimenval app con: "));
            }
        });

        /*ll_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(DeliveryStatus.this, Aboutus.class);
                startActivity(iv);

            }
        });

        ll_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iv = new Intent(DeliveryStatus.this, Termcondition.class);
                startActivity(iv);
            }
        });*/

        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //clearApplicationData();
                                clearAppData();
                                Intent intent = new Intent(DeliveryStatus.this, LoginAsDelivery.class);
                                startActivity(intent);
                                SharedPreferences settings = getSharedPreferences("Fooddelivery", Context.MODE_PRIVATE);
                                settings.edit().clear().apply();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryStatus.this);
                AlertDialog dialog = builder.setTitle("¿Estás seguro que deseas Cerrar Sesión?").setPositiveButton("SI", dialogClickListener)
                        .setNegativeButton("NO", dialogClickListener).show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });

    }

    private void initialization() {

        txt_header = findViewById(R.id.txt_header);
        //txt_name = findViewById(R.id.txt_name);
        txt_nameuser = findViewById(R.id.txt_nameuser);
        txt_perfil = findViewById(R.id.txt_perfil);
        btn_my_profile = findViewById(R.id.btn_deilverb);
        txt_presenceOn = findViewById(R.id.txt_presenceOn);
        list_order_none = findViewById(R.id.list_order_none);
        // btn_order_history = findViewById(R.id.btn_order_history);
        // btn_my_payment = findViewById(R.id.btn_my_profile);
        //img_user = findViewById(R.id.img_user);
        img_profile = findViewById(R.id.img_profile);
        img_status = findViewById(R.id.img_status);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        sw_radius_onoff = findViewById(R.id.Sw_radius_onoff);
        listView_delivery = findViewById(R.id.list_order_info);

        //getting shared pref and setting data
        Picasso.with(DeliveryStatus.this).load(getResources().getString(R.string.link) + "uploads/deliveryboys/" + getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserImage", "")).resize(200, 200).centerCrop().into(img_profile);
        /*Picasso.with(DeliveryStatus.this)
                    .load(getResources().getString(R.string.link) + "uploads/deliveryboys/" + getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserImage", ""))
                    .resize(50, 50)
                    .centerCrop().into(img_user);*/

        try {
            //Niveles de Riders
            if (getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserDescLevel", "").equals("Oro")){
                txt_perfil.setText(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserDescLevel", ""));
                txt_perfil.setTextColor(getResources().getColor(R.color.oro));
            } else if (getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserDescLevel", "").equals("Platino")){
                txt_perfil.setText(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserDescLevel", ""));
                txt_perfil.setTextColor(getResources().getColor(R.color.platino));
            } else if (getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserDescLevel", "").equals("Diamante")){
                txt_perfil.setText(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserDescLevel", ""));
                txt_perfil.setTextColor(getResources().getColor(R.color.diamante));
            }
        } catch(Exception e) {
            //
        }

        //txt_name.setText(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserName", ""));
        txt_nameuser.setText(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserName", ""));
        txt_header.setText(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserName", ""));
        sw_radius_onoff.setChecked(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getBoolean("isPresent", false));

        //dsfsd
        data = new ArrayList<>();
    }

    private void initViews() {
        sw_radius_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "yes";
                    sw_radius_onoff.setChecked(true);
                } else {
                    status = "no";
                    sw_radius_onoff.setChecked(false);
                }
                sendPresence(status);
            }
        });

        btn_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryUserProfile");
                editor.apply();

                Intent i = new Intent(DeliveryStatus.this, DeliveryUserProfile.class);
                startActivity(i);

            }
        });

        /*btn_my_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryPayment");
                editor.apply();

                Intent i = new Intent(DeliveryStatus.this, DeliveryPaymentHistory.class);
                startActivity(i);

            }
        });

        btn_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryOrderHistory");
                editor.apply();
                Intent i = new Intent(DeliveryStatus.this, DeliveryOrderHistory.class);
                startActivity(i);
            }
        });*/


        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.START))
                    mDrawerLayout.closeDrawer(Gravity.START);
                else {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        listView_delivery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryOrderDetail");
                editor.apply();

                Intent i = new Intent(DeliveryStatus.this, DeliveryOrderDetail.class);

                i.putExtra("DeliveryBoyId", DeliveryBoyId);
                i.putExtra("OrderNo", data.get(position).getOrderNo());
                i.putExtra("OrderAmount", data.get(position).getOrderAmount());
                i.putExtra("status", data.get(position).getStatus());
                i.putExtra("OrderTime", data.get(position).getOrderTime());
                i.putExtra("DeliveryAmount", data.get(position).getOrderDelivery());
                startActivity(i);
            }
        });


    }

    public static boolean checkInternet(Context context) {
        // TODO Auto-generated method stub
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean sendPresence(final String status) {

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_presence.php?";
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response123", response);
                        //    {"data":{"success":"1","presence":"false"}}
                        try {
                            JSONObject jo_main = new JSONObject(response);
                            final String txt_message = jo_main.getString("message");
                            String txt_success = jo_main.getString("success");

                            if (txt_success.equals("1")) {
                                settingData();
                                internet = true;
                                String isPresent = jo_main.getString("attendance");
                                getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit().putBoolean("isPresent", returnBool(isPresent)).apply();

                                //validar si esta activo o inactivo rider
                                if (!isPresent.isEmpty()){
                                    if (isPresent.equals("no")){
                                        txt_presenceOn.setText("Estás desconectado");
                                        txt_presenceOn.setTextColor(getResources().getColor(R.color.red));
                                    } else if (isPresent.equals("yes")){
                                        txt_presenceOn.setText("Estás conectado");
                                        txt_presenceOn.setTextColor(getResources().getColor(R.color.res_green));
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Existe un problema. Revisa tu conexión a Internet o Cierra Sesión.", Toast.LENGTH_SHORT).show();
                                }
                            } else if (txt_success.equals("-4") || (txt_success.equals("-5")) || (txt_success.equals("-8")) || (txt_success.equals("-11"))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryStatus.this, R.style.MyDialogTheme);
                                        builder1.setTitle("Información");
                                        builder1.setCancelable(false);
                                        builder1.setMessage(txt_message);
                                        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                editor.putBoolean("isDeliverAccountActive", false);
                                                editor.putString("DeliveryUserId", "");
                                                editor.putString("DeliveryUserName", "");
                                                editor.putString("DeliveryUserLevelId", "");
                                                editor.putString("DeliveryUserPhone", "");
                                                editor.putString("DeliveryUserEmail", "");
                                                editor.putString("DeliveryUserVNo", "");
                                                editor.putString("DeliveryUserVType", "");
                                                editor.putString("DeliveryUserImage", "");
                                                editor.putString("DeliveryUserDescLevel", "");
                                                editor.apply();

                                                SharedPreferences.Editor editor2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).edit();
                                                editor2.putString("regId", null);
                                                editor2.apply();

                                                Intent iv = new Intent(DeliveryStatus.this, Splash.class);
                                                iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(iv);

                                            }
                                        });
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }
                                });

                            } else {
                                check_sesion se = new check_sesion();
                                se.validate_sesion(DeliveryStatus.this, txt_success, txt_message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        String message = null;

                        Toast.makeText(getApplicationContext(), "Revisa tu conexión a Internet", Toast.LENGTH_SHORT).show();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("attendance", status); //Add the data you'd like to send to the server.
                MyData.put("session", "yes"); //Add the data you'd like to send to the server.
                MyData.put("deliverboy_id", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", ""));
                MyData.put("code", getString(R.string.version_app));
                MyData.put("operative_system", getString(R.string.sistema_operativo));
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + regId);
                return headers;
            }
        };

        //MySingleton.getInstance(DeliveryStatus.this).addToRequestQueue(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryStatus.this);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 2 * 1024 * 1024);
        RequestQueue queue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        queue.add(new ClearCacheRequest(cache, null));
        requestQueue.add(new ClearCacheRequest(cache, null));
        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        requestQueue.getCache().clear();
        requestQueue.getCache().remove(hp);
        stopSoundVibrate();

        //adding the string request to request queue
        requestQueue.add(stringRequest);

        return internet;
    }

    public void stopSoundVibrate() {
        try {

            Log.e("mainactivity", "entró aqui a pause");

            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                    player.setLooping(false);
                }
                player.reset();//It requires again setDataSource for player object.
                player.release();
                player = null; // fixed typo.

            }
            endVibration = true;
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
            editor.putBoolean("sound", false);
            editor.apply();

        } catch (Exception e) {
            Log.e("mainactivity", "entró aqui al error de pause");
            Log.e("mainactivity", e.toString());

        }
    }

    public void settingData() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                stopSoundVibrate();

                Log.e("mainactivity", "entró aqui iniciar setting data");
                if (data != null && data.size() > 0) {
                    data.clear();
                    listView_delivery.setAdapter(null);
                }


                //creating a string request to send request to the url
                String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_order.php?";
                Log.e("Response123", hp);
                Log.w(getClass().getName(), hp);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //hiding the progressbar after completion
                                Log.e("Response123", response);
                                try {

                                    //getting the whole json object from the response
                                    JSONObject obj = new JSONObject(response);
                                    String txt_success = obj.getString("success");
                                    final String txt_message = obj.getString("message");
                                    String isPresent2 = obj.getString("DeliveryBoyAttendance");

                                    if (isPresent2.equals("no") || isPresent2.equals("null")){
                                        sw_radius_onoff.setChecked(false);
                                    } else if (isPresent2.equals("yes")){
                                        sw_radius_onoff.setChecked(true);
                                    }

                                    //validar si esta activo o inactivo rider
                                    if (isPresent2 != null){
                                        if (isPresent2.equals("no") || isPresent2.equals("null")){
                                            txt_presenceOn.setText("Estás desconectado");
                                            txt_presenceOn.setTextColor(getResources().getColor(R.color.red));
                                        } else if (isPresent2.equals("yes")){
                                            txt_presenceOn.setText("Estás conectado");
                                            txt_presenceOn.setTextColor(getResources().getColor(R.color.res_green));
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Existe un problema. Revisa tu conexión a Internet o Cierra Sesión.", Toast.LENGTH_SHORT).show();
                                    }

                                    //Niveles de Riders
                                    level_id = obj.getString("DeliveryBoyLevel");
                                    switch (level_id) {
                                        case "Oro":
                                            txt_perfil.setText(level_id);
                                            txt_perfil.setTextColor(getResources().getColor(R.color.oro));
                                            break;
                                        case "Platino":
                                            txt_perfil.setText(level_id);
                                            txt_perfil.setTextColor(getResources().getColor(R.color.platino));
                                            break;
                                        case "Diamante":
                                            txt_perfil.setText(level_id);
                                            txt_perfil.setTextColor(getResources().getColor(R.color.diamante));
                                            break;
                                    }
                                    Log.e("Delivery_Boy_LevelsXX", level_id);

                                    if (txt_success.equals("1")) {
                                        String txt_assigned = obj.getString("assigned");
                                        //Si tiene un pedido asignado no suene
                                        //caso contrario si no está sonando que inicie el sonido
                                        if (txt_assigned != null && txt_assigned.equals("0")) {

                                            try {
                                                stopSoundVibrate();
                                                notification = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                                                        + "://" + getApplicationContext().getPackageName() + "/raw/faster_sound");
                                                if (player == null) {
                                                    player = MediaPlayer.create(getBaseContext(), notification);
                                                    player.setLooping(true);
                                                }

                                                Log.e("mainactivity", "tiene pedidos pendientes");
                                                endVibration = false;
                                                SharedPreferences prefsDeliveryBoyId = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                                sound = prefsDeliveryBoyId.getBoolean("sound", false);

                                                if (!sound) {
                                                    player.start();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                                            vibrator.vibrate(1000);
                                                            if (!endVibration) {
                                                                handler.postDelayed(this, 1500);
                                                            }
                                                        }
                                                    }, 1500);
                                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                                                    editor.putBoolean("sound", true);
                                                    editor.apply();
                                                }


                                            } catch (Exception e) {
                                                Log.e("mainactivity", "entró aqui al error al iniciar player");
                                                e.printStackTrace();
                                            }
                                        } else {

                                            stopSoundVibrate();
                                            //verificar si está sonando se detenga
                                        }

                                        data.clear();
                                        listView_delivery.setAdapter(null);

                                        JSONArray ja_order = obj.getJSONArray("order");
                                        DeliveryGetSet getSet;
                                        Boolean llevando = false;
                                        for (int i = 0; i < ja_order.length(); i++) {
                                            JSONObject jo_orderDetail = ja_order.getJSONObject(i);
                                            getSet = new DeliveryGetSet();
                                            getSet.setResName(jo_orderDetail.getString("restaurant_name"));
                                            getSet.setOrderDate(jo_orderDetail.getString("created_at"));
                                            getSet.setOrderAddress(jo_orderDetail.getString("order_address"));
                                            getSet.setOrderNo(jo_orderDetail.getString("order_no"));
                                            getSet.setOrderDelivery(jo_orderDetail.getString("delivery_price"));
                                            getSet.setOrderTime(jo_orderDetail.getString("time_rest"));
                                            getSet.setOrderAmount(jo_orderDetail.getString("total_amount"));
                                            getSet.setStatus(jo_orderDetail.getString("status"));
                                            Log.d("checkIddsd3", "" + jo_orderDetail.getString("status"));
                                            if (jo_orderDetail.getString("status").equals("1") || jo_orderDetail.getString("status").equals("3")) {
                                                orderNo = jo_orderDetail.getString("order_no");
                                                llevando = true;
                                            }
                                            data.add(getSet);
                                        }

                                        if (llevando) {
                                            uiHelper = new UiHelper(getBaseContext());
                                            locationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
                                            locationRequest = uiHelper.getLocationRequest();
                                            if (!uiHelper.isPlayServicesAvailable()) {
                                                Toast.makeText(getBaseContext(), "Play Services did not installed!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else requestLocationUpdates();
                                            firebaseHelper = new FirebaseHelper(orderNo);
                                            driverOnlineFlag.set(true);
                                        } else {
                                            driverOnlineFlag.set(false);
                                        }
                                        DeliveryAdapter adapter = new DeliveryAdapter(data, DeliveryStatus.this);
                                        listView_delivery.setAdapter(adapter);
                                    } else if (txt_success.equals("-4") || (txt_success.equals("-5")) || (txt_success.equals("-8")) || (txt_success.equals("-11"))) {
                                        driverOnlineFlag.set(false);
                                        if (data == null || data.isEmpty()) {
                                        } else {
                                            data.clear();
                                        }

                                        stopSoundVibrate();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryStatus.this, R.style.MyDialogTheme);
                                                builder1.setTitle("Información");
                                                builder1.setCancelable(false);
                                                builder1.setMessage(txt_message);
                                                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                        editor.putBoolean("isDeliverAccountActive", false);
                                                        editor.putString("DeliveryUserId", "");
                                                        editor.putString("DeliveryUserName", "");
                                                        editor.putString("DeliveryUserLevelId", "");
                                                        editor.putString("DeliveryUserPhone", "");
                                                        editor.putString("DeliveryUserEmail", "");
                                                        editor.putString("DeliveryUserVNo", "");
                                                        editor.putString("DeliveryUserVType", "");
                                                        editor.putString("DeliveryUserImage", "");
                                                        editor.putString("DeliveryUserDescLevel", "");
                                                        editor.apply();

                                                        SharedPreferences.Editor editor2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).edit();
                                                        editor2.putString("regId", null);
                                                        editor2.apply();

                                                        Intent iv = new Intent(DeliveryStatus.this, Splash.class);
                                                        iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(iv);

                                                    }
                                                });
                                                AlertDialog alert11 = builder1.create();
                                                alert11.show();
                                            }
                                        });

                                    } else {
                                        driverOnlineFlag.set(false);
                                        if (data == null || data.isEmpty()) {
                                        } else {
                                            data.clear();
                                        }

                                        check_sesion se = new check_sesion();
                                        se.validate_sesion(DeliveryStatus.this, txt_success, txt_message);
                                        stopSoundVibrate();
                                    }


                                } catch (JSONException e) {

                                    driverOnlineFlag.set(false);
                                    stopSoundVibrate();
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Si el sonido está activo pare de sonar
                                // error
                                stopSoundVibrate();

                                driverOnlineFlag.set(false);
                                Log.d("Error.Response", error.toString());
                                String message = null;
                                if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryStatus.this, R.style.MyDialogTheme);
                                    builder1.setTitle("Información");
                                    builder1.setCancelable(false);
                                    builder1.setMessage("Por favor verifica tu conexión a Internet");
                                    builder1.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            settingData();
                                        }
                                    });
                                    builder1.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            finishAffinity();
                                            stopSoundVibrate();
                                        }
                                    });
                                    try {
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    } catch(Exception e){
                                    // WindowManager$BadTokenException will be caught and the app would not display
                                    // the 'Force Close' message
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "Por el momento no podemos procesar tu solicitud", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("deliverboy_id", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", ""));
                        params.put("code", getString(R.string.version_app));
                        params.put("operative_system", getString(R.string.sistema_operativo));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + regId);
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return super.getBodyContentType();
                    }
                };

                //MySingleton.getInstance(DeliveryStatus.this).addToRequestQueue(stringRequest);
                RequestQueue requestQueue = Volley.newRequestQueue(DeliveryStatus.this);
                DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 2 * 1024 * 1024);
                RequestQueue queue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                queue.add(new ClearCacheRequest(cache, null));
                requestQueue.add(new ClearCacheRequest(cache, null));
                stringRequest.setShouldCache(false);
                queue.getCache().clear();
                requestQueue.getCache().clear();
                requestQueue.getCache().remove(hp);
                stopSoundVibrate();

                //adding the string request to request queue
                requestQueue.add(stringRequest);
            }
        });

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        driverOnlineFlag.set(false);
        stopSoundVibrate();
        showExitDialog();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
        data.clear();
        listView_delivery.setAdapter(null);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", true);
        editor.putString("Activity", "DeliveryStatus");
        editor.apply();

        try {
            GeoLocationService.start(this);
        } catch (Exception e) {
            //
        }
    }


    private void showExitDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryStatus.this, R.style.MyDialogTheme);
        builder1.setTitle(getString(R.string.Quit));
        builder1.setIcon(R.mipmap.ic_launcher);
        builder1.setMessage(getString(R.string.statementquit));
        builder1.setCancelable(true);
        builder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                stopSoundVibrate();
                finishAffinity();
                SharedPreferences prefsDeliveryBoyId = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                sound = prefsDeliveryBoyId.getBoolean("sound", false);
            }
        });
        builder1.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private boolean returnBool(String status) {
        return Objects.equals(status, "yes");
    }

    public static void changeStatsBarColor(Activity activity) {
        Window window = activity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.my_statusbar_color));
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location == null) return;
            if (locationFlag) {
                locationFlag = false;
            }
            if (driverOnlineFlag.get()) {
                firebaseHelper.updateDriver(new Driver(location.getLatitude(), location.getLongitude(), orderNo));
            }

        }
    };

    public void deleteDriver() {
        firebaseHelper.deleteDriver();
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!uiHelper.isHaveLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        if (uiHelper.isLocationProviderEnabled())
            uiHelper.showPositiveDialogWithListener
                    (this, getResources().getString(R.string.need_location), getResources().getString(R.string.location_content),
                            () -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)), "ACTIVAR", false);
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            try {
                if (grantResults.length > 0) {
                    int value = grantResults[0];
                    if (value == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (value == PackageManager.PERMISSION_GRANTED) requestLocationUpdates();
                }
            } catch (Exception e) {

            }
        }
    }

}