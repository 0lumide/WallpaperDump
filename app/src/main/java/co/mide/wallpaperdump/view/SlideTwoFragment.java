package co.mide.wallpaperdump.view;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.mide.wallpaperdump.R;

public class SlideTwoFragment extends BaseIntroSlideFragment {
    ImageView alarm, tinyOctopus;
    TextView title, description;

    @Override
    public void onPageScrolled(View view, float position) {
        float posWidth = view.getWidth() * position;
        tinyOctopus.setTranslationX(0.30F * posWidth);
        alarm.setTranslationX(0.15F * posWidth);

        float alpha = 1 - 0.8F * Math.abs(position);
        alarm.setAlpha(alpha);
        tinyOctopus.setAlpha(alpha);
        title.setAlpha(alpha);
        description.setAlpha(alpha);
    }

    @Override
    public @ColorInt
    int getDescriptionColor() {
        return ContextCompat.getColor(getContext(), R.color.colorSecondaryText);
    }

    @Override
    public @ColorInt int getTitleColor() {
        return ContextCompat.getColor(getContext(), R.color.colorPrimaryText);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.slide_two_title);
    }

    @Override
    protected String getDescription() {
        return getString(R.string.slide_two_description);
    }

    @Override
    public void onPageSelected() {
        alarm.setTranslationX(0);
        tinyOctopus.setTranslationX(0);

        alarm.setAlpha(1F);
        tinyOctopus.setAlpha(1F);
        title.setAlpha(1F);
        description.setAlpha(1F);

        float[] rotationValues = new float[25];
        rotationValues[0] = 0f;
        for (int i = 1; i < rotationValues.length; i++) {
            float[] options = new float[]{-10f, 0f, 10f, 0f};
            rotationValues[i] = options[(i - 1) % 4];
        }
        // Animate an alarm ring
        ObjectAnimator animationRotate = ObjectAnimator.ofFloat(alarm, "rotation", rotationValues);
        ObjectAnimator animationTranslate
                = ObjectAnimator.ofFloat(alarm, "translationY", 0f, -40f, 0f);
        animationRotate.setDuration(800);
        animationTranslate.setDuration(800);
        animationRotate.start();
        animationTranslate.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_intro_slide_two_stub;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarm = (ImageView) view.findViewById(R.id.image_alarm);
        tinyOctopus = (ImageView) view.findViewById(R.id.image_tiny_octopus);

        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
    }
}
