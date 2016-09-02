package co.mide.wallpaperdump;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.Glide;

import co.mide.wallpaperdump.model.Wallpaper;
import co.mide.wallpaperdump.util.ImgurUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ViewModel for a single gallery Image instance
 * Created by Olumide on 8/24/2016.
 */
public class GalleryImageViewModel extends BaseObservable {
    PhotoViewAttacher.OnViewTapListener viewTapListener;
    Wallpaper wallpaper;

    public GalleryImageViewModel(@NonNull final GalleryViewModel galleryViewModel, @NonNull Wallpaper wallpaper){
        //noinspection ConstantConditions
        if(galleryViewModel == null)
            throw new IllegalArgumentException("galleryViewModel cannot be null");
        //noinspection ConstantConditions
        if(wallpaper == null)
            throw new IllegalArgumentException("wallpaper cannot be null");

        this.wallpaper = wallpaper;

        viewTapListener = new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                galleryViewModel.toggleShowToolbar();
            }
        };
    }

    @BindingAdapter("setImage")
    @SuppressWarnings("unused")
    public static void loadImage(PhotoView photoView, String imageId){
        Glide.with(photoView.getContext())
                .load(ImgurUtil.imageLinkFromId(imageId))
                .into(photoView);
    }

    @Bindable
    @SuppressWarnings("unused")
    public String getImageId(){
        return wallpaper.getImageId();
    }

    public PhotoViewAttacher.OnViewTapListener getOnViewTapListener() {
        return viewTapListener;
    }
}
