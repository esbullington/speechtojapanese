package com.ericbullington.speechtojapanese;

import android.view.animation.Interpolator;


// From StackOverflow user "cold ash"
// http://stackoverflow.com/questions/23293052/blinking-alpha-animation-with-no-fade-effect
public class StepInterpolator implements Interpolator {

    float mDutyCycle;
    public StepInterpolator(float dutyCycle) {
        mDutyCycle = dutyCycle;
    }

    @Override
    public float getInterpolation(float input) {
        return input < mDutyCycle ? 0 : 1;
    }

}