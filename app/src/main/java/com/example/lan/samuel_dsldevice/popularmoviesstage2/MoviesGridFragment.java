package com.example.lan.samuel_dsldevice.popularmoviesstage2;

/**
 * Created by owner on 11/09/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lan.samuel_dsldevice.popularmoviesstage2.favoriteData.FavoriteContract;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesGridFragment extends Fragment implements MovieTask.MovieTaskCallBackInstance, LoaderManager.LoaderCallbacks<Cursor>{



    private static final int FAVORITE_LOADER = 0;

    private Cursor favoriteCursor;

    boolean displayingFavorite; //Must be initialized in onCreateView Base on the latest value of the favorite sorting preference

    private LinkedList<String> favoritePosterPaths;

    LinkedList<String> favoriteIds;

    //The states of the following members will be retained between screen configurations.
    CustomedGridView custGd;
    //MovieTask movieAsyncTask;
    MovieAdapter movieAdapter;

    private URL url;

    private LinkedList<String> absolutePosterPaths;

    LinkedList<String> movieIds;

    private HashMap<String, String> urlParameters;

    HashMap<Integer, Integer> firstVisiblePositions;

    int currPage;

    int firstPos;

    String currSelectionId;

    int currSelection = -1; // indicates the movie currently selected

    int currSelectionPage = 1;

    int tempFirstPos;

    Button buttonPrev;
    Button buttonNext;

    EditText pageText;

    String currentPageEnterred;

    private Integer tasksCompletedForAdapterPopulation;

    Object objectForSync;

    boolean callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod; //Variable that indicates if the populateAdapter method is called from
    // the onsizeChanged method or the setAttributes method

    private MovieListeners.MovieEditTextOnFocusChangeListener onFocusChangeListener;

    private MovieListeners.MovieEditorActionListener editorActionListener;

    private MovieListeners.MovieButtonsOnClickListener onClickListener;

    private MovieListeners.MoviesGridOnItemClickListener onItemClickListener;

    private MovieListeners.MovieOnScrollListener onScrollListener;

    Activity activity;

    public double imageRatio = 1.5f;

    //private boolean instanceStateRetained;

    private boolean skipAllUpdateMovie; // boolean to indicate if the updateMovies() method should skip all its operations
    // (when the fragment have been completely restored before the updateMovies() method)

    boolean scrollListenerEnabled = false;

    Display display;
    int originalRotation = -1;

    public int containerID;

    public boolean replaced;

    public boolean isTablet;

    public MoviesGridFragment() {

        //The following line is set in order to make the system retain the state of this Fragment
        //and its variables on configuration change
        //It also keeps the fragment instance from being killed during asynctask operations
        setRetainInstance(true);
        skipAllUpdateMovie = false;

        //tasksCompletedForAdapterPopulation = 0;
        //tasksRequiredForAdapterPopulation = 2; // The first time the adapter is populated, two tasks need to be completed:
        // The download of data from the server (done by the AsyncTasks) or the loading of data from the provider
        // The call of the onsizeChanged method of the CustomedGridView instance
        // Then afterward, only the one task will need to be completed (the call to onsizeChanged on con
        // figuration changes or the call to onStart)
        //callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod = false; //

        objectForSync = new Object();

        firstVisiblePositions = new HashMap<Integer, Integer>();

        firstPos = tempFirstPos = 0;

        //Initialize the originalOrietation with a default invalid value
        originalRotation = -1;

        if(currentPageEnterred == null) //We don't want to override a previous valid value
            currentPageEnterred = "1";

        //Initialize the listeners
        //onFocusChangeListener = new MovieListeners.MovieEditTextOnFocusChangeListener(this);
        editorActionListener = new MovieListeners.MovieEditorActionListener(this);
        onClickListener = new MovieListeners.MovieButtonsOnClickListener(this);
        onItemClickListener = new MovieListeners.MoviesGridOnItemClickListener(this);
        onScrollListener = new MovieListeners.MovieOnScrollListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //if(!PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(getString(R.string.pref_sort_key)))
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.menu_movie_fragment, menu);
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem menuItem){

        int id = menuItem.getItemId();
        if(id == R.id.movie_settings){
            //instanceStateRetained = false;
            startActivity(new Intent(activity , MovieSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pm2_main, container, false);//the fragment might already be attached to the container.
        // Setting the attachToRoot variable to true might make the program fail.
        ViewGroup layout = (ViewGroup) rootView.findViewById(R.id.gridFrame);

        activity = getActivity();

        //tasksCompletedForAdapterPopulation = 0;
        //tasksRequiredForAdapterPopulation = 2; // The first time the adapter is populated, two tasks need to be completed:
        // The download of data from the server (done by the AsyncTasks) or the loading of data from the provider
        // and The call of the onsizeChanged method of the CustomedGridView instance
        // Then afterward, only the one task will need to be completed (the call to onsizeChanged on con
        // figuration changes or the call to onStart)
        callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod = false; //

        //firstVisiblePositions = new HashMap<Integer, Integer>();

        //firstPos = tempFirstPos = 0;

        //Initialize the originalOrietation with a default invalid value
        //originalRotation = -1;

        if(currentPageEnterred == null)
            currentPageEnterred = "1";

        if(urlParameters == null)
        {
            //Create the url parameters hashMap
            urlParameters = new HashMap<String, String>();

            currSelectionPage = currPage = (int) getResources().getInteger(R.integer.default_page);

        }

        //Initialize the display mode
        displayingFavorite = shouldDisplayFromFavorite();

        initViews(rootView);



        return rootView;
    }

    //This is called only if the views were destroyed or their context was changed
    private void initViews(View rootView){

        //Initialize the display
        display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int oldOriginalRotation = originalRotation;
        if(originalRotation == -1)
            originalRotation = display.getRotation();

        /*
        //We need to adjust the variables indicating if the orientation change took place or not
        if(oldOriginalRotation != -1)
        { //We are somewhere at a later stage in the session of the application/activity
            //We need to make sure the state of the variables correspond to the state of the orientation
            //Since some change of orientation might have happened when the Activity was not in the foreground

            if(oldOriginalRotation != originalRotation)
            { //Here there has been an orientation change whether it happened when the Activity
                //was in foreground or not or whether it happened out of the application or not, we treat it as a regular
                //orientation change: as if it happened when the Activity was in foreground.
                tasksCompletedForAdapterPopulation = -1;
                tasksRequiredForAdapterPopulation = 1;
            }
            else
            {//There was no orientation change. Everything should work fine with regular values regardless
                tasksCompletedForAdapterPopulation = 0;
                tasksRequiredForAdapterPopulation = 1;
            }

        }
        else{//We are at the very beginning of the session; we should do nothing; all the variables are correctly initialized

        }
        */

        ViewGroup layout = (ViewGroup) rootView.findViewById(R.id.gridFrame);
        custGd = (CustomedGridView)layout.getChildAt(0); // the only child is the gridView instance

        //Set the state of the size capture to false.
        custGd.sizeCaptured = false;

        custGd.setMovieFragment(this);

        //Set the onclickListener since it needs to be done just once
        custGd.setOnItemClickListener(onItemClickListener);
        scrollListenerEnabled = false; //this must be set before the onScrollListener is set on the custumedGridView

        //The onScrollListener will be set somewhere after in the populateAdapter method

        //Build adapter
        //if(movieAdapter == null)
            movieAdapter = new MovieAdapter(activity, R.layout.grid_item);

        custGd.setAdapterInstance(movieAdapter);

        //set adapter on the gridView instance
        custGd.setAdapter(movieAdapter);

        movieAdapter.setView(custGd);

        //Set the Navigation bar
        ViewGroup navLayout = (ViewGroup) rootView.findViewById(R.id.navBarLayout);
        buttonPrev = (Button) navLayout.findViewById(R.id.button_prev);
        buttonNext = (Button) navLayout.findViewById(R.id.button_next);

        pageText = (EditText) navLayout.findViewById(R.id.textView_set_page);
        //pageText.setOnFocusChangeListener(onFocusChangeListener);
        pageText.setOnEditorActionListener(editorActionListener);
        //pageText.setFocusable(true);
        //pageText.setSingleLine();

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        buttonNext.setOnClickListener(onClickListener);
        buttonPrev.setOnClickListener(onClickListener);
    }

    @Override
    public void onStart(){

        super.onStart();
        updateMovies();
    }

    //This method will probably be optimized later
    void updateMovies(){

        try{

            resetAdapterIfNeeded(); //movieAdapter.clear();

            //First detect if an orientation change wasn't captured
            display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int oldOriginalRotation = originalRotation;
            originalRotation = display.getRotation();

            //We need to adjust the variables indicating if the orientation change took place or not
            //if(oldOriginalRotation != -1)
            //{ //We are somewhere at a later stage in the session of the application/activity
                //We need to make sure the state of the variables correspond to the state of the orientation
                //Since some change of orientation might have happened when the Activity was not in the foreground

                if(oldOriginalRotation == originalRotation /* || ! replaced */)
                { //Here there has been an orientation change whether it happened when the Activity
                    //was in foreground or not or whether it happened out of the application or not, we treat it as a regular
                    //orientation change: as if it happened when the Activity was in foreground.
                    //tasksCompletedForAdapterPopulation = -1;
                    //tasksRequiredForAdapterPopulation = 1;
                    //custGd.sizeCaptured = false;
                    custGd.updateNumColumns(-1,-1); //We must initiate the update of the number of columns from here

                }
                else
                {//There was no orientation change. Everything should work fine with regular values regardless
                    //tasksCompletedForAdapterPopulation = 0;
                    //tasksRequiredForAdapterPopulation = 1;
                    custGd.sizeCaptured = false;
                    custGd.triggerFragmentReset = true;
                }

            //custGd.updateNumColumns(-1,-1);
            //}

            //following line is suspect

            // because the size of the gridView doesn't change and its onSizeChanged method won't be called
            //custGd.setLayoutParams(new FrameLayout.LayoutParams(0,0));

            //custGd.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            //custGd.requestLayout();

            if(custGd != null){

                //Update the display mode to its latest value
                displayingFavorite = shouldDisplayFromFavorite();

                //if the url parameters changed or url is null, we build a new one and query the server
                if (!displayingFavorite && needsToBuildUrl()) {

                    //Set the variable indicating that we are displaying from favorites to false
                    displayingFavorite = false;

                    scrollListenerEnabled = false;
                    onScrollListener.userTouched = false;
                    //custGd.setOnScrollListener(null);

                    //Query network data with asynctask
                    String sortMode = MovieUtility.getStringSharedPreference(activity/*.getApplicationContext()*/, getString(R.string.pref_sort_key),
                            urlParameters.get(MovieUtility.getSORT_KEY_NAME(activity)));
                    urlParameters.put(MovieUtility.getSORT_KEY_NAME(activity), sortMode);
                    urlParameters.put(MovieUtility.getAPI_KEY_NAME(activity), MovieUtility.getAPI_KEY(activity));
                    urlParameters.put(activity.getString(R.string.page_key), "" + currPage);

                    //record the initial firstPosition for this page in the appropriate Map if required
                    if(!firstVisiblePositions.containsKey(currPage)){
                        firstVisiblePositions.put(currPage,0); //The initial first position is 0
                    }
                    else{
                        tempFirstPos = firstVisiblePositions.get(currPage);
                    }

                    MovieTask movieAsyncTask = new MovieTask(this);

                    url = MovieTask.buildURL(MovieUtility.getBASE_URL(activity), MovieUtility.getDISCOVER_MOVIE_ENDPOINT(activity), urlParameters);

                    //scrollListenerEnabled = false; //updating this variable at this point surprisingly has no effect; that's yet to be understood.
                    movieAsyncTask.execute(url);
                    //scrollListenerEnabled = false; //updating this variable at this point surprisingly has no effect (it would have been ideal); that's yet to be understood.
                }
                else //if(tasksRequiredForAdapterPopulation != 2)// If we are not at the beginning
                {

                    //synchronized (tasksRequiredForAdapterPopulation) {

                    //if (tasksCompletedForAdapterPopulation != -1) //Only if it's not in configuration change should we populate the adapter.
                    // Otherwise that will be initiated by the onSizeChanged method of the CustomedGridView
                    populateAdapter(); //This will make the gridView populate itself.
                    //}
                }
                //else{
                //Nothing
                //}

                //Hide or show the navigation bar
                Boolean showNavBar = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.nav_key), true);
                View navBar = activity.findViewById(R.id.navBarLayout);

                if (showNavBar && !displayingFavorite) //We should not show the navigation bar when displaying favorites
                    navBar.setVisibility(View.VISIBLE);
                else
                    navBar.setVisibility(View.GONE);

                //Set the page number
                //ViewGroup navLayout = (ViewGroup)getActivity().findViewById(R.id.navBarLayout);
                //TextView pageText = (TextView)navLayout.findViewById(R.id.textView_set_page);
                pageText.setText("" + currPage);

                String[] sortNames = getResources().getStringArray(R.array.pref_sort_list_titles);
                if(!displayingFavorite) {
                    String prevSortCriteria = urlParameters.get(MovieUtility.getSORT_KEY_NAME(activity));
                    String currSortCriteria = MovieUtility.getStringSharedPreference(activity, getString(R.string.pref_sort_key), prevSortCriteria);

                    String[] sortValues = getResources().getStringArray(R.array.pref_sort_list_values);

                    for (int i = 0; i < sortValues.length; i++) {
                        if (sortValues[i].equals(currSortCriteria)) {
                            TextView sortOrderTextView = (TextView) activity.findViewById(R.id.sort_order_textView);
                            sortOrderTextView.setText(getResources().getString(R.string.sort_order_prepend) + " " +sortNames[i]);
                            sortOrderTextView.setBackgroundColor(Color.parseColor(MovieUtility.getGRID_ITEM_BACKGROUND_COLOR_VALUE(activity)));
                            break;
                        }
                    }
                }
                else{
                    TextView sortOrderTextView = (TextView)activity.findViewById(R.id.sort_order_textView);
                    sortOrderTextView.setText(getResources().getString(R.string.sort_order_prepend) + " " +sortNames[sortNames.length-1]);
                    sortOrderTextView.setBackgroundColor(Color.parseColor(MovieUtility.getGRID_FAVORITE_ITEM_BACKGROUND_COLOR_VALUE(activity)));
                }
            }

        }
        catch(Exception exp){

        }

    }


    private boolean shouldDisplayFromFavorite(){

        String sortMode = MovieUtility.getStringSharedPreference(activity/*.getApplicationContext()*/, getString(R.string.pref_sort_key),
                urlParameters.get(MovieUtility.getSORT_KEY_NAME(activity)));

        return ((sortMode != null) && sortMode.equals(MovieUtility.getSORT_FAVORITE(activity)));
    }

    private boolean needsToBuildUrl()
    {

        if(url == null ||
                absolutePosterPaths == null //needed when there are connection issues and the data can't be retrieved.
            // We need the application to try retrieving data again on the next call the onStart
                )
        {

            return true; //It's only at the beginning that the url can be null
            //Read the current url parameters and compare them to the previous one
            //In case of change, return true. Else return false
        }

        int prevPage = Integer.parseInt(urlParameters.get(activity.getString(R.string.page_key)));

        if(prevPage != currPage)
            return true;

        String prevSortCriteria = urlParameters.get(MovieUtility.getSORT_KEY_NAME(activity));

        String currSortCriteria = MovieUtility.getStringSharedPreference(activity, getString(R.string.pref_sort_key), prevSortCriteria);

        if(prevSortCriteria==null || !prevSortCriteria.equals(currSortCriteria)) {
            currPage = (int) getResources().getDimension(R.dimen.default_page);

            if(firstVisiblePositions != null)
                firstVisiblePositions.clear();
            else
                firstVisiblePositions = new HashMap<Integer, Integer>();

            return true;
        }

        return false;
    }


    @Override
    public String getMainListName() {

        return MovieUtility.getMAIN_DISCOVER_MOVIE_LIST_NAME(activity);
    }

    @Override
    public LinkedList<String> getAttributesNames() {

        LinkedList<String> result = new LinkedList<String>();

        result.add(MovieUtility.getMOVIE_ID_ATTRIBUTE(activity));
        result.add(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(activity));
        return result;
    }

    @Override
    public void setAttributes(HashMap<String, Object> attributesLists) {
        //This method is going to make the poster paths absolute and set them into the movie adapter

        if(attributesLists!=null) {
            //This implementation expects values of the hashMap to be of type LinkedList<String>
            LinkedList<String> posterPaths = (LinkedList<String>) attributesLists.get(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(activity));

            int currPosterSizeRange = 2;
            for (int i = 0; i < posterPaths.size(); i++)
                posterPaths.set(i, MovieUtility.getPOSTER_PATH_BASE_URL(activity) +
                        MovieUtility.getImageSizeKey(currPosterSizeRange, activity) + posterPaths.get(i));
            absolutePosterPaths = posterPaths;

            movieIds = (LinkedList<String>) attributesLists.get(MovieUtility.getMOVIE_ID_ATTRIBUTE(activity));

            //At this point, the network operation was successful. Before calling populateAdapter,
            //we should update the first position with the entry in the firstVisiblePositions hashMap.
            tempFirstPos = firstVisiblePositions.get(currPage);

            //The adapter might already contain some data at this point due to size change and re-population.
            //So we should clear the adapter to ensure it's in the right state
            resetAdapterIfNeeded();

            //if(tasksRequiredForAdapterPopulation == 2) //At the beginning, we should signal the call to the populateAdapter method
                // is from this setAttributes Method
            //    callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod = true;

            populateAdapter();
        }
        else{ //the task execution failed, we should restart it

            url = null; //This will make the updateMovies method to create a new url and query the server
            // instead of just repopulating the gridView with previously fetched data

            //The 2 following instructions are useful to avoid the bug
            //that makes the gridView display movies that are not present in its adapter
            movieAdapter.setNotifyOnChange(true);
            movieAdapter.notifyDataSetChanged();

            //Wait 10 seconds before retrying.
            Thread updater = new Thread(new Runnable(){
                public void run(){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateMovies();
                }
            });
            updater.start();

        }

    }

    /*
    This method ensures that the adapter is in its original state with no date in.
    it's called by the onSizeChanged method of the custom gridView just before it calls the populate adapter method below
    The purpose is to solve a bug that made the gridview to duplicate its content; when populateAdapter() was called without
    calling this method before, the adapter couldn't be cleared as it's first done every time in the updateMovies() method of this class.
    And as a consequence, the adapter was repopulating itself with the same data, which was causing the gridView instance to repeat
    All its data when scrolling pass its logical end.
     */
    public void resetAdapterIfNeeded(){

        movieAdapter.setNotifyOnChange(false);
        if(movieAdapter.getCount() > 0)
            movieAdapter.clear();

        //custGd.removeAllViews();
        //movieAdapter.setNotifyOnChange(true);
    }


    public void populateAdapter(){

        synchronized (objectForSync) {

        /*
        boolean orientationChanged = tasksCompletedForAdapterPopulation == -1; //tells if the orientation has changed

        if(
                (tasksRequiredForAdapterPopulation == 2 &&
                        callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod) //At the beginning (When tasksRequiredForAdapterPopulation == 2),
                                                                   // the valid calls to this method should be made only from the setAttributes or onLoadFinished methods of this class
                                                                   // or from the onsizeChanged method of the custom gridView
                ||
                tasksRequiredForAdapterPopulation == 1 //After start, every call to this method is valid
                ||
                tasksCompletedForAdapterPopulation == -1 //Used to signal the call is following an orientation change
                )
            tasksCompletedForAdapterPopulation++;


        if((tasksCompletedForAdapterPopulation == tasksRequiredForAdapterPopulation
                || orientationChanged)) { // tasksCompletedForAdapterPopulation == 0)) {
        */

            if(custGd.isSizeCaptured()){

                //custGd.requestLayout();
                //custGd.resetList();

                //custGd.setLayoutParams(new FrameLayout.LayoutParams(0,0));
                //custGd.setLayoutParams(new FrameLayout.LayoutParams(0,0));
                //Initialize the reference of the right poster paths
                LinkedList<String> currentAbsolutePosterPaths;

                if(displayingFavorite)
                    currentAbsolutePosterPaths = favoritePosterPaths;
                else
                    currentAbsolutePosterPaths = absolutePosterPaths;

                if(currentAbsolutePosterPaths != null) {
                    //If needed, We set the onScrollListener for the custom gridView instance here
                    //Because we want to make sure we always do it after onsizeChanged has been called on the
                    //GridView instance; so to avoid handling unnecessary events in the listener.
                    if (!scrollListenerEnabled) {//CustGd can't be null at this stage
                        custGd.setOnScrollListener(onScrollListener);
                    }

                    //Set the paths in the movie adapter
                    //We first set the adapter not to notify its view of any change when it's being populated,
                    //because it will be very computationally intensive
                    movieAdapter.setNotifyOnChange(false);

               /*
               //Initialize the reference of the right poster paths
               LinkedList<String> currentAbsolutePaths;
               if(displayingFavorite)
                   currentAbsolutePaths = favoritePosterPaths;
               else
                   currentAbsolutePaths = absolutePosterPaths;
               */
                    //Let's make sure the index of the currently selected movie is right
                    //This is necessary when we select a movie in the favorite and come back to the previous soring display
                    //MovieDetailFragment movieDetailFragment = (MovieDetailFragment)(((PM2_MainActivity)activity).movieDetailFragment);
                    //movieDetailFragment = (MovieDetailFragment)(((ActionBarActivity)activity).getSupportFragmentManager().findFragmentByTag(((PM2_MainActivity)activity).movieDetailFragmentTag));

                    if(isTablet) {
                        if (currSelectionId != null) {

                            if (displayingFavorite && favoriteIds != null)
                                currSelection = favoriteIds.indexOf(currSelectionId);
                            else if (movieIds != null)
                                currSelection = movieIds.indexOf(currSelectionId);

                            //if (currSelection != -1 && !displayingFavorite)
                                currSelectionPage = currPage;

                        } else {
                            onItemClickListener.onItemClick(null, null, 0, -1);
                        }

                    }

                    int numCols = custGd.numOfCols;
                    //custGd.setNumColumns(custGd.getColumnsNumber());

                    //custGd.setSelection(tempFirstPos);
                    for (String path : currentAbsolutePosterPaths) {
                        movieAdapter.add(path);

                    }

                    movieAdapter.notifyDataSetChanged();
                    //custGd.setAdapter(movieAdapter);
                    custGd.smoothScrollToPosition(tempFirstPos);
                    if(displayingFavorite)
                        custGd.setSelection(tempFirstPos);
                    else
                    custGd.setSelection(tempFirstPos); // This instruction implicitly sets the adapter notification state to true.
                                                       // No need to call movieAdapter.setNotifyOnChange(true); after this
                    firstPos = tempFirstPos;

                    //custGd.forceLayout();
                }
                else if(!displayingFavorite) //We restart the loading of data on the network only if we are not displaying local favorite
                { //We assume this is a connection issue and we try to retrieve the data again
                    setAttributes(null);
                }

                //Initialize the task controller variables to their regular values
                //tasksCompletedForAdapterPopulation  = 0;
                //tasksRequiredForAdapterPopulation = 1; //after the first populating of the adapter only one task should be completed before the adapter is populated

            }

            //callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod = false;
        }

    }

    private static final String[] FAVORITE_PROJECTION = {

            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,
            FavoriteContract.FavoriteEntry.COLUMN_IMAGE_PATH,
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_IMAGE_PATH = 1;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri favoriteUri = FavoriteContract.FavoriteEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                favoriteUri,
                FAVORITE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data!=null) {
            //Update the cursor reference to the new one
            favoriteCursor = data;

            int currPosterSizeRange = 2;
            String posterPathsPrepend = MovieUtility.getPOSTER_PATH_BASE_URL(activity) +
                    MovieUtility.getImageSizeKey(currPosterSizeRange, activity);
            //fill the data structures with data from the cursor
            //if (favoritePosterPaths == null)
            favoritePosterPaths = new LinkedList<String>();

            //if (favoriteIds == null)
            favoriteIds = new LinkedList<String>();

            if (data.moveToFirst()) {
                do {
                    String currId = data.getString(COL_MOVIE_ID);
                    String currPosterPath = data.getString(COL_IMAGE_PATH);

                    //if (!favoriteIds.contains(currId))
                    favoriteIds.add(currId);

                    //if (!favoritePosterPaths.contains(posterPathsPrepend+currPosterPath))
                    favoritePosterPaths.add(posterPathsPrepend+currPosterPath);

                } while (data.moveToNext());
            }

            //Populate the
            //We are guaranteed that this method will always be called from the UI thread, so we can call
            //The populateAdapter method after updating the DS And after checking that a boolean variable (diplayingFavorite) is true

            if (displayingFavorite) {

                //if(tasksRequiredForAdapterPopulation == 2) //At the beginning, we should signal the call to the populateAdapter method
                // is from this onLoadFinished Method
                //callFromOnsizeChangeOrSetAttributesOrOnLoadFinishedMethod = true;

                //if(tasksCompletedForAdapterPopulation != -1) //If this is not following an orientation change (in the later case, populateAdapter should be called from onSizeChanged())

                resetAdapterIfNeeded();

                populateAdapter();

            }
            else{
                //If favorites are not displayed,
                // we need to make sure favorite movies are distinguished from non favorite movies
                // by forcing the gridView to repopulate itself

                movieAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //Release the cursor reference
        favoriteCursor = null;
    }


    @Override
    public void onPause(){

        super.onPause();

        /*
        if(endingByOrientationChange())
        { //If we are ending because of orientation change
            //We need to reset the appropriate variables
            synchronized (objectForSync){
                //tasksRequiredForAdapterPopulation = 1;
                //tasksCompletedForAdapterPopulation = -1; //This signals that we are on configuration change
            }

        }
        */

        onScrollListener.userTouched = false;

    }

    private boolean endingByOrientationChange(){

        int newRotation = display.getRotation();

        return (newRotation != originalRotation);
    }
    /*
    */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if(!endingByOrientationChange())
        { //We want to save the instance field only when they are not retained (like when the orientation doesn't change)
            //outState.putInt("currPage", currPage);
            firstPos = custGd.getFirstVisiblePosition();
            outState.putInt("firstPos", firstPos);

            outState.putInt("originalRotation",originalRotation);
            //Serialize all the parameters
            //outState.putString("sortState", urlParameters.get(MovieUtility.getSORT_KEY_NAME()));
            outState.putSerializable("urlParameters", urlParameters);

            outState.putSerializable("absolutePosterPaths", absolutePosterPaths);

            outState.putSerializable("movieIds", movieIds);

            outState.putSerializable("favoritePosterPaths", favoritePosterPaths);

            outState.putSerializable("favoriteIds", favoriteIds);

            outState.putBoolean("instanceStateRetained", false);

            outState.putBoolean("replaced", replaced);

            outState.putString("currentPageEnterred", currentPageEnterred);

            outState.putSerializable("firstVisiblePositions", firstVisiblePositions);

            outState.putInt("currSelection",currSelection);

            outState.putInt("currSelectionPage",currSelectionPage);

            outState.putString("currSelectionId",currSelectionId);

            outState.putBoolean("isTablet",isTablet);
        }
        else{
            outState.putBoolean("instanceStateRetained", true);
        }
    }

    @Override
    //Called after OnCreateView (So can be used to finalize fragment initialization by overriding parameters with saved ones)
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize the favorite cursorLoader
        getLoaderManager().initLoader(FAVORITE_LOADER,null,this);

        //custGd.requestLayout(); //Forces the system to compute the size of the gridView and call its onSizeChanged method if any change be.
        Boolean retainState = true;
        if(savedInstanceState != null)
            retainState = savedInstanceState.getBoolean("instanceStateRetained");

        if(savedInstanceState != null)
        replaced = savedInstanceState.getBoolean("replaced");

        retainState = retainState==null ? false: retainState;

        if (savedInstanceState != null && !retainState) {
            //probably orientation change

            //currPage = savedInstanceState.getInt("currPage");

            firstPos = savedInstanceState.getInt("firstPos");

            originalRotation = savedInstanceState.getInt("originalRotation");

            //String sortState = savedInstanceState.getString("sortState");
            //firstPos = savedInstanceState.getInt("firstPosition");
            if(absolutePosterPaths == null) {
                ArrayList<String> paths = (ArrayList<String>) savedInstanceState.getSerializable("absolutePosterPaths");

                if(paths != null)
                absolutePosterPaths = new LinkedList<String>(paths);
            }

            if(movieIds == null)
            {
                ArrayList<String> ids = (ArrayList<String>) savedInstanceState.getSerializable("movieIds");

                movieIds = new LinkedList<String>(ids);
            }

            if(favoritePosterPaths == null)
            {
                ArrayList<String> favPaths = (ArrayList<String>) savedInstanceState.getSerializable("favoritePosterPaths");

                favoritePosterPaths = new LinkedList<String>(favPaths);
            }

            if(favoriteIds == null)
            {
                ArrayList<String> favIds = (ArrayList<String>) savedInstanceState.getSerializable("favoriteIds");

                favoriteIds = new LinkedList<String>(favIds);
            }

            if(urlParameters == null || urlParameters.size() == 0)
                urlParameters = (HashMap<String, String>)savedInstanceState.getSerializable("urlParameters");

            currPage = Integer.parseInt(urlParameters.get(activity.getString(R.string.page_key)));

            if(savedInstanceState.containsKey("currentPageEnterred"))
                currentPageEnterred = savedInstanceState.getString("currentPageEnterred");

            firstVisiblePositions = (HashMap<Integer,Integer>)savedInstanceState.getSerializable("firstVisiblePositions");

            //Compute the url from the parameters
            url = MovieTask.buildURL(MovieUtility.getBASE_URL(activity), MovieUtility.getDISCOVER_MOVIE_ENDPOINT(activity), urlParameters);

            currSelection = savedInstanceState.getInt("currSelection");
            currSelectionPage = savedInstanceState.getInt("currSelectionPage");
            currSelectionId = savedInstanceState.getString("currSelectionId");


        } else {

        }

        //instanceStateRetained = false;
    }

    public Activity getAttachedActivity(){

        return activity;
    }


    public boolean isFavoriteMovie(int position){

        if(displayingFavorite){
            return true;
        }

        if(favoriteIds != null){

            return favoriteIds.contains(movieIds.get(position));
        }
        else if(favoriteIds == null){

            return false;
        }

        return false;
    }


    public boolean isMovieSelected(int position){

        return (currPage == currSelectionPage)&&position == currSelection;
    }

    public void updateGridItemBackgroundForTablet(int position){
        if(position != -1)
        {
            boolean isBackgroundFavorite = false;
            if(!displayingFavorite)
                if((isFavoriteMovie(position)))
                    isBackgroundFavorite = true;

            int index = position - custGd.getFirstVisiblePosition();
            ImageView currChild = (ImageView)custGd.getChildAt(index);
            if(currChild != null)
            movieAdapter.setBackgroundColor(isMovieSelected(position), isBackgroundFavorite,
                    currChild);
        }
    }
}
