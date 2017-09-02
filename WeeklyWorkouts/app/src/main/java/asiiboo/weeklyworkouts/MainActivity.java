package asiiboo.weeklyworkouts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends Activity {
    final String fileName = FileIOAssistant.getFileName();
    UserInformation userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ImageView iv = (ImageView) findViewById(R.id.statsCharm);
        TextView stat1 = (TextView) findViewById(R.id.stat1);
        TextView stat2 = (TextView) findViewById(R.id.stat2);
        TextView stat3 = (TextView) findViewById(R.id.stat3);
        TextView stat4 = (TextView) findViewById(R.id.stat4);
        Button workoutButton = (Button) findViewById(R.id.button);
        Button encycloButton = (Button) findViewById(R.id.button2);
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WeeklyWorkouts.class));
            }
        });
        encycloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Encyclopedia.class));
            }
        });

        try {
            userInformation = (UserInformation) FileIOAssistant.load(MainActivity.this, fileName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        if (userInformation == null) {
            userInformation = new UserInformation();
        }

        //Initialize user data
        switch (updateStatus()) {
            case DONE:
                iv.setImageResource(R.drawable.done_icon);
                break;
            case IN_PROGRESS:
                iv.setImageResource(R.drawable.in_progress_icon);
                break;
            case REFUSED:
                iv.setImageResource(R.drawable.refused_icon);
                break;
        }

        // Updates stats
        updateStats(stat1, stat2, stat3, stat4);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            userInformation = (UserInformation) FileIOAssistant.load(MainActivity.this, fileName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        ImageView iv = (ImageView) findViewById(R.id.statsCharm);
        TextView stat1 = (TextView) findViewById(R.id.stat1);
        TextView stat2 = (TextView) findViewById(R.id.stat2);
        TextView stat3 = (TextView) findViewById(R.id.stat3);
        TextView stat4 = (TextView) findViewById(R.id.stat4);

        switch (updateStatus()) {
            case DONE:
                iv.setImageResource(R.drawable.done_icon);
                break;
            case IN_PROGRESS:
                iv.setImageResource(R.drawable.in_progress_icon);
                break;
            case REFUSED:
                iv.setImageResource(R.drawable.refused_icon);
                break;
        }
        updateStats(stat1, stat2, stat3, stat4);
    }

    /*public void startWeeklyWorkouts(View view) {
        startActivity(new Intent(MainActivity.this, WeeklyWorkouts.class));
    }*/

    private Status updateStatus() {
        int refuseCount = 0;
        for (Workout m : userInformation.getWorkouts()) {
            if (m.getStatus() == Status.IN_PROGRESS) {
                return Status.IN_PROGRESS;
            }
            if (m.getStatus() == Status.REFUSED)
                refuseCount++;
        }

        // If half or more than half of workouts are refused then status is refused
        if (refuseCount > userInformation.getWorkouts().size() / 2)
            return Status.REFUSED;
        else
            return Status.DONE;
    }

    private void updateStats(TextView tv1, TextView tv2, TextView tv3, TextView tv4) {
        tv1.setText(updateExercisesLeft());
        tv2.setText(updateDayNumber());
        tv3.setText(updateCompletion());
        tv4.setText(updateExercisesDone());
    }

    private String updateExercisesDone() {
        int exercisesDone = 0;
        for(Workout w : userInformation.getWorkouts())
            for(Exercise e : w.getExercises())
                if(e.getStatus() == Status.DONE)
                    exercisesDone++;
        userInformation.setStat4(exercisesDone);
        try {
            FileIOAssistant.save(this, fileName, userInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "" + exercisesDone;
    }

    private String updateCompletion() {
        double totalExercsises = 0;
        double exercisesDone = 0;
        for(Workout w : userInformation.getWorkouts())
            totalExercsises += w.getExercises().size();
        for(Workout w : userInformation.getWorkouts())
            for(Exercise e : w.getExercises())
                if(e.getStatus() == Status.DONE)
                    exercisesDone++;
        int completion = (int)((exercisesDone / totalExercsises) * 100);
        userInformation.setStat3(completion);
        try {
            FileIOAssistant.save(this, fileName, userInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return completion + "%";

    }

    private String updateExercisesLeft() {
        int exercisesLeft = 0;
        for(Workout w : userInformation.getWorkouts())
            for(Exercise e : w.getExercises())
                if(e.getStatus() == Status.IN_PROGRESS)
                    exercisesLeft++;
        userInformation.setStat1(exercisesLeft);
        try {
            FileIOAssistant.save(this, fileName, userInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "" + exercisesLeft;
    }

    @NonNull
    private String updateDayNumber() {
        Calendar rightNow = Calendar.getInstance();
        if(updateStatus() == Status.DONE) {
            userInformation.setStartDate(Calendar.getInstance());
        }

        float dayCount = (float) rightNow.compareTo(userInformation.getStartDate()) / (24 * 60 * 60 * 1000) + 1;
        userInformation.setStat2((int)dayCount);
        try {
            FileIOAssistant.save(this, fileName, userInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "" + (int) dayCount;
    }

    /*
    public void startEncyclopedia(View view) {
        startActivity(new Intent(MainActivity.this, Encyclopedia.class));
    }
    */

/*
    public void testFunctions() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500);
        animation.setDuration(5000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }*/
}
