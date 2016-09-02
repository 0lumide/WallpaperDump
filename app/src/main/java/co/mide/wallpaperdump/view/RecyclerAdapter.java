package co.mide.wallpaperdump.view;
import android.app.Activity;
import android.app.WallpaperManager;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import co.mide.wallpaperdump.DumpCardViewModel;
import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.databinding.LayoutDumpCardBinding;
import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;


/**
 * Adapter for the recycler view
 * Created by Olumide on 8/9/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewBindingHolder> {
    Activity activity;
    WallpaperManager wallpaperManager;
    DatabaseHandler databaseHandler;

    public RecyclerAdapter(Activity activity, DatabaseHandler databaseHandler) {
        this.activity = activity;
        wallpaperManager = WallpaperManager.getInstance(activity);
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
        final int width = wallpaperManager.getDesiredMinimumWidth();
        final int height = wallpaperManager.getDesiredMinimumHeight();
        final int actualPosition = databaseHandler.getDumpCount() - 1 - position;
        Dump dump = databaseHandler.getDump(actualPosition);
        DumpCardViewModel viewModel = new DumpCardViewModel(activity, dump, actualPosition);
        viewBindingHolder.setBindingDump(viewModel);
        final String imageBig = String.format("http://api.wallpaperdumps.com/v1/image/%dx%d/%s", width, height, dump.getImages().get(0));
    }

    @Override
    public int getItemCount() {
        return databaseHandler.getDumpCount();
    }

    public static class ViewBindingHolder extends RecyclerView.ViewHolder{
        private LayoutDumpCardBinding binding;

        public void setBindingDump(DumpCardViewModel viewModel){
            binding.setViewModel(viewModel);
        }

        public ViewBindingHolder(LayoutDumpCardBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}