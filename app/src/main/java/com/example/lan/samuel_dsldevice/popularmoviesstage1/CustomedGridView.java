package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by owner on 11/09/2015.
 */
public class CustomedGridView extends GridView{

    private int numOfCols; //user preferred number of columns

    //private int selectedPosition;

    private MovieAdapter movieAdapter; //Adapter containing the data for this gridView instance

    private MoviesGridFragment movieFragment; //Reference of the container fragment for later callbacks

    public CustomedGridView(Context context){
        super(context);
        //inflate(getContext(), R.layout.card, this);
    }
    //The 2 following constructors are necessary for referencing this customed gridView implementation from the xml layout
    public CustomedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CustomedGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    /*
    This method will load the preferred number of columns from the shared preferences
    the method is called whenever the size of this gridView changes.
     */
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        if(w!=0 && h!=0)
        { //Only if both the height and width are non-zero then we perform some work

            //Retrieve the grid width and height from the preferences (initialize them if null)
            int gwPx = MovieUtility.getIntSharedPreference(getContext(),
                     ((Activity) getContext()).getResources().getString(R.string.gridView_width_key), 0);
            if(gwPx==0)
            { //We need to initialize the grid width and height in the preferences
                SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(getContext());
                //SharedPreferences sharedPref = ((Activity)getContext()).getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putInt(((Activity)getContext()).getString(R.string.gridView_width_key), w);
                editor.putInt(((Activity)getContext()).getString(R.string.gridView_height_key), h);
                editor.apply();
            }

            int maxNumCols = 0;
            if(gwPx != w) {
                //Calculate and Set the max number of columns for the current orientation (if the size preferences changed)
                //w / colMinWidth
                int colMinWidthDp = (int) getResources().getDimension(R.dimen.min_img_size);
                int coMinWidthPx = (int)MovieUtility.convertDpToPixel(colMinWidthDp, getContext());

                maxNumCols = w/coMinWidthPx;

                SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(getContext());
                //SharedPreferences sharedPref = ((Activity)getContext()).getPreferences(Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = sharedPref.edit();
                SharedPreferences.Editor editor = sf.edit();
                editor.putInt(((Activity)getContext()).getString(R.string.max_num_cols_key), maxNumCols);
                editor.putInt(((Activity)getContext()).getString(R.string.gridView_width_key), w);
                editor.putInt(((Activity)getContext()).getString(R.string.gridView_height_key), h);
                editor.apply();
                editor.apply(); //faster than commit

            }

            //Load the current number of columns from the shared preferences (or if not set yet, deduct the default from above and set it)
            int nCols = Integer.parseInt(MovieUtility.getStringSharedPreference(getContext(),
                     ((Activity) getContext()).getResources().getString(R.string.num_cols_key),""+0));

            if(nCols == 0) {

                numOfCols = maxNumCols / 2;
                if(numOfCols<2)
                    numOfCols = 3; //3 columns might be a lot for the portrait mode of many devices.
                                   // But it's meant to make the user access the settings to modify the number of column
                setNumColumns(numOfCols); //the number of columns is set here only fo the very first time
            }

            //Complete the setup
            completeSetup(w,h);

            movieFragment.resetAdapterIfNeeded(); //First make sure the adapter is cleared before attempting to populate it.
                                                  // If not the adapter will have duplicate content when onSizeChanged is often called directly
                                                  // like when the keyboard pops up in portrait mode and causes a shrink of the gridView Height.

            if(movieFragment.tasksRequiredForAdapterPopulation == 2) //At the beginning, we should signal the call to the populateAdapter method
                // is from this onSizeChanged Method
                movieFragment.callFromOnsizeChangeOrSetAttributesMethod = true;

            movieFragment.populateAdapter(); //attempt to populate the adapter since we might be the last task awaited to populate the adapter
        }


    }

    //Helper method that will get called by the onSizedChanged method to complete the gridView instance initialization
    //And pass useful variables to the adapter
    private void completeSetup(int w, int h){

        //Passed the gridView width and height to the adapter
        movieAdapter.updateGridDimensions(w,h);

        //Finalize the updating of this gridView columns
        updateNumColumns();
    }

    //this method sets the adapter instance and init the gridView instance with right settings
    public void setAdapterInstance(MovieAdapter movieAdapter){

        this.movieAdapter = movieAdapter;

    }

    public void updateNumColumns(){

        //First load the latest user-selected number of columns saved in the preferences
        int cCols = Integer.parseInt(MovieUtility.getStringSharedPreference(getContext(),
                ((Activity) getContext()).getResources().getString(R.string.num_cols_key),""+0));

        //Read The max number of cols that was computed and
        // saved to the preferences in the onsizedCnahged method just previously
        int maxCols = MovieUtility.getIntSharedPreference(getContext(),
                ((Activity) getContext()).getResources().getString(R.string.max_num_cols_key), 0);

        if(cCols > maxCols){ //We need to set the number of columns to the current max number
            setNumColumns(maxCols);
            numOfCols = maxCols;
        }else if(cCols != numOfCols && cCols != 0) {
            setNumColumns(cCols);
            numOfCols = cCols;
        }
        else if(cCols == 0){ //the very first time
            setNumColumns(numOfCols);
        }

        //Passed the set number of columns to the adapter instance
        movieAdapter.updateGridColumns(numOfCols);

    }

    public void setMovieFragment(MoviesGridFragment movieFragment){

        this.movieFragment = movieFragment;
    }

    public int getColumnsNumber(){

        return numOfCols;
    }
}
