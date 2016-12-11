package com.example.fbrigati.popularmovies_stage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.fbrigati.popularmovies_stage2.R;

/**
 * Created by FBrigati on 10/10/2016.
 */

public class ReviewObjectAdapter extends CursorAdapter {

    public final String LOG_TAG = ReviewObjectAdapter.class.getSimpleName();



    public ReviewObjectAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_reviews,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.textHolder.setText(cursor.getString(3));

    }

    public static class ViewHolder {
        TextView textHolder;
        int position;

        public ViewHolder(View view){
            textHolder = (TextView) view.findViewById(R.id.movie_review_content);
        }

    }
}
