package com.ericbullington.speechtojapanese.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ericbullington.speechtojapanese.R;
import com.ericbullington.speechtojapanese.ui.MainActivity;


public class TranslationActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_translation);

        TextView contentView = (TextView) findViewById(R.id.fullscreen_content);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


        contentView.setText(message);

    }

}


