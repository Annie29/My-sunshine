package com.example.android.sunshine.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by laurie on 8/1/2015.
 */
//public class MyAdapter extends ArrayAdapter<String> {
public class MyAdapter extends ArrayAdapter<MovieData> {
    Context mContext;
    List<MovieData> mData;
    public MyAdapter(Context context, int resource, int textViewResourceId, List<MovieData> objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Image Adapter!!!!", " Entered getView");
        ImageView imageView;
        if (convertView == null) {
           imageView = new ImageView(mContext);
        }
        else {
            imageView = (ImageView) convertView;
        }

        /*String url = mContext.getString(R.string.API_URL_image_base)
                + mContext.getString(R.string.API_image_size_preferred)
                + mData.get(position).getPosterURL();
        Log.d("Image Adapter ", "!!! URL is " + url);
        Picasso.with(mContext)
                .load(url)
                .into(imageView);
        */


//  TODO: Go to Picasso
//        imageView.setImageResource(mThumbIds[position % 2]);
        //return imageView;

        String url = mContext.getString(R.string.API_URL_image_base)
                + mContext.getString(R.string.API_image_size_preferred)
                + mData.get(position).getPosterURL();
        Log.d("Image Adapter ", "!!! URL is " + url);
        Picasso.with(mContext)
                .load(url)
                .into(imageView);

      //  TextView textView = (TextView) parent.findViewById(R.id.grid_item_title);

//        if (position % 2 == 0)
      /*  Picasso.with(mContext)
                .load("https://en.wikipedia.org/wiki/File:Dutch_Harbor_Naval_Op._Base.jpg")
                .placeholder(R.drawable.abc_ratingbar_full_material)
                .error(R.drawable.abc_ratingbar_full_material)
                .into(imageView);*/

  //      Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
   //     else
     //       imageView.setImageResource(mThumbIds[position % 2]);
        /*
            Picasso.with(mContext)
                    .load("https://en.wikipedia.org/wiki/File:USA_Alaska_location_map.svg")
                    .into(imageView);
*/
        return imageView;

    }

    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3};
}
