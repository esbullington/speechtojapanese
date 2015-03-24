package com.ericbullington.speechtojapanese;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;


public class ColoredButton extends ImageButton {

    private boolean isGreen = true;

    public ColoredButton(Context context) {
        super(context);
    }

    public ColoredButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColoredButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void toggleColor() {
        this.isGreen = !isGreen;
        changeBgColor();
    }

    public void setColorGreen() {
        this.isGreen = true;
        changeBgColor();
    }

    public void setColorRed() {
        this.isGreen = false;
        changeBgColor();
    }

    private void changeBgColor() {
        if (isGreen) {
            setImageResource(R.drawable.ic_action_playback_play);
        } else {
            setImageResource(R.drawable.ic_action_playback_stop);
        }
    }

    public boolean onTouchEvent (@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleColor();
        }
        return super.onTouchEvent(event);
    }


}
