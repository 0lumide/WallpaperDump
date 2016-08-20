package co.mide.wallpaperdump.db;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.model.Wallpaper;
import co.mide.wallpaperdump.util.Objects;

/**
 * Class to test the DatabaseHandler class
 * Created by Olumide on 8/8/2016.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHandlerTest {
    DatabaseHandler databaseHandler;

    void grantPermissionIfNeeded() throws Exception{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check permission state
            int permission = ActivityCompat.checkSelfPermission(InstrumentationRegistry.getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            String packageName = InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName();
            //check if permission has already been granted
            if (PackageManager.PERMISSION_GRANTED != permission) {
                String command = "pm grant "+ packageName + " " + Manifest.permission.WRITE_EXTERNAL_STORAGE;
                InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command);
            }
        }
    }

    @Before
    public void setUp() throws Exception{
        grantPermissionIfNeeded();
        //Countdown latch to ensure the database is ready
        final CountDownLatch lock = new CountDownLatch(1);
        databaseHandler = DatabaseHandler.getInstance(InstrumentationRegistry.getTargetContext(), new DBHandlerResponse() {
            @Override
            public void onDBReady(DatabaseHandler databaseHandler) {
                lock.countDown();
            }
        });
        lock.await(2000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void test_database_count() throws Exception{
        int startCount = databaseHandler.getDumpCount();

        databaseHandler.addToDumpTable(new Dump());

        int endCount = databaseHandler.getDumpCount();
        Assert.assertEquals(endCount - startCount, 1);
    }

    @Test
    public void test_add_dump_to_database(){
        Dump dump = new Dump();
        dump.setTimestamp(System.currentTimeMillis());
        dump.setIsNSFW(Boolean.FALSE);
        dump.setUploadedBy("banana");
        dump.setDumpId("for");

        List<String> images = new LinkedList<>();
        images.add("not");
        images.add("a");
        images.add("real");
        images.add("wallpaaper");
        dump.setImages(images);

        databaseHandler.addToDumpTable(dump);
        Dump retrievedDump = databaseHandler.getDump(databaseHandler.getDumpCount() - 1);

        Assert.assertEquals(retrievedDump.getDumpId(), dump.getDumpId());
        Assert.assertEquals(retrievedDump.getIsNSFW(), dump.getIsNSFW());
        Assert.assertEquals(retrievedDump.getUploadedBy(), dump.getUploadedBy());
        Assert.assertEquals(retrievedDump.getTimestamp(), dump.getTimestamp());
        Assert.assertTrue(Objects.equals(retrievedDump.getImages(), dump.getImages()));
    }

    @Test
    public void test_add_wallpaper_to_database(){
        List<String> tags = new LinkedList<>();
        tags.add("scale");
        tags.add("guitar");
        tags.add("for");

        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setIsNSFW(Boolean.FALSE);
        wallpaper.setImageId("temperature");
        wallpaper.setTags(tags);

        databaseHandler.addToWallpaperTable(wallpaper);
        Wallpaper retrievedWallpaper = databaseHandler.getWallpaper("temperature");

        Assert.assertEquals(retrievedWallpaper.getImageId(), wallpaper.getImageId());
        Assert.assertEquals(retrievedWallpaper.getIsNSFW(), wallpaper.getIsNSFW());
        Assert.assertTrue(Objects.equals(retrievedWallpaper.getTags(), wallpaper.getTags()));
    }
}