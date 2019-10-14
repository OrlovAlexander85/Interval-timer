package ru.orlovph.intervaltimer;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    String time;

    public String time(int millis) {
        time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return time;
    }

    static CountDownTimer mCountDownTimer;


    static String[] timerStateString = {"Get ready!", "GO!", "Rest", "Finish"};

    int currentSet = 1;
    int setNumber = MainActivity.setNumber;
    int workoutInterval = MainActivity.workoutInterval + 1000;
    int restInterval = MainActivity.restInterval + 1000;
    int timer = 6000;
    int timeToSet;

    boolean isGoing = true;
    boolean isPaused = false;

    AdView adView;

    int[] timerTimeFromState = {timer, workoutInterval, restInterval};

    // State of timer : 0 = Get ready, 1 = GO!, 2 = Rest.
    int state = 0;

    TextView setsTextView;
    TextView timerTextView;
    TextView textView;
    ImageButton playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setsTextView = findViewById(R.id.setsTextView);
        timerTextView = findViewById(R.id.timerTextView);
        textView = findViewById(R.id.TextView);
        playPauseButton = findViewById(R.id.startButton);

// Размещение рекламного баннера
        adView = findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        runTimer();

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isGoing && isPaused) {
                    timeToSet += 1000;
                    resume();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    isPaused = false;
                    isGoing = true;
                } else {
                    TimerActivity.pause();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    isPaused = true;
                    isGoing = false;
                }
            }

        });


    }

    public void runTimer() {

        textView.setText(timerStateString[state]);
        String text = (currentSet) + " of " + setNumber;
        setsTextView.setText(text);
        timeToSet = (int) (timerTimeFromState[state] + 200);

        mCountDownTimer = new CountDownTimer(timeToSet, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeToSet -= 1000;
                timerTextView.setText(time(timeToSet));

                if (timeToSet > 1000 && timeToSet < 3500) {
                    shortBeep();
                } else if (timeToSet < 1000)
                    longBeep();
            }

            @Override
            public void onFinish() {
                checkState();

            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        mCountDownTimer.cancel();
        super.onBackPressed();
        this.finish();
    }

    public static void pause() {
        mCountDownTimer.cancel();
    }

    public void resume() {
        mCountDownTimer = new CountDownTimer(timeToSet + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeToSet -= 1000;
                timerTextView.setText(time(timeToSet));

                if (timeToSet > 1000 && timeToSet < 3500) {
                    shortBeep();
                } else if (timeToSet < 1000)
                    longBeep();
            }

            @Override
            public void onFinish() {
                checkState();

            }
        }.

                start();

    }

    public void checkState() {
        // GET READY
        if (state == 0) {
            state++;
            runTimer();
            // WORKOUT
        } else if (state == 1) {
            if (currentSet < setNumber) {
                state++;
                runTimer();
            } else {
                state = 3;
                checkState();
            }
            // REST
        } else if (state == 2) {
            state = 1;
            currentSet++;
            runTimer();
            // FINISH
        } else if (state == 3) {
            setsTextView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            timerTextView.setText(timerStateString[state]);
            playPauseButton.setVisibility(View.INVISIBLE);
        }

    }

    public static ToneGenerator toneG;

    public static void shortBeep() {
        try {
            if (toneG == null) {
                toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            }
            toneG.startTone(ToneGenerator.TONE_DTMF_2, 350);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (toneG !=null){
                        Log.i("ToneGenerator", "Released");
                        toneG.release();
                        toneG = null;
                    }
                }
            },350);
        }catch (Exception e){
            Log.i("Exception:", e.toString());
        }


//        toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//        toneG.startTone(ToneGenerator.TONE_DTMF_2, 350);

    }

    public static ToneGenerator toneGL;

    public static void longBeep() {
        try {
            if (toneGL == null) {
                toneGL = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            }
            toneGL.startTone(ToneGenerator.TONE_DTMF_2, 900);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (toneGL !=null){
                        Log.i("ToneGenerator toneGL", "Released");
                        toneGL.release();
                        toneGL = null;
                    }
                }
            },900);
        }catch (Exception e){
            Log.i("Exception toneGL:", e.toString());
        }


//        toneGL = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//        toneGL.startTone(ToneGenerator.TONE_DTMF_2, 900);
    }
}
