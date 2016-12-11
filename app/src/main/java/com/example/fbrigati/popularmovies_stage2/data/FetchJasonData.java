package com.example.fbrigati.popularmovies_stage2.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import static java.security.AccessController.getContext;

/**
 * Created by FBrigati on 07/12/2016.
 */

public class FetchJasonData {

    //private Context mContext;

    final static String LOG_TAG = FetchJasonData.class.getSimpleName();

    public static void getReviewsDataFromJson(Context mContext, String movieDBJsonStr, Uri detail_uri)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String JO_MOVID = "id";
        final String JO_LIST = "results";
        final String JO_REVIEW_ID = "id";
        final String JO_REVIEW_CONTENT = "content";

        JSONObject moviesJson = new JSONObject(movieDBJsonStr);
        JSONArray moviesList = moviesJson.getJSONArray(JO_LIST);

        // Insert the new movies information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesList.length());


        for(int i = 0; i < moviesList.length(); i++) {

            ContentValues movieValues = new ContentValues();
            // Get the JSON object representing a single movie
            JSONObject movieObject = moviesList.getJSONObject(i);

           //initialize new MovieObject..
            //MovieObject mvObj = new MovieObject(
            movieValues.put(MovieContract.ReviewsEntry.COLUMN_REV_ID, movieObject.getString(JO_REVIEW_ID));
            movieValues.put(MovieContract.ReviewsEntry.COLUMN_MOVID, MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri));
            movieValues.put(MovieContract.ReviewsEntry.COLUMN_CONTENT, movieObject.getString(JO_REVIEW_CONTENT));

            cVVector.add(movieValues);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        return;
    }

    public static void getTrailersDataFromJson(Context mContext, String movieDBJsonStr, Uri detail_uri)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String JO_MOVID = "id";
        final String JO_LIST = "results";
        final String JO_KEY = "key";
        final String JO_NAME = "name";
        final String JO_SITE = "site";

        JSONObject moviesJson = new JSONObject(movieDBJsonStr);
        JSONArray moviesList = moviesJson.getJSONArray(JO_LIST);

        // Insert the new movies information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesList.length());

        //if (moviesList.length()>0) movieAdapter.clear();

        /*if (moviesList.length()> 5){
            Log.v(LOG_TAG, "deleting old data..");
            // delete old data so we don't build up on table rows
            mContext.getContentResolver().delete(MovieContract.ReviewsEntry.CONTENT_URI,
                    null,
                    null);
        } */

        for(int i = 0; i < moviesList.length(); i++) {

            ContentValues movieValues = new ContentValues();
            // Get the JSON object representing a single movie
            JSONObject movieObject = moviesList.getJSONObject(i);

           //initialize new MovieObject..
            //MovieObject mvObj = new MovieObject(
            movieValues.put(MovieContract.TrailerEntry.COLUMN_MOVID, MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri));
            movieValues.put(MovieContract.TrailerEntry.COLUMN_KEY, movieObject.getString(JO_KEY));
            movieValues.put(MovieContract.TrailerEntry.COLUMN_NAME, movieObject.getString(JO_NAME));
            movieValues.put(MovieContract.TrailerEntry.COLUMN_SITE, movieObject.getString(JO_SITE));

            cVVector.add(movieValues);

        }
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
        }

        return;
    }

    public static void getMovieDataFromJson(Context mContext, String movieDBJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JO_MOVID = "id";
        final String JO_LIST = "results";
        final String JO_TITLE = "title";
        final String JO_ORIGINAL_TITLE = "original_title";
        final String JO_OVERVIEW = "overview";
        final String JO_RELEASE_DATE = "release_date";
        final String JO_POSTER = "poster_path";
        final String JO_BACKDROP_PATH = "backdrop_path";
        final String JO_VOTE_AVG = "vote_average";
        final String JO_POPULARITY = "popularity";

        JSONObject moviesJson = new JSONObject(movieDBJsonStr);
        JSONArray moviesList = moviesJson.getJSONArray(JO_LIST);

        // Insert the new movies information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesList.length());


        if (moviesList.length()> 5){
            // delete old data so we don't build up on table rows
            mContext.getContentResolver().delete(MovieContract.MoviesEntry.CONTENT_URI,
                    MovieContract.MoviesEntry.COLUMN_FAVOURITE + " = 0 ",
                    null);
        }

        for(int i = 0; i < moviesList.length(); i++) {


            // Get the JSON object representing a single movie
            JSONObject movieObject = moviesList.getJSONObject(i);

            ContentValues movieValues = new ContentValues();

            //initialize new MovieObject..
            //MovieObject mvObj = new MovieObject(
            movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVID, movieObject.getString(JO_MOVID));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_TITLE, movieObject.getString(JO_TITLE));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, movieObject.getString(JO_ORIGINAL_TITLE));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_OVERVIEW, movieObject.getString(JO_OVERVIEW));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE, movieObject.getString(JO_RELEASE_DATE));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_POSTER_PATH, movieObject.getString(JO_POSTER));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_BACKDROP_PATH, movieObject.getString(JO_BACKDROP_PATH));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieObject.getString(JO_VOTE_AVG));
            movieValues.put(MovieContract.MoviesEntry.COLUMN_POPULARITY, movieObject.getString(JO_POPULARITY));

            cVVector.add(movieValues);
        }
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.MoviesEntry.CONTENT_URI, cvArray);
        }

        return;
    }

}
