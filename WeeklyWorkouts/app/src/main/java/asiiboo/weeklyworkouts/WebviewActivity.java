package asiiboo.weeklyworkouts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.io.IOException;
import java.util.ArrayList;

public class WebviewActivity extends AppCompatActivity {
    UserInformation userInformation;
    Dictionary dictionary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load user data
        try {
            userInformation = (UserInformation) FileIOAssistant.load(this, FileIOAssistant.getFileName());
            dictionary = new Dictionary(this);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Load previous activity indexes
        final int muscle_idx = getIntent().getExtras().getInt("EXTRA_SESSION_DIC_MUSCLE_IDX");
        final int exercise_idx = getIntent().getExtras().getInt("EXTRA_SESSION_DIC_EXERCISE_IDX");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Workout workout = new Workout(dictionary.muscleList.get(muscle_idx).getName());
                Exercise exercise = new Exercise(dictionary.muscleList.get(muscle_idx).getExerciseList().get(exercise_idx).getName());
                if(userInformation.getWorkouts().contains(workout)) {
                    int temp_idx = userInformation.getWorkouts().indexOf(workout);
                    if(!userInformation.getWorkouts().get(temp_idx).getExercises().contains(exercise)) {
                        userInformation.getWorkouts().get(temp_idx).addExercise(exercise);
                        Snackbar.make(view, "Exercise has been added!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Exercise already exists.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    workout.addExercise(exercise);
                    userInformation.getWorkouts().add(workout);
                    Snackbar.make(view, "Workout/Exercise has been added!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                try {
                    FileIOAssistant.save(WebviewActivity.this, FileIOAssistant.getFileName(), userInformation);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(getIntent().getExtras().getString("EXTRA_SESSION_URL"));
    }


}
