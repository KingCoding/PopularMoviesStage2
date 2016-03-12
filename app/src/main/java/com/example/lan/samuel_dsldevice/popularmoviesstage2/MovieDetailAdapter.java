package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by owner on 11/12/2015.
 */
public class MovieDetailAdapter  extends ArrayAdapter<String> {

    public int adapterType;

    public static int trailerCode  = 1;

    public static int reviewCode = 2;

    private Drawable drawable;

    private MovieListeners.MovieDetailTrailerReviewsOnItemClickListener movieDetailTrailerReviewsOnItemClickListener;

    private AdapterView adapterView;

    private Object itemsMetaData; //complementary data for populating the items

    private View clickedView;

    private View.OnClickListener listViewClickDispatcher = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            int currPosition = (Integer)v.getTag();

            //We need to get the tag of the scrollView from its child since its own tag often gets corrupted
            if(v instanceof HorizontalScrollView)
                currPosition = (Integer)((HorizontalScrollView) v).getChildAt(0).getTag();

            movieDetailTrailerReviewsOnItemClickListener.
                    onItemClick(adapterView, clickedView, currPosition, 0);
        }
    };

    public MovieDetailAdapter(Context context, int resource) {
        super(context, resource);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View listViewItem = convertView;
        //final View itemView = listViewItem;
        if(convertView == null){
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if(adapterType == trailerCode)
                listViewItem = mInflater.inflate(R.layout.trailer_item_layout, null);
            else if(adapterType == reviewCode)
                listViewItem = mInflater.inflate(R.layout.review_item_layout, null);

            listViewItem.setTag(position);
            //final View itemView = listViewItem;
            clickedView = listViewItem; //This is not needed to be non null. But we do assign it for convenience sake.
            //listViewItem.setOnClickListener( listViewClickDispatcher);
            //listViewItem.setOnTouchListener(new MovieTouchListener());

            if(adapterType == trailerCode)
            {
                (listViewItem.findViewById(R.id.trailer_title_textView)).setOnClickListener(listViewClickDispatcher);
                (listViewItem.findViewById(R.id.trailer_size_textView)).setOnTouchListener(new MovieTouchListener());
                //(listViewItem.findViewById(R.id.trailer_title_horizontal_scrollView)).setTag(position);
                //(listViewItem.findViewById(R.id.trailer_title_horizontal_scrollView)).setTag(position);
                (listViewItem.findViewById(R.id.trailer_title_horizontal_scrollView)).setOnTouchListener(new MovieTouchListener());
                //(listViewItem.findViewById(R.id.trailer_size_wrapper)).setOnTouchListener(new MovieTouchListener());
            }
            else if(adapterType == reviewCode)
            {
                (listViewItem.findViewById(R.id.review_author_textView)).setOnClickListener(listViewClickDispatcher);
                (listViewItem.findViewById(R.id.review_size_textView)).setOnTouchListener(new MovieTouchListener());
                (listViewItem.findViewById(R.id.review_title_horizontal_scrollView)).setOnTouchListener(new MovieTouchListener());
                //(listViewItem.findViewById(R.id.review_size_wrapper)).setOnTouchListener(new MovieTouchListener());
            }

        }
        //else{
        // We need to set the right tag to the current position when the view has been recycled
            listViewItem.setTag(position);
            ViewGroup itemLayout = ((ViewGroup)listViewItem);
            for(int i=0; i< itemLayout.getChildCount(); i++)
            {

                View currChild = itemLayout.getChildAt(i);
                currChild.setTag(position);
                if(currChild instanceof HorizontalScrollView ) {

                    ((HorizontalScrollView) currChild).getChildAt(0).setTag(position);

                    ((HorizontalScrollView) currChild).scrollTo(0,0);
                }

                if(currChild instanceof LinearLayout) {

                    ((LinearLayout) currChild).getChildAt(0).setTag(position);

                }
                /*
                if(v.getId() == R.id.trailer_title_horizontal_scrollView)
                {
                    v.setTag(position);
                    break;
                }
                else if(v.getId() == R.id.review_title_horizontal_scrollView){

                }
                */

            }
            //(listViewItem.findViewById(R.id.trailer_title_horizontal_scrollView)).setTag(position);
            //(listViewItem.findViewById(R.id.trailer_title_horizontal_scrollView)).setTag(position);
        //}

        String currItem = getItem(position);
        //String currItemMetaData = null;

        if(adapterType == reviewCode){
            ((TextView)(listViewItem.findViewById(R.id.review_author_textView))).setText(currItem);
            ((TextView)(listViewItem.findViewById(R.id.review_size_textView))).setText("size: "+((LinkedList)itemsMetaData).get(position));

        }
        else{
            ((TextView)(listViewItem.findViewById(R.id.trailer_title_textView))).setText(currItem);
            ((TextView)(listViewItem.findViewById(R.id.trailer_size_textView))).setText("size: "+((LinkedList)itemsMetaData).get(position));

        }

        /*
        if(adapterType == trailerCode){
            //Populate the gridItemView with the icon; using the Picasso library


        }
        */

        //Button currButtonItem = ((Button)listViewItem);
        //Set the text for the current item
        //currButtonItem.setText(currItem);


        //Set the background for the current item
        if(position %2 == 0)
        { //Set background to the light color

          listViewItem.setBackgroundColor(Color.parseColor(MovieUtility.getEVEN_LIST_ITEM_COLOR_VALUE(getContext())));
            if(adapterType == reviewCode){
                ((TextView)(listViewItem.findViewById(R.id.review_author_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getEVEN_LIST_ITEM_COLOR_VALUE(getContext())));
                ((TextView)(listViewItem.findViewById(R.id.review_size_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getEVEN_LIST_ITEM_COLOR_VALUE(getContext())));
            }
            else{
                ((TextView)(listViewItem.findViewById(R.id.trailer_title_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getEVEN_LIST_ITEM_COLOR_VALUE(getContext())));
                ((TextView)(listViewItem.findViewById(R.id.trailer_size_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getEVEN_LIST_ITEM_COLOR_VALUE(getContext())));
            }
        }
        else
        { //Set the background to the alternate darker color

          listViewItem.setBackgroundColor(Color.parseColor(MovieUtility.getODD_LIST_ITEM_COLOR_VALUE(getContext())));

            if(adapterType == reviewCode){
                ((TextView)(listViewItem.findViewById(R.id.review_author_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getODD_LIST_ITEM_COLOR_VALUE(getContext())));
                ((TextView)(listViewItem.findViewById(R.id.review_size_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getODD_LIST_ITEM_COLOR_VALUE(getContext())));
            }
            else{
                ((TextView)(listViewItem.findViewById(R.id.trailer_title_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getODD_LIST_ITEM_COLOR_VALUE(getContext())));
                ((TextView)(listViewItem.findViewById(R.id.trailer_size_textView))).
                        setBackgroundColor(Color.parseColor(MovieUtility.getODD_LIST_ITEM_COLOR_VALUE(getContext())));
            }
        }

        //Drawable currentDrawable = (adapterType==reviewCode)? drawables.get(reviewCode) : drawables.get(trailerCode);
        //currButtonItem.setCompoundDrawables(drawable, null, null, null);

        return listViewItem;
    }


    public void setAdapterType(int code){

        adapterType = code;
    }

    public void setDrawable(Drawable drawable){

        this.drawable = drawable;
    }

    public void setOnclickListener(MovieListeners.MovieDetailTrailerReviewsOnItemClickListener movieDetailTrailerReviewsOnItemClickListener){

        this.movieDetailTrailerReviewsOnItemClickListener = movieDetailTrailerReviewsOnItemClickListener;
        movieDetailTrailerReviewsOnItemClickListener.setMovieDetailAdapter(this);
    }

    public void setAdapterView(AdapterView adapterView){

        this.adapterView = adapterView;
    }

    public void setItemMetaData(Object itemsMetadata){

        this.itemsMetaData = itemsMetadata;
    }


    public class MovieTouchListener implements View.OnTouchListener {

        private boolean isActionDown = false;
        /*
        private HorizontalScrollView touchedView;

        public HSTouchListener(HorizontalScrollView touchedView){

            this.touchedView = touchedView;
        }
        */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Do stuff
                if(isActionDown)
                        listViewClickDispatcher.onClick(v);
            }
            else if(event.getAction() == MotionEvent.ACTION_DOWN){
                isActionDown = true;
            }
            else{
                isActionDown = false;
            }

            return false;
        }

    }

}
