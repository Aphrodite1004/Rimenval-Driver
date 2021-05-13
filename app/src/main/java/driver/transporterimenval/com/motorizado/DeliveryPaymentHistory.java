package driver.transporterimenval.com.motorizado;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import driver.transporterimenval.com.Adapter.PaymentAdapter;
import driver.transporterimenval.com.Getset.paymentGetSet;
import driver.transporterimenval.com.R;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.check_sesion;


public class DeliveryPaymentHistory extends NotificationToAlertActivity {


    private ArrayList<paymentGetSet> data;
    private PaymentAdapter adapter;
    private ListView lv_order_history;
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private static DeliveryPaymentHistory instance;
    //private static androidx.appcompat.widget.AppCompatButton btn_bell;
    private boolean active;
    private int count;
    private String regId;
    private static final String MY_PREFS_ACTIVITY = "DeliveryActivity";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_payment);
        instance = this;
        active=false;
        count=0;

        //volley cache
        //requestQueue = MySingleton.getInstance(DeliveryPaymentHistory.this).getRequestQueue();

        getSupportActionBar().hide();
        displayFirebaseRegId();
        initView();
    }

    public static DeliveryPaymentHistory getInstance() {
        return instance;

    }

    private void initView() {
        TextView txt_title = findViewById(R.id.txt_title);
        lv_order_history = findViewById(R.id.lv_order_history);
        //btn_bell= findViewById(R.id.btn_bell1);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", false);
        editor.putString("Activity", "DeliveryPaymentHistory");
        editor.apply();

        lv_order_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryPaymentDetail");
                editor.apply();

                Intent i = new Intent(DeliveryPaymentHistory.this, DeliveryPaymentDetail.class);
                i.putExtra("PaymentNo", data.get(position).getPaymentNo());
                startActivity(i);

            }
        });

        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();

            }
        });
        settingData();

        /*btn_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(active){
                Intent i = new Intent(DeliveryPaymentHistory.this, DeliveryStatus.class);
                startActivity(i);
            }
            }
        });*/
    }

    /*public void newnotification () {
    count++;
        btn_bell.setBackgroundResource(R.drawable.yes_icon);
        btn_bell.setText(String.valueOf(count));
        active=true;
    }*/

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        Log.e("fireBaseRid", "Firebase Reg id: " + regId);
    }

    private void settingData() {

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + getString(R.string.servicepath) + "payments_history_deliveryboy.php?";
        Log.e("Response", hp);
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);

                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            String txt_success=obj.getString("success");
                            final String txt_message=obj.getString("message");

                            if (txt_success.equals("1")) {
                                JSONArray ja_order = obj.getJSONArray("order");
                                paymentGetSet getSet;
                                data = new ArrayList<>();

                                for (int i = 0; i < ja_order.length(); i++) {
                                    JSONObject jo_orderDetail = ja_order.getJSONObject(i);

                                    getSet = new paymentGetSet();
                                    getSet.setPaymentNo(jo_orderDetail.getString("id"));
                                    getSet.setPaymentMaxDate(jo_orderDetail.getString("time_start"));
                                    getSet.setOrderQuantity(jo_orderDetail.getString("items"));
                                    getSet.setPaymentAmount(jo_orderDetail.getString("amount"));
                                    getSet.setComplete(jo_orderDetail.getString("status"));
                                    getSet.setPaymentDate(jo_orderDetail.getString("payment_date"));
                                    data.add(getSet);
                                }

                                PaymentAdapter adapter = new PaymentAdapter(data, DeliveryPaymentHistory.this);
                                lv_order_history.setAdapter(adapter);


                            } else if (txt_success.equals("-4")||(txt_success.equals("-5"))||(txt_success.equals("-8"))||(txt_success.equals("-11")))  {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryPaymentHistory.this, R.style.MyDialogTheme);
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
                                                editor.putString("DeliveryUserPhone", "");
                                                editor.putString("DeliveryUserEmail", "");
                                                editor.putString("DeliveryUserVNo", "");
                                                editor.putString("DeliveryUserVType", "");
                                                editor.putString("DeliveryUserImage", "");
                                                editor.apply();

                                                SharedPreferences.Editor editor2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).edit();
                                                editor2.putString("regId",null);
                                                editor2.apply();


                                                Intent iv = new Intent(DeliveryPaymentHistory.this, Splash.class);
                                                iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(iv);

                                            }
                                        });
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }
                                });

                            }  else{
                                check_sesion se=new check_sesion();
                                se.validate_sesion(DeliveryPaymentHistory.this,txt_success,txt_message);

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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryPaymentHistory.this, R.style.MyDialogTheme);
                            builder1.setTitle("Información");
                            builder1.setCancelable(false);
                            builder1.setMessage("Por favor verifica tu conexión a Internet");
                            builder1.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //new user_check_restaurant_location().execute();
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

                            }


                        } else
                            Toast.makeText(getApplicationContext(), "Por el momento no podemos procesar tu solicitud", Toast.LENGTH_SHORT).show();
                    }

                })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("deliverboy_id", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", ""));
                params.put("code", getString(R.string.version_app));
                params.put("operative_system",  getString(R.string.sistema_operativo));
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

        //MySingleton.getInstance(DeliveryPaymentHistory.this).addToRequestQueue(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryPaymentHistory.this);
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
