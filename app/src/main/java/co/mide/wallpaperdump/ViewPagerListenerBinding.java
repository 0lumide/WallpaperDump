package co.mide.wallpaperdump;

import android.databinding.BindingAdapter;
import android.databinding.adapters.ListenerUtil;
import android.support.v4.view.ViewPager;

/**
 * Implementation to make it possible to Bind single method from listener with multiple methods
 * for View pager
 * +George Mount 2016
 * http://stackoverflow.com/a/39233833/2057884
 */
public class ViewPagerListenerBinding {
    private ViewPagerListenerBinding() {

    }

    public interface OnPageScrollStateChanged {
        void onPageScrollStateChanged(int state);
    }

    public interface OnPageScrolled {
        void onPageScrolled(int position, float offset, int offsetPixels);
    }

    public interface OnPageSelected {
        void onPageSelected(int position);
    }

    @BindingAdapter(value = {
            "android:onPageScrollStateChanged",
            "android:onPageScrolled",
            "android:onPageSelected"}, requireAll = false)
    @SuppressWarnings("unused")
    public static void setViewPagerListeners(ViewPager view,
                                             final OnPageScrollStateChanged scrollStateChanged,
                                             final OnPageScrolled scrolled,
                                             final OnPageSelected selected) {
        ViewPager.OnPageChangeListener newListener = null;
        if (scrollStateChanged != null || scrolled != null || selected != null) {
            newListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrollStateChanged(int state) {
                    if (scrollStateChanged != null) {
                        scrollStateChanged.onPageScrollStateChanged(state);
                    }
                }

                @Override
                public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {
                    if (scrolled != null) {
                        scrolled.onPageScrolled(pos, posOffset, posOffsetPixels);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    if (selected != null) {
                        selected.onPageSelected(position);
                    }
                }
            };
        }

        ViewPager.OnPageChangeListener oldListener = ListenerUtil.trackListener(view,
                newListener, R.id.viewPagerListener);
        if (oldListener != null) {
            view.removeOnPageChangeListener(oldListener);
        }
        if (newListener != null) {
            view.addOnPageChangeListener(newListener);
        }
    }
}