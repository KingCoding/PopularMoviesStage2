
package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by owner on 10/09/2015.
 */
public class MovieAdapter extends ArrayAdapter<String> {

    int gridWidhPx; //The width of the associated gridView instance
    int gridHeighPx; //The height of the associated gridView instance
    int numOfCols;  //The number of columns of the associated gridView instance
    View grid; //The reference of the gridView instance
    Thread loader = null;
    Boolean thumbRatioComputed = false;
    private final Object lock = new Object();
    int count;

    public float thumbRatio = -1.0f; //Height/weight Ratio that will be used for displaying the movies thumbnails

    public MovieAdapter(Context context, int resource){
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View gridItemView = convertView;

        if(convertView == null){
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            gridItemView = mInflater.inflate(R.layout.grid_item, null);
        }

        //This keeps the adapter from being queried when it has no data for the specified position
        int c = getCount();
        if(c == 0 || c<= position)
            return gridItemView;

        final String currUrl = getItem(position); //might cause indexOutOfBoundsEeption; should be placed in a try catch block

        //Thread loader = null;
        /**/
        //We want to keep the same ratio for the downloaded pictures
        //But we want to adjust the width so that the maximum amount of
        //thumbnails are displayed on the screen
        //For instance with a greater screen, more thumbnails should be displayed

        if(thumbRatio == -1) {
            //Bitmap image = null;
            //ImageView imageView = new ImageView(getContext());
            //imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //Picasso.with(getContext()).load(currUrl).into(imageView);
            //final Bitmap bitmap = null;
            loader = new Thread(new Runnable(){
                    public void run(){
                         synchronized (loader) {
                            try {
                                Bitmap bitmap = Picasso.with(getContext().getApplicationContext()).load(currUrl).get();
                                int wPx = bitmap.getWidth();
                                int hPx = bitmap.getHeight();
                                thumbRatio = (float) hPx / (float) wPx;
                                ((CustomedGridView) grid).movieFragment.imageRatio = thumbRatio;
                                thumbRatioComputed = true;
                                loader.notify();
                            } catch (IOException e) {
                                e.printStackTrace();
                                //notify();
                            }finally {

                            }
                        }
               }
            });

            loader.start();

            //thumbRatio = (float) hPx / (float) wPx;

            //Wait until the thumbRatio is computed
            synchronized (loader){
                while(!thumbRatioComputed) {
                    try {
                        loader.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

/*

        if(count != -1)
        {
            count++;
        }



        if(count > numOfCols){

            Handler mainHandler = new Handler(getContext().getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    int c = 10;
                   while(c > 0) {
                       CustomedGridView gridView = ((CustomedGridView) grid);
                       int columns = 0;
                       int children = gridView.getChildCount();
                       if (children > 0) {
                           int width = gridView.getChildAt(0).getMeasuredWidth();
                           if (width > 0) {
                               columns = gridView.getWidth() / width;
                           }
                       }

                       if (columns != numOfCols)
                           ((CustomedGridView) grid).setNumColumns(numOfCols);
                       else
                           break;

                       c--;

                       //Thread.sleep(100);
                   }
                    //((CustomedGridView)grid).forceLayout();
                } // This is your code
            };
            mainHandler.post(myRunnable);

            count = -1;
        }

*/
       // else {


            //Adjust the height to keep aspect ratio
            //float minPxHeigth = minPxWidth * thumbRatio;

        /*
        //Load the number of columns from the preferenes
        String colPrefKey = getContext().getString(R.string.num_cols_key); //load the pref based on its key
        int defaultCols = MovieUtility.getIntSharedPreference(getContext(), getContext().getString(R.string.def_num_cols_key), 2); //load the default cols from the resources
        int numCols = Integer.parseInt(MovieUtility.getStringSharedPreference(getContext(), colPrefKey, "" + defaultCols));
        */
            //float borderTotalWidth = 2*MovieUtility.convertDpToPixel( 4,getContext());
        //}

        //Deduct the width and height of the thumbnail from the width of the gidView, its number of columns and the value of the thumbnail ratio; 1.5f
        float currWPx = ((float)gridWidhPx / (float)numOfCols);

        float currHPx = thumbRatio * currWPx;

        //Set the background of the image depending on whether it's a favorite or non favorite movie
        boolean isBackgroundFavorite = false;

        if(!(((CustomedGridView)grid).movieFragment.displayingFavorite))
           if(((CustomedGridView)grid).movieFragment.isFavoriteMovie(position))
               isBackgroundFavorite = true;

        boolean isSelected = false;
            if(((CustomedGridView)grid).movieFragment.isMovieSelected(position))
                isSelected = true;

        setBackgroundColor(isSelected, isBackgroundFavorite, (ImageView) gridItemView);

        //Inflate the imageView of the thumbnail with picasso; using its url.
        Picasso.with(getContext()).load(currUrl).resize((int) Math.floor(currWPx), (int) Math.floor(currHPx)).into((ImageView) gridItemView.findViewById(R.id.grid_item_imageView));

        return gridItemView;
    }


    public void setBackgroundColor(boolean isSelected, boolean isBackgroundFavorite, ImageView imageView){

        if(isSelected && ((CustomedGridView)grid).movieFragment.isTablet ){
            imageView.setBackgroundColor(Color.parseColor(MovieUtility.getGRID_SELECTED_ITEM_BACKGROUND_COLOR_VALUE(getContext())));
        }
        else {
            if (isBackgroundFavorite)
                imageView.setBackgroundColor(Color.parseColor(MovieUtility.getGRID_FAVORITE_ITEM_BACKGROUND_COLOR_VALUE(getContext())));
            else
                imageView.setBackgroundColor(Color.parseColor(MovieUtility.getGRID_ITEM_BACKGROUND_COLOR_VALUE(getContext())));
        }

    }

    public void updateGridDimensions(int gridWidthPx, int gridHeightPx){

        this.gridWidhPx = gridWidthPx;
        this.gridHeighPx = gridHeightPx;
    }

    public void updateGridColumns(int cols){
        this.numOfCols = cols;
    }

    public void setView(CustomedGridView custGd) {
        grid = custGd;
    }
}
