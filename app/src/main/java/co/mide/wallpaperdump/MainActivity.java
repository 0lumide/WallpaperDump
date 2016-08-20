package co.mide.wallpaperdump;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import co.mide.wallpaperdump.db.DBHandlerResponse;
import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.views.EmptyRecyclerView;
import co.mide.wallpaperdump.views.RecyclerAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    static DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupDatabase();

        ((SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout)).setOnRefreshListener(this);
        ((EmptyRecyclerView)findViewById(R.id.recycler_view)).setLayoutManager(new LinearLayoutManager(this));
        ((SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout)).setColorSchemeResources(R.color.colorAccent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(MainActivity.this, BlacklistActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupDatabase(){
        databaseHandler = DatabaseHandler.getInstance(this, new DBHandlerResponse(){
            public void onDBReady(DatabaseHandler dbHandler){
                ((EmptyRecyclerView)findViewById(R.id.recycler_view)).setAdapter(new RecyclerAdapter(MainActivity.this, dbHandler));
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
                ((SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout)).setRefreshing(false);
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

    @Override
    public @NonNull View findViewById(int id){
        View view = super.findViewById(id);
        if(view == null)
            throw new IllegalArgumentException("View "+id+" doesn't exist");
        return view;
    }
}
