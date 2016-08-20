package co.mide.wallpaperdump;

import android.app.WallpaperManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.Map;

import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.views.ViewPagerAdapter;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String DUMP_INDEX = "DUMP_INDEX";
    public static final String WALLPAPER_INDEX = "WALLPAPER_INDEX";
    public static final String WALLPAPER_ID = "WALLPAPER_ID";
    int wallpaperIndex;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    View decorView;
    int currentPage;
    Dump dump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //postpone transition till viewpager is ready
        ActivityCompat.postponeEnterTransition(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);

        final int dumpIndex = getIntent().getIntExtra(DUMP_INDEX, -1);
        wallpaperIndex = getIntent().getIntExtra(WALLPAPER_INDEX, -1);
        currentPage = wallpaperIndex;
        final String wallpaperID = getIntent().getStringExtra(WALLPAPER_ID);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        final DatabaseHandler databaseHandler = MainActivity.databaseHandler;
        dump = databaseHandler.getDump(dumpIndex);
        adapter = new ViewPagerAdapter(GalleryActivity.this, this, dump);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(wallpaperIndex, false);
        setGalleryTitle(wallpaperIndex);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                sharedElements.put(wallpaperID, adapter.getCurrentView(wallpaperIndex));
                super.onMapSharedElements(names, sharedElements);
            }
        });

        ActivityCompat.startPostponedEnterTransition(GalleryActivity.this);

        decorView = getWindow().getDecorView();
        setupActionBarListener();
        setupPageChangeListener();
        toggleSystemUi();
    }

    private void setGalleryTitle(int position){
        getSupportActionBar().setTitle(getResources().getString(R.string.gallery_title,
                position+1, dump.getImages().size()));
    }

    private void setupPageChangeListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                setGalleryTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupActionBarListener(){
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            int color = ContextCompat.getColor(GalleryActivity.this, R.color.colorBlackTransparent);
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                            getSupportActionBar().show();
                        } else {
                            getSupportActionBar().hide();
                            int color = ContextCompat.getColor(GalleryActivity.this, R.color.transparent);
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                        }
                    }
                });
    }

    @Override
    public void onClick(View view){
        toggleSystemUi();
    }

    void toggleSystemUi(){
        if(getSupportActionBar().isShowing()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    @Override
    public void onBackPressed(){
        exit();
    }

    public void exit(){
        if(wallpaperIndex == viewPager.getCurrentItem()){
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Uri uri = Uri.parse("http://i.imgur.com/"+dump.getImages().get(currentPage)+".jpg");
                    WallpaperManager.getInstance(this).getCropAndSetWallpaperIntent(uri);
                }else{
                    throw new IllegalStateException("This menu should be deactivated");
                }
                break;
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public @NonNull ActionBar getSupportActionBar(){
        return super.getSupportActionBar();
    }
}
