package co.mide.wallpaperdump.view;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.mide.wallpaperdump.R;

public class SlideThreeFragment extends BaseIntroSlideFragment {
    ImageView disco, cera;
    TextView title, description;

    @Override
    public void onPageScrolled(View view, float position) {
        float posWidth = view.getWidth() * position;
        cera.setTranslationX(0.30F * posWidth);
        disco.setTranslationX(0);

        float alpha = 1 - Math.abs(position);
        cera.setAlpha(alpha);
        disco.setAlpha(alpha);
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
        return getString(R.string.slide_three_title);
    }

    @Override
    protected String getDescription() {
        return getString(R.string.slide_three_description);
    }

    @Override
    public void onPageSelected() {
        cera.setTranslationX(0);
        disco.setTranslationX(0);

        cera.setAlpha(1F);
        disco.setAlpha(1F);
        title.setAlpha(1F);
        description.setAlpha(1F);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_intro_slide_three_stub;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cera = (ImageView) view.findViewById(R.id.image_cera);
        disco = (ImageView) view.findViewById(R.id.image_disco_ball);

        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
    }
}
