package co.mide.wallpaperdump;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.Date;

import co.mide.imagegridlayout.ImageGridLayout;
import co.mide.textimageview.ForegroundImageView;
import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.util.ImgurUtil;
import co.mide.wallpaperdump.view.GalleryActivity;

/**
 * View model for dump cardView
 */
public class DumpCardViewModel extends BaseObservable {
    Dump dump;
    Activity activity;
    int displayedImageCount;
    boolean hasOverflow;
    int overFlowCount = 0;
    int dumpIndex;

    public DumpCardViewModel(Activity activity, Dump dump, int dumpIndex){
        this.activity = activity;
        this.dump = dump;
        this.dumpIndex = dumpIndex;

        int baseNumImages = activity.getResources().getInteger(R.integer.image_count);
        int temp = baseNumImages + calcImageCountOffset(dump.getDumpId());
        displayedImageCount = Math.min(temp, dump.getImages().size());
        hasOverflow = temp == displayedImageCount;
        if(hasOverflow){
            overFlowCount = dump.getImages().size() - temp;
        }
    }

    /**
     * This returns a number based on the dumpId. For every dump id there is exactly one number,
     * but not vice versa
     * @param dumpId the key to use to calculate the number
     * @return either -1, 0 or 1
     */
    private static int calcImageCountOffset(String dumpId){
        int num = 0;
        for(char c: dumpId.toCharArray()){
            num += c;
        }
        return (num % 3) - 1;
    }

    public int getDisplayImageCount(){
        return displayedImageCount;
    }

    public String getTitle(){
        return dump.getTitle();
    }

    public Spanned getUsername(){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(ImgurUtil.userProfileHtmlLinkFromId(dump.getUploadedBy()),
                    Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            result = Html.fromHtml(ImgurUtil.userProfileHtmlLinkFromId(dump.getUploadedBy()));
        }
        return result;
    }

    public String getPostTime(){
        return DateFormat.getDateTimeInstance().format(new Date(dump.getTimestamp()));
    }

    public ImageGridLayout.OnMoreClicked getOnMoreClicked(){
        return new ImageGridLayout.OnMoreClicked() {
            @Override
            public void onMoreClicked(ImageGridLayout imageGridLayout) {
                Intent intent = new Intent(activity, GalleryActivity.class);
                intent.putExtra("DUMP_INDEX", dumpIndex);
                intent.putExtra("WALLPAPER_INDEX", displayedImageCount);
                activity.startActivity(intent);
            }
        };
    }

    @BindingAdapter("setImages")
    @SuppressWarnings("unused")
    public static void setImages(ImageGridLayout gridLayout, final DumpCardViewModel viewModel){
        //remove extra views
        gridLayout.setMaxImagesCount(viewModel.getDisplayImageCount());
        gridLayout.setOnMoreClickedCallback(viewModel.getOnMoreClicked());

        for(int i = 0; i < viewModel.getDisplayImageCount(); i++) {
            final ForegroundImageView imageView;
            if(i >= gridLayout.getImageCount()) {
                imageView = new ForegroundImageView(viewModel.activity);
                gridLayout.addView(imageView);
            }else {
                imageView = (ForegroundImageView) gridLayout.getChildAt(i);
            }

            setupImage(viewModel, imageView, i);
        }
        //todo fix workaround when library gets fixed
        if(viewModel.hasOverflow) {
            gridLayout.setMoreImagesCount(viewModel.overFlowCount - 1);

            //add fake view to make number show up
            gridLayout.addView(new ImageView(gridLayout.getContext()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setupImage(final DumpCardViewModel viewModel, ForegroundImageView imageView,
                                   final int wallpaperIndex){

        final String id = viewModel.dump.getImages().get(wallpaperIndex);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imageView.setTransitionName(id);


        int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
        TypedArray ta = viewModel.activity.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        imageView.setForeground(drawableFromTheme);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = GalleryActivity.getStartIntent(viewModel.activity,
                        id, viewModel.dumpIndex, wallpaperIndex);

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(viewModel.activity, new Pair<>(v, id));
                ActivityCompat.startActivity(viewModel.activity, intent, options.toBundle());
            }
        });

        Glide.with(viewModel.activity).
                load(ImgurUtil.smallerImageLinkFromId(viewModel.dump.getImages().get(wallpaperIndex)))
                .centerCrop()
                .into(imageView);
    }
}
