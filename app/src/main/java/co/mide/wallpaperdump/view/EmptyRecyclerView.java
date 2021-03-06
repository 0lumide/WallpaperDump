package co.mide.wallpaperdump.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * @author +Kernald
 * source: http://stackoverflow.com/a/27801394/2057884
 * @since Jan 9 2016
 */
@SuppressWarnings("unused")
public class EmptyRecyclerView extends RecyclerView {
    private View emptyView;
    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            float from = emptyView.getAlpha();
            if (emptyView.getVisibility() != VISIBLE)
                from = 0.0f;
            float to = emptyViewVisible ? 1.0f : 0.0f;
            AlphaAnimation anim = new AlphaAnimation(from, to);
            anim.setDuration(emptyViewVisible ? 750 : 100);
            anim.setFillAfter(true);
            emptyView.startAnimation(anim);
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            //Set visibility of recycler view
            setVisibility(emptyViewVisible ? INVISIBLE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }
}
