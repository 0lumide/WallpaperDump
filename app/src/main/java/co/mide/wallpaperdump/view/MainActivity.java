package co.mide.wallpaperdump.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import co.mide.wallpaperdump.FakeData;
import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.databinding.ActivityMainBinding;
import co.mide.wallpaperdump.db.DatabaseHandler;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    DatabaseHandler databaseHandler;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        //This doesn't seem like it ever needs to get un-subscribed
        Observable.fromCallable(() -> DatabaseHandler.getInstance(MainActivity.this))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((databaseHandler) -> {
                    MainActivity.this.databaseHandler = databaseHandler;
                    //todo delete for loop when wallpaper dump api is implemented
                    if (databaseHandler.getDumpCount() == 0) {
                        for (int i = 0; i < 100; i++) {
                            databaseHandler
                                    .addToDumpTable(FakeData.createFakeDump(databaseHandler));
                        }
                    }
                    binding.content.recyclerView
                            .setAdapter(new RecyclerAdapter(this, databaseHandler));
                });

        binding.content.swipeRefreshLayout.setOnRefreshListener(this);
        binding.content.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.content.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        binding.fab.setOnClickListener((view) -> launchIntro());
        //This is because of a bug in sdk 23 that causes a NPE to be thrown
        try {
            launchIntro();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void launchIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }
    /**
     * @inheritDoc
     */
    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> binding.content.swipeRefreshLayout.setRefreshing(false)
                , 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
