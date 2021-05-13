package driver.transporterimenval.com.motorizado;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import driver.transporterimenval.com.R;

public class NotificationToAlertActivity  extends AppCompatActivity {

    private static final String CHANNEL_ID = "12345";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("NotificationToAlert"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        // Extract data included in the Intent
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        //alert data here

        AlertDialog.Builder builder1;
        if (getLocalClassName().contains("DeliveryOrderDetail")){
            builder1 = new AlertDialog.Builder(DeliveryOrderDetail.getInstance(), R.style.MyDialogTheme);
        } else if (getLocalClassName().contains("DeliveryOrderHistory")){
            builder1 = new AlertDialog.Builder(DeliveryOrderHistory.getInstance(), R.style.MyDialogTheme);
        } else if (getLocalClassName().contains("DeliveryPaymentDetail")){
            builder1 = new AlertDialog.Builder(DeliveryPaymentDetail.getInstance(), R.style.MyDialogTheme);
        } else if (getLocalClassName().contains("DeliveryPaymentHistory")){
            builder1 = new AlertDialog.Builder(DeliveryPaymentHistory.getInstance(), R.style.MyDialogTheme);
        } else if (getLocalClassName().contains("DeliveryStatus")){
            builder1 = new AlertDialog.Builder(DeliveryStatus.getInstance(), R.style.MyDialogTheme);
        } else if (getLocalClassName().contains("DeliveryUserProfile")){
            builder1 = new AlertDialog.Builder(DeliveryUserProfile.getInstance(), R.style.MyDialogTheme);
        } else if (getLocalClassName().contains("DeliveryWorkTime")){
            builder1 = new AlertDialog.Builder(DeliveryWorkTime.getInstance(), R.style.MyDialogTheme);
        } else {
            return;
        }

        builder1.setTitle(title);
        builder1.setCancelable(false);
        builder1.setMessage(message);
        //builder1.setIcon(R.drawable.ic_notification_small);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert11 = builder1.create();
        if (NotificationToAlertActivity.this != null && !NotificationToAlertActivity.this.isFinishing()) {
            alert11.show();
        }

        }
    };

}
