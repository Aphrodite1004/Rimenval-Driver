package driver.transporterimenval.com.model;

public class WorkReservationModel {

    private  String _week_workreservation;
    private String _date_workreservation;
    private String _time_workreservation;

    public WorkReservationModel(String week_workreservation, String date_workreservation, String time_workreservation) {
        this._week_workreservation = week_workreservation;
        this._date_workreservation = date_workreservation;
        this._time_workreservation = time_workreservation;
    }

    public String get_week_workreservation() {
        return _week_workreservation;
    }

    public void set_week_workreservation(String week_workreservation) {
        this._week_workreservation = week_workreservation;
    }

    public String get_date_workreservation() {
        return _date_workreservation;
    }

    public void set_date_workreservation(String date_workreservation) {this._date_workreservation = date_workreservation;}

    public String get_time_workreservation() {
        return _time_workreservation;
    }

    public void set_time_workreservation(String time_workreservation) {this._time_workreservation = time_workreservation;}
}
