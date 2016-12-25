package dhruv.com.butim;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

	private static final long MILLIS_IN_FUTURE = 10000L;
	private static final long COUNT_DOWN_INTERVAL = 1000L;
	private static final int MILLIS = 1000;

	private Button mOverButton;
	private Button mButton;
	private View mOverView;
	private ScaleAnimation mScaleAnimation;
	private CountDownTimer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		mButton = (Button) findViewById(R.id.timer_base_button);
		mOverButton = (Button) findViewById(R.id.text_button);
		Button endAnimButton = (Button) findViewById(R.id.end_anim_button);
		mOverView = findViewById(R.id.over_view);

		mButton.post(new Runnable() {
			@Override
			public void run() {

				final ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						mButton.getWidth(), mButton.getHeight()
				);
				mOverView.setLayoutParams(layoutParams);
			}
		});

		mScaleAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
		mScaleAnimation.setInterpolator(new LinearInterpolator());
		mScaleAnimation.setDuration(MILLIS_IN_FUTURE);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOverView.setVisibility(View.VISIBLE);
				mOverButton.setVisibility(View.VISIBLE);
				mOverView.startAnimation(mScaleAnimation);
				startTimer();
			}
		});

		endAnimButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mScaleAnimation.hasEnded()) {
					mOverView.clearAnimation();
					mTimer.onFinish();
					mTimer.cancel();
				}
			}
		});

		mScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mOverView.setVisibility(View.VISIBLE);
				mButton.setEnabled(false);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mOverView.setVisibility(View.GONE);
				mButton.setEnabled(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	private void startTimer() {
		mTimer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
			@Override
			public void onTick(long millisUntilFinished) {
				int left = (int) (millisUntilFinished / MILLIS);
				String formattedString = String.format(
						getString(R.string.resend_otp_formatted), left);
				mOverButton.setText(formattedString);
				mButton.setText(formattedString);
			}

			@Override
			public void onFinish() {
				mOverButton.setVisibility(View.GONE);
				mButton.setText(R.string.resend_otp);
			}
		};
		mTimer.start();
	}
}
