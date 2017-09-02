package asiiboo.weeklyworkouts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class WeeklyWorkouts extends AppCompatActivity {
    private UserInformation userInformation;
    private final String fileName = FileIOAssistant.getFileName();
    ListView lv;
    private int lv_idx;
    MyAdapter adapter;
    Dictionary dictionary;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Weekly Workouts");

        try {
            dictionary = new Dictionary(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load muscle groups
        try {
            userInformation = (UserInformation) FileIOAssistant.load(WeeklyWorkouts.this, fileName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        if (userInformation == null)
            userInformation = new UserInformation();


        // Display Muscle groups
        updateListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialog().show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            userInformation = (UserInformation) FileIOAssistant.load(WeeklyWorkouts.this, fileName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        if (userInformation == null)
            userInformation = new UserInformation();
        updateListView();
    }


    public Dialog makeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(WeeklyWorkouts.this);
        final LayoutInflater inflater = WeeklyWorkouts.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_input_window, null);

        final AutoCompleteTextView etSectionName = (AutoCompleteTextView) view.findViewById(R.id.title_popup);
        ArrayList<String> auxAL = new ArrayList<>();
        for(DictMuscle dm : dictionary.muscleList) {
            auxAL.add(dm.getName());
        }
        String[] texts = auxAL.toArray(new String[auxAL.size()]);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, texts);
        etSectionName.setAdapter(adapter);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog f = (Dialog) dialogInterface;
                        EditText etDescription = (EditText) f.findViewById(R.id.description_popup);

                        Workout temp = new Workout(etSectionName.getText().toString());
                        temp.setDescription(etDescription.getText().toString());
                        userInformation.getWorkouts().add(temp);
                        try {
                            FileIOAssistant.save(WeeklyWorkouts.this, fileName, userInformation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateListView();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(WeeklyWorkouts.this);
        final LayoutInflater inflater = WeeklyWorkouts.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.row_input_window, null);
        final ImageView iv = (ImageView) view.findViewById(R.id.banner_iv);
        final AutoCompleteTextView etSectionName = (AutoCompleteTextView) view.findViewById(R.id.title_popup);
        final EditText etDescription = (EditText) view.findViewById(R.id.description_popup);

        iv.setImageResource(R.drawable.edit_workout);
        etSectionName.setText(userInformation.getWorkouts().get(idx).getName());
        etDescription.setText(userInformation.getWorkouts().get(idx).getDescription());

        ArrayList<String> auxAL = new ArrayList<>();
        for(DictMuscle dm : dictionary.muscleList) {
            auxAL.add(dm.getName());
        }
        String[] texts = auxAL.toArray(new String[auxAL.size()]);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, texts);
        etSectionName.setAdapter(adapter);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog f = (Dialog) dialogInterface;

                        userInformation.getWorkouts().get(idx).setName(etSectionName.getText().toString());
                        userInformation.getWorkouts().get(idx).setDescription(etDescription.getText().toString());
                        try {
                            FileIOAssistant.save(WeeklyWorkouts.this, fileName, userInformation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateListView();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public void updateListView() {
        int numWorkouts = userInformation.getWorkouts().size();
        String[] titles = new String[numWorkouts];
        String[] descriptions = new String[numWorkouts];
        int[] images = new int[numWorkouts];

        for (int i = 0; i < numWorkouts; i++) {
            titles[i] = userInformation.getWorkouts().get(i).getName();

            descriptions[i] = userInformation.getWorkouts().get(i).getDescription();
            switch (userInformation.getWorkouts().get(i).getStatus()) {
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
        lv = (ListView) findViewById(R.id.idListView);
        adapter = new MyAdapter(this, titles, descriptions, images);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WeeklyWorkouts.this, CardActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", i);
                startActivity(intent);
            }
        });

        lv.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.workout_lv_menu, menu);
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                lv_idx = info.position;
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_workout:
                deleteAlert(item.getActionView());
                break;
            case R.id.edit_workout:
                makeEditDialog(lv_idx).show();
                break;
            default:
                return false;
        }
        return true;
    }

    public void deleteAlert(final View view) {
        new AlertDialog.Builder(WeeklyWorkouts.this)
                .setTitle("Confirm Delete:")
                .setMessage("Do you really delete this workout?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        userInformation.getWorkouts().remove(lv_idx);
                        try {
                            FileIOAssistant.save(WeeklyWorkouts.this, fileName, userInformation);
                        } catch (IOException ce) {
                            ce.printStackTrace();
                        }
                        updateListView();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


}

class MyAdapter extends ArrayAdapter {
    private int[] imageArray;
    private String[] titleArray;
    private String[] descArray;

    MyAdapter(Context context, String[] titles1, String[] descriptions1, int[] img1) {
        super(context, R.layout.custlistview, R.id.idTitle, titles1);
        this.imageArray = img1;
        this.titleArray = titles1;
        this.descArray = descriptions1;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custlistview, parent, false);

        ImageView myImage = (ImageView) row.findViewById(R.id.idPic);
        TextView myTitle = (TextView) row.findViewById(R.id.idTitle);
        TextView myDescription = (TextView) row.findViewById(R.id.idDescription);


        myImage.setImageResource(imageArray[position]);
        myTitle.setText(titleArray[position]);
        myDescription.setText(descArray[position]);
        return row;
    }
}
