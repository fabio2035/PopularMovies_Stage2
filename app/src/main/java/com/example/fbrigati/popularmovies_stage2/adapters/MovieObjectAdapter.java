package com.example.fbrigati.popularmovies_stage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fbrigati.popularmovies_stage2.BuildConfig;
import com.example.fbrigati.popularmovies_stage2.MovieList;
import com.example.fbrigati.popularmovies_stage2.R;
import com.squareup.picasso.Picasso;

/**
 * Created by FBrigati on 10/10/2016.
 */

public class MovieObjectAdapter extends CursorAdapter {

    public final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();



    public MovieObjectAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //MovieObject movieObject = getItem(cursor.getPosition());

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(BuildConfig.MOVIEDB_PIC_SIZE_SMALL).appendPath(cursor.getString(MovieList.COL_POSTER_PATH))
                .build();
        Log.v(LOG_TAG, "buiilt uri:" + builtUri);

        String ref = builtUri.toString().replace("%2F","");

        Picasso.with(context).load(ref).into(viewHolder.imageHolder);

        viewHolder.textHolder.setText(cursor.getString(MovieList.COL_TITLE));

    }

    public static class ViewHolder {
        TextView textHolder;
        ImageView imageHolder;
        int position;

        public ViewHolder(View view){
            imageHolder = (ImageView) view.findViewById(R.id.poster_image);
            textHolder = (TextView) view.findViewById(R.id.movie_title);
        }

    }
}
