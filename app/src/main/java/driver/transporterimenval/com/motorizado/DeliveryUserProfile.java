package driver.transporterimenval.com.motorizado;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import driver.transporterimenval.com.R;


public class DeliveryUserProfile extends NotificationToAlertActivity {
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private static DeliveryUserProfile instance;
    private boolean active;
    private int count;
    private static final String MY_PREFS_ACTIVITY = "DeliveryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_user_profile);
        getSupportActionBar().hide();
        instance = this;
        active=false;
        count=0;
        initViews();

    }
    public static DeliveryUserProfile getInstance() {
        return instance;
    }

    private void initViews() {
        //TextView txt_header = findViewById(R.id.txt_header);
        //TextView txt_name_tittle = findViewById(R.id.txt_name_tittle);
        //TextView txt_vehicle_type_tittle = findViewById(R.id.txt_vehicle_type_tittle);
        //TextView txt_contact_tittle = findViewById(R.id.txt_contact_tittle);
        //TextView txt_email_tittle = findViewById(R.id.txt_email_tittle);
        //TextView txt_vehicle_no_tittle = findViewById(R.id.txt_vehicle_no_tittle);
        TextView txt_name = findViewById(R.id.txt_name);
        TextView txt_contact = findViewById(R.id.txt_contact);
        TextView txt_email = findViewById(R.id.txt_email);
        TextView txt_vehicle_no = findViewById(R.id.txt_vehicle_no);
        TextView txt_vehicle_type = findViewById(R.id.txt_vehicle_type);
        ImageView img_user = findViewById(R.id.img_user);
        ImageView img = findViewById(R.id.img12);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        try {
            Picasso.with(DeliveryUserProfile.this)
            .load(getResources().getString(R.string.link) + getString(R.string.imagepathboy) + prefs
                    .getString("DeliveryUserImage", ""))
                    .resize(300, 280)
                    .centerCrop()
                    .into(img);

            Picasso.with(DeliveryUserProfile.this)
                    .load(getResources().getString(R.string.link) + getString(R.string.imagepathboy) + prefs
                            .getString("DeliveryUserImage", ""))
                            .resize(250, 250)
                            .centerCrop()
                            .into(img_user);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }

        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //getting shared preference
        SharedPreferences sp = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        txt_name.setText(sp.getString("DeliveryUserName", ""));
        txt_contact.setText(sp.getString("DeliveryUserPhone", ""));
        txt_email.setText(sp.getString("DeliveryUserEmail", ""));
        txt_vehicle_no.setText(sp.getString("DeliveryUserVNo", ""));
        txt_vehicle_type.setText(sp.getString("DeliveryUserVType", ""));
    }

}
