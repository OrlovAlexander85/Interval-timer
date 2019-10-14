package ru.orlovph.intervaltimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static int setNumber = 2;
    public static int workoutInterval = 30000;
    public static int restInterval = 10000;
    final int REP_DELAY = 50;

    String tag;
    String time;

    TextView setsTextView;
    TextView workoutTextView;
    TextView restIntervalTextView;

    Button setsPlus;
    Button setsMinus;
    Button workIntervalPlus;
    Button workIntervalMinus;
    Button restIntervalPlus;
    Button restIntervalMinus;

    AdView adView;

    static ImageButton startTimer;

    public String time(int millis) {
        time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return time;
    }

    public void editSetNumber(View view) {
        tag = String.valueOf(view.getTag());
        switch (tag) {
            case "plus":
                setNumber++;
                setsTextView.setText(String.valueOf(setNumber));
                break;
            case "minus":
                if (setNumber > 2) {
                    setNumber--;
                    setsTextView.setText(String.valueOf(setNumber));
                    break;
                }

        }
    }

    // onLongClickListener для кнопок увеличени и уменьшения:
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private Handler repeatUpdateHandler = new Handler();

    class RptUpdaterRest implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                incrementRest();
                repeatUpdateHandler.postDelayed(new RptUpdaterRest(), REP_DELAY);
            } else if (mAutoDecrement) {
                decrementRest();
                repeatUpdateHandler.postDelayed(new RptUpdaterRest(), REP_DELAY);
            }
        }
    }

    class RptUpdaterWorkout implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                incrementWorkout();
                repeatUpdateHandler.postDelayed(new RptUpdaterWorkout(), REP_DELAY);
            } else if (mAutoDecrement) {
                decrementWorkout();
                repeatUpdateHandler.postDelayed(new RptUpdaterWorkout(), REP_DELAY);
            }
        }
    }

    public void incrementRest() {
        restInterval += 1000;
        restIntervalTextView.setText(time(restInterval));
    }

    public void decrementRest() {
        if (restInterval > 1000) {
            restInterval -= 1000;
            restIntervalTextView.setText(time(restInterval));
        }
    }

    public void incrementWorkout() {
        workoutInterval += 1000;
        workoutTextView.setText(time(workoutInterval));
    }

    public void decrementWorkout() {
        if (workoutInterval > 1000) {
            workoutInterval -= 1000;
            workoutTextView.setText(time(workoutInterval));
        }
    }

    //

    public void editWorkoutTimer(View view) {
        tag = String.valueOf(view.getTag());
        switch (tag) {
            case "plus":
                workoutInterval += 1000;
                workoutTextView.setText(time(workoutInterval));
                break;
            case "minus":
                if (workoutInterval > 1000) {
                    workoutInterval -= 1000;
                    workoutTextView.setText(time(workoutInterval));
                    break;
                }

        }
    }

    public void editRestTimer(View view) {
        tag = String.valueOf(view.getTag());
        switch (tag) {
            case "plus":
                restInterval += 1000;
                restIntervalTextView.setText(time(restInterval));
                break;
            case "minus":
                if (restInterval > 1000) {
                    restInterval -= 1000;
                    restIntervalTextView.setText(time(restInterval));
                    break;
                }

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTimer = findViewById(R.id.startButton);

        setsTextView = findViewById(R.id.numberOfSetsTextView);
        workoutTextView = findViewById(R.id.workoutTimerTextView);
        restIntervalTextView = findViewById(R.id.restIntervalTimerTextView);

        setsPlus = findViewById(R.id.buttonSetsPlus);
        setsMinus = findViewById(R.id.buttonSetsMinus);
        workIntervalPlus = findViewById(R.id.buttonWorkoutPlus);
        workIntervalMinus = findViewById(R.id.buttonWorkoutMinus);
        restIntervalPlus = findViewById(R.id.buttonRestPlus);
        restIntervalMinus = findViewById(R.id.buttonRestMinus);

// Размещение рекламного баннера
        adView = findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimerActivity();
            }

        });
// RestInterval OnLongClickListener
        restIntervalPlus.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoIncrement = true;
                        repeatUpdateHandler.post(new RptUpdaterRest());
                        return true;
                    }
                }
        );

        restIntervalPlus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement) {
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        restIntervalMinus.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoDecrement = true;
                        repeatUpdateHandler.post(new RptUpdaterRest());
                        return true;
                    }
                }
        );

        restIntervalMinus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement) {
                    mAutoDecrement = false;
                }
                return false;
            }
        });
// End of RestInterval OnLongClickListener.


// WorkoutInterval OnLongClickListener
        workIntervalPlus.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoIncrement = true;
                        repeatUpdateHandler.post(new RptUpdaterWorkout());
                        return true;
                    }
                }
        );

        workIntervalPlus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement) {
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        workIntervalMinus.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoDecrement = true;
                        repeatUpdateHandler.post(new RptUpdaterWorkout());
                        return true;
                    }
                }
        );

        workIntervalMinus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement) {
                    mAutoDecrement = false;
                }
                return false;
            }
        });
// End of WorkoutInterval OnLongClickListener.

    }

    public void startTimerActivity() {
        Intent intent = new Intent(MainActivity.this, TimerActivity.class);
        startActivity(intent);
    }
}
