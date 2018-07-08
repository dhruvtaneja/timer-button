package com.dhruv.timerbutton;

public interface ButtonAnimationListener {

    /**
     * Callback received when button animation ends. Note that
     * it is different from {@link #onAnimationReset()} which is
     * invoked when the animation is reset to its start state
     */
    void onAnimationEnd();

    /**
     * Callback received when button animation is reset.
     */
    void onAnimationReset();

    /**
     * Callback received when button animation starts
     */
    void onAnimationStart();

}
