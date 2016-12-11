package com.example.fbrigati.popularmovies_stage2;

        import android.content.ActivityNotFoundException;
        import android.content.ContentValues;
        import android.content.Intent;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.LoaderManager;
        import android.support.v4.content.CursorLoader;
        import android.support.v4.content.Loader;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.CursorAdapter;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.RelativeLayout;
        import android.widget.SimpleCursorAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.example.fbrigati.popularmovies_stage2.adapters.ReviewObjectAdapter;
        import com.example.fbrigati.popularmovies_stage2.adapters.TrailerObjectAdapter;
        import com.example.fbrigati.popularmovies_stage2.data.FetchJasonData;
        import com.example.fbrigati.popularmovies_stage2.data.MovieContract;
        import com.squareup.picasso.Picasso;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Vector;

/**
 * Created by FBrigati on 04/12/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    final static String LOG_TAG = DetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private boolean isFavourite;
    private long movieID = 0;

    private static final int DETAIL_LOADER = 0;
    private static final int DETAIL_REVIEWS_LOADER = 1;
    private static final int DETAIL_TRAILER_LOADER = 2;

    private Uri detail_uri;

    ReviewObjectAdapter reviewsAdapter;

    TrailerObjectAdapter trailersAdapter;


    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MoviesEntry.TABLE_NAME + "." + MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.COLUMN_MOVID,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MoviesEntry.COLUMN_OVERVIEW,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POSTER_PATH,
            MovieContract.MoviesEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MoviesEntry.COLUMN_POPULARITY,
            MovieContract.MoviesEntry.COLUMN_FAVOURITE
    };

    //bounded columns...
    public static final int COL_MOVID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_POSTER_PATH = 6;
    public static final int COL_BACKDRP_PATH = 7;
    public static final int COL_VOTE_AVERAGE = 8;
    public static final int COL_FAVOURITE = 10;


    private static final String[] MOVIE_REVIEWS_COLUMNS = {
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry._ID,
            MovieContract.ReviewsEntry.COLUMN_REV_ID,
            MovieContract.ReviewsEntry.COLUMN_MOVID,
            MovieContract.ReviewsEntry.COLUMN_CONTENT};


    //bounded columns...
    public static final int COL_REV_ID = 1;
    public static final int COL_REV_MOVID = 2;
    public static final int COL_REV_CONTENT = 3;


    private static final String[] MOVIE_TRAILERS_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVID,
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_SITE};


        //bounded columns...
    public static final int COL_TRLR_MOVID = 1;
    public static final int COL_TRLR_KEY = 2;
    public static final int COL_TRLR_NAME = 3;
    public static final int COL_TRLR_SITE = 4;


    public DetailFragment(){

    }

    private TextView mtitle;
    private ImageView mposter;
    private TextView msynopsis;
    private TextView mrating;
    private TextView mrel_date;
    private ListView vreviews_list;
    private ListView vtrailers_list;
    private RelativeLayout mrelativeLayout;
    private ImageButton mFavoriteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        //get intent...
        //Intent intent = getActivity().getIntent();

        Bundle arguments = getArguments();
        if (arguments != null){
            detail_uri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(com.example.fbrigati.popularmovies_stage2.R.layout.fragment_movie_detail, container, false);

        //Post original title
        mtitle = (TextView) rootView.findViewById(com.example.fbrigati.popularmovies_stage2.R.id.movie_original_title);

        //Post poster image
        mposter = (ImageView) rootView.findViewById(com.example.fbrigati.popularmovies_stage2.R.id.poster_image_detail);

        //Post synopsis
        msynopsis = (TextView) rootView.findViewById(com.example.fbrigati.popularmovies_stage2.R.id.movie_synopsis);

        //Post ratings
        mrating = (TextView) rootView.findViewById(com.example.fbrigati.popularmovies_stage2.R.id.movies_rating);

        //Post release date
        mrel_date = (TextView) rootView.findViewById(com.example.fbrigati.popularmovies_stage2.R.id.movies_date_release);

        //get reference to reviews ListView
        vreviews_list = (ListView) rootView.findViewById(R.id.movies_reviews_container);

        //get reference to trailers ListView
        vtrailers_list = (ListView) rootView.findViewById(R.id.movies_trailer_container);

        mrelativeLayout  = (RelativeLayout) rootView.findViewById(R.id.relative_layout_id);

        mFavoriteButton = (ImageButton) rootView.findViewById(R.id.favourite_button);

        vtrailers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null){
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + cursor.getString(COL_TRLR_KEY)));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + cursor.getString(COL_TRLR_KEY)));
                    try {
                        startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        startActivity(webIntent);
                    }
                }

            }
        });

        mFavoriteButton.setOnClickListener(this);

        reviewsAdapter = new ReviewObjectAdapter(getActivity(), null, 0);

        trailersAdapter = new TrailerObjectAdapter(getActivity(), null, 0);

        //Log.v(LOG_TAG, "list size: " + reviews_list.size());
        vreviews_list.setAdapter(reviewsAdapter);
        vtrailers_list.setAdapter(trailersAdapter);

        //request for an update on the reviews and trailers..
        requestReviewsAndTrailers(MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri));

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

         private void requestReviewsAndTrailers(Long movid) {

            Log.v(LOG_TAG, "Inside requestReviewsAndTrailers with movid:" + movid );

            String lang = "en-US";
            final String LANG_PARAM = "language";
            final String APIKEY_PARAM = "api_key";
            final String REVIEWS_PARAM = "reviews";
             final String TRAILERS_PARAM = "videos";

            Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(Long.toString(movid))
                    .appendPath(REVIEWS_PARAM)
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                    .appendQueryParameter(LANG_PARAM,lang)
                    .build();

            //Instantiate Request..
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url = builtUri.toString();
            Log.v(LOG_TAG, "Url being called: " + url);

            //String request..
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Check response...
                            if (!response.isEmpty()) {
                                try {
                                    FetchJasonData.getReviewsDataFromJson(getActivity(), response, detail_uri);
                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "There was an error retrieving data from server, " +
                                                    "please check your internet connection and try again",
                                            Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getActivity(), "There was an error " +
                                                "please check your internet connection and try again"
                                        , Toast.LENGTH_LONG).show();
                            }
                        }
                    },  new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(getActivity(), "There was an error " +
                                    "please check your internet connection and try again"
                            , Toast.LENGTH_LONG).show();
                }
            });
            //Add the request to RequestQueue
            queue.add(stringRequest);

             //Go for the trailers...
             builtUri = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon()
                     .appendPath(Long.toString(movid))
                     .appendPath(TRAILERS_PARAM)
                     .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                     .appendQueryParameter(LANG_PARAM,lang)
                     .build();

             url = builtUri.toString();
             Log.v(LOG_TAG, "Url being called for trailers: " + url);

             //String request..
             stringRequest = new StringRequest(Request.Method.GET, url,
                     new Response.Listener<String>() {
                         @Override
                         public void onResponse(String response) {
                             //Check response...
                             if (!response.isEmpty()) {
                                 try {
                                     FetchJasonData.getTrailersDataFromJson(getActivity(), response, detail_uri);
                                 } catch (JSONException e) {
                                     Toast.makeText(getActivity(), "There was an error retrieving data from server, " +
                                                     "please check your internet connection and try again",
                                             Toast.LENGTH_LONG).show();
                                     e.printStackTrace();
                                 }
                             } else {
                                 Toast.makeText(getActivity(), "There was an error " +
                                                 "please check your internet connection and try again"
                                         , Toast.LENGTH_LONG).show();
                             }
                         }
                     },  new Response.ErrorListener(){
                 @Override
                 public void onErrorResponse(VolleyError error){
                     Toast.makeText(getActivity(), "There was an error " +
                                     "please check your internet connection and try again"
                             , Toast.LENGTH_LONG).show();
                 }
             });

             //Add the request to RequestQueue
             queue.add(stringRequest);

        }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "inside onCreateLoader with uri: " + detail_uri);
        Uri uri;

        switch (id){
        case DETAIL_LOADER:
            Log.v(LOG_TAG, "details cursor loader called with uri:" + detail_uri);
            if (detail_uri != null){
                 return  new CursorLoader(
                    getActivity(),
                    detail_uri,
                    MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    null);}

        case DETAIL_REVIEWS_LOADER:
            uri = MovieContract.ReviewsEntry.buildReviewsUri(
                    MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri)
            );
            Log.v(LOG_TAG, "reviews cursor loader called with uri:" + uri);
                return  new CursorLoader(
                        getActivity(),
                        uri,
                        MOVIE_REVIEWS_COLUMNS,
                        null,
                        null,
                        null);

        case DETAIL_TRAILER_LOADER:
        uri = MovieContract.TrailerEntry.buildTrailersUri(
                MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri)
        );
        Log.v(LOG_TAG, "trailers cursor loader called with uri:" + uri);
        return  new CursorLoader(
                getActivity(),
                uri,
                MOVIE_TRAILERS_COLUMNS,
                null,
                null,
                null);
    }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()){
        case DETAIL_LOADER:


        if (data != null && data.moveToFirst()) {

            //reset favourite boolean...
            if(data.getInt(COL_FAVOURITE)==1){
                isFavourite = true;
                mFavoriteButton.setImageResource(R.drawable.ic_fav_button_on);}
            else{
                isFavourite = false;
                mFavoriteButton.setImageResource(R.drawable.ic_fav_button_off);
            }

            //check backdrop
            if(!data.getString(COL_BACKDRP_PATH).isEmpty()){

                //Post poster image
                Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_IMAGE_BASE_URL).buildUpon()
                        .appendEncodedPath(BuildConfig.MOVIEDB_PIC_SIZE_BIG).appendEncodedPath(data.getString(COL_BACKDRP_PATH))
                        .build();
                String ref = builtUri.toString().replace("%2F", "");

                Picasso.with(getContext()).load(ref).into(mposter);
                Log.v(LOG_TAG, "Url reference: " + ref);
            mposter.setScaleType(ImageView.ScaleType.FIT_XY);
            mposter.setAdjustViewBounds(true);}

            Log.v(LOG_TAG, "title:" + data.getString(COL_TITLE));
            //Post original title
            mtitle.setText(data.getString(COL_TITLE));

            //Post synopsispsis;
            msynopsis.setText(data.getString(COL_OVERVIEW));

            //Post ratings
            mrating.setText(data.getString(COL_VOTE_AVERAGE));

            //Post release date
            mrel_date.setText(data.getString(COL_RELEASE_DATE).substring(0,4));

            //set favourite icon..
            if(data.getInt(COL_FAVOURITE)==1){
                Log.v(LOG_TAG, "Is favourite.. setting button image");
                mFavoriteButton.setImageResource(R.drawable.ic_fav_button_on);
            }else {
                Log.v(LOG_TAG, "Not favourite because value is:" + data.getInt(COL_FAVOURITE));
            }


            break;}

            case DETAIL_REVIEWS_LOADER:
                if (data != null && data.moveToFirst()) {
                    for(int i = 0; i < data.getColumnNames().length; i++) {
                    Log.v(LOG_TAG, "Reviews Cursor column name: " + data.getColumnName(i));}
                    reviewsAdapter.swapCursor(data);
                }
                break;
            case DETAIL_TRAILER_LOADER:
                if (data != null && data.moveToFirst()) {
                    for(int i = 0; i < data.getColumnNames().length; i++) {
                        Log.v(LOG_TAG, "Trailer Cursor column names: " + data.getColumnName(i));}
                    trailersAdapter.swapCursor(data);
                }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Inside swapcursor...");
        switch (loader.getId()){
            case DETAIL_REVIEWS_LOADER:
                reviewsAdapter.swapCursor(null);
                break;
            case DETAIL_TRAILER_LOADER:
                trailersAdapter.swapCursor(null);
                break;
        }

    }

    @Override
    public void onClick(View v) {

        //change movie to favourite/not favourite..
        ContentValues contents = new ContentValues();
        if(isFavourite) {
            contents.put(MovieContract.MoviesEntry.COLUMN_FAVOURITE, 0);
            Log.v(LOG_TAG, "updating favourite with 0");
        }else{
            contents.put(MovieContract.MoviesEntry.COLUMN_FAVOURITE, 1);
            Log.v(LOG_TAG, "updating favourite with 1");
        }

        String[] mSelectionArgs = {Long.toString(MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri))};

        int rowsUpdated = getActivity().getContentResolver().update(
                MovieContract.MoviesEntry.buildFavouritesIDuri(MovieContract.MoviesEntry.getMovie_idFromUri(detail_uri)),
                contents,
                null,
                mSelectionArgs);
        Log.v(LOG_TAG, "rows updated: " + rowsUpdated);
        if(rowsUpdated > 0){
            if(isFavourite){
                Toast.makeText(getActivity(), "Favourite item disabled", Toast.LENGTH_SHORT).show();
                mFavoriteButton.setImageResource(R.drawable.ic_fav_button_off);
                isFavourite = false;
            }else{
                Toast.makeText(getActivity(), "Movie added to favourites", Toast.LENGTH_SHORT).show();
                mFavoriteButton.setImageResource(R.drawable.ic_fav_button_on);
                isFavourite = true;
            }
        }else{
            Toast.makeText(getActivity(), "An error ocurred no items were updated", Toast.LENGTH_SHORT).show();
        }

    }
}

