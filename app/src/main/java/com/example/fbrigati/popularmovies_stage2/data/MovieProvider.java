package com.example.fbrigati.popularmovies_stage2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by FBrigati on 03/12/2016.
 */

public class MovieProvider extends ContentProvider {

    private final String LOG_TAG = MovieProvider.class.getSimpleName();
    //URI Matcher for the provider
    public static final UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_SINGLE = 101;
    static final int MOVIES_TOP_RATED = 102;
    static final int MOVIES_POPULAR = 103;
    static final int MOVIES_FAVOURITES = 104;
    static final int MOVIES_FAVOURITES_ID = 105;

    static final int MOVIES_REVIEWS = 200;
    static final int MOVIES_REVIEWS_ID = 201;

    static final int MOVIES_TRAILERS = 300;
    static final int MOVIES_TRAILERS_ID = 301;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIE_SINGLE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/favourites", MOVIES_FAVOURITES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/favourites/#", MOVIES_FAVOURITES_ID);

        //for reviews..
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, MOVIES_REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", MOVIES_REVIEWS_ID);

        //for trailers..
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, MOVIES_TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/#", MOVIES_TRAILERS_ID);

        return matcher;
    }


    //Query filters...
    private static final String specificMovie = MovieContract.MoviesEntry.COLUMN_MOVID + "= ? ";

    private static final String specificReview = MovieContract.ReviewsEntry.COLUMN_MOVID + "= ? ";

    private static final String specificTrailer = MovieContract.TrailerEntry.COLUMN_MOVID + "= ? ";

    private static final String favourites = MovieContract.MoviesEntry.COLUMN_FAVOURITE + "= 1 ";

    //sorting orders...
    private static final String orderByPopularity = MovieContract.MoviesEntry.COLUMN_POPULARITY + " DESC ";
    private static final String orderByRating = MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC ";


    private Cursor getMovieDetails(Uri uri, String[] projection, String sortOrder) {

       Long movID = MovieContract.MoviesEntry.getMovie_idFromUri(uri);
       String[] selectionArgs = {Long.toString(movID)};
       String selection = specificMovie;

      return mOpenHelper.getReadableDatabase().query(
                MovieContract.MoviesEntry.TABLE_NAME,
                projection,
                specificMovie,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getReviewDetails(Uri uri, String[] projection, String sortOrder) {

        Long movID = MovieContract.MoviesEntry.getMovie_idFromUri(uri);
        String[] selectionArgs = {Long.toString(movID)};
        String selection = specificReview;

       return mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewsEntry.TABLE_NAME,
                projection,
                specificReview,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    private Cursor getTrailerDetails(Uri uri, String[] projection, String sortOrder) {

        Long movID = MovieContract.MoviesEntry.getMovie_idFromUri(uri);
        String[] selectionArgs = {Long.toString(movID)};
        //String selection = specificTrailer;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                projection,
                specificTrailer,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //database queries for cursors..

        Cursor cursor;



        switch (uriMatcher.match(uri)){
            //All movies from table..
            case MOVIES:
            {
                    cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case MOVIE_SINGLE:
            {
                cursor = getMovieDetails(uri, projection, sortOrder);
                break;
            }
            case MOVIES_FAVOURITES:
            {

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME,
                        projection,
                        favourites,
                        null,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case MOVIES_REVIEWS:
            {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            }
            case MOVIES_REVIEWS_ID:
            {
                cursor = getReviewDetails(uri, projection, sortOrder);
                break;

            }

            case MOVIES_TRAILERS:
            {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            }
            case MOVIES_TRAILERS_ID:
            {
                cursor = getTrailerDetails(uri, projection, sortOrder);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_SINGLE:
                return MovieContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIES_POPULAR:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_TOP_RATED:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_FAVOURITES:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_FAVOURITES_ID:
                return MovieContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {
            case MOVIES: {
                _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = MovieContract.MoviesEntry.buildMoviesUri(_id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);}
                break;}
            case MOVIES_REVIEWS: {
                _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewsEntry.buildReviewsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
                case MOVIES_TRAILERS: {
                    _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                    if ( _id > 0 ) {
                        returnUri = MovieContract.TrailerEntry.buildTrailersUri(_id);
                    }else{
                        throw new android.database.SQLException("Failed to insert row into " + uri);}
                    break;
            }default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIES:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case MOVIES_REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOVIES_TRAILERS:
            db.beginTransaction();
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = uriMatcher.match(uri);
            int rowsUpdated;

            switch (match) {
                case MOVIES_FAVOURITES_ID:

                    //Log.v(LOG_TAG, "Values for update query: " + )
                    rowsUpdated = db.update(MovieContract.MoviesEntry.TABLE_NAME, values, specificMovie,
                            selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }
}
