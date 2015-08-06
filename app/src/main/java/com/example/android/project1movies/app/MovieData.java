package com.example.android.project1movies.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A class to represent the data about one movie.
 * This class is responsible for parsing the JSON data
 * about a single movie.  As the project is expanded,
 * this class will also expand to hold more information.
 *
 * @author Laurie White
 * @version 7/27/2015.
 */
public class MovieData implements Parcelable {

    private final String LOG_TAG = MovieData.class.getSimpleName();

    public MovieData(JSONObject jsonData) {
        final String TMD_title = "original_title";
        final String TMD_image = "poster_path";
        final String TMD_vote_average = "vote_average";
        final String TMD_vote_count = "vote_count";
        final String TMD_release_date = "release_date";
        final String TMD_synopsis = "overview";


        try {
            mTitle = jsonData.getString(TMD_title);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "No name for movie");
        }
        try {
            mPoster = jsonData.getString(TMD_image);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "No poster for movie");
        }

        try {
            mReleaseDate = jsonData.getString(TMD_release_date);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "No release date for movie");
        }

        try {
            mVoteAverage = jsonData.getDouble(TMD_vote_average);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "No vote average for movie");
        }

        try {
            mOverview = jsonData.getString(TMD_synopsis);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "No overview for movie");
        }

        try {
            mVotes = jsonData.getInt(TMD_vote_count);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "No number of votes for movie");
        }
    }

    private String mOverview;

    public String getOverview() {
        if (mOverview == null || mOverview.equals("null")) {
            return "No overview available";
        }
        return mOverview;
    }

    private double mVoteAverage;

    public double getVoteAverage() {
        return mVoteAverage;
    }

    private String mPoster;

    /**
     * Give the relative path to the poster for this movie
     *
     * @return the relative path to the poster
     */
    public String getPosterURL() {
        return mPoster;
    }

    private String mTitle;

    public String getTitle() {
        return mTitle;
    }

    private String mReleaseDate;

    public String getReleaseDate() {
        if (mReleaseDate == null){
            return "unknown";
        }
        return mReleaseDate;
    }

    private int mVotes;

    public int getVotes() {
        return mVotes;
    }


    /*  So, to pass this entire class to an Activity to display the details,
        it looks like I should make it a Parcelable (based on
        http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
        ).  So, here's all the methods needed to implement a Parcelable.
        (And I thought Comparable was a pain to pronounce.)
     */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mPoster);
        out.writeString(mTitle);
        out.writeString(mReleaseDate);
        out.writeString(mOverview);
        out.writeInt(mVotes);
        out.writeDouble(mVoteAverage);
    }

    public static final Parcelable.Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    private MovieData(Parcel in) {
        mPoster = in.readString();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mVotes = in.readInt();
        mVoteAverage = in.readDouble();
    }

}