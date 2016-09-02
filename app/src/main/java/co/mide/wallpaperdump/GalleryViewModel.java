package co.mide.wallpaperdump;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import co.mide.wallpaperdump.model.Dump;

/**
 * View model for the gallery
 * Created by Olumide on 8/24/2016.
 */
public class GalleryViewModel extends BaseObservable{
    int currentPageNum;
    Dump dump;
    ToolbarToggler toolbarToggler;

    /**
     * @param currentPage is not zero based. The first page is 1 e.t.c
     * @param dump The model the view is representing
     */
    public GalleryViewModel(int currentPage, Dump dump, ToolbarToggler toolbarToggler){
        if(currentPage <= 0){
            throw new IllegalArgumentException("currentPage must be positive");
        }
        if(dump.getImages().size() <= 0){
            throw new IllegalArgumentException("number of pages must be positive");
        }
        if(currentPage > dump.getImages().size()){
            throw new IllegalArgumentException("currentPage cannot be more than the number of pages");
        }
        this.currentPageNum = currentPage;
        this.dump = dump;
        this.toolbarToggler = toolbarToggler;
    }

    public Dump getDump(){
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
        setCurrentPageNum(position+1);
    }

    public void setCurrentPageNum(int pageNum){
        currentPageNum = pageNum;
        notifyPropertyChanged(co.mide.wallpaperdump.BR.currentPageNum);
    }

    public void toggleShowToolbar(){
        toolbarToggler.toggleShowToolbar();
    }

    public interface ToolbarToggler{
        void toggleShowToolbar();
    }
}
