package com.ericbullington.speechtojapanese;


import android.app.Activity;

import java.io.File;

import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.ericbullington.speechtojapanese.ui.ColoredButton;

public class MainActivity extends Activity {
    
    private static final String TAG="MainActivity";

    private boolean isButtonGreen = false;
    private AudioRecorder audioRecorder = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI
        setContentView(R.layout.main);

        // Instantiate the application's audio recorder
        audioRecorder = new AudioRecorder(this);

        // Set click listener
        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColoredButton btn = (ColoredButton) findViewById(R.id.btnStart);
                if (isButtonGreen) {
                    isButtonGreen = false;
                    v.clearAnimation();
                    Log.i(TAG, "Recording stopped.");
                    audioRecorder.stop();
                    btn.setColorGreen();
                } else {
                    isButtonGreen = true;
                    btn.setColorRed();
                    Animation mAnimation = new AlphaAnimation(1, 0);
                    mAnimation.setDuration(600);
                    mAnimation.setInterpolator(new StepInterpolator(0.5f));
                    mAnimation.setRepeatCount(Animation.INFINITE);
                    mAnimation.setRepeatMode(Animation.REVERSE);
                    btn.startAnimation(mAnimation);
                    Log.i(TAG, "Now recording...");
                    audioRecorder.start();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Clean up temporary recorder files
        File dir = audioRecorder.getFileDirectory();
        deleteTempFiles(dir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean deleteTempFiles(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChild : children) {
                new File(dir, aChild).delete();
            }
        }
        return true;
    }

}
