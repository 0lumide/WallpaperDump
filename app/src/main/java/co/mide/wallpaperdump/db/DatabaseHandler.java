package co.mide.wallpaperdump.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.model.Wallpaper;

/**
 * Wrapper class for handling database queries
 * Created by Olumide on 2/2/2015.
 * Modified on 8/6/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper{
    private static final String DB_NAME = "WALLPAPER_DUMP";
    private static final int VERSION = 1;
    private static DatabaseHandler databaseHandler = null;

    private DatabaseHandler(@NonNull Context context){
        // The constructor is setup this way to prevent casting errors if used incorrectly
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
        //Use Writable Db for both reading and writing. It's easier and simpler
        getWritableDatabase();
    }

    public static synchronized DatabaseHandler getInstance(@NonNull Context context){
        if(databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context.getApplicationContext());
        }

        return databaseHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //create dump table
        db.execSQL(DumpTable.CREATE_TABLE);
        //create wallpaper table
        db.execSQL(WallpaperTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //do nothing, not yet needed
    }

    /**
     * NOTE: remember to close the cursor when done
     * @return Cursor for the Wallpaper table
     */
    Cursor getWallpaperCursor(){
        return getWritableDatabase().rawQuery(WallpaperTable.QUERY_SELECT_ALL, null);
    }


    /**
     * NOTE: remember to close the cursor when done
     * @return Cursor for the Dump table
     */
    Cursor getDumpCursor(){
        return getWritableDatabase().rawQuery(DumpTable.QUERY_SELECT_ALL, null);
    }

    public void deleteAllWallpaperTableRows(){
        getWritableDatabase().delete(WallpaperTable.TABLE_NAME, null, null);
    }

    public void deleteAllDumpTableRows(){
        getWritableDatabase().delete(DumpTable.TABLE_NAME, null, null);
        getWritableDatabase().execSQL(DumpTable.RESET_PRIMARY_KEY);
    }

    /**
     * @return the number of dump entries in the database
     */
    public int getDumpCount() {
        Cursor cursor = getDumpCursor();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * @return the number of wallpaper entries in the database
     */
    @SuppressWarnings("unused")
    public int getWallpaperCount() {
        Cursor cursor = getWallpaperCursor();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private Cursor getSingleWallpaperRowCursor(String wallpaperId){
        return getWritableDatabase().rawQuery(WallpaperTable.selectRowByImageId(wallpaperId), null);
    }

    /**
     *
     * @param wallpaperId the unique id of the wallpaper entry. This is also it's imgur unique id
     * @return the Wallpaper with the provided wallpaperId
     */
    public Wallpaper getWallpaper(String wallpaperId){
        Cursor cursor = getSingleWallpaperRowCursor(wallpaperId);
        if(cursor == null || !cursor.moveToFirst()) {
            if(cursor != null) cursor.close();
            throw new IllegalArgumentException("Wallpaper: " + wallpaperId + " doesn't exist");
        }

        Wallpaper wallpaper = new Wallpaper();
        //IMAGE_ID | CLARIFAI_IS_NSFW | TAGS |
        wallpaper.setImageId(cursor.getString(0));
        wallpaper.setIsNSFW(Boolean.parseBoolean(cursor.getString(1)));
        wallpaper.setTags(Arrays.asList(cursor.getString(2).split(",")));

        cursor.close();
        return wallpaper;
    }

    /**
     * Retrieved the Dump stored at the requested index
     * @param index the position of the dump. 0 is the first 1 the second, e.t.c
     * @return the Dump at the specified index.
     */
    public Dump getDump(int index){
        if(index < 0 || index >= getDumpCount())
            throw new IllegalArgumentException(index+" is not a valid index");
        //In reality the query is not 0 based so we have to increment index by 1
        index += 1;

        Cursor dumpCursor = getWritableDatabase().rawQuery(DumpTable.selectRowByPrimaryKey(index), null);
        if(dumpCursor == null || !dumpCursor.moveToFirst()) {
            if(dumpCursor != null) dumpCursor.close();
            throw new IllegalStateException("Something went off the deep end");
        }
        Dump dump = new Dump();

        //INTEGER_ID | ALBUM_ID/DUMP_ID | IS_NSFW | UPLOADER | DUMP_DATE | WALLPAPERS |
        dump.setDumpId(dumpCursor.getString(1));
        dump.setIsNSFW(Boolean.parseBoolean(dumpCursor.getString(2)));
        dump.setUploadedBy(dumpCursor.getString(3));
        dump.setTimestamp(dumpCursor.getLong(4));
        String[] wallpaperIds = dumpCursor.getString(5).split(",");

        List<String> wallpapers = new ArrayList<>(wallpaperIds.length);

        Collections.addAll(wallpapers, wallpaperIds);

        dump.setImages(wallpapers);
        dumpCursor.close();
        dump.setTitle("Dump #"+index);
        return dump;
    }

    public void addToWallpaperTable(Wallpaper wallpaper){
        Cursor cursor = getSingleWallpaperRowCursor(wallpaper.getImageId());
        if(cursor != null && cursor.moveToFirst()){
            Log.i("dbug", "Wallpaper already exists");
            cursor.close();
            return;
        }
        if(cursor != null) cursor.close();

        StringBuilder stringBuilder = new StringBuilder();
        for(String tag: wallpaper.getTags()){
            stringBuilder.append(tag);
            stringBuilder.append(',');
        }
        //trim off the trailing comma
        String tags = stringBuilder.length() > 0 ? stringBuilder.substring(0, stringBuilder.length() - 1): "";

        //IMAGE_ID | CLARIFAI_IS_NSFW | TAGS |
        ContentValues values = new ContentValues();
        values.put(WallpaperTable.IMAGE_ID, wallpaper.getImageId());
        values.put(WallpaperTable.CLARIFAI_IS_NSFW, wallpaper.getIsNSFW().toString());
        values.put(WallpaperTable.TAGS, tags);
        getWritableDatabase().insert(WallpaperTable.TABLE_NAME, null, values);
    }

    /**
     * Adds the dump at the end of the database
     * @param dump the dump to be stored
     */
    public void addToDumpTable(Dump dump){
        StringBuilder stringBuilder = new StringBuilder();
        for(String wallpaperId: dump.getImages()){
            stringBuilder.append(wallpaperId);
            stringBuilder.append(',');
        }
        //trim off the trailing comma
        String wallpapers = stringBuilder.length() > 0 ? stringBuilder.substring(0, stringBuilder.length() - 1): "";

        //INTEGER_ID | ALBUM_ID/DUMP_ID | IS_NSFW | UPLOADER | DUMP_DATE | WALLPAPERS |
        ContentValues values = new ContentValues();
        values.put(DumpTable.DUMP_ID, dump.getDumpId());
        values.put(DumpTable.IMGUR_IS_NSFW, dump.getIsNSFW().toString());
        values.put(DumpTable.UPLOADER, dump.getUploadedBy());
        values.put(DumpTable.DUMP_DATE, dump.getTimestamp());
        values.put(DumpTable.WALLPAPERS, wallpapers);
        getWritableDatabase().insert(DumpTable.TABLE_NAME, null, values);
    }
}

class WallpaperTable{
    //IMAGE_ID | CLARIFAI_IS_NSFW | TAGS |
    public static final String TABLE_NAME = "WALLPAPER_TABLE";
    public static final String IMAGE_ID = "IMAGE_ID";
    public static final String CLARIFAI_IS_NSFW = "CLARIFAI_IS_NSFW";
    public static final String TAGS = "TAGS";

    public static final String QUERY_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    /**
     * Query that creates a new table
     */
    public static final String CREATE_TABLE = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT)",
            TABLE_NAME, IMAGE_ID, CLARIFAI_IS_NSFW, TAGS);

    /**
     * Returns the query that selects the wanted row from the table
     * @param id the image id
     * @return the query
     */
    public static String selectRowByImageId(String id){
        return "SELECT * FROM " + WallpaperTable.TABLE_NAME + " WHERE "+WallpaperTable.IMAGE_ID+" = '"+id+"'";
    }
}

