package co.mide.wallpaperdump.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.mide.wallpaperdump.FakeData;
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

    @Before
    public void setUp() throws Exception{
        databaseHandler = DatabaseHandler.getInstance(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        databaseHandler.deleteAllDumpTableRows();
        databaseHandler.deleteAllWallpaperTableRows();
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
        Dump dump = FakeData.createFakeDump(databaseHandler, 1);

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
        Wallpaper wallpaper = FakeData.createFakeWallpaper();

        databaseHandler.addToWallpaperTable(wallpaper);

        Wallpaper retrievedWallpaper = databaseHandler.getWallpaper(wallpaper.getImageId());

        Assert.assertEquals(retrievedWallpaper.getImageId(), wallpaper.getImageId());
        Assert.assertEquals(retrievedWallpaper.getIsNSFW(), wallpaper.getIsNSFW());
        Assert.assertTrue(Objects.equals(retrievedWallpaper.getTags(), wallpaper.getTags()));
    }


    /**
     * Test the parsing of boolean in the databaseHandler.
     * It used to have problems parsing true values
     */
    @Test
    public void test_retrieve_wallpaper_boolean_parsing(){
        Wallpaper wallpaper = FakeData.createFakeWallpaper();
        wallpaper.setIsNSFW(true);

        databaseHandler.addToWallpaperTable(wallpaper);
        Wallpaper retrievedWallpaper = databaseHandler.getWallpaper(wallpaper.getImageId());

        Assert.assertEquals(retrievedWallpaper.getIsNSFW(), wallpaper.getIsNSFW());
    }

    /**
     * Test the parsing of boolean in the databaseHandler.
     * It used to have problems parsing true values
     */
    @Test
    public void test_retrieve_dump_boolean_parsing(){
        Dump dump = FakeData.createFakeDump(databaseHandler, 1);
        dump.setIsNSFW(true);
        databaseHandler.addToDumpTable(dump);
        Dump retrievedDump = databaseHandler.getDump(databaseHandler.getDumpCount() - 1);
        Assert.assertEquals(retrievedDump.getIsNSFW(), dump.getIsNSFW());
    }
}