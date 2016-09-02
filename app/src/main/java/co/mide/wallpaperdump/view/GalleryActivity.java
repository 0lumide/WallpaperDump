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

public class GalleryActivity extends AppCompatActivity implements GalleryViewModel.ToolbarToggler{
    public static final String DUMP_INDEX = "DUMP_INDEX";
    public static final String WALLPAPER_INDEX = "WALLPAPER_INDEX";
    public static final String WALLPAPER_ID = "WALLPAPER_ID";
    int wallpaperIndex;
    ActivityGalleryBinding binding;
    ViewPagerAdapter adapter;
    Dump dump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //postpone transition till viewpager is ready
        ActivityCompat.postponeEnterTransition(this);
        super.onCreate(savedInstanceState);


        final int dumpIndex = getIntent().getIntExtra(DUMP_INDEX, -1);
        wallpaperIndex = getIntent().getIntExtra(WALLPAPER_INDEX, -1);
        final String wallpaperID = getIntent().getStringExtra(WALLPAPER_ID);

        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);
        dump = databaseHandler.getDump(dumpIndex);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        binding.setViewModel(new GalleryViewModel(wallpaperIndex+1, dump, this));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ViewPagerAdapter(databaseHandler, binding.getViewModel());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(wallpaperIndex, false);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                sharedElements.put(wallpaperID, adapter.getCurrentView(wallpaperIndex));
                super.onMapSharedElements(names, sharedElements);
            }
        });

        ActivityCompat.startPostponedEnterTransition(GalleryActivity.this);

        setupActionBarListener();
    }

    public static Intent getStartIntent(Context context, String wallpaperId, int dumpIndex, int wallpaperIndex){
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(DUMP_INDEX, dumpIndex);
        intent.putExtra(WALLPAPER_INDEX, wallpaperIndex);
        intent.putExtra(WALLPAPER_ID, wallpaperId);
        return intent;
    }

    private void setupActionBarListener(){
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            getSupportActionBar().show();
                            binding.toolbar.animate().setListener(null);
                            binding.toolbar.animate().translationY(0).setDuration(100).start();
                        } else {
                            binding.toolbar.animate().translationY(-binding.toolbar.getHeight()).setDuration(100).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {}

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    getSupportActionBar().hide();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                    getSupportActionBar().hide();
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {}
                            }).start();
                        }
                    }
                });
    }

    public void toggleShowToolbar(){
        if(getSupportActionBar().isShowing()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE;
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    @Override
    public void onBackPressed(){
        exit();
    }

    public void exit(){
        if(wallpaperIndex == binding.viewPager.getCurrentItem()){
            ActivityCompat.finishAfterTransition(this);
        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            menu.findItem(R.id.manual_set_wallpaper).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //handle on up pressed
            case android.R.id.home:
                exit();
                return(true);
            case R.id.manual_set_wallpaper:
                break;
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public @NonNull ActionBar getSupportActionBar(){
        ActionBar actionBar = super.getSupportActionBar();
        if(actionBar == null){
            throw new IllegalArgumentException("Support action bar doesn't exist");
        }
        return actionBar;
    }
}
