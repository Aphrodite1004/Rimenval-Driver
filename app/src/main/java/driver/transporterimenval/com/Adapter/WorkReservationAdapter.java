package driver.transporterimenval.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.util.List;

import driver.transporterimenval.com.R;
import driver.transporterimenval.com.model.WorkReservationModel;
import driver.transporterimenval.com.motorizado.DeliveryWorkTime;
import driver.transporterimenval.com.utils.GlobalVariable;

/**
 * Created by Eric on 02-Nov-17.
 */

public class WorkReservationAdapter extends RecyclerView.Adapter<WorkReservationAdapter.ViewHolder> {

    private final Context context;

    private final List<WorkReservationModel> dataAdapters;



    public WorkReservationAdapter(List<WorkReservationModel> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workreservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder Viewholder, int position) {

        final WorkReservationModel dataAdapterOBJ = dataAdapters.get(position);
        Viewholder.week_workreservation.setText(GlobalVariable.getWeekName(dataAdapterOBJ.get_week_workreservation()));
        Viewholder.date_workreservation.setText(dataAdapterOBJ.get_date_workreservation());
        Viewholder.time_workreservation.setText(dataAdapterOBJ.get_time_workreservation());

        Viewholder.worktime_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVariable.positionOfReservation = position;
                String time;
                String startTime, endTime;
                int startHour = 7, startMinute = 0, endHour = 22, endMinute = 0;
                try{
                    time = dataAdapterOBJ.get_time_workreservation();
                    startTime = time.split("-")[0].replace(" ", "");
                    endTime = time.split("-")[1].replace(" ", "");

                    startHour =Integer.valueOf(startTime.substring(0, startTime.length()-2).split(":")[0]);
                    startMinute =Integer.valueOf(startTime.substring(0, startTime.length()-2).split(":")[1]);
                    if (startTime.contains("PM")){
                        startHour %=  12;
                        startHour += 12;

                    }
                    endHour =Integer.valueOf(endTime.substring(0, endTime.length()-2).split(":")[0]);
                    endMinute =Integer.valueOf(endTime.substring(0, endTime.length()-2).split(":")[1]);
                    if (endTime.contains("PM")){
                        endHour %= 12;
                        endHour += 12;
                    }
                } catch (Exception e){
                    startHour = 7;
                    startMinute = 0;
                    endHour = 22;
                    endMinute = 0;
                }


                RangeTimePickerDialog dialog = new RangeTimePickerDialog();
                dialog.newInstance();
                dialog.setIs24HourView(false);
                dialog.setRadiusDialog(20);
                dialog.setTextTabStart("Inicio");
                dialog.setTextTabEnd("Fin");
                dialog.setCancelable(false);
                dialog.setTextBtnNegative("Cancelar");
                dialog.setTextBtnPositive("Aceptar");
                dialog.setValidateRange(false);
                dialog.setColorBackgroundHeader(R.color.colorPrimary);
                dialog.setColorBackgroundTimePickerHeader(R.color.colorPrimary);
                dialog.setColorTextButton(R.color.colorPrimaryDark);
                dialog.enableMinutes(true);
                dialog.setStartTabIcon(R.drawable.ic_access_time_black_24dp);
                dialog.setEndTabIcon(R.drawable.ic_timelapse_black_24dp);
                dialog.setMinimumSelectedTimeInMinutes(0,false);
                dialog.setInitialOpenedTab(RangeTimePickerDialog.InitialOpenedTab.START_CLOCK_TAB);
                dialog.setInitialStartClock(startHour, startMinute);
                dialog.setInitialEndClock(endHour, endMinute);
                dialog.show(((DeliveryWorkTime) context).getSupportFragmentManager(), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView week_workreservation;
        final TextView date_workreservation;
        final TextView time_workreservation;
        final FloatingActionButton worktime_Button;

        ViewHolder(View itemView) {
            super(itemView);
            week_workreservation = itemView.findViewById(R.id.week_workreservation);
            date_workreservation = itemView.findViewById(R.id.date_workreservation);
            time_workreservation = itemView.findViewById(R.id.time_workreservation);
            worktime_Button = itemView.findViewById(R.id.worktime_Button);
        }
    }

    public void addList(List<WorkReservationModel> dataAdapter){
        dataAdapters.addAll(dataAdapter);
        notifyItemRangeChanged(0,dataAdapters.size());
    }
    public void refreshList(){
        notifyItemRangeChanged(0,dataAdapters.size());
    }
    public List<WorkReservationModel> getListData(){
        return dataAdapters;
    }

}
