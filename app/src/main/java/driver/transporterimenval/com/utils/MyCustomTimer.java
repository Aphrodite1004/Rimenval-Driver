package driver.transporterimenval.com.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import driver.transporterimenval.com.motorizado.DeliveryStatus;

public class MyCustomTimer implements Runnable {
    public MyCustomTimer()  {

    }
    public void setTimer(int Seconds, final TextView tv) {

       new CountDownTimer(Seconds* 1000+1000, 1000) {


            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                if(minutes==0 && seconds<=60){
                    tv.setTextColor(Color.RED);
                }

                tv.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }
            public void onFinish() {
                tv.setText("");
                DeliveryStatus.getInstance();
            }
        }.start();

    }

    @Override
    public void run() {

    }
}