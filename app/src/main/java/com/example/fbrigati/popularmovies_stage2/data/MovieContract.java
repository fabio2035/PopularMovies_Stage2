package com.example.fbrigati.popularmovies_stage2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by FBrigati on 01/12/2016.
 */

public class MovieContract {


    //Content authority
    public static final String CONTENT_AUTHORITY = "com.example.fbrigati.popularmovies_stage2";

    //base content uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //available paths..
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";

    //defenitions for Movies table contents
    public static final class MoviesEntry implements BaseColumns{

        //path to table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        //content type
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                + PATH_MOVIES;
        //content item type
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                + PATH_MOVIES;

        //Table name
        public static final String TABLE_NAME = "movies";

        //fields..
        public static final String COLUMN_MOVID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVOURITE = "favourite";

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieDetailsUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildFavouritesUri(){
            return CONTENT_URI.buildUpon().appendPath("favourites").build();
        }

        public static Uri buildFavouritesIDuri(long id){
            return CONTENT_URI.buildUpon().appendPath("favourites").appendPath(Long.toString(id)).build();
        }

        public static long getMovie_idFromUri(Uri uri) {
            String movieIDstr = uri.getLastPathSegment();

            if (null != movieIDstr)
                return Long.parseLong(movieIDstr);
            else
                return 0;
        }

    }


    //defenitions for reviews table contents
    public static final class ReviewsEntry implements BaseColumns{

        //path to table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        //content type
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                + PATH_REVIEWS;
        //content item type
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                + PATH_REVIEWS;

        //Table name
        public static final String TABLE_NAME = "reviews";

        //fields..
        public static final String COLUMN_REV_ID = "id";
        public static final String COLUMN_MOVID = "movie_id";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    //defenitions for trailer table contents
    public static final class TrailerEntry implements BaseColumns{

        //path to table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        //content type
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                + PATH_TRAILERS;
        //content item type
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                + PATH_TRAILERS;

        //Table name
        public static final String TABLE_NAME = "trailers";

        //fields..
        public static final String COLUMN_MOVID = "movie_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";

        public static Uri buildTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
