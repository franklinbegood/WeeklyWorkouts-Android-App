package asiiboo.weeklyworkouts;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.util.SortedList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Dictionary Object stores the information read from exercise_data
 */

public class Dictionary {
    ArrayList<DictMuscle> muscleList;
    public Dictionary(Context myContext) throws IOException{
        AssetManager mngr = myContext.getAssets();
        InputStream is = mngr.open("exercise_data");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;

        muscleList = new ArrayList<>();
        while ((line=br.readLine()) != null) {
            String[] vars = line.split("</>");
            DictMuscle dmTemp = new DictMuscle(vars[1], vars[3], vars[4]);
            if (muscleList.contains(dmTemp)) {
                muscleList.get(muscleList.indexOf(dmTemp)).addExercise(new DictExercise(vars[0], vars[2]));
            } else {
                dmTemp.addExercise(new DictExercise(vars[0], vars[2]));
                muscleList.add(dmTemp);
            }
        }

        br.close();
        sortDictionary(this);
    }

    private void sortDictionary(Dictionary dictionary) {
        Collections.sort(muscleList);
        for(DictMuscle dm : muscleList) {
            Collections.sort(dm.getExerciseList());
        }
    }

    @Override
    public String toString() {
        String str = "";
        for(DictMuscle dm : muscleList) {
            str += "[" + dm.getName() + "] >> | ";
            for(DictExercise de : dm.getExerciseList()) {
                str += de.getName() + " | ";
            }
            str += "\n";
        }
        return str;
    }

}

class DictMuscle implements Comparable<DictMuscle> {
    private String name;
    private ArrayList<DictExercise> exerciseList;
    private Boolean isFront;
    private BodySection bodySection;

    DictMuscle(String name, String side, String bodySec) {
        this.name = name;
        exerciseList = new ArrayList<>();
        if(side.equals("F")) {
            isFront = true;
        } else {
            isFront = false;
        }
        switch (bodySec) {
            case "A":
                bodySection = BodySection.ARMS;
                break;
            case "T":
                bodySection = BodySection.TORSO;
                break;
            case "L":
                bodySection = BodySection.LOWER_BODY;
                break;
            default:
                bodySection = BodySection.UNKNOWN;
        }
    }

    String getName() {
        return this.name;
    }

    Boolean isFront() {
        return this.isFront;
    }

    BodySection getBodySection() {
        return this.bodySection;
    }

    ArrayList<DictExercise> getExerciseList() {
        return this.exerciseList;
    }

    void addExercise(DictExercise exercise) {
        if(!this.exerciseList.contains(exercise))
            this.exerciseList.add(exercise);
    }

    @Override
    public boolean equals(Object obj) {
        DictMuscle dm = (DictMuscle) obj;
        return dm.getName().equals(this.getName());
    }

    @Override
    public int compareTo(DictMuscle o) {
        return name.compareTo(o.getName()) < 0 ? -1 : name.compareTo(o.getName()) > 0 ? 1 : 0;
    }

}

class DictExercise implements Comparable<DictExercise> {
    private String name;
    private String url;

    DictExercise(String name, String url) {
        this.name = name;
        this.url = url;
    }

    String getName() {
        return name;
    }

    String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        DictExercise de = (DictExercise) obj;
        return de.getName().equals(this.getName());
    }

    @Override
    public int compareTo(DictExercise o) {
        return name.compareTo(o.getName()) < 0 ? -1 : name.compareTo(o.getName()) > 0 ? 1 : 0;
    }

}