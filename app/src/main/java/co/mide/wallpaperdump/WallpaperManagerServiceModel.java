package co.mide.wallpaperdump;

import android.content.SharedPreferences;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;
import co.mide.wallpaperdump.model.Wallpaper;
import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class WallpaperManagerServiceModel {
    public static final String DUMP_INDEX =
            "co.mide.wallpaperdump.WallpaperManagerModel.DUMP_INDEX";
    public static final String WALLPAPER_INDEX =
            "co.mide.wallpaperdump.WallpaperManagerModel.WALLPAPER_INDEX";

    boolean isChangingEnabled;

    BehaviorSubject<Wallpaper> wallpaperChange;
    BehaviorSubject<Dump> dumpChange;
    SharedPreferences sharedPreferences;
    DatabaseHandler dbHandler;
    Random random = new Random(8008888888L);

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    Subscription timerSubscription;

    public WallpaperManagerServiceModel(DatabaseHandler dbHandler, long interval) {
        this.dbHandler = dbHandler;
        this.wallpaperChange = BehaviorSubject.create(getNextWallpaper());

        timerSubscription = subscribeToTimerObservable(interval);
        subscriptions.add(timerSubscription);
    }

    private Subscription subscribeToTimerObservable(long interval) {
        return Observable.interval(interval, TimeUnit.MINUTES)
                .filter(elapsedTime -> isChangingEnabled)
                .subscribe(elapsedTime -> wallpaperChange.onNext(getNextWallpaper()));
    }

    public void disableWallpaperChange() {
        isChangingEnabled = false;
    }

    public void enableWallpaperChange() {
        isChangingEnabled = true;
    }

    public void changeInterval(long newInterval) {
        subscriptions.remove(timerSubscription);
        timerSubscription = subscribeToTimerObservable(newInterval);
        subscriptions.add(timerSubscription);
    }

    public void cleanUp() {
        subscriptions.clear();
    }

    Wallpaper getNextWallpaper() {
        int wallpaperIndex = sharedPreferences.getInt(WALLPAPER_INDEX, 0) + 1;
        int dumpIndex = sharedPreferences.getInt(DUMP_INDEX, 0);
        Dump dump = dbHandler.getDump(dumpIndex);

        if (wallpaperIndex >= dump.getImages().size()) {
            dump = getNextDump();
            wallpaperIndex = 0;
        }

        Wallpaper wallpaper = dbHandler.getWallpaper(dump.getImages().get(wallpaperIndex));
        sharedPreferences.edit().putInt(WALLPAPER_INDEX, wallpaperIndex).apply();

        return wallpaper;
    }

    Dump getNextDump() {
        int newDumpIndex = random.nextInt() % dbHandler.getDumpCount();
        sharedPreferences.edit().putInt(DUMP_INDEX, newDumpIndex).apply();
        Dump newDump = dbHandler.getDump(newDumpIndex);
        dumpChange.onNext(newDump);
        return newDump;
    }

    public Observable<Wallpaper> getWallpaperObservable() {
        return wallpaperChange;
    }

    public Observable<Dump> getDumpObservable() {
        return dumpChange;
    }
}