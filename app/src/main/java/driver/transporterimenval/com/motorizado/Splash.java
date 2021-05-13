package driver.transporterimenval.com.motorizado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import driver.transporterimenval.com.R;


public class Splash extends AppCompatActivity {
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private boolean isDeliveryAccountActive = false;

    //Button crash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen
        );
        DeliveryStatus.changeStatsBarColor(Splash.this);
        
        int SPLASH_TIME_OUT = getResources().getInteger(R.integer.splash_time_out);
        //crash.setText("asfjlksfjlsf");

        new Handler().postDelayed(new Runnable() {
              /*
               * Showing splash screen with a timer. This will be useful when you
               * want to show case your app logo / company
               */
              @Override
              public void run() {
                  SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                  isDeliveryAccountActive = prefs.getBoolean("isDeliverAccountActive", false);
                  if (isDeliveryAccountActive) {
                      Intent iv = new Intent(Splash.this, DeliveryStatus.class);
                      startActivity(iv);
                      overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                      finish();
                  } else {
                          Intent iv = new Intent(Splash.this, LoginAsDelivery.class);
                          startActivity(iv);
                      overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                          finish();

                  }
              }
          },
        SPLASH_TIME_OUT);
    }

}

