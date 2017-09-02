package asiiboo.weeklyworkouts;

import java.io.Serializable;
import java.util.ArrayList;

public class Workout implements Serializable {
    private String name;
    private String description;
    private Status status;
    private ArrayList<Exercise> exercises;

    Workout(String name) {
        this.name = name;
        this.exercises = new ArrayList<>();
        this.description = "";
    }

    public void addExercise(Exercise w) {
        this.exercises.add(w);
    }

    ArrayList<Exercise> getExercises() {
        return this.exercises;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setDescription(String str) {
        this.description = str;
    }

    String getDescription() {
        return this.description;
    }

    Status getStatus() {
        updateStatus();
        return this.status;
    }

    private void updateStatus() {
        int refuseCount = 0;
        boolean inProgress = false;
        for (Exercise w : exercises) {
            if (w.getStatus() == Status.REFUSED) {
                refuseCount++;
            }
            if (refuseCount >= 3) {
                this.status = Status.REFUSED;
                return;
            }
            if (w.getStatus() == Status.IN_PROGRESS)
                inProgress = true;
        }
        if (inProgress) {
            this.status = Status.IN_PROGRESS;
        } else {
            this.status = Status.DONE;
        }
    }

    @Override
    public boolean equals(Object o) {
        return name.equals(((Workout)o).getName());
    }
}
