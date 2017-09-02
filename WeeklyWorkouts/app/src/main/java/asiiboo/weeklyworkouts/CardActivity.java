package asiiboo.weeklyworkouts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class CardActivity extends AppCompatActivity {
    private final String fileName = FileIOAssistant.getFileName();
    private int workout_idx, card_idx;
    private UserInformation userInformation;
    Dictionary dictionary;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_group);

        try {
            dictionary = new Dictionary(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Get index of workout
        workout_idx = getIntent().getExtras().getInt("EXTRA_SESSION_ID");

        // Load from UserInformation save file
        try {
            userInformation = (UserInformation) FileIOAssistant.load(CardActivity.this, fileName);
        } catch (ClassNotFoundException | IOException ce) {
            ce.printStackTrace();
        }

        // Set toolbar title
        if (userInformation == null)
            getSupportActionBar().setTitle("Exercises");
        else
            getSupportActionBar().setTitle(userInformation.getWorkouts().get(workout_idx).getName());

        // Update grid view
        updateGridView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialog(workout_idx).show();
            }
        });


    }

    public Dialog makeDialog(final int idx) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
        final LayoutInflater inflater = CardActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.card_input_window, null);
        final NumberPicker npSet = (NumberPicker) view.findViewById(R.id.sets_numPic_card);
        final NumberPicker npRep = (NumberPicker) view.findViewById(R.id.reps_numPic_card);
        final AutoCompleteTextView etSectionName = (AutoCompleteTextView) view.findViewById(R.id.title_editText_card);

        ArrayList<String> auxAL = new ArrayList<>();
        String muscleName = userInformation.getWorkouts().get(idx).getName();
        if(dictionary.muscleList.contains(new DictMuscle(muscleName, "N", "N"))) {
            int muscleIdx = dictionary.muscleList.indexOf(new DictMuscle(muscleName, "N", "N"));
            for (DictExercise de : dictionary.muscleList.get(muscleIdx).getExerciseList()) {
                auxAL.add(de.getName());
            }
            String[] texts = auxAL.toArray(new String[auxAL.size()]);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, texts);
            etSectionName.setAdapter(adapter);
        }

        // Populate NumPicker values
        npSet.setMaxValue(20);
        npSet.setMinValue(0);

        npRep.setMinValue(0);
        npRep.setMaxValue(100);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog f = (Dialog) dialogInterface;
                        EditText etDescription = (EditText) f.findViewById(R.id.description_editText_card);
                        NumberPicker npSet = (NumberPicker) f.findViewById(R.id.sets_numPic_card);
                        NumberPicker npRep = (NumberPicker) f.findViewById(R.id.reps_numPic_card);


                        Exercise temp = new Exercise(etSectionName.getText().toString());
                        temp.setDescription(etDescription.getText().toString());
                        temp.setSet(npSet.getValue());
                        temp.setRep(npRep.getValue());
                        userInformation.getWorkouts().get(idx).getExercises().add(temp);
                        try {
                            FileIOAssistant.save(CardActivity.this, fileName, userInformation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateGridView();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });


        return builder.create();
    }

    public Dialog makeEditDialog(final int idx) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
        final LayoutInflater inflater = CardActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.card_input_window, null);
        final ImageView iv = (ImageView) view.findViewById(R.id.banner_card);
        final AutoCompleteTextView etSectionName = (AutoCompleteTextView) view.findViewById(R.id.title_editText_card);
        final EditText etDescription = (EditText) view.findViewById(R.id.description_editText_card);
        final NumberPicker npSet = (NumberPicker) view.findViewById(R.id.sets_numPic_card);
        final NumberPicker npRep = (NumberPicker) view.findViewById(R.id.reps_numPic_card);
        final Exercise exercise = userInformation.getWorkouts().get(workout_idx).getExercises().get(idx);


        ArrayList<String> auxAL = new ArrayList<>();
        String muscleName = userInformation.getWorkouts().get(idx).getName();

        if(dictionary.muscleList.contains(new DictMuscle(muscleName,"N","N"))) {
            int muscleIdx = dictionary.muscleList.indexOf(new DictMuscle(muscleName,"N","N"));
            for (DictExercise de : dictionary.muscleList.get(muscleIdx).getExerciseList()) {
                auxAL.add(de.getName());
            }
            String[] texts = auxAL.toArray(new String[auxAL.size()]);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, texts);
            etSectionName.setAdapter(adapter);
        }

        // Populate NumPicker values
        npSet.setMaxValue(20);
        npSet.setMinValue(0);

        npRep.setMinValue(0);
        npRep.setMaxValue(100);

        iv.setImageResource(R.drawable.edit_exercise);
        etSectionName.setText(exercise.getName());
        etDescription.setText(exercise.getDescription());
        npSet.setValue(exercise.getSet());
        npRep.setValue(exercise.getRep());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog f = (Dialog) dialogInterface;

                        exercise.setName(etSectionName.getText().toString());
                        exercise.setDescription(etDescription.getText().toString());
                        exercise.setSet(npSet.getValue());
                        exercise.setRep(npRep.getValue());
                        try {
                            FileIOAssistant.save(CardActivity.this, fileName, userInformation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateGridView();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_exercise:
                deleteAlert(item.getActionView());
                break;
            case R.id.edit_exercise:
                makeEditDialog(card_idx).show();
                break;
            case R.id.set_to_done:
                userInformation.getWorkouts().get(workout_idx).getExercises().get(card_idx).setStatus(Status.DONE);
                try {
                    FileIOAssistant.save(CardActivity.this, fileName, userInformation);
                } catch (IOException ce) {
                    ce.printStackTrace();
                }
                updateGridView();
                break;
            case R.id.set_to_in_progress:
                userInformation.getWorkouts().get(workout_idx).getExercises().get(card_idx).setStatus(Status.IN_PROGRESS);
                try {
                    FileIOAssistant.save(CardActivity.this, fileName, userInformation);
                } catch (IOException ce) {
                    ce.printStackTrace();
                }
                updateGridView();
                break;
            case R.id.set_to_refused:
                // Change icon by changing enum and updateGridView
                userInformation.getWorkouts().get(workout_idx).getExercises().get(card_idx).setStatus(Status.REFUSED);
                try {
                    FileIOAssistant.save(CardActivity.this, fileName, userInformation);
                } catch (IOException ce) {
                    ce.printStackTrace();
                }
                updateGridView();
                break;
            default:
                return false;
        }
        return true;
    }

    public void updateGridView() {
        Workout workout = userInformation.getWorkouts().get(workout_idx);
        int numExercises = workout.getExercises().size();

        String[] titles = new String[numExercises];
        int[] set = new int[numExercises];
        int[] rep = new int[numExercises];
        String[] descriptions = new String[numExercises];
        int[] images = new int[numExercises];

        for (int i = 0; i < workout.getExercises().size(); i++) {
            titles[i] = workout.getExercises().get(i).getName();
            descriptions[i] = workout.getExercises().get(i).getDescription();
            set[i] = workout.getExercises().get(i).getSet();
            rep[i] = workout.getExercises().get(i).getRep();
            switch (workout.getExercises().get(i).getStatus()) {
                case DONE:
                    images[i] = R.drawable.done_icon;
                    break;
                case IN_PROGRESS:
                    images[i] = R.drawable.in_progress_icon;
                    break;
                case REFUSED:
                    images[i] = R.drawable.refused_icon;
                    break;
            }

        }
        GridView gv = (GridView) findViewById(R.id.idGridView);
        MyCardAdapter adapter = new MyCardAdapter(this, titles, descriptions, images, set, rep);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // What do i do?
            }
        });
        gv.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.exercise_card_menu, menu);
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                card_idx = info.position;
            }
        });


    }

    public void deleteAlert(final View view) {
        new AlertDialog.Builder(CardActivity.this)
                .setTitle("Confirm Delete:")
                .setMessage("Do you really delete this exercise?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        userInformation.getWorkouts().get(workout_idx).getExercises().remove(card_idx);
                        try {
                            FileIOAssistant.save(CardActivity.this, fileName, userInformation);
                        } catch (IOException ce) {
                            ce.printStackTrace();
                        }
                        updateGridView();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


}

class MyCardAdapter extends ArrayAdapter {
    private int[] imageArray;
    private String[] titleArray;
    private String[] descArray;
    private int[] set;
    private int[] rep;


    MyCardAdapter(Context context, String[] titles1, String[] descriptions1, int[] img1
            , int[] set, int[] rep) {
        super(context, R.layout.custlistview, R.id.idTitle, titles1);
        this.imageArray = img1;
        this.titleArray = titles1;
        this.descArray = descriptions1;
        this.set = set;
        this.rep = rep;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View card = inflater.inflate(R.layout.cardview, parent, false);

        ImageView pic = (ImageView) card.findViewById(R.id.pic_imageView_card);
        TextView title = (TextView) card.findViewById(R.id.title_textView_card);
        TextView sets = (TextView) card.findViewById(R.id.sets_textView_card);
        TextView reps = (TextView) card.findViewById(R.id.reps_textView_card);
        //TextView myDescription = (TextView) row.findViewById(R.id.idDescription);


        pic.setImageResource(imageArray[position]);
        title.setText(titleArray[position]);
        String str1 = "Sets: " + set[position];
        String str2 = "Reps: " + rep[position];
        sets.setText(str1);
        reps.setText(str2);

        return card;
    }


}
