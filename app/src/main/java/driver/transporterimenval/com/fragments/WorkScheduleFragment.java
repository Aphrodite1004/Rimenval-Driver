package driver.transporterimenval.com.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import java.util.List;
import java.util.Map;

import driver.transporterimenval.com.Adapter.WorkScheduleAdapter;
import driver.transporterimenval.com.R;
import driver.transporterimenval.com.model.WorkScheduleModel;
import driver.transporterimenval.com.utils.Config;
import driver.transporterimenval.com.utils.GlobalVariable;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class WorkScheduleFragment extends Fragment {

  private String regId;
  private static final String MY_PREFS_NAME = "Fooddelivery";

  public List<WorkScheduleModel> listOfSchedule;
  public RecyclerView workSchedule_RecyclerView;
  private TextView workScheduleTextView;
  RequestQueue requestQueue;

  public WorkScheduleFragment() {
    //
  }

  public static WorkScheduleFragment newInstance() {
    WorkScheduleFragment fragment = new WorkScheduleFragment();
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
    return inflater.inflate(R.layout.fragment_work_schedule, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
    regId = pref.getString("regId", null);
    Log.e("~~~~~", regId);

    workSchedule_RecyclerView = view.findViewById(R.id.workSchedule_RecyclerView);
    workSchedule_RecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManagerOfrecyclerView = new GridLayoutManager(getActivity(), 1);
    workSchedule_RecyclerView.setLayoutManager(layoutManagerOfrecyclerView);

    workScheduleTextView = view.findViewById(R.id.workScheduleTextView);

    getSchedule();
  }

  private void getSchedule() {

    //creating a string request to send request to the url
    String hp = getString(R.string.link) + getString(R.string.servicepath) + "deliveryboy_get_registered.php?";
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

            GlobalVariable.nowDate = obj.getString("date");
            JSONArray jsonArray = obj.getJSONArray("data");

            listOfSchedule = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
              obj = jsonArray.getJSONObject(i);
              listOfSchedule.add(new WorkScheduleModel(GlobalVariable.getWeekName(obj.getString("week")), obj.getString("date"), obj.getString("time")));
            }

            RecyclerView.Adapter recyclerViewadapter = new WorkScheduleAdapter(listOfSchedule, getActivity());
            workSchedule_RecyclerView.setAdapter(recyclerViewadapter);
            recyclerViewadapter.notifyItemRangeChanged(0, listOfSchedule.size());

            workScheduleTextView.setVisibility(View.GONE);
            workSchedule_RecyclerView.setVisibility(View.VISIBLE);

          } else if(txt_success == "false") {

            workScheduleTextView.setVisibility(View.VISIBLE);
            workSchedule_RecyclerView.setVisibility(View.GONE);
            String text = "¡Aún no tienes registrado un horario de trabajo!";
            workScheduleTextView.setText(text);

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

}
