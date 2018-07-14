package dhruv.com.butim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dhruv.timerbutton.TimerButton;

public class MainActivity extends AppCompatActivity {

    private static final long MILLIS_IN_FUTURE = 6000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimerButton mTimerButton;
        Button endAnimButton = findViewById(R.id.end_anim_button);
        mTimerButton = findViewById(R.id.timer_button);
        mTimerButton.setDuration(MILLIS_IN_FUTURE);

        endAnimButton.setOnClickListener(v -> mTimerButton.end());

        Button resetAnimButton = findViewById(R.id.reset_anim_button);
        resetAnimButton.setOnClickListener(v -> mTimerButton.reset());
    }
}
