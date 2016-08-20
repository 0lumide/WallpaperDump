package co.mide.wallpaperdump.views;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import co.mide.wallpaperdump.model.Dump;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Adapter for the gallery viewpager
 * Created by Olumide on 8/15/2016.
 */
@SuppressWarnings("unused")
public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    Dump dump;
    View.OnClickListener listener;
    Map<Integer, View> currentViews;

    public ViewPagerAdapter(Context context, View.OnClickListener listener, Dump dump) {
        this.context = context;
        this.listener = listener;
        this.dump = dump;
        currentViews = new HashMap<>();
    }

    public View getCurrentView(int index){
        return currentViews.get(index);
    }

    @Override
    /*{@inheritDoc}*/
    public View instantiateItem(ViewGroup collection, int position) {
        PhotoView imageView = new PhotoView(context);
        imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                listener.onClick(view);
            }
        });

        String id = dump.getImages().get(position);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imageView.setTransitionName(id);

        String imageLink = "http://i.imgur.com/"+id+".jpg";
        Glide.with(context)
                .load(imageLink)
                .into(imageView);

        collection.addView(imageView);
        currentViews.put(position, imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        currentViews.remove(position);
    }

    @Override
    public int getCount() {
        return dump.getImages().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        return "Title";//Context.getString(customPagerEnum.getTitleResId());
    }
}
