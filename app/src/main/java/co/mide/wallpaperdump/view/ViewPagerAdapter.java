package co.mide.wallpaperdump.view;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import co.mide.wallpaperdump.GalleryImageViewModel;
import co.mide.wallpaperdump.GalleryViewModel;
import co.mide.wallpaperdump.databinding.PhotoViewBinding;
import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.model.Wallpaper;

/**
 * Adapter for the gallery viewpager
 * Created by Olumide on 8/15/2016.
 */
@SuppressWarnings("unused")
public class ViewPagerAdapter extends PagerAdapter {
    Map<Integer, View> currentViews;
    GalleryViewModel galleryViewModel;
    DatabaseHandler databaseHandler;

    public ViewPagerAdapter(DatabaseHandler dbHandler, GalleryViewModel galleryViewModel) {
        this.databaseHandler = dbHandler;
        this.galleryViewModel = galleryViewModel;
        currentViews = new HashMap<>();
    }

    public View getCurrentView(int index){
        return currentViews.get(index);
    }

    @Override
    /*{@inheritDoc}*/
    public View instantiateItem(ViewGroup collection, int position) {
        Dump dump = galleryViewModel.getDump();

        LayoutInflater inflater = LayoutInflater.from(collection.getContext());

        Wallpaper wallpaper = databaseHandler.getWallpaper(dump.getImages().get(position));
        GalleryImageViewModel galleryImageViewModel = new GalleryImageViewModel(galleryViewModel, wallpaper);

        PhotoViewBinding binding = PhotoViewBinding.inflate(inflater, collection, false);
        binding.setViewModel(galleryImageViewModel);

        collection.addView(binding.getRoot());
        currentViews.put(position, binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        currentViews.remove(position);
    }

    @Override
    public int getCount() {
        return galleryViewModel.getDump().getImages().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //This is irrelevant since we use Toolbar
        return "";
    }
}
