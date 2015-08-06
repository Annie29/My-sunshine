package com.example.android.project1movies.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by laurie on 8/1/2015.
 */
//public class MovieDataAdapter extends ArrayAdapter<String> {
public class MovieDataAdapter extends ArrayAdapter<MovieData> {
    Context mContext;
    List<MovieData> mData;

    public MovieDataAdapter(Context context, int resource, int textViewResourceId, List<MovieData> objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        //  TODO: Handle cases with no posters in this size.
        String url = mContext.getString(R.string.API_URL_image_base)
                + mContext.getString(R.string.API_image_size_preferred)
                + mData.get(position).getPosterURL();
        Picasso.with(mContext)
                .load(url)
                .into(imageView);

        return imageView;

    }

}
