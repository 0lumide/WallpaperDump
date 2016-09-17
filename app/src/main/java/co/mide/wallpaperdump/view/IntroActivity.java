package co.mide.wallpaperdump.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import java.util.Random;

import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.view.SlideOneFragment;
import co.mide.wallpaperdump.view.SlideThreeFragment;
import co.mide.wallpaperdump.view.SlideTwoFragment;

public class IntroActivity extends com.miz.introactivity.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.IntroActivity);
        super.onCreate(savedInstanceState);
        //Draw under status bar lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    protected void initialize() {
        addIntroScreen(new SlideOneFragment(), randomColor());
        addIntroScreen(new SlideTwoFragment(), randomColor());
        addIntroScreen(new SlideThreeFragment(), randomColor());

        setShowSkipButton(false);
        setShowNextButton(true);
        setNextButtonBackgroundColor(Color.WHITE);
        setNextButtonIconColor(Color.WHITE);
        setProgressCircleColor(Color.WHITE);
    }

    private int randomColor() {
        return new Random().nextInt() | 0xff0000;
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public void onSkipPressed() {

    }

    @Override
    protected void onDonePressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    /**
     * Callback when the "Next" button is pressed.
     * @param pagePosition Zero-based index of the current page position.
     */
    protected void onNextPressed(int pagePosition) {

    }
}
