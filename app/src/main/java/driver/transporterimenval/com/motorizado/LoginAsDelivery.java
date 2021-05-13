package driver.transporterimenval.com.motorizado;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import driver.transporterimenval.com.R;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.check_sesion;


public class LoginAsDelivery extends AppCompatActivity {

    private TextView txt_title;
    private TextInputEditText edit_email;
    private TextInputEditText edit_pwd;
    private Button btn_login;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private String regId;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as_delivery);
        getSupportActionBar().hide();
        DeliveryStatus.changeStatsBarColor(LoginAsDelivery.this);
        initViews();

        //volley cache
        //requestQueue = MySingleton.getInstance(LoginAsDelivery.this).getRequestQueue();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        if (regId==null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginAsDelivery.this, new OnSuccessListener<InstanceIdResult>() {

                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {

                    String newToken = instanceIdResult.getToken();
                    Log.e("newToken", newToken);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("regId", newToken);
                    editor.apply();
                    Log.d("newtokenfromfirebase", "" + newToken);

                    regId = pref.getString("regId", null);
                }
            });
        }

    }

    private void initViews() {
        txt_title = findViewById(R.id.txt_title);
        edit_email = findViewById(R.id.edit_email);
        edit_pwd = findViewById(R.id.edit_pwd);
        btn_login = findViewById(R.id.btn_login);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitform();
            }
        });

    }

    private boolean validateEmail() {
        String email = edit_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(edit_email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (edit_pwd.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(edit_pwd);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void submitform()
    {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        String userEmail = edit_email.getText().toString();
        String password = edit_pwd.getText().toString();
        submitdata(userEmail, password);
    }


    private void submitdata(final String userEmail, final String password) {

        //creating a string request to send request to the url
        String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_login.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp, new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {
                //hiding the progressbar after completion
                Log.e("Response", response);
                try {
                    //getting the whole json object from the response
                    JSONObject obj = new JSONObject(response);
                    String txt_success = obj.getString("success");
                    String txt_message = obj.getString("message");

                    if (txt_success.equals("1")) {
                        JSONArray ja_login = obj.getJSONArray("deliveryboy_detail");
                        JSONObject jo_detail = ja_login.getJSONObject(0);
                        String id = jo_detail.getString("id");
                        String level_id = jo_detail.getString("level_id");
                        String name = jo_detail.getString("name");
                        String phone = jo_detail.getString("phone");
                        String image = jo_detail.getString("image");
                        String email = jo_detail.getString("email");
                        String vehicle_no = jo_detail.getString("vehicle_no");
                        String vehicle_type = jo_detail.getString("vehicle_type");
                        String desc_levels = jo_detail.getString("DESC_LEVELS");
                        saveToSharedPref(id, level_id, name, phone, email, vehicle_no, vehicle_type, image, desc_levels);
                    }
                    else   if (txt_success.equals("-11")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginAsDelivery.this, R.style.MyDialogTheme);
                        builder1.setTitle("Información");
                        builder1.setCancelable(false);
                        builder1.setMessage(txt_message);
                        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                    else{
                        check_sesion se=new check_sesion();
                        se.validate_sesion(LoginAsDelivery.this,txt_success,txt_message);
                    }
                }
                catch (JSONException e)
                {
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginAsDelivery.this, R.style.MyDialogTheme);
                            builder1.setTitle("Información");
                            builder1.setCancelable(false);
                            builder1.setMessage("Por favor verifica tu conexión a Internet");
                            builder1.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String userEmail = edit_email.getText().toString();
                                    String password = edit_pwd.getText().toString();
                                    submitdata(userEmail, password);
                                }
                            });
                            builder1.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finishAffinity();
                                }
                            });

                            AlertDialog alert11 = builder1.create();
                            try {
                                if (this != null) {
                                    alert11.show();
                                }
                            }catch (Exception e){

                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Por el momento no podemos procesar tu solicitud", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                params.put("password", password);
                params.put("token", regId);
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

        //MySingleton.getInstance(LoginAsDelivery.this).addToRequestQueue(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginAsDelivery.this);
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
    public void onBackPressed() {
        showExitDialog();

    }

    private void showExitDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginAsDelivery.this, R.style.MyDialogTheme);
        builder1.setTitle(getString(R.string.Quit));
        builder1.setIcon(R.mipmap.ic_launcher);
        builder1.setMessage(getString(R.string.statementquit));
        builder1.setCancelable(true);
        builder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
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

    private void saveToSharedPref(String id, String level_id, String name, String phone, String email, String vehicle_no, String vehicle_type, String image, String desc_levels) {
        SharedPreferences.Editor edit = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        edit.putBoolean("isDeliverAccountActive", true);
        edit.putString("DeliveryUserId", id);
        edit.putString("DeliveryUserLevelId", level_id);
        edit.putString("DeliveryUserName", name);
        edit.putString("DeliveryUserPhone", phone);
        edit.putString("DeliveryUserEmail", email);
        edit.putString("DeliveryUserVNo", vehicle_no);
        edit.putString("DeliveryUserVType", vehicle_type);
        edit.putString("DeliveryUserImage", image);
        edit.putString("DeliveryUserDescLevel", desc_levels);
        edit.apply();
        Intent i = new Intent(LoginAsDelivery.this, DeliveryStatus.class);
        startActivity(i);
        finish();

    }
}
