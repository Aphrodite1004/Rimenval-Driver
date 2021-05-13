package driver.transporterimenval.com.motorizado;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import driver.transporterimenval.com.R;
import driver.transporterimenval.com.fragments.WorkReservationFragment;
import driver.transporterimenval.com.fragments.WorkScheduleFragment;
import driver.transporterimenval.com.utils.GlobalVariable;

public class DeliveryWorkTime extends NotificationToAlertActivity  implements RangeTimePickerDialog.ISelectedTime {
//    private static final String MY_PREFS_NAME = "Fooddelivery";
    private static DeliveryWorkTime instance;
//    private static androidx.appcompat.widget.AppCompatButton btn_bell;
//    private boolean active;
//    private int count;
//    private static final String MY_PREFS_ACTIVITY = "DeliveryActivity";
    private String nowFragment = "";

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_work_time);
        getSupportActionBar().hide();
        instance = this;

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(WorkScheduleFragment.newInstance(), "HomeFragment");

        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DeliveryWorkTime.this, DeliveryStatus.class);
        i.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        this.finish();
    }

    public static DeliveryWorkTime getInstance() {
        return instance;
    }

    public void openFragment(Fragment fragment, String fragmentName) {
        if (nowFragment.equals(fragmentName)) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentName.equals("WorkReservationFragment")){
            nowFragment= "WorkReservationFragment";
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        } else {
            nowFragment= "WorkScheduleFragment";
            transaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
        }
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    switch (item.getItemId()) {
                        case R.id.navigation_work_schedule:
                            openFragment(WorkScheduleFragment.newInstance(), "WorkScheduleFragment");
                            return true;
                        case R.id.navigation_work_reservation:
                            openFragment(WorkReservationFragment.newInstance(), "WorkReservationFragment");
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd)
    {
        String time = "";
        time = time + (hourStart <= 12 ? (String.format("%02d", hourStart) + ":" + String.format("%02d", minuteStart) + " AM"):(String.format("%02d", hourStart - 12) + ":" + String.format("%02d", minuteStart) + " PM"));
        time = time + " - ";
        time = time + (hourEnd <= 12 ? (String.format("%02d", hourEnd) + ":" + String.format("%02d", minuteEnd) + " AM"):(String.format("%02d", hourEnd - 12) + ":" + String.format("%02d", minuteEnd) + " PM"));
        GlobalVariable.listOfReservation.get(GlobalVariable.positionOfReservation).set_time_workreservation(time);
        Toast.makeText(this, "Inicio: "+hourStart+":"+minuteStart+" | Fin: "+hourEnd+":"+minuteEnd, Toast.LENGTH_SHORT).show();
        WorkReservationFragment.getInstance().doTask();
    }
}
