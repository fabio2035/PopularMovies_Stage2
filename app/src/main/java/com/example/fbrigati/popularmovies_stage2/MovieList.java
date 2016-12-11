package com.example.fbrigati.popularmovies_stage2;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fbrigati.popularmovies_stage2.adapters.MovieObjectAdapter;
import com.example.fbrigati.popularmovies_stage2.data.FetchJasonData;
import com.example.fbrigati.popularmovies_stage2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;


public class MovieList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String ID_MESSAGE = "com.example.fbrigati.popularmovies.MESSAGE";

    private static final int MOVIE_LOADER = 0;
    private static final int MOVIE_FAVOURITES_LOADER = 1;

    private int mPosition = GridView.INVALID_POSITION;

    MovieObjectAdapter movieAdapter;

    private ArrayList<MovieObject> movieList;

    //bounded columns...
    public static final int COL_MOVID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_ORIGINAL_TITLE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_POSTER_PATH = 6;
    public static final int COL_BACKDROP_PATH = 7;
    public static final int COL_VOTE_AVERAGE = 8;
    public static final int COL_POPULARITY = 9;
    public static final int COL_FAVOURITE = 10;


    private static final String[] MOVIE_COLUMNS = {
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


    public MovieObject[] movieObjects = {new MovieObject("","",
            "Mock Original",
            "Mock Overview",
            "01/01/1979",
            "some.jpg",
            "some2.jpg",
            "2.58","")};

    public interface Callback {
        /*//**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri uri);
    }

    public final static String LOG_TAG = MovieList.class.getSimpleName();


    public MovieList() {
        // Required empty public constructor
    }

    @Override
         public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                 // Inflate the menu; this adds items to the action bar if it is present.
                 inflater.inflate(com.example.fbrigati.popularmovies_stage2.R.menu.fragment_main, menu);
             }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState == null || !savedInstanceState.containsKey(ID_MESSAGE)) {
          movieList = new ArrayList<MovieObject>(Arrays.asList(movieObjects));
                     }
                 else {
                         movieList = savedInstanceState.getParcelableArrayList(ID_MESSAGE);
                     }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
           outState.putParcelableArrayList(ID_MESSAGE, movieList);
           super.onSaveInstanceState(outState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_main, container, false);

        movieAdapter = new MovieObjectAdapter(getActivity(),null,0);

        //Get a reference to the listView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(com.example.fbrigati.popularmovies_stage2.R.id.movies_grid);
        gridView.setAdapter(movieAdapter);
        //TextView emptyView = (TextView) findViewById(R.id.empty);
        //emptyView.setText("No Items Available");
        //gridView.setEmptyView(emptyView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor != null){
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MoviesEntry.buildMoviesUri(cursor.getLong(COL_MOVID)));
                }
                mPosition = position;
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String QUERY_SORT = pref.getString(getString(com.example.fbrigati.popularmovies_stage2.R.string.pref_sort_key), getString(com.example.fbrigati.popularmovies_stage2.R.string.pref_sort_default));
        if((QUERY_SORT.equals(getString(R.string.pref_sort_entry_topr))) || (QUERY_SORT.equals(getString(R.string.pref_sort_entry_popr)))) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
            getLoaderManager().destroyLoader(MOVIE_FAVOURITES_LOADER);
            requestMovies(QUERY_SORT);
        }else{
            getLoaderManager().initLoader(MOVIE_FAVOURITES_LOADER, null, this);
            getLoaderManager().destroyLoader(MOVIE_LOADER);}
    }

    private void requestMovies(String preferred_sort) {

        //Get shared preferences..

        String lang = "en-US";
        final String LANG_PARAM = "language";
        final String APIKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon()
                .appendPath(preferred_sort)
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter(LANG_PARAM,lang)
                .build();

        //Instantiate Request..
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = builtUri.toString();

        //String request..
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Check response...
                        if (!response.isEmpty()) {
                            try {
                                FetchJasonData.getMovieDataFromJson(getActivity(),response);
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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){

            case MOVIE_LOADER:
            {
               //define sortorder.
                String sortOrder = "";
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String QUERY_SORT = pref.getString(getString(com.example.fbrigati.popularmovies_stage2.R.string.pref_sort_key), getString(com.example.fbrigati.popularmovies_stage2.R.string.pref_sort_default));

                if (QUERY_SORT.equals(getString(R.string.pref_sort_entry_popr))) {
                    sortOrder = MovieContract.MoviesEntry.COLUMN_POPULARITY + " DESC ";
               }else {
                    sortOrder = MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC ";
                }


                return new CursorLoader(getActivity(),
                        MovieContract.MoviesEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        sortOrder);

            }
            case MOVIE_FAVOURITES_LOADER:
            {

                return new CursorLoader(getActivity(),
                        MovieContract.MoviesEntry.buildFavouritesUri(),
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);

            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    movieAdapter.swapCursor(data);
            }




    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
