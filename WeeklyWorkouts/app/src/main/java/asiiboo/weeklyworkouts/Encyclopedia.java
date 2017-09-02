package asiiboo.weeklyworkouts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class Encyclopedia extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private Boolean isFront;
    private LinearLayout mEncyclopediaLayout;
    private GestureDetectorCompat mDetector;
    private FrameLayout mContent;
    private BodySection mBodySection;
    private Dictionary mDictionary;
    private ArrayList<DictMuscle> front_arms, back_arms, front_torso, back_torso, front_lb, back_lb;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_front:
                    mBodySection = BodySection.LOWER_BODY;
                    updateGridView();
                    return true;
                case R.id.navigation_back:
                    mBodySection = BodySection.TORSO;
                    updateGridView();
                    return true;
                case R.id.navigation_notifications:
                    mBodySection = BodySection.ARMS;
                    updateGridView();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

        // Initialize global variables
        isFront = true;
        mEncyclopediaLayout = (LinearLayout) findViewById(R.id.container);
        mDetector = new GestureDetectorCompat(this,this);
        mContent = (FrameLayout) findViewById(R.id.content);
        mBodySection = BodySection.LOWER_BODY;
        try {
            mDictionary = new Dictionary(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        partitionExercises();
        updateGridView();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("Touched");
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if(isFront && Math.abs(velocityX) >= 1000 && Math.abs(velocityY) < Math.abs(velocityX)) {
            //mEncyclopediaLayout.setBackground(getDrawable(R.drawable.body_back));
            isFront = false;
            updateGridView();
        } else if (Math.abs(velocityX) >= 1000 && Math.abs(velocityY) < Math.abs(velocityX)){
            //mEncyclopediaLayout.setBackground(getDrawable(R.drawable.body_front));
            isFront = true;
            updateGridView();
        }
        return true;
    }

    private void updateGridView() {
        String titles[] = new String[7];
        int colors[] = {R.color.rainbow1, R.color.rainbow2, R.color.rainbow3, R.color.rainbow4, R.color.rainbow5, R.color.rainbow6, R.color.rainbow7};
        ArrayList<DictMuscle> dm;
        final DictMuscle[] dmArray;
        if (isFront) {
            switch (mBodySection) {
                case LOWER_BODY:
                    dm = front_lb;
                    mEncyclopediaLayout.setBackground(getDrawable(R.drawable.lowerbody_front));
                    break;
                case TORSO:
                    dm = front_torso;
                    mEncyclopediaLayout.setBackground(getDrawable(R.drawable.torso_front));
                    break;
                case ARMS:
                    dm = front_arms;
                    mEncyclopediaLayout.setBackground(getDrawable(R.drawable.arms_front));
                    break;
                default:
                    dm = new ArrayList<>();
            }
        } else {
            switch (mBodySection) {
                case LOWER_BODY:
                    dm = back_lb;
                    mEncyclopediaLayout.setBackground(getDrawable(R.drawable.lowerbody_back));
                    break;
                case TORSO:
                    dm = back_torso;
                    mEncyclopediaLayout.setBackground(getDrawable(R.drawable.torso_back));
                    break;
                case ARMS:
                    dm = back_arms;
                    mEncyclopediaLayout.setBackground(getDrawable(R.drawable.arms_back));
                    break;
                default:
                    dm = new ArrayList<>();
            }
        }
        dmArray = new DictMuscle[dm.size()];
        for (int i = 0; i < dm.size(); i++) {
            titles[i] = dm.get(i).getName();
            dmArray[i] = dm.get(i);
        }

        ListView lv = (ListView) findViewById(R.id.encyclopediaListView);

        // Set the width of list view of muscle legend to half the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lv.getLayoutParams().width = (int)(displayMetrics.widthPixels * 0.36);

        MyEncyclopediaAdapter adapter = new MyEncyclopediaAdapter(this, colors, dmArray);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Encyclopedia.this, EncyclopediaDetail.class);
                intent.putExtra("EXTRA_SESSION_TITLE", dmArray[i].getName());
                startActivity(intent);
            }
        });

    }
    private void partitionExercises() {
        front_arms = new ArrayList<>();
        back_arms = new ArrayList<>();
        front_torso = new ArrayList<>();
        back_torso = new ArrayList<>();
        front_lb = new ArrayList<>();
        back_lb = new ArrayList<>();

        for(int i = 0; i < mDictionary.muscleList.size(); i++) {
            DictMuscle dm = mDictionary.muscleList.get(i);
            switch (dm.getBodySection()) {
                case ARMS:
                    if(dm.isFront())
                        front_arms.add(dm);
                    else
                        back_arms.add(dm);
                    break;
                case TORSO:
                    if(dm.isFront())
                        front_torso.add(dm);
                    else
                        back_torso.add(dm);
                    break;
                case LOWER_BODY:
                    if(dm.isFront())
                        front_lb.add(dm);
                    else
                        back_lb.add(dm);
                    break;
                case UNKNOWN:
                    break;
            }
        }
    }
}



class MyEncyclopediaAdapter extends ArrayAdapter {
    private int[] colorArray;
    private DictMuscle[] muscleArray;

    MyEncyclopediaAdapter(Context context, int[] colorArray, DictMuscle[] muscleArray) {
        // 4th parameter might need to be string array
        super(context, R.layout.encyclopedia_customview, R.id.encyclopediaTitle, muscleArray);
        this.colorArray = colorArray;
        this.muscleArray = muscleArray;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.encyclopedia_customview, parent, false);

        View myRec = row.findViewById(R.id.encyclopediaRectangle);
        TextView myTitle = (TextView) row.findViewById(R.id.encyclopediaTitle);

        myRec.getBackground().setColorFilter(row.getResources().getColor(colorArray[position]), PorterDuff.Mode.MULTIPLY);
        myTitle.setText(muscleArray[position].getName());
        return row;
    }
}


