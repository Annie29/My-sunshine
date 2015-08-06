/*
 * Copyright (C) 2015 Laurie White (copyright@lauriewhite.org)
 *
 * Project based on Project Sunshine from Udacity's "Developing
 * Android Apps" course at
 * https://www.udacity.com/course/developing-android-apps--ud853
 *
 */
package com.example.android.project1movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Encapsulates fetching the movie information and displaying it as a {@link GridView} layout.
 */
public class MovieFragment extends Fragment {

    private MovieCollection mCollection;

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private ArrayAdapter<MovieData> mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPref.getString(getString(R.string.PREF_sort_order_key), getString(R.string.PREF_option_popularity));
        String minVotes = sharedPref.getString(getString(R.string.PREF_minimum_votes_key), getString(R.string.PREF_minimum_votes_default));
        fetchMoviesTask.execute(sortOrder, minVotes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter =
                new MovieDataAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.grid_item_movie, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        new ArrayList<MovieData>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView listView = (GridView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mMovieAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieData forecast = mMovieAdapter.getItem(position);
                //  TODO: Come up with a better name than EXTRA_TEXT
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private String moviesJsonStr = null;

        @Override
        /**
         * params[0] = sort order
         * params[1] = minimum votes
         */
        protected MovieData[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            try {
                // Construct the URL for the theMovieDB query
                // Possible parameters are available at TMDB's forecast API page, at
                // https://www.themoviedb.org/documentation/api/discover
                final String MOVIE_BASE_URL = getString(R.string.API_URL_base) + getString(R.string.API_discover);
                String sort_order = getString(R.string.PREF_option_popularity);
                if (params.length > 0) {
                    sort_order = params[0];
                }

                //  Decide which URL to use based on the parameters.
                //  All URLS have the same beginning
                Uri builtUri = Uri.parse(MOVIE_BASE_URL);
                //  THen determine whether to search by popularity or vote average.
                if (sort_order.equals(getString(R.string.PREF_option_popularity))) {
                    builtUri = builtUri.buildUpon()
                            .appendQueryParameter(getString(R.string.API_sort_query),
                                    getString(R.string.API_sort_by_pop)).build();
                } else {  //  If vote average, do we care about the minimum number of votes?
                    builtUri = builtUri.buildUpon()
                            .appendQueryParameter(getString(R.string.API_sort_query),
                                    getString(R.string.API_sort_by_vote)).build();
                    if (sort_order.equals(getString(R.string.PREF_option_rating_count))) {
                        String minVotes = getString(R.string.PREF_minimum_votes_default);
                        //  Did the user enter a valid integer in the minimum number of votes pref?
                        if (params.length > 1) {
                            try {
                                Integer i = new Integer(params[1]);
                                if (i > 0) {
                                    minVotes = params[1];
                                }
                            } catch (NumberFormatException e) {
                                //  Well, that wasn't an int, keep the 50.
                            }
                        }
                        builtUri = builtUri.buildUpon()
                                .appendQueryParameter(getString(R.string.API_use_minimum_votes),
                                        minVotes).build();
                    }
                }

                //  All URIs end with the key
                builtUri = builtUri.buildUpon().appendQueryParameter(getString(R.string.API_key_query),
                        getString(R.string.API_key))
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to the movie database, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                this.moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            mCollection = new MovieCollection(this.moviesJsonStr);
            return null;
        }


        @Override
        protected void onPostExecute(MovieData[] result) {
            super.onPostExecute(result);
            mMovieAdapter.clear();
            for (MovieData dayForecastStr : mCollection) {
                mMovieAdapter.add(dayForecastStr);
            }
        }
    }
}
