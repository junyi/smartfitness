package astar.smartfitness.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by student on 8/7/2015.
 */

public class ProfileView implements Target {
    View view;

    public ProfileView(View view) {
        this.view = view;
    }

    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        view.setBackground(new BitmapDrawable(bitmap));
        Log.d("OMG", "Bitmap loaded!");
    }

    @Override public void onBitmapFailed(Drawable failed) {
        Log.d("OMG","Bitmap load failed");
    }

    @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d("OMG","Preparing to load");
    }
}
