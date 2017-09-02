package asiiboo.weeklyworkouts;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class EncyclopediaDetail extends AppCompatActivity {
    Dictionary dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia_detail);

        // Get title of muscle
        String activityTitle = getIntent().getExtras().getString("EXTRA_SESSION_TITLE");

        // Set title of activity
        setTitle(activityTitle);

        // Load workout data and set array
        ArrayList<DictExercise> exercises = null;
        int temp_idx = 0;
        try {
            dictionary = new Dictionary(this);
            temp_idx = dictionary.muscleList.indexOf(new DictMuscle(activityTitle, "N", "N"));
            exercises = dictionary.muscleList.get(temp_idx).getExerciseList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int aux_idx = temp_idx;

        // Create a String[] of names of exercises
        String[] exerciseNames = new String[0];
        if(exercises != null) {
            exerciseNames = new String[exercises.size()];
            for (int i = 0; i < exercises.size(); i++) {
                exerciseNames[i] = exercises.get(i).getName();
            }
        }

        // Find list view
        ListView lv = (ListView) findViewById(R.id.encyclopediaDetailListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exerciseNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(EncyclopediaDetail.this, WebviewActivity.class);
                intent.putExtra("EXTRA_SESSION_URL", dictionary.muscleList.get(aux_idx).getExerciseList().get(i).getUrl());
                intent.putExtra("EXTRA_SESSION_DIC_MUSCLE_IDX", aux_idx);
                intent.putExtra("EXTRA_SESSION_DIC_EXERCISE_IDX", i);
                startActivity(intent);
            }
        });

    }
}

