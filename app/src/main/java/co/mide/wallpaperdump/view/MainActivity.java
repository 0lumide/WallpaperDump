package co.mide.wallpaperdump.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import co.mide.wallpaperdump.FakeData;
import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.databinding.ActivityMainBinding;
import co.mide.wallpaperdump.db.DatabaseHandler;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    static DatabaseHandler databaseHandler;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        databaseHandler = DatabaseHandler.getInstance(this);

        //todo delete for loop when wallpaper dump api is implemented
        if(databaseHandler.getDumpCount() == 0) {
            for (int i = 0; i < 100; i++) {
                databaseHandler.addToDumpTable(FakeData.createFakeDump(databaseHandler));
            }
        }
        binding.content.recyclerView.setAdapter(new RecyclerAdapter(MainActivity.this, databaseHandler));

        binding.content.swipeRefreshLayout.setOnRefreshListener(this);
        binding.content.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.content.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(MainActivity.this, BlacklistActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.content.swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
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
