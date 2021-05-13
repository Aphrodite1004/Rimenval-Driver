package driver.transporterimenval.com.motorizado;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import driver.transporterimenval.com.R;


public class DialogActivity extends AppCompatActivity implements View.OnClickListener {
    private Dialog dialog;
    private TextView title_dialog,text_dialog;
    private Button yes,no;
    private String TAG = "DialogActivity";
    private String title,message,button_dialog,click_action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dialog);
        gettingIntents();
        initViews();

    }
    private void gettingIntents() {
        Intent i = getIntent();
        title = i.getStringExtra("title");
        message = i.getStringExtra("message");
        button_dialog = i.getStringExtra("button_dialog");
        click_action = i.getStringExtra("click_action");
        Log.d("response1", title+message+button_dialog+click_action);
    }

    private void initViews() {
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_dialog,null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        title_dialog= (TextView) view.findViewById(R.id.title_dialog);
        text_dialog= (TextView) view.findViewById(R.id.text_dialog);
        yes = (Button) view.findViewById(R.id.start_compaign_yes);
        no = (Button)view. findViewById(R.id.start_compaign_no);

        yes.setText(button_dialog);
        title_dialog.setText(title);
        text_dialog.setText(message);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.start_compaign_yes){
            Log.e(TAG,"customer press yes.");
            Intent dialog=new Intent(click_action);
            dialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,dialog,PendingIntent.FLAG_ONE_SHOT);
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        }else if (v.getId() == R.id.start_compaign_no){
            finishAffinity();
        }
    }
}