
package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by owner on 10/09/2015.
 */
public class MovieAdapter extends ArrayAdapter<String> {

    int gridWidhPx; //The width of the associated gridView instance
    int gridHeighPx; //The height of the associated gridView instance
    int numOfCols;  //The number of columns of the associated gridView instance
    View grid; //The reference of the gridView instance

    public float thumbRatio = 1.5f; //Height/weight Ratio that will be used for displaying the movies thumbnails

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

        String currUrl = getItem(position);

        /**/
        //We want to keep the same ratio for the downloaded pictures
        //But we want to adjust the width so that the maximum amount of
        //thumbnails are displayed on the screen
        //For instance with a greater screen, more thumbnails should be displayed

        if(thumbRatio == -1) {
            //Bitmap image = null;
 /*
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                Picasso.with(getContext()).load(currUrl).into(imageView);

            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            int wPx = bitmap.getWidth();
            int hPx = bitmap.getHeight();

            thumbRatio = (float) hPx / (float) wPx;


*/

        }
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

        //Deduct the width and height of the thumbnail from the width of the gidView, its number of columns and the value of the thumbnail ration; 1.5f
        float currWPx = ((float)gridWidhPx / (float)numOfCols);

        float currHPx = thumbRatio * currWPx;

        //Inflate the imageView of the thumbnail with picasso; using its url.
        Picasso.with(getContext()).load(currUrl).resize((int) Math.floor(currWPx), (int) Math.floor(currHPx)).into((ImageView) gridItemView.findViewById(R.id.grid_item_imageView));

        return gridItemView;
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
