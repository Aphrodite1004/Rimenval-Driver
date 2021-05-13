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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import driver.transporterimenval.com.Adapter.DeliveryAdapter;
import driver.transporterimenval.com.Adapter.OrderDetailAdapter;
import driver.transporterimenval.com.Getset.DeliveryGetSet;
import driver.transporterimenval.com.R;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.check_sesion;


public class DeliveryOrderHistory extends NotificationToAlertActivity {


    private ArrayList<DeliveryGetSet> data;
    private OrderDetailAdapter adapter;
    private ListView lv_order_history;
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private static DeliveryOrderHistory instance;
    private static androidx.appcompat.widget.AppCompatButton btn_bell;
    private boolean active;
    private int count;
    private static final String MY_PREFS_ACTIVITY = "DeliveryActivity";
    private String regId,DeliveryBoyId;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order_history);
        instance = this;
        active=false;
        count=0;
        getSupportActionBar().hide();

        //volley cache
        //requestQueue = MySingleton.getInstance(DeliveryOrderHistory.this).getRequestQueue();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        DeliveryBoyId = pref.getString("DeliveryUserId",null);
    }
    public static DeliveryOrderHistory getInstance() {
        return instance;

    }


    private void initView() {
        TextView txt_title = findViewById(R.id.txt_title);
        lv_order_history = findViewById(R.id.lv_order_history);
        //btn_bell= findViewById(R.id.btn_bell1);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
        editor.putBoolean("Main", false);
        editor.putString("Activity", "DeliveryOrderHistory");
        editor.apply();

        lv_order_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(data.get(position).getStatus().equals("4")) {
                    Toast.makeText(DeliveryOrderHistory.this, "Orden #"+ data.get(position).getOrderNo() + " Entregada con éxito.", Toast.LENGTH_LONG).show();
                } else if(data.get(position).getStatus().equals("2")){
                    Toast.makeText(DeliveryOrderHistory.this, "Orden #"+ data.get(position).getOrderNo() + " Anulada.", Toast.LENGTH_LONG).show();
                }

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_ACTIVITY, MODE_PRIVATE).edit();
                editor.putBoolean("Main", false);
                editor.putString("Activity", "DeliveryOrderDetail");
                editor.apply();

                Intent i = new Intent(DeliveryOrderHistory.this, DeliveryOrderDetail.class);
                i.putExtra("DeliveryBoyId", getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", ""));
                i.putExtra("OrderNo", data.get(position).getOrderNo());
                i.putExtra("OrderAmount", data.get(position).getOrderAmount());
                i.putExtra("status", data.get(position).getStatus());
                i.putExtra("OrderTime", data.get(position).getOrderTime());
                i.putExtra("DeliveryAmount", data.get(position).getOrderDelivery());
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
                Intent i = new Intent(DeliveryOrderHistory.this, DeliveryStatus.class);
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

    private void settingData() {

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + getString(R.string.servicepath) + "order_history_delivery.php?";
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
                                DeliveryGetSet getSet;
                                data = new ArrayList<>();
                                for (int i = 0; i < ja_order.length(); i++)
                                {
                                    JSONObject jo_orderDetail = ja_order.getJSONObject(i);
                                    getSet = new DeliveryGetSet();
                                    getSet.setResName(jo_orderDetail.getString("restaurant_name"));
                                    getSet.setOrderAddress(jo_orderDetail.getString("order_address"));
                                    getSet.setOrderNo(jo_orderDetail.getString("order_no"));
                                    getSet.setOrderDelivery(jo_orderDetail.getString("delivery_price"));
                                    getSet.setOrderTime(jo_orderDetail.getString("time_rest"));
                                    getSet.setOrderDate(jo_orderDetail.getString("date"));
                                    getSet.setOrderAmount(jo_orderDetail.getString("total_amount"));
                                    getSet.setOrderComision(jo_orderDetail.getString("payment_amount"));
                                    getSet.setStatus(jo_orderDetail.getString("status"));
                                    data.add(getSet);
                                }
                                //Log.e("OrderList","=======>>"+ja_order.length());
                                DeliveryAdapter adapter = new DeliveryAdapter(data, DeliveryOrderHistory.this);
                                lv_order_history.setAdapter(adapter);
                                } else if (txt_success.equals("-4")||(txt_success.equals("-5"))||(txt_success.equals("-8"))||(txt_success.equals("-11")))  {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryOrderHistory.this, R.style.MyDialogTheme);
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


                                                Intent iv = new Intent(DeliveryOrderHistory.this, Splash.class);
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

                            }  else{
                                check_sesion se=new check_sesion();
                                se.validate_sesion(DeliveryOrderHistory.this,txt_success,txt_message);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurs
                        //Log.e("Error", "onErrorResponse: "+error.toString() );

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                            Toast.makeText(getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();

                        }
                    }
                }  )    {
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

        //MySingleton.getInstance(DeliveryOrderHistory.this).addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000*60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryOrderHistory.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

}
