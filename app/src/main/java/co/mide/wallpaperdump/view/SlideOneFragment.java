package co.mide.wallpaperdump.view;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.mide.wallpaperdump.R;

public class SlideOneFragment extends BaseIntroSlideFragment {
    ImageView banana, guitar, fork;
    TextView title, description;

    @Override
    public void onPageScrolled(View view, float position) {
        float posWidth = view.getWidth() * position;
        banana.setTranslationX(0.30F * posWidth);
        fork.setTranslationX(0.15F * posWidth);
        guitar.setTranslationX(0F);

        float alpha = 1 - 0.8F * Math.abs(position);
        banana.setAlpha(alpha);
        guitar.setAlpha(alpha);
        fork.setAlpha(alpha);
        title.setAlpha(alpha);
        description.setAlpha(alpha);
    }

    @Override
    public @ColorInt int getDescriptionColor() {
        return ContextCompat.getColor(getContext(), R.color.colorSecondaryText);
    }

    @Override
    public @ColorInt int getTitleColor() {
        return ContextCompat.getColor(getContext(), R.color.colorPrimaryText);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.slide_one_title);
    }

    @Override
    protected String getDescription() {
        return getString(R.string.slide_one_description);
    }

    @Override
    public void onPageSelected() {
        banana.setTranslationX(0);
        guitar.setTranslationX(0);
        fork.setTranslationX(0);

        banana.setAlpha(1F);
        guitar.setAlpha(1F);
        fork.setAlpha(1F);
        title.setAlpha(1F);
        description.setAlpha(1F);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_intro_slide_one_stub;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        banana = (ImageView) view.findViewById(R.id.image_banana);
        fork = (ImageView) view.findViewById(R.id.image_fork);
        guitar = (ImageView) view.findViewById(R.id.image_guitar);

        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
    }
}
