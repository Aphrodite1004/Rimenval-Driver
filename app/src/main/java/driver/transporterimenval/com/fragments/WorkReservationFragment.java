package driver.transporterimenval.com.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import driver.transporterimenval.com.Adapter.WorkReservationAdapter;
import driver.transporterimenval.com.R;
import driver.transporterimenval.com.model.WorkReservationModel;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.GlobalVariable;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class WorkReservationFragment extends MyFragment {

  private static WorkReservationFragment instance;

  private String regId;
  private static final String MY_PREFS_NAME = "Fooddelivery";

  private TextView workReservationTextView;
  private Button reservationButton;
  public RecyclerView workReservation_RecyclerView;
  RequestQueue requestQueue;

  public static WorkReservationFragment getInstance() {
    return instance;
  }

  public WorkReservationFragment() {
    // Required empty public constructor
  }

  // TODO: Rename and change types and number of parameters
  public static WorkReservationFragment newInstance() {
    WorkReservationFragment fragment = new WorkReservationFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //volley cache
    //requestQueue = MySingleton.getInstance(getContext()).getRequestQueue();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_work_reservation, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    instance = this;

    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
    regId = pref.getString("regId", null);

    workReservation_RecyclerView = view.findViewById(R.id.workReservation_RecyclerView);
    workReservation_RecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManagerOfrecyclerView = new GridLayoutManager(getActivity(), 1);
    workReservation_RecyclerView.setLayoutManager(layoutManagerOfrecyclerView);

    workReservationTextView = view.findViewById(R.id.workReservationTextView);

    reservationButton = (Button) view.findViewById(R.id.workreservationButton);
    reservationButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setReservation();
        reservationButton.setEnabled(false);
        reservationButton.setVisibility(View.INVISIBLE);
      }
    });
    workReservationTextView.setVisibility(View.GONE);
    reservationButton.setVisibility(View.GONE);
    workReservation_RecyclerView.setVisibility(View.GONE);

    getReservation();

    workReservationTextView.setVisibility(View.GONE);
    reservationButton.setVisibility(View.VISIBLE);
    workReservation_RecyclerView.setVisibility(View.VISIBLE);
  }

  private void getReservation() {

    //creating a string request to send request to the url
    String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_can_register.php?";
    StringRequest stringRequest = new StringRequest(Request.Method.POST, hp, new Response.Listener<String>() {
      @Override

      public void onResponse(String response) {
        //hiding the progressbar after completion
        try {
          //getting the whole json object from the response
          JSONObject obj = new JSONObject(response);
          String txt_success = obj.getString("success");
          Log.e("~~~~getresponse", obj.toString());

          if (txt_success.equals("true")) {
            JSONArray jsonArray = obj.getJSONArray("data");
            GlobalVariable.listOfReservation = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
              obj = jsonArray.getJSONObject(i);
              GlobalVariable.listOfReservation.add(new WorkReservationModel(obj.getString("week"), obj.getString("date"), obj.getString("time")));
            }

            RecyclerView.Adapter recyclerViewadapter = new WorkReservationAdapter(GlobalVariable.listOfReservation, getActivity());
            workReservation_RecyclerView.setAdapter(recyclerViewadapter);
            recyclerViewadapter.notifyItemRangeChanged(0, GlobalVariable.listOfReservation.size());

            workReservationTextView.setVisibility(View.GONE);
            reservationButton.setVisibility(View.VISIBLE);
            workReservation_RecyclerView.setVisibility(View.VISIBLE);

          } else if(txt_success == "false") {

            workReservationTextView.setVisibility(View.VISIBLE);
            reservationButton.setVisibility(View.GONE);
            workReservation_RecyclerView.setVisibility(View.GONE);
            String text = "Puedes establecer un horario de trabajo el " + GlobalVariable.getWeekName(obj.getString("week")) + " (" + obj.getString("date") + ").";
            workReservationTextView.setText(text);

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
                  Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_SHORT).show();
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
        params.put("deliverboy_id", getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", ""));
        params.put("code", getString(R.string.version_app));
        params.put("operative_system", getString(R.string.sistema_operativo));
        Log.e("~~~~getrequest", params.toString());
        return params;
      }

      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + regId);//markmark
        return headers;
      }

      @Override
      public String getBodyContentType() {
        return super.getBodyContentType();
      }
    };

    //MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    //creating a request queue
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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

  private void setReservation() {

    //creating a string request to send request to the url
    String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_register_schedule.php?";
    StringRequest stringRequest = new StringRequest(Request.Method.POST, hp, new Response.Listener<String>() {
      @Override

      public void onResponse(String response) {
        //hiding the progressbar after completion
        try {
          //getting the whole json object from the response
          JSONObject obj = new JSONObject(response);
          String txt_success = obj.getString("success");

          if (txt_success.equals("true")) {

            Toast.makeText(getApplicationContext(), "¡Registro exitoso!", Toast.LENGTH_SHORT).show();

            workReservationTextView.setVisibility(View.VISIBLE);
            reservationButton.setVisibility(View.GONE);
            workReservation_RecyclerView.setVisibility(View.GONE);
            String text = "Puedes establecer un horario de trabajo el " + GlobalVariable.getWeekName(obj.getString("week")) + " (" + obj.getString("date") + ").";
            workReservationTextView.setText(text);

          } else if(txt_success == "false") {
            Toast.makeText(getApplicationContext(), "¡Registro fallido!", Toast.LENGTH_SHORT).show();
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
                  Toast.makeText(getApplicationContext(), "No hay conexión a internet ", Toast.LENGTH_SHORT).show();
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
        params.put("deliverboy_id", getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("DeliveryUserId", ""));
        ;
        params.put("code", getString(R.string.version_app));
        params.put("operative_system", getString(R.string.sistema_operativo));
        Log.e("Requestcanregister", params.toString());

        String working_date = "";
        String working_time = "";

        for(int i=0; i<GlobalVariable.listOfReservation.size();i++){
          working_date = working_date + GlobalVariable.listOfReservation.get(i).get_date_workreservation() + ",";
          working_time = working_time + GlobalVariable.listOfReservation.get(i).get_time_workreservation() + ",";
        }
        params.put("working_date", working_date);
        params.put("working_time", working_time);
        Log.e("~~~~setrequest", params.toString());
        return params;
      }

      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + regId);//markmark
        return headers;
      }

      @Override
      public String getBodyContentType() {
        return super.getBodyContentType();
      }
    };

    //MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
  public void doTask(){
    RecyclerView.Adapter recyclerViewadapter = new WorkReservationAdapter(GlobalVariable.listOfReservation, getActivity());
    workReservation_RecyclerView.setAdapter(recyclerViewadapter);
    recyclerViewadapter.notifyItemChanged(GlobalVariable.positionOfReservation);
    Log.e("~~~~~~", GlobalVariable.positionOfReservation + "");
  }
}
