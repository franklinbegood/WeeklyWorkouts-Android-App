package asiiboo.weeklyworkouts;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


class UserInformation implements Serializable {
    private int stat1;
    private int stat2;
    private int stat3;
    private int stat4;
    private ArrayList<Workout> workouts;
    private Calendar startDate;

    UserInformation() {
        stat1 = 0;
        stat2 = 0;
        stat3 = 0;
        stat4 = 0;
        startDate = Calendar.getInstance();
        workouts = new ArrayList<>();
    }

    Calendar getStartDate() {
        return this.startDate;
    }

    void setStartDate(Calendar date) {
        this.startDate = date;
    }

    int getStat1() {
        return this.stat1;
    }

    int getStat2() {
        return this.stat2;
    }

    int getStat3() {
        return this.stat3;
    }

    int getStat4() {
        return this.stat4;
    }

    void setStat1(int i) {
        this.stat1 = i;
    }

    void setStat2(int i) {
        this.stat2 = i;
    }

    void setStat3(int i) {
        this.stat3 = i;
    }

    void setStat4(int i) {
        this.stat4 = i;
    }

    ArrayList<Workout> getWorkouts() {
        return this.workouts;
    }
}
