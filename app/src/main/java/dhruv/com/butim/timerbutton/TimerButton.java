package dhruv.com.butim.timerbutton;


import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import dhruv.com.butim.R;

public class TimerButton extends RelativeLayout implements Animation.AnimationListener, View.OnClickListener {

    private static final long INTERVAL = 500L;
    private Button mBaseButton;
    private View mOverView;
    private Button mTransparentButton;

    private ScaleAnimation mScaleAnimation;
    private ButtonCountDownTimer mTimer;

    private long mDuration = 10000L;
    private int mDynamicStringId = 0;
    private String mOnAnimationCompleteText = "";
    private String mBeforeAnimtionText = "";

    public TimerButton(Context context) {
        super(context);
        init();
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
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

        mBaseButton = (Button) findViewById(R.id.timer_base_button);
        mOverView = findViewById(R.id.over_view);
        mTransparentButton = (Button) findViewById(R.id.text_button);

        mScaleAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
        mScaleAnimation.setInterpolator(new LinearInterpolator());
        mScaleAnimation.setDuration(mDuration);
        mScaleAnimation.setAnimationListener(this);

        mTimer = new ButtonCountDownTimer(mDuration, INTERVAL);

        mBaseButton.setOnClickListener(this);
    }

    public void setDuration(long duration) {
        mDuration = duration;
        mScaleAnimation.setDuration(mDuration);
    }

    public void setStaticText(String beforeAnimationText) {
        if (beforeAnimationText != null) {
            if (mBeforeAnimtionText.equals(beforeAnimationText)) {
                return;
            }
            mBeforeAnimtionText = beforeAnimationText;
            mBaseButton.setText(mBeforeAnimtionText);
            mTransparentButton.setText(mBeforeAnimtionText);
        }
    }

    public void setOnAnimationCompleteText(String onAnimationCompleteText) {
        mOnAnimationCompleteText = onAnimationCompleteText;
    }

    public void setDynamicText(int id) {
        mDynamicStringId = id;
    }

    public void setButtonSelector(int id) {
        mBaseButton.setBackground(getResources().getDrawable(id, null));
    }

    public void setAnimationBackground(int id) {
        mOverView.setBackground(getResources().getDrawable(id, null));
    }

    public void startAnimation() {
        mOverView.startAnimation(mScaleAnimation);
        mTimer.start();
    }

    public void reset() {
        if (!TextUtils.isEmpty(mBeforeAnimtionText)) {
            mBaseButton.setText(mBeforeAnimtionText);
            mTransparentButton.setText(mBeforeAnimtionText);
        }
        mBaseButton.setEnabled(true);
        mOverView.setVisibility(GONE);
        mTransparentButton.setVisibility(GONE);
        mOverView.clearAnimation();
        mTimer.cancel();
    }

    public void end() {
        if (!mScaleAnimation.hasEnded()) {
            mOverView.clearAnimation();
            mTimer.onFinish();
            mTimer.cancel();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mOverView.setVisibility(View.VISIBLE);
        mTransparentButton.setVisibility(View.VISIBLE);
        mBaseButton.setEnabled(false);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mOverView.setVisibility(View.GONE);
        mTransparentButton.setVisibility(GONE);
        mBaseButton.setEnabled(true);
        if (!TextUtils.isEmpty(mOnAnimationCompleteText)) {
            mBaseButton.setText(mOnAnimationCompleteText);
        } else {
            mBaseButton.setText(mBeforeAnimtionText);
        }
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
            String formattedString = "";
            if (mDynamicStringId != 0) {
                formattedString = String.format(
                        getContext().getString(mDynamicStringId), left);
            } else {
                formattedString += left;
            }
            mBaseButton.setText(formattedString);
            mBaseButton.requestLayout();
            mTransparentButton.setText(formattedString);
            mTransparentButton.requestLayout();
        }

        @Override
        public void onFinish() {

        }
    }
}
