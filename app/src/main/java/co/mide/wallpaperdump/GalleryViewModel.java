package co.mide.wallpaperdump;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import co.mide.wallpaperdump.model.Dump;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * View model for the gallery
 */
public class GalleryViewModel extends BaseObservable {
    int currentPageNum;
    boolean toolbarIsVisibile = true;
    Dump dump;
    @NonNull BehaviorSubject<Boolean> toggleToolbarVisibilityObservable
            = BehaviorSubject.create(toolbarIsVisibile);

    /**
     * @param currentPage is not zero based. The first page is 1 e.t.c
     * @param dump The model the view is representing
     */
    public GalleryViewModel(int currentPage, @NonNull Dump dump) {
        if (currentPage <= 0) {
            throw new IllegalArgumentException("currentPage must be positive");
        }
        //noinspection ConstantConditions
        if (dump == null) {
            throw new IllegalArgumentException("dump cannot be null");
        }
        if (dump.getImages().size() <= 0) {
            throw new IllegalArgumentException("number of pages must be positive");
        }
        if (currentPage > dump.getImages().size()) {
            throw new IllegalArgumentException("currentPage must be more than the number of pages");
        }
        this.currentPageNum = currentPage;
        this.dump = dump;
    }

    @NonNull
    public Dump getDump() {
        return dump;
    }

    @Bindable
    @SuppressWarnings("unused")
    public int getCurrentPageNum() {
        return currentPageNum;
    }

    @Bindable
    @SuppressWarnings("unused")
    public int getNumPages() {
        return dump.getImages().size();
    }


    /**
     * Method bound to ViewPager onPageSelected callback
     * @param position current ViewPager page selection
     */
    @SuppressWarnings("unused")
    public void onPageSelected(int position) {
        //position is zero based index, but page number isn't
        setCurrentPageNum(position + 1);
    }

    private void setCurrentPageNum(int pageNum) {
        currentPageNum = pageNum;
        notifyPropertyChanged(co.mide.wallpaperdump.BR.currentPageNum);
    }

    @NonNull
    public Observable<Boolean> getToggleToolbarVisibilityObservable() {
        return toggleToolbarVisibilityObservable;
    }

    public void toggleShowToolbar() {
        toolbarIsVisibile = !toolbarIsVisibile;
        toggleToolbarVisibilityObservable.onNext(toolbarIsVisibile);
    }
}