class DumpTable{
    //INTEGER_ID | ALBUM_ID/DUMP_ID | IS_NSFW | UPLOADER | DUMP_DATE | WALLPAPERS |
    public static final String TABLE_NAME = "DUMP_TABLE";
    public static final String DUMP_ID = "DUMP_ID";
    public static final String INTEGER_ID = "INTEGER_ID";
    public static final String IMGUR_IS_NSFW = "IMGUR_IS_NSFW";
    public static final String UPLOADER = "UPLOADER";
    public static final String DUMP_DATE = "DUMP_DATE";
    public static final String WALLPAPERS = "WALLPAPERS";

    public static final String QUERY_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    public static final String RESET_PRIMARY_KEY = "DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DumpTable.TABLE_NAME + "'";
    /**
     * Query that creates a new table
     */
    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER DEFAULT 0, %s TEXT)",
            TABLE_NAME, INTEGER_ID, DUMP_ID, IMGUR_IS_NSFW, UPLOADER, DUMP_DATE, WALLPAPERS);

    /**
     * Returns the query that selects the wanted row from the table
     * @param index the dump index
     * @return the query
     */
    public static String selectRowByPrimaryKey(int index){
        return "SELECT * FROM " + TABLE_NAME + " WHERE "+INTEGER_ID+" = '"+index+"'";
    }

}
