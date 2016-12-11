package com.example.fbrigati.popularmovies_stage2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity {

    public final static String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    public MovieObject movieObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.fbrigati.popularmovies_stage2.R.layout.activity_movie_detail);

        if(savedInstanceState == null){
            //create the detail fragment and add it to the activity
            //using a fragment transaction

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(com.example.fbrigati.popularmovies_stage2.R.id.activity_movie_detail, fragment)
                    .commit();
        }
    }



}
