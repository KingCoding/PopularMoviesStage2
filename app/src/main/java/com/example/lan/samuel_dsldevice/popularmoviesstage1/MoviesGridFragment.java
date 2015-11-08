package com.example.lan.samuel_dsldevice.popularmoviesstage1;

/**
 * Created by owner on 11/09/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesGridFragment extends Fragment implements MovieTask.MovieTaskCallBackInstance{

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

    int tempFirstPos;

    Button buttonPrev;
    Button buttonNext;

    EditText pageText;

    String currentPageEnterred;

    private Integer tasksCompletedForAdapterPopulation;

    Integer tasksRequiredForAdapterPopulation;

    boolean callFromOnsizeChangeOrSetAttributesMethod; //Variable that indicates if the populateAdapter method is called from
                                                              // the onsizeChanged method or the setAttributes method

    private MovieListeners.MovieEditTextOnFocusChangeListener onFocusChangeListener;

    private MovieListeners.MovieEditorActionListener editorActionListener;

    private MovieListeners.MovieButtonsOnClickListener onClickListener;

    private MovieListeners.MoviesGridOnItemClickListener onItemClickListener;

    private MovieListeners.MovieOnScrollListener onScrollListener;

    Activity activity;

    //private boolean instanceStateRetained;

    private boolean skipAllUpdateMovie; // boolean to indicate if the updateMovies() method should skip all its operations
                                        // (when the fragment have been completely restored before the updateMovies() method)

    boolean scrollListenerEnabled = false;

    Display display;
    int originalRotation;

    public MoviesGridFragment() {

        //The following line is set in order to make the system retain the state of this Fragment
        //and its variables on configuration change
        //It also keeps the fragment instance from being killed during asynctask operations
        setRetainInstance(true);
        skipAllUpdateMovie = false;
        tasksCompletedForAdapterPopulation = 0;
        tasksRequiredForAdapterPopulation = 2; // The first time the adapter is populated, two tasks need to be completed:
                                               // The download of data from the server (done by the AsyncTasks)
                                               // The call of the onsizeChanged method of the CustomedGridView instance
                                               // Then afterward, only the one task will need to be completed (the call to onsizeChanged on con
                                               // figuration changes or the call to onStart)
        callFromOnsizeChangeOrSetAttributesMethod = false; //

        firstVisiblePositions = new HashMap<Integer, Integer>();

        firstPos = tempFirstPos = 0;

        //Initialize the originalOrietation with a default invalid value
        originalRotation = -1;

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

        if(!PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(getString(R.string.pref_sort_key)))
            PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);
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
        View rootView = inflater.inflate(R.layout.fragment_pm1__main, container, false);//the fragment might already be attached to the container.
                                                                                        // Setting the attachToRoot variable to true might make the program fail.
        ViewGroup layout = (ViewGroup) rootView.findViewById(R.id.gridFrame);

        activity = getActivity();

            initViews(rootView);

        if(urlParameters == null)
        {
            //Create the url parameters hashMap
            urlParameters = new HashMap<String, String>();

            currPage = (int) getResources().getInteger(R.integer.default_page);
        }

        return rootView;
    }

    //This is called only if the views were destroyed or their context was changed
    private void initViews(View rootView){

        //Initialize the display
        display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int oldOriginalRotation = originalRotation;
        originalRotation = display.getRotation();

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

        ViewGroup layout = (ViewGroup) rootView.findViewById(R.id.gridFrame);
        custGd = (CustomedGridView)layout.getChildAt(0); // the only child is the gridView instance
        custGd.setMovieFragment(this);

        //Set the onclickListener since it needs to be done just once
        custGd.setOnItemClickListener(onItemClickListener);
        scrollListenerEnabled = false; //this must be set before the onScrollListener is set on the custumedGridView
                                       //The onScrollListener will be set somewhere after in the populateAdapter method

        //Build adapter
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
        pageText.setOnFocusChangeListener(onFocusChangeListener);
        pageText.setOnEditorActionListener(editorActionListener);
        pageText.setFocusable(true);
        pageText.setSingleLine();

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

        resetAdapterIfNeeded(); //movieAdapter.clear();
        custGd.updateNumColumns(); //We must initiate the update of the number of columns from here
                                   // because the size of the gridView doesn't change and its onSizeChanged method won't be called

        if(custGd != null){

            //if the url parameters changed or url is null, we build a new one and query the server
            if (needsToBuildUrl()) {

                scrollListenerEnabled = false;

                //Query network data with asynctask
                String sortMode = MovieUtility.getStringSharedPreference(activity.getApplicationContext(), getString(R.string.pref_sort_key),
                        urlParameters.get(MovieUtility.getSORT_KEY_NAME(activity)));
                urlParameters.put(MovieUtility.getSORT_KEY_NAME(activity), sortMode);
                urlParameters.put(MovieUtility.getAPI_KEY_NAME(activity), MovieUtility.getAPI_KEY(activity));
                urlParameters.put(activity.getString(R.string.page_key), "" + currPage);

                //record the initial firstPosition for this page in the appropriate Map if required
                if(!firstVisiblePositions.containsKey(currPage)){
                    firstVisiblePositions.put(currPage,0); //The initial first position is 0
                }

                MovieTask movieAsyncTask = new MovieTask(this);

                url = MovieTask.buildURL(MovieUtility.getBASE_URL(activity), MovieUtility.getDISCOVER_MOVIE_ENDPOINT(activity), urlParameters);

                //scrollListenerEnabled = false; //updating this variable at this point surprisingly has no effect; that's yet to be understood.
                movieAsyncTask.execute(url);
                //scrollListenerEnabled = false; //updating this variable at this point surprisingly has no effect (it would have been ideal); that's yet to be understood.
            } else //if(number of columns changed)
            {

                synchronized (tasksRequiredForAdapterPopulation) {

                    if (tasksCompletedForAdapterPopulation != -1) //Only if it's not in configuration change should we populate the adapter.
                        populateAdapter(); //This will make the gridView populate itself.
                }
            }
            //else{
            //Nothing
            //}

            //Hide or show the navigation bar
            Boolean showNavBar = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.nav_key), true);
            View navBar = activity.findViewById(R.id.navBarLayout);

            if (showNavBar)
                navBar.setVisibility(View.VISIBLE);
            else
                navBar.setVisibility(View.GONE);

            //Set the page number
            //ViewGroup navLayout = (ViewGroup)getActivity().findViewById(R.id.navBarLayout);
            //TextView pageText = (TextView)navLayout.findViewById(R.id.textView_set_page);
            pageText.setText("" + currPage);

        }

    }


    private boolean needsToBuildUrl()
    {

        if(url == null)
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

            if(tasksRequiredForAdapterPopulation == 2) //At the beginning, we should signal the call to the populateAdapter method
                                                       // is from this setAttributes Method
                callFromOnsizeChangeOrSetAttributesMethod = true;

            populateAdapter();
        }
        else{ //the task execution failed, we should restart it
            url = null;
            updateMovies();
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

        //movieAdapter.setNotifyOnChange(true);
    }


    public void populateAdapter(){

    synchronized (tasksRequiredForAdapterPopulation) {


        if(
                (tasksRequiredForAdapterPopulation == 2 &&
                        callFromOnsizeChangeOrSetAttributesMethod) //At the beginning (When tasksRequiredForAdapterPopulation == 2),
                                                                   // the valid calls to this method should be made only from the setAttributes method of this class
                                                                   // or from the onsizeChanged method of the custom gridView
                ||
                tasksRequiredForAdapterPopulation == 1 //After start, every call to this method is valid
                ||
                tasksCompletedForAdapterPopulation == -1 //Used to signal the call is following an orientation change
                )

            tasksCompletedForAdapterPopulation++;

        if((tasksCompletedForAdapterPopulation == tasksRequiredForAdapterPopulation
                || tasksCompletedForAdapterPopulation == 0)) {

            //If needed, We set the onScrollListener for the custom gridView instance here
            //Because we want to make sure we always do it after onsizeChanged has been called on the
            //GridView instance; so to avoid handling unnecessary events in the listener.
            if(!scrollListenerEnabled)
            {//CustGd can't be null at this stage
                custGd.setOnScrollListener(onScrollListener);
            }

            //Set the paths in the movie adapter
            //We first set the adapter not to notify its view of any change when it's being populated,
            //because it will be very computationally intensive
            movieAdapter.setNotifyOnChange(false);
            for (String path : absolutePosterPaths) {
                movieAdapter.add(path);

            }

                movieAdapter.notifyDataSetChanged();
                custGd.setSelection(tempFirstPos); // This instruction implicitly sets the adapter notification state to true.
                                                   // No need to call movieAdapter.setNotifyOnChange(true); after this
                firstPos = tempFirstPos;

            //Initialize the task controller variables to their regular values
            tasksCompletedForAdapterPopulation  = 0;
            tasksRequiredForAdapterPopulation = 1; //after the first populating of the adapter only one task should be completed before the adapter is populated
        }

        callFromOnsizeChangeOrSetAttributesMethod = false;
    }

    }

    public interface MovieDetailInitiator{

        public void sendIntent(String... intentData);
    }



    @Override
    public void onPause(){

        super.onPause();

        if(endingByOrientationChange())
        { //If we are ending because of orientation change
          //We need to reset the appropriate variables
            synchronized (tasksRequiredForAdapterPopulation){
                tasksRequiredForAdapterPopulation = 1;
                tasksCompletedForAdapterPopulation = -1; //This signals that we are on configuration change
            }

        }

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
            //Serialize all the parameters
            //outState.putString("sortState", urlParameters.get(MovieUtility.getSORT_KEY_NAME()));
            outState.putSerializable("urlParameters", urlParameters);

            outState.putSerializable("absolutePosterPaths", absolutePosterPaths);

            outState.putSerializable("movieIds", movieIds);

            outState.putBoolean("instanceStateRetained", false);

            outState.putSerializable("firstVisiblePositions", firstVisiblePositions);
        }
        else{
            outState.putBoolean("instanceStateRetained", true);
        }
    }

    @Override
    //Called after OnCreateView (So can be used to finalize fragment initialization by overriding parameters with saved ones)
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //custGd.requestLayout(); //Forces the system to compute the size of the gridView and call its onSizeChanged method if any change be.
        Boolean retainState = true;
        if(savedInstanceState != null)
            retainState = savedInstanceState.getBoolean("instanceStateRetained");

        retainState = retainState==null ? false: retainState;

        if (savedInstanceState != null && !retainState) {
            //probably orientation change

            //currPage = savedInstanceState.getInt("currPage");

            firstPos = savedInstanceState.getInt("firstPos");

            //String sortState = savedInstanceState.getString("sortState");
            //firstPos = savedInstanceState.getInt("firstPosition");
            if(absolutePosterPaths == null)
            absolutePosterPaths = (LinkedList<String>)savedInstanceState.getSerializable("absolutePosterPaths");

            if(movieIds == null)
            movieIds = (LinkedList<String>)savedInstanceState.getSerializable("movieIds");


            if(urlParameters == null || urlParameters.size() == 0)
            urlParameters = (HashMap<String, String>)savedInstanceState.getSerializable("urlParameters");

            currPage = Integer.parseInt(urlParameters.get(activity.getString(R.string.page_key)));

            firstVisiblePositions = (HashMap<Integer,Integer>)savedInstanceState.getSerializable("firstVisiblePositions");

            //Compute the url from the parameters
            url = MovieTask.buildURL(MovieUtility.getBASE_URL(activity), MovieUtility.getDISCOVER_MOVIE_ENDPOINT(activity), urlParameters);

        } else {

        }

        //instanceStateRetained = false;
    }

    public Activity getAttachedActivity(){

        return activity;
    }


}
