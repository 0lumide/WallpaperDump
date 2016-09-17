package co.mide.wallpaperdump.view;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import co.mide.wallpaperdump.DumpCardViewModel;
import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.databinding.LayoutDumpCardBinding;
import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;


/**
 * Adapter for the recycler view
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewBindingHolder> {
    Activity activity;
    DatabaseHandler databaseHandler;
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    PublishSubject<Pair<Intent, Pair[]>> launchActivitySubject = PublishSubject.create();

    public RecyclerAdapter(Activity activity, DatabaseHandler databaseHandler) {
        this.activity = activity;
        this.databaseHandler = databaseHandler;
    }

    @Override
    public ViewBindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutDumpCardBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.layout_dump_card,
                parent,
                false);
        return new ViewBindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewBindingHolder viewBindingHolder, int position) {
        final int actualPosition = databaseHandler.getDumpCount() - 1 - position;
        Dump dump = databaseHandler.getDump(actualPosition);
        DumpCardViewModel viewModel = new DumpCardViewModel(activity, dump, actualPosition);
        viewBindingHolder.setBindingViewModel(viewModel);

        //un-subscribe previous subscription
        Subscription subscription = viewBindingHolder.subscription;
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscriptions.remove(subscription);
        }

        //subscribe to launch activity observable and call onNext
        subscription = viewModel.getLaunchActivityObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launchActivitySubject::onNext);

        //Add the subscription to the ViewHolder and composite so it can be un-subscribed later
        viewBindingHolder.setSubscription(subscription);
        subscriptions.add(subscription);
    }

    public Observable getLaunchActivityObservable() {
        return launchActivitySubject;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        subscriptions.clear();
    }

    @Override
    public int getItemCount() {
        return databaseHandler.getDumpCount();
    }

    public static class ViewBindingHolder extends RecyclerView.ViewHolder {
        private LayoutDumpCardBinding binding;
        private Subscription subscription;

        public void setBindingViewModel(DumpCardViewModel viewModel) {
            binding.setViewModel(viewModel);
        }

        public void setSubscription(Subscription subscription) {
            this.subscription = subscription;
        }

        public ViewBindingHolder(LayoutDumpCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}