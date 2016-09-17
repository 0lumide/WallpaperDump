package co.mide.wallpaperdump.view;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.Map;

import co.mide.wallpaperdump.GalleryViewModel;
import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.databinding.ActivityGalleryBinding;
import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class GalleryActivity extends AppCompatActivity {
    public static final String DUMP_INDEX = "co.mide.wallpaperdump.GalleryActivity.DUMP_INDEX";
    public static final String WALLPAPER_INDEX =
            "co.mide.wallpaperdump.GalleryActivity.WALLPAPER_INDEX";
    public static final String NUM_VISIBLE_VIEWS =
            "co.mide.wallpaperdump.GalleryActivity.NUM_VISIBLE_VIEWS";
    public static final int TOOLBAR_ANIMATION_DURATION = 100;

    ActivityGalleryBinding binding;
    ViewPagerAdapter adapter;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //postpone transition till viewpager is ready
        ActivityCompat.postponeEnterTransition(this);

        super.onCreate(savedInstanceState);

        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);

        final int dumpIndex = getIntent().getIntExtra(DUMP_INDEX, -1);
        final int wallpaperIndex = getIntent().getIntExtra(WALLPAPER_INDEX, -1);
        final int numberOfVisibleViews = getIntent().getIntExtra(NUM_VISIBLE_VIEWS, -1);

        Dump dump = databaseHandler.getDump(dumpIndex);

        final String wallpaperID = dump.getImages().get(wallpaperIndex);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        binding.setViewModel(new GalleryViewModel(wallpaperIndex + 1, dump));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ViewPagerAdapter(databaseHandler, binding.getViewModel());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(wallpaperIndex, false);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                names.clear();
                sharedElements.clear();
                View sharedView = adapter.getCurrentView(binding.viewPager.getCurrentItem());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //on entry getTransitionName returns null for some weird reason
                    //but it's fine on exit
                    //just so happens that wallpaperIndex != binding.viewPager.getCurrentItem()
                    //is only true on exit
                    String transitionName = wallpaperID;
                    if (wallpaperIndex != binding.viewPager.getCurrentItem()) {
                        transitionName = sharedView.getTransitionName();
                    }

                    if (binding.viewPager.getCurrentItem() < numberOfVisibleViews) {
                        names.add(transitionName);
                        sharedElements.put(transitionName, sharedView);
                    }
                }
                super.onMapSharedElements(names, sharedElements);
            }
        });

        ActivityCompat.startPostponedEnterTransition(this);

        setupActionBarListener();
    }

    public Subscription subscribeToToggleToolbarVisibility() {
        return binding.getViewModel().getToggleToolbarVisibilityObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::toggleShowToolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
        subscriptions.add(subscribeToToggleToolbarVisibility());
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    /**
     * Static method to create Activity launching intent
     * @param dumpIndex the index of the dump in the database to be displayed
     * @param wallpaperIndex the index of the Wallpaper the viewPager should scroll to
     * @return the intent to start the activity
     */
    public static Intent getStartIntent(Context context, int dumpIndex, int wallpaperIndex) {
        return getStartIntent(context, dumpIndex, wallpaperIndex, -1);
    }

    /**
     * Static method to create Activity launching intent
     * @param dumpIndex the index of the dump in the database to be displayed
     * @param wallpaperIndex the index of the Wallpaper the viewPager should scroll to
     * @param numberOfVisibleViews the number of views that are displayed in the gridLayout. It's
     *                             used in deciding which views to enable exitTransition on
     * @return the intent to start the activity
     */
    public static Intent getStartIntent(Context context, int dumpIndex, int wallpaperIndex,
                                        int numberOfVisibleViews) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(DUMP_INDEX, dumpIndex);
        intent.putExtra(WALLPAPER_INDEX, wallpaperIndex);
        intent.putExtra(NUM_VISIBLE_VIEWS, numberOfVisibleViews);
        return intent;
    }

    private void setupActionBarListener() {
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    getSupportActionBar().show();
                    binding.toolbar.animate()
                            .translationY(0)
                            .setDuration(TOOLBAR_ANIMATION_DURATION)
                            .setListener(null)
                            .start();
                } else {
                    binding.toolbar.animate()
                            .translationY(-binding.toolbar.getHeight())
                            .setDuration(TOOLBAR_ANIMATION_DURATION)
                            .setListener(createToolbarHideAnimationListener())
                            .start();
                }
            });
    }

    private Animator.AnimatorListener createToolbarHideAnimationListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                getSupportActionBar().hide();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                getSupportActionBar().hide();
            }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        };
    }

    void toggleShowToolbar(boolean showToolbar) {
        if (showToolbar) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE;
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            menu.findItem(R.id.manual_set_wallpaper).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //handle on up pressed
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                return true;
            case R.id.manual_set_wallpaper:
//                WallpaperManager wallpaperManager;
//                wallpaperManager = WallpaperManager.getInstance(activity);
//                int width = wallpaperManager.getDesiredMinimumWidth();
//                int height = wallpaperManager.getDesiredMinimumHeight();
//                String img = String.format("http://api.wallpaperdumps.com/v1/image/%dx%d/%s",
//                        width, height, dump.getImages().get(0));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @NonNull
    public ActionBar getSupportActionBar() {
        ActionBar actionBar = super.getSupportActionBar();
        if (actionBar == null) {
            throw new IllegalArgumentException("Support action bar doesn't exist");
        }
        return actionBar;
    }
}
