package driver.transporterimenval.com.motorizado;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;

import driver.transporterimenval.com.Adapter.OrderDetailAdapter;
import driver.transporterimenval.com.R;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;

import driver.transporterimenval.com.Getset.orderDetailGetSet;
import driver.transporterimenval.com.helperTracking.FirebaseHelper;
import driver.transporterimenval.com.helperTracking.UiHelper;
import driver.transporterimenval.com.model.Driver;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.GPSTracker;
import driver.transporterimenval.com.utils.check_sesion;
import servicios.BubbleHeadService;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeliveryOrderDetail extends NotificationToAlertActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2161;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static String DRIVER_ID;   // Id must be unique for every driver.


    private FirebaseHelper firebaseHelper;
    private AtomicBoolean driverOnlineFlag = new AtomicBoolean(false);

    private FusedLocationProviderClient locationProviderClient;
    private UiHelper uiHelper;
    private LocationRequest locationRequest;

    private boolean locationFlag = true;

    //-----------------------------
    private ListView list_order;
    private static final String MY_PREFS_NAME = "Fooddelivery";

    private static final String MY_PREFS_ACTIVITY = "DeliveryActivity";
    private String deliveryBoyId, orderNo, orderStatus, isPick, level_id, desc_levels;
    private String orderTextStatus, orderAssign, orderPrice, orderTotal, orderDeliveryPrice, orderAmountPay, orderPayment, orderTimeRest, orderChange, orderNote;
    private String userAddress, userName, userPhone, userLat, userLon, userImage, userId, userDni;
    private String restaurantName, restaurantAddress, restaurantPhoto, restaurantPhone, restaurantLat, restaurantLon;
    private ProgressDialog progressDialog;
    Button btn_picked;
    private ArrayList<orderDetailGetSet> getsetDeliveryorderdetail;
    private static DeliveryOrderDetail instance;
    private static androidx.appcompat.widget.AppCompatButton btn_bell;
    private boolean active;
    private int count;
    private String timearrive;
    private String regId;
    private TextView txt_orderTime;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order_detail);
        Objects.requireNonNull(getSupportActionBar()).hide();
        gettingIntents();

        //volley cache
        //requestQueue = MySingleton.getInstance(DeliveryOrderDetail.this).getRequestQueue();

        instance = this;
        active = false;
        count = 0;
        timearrive = "";
        displayFirebaseRegId();

        btn_picked = findViewById(R.id.btn_picked);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }

    private void initializeView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
            editor.putBoolean("Main", false);
            editor.putString("Activity", "DeliveryBurble");
            editor.apply();
            Intent resultIntent = new Intent(this, BubbleHeadService.class);
            resultIntent.putExtra("OrderNo", orderNo);
            resultIntent.putExtra("DeliveryBoyId", deliveryBoyId);
            this.startService(resultIntent);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new get_order_details().execute();
    }

    public static DeliveryOrderDetail getInstance() {
        return instance;
    }

    private void gettingIntents() {
        Intent i = getIntent();
        SharedPreferences prefsDeliveryBoyId = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        deliveryBoyId = prefsDeliveryBoyId.getString("DeliveryUserId", null);
        orderNo = i.getStringExtra("OrderNo");

        Log.d("ORerNumberExperiment", deliveryBoyId + orderNo);
    }


    private void dialogpicked(String mesage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
        builder1.setTitle("Confirmación");
        builder1.setCancelable(false);
        builder1.setMessage(mesage);
        builder1.setPositiveButton("Si, estoy seguro", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new postingData().execute();
                Intent i = new Intent(DeliveryOrderDetail.this, DeliveryStatus.class);
                startActivity(i);

            }
        });
        builder1.setNegativeButton("No, cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        try {
            if (this != null) {
                alert11.show();
            }
        } catch (Exception e) {
            //
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
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
            int value = grantResults[0];
            if (value == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                finish();
            } else if (value == PackageManager.PERMISSION_GRANTED) requestLocationUpdates();
        }
    }

    private void initViews() {

        //btn_bell = findViewById(R.id.btn_bell2);
        TextView txt_header = findViewById(R.id.txt_header);
        txt_header.setText(getString(R.string.txt_order_no) + orderNo);

        Log.d("checkIddsd", "" + orderStatus);

        driver.transporterimenval.com.utils.RoundedImageView img_user = findViewById(R.id.image);

        try {

            TextView txt_orderStatus = findViewById(R.id.txt_orderStatus);
            txt_orderStatus.setText(orderTextStatus);
            TextView txt_orderAmount = findViewById(R.id.txt_orderAmount);
            if (orderPrice != null || orderTotal != null) {
                txt_orderAmount.setText("Pedido: " + getString(R.string.currency) + String.format("%.2f", Double.parseDouble(orderPrice)).replace(".", ",") + " | Total: " + getString(R.string.currency) + String.format("%.2f", Double.parseDouble(orderTotal)).replace(".", ","));
            }
            TextView txt_deliveryPrice = findViewById(R.id.txt_deliveryPrice);
            txt_deliveryPrice.setText("Carrera: " + getString(R.string.currency) + String.format("%.2f", Double.parseDouble(orderDeliveryPrice)).replace(".", ",") + " | Vuelto: " + getString(R.string.currency) + String.format("%.2f", Double.parseDouble(orderChange)).replace(".", ","));
            txt_orderTime = findViewById(R.id.txt_orderTime);

        } catch (Exception e) {
            //
        }

        if (!orderTimeRest.equals("0")) {
            check_sesion.reverseTimer(Integer.parseInt(orderTimeRest), txt_orderTime);
        }

        TextView txt_name = findViewById(R.id.txt_name);
        TextView txt_address = findViewById(R.id.txt_address);
        TextView txt_modePay = findViewById(R.id.txt_modePay);
        TextView txt_notes = findViewById(R.id.txt_notes);
        TextView button_noteRestaurant = findViewById(R.id.btn_restaurant_order);
        RelativeLayout rel_note_restaurant = findViewById(R.id.rel_note_restaurant);
        rel_note_restaurant.setVisibility(View.GONE);
        Log.d("estadores", "" + userId);
        if (userId.equals("-1")) {
            rel_note_restaurant.setVisibility(View.VISIBLE);
            button_noteRestaurant.setText(orderNote);
        } else {
            rel_note_restaurant.setVisibility(View.GONE);

        }
        txt_modePay.setText("Forma de Pago: " + orderPayment + " | Factura: SI");
        if (orderStatus.equals("0")){
            if (desc_levels.equals("Platino") || desc_levels.equals("Diamante")){
                txt_notes.setText("Notas: "+orderNote);
                txt_notes.setTextColor(getResources().getColor(R.color.red));
            } else {
                txt_notes.setVisibility(View.GONE);
            }
        } else if (orderStatus.equals("1") || orderStatus.equals("2") || orderStatus.equals("3") || orderStatus.equals("4") || orderStatus.equals("5")){
            txt_notes.setText("Notas: "+orderNote);
            txt_notes.setTextColor(getResources().getColor(R.color.red));
            txt_notes.setVisibility(View.VISIBLE);
        }

        Button btn_call = findViewById(R.id.btn_call);
        Button btn_call_client = findViewById(R.id.btn_call_client);
        Button btn_fact = findViewById(R.id.btn_fact);
        RelativeLayout rela_map = findViewById(R.id.rel_fourth);
        RelativeLayout call_client = findViewById(R.id.rel_fourth2);

        if (orderStatus.equals("0") || orderStatus.equals("3")) {
            txt_name.setText(userName);
            txt_address.setText(userAddress);
            Picasso.with(DeliveryOrderDetail.this)
                    .load(getResources().getString(R.string.link) + "uploads/restaurant/" + userImage)
                    .resize(200, 200)
                    .centerCrop()
                    .into(img_user);
            rela_map.setVisibility(View.GONE);

            if (orderStatus.equals("3")) {
                uiHelper = new UiHelper(this);
                locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                locationRequest = uiHelper.getLocationRequest();
                if (!uiHelper.isPlayServicesAvailable()) {
                    Toast.makeText(this, "Play Services no está instalado", Toast.LENGTH_SHORT).show();
                    finish();
                } else requestLocationUpdates();
                firebaseHelper = new FirebaseHelper(orderNo);
                driverOnlineFlag.set(true);
                btn_call.setText("Llamar a la Tienda");
                btn_call_client.setText("Llamar al Cliente");
                rela_map.setVisibility(View.VISIBLE);

            } else {
                driverOnlineFlag.set(false);
            }

        } else {
            txt_name.setText(restaurantName);
            txt_address.setText(restaurantAddress);
            Picasso.with(DeliveryOrderDetail.this)
                    .load(getResources().getString(R.string.link) + "uploads/restaurant/" + restaurantPhoto)
                    .resize(200, 200)
                    .centerCrop()
                    .into(img_user);
            rela_map.setVisibility(View.GONE);

            if (orderStatus.equals("5")) {
                btn_picked.setText("Llamar a la Tienda");
            }
            if (orderStatus.equals("1")) {
                uiHelper = new UiHelper(this);
                locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                locationRequest = uiHelper.getLocationRequest();
                if (!uiHelper.isPlayServicesAvailable()) {
                    Toast.makeText(this, "¡Play Services no se instaló!", Toast.LENGTH_SHORT).show();
                    finish();
                } else requestLocationUpdates();
                firebaseHelper = new FirebaseHelper(orderNo);
                driverOnlineFlag.set(true);
                btn_call.setText("Llamar a la Tienda");
                btn_call_client.setText("Llamar al Cliente");
                rela_map.setVisibility(View.VISIBLE);
            }
            if (!userDni.equals("9999999999")) {
                txt_modePay.setText("Forma de Pago: " + orderPayment + " | Factura: SI");
            } else {
                if (userId.equals("-1")) {
                    txt_modePay.setText("Dirigirse a la Tienda a: " + orderNote);
                } else {
                    txt_modePay.setText("Forma de Pago: " + orderPayment + " | Factura: SI");
                }
            }
            if (orderStatus.equals("4") || orderStatus.equals("2") || orderStatus.equals("6") || orderStatus.equals("7")) {
                driverOnlineFlag.set(false);
                btn_picked.setVisibility(View.GONE);
            }
        }


        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri;
                if (orderStatus.equals("1") || orderStatus.equals("3")) {
                    uri = "tel:" + restaurantPhone;
                } else {
                    uri = "tel:" + userPhone;
                }
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse(uri));
                startActivity(i);
                initializeView();
            }
        });

        btn_call_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri;
                if (orderStatus.equals("1") || orderStatus.equals("3")) {
                    uri = "tel:" + userPhone;
                } else {
                    uri = "tel:" + restaurantPhone;
                }
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse(uri));
                startActivity(i);
                initializeView();
            }
        });

        btn_fact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String source = "<b><font color=#303C44> Nombre: </font></b>" + userName + "<br/> "
                        + "<b><font color=#303C44> Cédula/Ruc: </font></b>" + userDni + "<br/> "
                        + "<b><font color=#303C44> Dirección: </font></b>" + userAddress + "<br/> "
                        + "<b><font color=#303C44> Teléfono: </font></b>" + userPhone.replace("+593", "0") + "<br/> "
                        + "<b><font color=#303C44> Total Producto: </font></b> $" + orderPrice.replace(".", ",") + "<br/> ";

                AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
                builder1.setTitle("Facturación");
                builder1.setMessage(Html.fromHtml(source));
                builder1.setCancelable(false);
                builder1.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert11 = builder1.create();
                try {
                    if (this != null) {
                        alert11.show();
                    }
                } catch (Exception e) {
                    //
                }


            }
        });

        Button btn_map = findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gettingGPSLocation();
            }
        });

        if (orderStatus.equals("0")) {
            btn_picked.setVisibility(View.VISIBLE);
            btn_picked.setText(R.string.picked);
            btn_picked.setBackgroundColor(getResources().getColor(R.color.res_green));

        } else if (orderStatus.equals("1")) {
            btn_picked.setVisibility(View.VISIBLE);
            btn_picked.setText(R.string.restaurantok);
            btn_picked.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        } else if (orderStatus.equals("3")) {
            btn_picked.setVisibility(View.VISIBLE);
            btn_picked.setText(R.string.complete);
            btn_picked.setBackgroundColor(getResources().getColor(R.color.res_green));

        }


        btn_picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderStatus.equals("0")) {
                    isPick = "accepted";
                    btn_picked.setBackgroundColor(getResources().getColor(R.color.res_green));
                    dialogpicked("¿Estás seguro que deseas aceptar la orden?");

                } else if (orderStatus.equals("1")) {
                    btn_picked.setBackgroundColor(getResources().getColor(R.color.res_green));

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
                    builder1.setTitle("Información");
                    builder1.setCancelable(true);
                    builder1.setMessage("Te recomendamos que antes de confirmar el tiempo de entrega, revises la ruta del cliente.");
                    builder1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            final NumberPicker picker = new NumberPicker(DeliveryOrderDetail.this);
                            final String[] data = new String[]{"                5 MINUTOS                ", "                10 MINUTOS                ", "                15 MINUTOS                ", "                20 MINUTOS                ", "                25 MINUTOS                ", "                30 MINUTOS                ", "                35 MINUTOS                ", "                40 MINUTOS                "};
                            picker.setMinValue(0);
                            picker.setMaxValue(data.length - 1);
                            picker.setDisplayedValues(data);

                            FrameLayout layout = new FrameLayout(DeliveryOrderDetail.this);
                            layout.addView(picker, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.CENTER));

                            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.AlertDialogTheme);
                            builder.setTitle("¿Qué tiempo estimas que te tomará llevar la orden al destino?")
                                    .setView(layout)
                                    .setCancelable(false)
                                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            isPick = "picked";
                                            int item = picker.getValue();

                                            if (item == 0) {
                                                timearrive = "5";
                                            } else if (item == 1) {
                                                timearrive = "10";
                                            } else if (item == 2) {
                                                timearrive = "15";
                                            }
                                            if (item == 3) {
                                                timearrive = "20";
                                            } else if (item == 4) {
                                                timearrive = "25";
                                            } else if (item == 5) {
                                                timearrive = "30";
                                            } else if (item == 6) {
                                                timearrive = "35";
                                            } else if (item == 7) {
                                                timearrive = "40";
                                            }

                                            new postingData().execute();

                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            // cancelar();
                                        }
                                    })

                                    .show();

                        }
                    });
                    builder1.setNegativeButton("Ruta del Cliente", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            GPSTracker gps = new GPSTracker();
                            gps.init(DeliveryOrderDetail.this);
                            // check if GPS enabled
                            if (gps.canGetLocation()) {
                                try {
                                    String uri = "";
                                    uri = "geo:0,0?q=" + userLat + "," + userLon;

                                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                                    initializeView();

                                } catch (NullPointerException | NumberFormatException e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                            } else {
                                gps.showSettingsAlert();
                            }
                        }
                    });


                    AlertDialog alert11 = builder1.create();
                    try {
                        if (this != null) {
                            alert11.show();
                        }
                    } catch (Exception e) {
                        //
                    }


                } else if (orderStatus.equals("3")) {
                    isPick = "complete";
                    dialogpicked("¿Estás seguro que ya entregaste la orden al cliente? ");

                } else if (orderStatus.equals("5")) {
                    String uri;
                    uri = "tel:" + restaurantPhone;
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse(uri));
                    startActivity(i);
                } else if (orderStatus.equals("4") || orderStatus.equals("2") || orderStatus.equals("6") || orderStatus.equals("7")) {
                    btn_picked.setVisibility(View.GONE);
                }

            }
        });

        list_order = findViewById(R.id.list_order2);

        /*androidx.appcompat.widget.AppCompatButton btn_bell = findViewById(R.id.btn_bell2);
        btn_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DeliveryOrderDetail.this, DeliveryStatus.class);
                startActivity(i);
            }
        });*/

    }

    private void gettingGPSLocation() {
        GPSTracker gps = new GPSTracker();
        gps.init(DeliveryOrderDetail.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            try {

                String uri = "";
                Intent intent;
                if (orderStatus.equals("3")) {
                    uri = "geo:0,0?q=" + userLat + "," + userLon;
                } else {
                    uri = "geo:0,0?q=" + restaurantLat + "," + restaurantLon;
                }

                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                initializeView();

            } catch (NullPointerException | NumberFormatException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        } else {
            gps.showSettingsAlert();
        }


    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        Log.e("fireBaseRid", "Firebase Reg id: " + regId);
    }


    class get_order_details extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DeliveryOrderDetail.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            if (DeliveryOrderDetail.this != null && !DeliveryOrderDetail.this.isFinishing()) {
                progressDialog.show();
            }
            //progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDialog.isShowing())
                progressDialog.dismiss();
            // new showResponse().execute();

        }
    }

    public void getData() {

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_order_details.php?";
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response777", response);

                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            String txt_success = obj.getString("success");
                            final String txt_message = obj.getString("message");
                            orderAssign = obj.getString("assign");
                            Log.e("ingresoforeground", orderAssign);
                            if (orderAssign.equals("0")) {
                                Intent i = new Intent(DeliveryOrderDetail.this, DeliveryStatus.class);
                                startActivity(i);
                            }
                            if (txt_success.equals("1")) {
                                getsetDeliveryorderdetail = new ArrayList<>();
                                orderDetailGetSet getSet;

                                //Order
                                JSONObject ja_order = obj.getJSONObject("order");
                                orderTextStatus = ja_order.getString("text_status");
                                orderStatus = ja_order.getString("status");
                                orderPrice = ja_order.getString("order_price");
                                orderTotal = ja_order.getString("total_general");
                                orderDeliveryPrice = ja_order.getString("delivery_price");
                                orderAmountPay = ja_order.getString("amount_pay");
                                orderPayment = ja_order.getString("payment");
                                orderTimeRest = ja_order.getString("time_rest");
                                orderChange = ja_order.getString("order_change");
                                orderNote = ja_order.getString("order_note");
                                orderNo = ja_order.getString("order_no");

                                //Cliente
                                JSONObject ja_customer = obj.getJSONObject("user");
                                userAddress = ja_customer.getString("user_address");
                                userName = ja_customer.getString("user_name");
                                userPhone = ja_customer.getString("user_phone");
                                userLat = ja_customer.getString("user_lat");
                                userLon = ja_customer.getString("user_long");
                                userImage = ja_customer.getString("user_image");
                                userId = ja_customer.getString("user_id");
                                userDni = ja_customer.getString("user_dni");

                                //Restaurante
                                JSONObject ja_restaurant = obj.getJSONObject("restaurant");
                                restaurantName = ja_restaurant.getString("restaurant_name");
                                restaurantAddress = ja_restaurant.getString("restaurant_address");
                                restaurantPhoto = ja_restaurant.getString("restaurant_photo");
                                restaurantPhone = ja_restaurant.getString("restaurant_phone");
                                restaurantLat = ja_restaurant.getString("restaurant_lat");
                                restaurantLon = ja_restaurant.getString("restaurant_lon");

                                //Levels Riders
                                JSONObject ja_riders = obj.getJSONObject("DeliveryBoyLevel");
                                level_id = ja_riders.getString("level_id");
                                desc_levels = ja_riders.getString("desc_levels");

                                Log.e("Delivery_Boy_Levels", desc_levels);

                                //items
                                initViews();

                                JSONArray jsonArray = ja_order.getJSONArray("item_name");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo_orderDetail = jsonArray.getJSONObject(i);

                                    getSet = new orderDetailGetSet();
                                    getSet.setItemName(jo_orderDetail.getString("name"));
                                    getSet.setItemQuantity(jo_orderDetail.getString("qty"));
                                    getSet.setItemPrice(jo_orderDetail.getString("amount"));
                                    getsetDeliveryorderdetail.add(getSet);
                                }
                                OrderDetailAdapter adapter = new OrderDetailAdapter(getsetDeliveryorderdetail, DeliveryOrderDetail.this);

                                Log.d("hola", adapter.toString());
                                list_order.setAdapter(adapter);

                            } else if (txt_success.equals("-4") || (txt_success.equals("-5")) || (txt_success.equals("-8")) || (txt_success.equals("-11"))) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
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

                                                Intent iv = new Intent(DeliveryOrderDetail.this, Splash.class);
                                                iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(iv);
                                            }
                                        });
                                        AlertDialog alert11 = builder1.create();
                                        try {
                                            if (this != null) {
                                                alert11.show();
                                            }
                                        } catch (Exception e) {
                                            //
                                        }
                                    }
                                });

                            } else {
                                check_sesion se = new check_sesion();
                                se.validate_sesion(DeliveryOrderDetail.this, txt_success, txt_message);

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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
                            builder1.setTitle("Información");
                            builder1.setCancelable(false);
                            builder1.setMessage("Por favor verifica tu conexión a Internet");
                            builder1.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    getData();
                                }
                            });
                            builder1.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finishAffinity();
                                }
                            });
                            builder1.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alert11 = builder1.create();
                            try {
                                if (this != null) {
                                    alert11.show();
                                }
                            } catch (Exception e) {
                                //
                            }

                        } else
                            Toast.makeText(getApplicationContext(), "Por el momento no podemos procesar tu solicitud", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", orderNo);
                params.put("deliverboy_id", deliveryBoyId);
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

        //MySingleton.getInstance(DeliveryOrderDetail.this).addToRequestQueue(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryOrderDetail.this);
        /*DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 2 * 1024 * 1024);
        RequestQueue queue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        queue.add(new ClearCacheRequest(cache, null));
        requestQueue.add(new ClearCacheRequest(cache, null));
        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        requestQueue.getCache().clear();
        requestQueue.getCache().remove(hp);*/

        //adding the string request to request queue
        requestQueue.add(stringRequest);


    }

    class postingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DeliveryOrderDetail.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.e("sourceFile", "" + orderNo);
            String hp = "";
            if (isPick.equals("accepted")) {
                if (userId.equals("-1")) {
                    hp = getString(R.string.link) + getString(R.string.serviceparcel) + "parcel_accept_deliveryboy.php";
                } else {
                    hp = getString(R.string.link) + getString(R.string.servicepath) + "order_accept_deliveryboy.php";
                }
            } else if (isPick.equals("picked")) {
                Log.e("Response12322", "picked");
                hp = getString(R.string.link) + getString(R.string.servicepath) + "order_picked_deliveryboy.php";

            } else if (isPick.equals("complete")) {
                Log.e("Response12322", "complete");
                hp = getString(R.string.link) + getString(R.string.servicepath) + "order_delivered_deliveryboy.php";
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, hp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responsedat = new JSONObject(response);
                        String txt_success = responsedat.getString("success");
                        String txt_message = responsedat.getString("message");

                        if (txt_success.equals("1")) {
                            if (orderStatus.equals("1")) {
                                check_sesion.reverseTimer(Integer.parseInt("0"), txt_orderTime);
                            } else if (orderStatus.equals("3") && (isPick.equals("complete"))) {
                                check_sesion.reverseTimer(Integer.parseInt("0"), txt_orderTime);
                                btn_picked.setVisibility(View.GONE);

                            } else if (orderStatus.equals("4") || orderStatus.equals("2") || orderStatus.equals("6") || orderStatus.equals("7")) {
                                check_sesion.reverseTimer(Integer.parseInt("0"), txt_orderTime);
                                btn_picked.setVisibility(View.GONE);
                            }

                            new get_order_details().execute();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
                            builder1.setTitle("Información");
                            builder1.setCancelable(false);
                            builder1.setMessage(txt_message);
                            builder1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (orderStatus.equals("4") && (isPick.equals("complete"))) {
                                        Intent i = new Intent(DeliveryOrderDetail.this, DeliveryStatus.class);
                                        startActivity(i);
                                    }
                                }
                            });

                            AlertDialog alert11 = builder1.create();
                            try {
                                if (this != null) {
                                    alert11.show();
                                }
                            } catch (Exception e) {
                                //
                            }

                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderDetail.this, R.style.MyDialogTheme);
                            builder1.setTitle("Información");
                            builder1.setCancelable(false);
                            builder1.setMessage(txt_message);
                            builder1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    new get_order_details().execute();
                                }
                            });

                            AlertDialog alert11 = builder1.create();
                            try {
                                if (this != null) {
                                    alert11.show();
                                }
                            } catch (Exception e) {
                                //
                            }
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
                            if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
                                Toast.makeText(getApplicationContext(), "Por favor revisa tu conexión a Internet", Toast.LENGTH_SHORT).show();

                            } else
                                Toast.makeText(getApplicationContext(), "Por el momento no podemos procesar tu solicitud", Toast.LENGTH_SHORT).show();
                        }

                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("order_id", orderNo);
                    params.put("deliverboy_id", deliveryBoyId);
                    params.put("code", getString(R.string.version_app));
                    params.put("operative_system", getString(R.string.sistema_operativo));

                    if (isPick.equals("picked")) {
                        params.put("time_orden", timearrive);
                    }
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

            //MySingleton.getInstance(DeliveryOrderDetail.this).addToRequestQueue(postRequest);
            RequestQueue requestQueue = Volley.newRequestQueue(DeliveryOrderDetail.this);
            /*DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 2 * 1024 * 1024);
            RequestQueue queue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
            queue.add(new ClearCacheRequest(cache, null));
            requestQueue.add(new ClearCacheRequest(cache, null));
            postRequest.setShouldCache(false);
            queue.getCache().clear();
            requestQueue.getCache().clear();
            requestQueue.getCache().remove(hp);*/

            //adding the string request to request queue
            requestQueue.add(postRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void initView() {
        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

}