package co.mide.wallpaperdump.views;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import co.mide.imagegridlayout.ImageGridLayout;
import co.mide.textimageview.ForegroundImageView;
import co.mide.wallpaperdump.GalleryActivity;
import co.mide.wallpaperdump.R;
import co.mide.wallpaperdump.db.DatabaseHandler;
import co.mide.wallpaperdump.model.Dump;


/**
 * Adapter for the recycler view
 * Created by Olumide on 8/9/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Activity context;
    WallpaperManager wallpaperManager;
    DatabaseHandler databaseHandler;

    public RecyclerAdapter(Activity context, DatabaseHandler databaseHandler) {
        this.context = context;
        wallpaperManager = WallpaperManager.getInstance(context);
        this.databaseHandler = databaseHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_dump_card, parent, false);
        // set the view's size, margins, padding and layout parameters
        return new RecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int width = wallpaperManager.getDesiredMinimumWidth();
        final int height = wallpaperManager.getDesiredMinimumHeight();
        final int actualPosition = databaseHandler.getDumpCount() - 1 - position;
        Dump dump = databaseHandler.getDump(actualPosition);
        final String imageBig = String.format("http://api.wallpaperdumps.com/v1/image/%dx%d/%s", width, height, dump.getImages().get(0));
        final int limit = holder.imageGridLayout.getMaxImagesCount();

        holder.imageGridLayout.setOnMoreClickedCallback(new ImageGridLayout.OnMoreClicked() {
            @Override
            public void onMoreClicked(ImageGridLayout imageGridLayout) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("DUMP_INDEX", actualPosition);
                intent.putExtra("WALLPAPER_INDEX", limit);
                context.startActivity(intent);
            }
        });
        for(int i = 0; i < limit && i < dump.getImages().size(); i++) {
            ForegroundImageView imageView;
            if(i >= holder.imageGridLayout.getImageCount()) {
                imageView = new ForegroundImageView(context);
                holder.imageGridLayout.addView(imageView);
            }else {
                imageView = (ForegroundImageView) holder.imageGridLayout.getChildAt(i);
            }
            final String id = dump.getImages().get(i);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                imageView.setTransitionName(id);


            int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
            TypedArray ta = context.obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
            ta.recycle();
            imageView.setForeground(drawableFromTheme);
            final int wallpaperIndex = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Intent intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra("DUMP_INDEX", actualPosition);
                    intent.putExtra("WALLPAPER_INDEX", wallpaperIndex);
                    intent.putExtra("WALLPAPER_ID", id);


                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(context, new Pair<>(v, id));
                    ActivityCompat.startActivity(context, intent, options.toBundle());
                }
            });
            //todo replace with wallpaper dump api wrapper
//            String imageLink = "http://i.imgur.com/"+dump.getImages().get(i)+"b.jpg";
//            String imageLink = "http://i.imgur.com/"+dump.getImages().get(i)+"s.jpg";
            String imageLink = "http://i.imgur.com/"+dump.getImages().get(i)+"l.jpg";
//            String imageLink = "http://i.imgur.com/"+dump.getImages().get(i).getImageId()+"t.jpg";
//            imageLink = String.format("http://api.wallpaperdumps.com/v1/image/%dx%d/%s", width, height, dump.getImages().get(i).getImageId());
            //add imageView to layout before gliding, since the layout assigns width and height to the view
            Glide.with(context).
                    load(imageLink)
                    .centerCrop()
                    .into(imageView);
        }
        if(dump.getImages().size() > limit)
            holder.imageGridLayout.setMoreImagesCount(dump.getImages().size() - limit);
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(String.format("<a href=\"https://imgur.com/user/%s\">%s</a>",
                    dump.getUploadedBy(), dump.getUploadedBy()), Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            result = Html.fromHtml(String.format("<a href=\"https://imgur.com/user/%s\">%s</a>",
                    dump.getUploadedBy(), dump.getUploadedBy()));
        }
        holder.uploader.setText(result);
        holder.uploader.setMovementMethod(LinkMovementMethod.getInstance());
        holder.date.setText(DateFormat.getDateTimeInstance().format(new Date(dump.getTimestamp())));
        holder.title.setText(dump.getTitle());
    }

    @Override
    public int getItemCount() {
        return databaseHandler.getDumpCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageGridLayout imageGridLayout;
        public TextView date;
        public TextView title;
        public TextView uploader;

        public ViewHolder(View view){
            super(view);
            imageGridLayout = (ImageGridLayout) view.findViewById(R.id.grid_layout);
            date = (TextView)view.findViewById(R.id.post_time);
            title = (TextView)view.findViewById(R.id.title);
            uploader = (TextView)view.findViewById(R.id.username);
        }
    }
}