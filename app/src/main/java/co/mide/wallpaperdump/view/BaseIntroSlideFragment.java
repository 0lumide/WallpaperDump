package co.mide.wallpaperdump.view;

import android.graphics.Color;

import com.miz.introactivity.BaseIntroFragment;
import com.miz.introactivity.CustomAnimationPageTransformerDelegate;

public abstract class BaseIntroSlideFragment extends BaseIntroFragment
        implements CustomAnimationPageTransformerDelegate {

    @Override
    protected int getTitleColor() {
        return Color.WHITE;
    }

    @Override
    protected int getDescriptionColor() {
        return Color.WHITE;
    }

    @Override
    protected int getDrawableId() {
        return 0;
    }

    @Override
    protected int getResourceType() {
        return RESOURCE_TYPE_LAYOUT;
    }

    @Override
    public void onPageInvisible(float position) {

    }
}
