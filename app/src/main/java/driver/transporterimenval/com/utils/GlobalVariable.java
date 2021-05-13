package driver.transporterimenval.com.utils;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import driver.transporterimenval.com.model.WorkReservationModel;

public class GlobalVariable extends Application {
    public static List<WorkReservationModel> listOfReservation = new ArrayList<>();

    public static int positionOfReservation = 0;

    public static String nowDate = "";

    public List<WorkReservationModel> getSomeVariable() {
        return listOfReservation;
    }

    public void setSomeVariable(List<WorkReservationModel> _listOfReservation) {
        this.listOfReservation = _listOfReservation;
    }

    public static String getWeekName(String weekNumber){
        String weekName = "";
        switch (weekNumber){
            case "1":
                weekName = "Lunes";
                break;
            case "2":
                weekName = "Martes";
                break;
            case "3":
                weekName = "Miércoles";
                break;
            case "4":
                weekName = "Jueves";
                break;
            case "5":
                weekName = "Viernes";
                break;
            case "6":
                weekName = "Sábado";
                break;
            case "7":
                weekName = "Domingo";
                break;
            default:
                weekName = "";
                break;
        }
        return weekName;
    }
}
