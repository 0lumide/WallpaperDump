package co.mide.wallpaperdump.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import co.mide.wallpaperdump.FakeData;
import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.model.Wallpaper;

/**
 * Wrapper class for handling database queries
 * Created by Olumide on 2/2/2015.
 * Modified on 8/6/2016.
 */
@SuppressWarnings("unused")
public class DatabaseHandler extends SQLiteOpenHelper implements DBAsyncResponse{
    private static final String DB_NAME = "WALLPAPER_DUMP";
    private static final int VERSION = 1;
    private boolean isDBReady = false;
    private Context context;
    private DBHandlerResponse dbHandlerResponse;
    private static DatabaseHandler databaseHandler = null;

    private DatabaseHandler(@NonNull Context context, @NonNull DBHandlerResponse dbHandlerResponse){
        // The constructor is setup this way to prevent casting errors if used incorrectly
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        this.dbHandlerResponse = dbHandlerResponse;
        //Use Writable Db for both reading and writing. It's easier and simpler
        AsyncGetWritableDB asyncGetWritableDB = new AsyncGetWritableDB(this, this);
        asyncGetWritableDB.execute();
    }

    public static synchronized DatabaseHandler getInstance(@NonNull Context context, @NonNull DBHandlerResponse dbHandlerResponse){
        if(databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context.getApplicationContext(), dbHandlerResponse);
        }else{

            databaseHandler.dbHandlerResponse = dbHandlerResponse;
            //Use Writable Db for both reading and writing. It's easier and simpler
            AsyncGetWritableDB asyncGetWritableDB = new AsyncGetWritableDB(databaseHandler, databaseHandler);
            asyncGetWritableDB.execute();
        }
        return databaseHandler;
    }

    public boolean isDBReady(){
        return isDBReady;
    }

    @Override
    //this method is called after onCreate and onUpgrade if they're called
    public void processFinish(SQLiteDatabase db){
        isDBReady = true;
        if(getDumpCount() == 0){
            for(int i = 0; i < 100; i++){
                addToDumpTable(createFakeDump(i));
            }
        }
        dbHandlerResponse.onDBReady(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //create dump table
        db.execSQL(DumpTable.CREATE_TABLE);
        //create wallpaper table
        db.execSQL(WallpaperTable.CREATE_TABLE);
    }

    private Dump createFakeDump(int index){
        int i = index % 11;

        Dump dump = new Dump();
        dump.setDumpId(FakeData.dumpIds[i]);
        dump.setUploadedBy(FakeData.uploaders[i]);
        dump.setTimestamp(System.currentTimeMillis()-(long)(Math.random()*10000000000L));
        dump.setIsNSFW(Math.round(Math.random())==1);
        List<String> wallpapers = new LinkedList<>();

        int limit = 50 + (int)(Math.random()*50);
        for(int f = 0; f < limit; f++){
            List<String> tags = new LinkedList<>();
            for(int e = 0; e < 10; e++){
                tags.add(FakeData.tagCloud[(int)(Math.random()*FakeData.tagCloud.length)]);
            }

            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setIsNSFW(Math.round(Math.random())==1);
            wallpaper.setImageId(FakeData.imageIds[(int)(Math.random()*FakeData.imageIds.length)]);
            wallpaper.setTags(tags);

            addToWallpaperTable(wallpaper);

            wallpapers.add(wallpaper.getImageId());
        }
        dump.setImages(wallpapers);

        return dump;
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
        if(!isDBReady)
            throw new IllegalStateException("Database not yet ready");
        return getWritableDatabase().rawQuery(WallpaperTable.QUERY_SELECT_ALL, null);
    }


    /**
     * NOTE: remember to close the cursor when done
     * @return Cursor for the Dump table
     */
    Cursor getDumpCursor(){
        if(!isDBReady)
            throw new IllegalStateException("Database not yet ready");
        return getWritableDatabase().rawQuery(DumpTable.QUERY_SELECT_ALL, null);
    }

    public Wallpaper getWallpaper(String wallpaperId){
        if(!isDBReady)
            throw new IllegalStateException("Database not yet ready");

        Cursor cursor = getWritableDatabase().rawQuery(WallpaperTable.selectRowByImageId(wallpaperId), null);
        if(cursor == null || !cursor.moveToFirst())
            throw new IllegalArgumentException("Wallpaper: "+wallpaperId+" doesn't exist");

        Wallpaper wallpaper = new Wallpaper();
        //IMAGE_ID | CLARIFAI_IS_NSFW | TAGS |
        wallpaper.setImageId(cursor.getString(0));
        wallpaper.setIsNSFW(Boolean.parseBoolean(cursor.getString(1)));
        wallpaper.setTags(Arrays.asList(cursor.getString(2).split(",")));

        cursor.close();
        return wallpaper;
    }

    public int getDumpCount() {
        Cursor cursor = getDumpCursor();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Retrieved the Dump stored at the requested index
     * @param index the position of the dump. 0 is the first 1 the second, e.t.c
     * @return the Dump at the specified index.
     */
    public Dump getDump(int index){
        if(index < 0 || index >= getDumpCount())
            throw new IllegalArgumentException(index+" is not a valid index");

        Cursor dumpCursor = getDumpCursor();
        //check for empty cursor
        if(!dumpCursor.moveToFirst())
            throw new IllegalStateException("Something went off the deep end");
        dumpCursor.moveToPosition(index);

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
        dump.setTitle("Dump #"+(index+1));
        return dump;
    }

    void addToWallpaperTable(Wallpaper wallpaper){
        if(!isDBReady)
            throw new IllegalStateException("Database has not been initialized yet");
        Cursor cursor = getWritableDatabase().rawQuery(WallpaperTable.selectRowByImageId(wallpaper.getImageId()), null);
        if(cursor.moveToFirst()) {
            cursor.close();
        }
        cursor.close();
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
        values.put(WallpaperTable.CLARIFAI_IS_NSFW, wallpaper.getIsNSFW());
        values.put(WallpaperTable.TAGS, tags);
    }

    /**
     * Adds the dump at the end of the database
     * @param dump the dump to be stored
     */
    public void addToDumpTable(Dump dump){
        if(!isDBReady)
            throw new IllegalStateException("Database has not been initialized yet");

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

    static class AsyncGetWritableDB extends AsyncTask<Object, Void, SQLiteDatabase> {
        private DatabaseHandler dbHandler;
        private DBAsyncResponse delegate = null;

        public AsyncGetWritableDB(DatabaseHandler dbh, DBAsyncResponse delegate){
            dbHandler = dbh;
            this.delegate = delegate;
        }
        @Override
        protected SQLiteDatabase doInBackground(Object ... objects) {
            return dbHandler.getReadableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db){
            if(delegate != null)
                delegate.processFinish(db);
        }
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

    /**
     * Query that creates a new table
     */
    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER DEFAULT 0, %s TEXT)",
            TABLE_NAME, INTEGER_ID, DUMP_ID, IMGUR_IS_NSFW, UPLOADER, DUMP_DATE, WALLPAPERS);
}

interface DBAsyncResponse {
    void processFinish(SQLiteDatabase db);
}

