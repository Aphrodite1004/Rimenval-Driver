package driver.transporterimenval.com.model;

public class WorkScheduleModel {

    private  String _week_workschedule;
    private String _date_workschedule;
    private String _time_workschedule;

    public WorkScheduleModel(String week_workschedule, String date_workschedule, String time_workschedule) {
        this._week_workschedule = week_workschedule;
        this._date_workschedule = date_workschedule;
        this._time_workschedule = time_workschedule;
    }

    public String get_week_workschedule() {
        return _week_workschedule;
    }

    public void set_week_workschedule(String week_workschedule) {
        this._week_workschedule = week_workschedule;
    }

    public String get_date_workschedule() {
        return _date_workschedule;
    }

    public void set_date_workschedule(String date_workschedule) {this._date_workschedule = date_workschedule;}

    public String get_time_workschedule() {
        return _time_workschedule;
    }

    public void set_time_workschedule(String time_workschedule) {this._time_workschedule = time_workschedule;}
}
