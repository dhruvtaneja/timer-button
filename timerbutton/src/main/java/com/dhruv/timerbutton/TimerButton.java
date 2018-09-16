package com.dhruv.timerbutton;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;


/**
 * A button that has a countdown timer running over it.
 */
public class TimerButton extends RelativeLayout implements Animation.AnimationListener, View.OnClickListener {

    private static final long INTERVAL = 500L;
    private static final int DEFAULT_TEXT_SIZE = 14;

    private Button mBaseButton;
    private View mOverView;
    private Button mTransparentButton;

    private ScaleAnimation mScaleAnimation;
    private ButtonCountDownTimer mTimer;
    private ColorStateList mTextColor;
    private ButtonAnimationListener mAnimationListener;

    private long mDuration = 10000L;
    private long mDurationLeft;
    private int mDynamicStringId;
    private int mButtonBackgroundId;
    private int mAnimationBackgroundId;
    private int mTextSize;
    private boolean mIsReset;
    private boolean mIsAnimating;
    private String mOnAnimationCompleteText = "";
    private String mBeforeAnimationText = "";
    private Typeface mTextFont;

    public TimerButton(Context context) {
        super(context);
        init();
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
        init();
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context, attrs);
        init();
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton);
        mOnAnimationCompleteText = a.getString(R.styleable.TimerButton_animationCompleteText);
        mBeforeAnimationText = a.getString(R.styleable.TimerButton_defaultText);
        mDynamicStringId = a.getResourceId(R.styleable.TimerButton_dynamicString, 0);
        mButtonBackgroundId = a.getResourceId(R.styleable.TimerButton_buttonBackground, 0);
        mAnimationBackgroundId = a.getResourceId(R.styleable.TimerButton_animationBackground, 0);
        mTextColor = a.getColorStateList(R.styleable.TimerButton_buttonTextColor);
        mTextSize = a.getDimensionPixelSize(R.styleable.TimerButton_buttonTextSize, 0);
        if (a.hasValue(R.styleable.TimerButton_buttonTextFont)) {
            int fontId = a.getResourceId(R.styleable.TimerButton_buttonTextFont, -1);
            mTextFont = ResourcesCompat.getFont(context, fontId);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            if (i == 0) {
                height = getChildAt(i).getMeasuredHeight();
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            int width  = r - l;
            int height = b - t;
            getChildAt(i).layout(0, 0, width, height);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.layout_timer_button, this);

        mBaseButton = findViewById(R.id.timer_base_button);
        mOverView = findViewById(R.id.over_view);
        mTransparentButton = findViewById(R.id.text_button);
        if (mTextFont != null) {
            mBaseButton.setTypeface(mTextFont);
        }

        setBeforeAnimationText(mBeforeAnimationText);
        setButtonBackground(mButtonBackgroundId);
        setAnimationBackground(mAnimationBackgroundId);
        mBaseButton.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(0xFF000000));
        mTransparentButton.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(0xFF000000));
        mBaseButton.setOnClickListener(this);

        Typeface typeface = getTypeface();
        if (typeface != null) {
            mBaseButton.setTypeface(typeface);
            mTransparentButton.setTypeface(typeface);
        }

        if (mTextSize > 0) {
            mBaseButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mTransparentButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        } else {
            mBaseButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
            mTransparentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
        }
    }

    /**
     * Called to set the typeface of the button during the constructor call. Override this method
     * to set your own font
     *
     * @return Typeface
     */
    public Typeface getTypeface() {
        if (mBaseButton != null) {
            return mBaseButton.getTypeface();
        }
        return null;
    }

    private void setupAnimation() {
        float fromX = ((float) (mDuration - mDurationLeft)) / mDuration;
        mScaleAnimation = new ScaleAnimation(fromX, 1.0f, 1.0f, 1.0f);
        mScaleAnimation.setInterpolator(new LinearInterpolator());
        mScaleAnimation.setDuration(mDurationLeft == 0 ? mDuration : mDurationLeft);
        mScaleAnimation.setAnimationListener(this);
    }

    /**
     * Set the duration for which the animation will run and the button will be disabled
     *
     * @param duration duration of animation
     */
    public void setDuration(long duration) {
        mDurationLeft = mDuration = duration;
    }

    /**
     * Set the text to display before the animations will run
     *
     * @param beforeAnimationText text to display before animation
     */
    public void setBeforeAnimationText(@NonNull String beforeAnimationText) {
        mBeforeAnimationText = beforeAnimationText;
        mBaseButton.setText(mBeforeAnimationText);
        mTransparentButton.setText(mBeforeAnimationText);
    }

    /**
     * Set the text to display after the animation is finished. Set as null if
     * it is supposed to be same as set in {@link #setBeforeAnimationText(String)}
     *
     * @param onAnimationCompleteText text to display after animation is finished
     */
    public void setOnAnimationCompleteText(@Nullable String onAnimationCompleteText) {
        if (mOnAnimationCompleteText == null || mOnAnimationCompleteText.isEmpty()) {
            mOnAnimationCompleteText = mBeforeAnimationText;
        }
        mOnAnimationCompleteText = onAnimationCompleteText;
    }

    /**
     * Set the string resource id to be displayed during the animation.
     * The string resource should be a formatted string that accepts one integer
     *
     * @param id string resource id
     */
    public void setDynamicText(@IdRes int id) {
        mDynamicStringId = id;
    }

    /**
     * Set the background of the button
     *
     * @param id background resource id
     */
    public void setButtonBackground(@DrawableRes int id) {
        if (id != 0) {
            mButtonBackgroundId = id;
            mBaseButton.setBackgroundResource(id);
        }
    }

    /**
     * Set the background for the overlaying animation
     *
     * @param id animation resource id
     */
    public void setAnimationBackground(@DrawableRes int id) {
        if (id != 0) {
            mAnimationBackgroundId = id;
            mOverView.setBackgroundResource(id);
        }
    }

    /**
     * Start the button animation
     */
    public void startAnimation() {
        mIsAnimating = true;
        setupTimer();
        setupAnimation();
        mOverView.startAnimation(mScaleAnimation);
        mTimer.start();
    }

    private void setupTimer() {
        mTimer = new ButtonCountDownTimer(mDurationLeft == 0 ? mDuration : mDurationLeft, INTERVAL);
    }

    /**
     * Reset button animation
     */
    public void reset() {
        mIsReset = true;
        end();
    }

    /**
     * Forcefully end the animation. Note that this is different from
     * {@link #reset()} which resets the animation to the start state
     */
    public void end() {
        if (!mScaleAnimation.hasEnded()) {
            mOverView.clearAnimation();
            mTimer.onFinish();
            mTimer.cancel();
        }
        mDurationLeft = mDuration;
    }

    /**
     * Set the {@link ButtonAnimationListener} object to receive callbacks
     * @param listener {@link ButtonAnimationListener} object
     */
    public void setButtonAnimationListener(@Nullable ButtonAnimationListener listener) {
        mAnimationListener = listener;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mOverView.setVisibility(View.VISIBLE);
        mTransparentButton.setVisibility(View.VISIBLE);
        mBaseButton.setEnabled(false);
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationStart();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimationListener = null;
        if (mTimer != null) {
            mTimer.onFinish();
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mOverView.setVisibility(View.GONE);
        mTransparentButton.setVisibility(GONE);
        mBaseButton.setEnabled(true);
        if (mIsReset) {
            mBaseButton.setText(mBeforeAnimationText);
        } else {
            mBaseButton.setText(mOnAnimationCompleteText);
        }
        mIsReset = false;
        mDurationLeft = mDuration;
        mIsAnimating = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View v) {
        startAnimation();
    }

    private class ButtonCountDownTimer extends CountDownTimer {

        private ButtonCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int left = (int) (millisUntilFinished / TimeUnit.SECONDS.toMillis(1)) + 1;
            mDurationLeft = left * 1000L;
            String formattedString = "";
            if (mDynamicStringId != 0) {
                formattedString = String.format(
                        getContext().getString(mDynamicStringId), left);
            } else {
                formattedString += left;
            }
            mBaseButton.setText(formattedString);
            mTransparentButton.setText(formattedString);
        }

        @Override
        public void onFinish() {
            if (mAnimationListener != null) {
                if (mIsReset) {
                    mAnimationListener.onAnimationReset();
                } else {
                    mAnimationListener.onAnimationEnd();
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mDurationLeft = savedState.timeInFuture;
        mIsAnimating = savedState.isAnimating;

        if (mIsAnimating) {
            startAnimation();
        } else {
            mBaseButton.setText(savedState.buttonText);
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState =  super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.timeInFuture = mDurationLeft;
        ss.width = mOverView.getWidth();
        ss.isAnimating = mIsAnimating;
        ss.buttonText = mBaseButton.getText().toString();

        return ss;
    }

    class SavedState extends BaseSavedState {

        long timeInFuture;
        int width;
        int maxWidth;
        boolean isAnimating;
        String buttonText;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(timeInFuture);
            out.writeInt(width);
            out.writeInt(maxWidth);
            out.writeByte((byte) (isAnimating ? 1 : 0));
            out.writeString(buttonText);
        }
    }
}
