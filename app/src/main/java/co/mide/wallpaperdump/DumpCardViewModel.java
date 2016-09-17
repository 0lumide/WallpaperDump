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
import rx.Observable;
import rx.subjects.PublishSubject;

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
    PublishSubject<Pair<Intent, Pair[]>> launchActivitySubject = PublishSubject.create();

    public DumpCardViewModel(Activity activity, Dump dump, int dumpIndex) {
        this.activity = activity;
        this.dump = dump;
        this.dumpIndex = dumpIndex;

        //instantiate observable/subject that'll notify that an activity should be launched

        int baseNumImages = activity.getResources().getInteger(R.integer.image_count);
        int temp = baseNumImages + calcImageCountOffset(dump.getDumpId());
        displayedImageCount = Math.min(temp, dump.getImages().size());
        hasOverflow = temp == displayedImageCount;
        if (hasOverflow) {
            overFlowCount = dump.getImages().size() - temp;
        }
    }

    /**
     * This returns a number based on the dumpId. For every dump id there is exactly one number,
     * but not vice versa
     * @param dumpId the key to use to calculate the number
     * @return either -1, 0 or 1
     */
    private static int calcImageCountOffset(String dumpId) {
        int num = 0;
        for (char c : dumpId.toCharArray()) {
            num += c;
        }
        return (num % 3) - 1;
    }

    public int getDisplayImageCount() {
        return displayedImageCount;
    }

    public String getTitle() {
        return dump.getTitle();
    }

    public Spanned getUsername() {
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

    public String getPostTime() {
        return DateFormat.getDateTimeInstance().format(new Date(dump.getTimestamp()));
    }

    public ImageGridLayout.OnMoreClicked getOnMoreClicked() {
        return imageGridLayout -> {
            Intent intent = GalleryActivity.getStartIntent(activity,
                    dumpIndex, getDisplayImageCount(), displayedImageCount);
            activity.startActivity(intent);
        };
    }

    @BindingAdapter("setImages")
    @SuppressWarnings("unused")
    public static void setImages(ImageGridLayout gridLayout, final DumpCardViewModel viewModel) {
        //remove extra views
        gridLayout.setMaxImagesCount(viewModel.getDisplayImageCount());
        gridLayout.setOnMoreClickedCallback(viewModel.getOnMoreClicked());

        /*
            In this case pairs actually pairs all the images in the layout
            it's a hack to make sure that in the Gallery activity other images other than the
            initial one will also have an exit transition.
            It works out since there's no exit transition in this activity and the Gallery activity
            has only one imageView involved in the transition both at entry and exit.
            I don't see myself using proper channels (finishAfterTransition() and onActivityReenter)
            as it's complicated jk jk I'll do it later //todo
        */
        Pair[] pairs = new Pair[viewModel.getDisplayImageCount()];
        for (int i = 0; i < viewModel.getDisplayImageCount(); i++) {
            final ForegroundImageView imageView;
            if (i >= gridLayout.getImageCount()) {
                imageView = new ForegroundImageView(viewModel.activity);
                gridLayout.addView(imageView);
            } else {
                imageView = (ForegroundImageView) gridLayout.getChildAt(i);
            }

            pairs[i] = new Pair<>((View) imageView, viewModel.dump.getImages().get(i));
        }

        for (int i = 0; i < viewModel.getDisplayImageCount(); i++) {
            ForegroundImageView imageView = (ForegroundImageView) gridLayout.getChildAt(i);
            setupImage(viewModel, imageView, i, pairs);
        }

        //todo fix workaround when library gets fixed
        if (viewModel.hasOverflow) {
            gridLayout.setMoreImagesCount(viewModel.overFlowCount - 1);

            //add fake view to make number show up
            gridLayout.addView(new ImageView(gridLayout.getContext()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setupImage(final DumpCardViewModel viewModel, ForegroundImageView imageView,
                                   final int wallpaperIndex, final Pair[] pairs) {

        final String id = viewModel.dump.getImages().get(wallpaperIndex);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imageView.setTransitionName(id);


        int[] attrs = new int[] {android.R.attr.selectableItemBackground /* index 0 */};
        TypedArray ta = viewModel.activity.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        imageView.setForeground(drawableFromTheme);

        imageView.setOnClickListener(v -> {
                Intent intent = GalleryActivity.getStartIntent(viewModel.activity,
                        viewModel.dumpIndex, wallpaperIndex, viewModel.displayedImageCount);

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                viewModel.activity,
                                pairs);

                ActivityCompat.startActivity(viewModel.activity, intent, options.toBundle());
            });

        String imageId = viewModel.dump.getImages().get(wallpaperIndex);
        Glide.with(viewModel.activity).
                load(ImgurUtil.smallerImageLinkFromId(imageId))
                .centerCrop()
                .into(imageView);
    }

    public Observable<Pair<Intent, Pair[]>> getLaunchActivityObservable() {
        return launchActivitySubject;
    }
}
