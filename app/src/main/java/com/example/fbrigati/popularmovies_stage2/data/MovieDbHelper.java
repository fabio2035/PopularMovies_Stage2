package com.example.fbrigati.popularmovies_stage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FBrigati on 02/12/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "moviesdb.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create movies table..
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MoviesEntry.TABLE_NAME + " (" +
                MovieContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MoviesEntry.COLUMN_MOVID + " INTEGER NOT NULL," +
                MovieContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MoviesEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_POPULARITY + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_FAVOURITE + " INTEGER DEFAULT 0, " +

                // Set up the movieID column as a foreign key to reviews table.
                " FOREIGN KEY (" + MovieContract.MoviesEntry.COLUMN_MOVID + ") REFERENCES " +
                MovieContract.ReviewsEntry.TABLE_NAME + " (" + MovieContract.MoviesEntry.COLUMN_MOVID + "), " +

                //unique constraints..
                "UNIQUE (" + MovieContract.MoviesEntry.COLUMN_MOVID + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);

        //Create reviews table..
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.ReviewsEntry.TABLE_NAME + " (" +
                MovieContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewsEntry.COLUMN_REV_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_MOVID + " INTEGER NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

        //unique constraints..
        "UNIQUE (" + MovieContract.ReviewsEntry.COLUMN_REV_ID + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_REVIEWS_TABLE);


        //Create trailers table..
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.TrailerEntry.COLUMN_MOVID + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_SITE + " TEXT NOT NULL, " +

                //unique constraints..
                "UNIQUE (" + MovieContract.TrailerEntry.COLUMN_KEY + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(db);
    }
}
