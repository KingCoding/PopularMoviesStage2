package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lan.samuel_dsldevice.popularmoviesstage2.favoriteData.FavoriteContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by owner on 07/12/2015.
 */
public class MovieDetailFragment extends Fragment implements MovieTask.MovieTaskCallBackInstance, MovieDetailsLoaderCallbacks<Cursor>{

    private static final int FAVORITE_DETAIL_LOADER = 0;

    private static final int FAVORITE_DETAIL_UPDATER = 1;

    String movieId;
    boolean isFavorite;
    boolean movieDetailsLoaded;
    boolean primaryDetailsLoaded;
    boolean trailersLoaded;
    boolean reviewsLoaded;
    HashMap<String, Object> currentDetails;
    String currMainList; //The default value is none
    String currMainListExtension; //The default value is none

    LinkedList<String> detailsLoadingOrder;

    LinkedList<String> reviewsList;

    LinkedList<String> reviewsAuthors;

    LinkedList<Integer> reviewsSizes;

    MovieDetailAdapter reviewsAdapter;
    int reviewListFirstVisiblePosition;
    private MovieListeners.ListNavigators reviewsListNavigatorsListener;

    private int currReviewIndex;

    LinkedList<String> trailersKeys;
    LinkedList<String> trailersNames;
    LinkedList<String> trailersSites;
    LinkedList<String> trailersSizes;
    MovieDetailAdapter trailersAdapter;
    int trailerListFirstVisiblePosition;
    private MovieListeners.ListNavigators trailersListNavigatorListener;

    private String trailer1Path = "No trailer loaded yet";

    private int currTrailerIndex;

    private static String videoExtensionName = "videos";

    private static String reviewExtensionName = "reviews";

    private static String primaryDetailsExtensionName = "PRIMARY";

    private Loader<Cursor> movieCursorLoader;

    private Loader<Cursor> movieCursorUpdater;

    private boolean updatingCursor;

    private Object lockMonitor;

    private Lock updateLock;

    private int updateCode;

    private int updateResult;

    private static final int DELETE = 1;

    private static final int INSERT = 2;

    private static final int UPDATE_SUCCESS = 1;

    private static final int UPDATE_FAILURE = 2;

    private MovieListeners.MovieDetailTrailerReviewsOnItemClickListener movieDetailReviewsOnItemClickListener;
    private MovieListeners.MovieDetailTrailerReviewsOnItemClickListener movieDetailTrailerOnItemClickListener;

    private MovieListeners.MovieFavoriteButtonOnClickListener movieFavoriteButtonOnClickListener;
    //private MovieTask movieTask;
    DetailViewHolder detailViewHolder;
    private int iconDpSize;
    private int favoriteIconDpSize;
    private double imageRatio;

    ShareActionProvider mShareActionProvider;


    public MovieDetailFragment() {
        setRetainInstance(true); //We don't want this fragment instance to be killed while the background task is being executed
        movieDetailsLoaded = false;
        updatingCursor = false;
        lockMonitor = new Object();
        updateLock = new ReentrantLock();
        movieFavoriteButtonOnClickListener = new MovieListeners.MovieFavoriteButtonOnClickListener(this);
        movieDetailReviewsOnItemClickListener = new MovieListeners.MovieDetailTrailerReviewsOnItemClickListener(this);
        movieDetailTrailerOnItemClickListener = new MovieListeners.MovieDetailTrailerReviewsOnItemClickListener(this);
        reviewsListNavigatorsListener = new MovieListeners.ListNavigators(this);
        trailersListNavigatorListener = new MovieListeners.ListNavigators(this);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.menu_movie_detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share_trailer1_url);

        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(menuItem);

        //Set a default void intent on the share action provider
        if(!trailersLoaded){
            mShareActionProvider.setShareIntent(new Intent(Intent.ACTION_SEND));
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
    @Override
    public void onPrepareOptionsMenu (Menu menu){

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareDetailIntent());
        }
        else{

        }
    }
    */

    public void showReview(int reviewIndex){

        /*
        Intent reviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+""));
        reviewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        reviewIntent.putExtra("sms_body",reviewsList.get(reviewIndex));
        startActivity(Intent.createChooser(reviewIntent,"View Review with"));
        */

        reviewListFirstVisiblePosition = reviewIndex;
        int adjustedReviewIndex =  reviewIndex++;
        Bundle reviewsArguments = new Bundle();
        //Set arguments for the movie title, review current index and reviewList
        //...
        reviewsArguments.putInt("" + R.id.review_display_title_textview, reviewIndex);
        reviewsArguments.putString(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()), (String)currentDetails.get(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity())));

        int currImageSizeRange = 3;
        String reviewIconUrl = MovieUtility.getPOSTER_PATH_BASE_URL(getActivity())
                + MovieUtility.getImageSizeKey(currImageSizeRange, getActivity())
                + currentDetails.get(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()));
        reviewsArguments.putString(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()), reviewIconUrl);

        ArrayList<String> reviewsArrayList = new ArrayList<String>(reviewsList);
        reviewsArguments.putStringArrayList(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()) , reviewsArrayList);

        ArrayList<String> reviewAuthorsArrayList = new ArrayList<String>(reviewsAuthors);
        reviewsArguments.putStringArrayList(""+R.id.reviewer_textview , reviewAuthorsArrayList);

        //reviewsArguments.putString(""+R.id.reviewer_textview,reviewsAuthors.get(reviewIndex-1));

        PM2_MainActivity pm2_mainActivity = (PM2_MainActivity)getActivity();

        //reviewListFirstVisiblePosition = reviewIndex-1;

        pm2_mainActivity.sendIntent(MovieUtility.MovieDetailInitiator.REPLACE_DETAIL_WITH_REVIEW, reviewsArguments);


    }

    private Intent createShareDetailIntent(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET s deprecated by FLAG_ACTIVITY_NEW_DOCUMENT            shareIntent.setType("text/plain");
        if(getActivity() != null && currentDetails != null)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, (currentDetails.get(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()))).toString());
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailer1Path);//trailer1Path);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    //Approach 1
    //Make use of asyncktask to implement this method
    // And make sure there are callbacks for deleting and updating the db
    // that will be called from the asynctask.

    //Approach 2
    //Implement a CursorLoader and override its loadInBackground method to
    //make updates to the db. In this case, the return cursor will be null
    //That's what we will base ourselves on in the onLoad finished method to
    //distinguish between updates and other operations.
    //This fragment will implement the callback that will be called by the
    //overridden method. Variables in ths fragment will inform the loader about
    //The type of operation (loading, update)
    //This method will just set the right variables, wait when every details is loaded
    //and restart the customized loader.
    public void updateFavoriteStatu(boolean makeFavorite){

            final boolean favorite = makeFavorite;

            //Kick off the restart of the loader in a new handler
            //To ensure that the notification for the following wait() in the UI thread will always be done in a different thread

                    updateLock.lock();

                    detailViewHolder.favoriteButton.setEnabled(false);//disable the button before performing the update

                    if(favorite){ //Save the current movie details in the favorite db
                        updateCode = INSERT;
                    }
                    else{// Remove the current movie details in the favorite db
                        updateCode = DELETE;
                    }

                    updatingCursor = true;
                    synchronized (lockMonitor) {

                    getLoaderManager().restartLoader(FAVORITE_DETAIL_UPDATER, null, this);
                    try{
                        lockMonitor.wait(); //wait for the signal that the loader has been instanciated
                        updateLock.unlock(); //now free the update lock
                    }
                    catch(InterruptedException iexp){

                    }
                }

    }


    public void showCurrentTrailer(int trailerIndex){

        //reviewListFirstVisiblePosition = detailViewHolder.reviewsContainer.getFirstVisiblePosition();
        //trailerListFirstVisiblePosition = detailViewHolder.trailersContainer.getFirstVisiblePosition();

        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailersKeys.get(trailerIndex)));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET s deprecated by FLAG_ACTIVITY_NEW_DOCUMENT            shareIntent.setType("text/plain");addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET s deprecated by FLAG_ACTIVITY_NEW_DOCUMENT            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(intent,"View Trailer "+trailersNames.get(trailerIndex)+" with"));
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+trailersKeys.get(trailerIndex)));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET s deprecated by FLAG_ACTIVITY_NEW_DOCUMENT            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(intent,"View Trailer "+trailersNames.get(trailerIndex)+" with"));        }

        trailerListFirstVisiblePosition = trailerIndex;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //Override the value of isFavorite with any possibly saved value
        if(savedInstanceState!=null && savedInstanceState.containsKey("isFavorite"))
            isFavorite = savedInstanceState.getBoolean("isFavorite");

        // If the value of movieDetailsLoaded is false, try to get it from the savedInstanceState in case it was persisted
        // When the fragment was last destroyed

        if(!movieDetailsLoaded && savedInstanceState!=null && savedInstanceState.containsKey("movieDetailsLoaded"))
            movieDetailsLoaded = savedInstanceState.getBoolean("movieDetailsLoaded");


            //Intent intent = getActivity().getIntent();
            iconDpSize = getActivity().getResources().getInteger(R.integer.icon_size);
            favoriteIconDpSize = (int)MovieUtility.convertDpToPixel(25, getActivity());
            Bundle args = getArguments();
            String favorite = null;
            if(args!=null) {
                movieId = args.getString(MovieUtility.getMOVIE_ID_ATTRIBUTE(getActivity()));
                favorite = args.getString(MovieUtility.getSORT_FAVORITE(getActivity()));
                imageRatio = args.getDouble(MovieUtility.getMOVIE_FAVORITE_ICON_KEY(getActivity()));
            }

            //We make sure the following is executed only when the fragment is first created
            //Not on screen configuration
            if (args != null && movieId != null) {

                //We can assume it's as at the beginning if the movie details aren't completely loaded.
                //Then we get the value of isFavorite from the arguments.
                if(!movieDetailsLoaded)//if(savedInstanceState==null||(savedInstanceState!=null && !savedInstanceState.containsKey("isFavorite")))
                    isFavorite = (favorite != null);

                /*
                View detailInnerContainer = (ViewGroup) rootView.findViewById(R.id.detail_inner_container);
                //We initialize the view holder
                detailViewHolder = new DetailViewHolder(detailInnerContainer, getActivity());
                */
                //movieId = intent.getStringExtra(Intent.EXTRA_TEXT);

                //Initialize the review and trailer current indexes
                currTrailerIndex = 0;
                currReviewIndex = 0;

                if(!movieDetailsLoaded) {
                    if (detailsLoadingOrder == null)
                        setDetailsLoadingOrder();

                    if (!isFavorite && currMainListExtension == null) { //We want to make sure the current list extension is not ignored in case we return from an orientation change
                        //HashMap<String, String> urlParameters = new HashMap<String, String>();
                        //urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));
                        updateMainListMetaData();
                        //loadMovieDetails();
                    }

                }
            }



        View detailInnerContainer = (ViewGroup) rootView.findViewById(R.id.detail_inner_container);
        //We initialize the view holder
        detailViewHolder = new DetailViewHolder(detailInnerContainer, getActivity());

        ((CustomedImageView)detailViewHolder.movieImageView).setFragment(this);
        //Make sure the previous instances of the adapters are null
        reviewsAdapter = null;
        trailersAdapter = null;

        //reviewListFirstVisiblePosition = trailerListFirstVisiblePosition = 0;

        return rootView;
    }

    private void setDetailsLoadingOrder(){
        detailsLoadingOrder = new LinkedList<String>();

        detailsLoadingOrder.add(primaryDetailsExtensionName);
        detailsLoadingOrder.add(reviewExtensionName);
        detailsLoadingOrder.add(videoExtensionName);
    }

    private void updateMainListMetaData(){

        try {
            this.currMainListExtension = detailsLoadingOrder.get(0);
        }catch(RuntimeException rtexp){

            this.currMainListExtension = null;
        }

        if(currMainListExtension!=null) {

            if(currMainListExtension.equals(primaryDetailsExtensionName))
                currMainList = null; //No main list is associated with the primary details
            else if(currMainListExtension.equals(reviewExtensionName))
                currMainList = MovieUtility.getMAIN_REVIEW_MOVIE_LIST_NAME(getActivity());
            else
                currMainList = MovieUtility.getMAIN_VIDEO_MOVIE_LIST_NAME(getActivity());
        }

        if(detailsLoadingOrder.size()!=0)
        detailsLoadingOrder.removeFirst(); //pop the current detail extension name
    }

    private void loadMovieDetails(){

        //movieDetailsLoaded = (reviewsLoaded && primaryDetailsLoaded && trailersLoaded); //Compute the latest value
        if(!movieDetailsLoaded) {
            //This method should perform its work based on the current detail extension value
            //We should make sure the currentMainListExtension value is not null
            if (currMainListExtension == null) {

                if (detailsLoadingOrder == null || detailsLoadingOrder.size()==0)
                    setDetailsLoadingOrder();

                if (!isFavorite && currMainListExtension == null) { //We want to make sure the current list extension is not ignored in case we return from an orientation change
                    //HashMap<String, String> urlParameters = new HashMap<String, String>();
                    //urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));
                    updateMainListMetaData();
                    //loadMovieDetails();
                }

            }
                if(!isFavorite) {
                    try {

                        MovieTask movieTask = new MovieTask(this);
                        URL url = null;
                        if (currMainListExtension.equals(primaryDetailsExtensionName)) {
                            //movieTask = new MovieTask(this);

                            HashMap<String, String> urlParameters = new HashMap<String, String>();
                            urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));

                            url = movieTask.buildURL(MovieUtility.getBASE_URL(getActivity()), MovieUtility.getMOVIE_ENDPOINT(getActivity()) + movieId, urlParameters);

                        } else if (currMainListExtension.equals(reviewExtensionName)) {
                            HashMap<String, String> urlParameters = new HashMap<String, String>();
                            urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));

                            url = movieTask.buildURL(MovieUtility.getBASE_URL(getActivity()), MovieUtility.getMOVIE_ENDPOINT(getActivity()) + movieId + "/" + reviewExtensionName, urlParameters);
                        } else if (currMainListExtension.equals(videoExtensionName)) {
                            HashMap<String, String> urlParameters = new HashMap<String, String>();
                            urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));

                            url = movieTask.buildURL(MovieUtility.getBASE_URL(getActivity()), MovieUtility.getMOVIE_ENDPOINT(getActivity()) + movieId + "/" + videoExtensionName, urlParameters);
                        }
                        if(movieId!=null)
                            movieTask.execute(url);

                    } catch (Exception exp) {

                    }
                }
        }
        else{
            setAttributes(currentDetails);
        }
    }

    @Override
    public void onStart(){

        super.onStart();

        //if(/* !movieDetailsLoaded && */!isFavorite){
            //setAttributes(null);
            loadMovieDetails();
        //}

    }

    @Override
    public void onPause(){
        super.onPause();

        //We will preserve the index of the items most visible between the first and second visible of each list
        reviewListFirstVisiblePosition = mostVisibleFirstOrSecond(detailViewHolder.reviewsContainer);
        trailerListFirstVisiblePosition = mostVisibleFirstOrSecond(detailViewHolder.trailersContainer);
    }

    int mostVisibleFirstOrSecond(ViewGroup list){

        View first = null;
        View second = null;

        first = list.getChildAt(0);
        second = list.getChildAt(1);

        int mostVisibleFirstOrSecond = ((ListView)list).getFirstVisiblePosition();
        if(first == null || second == null || (second.getTop() > list.getHeight()))
            ;//mostVisibleFirstOrSecond = ((ListView)list).getFirstVisiblePosition();
        else{
            int firstVisibleHeight = first.getBottom();
            int secondVisibleHeight = detailViewHolder.reviewsContainer.getHeight() - second.getTop();
            if(firstVisibleHeight < secondVisibleHeight)
                mostVisibleFirstOrSecond = ((ListView)list).getFirstVisiblePosition()+1;

        }

        return mostVisibleFirstOrSecond;
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //Persist the state of the currentDetails, of movieDetailsLoaded and of the trailers and reviews indexes
        outState.putBoolean("movieDetailsLoaded", movieDetailsLoaded);
        outState.putBoolean("isFavorite", isFavorite);
        outState.putInt("currReviewIndex",currReviewIndex);
        outState.putInt("currTrailerIndex",currTrailerIndex);
        //Save the currentDetails hashMap
        outState.putSerializable("currentDetails", currentDetails);

        reviewListFirstVisiblePosition = mostVisibleFirstOrSecond(detailViewHolder.reviewsContainer);
        trailerListFirstVisiblePosition = mostVisibleFirstOrSecond(detailViewHolder.trailersContainer);

        outState.putInt("reviewListFirstVisiblePosition", reviewListFirstVisiblePosition);
        outState.putInt("trailerListFirstVisiblePosition",trailerListFirstVisiblePosition);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //Load the variables saved in the bundle savedInstanceState
        //...
        if(savedInstanceState != null) {
            currentDetails = (HashMap<String, Object>) savedInstanceState.getSerializable("currentDetails");

            reviewListFirstVisiblePosition = savedInstanceState.getInt("reviewListFirstVisiblePosition");
            trailerListFirstVisiblePosition = savedInstanceState.getInt("trailerListFirstVisiblePosition");
        }

        //Initialize the favorite cursorLoader if the details are for a favorite movie
        if(isFavorite) {
            getLoaderManager().initLoader(FAVORITE_DETAIL_LOADER, null, this);
        }

        if(movieDetailsLoaded){ // We are returning from a previous persisted session

            //We need to call the setAttributes method after resetting the load indicator variables back to false
            primaryDetailsLoaded = trailersLoaded = reviewsLoaded = false; //They are actually loaded at this point. But this is a hack to reconstruct the UI
            //setAttributes(currentDetails);
        }
        /*
        else{
            //HashMap<String, String> urlParameters = new HashMap<String, String>();
            //urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));
            updateMainListMetaData();
            loadMovieDetails();
        }*/

        /*
        ViewGroup container = detailViewHolder.container;
        ViewGroup parent = (ViewGroup)container.getParent();
        parent.removeView(container);
        detailViewHolder.container = parent;
        */


        //At this point, restore the state of views after orientation change by
        //first Checking that the associated member variables have been retained
        //...
    }

    @Override
    public String getMainListName() {
        return currMainList;
    }

    @Override
    public LinkedList<String> getAttributesNames() {

        LinkedList<String> attributesNames = new LinkedList<String>();

        if(currMainListExtension.equals(primaryDetailsExtensionName)) {
            currMainList = null; //No main list is associated with the primary details
            attributesNames.add(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_RELEASE_DATE_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_DURATION_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_RATING_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_SYPNOSIS_ATTRIBUTE(getActivity()));
        }
        else if(currMainListExtension.equals(reviewExtensionName)) {
            attributesNames.add(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE((getActivity())));
            attributesNames.add(MovieUtility.getMOVIE_REVIEW_AUTHOR_ATTRIBUTE(getActivity()));
        }
        else{
            attributesNames.add(MovieUtility.getMOVIE_TRAILER_KEY_ATTRIBUTE((getActivity())));
            attributesNames.add(MovieUtility.getMOVIE_TRAILER_NAME_ATTRIBUTE((getActivity())));
            attributesNames.add(MovieUtility.getMOVIE_TRAILER_SITE_ATTRIBUTE((getActivity())));
            attributesNames.add(MovieUtility.getMOVIE_TRAILER_SIZE_ATTRIBUTE((getActivity())));
        }


        return attributesNames;
    }

    @Override
        /*
        This implementation expects values of the hashMap to be of type String
         */
    public void setAttributes(HashMap<String, Object> attributes) {

        if (detailViewHolder != null) {

            if(attributes != null) {

                int remainingDetails = detailsLoadingOrder.size();

                updateAttributes(attributes);
                if(!primaryDetailsLoaded && attributes.get(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()))!=null){


                    detailViewHolder.titleTextView.setText((String) attributes.get(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity())));

                    int currImageSizeRange = 3;
                    String currImageUrl = MovieUtility.getPOSTER_PATH_BASE_URL(getActivity())
                            + MovieUtility.getImageSizeKey(currImageSizeRange, getActivity())
                            + attributes.get(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()));

                    //Picasso.with(getActivity()).load(currUrl).into(detailViewHolder.movieImageView);
                    ((CustomedImageView)detailViewHolder.movieImageView).setImageUrl(currImageUrl);

                    //Set the ratio of the image
                    ((CustomedImageView)detailViewHolder.movieImageView).setImageRatio(imageRatio);

                    //compute the image sizes and load the image
                    ((CustomedImageView)detailViewHolder.movieImageView).setLayoutAndComputeImageDimensions();

                    //Attempt to load the image
                    //((CustomedImageView)detailViewHolder.movieImageView).loadImage(imageRatio);

                    //Set the Favorite icon
                    updateFavoriteIcon();

                    //final ScrollView scrollView = (ScrollView)getActivity().findViewById(R.id.movie_detail_scrollview);

                    //Position the scrollView at the middle
                    //scrollView.scrollTo(0, scrollView.getBottom()/2);
                /*
                ViewTreeObserver vto = scrollView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        scrollView.scrollTo(scrollView.getChildAt(0).getWidth()/2, 0);
                    }
                });
                */

                    detailViewHolder.yearTextView.setText((String) attributes.get(MovieUtility.getMOVIE_RELEASE_DATE_ATTRIBUTE(getActivity())));
                    detailViewHolder.durationTextView.setText((String) (""+attributes.get(MovieUtility.getMOVIE_DURATION_ATTRIBUTE(getActivity())) + getActivity().getString(R.string.movie_duration_unit)));
                    detailViewHolder.ratingTextView.setText((String) ("Rating: "+attributes.get(MovieUtility.getMOVIE_RATING_ATTRIBUTE(getActivity()))));
                    detailViewHolder.sypnosisTextView.setText((String) attributes.get(MovieUtility.getMOVIE_SYPNOSIS_ATTRIBUTE(getActivity())));

                    primaryDetailsLoaded = true;

                    if(!movieDetailsLoaded)
                        movieDetailsLoaded = (reviewsLoaded && primaryDetailsLoaded && trailersLoaded);

                    //Update the current main list and its extension for the next details
                    // (an order mechanism can be defined in an improved version with a linked list FIFO that contains indexes of deatails
                    // in the order they should be executed. To execute for some details, Its index is first removed. The list FIFO
                    // could be populated in the onCreateView method)

                    updateMainListMetaData();

                    if(!isFavorite && !movieDetailsLoaded)
                    {
                        //call loadMoviesDetails before returning
                        loadMovieDetails();
                        return;
                    }

                }

                if(!reviewsLoaded /*and one attributes is readable*/&&
                        attributes.get(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()))!=null)
                //The attribute is retrieved from the hashMap based on the main list name and its extension
                {
                    //Retrieve the reviews String from the hashMap;
                    //...

                    reviewsAuthors = (LinkedList<String>)attributes.get(MovieUtility.getMOVIE_REVIEW_AUTHOR_ATTRIBUTE(getActivity()));

                    reviewsList = (LinkedList<String>)attributes.get(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()));

                    //Populate the reviewsSizes list
                    LinkedList<Integer> sizes = new LinkedList<Integer>();
                    for(int i=0; i< reviewsList.size(); i++)
                        sizes.add(reviewsList.get(i).length());
                    reviewsSizes = sizes;

                    //String reviewsConcatenated = (String)attributes.get(MovieUtility.getMAIN_REVIEW_MOVIE_LIST_NAME(getActivity())+reviewExtensionName);
                    //String[] reviewsSeparated = reviewsConcatenated.split(FavoriteContract.FavoriteEntry.COLUMN_FIELD_SEPARATOR);
                    //reviews = reviewsSeparated;
                    String reviewsTitle = null;

                    reviewsTitle = reviewsList.size() + " "+MovieUtility.getREVIEW_TITLE_TEXT(getActivity());

                    String prevTitle = ((TextView)detailViewHolder.reviewTitleTextView).getText().toString();

                    /*
                    //Get the empty space preceding the previous title and prepend it to the current title.

                    for(int i=0; i< prevTitle.length(); i++)
                        if(prevTitle.substring(i,i).equals(""))
                            reviewsTitle = " "+reviewsTitle;
                        else
                            break;

                    if(reviewsList.size() <= 1)
                        reviewsTitle = (reviewsTitle.substring(0,reviewsTitle.length()-2) + reviewsTitle.substring(reviewsTitle.length()-1,reviewsTitle.length()));
                    */
                    //Set the current title
                    ((TextView)detailViewHolder.reviewTitleTextView).setText(reviewsTitle);

                    //Set or update the adapter on the reviews listView
                    if(reviewsAdapter == null) {//Initialize the adapter

                        reviewsAdapter = new MovieDetailAdapter(getActivity(), R.layout.review_item_layout);
                        reviewsAdapter.setDrawable(MovieUtility.getSizedDrawable(R.drawable.read_review_icon, iconDpSize, iconDpSize, getActivity()));
                        reviewsAdapter.setAdapterType(MovieDetailAdapter.reviewCode);
                        reviewsAdapter.setNotifyOnChange(false);
                        detailViewHolder.reviewsContainer.setAdapter(reviewsAdapter);
                        //pass the onclick listener to the adapter
                        reviewsAdapter.setOnclickListener(movieDetailReviewsOnItemClickListener);
                        reviewsAdapter.setAdapterView(detailViewHolder.reviewsContainer);
                    }
                    else{ //update the adapter
                        reviewsAdapter.setNotifyOnChange(false);
                        reviewsAdapter.clear();
                    }

                    //Populate the adapter with the new data
                    int currReviewIndex = 0;
                    for (String reviewAuthor : reviewsAuthors) {
                        //reviewsAdapter.add("Review "+currReviewIndex);
                        currReviewIndex++;
                        reviewsAdapter.add(currReviewIndex + " - " + reviewAuthor);
                    }

                    reviewsAdapter.setItemMetaData(reviewsSizes);

                    reviewsAdapter.notifyDataSetChanged();
                    reviewsAdapter.setNotifyOnChange(true);

                    //
                    detailViewHolder.reviewsContainer.setSelection(reviewListFirstVisiblePosition);
                    //detailViewHolder.reviewsContainer.requestFocusFromTouch();
                    //detailViewHolder.reviewsContainer.setItemChecked(reviewListFirstVisiblePosition, true);
                    reviewsLoaded = true;

                    if(!movieDetailsLoaded)
                        movieDetailsLoaded = (reviewsLoaded && primaryDetailsLoaded && trailersLoaded);

                    updateMainListMetaData();

                    if(!isFavorite && !movieDetailsLoaded)
                    {
                        //call loadMoviesDetails before returning
                        loadMovieDetails();
                        return;
                    }

                }

                if(!trailersLoaded /*and one attributes is readable*/
                        &&attributes.get(MovieUtility.getMOVIE_TRAILER_KEY_ATTRIBUTE(getActivity()))!=null){

                    //Retrieve the trailers lists from the hashMap;
                    //...
                    trailersKeys = (LinkedList<String>)attributes.get(MovieUtility.getMOVIE_TRAILER_KEY_ATTRIBUTE(getActivity()));
                    trailersNames = (LinkedList<String>) attributes.get(MovieUtility.getMOVIE_TRAILER_NAME_ATTRIBUTE(getActivity()));
                    trailersSites = (LinkedList<String>) attributes.get(MovieUtility.getMOVIE_TRAILER_SITE_ATTRIBUTE(getActivity()));
                    trailersSizes = (LinkedList<String>) attributes.get(MovieUtility.getMOVIE_TRAILER_SIZE_ATTRIBUTE(getActivity()));

                    //String trailersConcatenated = (String)attributes.get(MovieUtility.getMAIN_VIDEO_MOVIE_LIST_NAME(getActivity())+reviewExtensionName);
                    //String[] trailersSeparated = trailersConcatenated.split(FavoriteContract.FavoriteEntry.COLUMN_FIELD_SEPARATOR);
                    //trailersPaths = trailersSeparated;
                    if(trailersKeys != null && trailersKeys.size() > 0)
                     trailer1Path = "http://www.youtube.com/watch?v=" + trailersKeys.get(0);
                    mShareActionProvider.setShareIntent(createShareDetailIntent());
                    //Retrieve the views through the view holder and Update their values with the current review information
                    // (the number of reviews and the current review)
                    //...

                    String trailersTitle = null;

                    trailersTitle = trailersKeys.size() + " "+MovieUtility.getTRAILER_TITLE_TEXT(getActivity());

                    String prevTitle = ((TextView)detailViewHolder.trailerTitleTextView).getText().toString();

                    //Get the empty space preceding the previous title and prepend it to the current title.
                    /*
                    for(int i=0; i< prevTitle.length(); i++)
                        if(prevTitle.substring(i,i).equals(""))
                            trailersTitle = " "+trailersTitle;
                        else
                            break;

                    if(trailersKeys.size() <= 1)
                        //Remove the s on the title name
                          trailersTitle = (trailersTitle.substring(0,trailersTitle.length()-2) + trailersTitle.substring(trailersTitle.length()-1,trailersTitle.length()));

                    */
                    //Set the current title
                    ((TextView)detailViewHolder.trailerTitleTextView).setText(trailersTitle);

                    //Set or update the adapter on the reviews listView
                    if(trailersAdapter == null) {//Initialize the adapter
                        trailersAdapter = new MovieDetailAdapter(getActivity(), R.layout.trailer_item_layout);
                        trailersAdapter.setDrawable(MovieUtility.getSizedDrawable(R.drawable.play_trailer_icon, iconDpSize, iconDpSize, getActivity()));
                        trailersAdapter.setAdapterType(MovieDetailAdapter.trailerCode);
                        trailersAdapter.setNotifyOnChange(false);
                        detailViewHolder.trailersContainer.setAdapter(trailersAdapter);
                        //pass the onclick listener to the adapter
                        trailersAdapter.setOnclickListener(movieDetailTrailerOnItemClickListener);
                        trailersAdapter.setAdapterView(detailViewHolder.trailersContainer);
                    }
                    else{ //update the adapter
                        trailersAdapter.setNotifyOnChange(false);
                        trailersAdapter.clear();
                    }

                    //Populate the adapter with the new data
                    int currTrailerIndex = 0;
                    for (String trailerName : trailersNames) {
                        //trailersAdapter.add("Trailer "+currTrailerIndex);
                        currTrailerIndex++;
                        trailersAdapter.add(currTrailerIndex + " - " + trailerName);
                    }

                    trailersAdapter.setItemMetaData(trailersSizes);

                    trailersAdapter.notifyDataSetChanged();
                    trailersAdapter.setNotifyOnChange(true);

                    detailViewHolder.trailersContainer.setSelection(trailerListFirstVisiblePosition);
                    //detailViewHolder.trailersContainer.requestFocusFromTouch();
                    //detailViewHolder.trailersContainer.setItemChecked(trailerListFirstVisiblePosition, false);
                    trailersLoaded = true;

                    if(!movieDetailsLoaded)
                      movieDetailsLoaded = (reviewsLoaded && primaryDetailsLoaded && trailersLoaded);

                    updateMainListMetaData();

                    if(!isFavorite && !movieDetailsLoaded)
                    {
                        //call loadMoviesDetails before returning
                        loadMovieDetails();
                        return;
                    }
                }

                //If the current details failed to be properly loaded from the network, retry reloading the same details
                if(!isFavorite && detailsLoadingOrder.size() == remainingDetails && detailsLoadingOrder.size()!= 0)
                {


                    reloadMovieDetails(5000);
                }
            }
            else{

                //Wait 10 seconds before retrying.
                reloadMovieDetails(5000);

            }
        }

    }

    private void updateAttributes(HashMap<String, Object> newAttributes){

        if(currentDetails == null) {
            currentDetails = new HashMap<String, Object>();
            currentDetails.put(MovieUtility.getMOVIE_ID_ATTRIBUTE(getActivity()),movieId);
        }

        if(currentDetails != newAttributes)
        for(String attKey : newAttributes.keySet()){
            currentDetails.put(attKey, newAttributes.get(attKey));
        }
    }

    private void reloadMovieDetails(final long waitTime){

        movieDetailsLoaded = false; //we make sure the loaded state is correct before attempting to reload

        if(!trailersLoaded){
            mShareActionProvider.setShareIntent(new Intent(Intent.ACTION_SEND));
        }
        //Wait waitTime miliseconds before retrying.
        Thread reloader = new Thread(new Runnable(){
            public void run(){
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadMovieDetails();
            }
        });
        reloader.start();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if(!updatingCursor) { //Loading details from favorites


            Uri favoriteUri = FavoriteContract.FavoriteEntry.CONTENT_URI;

            //Prepare the select
            String selection = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID+"=?";
            String[] queryArgs = new String[1];
            queryArgs[0] = movieId;

            //if (movieCursorLoader == null) {
                movieCursorLoader = new CursorLoaderUpdater(getActivity(),
                        favoriteUri,
                        FAVORITE_DETAIL_PROJECTION,
                        selection,
                        queryArgs,
                        null);
                ((CursorLoaderUpdater)movieCursorLoader).setUpdatingCursor(updatingCursor);
            //}
        // final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = this;
         Thread notifier = new Thread(new Runnable(){
                          public void run(){
            //updateLock.unlock();
            synchronized (lockMonitor){
                lockMonitor.notify();
            }

        }
            }
            );
            notifier.start();
            return movieCursorLoader;
        }
        else{ // Updating favorites

            Uri favoriteUri = FavoriteContract.FavoriteEntry.CONTENT_URI;

            //if (movieCursorUpdater == null) {

                //Prepare the select
                String select = null;
                movieCursorUpdater = new CursorLoaderUpdater(getActivity(),
                        favoriteUri,
                        FAVORITE_DETAIL_PROJECTION,
                        select,
                        null,
                        null);
                ((CursorLoaderUpdater)movieCursorUpdater).setUpdatingCursor(updatingCursor);
                ((CursorLoaderUpdater) movieCursorUpdater).setMovieDetailsLoaderCallbacks(this);
            //}

                    Thread notifier = new Thread(new Runnable(){
                                     public void run(){
            //updateLock.unlock();
            synchronized (lockMonitor){
                lockMonitor.notify();
            }

                }
                         }
            );
            notifier.start();
            return movieCursorUpdater;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data !=null) {
            //Update the values of currMainList along with the boolean loading indicators and
            // call setAttributes with a hashMap built from data
            //The hashMap should be populated using the main list and its extension names
            HashMap<String, Object> favoriteDetails = new HashMap<String, Object>();

            if (data.moveToFirst()) { //We expect only a single row of data.
                // That's why we shouldn't loop
                //do {
                favoriteDetails.put(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()), data.getString(COL_IMAGE_PATH));
                favoriteDetails.put(MovieUtility.getMOVIE_RELEASE_DATE_ATTRIBUTE(getActivity()), data.getString(COL_DATE));
                favoriteDetails.put(MovieUtility.getMOVIE_DURATION_ATTRIBUTE(getActivity()), data.getInt(COL_DURATION));
                favoriteDetails.put(MovieUtility.getMOVIE_RATING_ATTRIBUTE(getActivity()), data.getDouble(COL_RATING));
                favoriteDetails.put(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()), data.getString(COL_TITLE));
                favoriteDetails.put(
                        MovieUtility.getMOVIE_SYPNOSIS_ATTRIBUTE(getActivity()), data.getString(COL_SYPNOSIS));
                String reviewsJsonStr = data.getString(COL_REVIEWS);

                LinkedList<String> reviewList = null;
                LinkedList<String> authors = null;
                try {
                    JSONArray reviewsJsonArray = MovieUtility.extractArray(reviewsJsonStr, MovieUtility.getMAIN_REVIEW_MOVIE_LIST_NAME(getActivity()));
                    reviewList = MovieUtility.extractAttributeList(reviewsJsonArray, MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()));
                    authors = MovieUtility.extractAttributeList(reviewsJsonArray, MovieUtility.getMOVIE_REVIEW_AUTHOR_ATTRIBUTE(getActivity()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //String[] reviewsSeparated = reviewsConcatenated.split(FavoriteContract.FavoriteEntry.COLUMN_FIELD_SEPARATOR);
                //LinkedList<String> reviewsList = new LinkedList<String>();
                //for(int i=0; i<reviewsSeparated.length; i++)
                //    reviewsList.add(reviewsSeparated[i]);
                favoriteDetails.put(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()), reviewList);
                favoriteDetails.put(MovieUtility.getMOVIE_REVIEW_AUTHOR_ATTRIBUTE(getActivity()), authors);

                String trailersJsonStr = data.getString(COL_TRAILERS);
                LinkedList<String> trailerKeysList = null;
                String keyAtt = MovieUtility.getMOVIE_TRAILER_KEY_ATTRIBUTE(getActivity());
                LinkedList<String> trailerNamesList = null;
                String nameAtt = MovieUtility.getMOVIE_TRAILER_NAME_ATTRIBUTE(getActivity());
                LinkedList<String> trailerSitesList = null;
                String siteAtt = MovieUtility.getMOVIE_TRAILER_SITE_ATTRIBUTE(getActivity());
                LinkedList<String> trailerSizesList = null;
                String sizeAtt = MovieUtility.getMOVIE_TRAILER_SIZE_ATTRIBUTE(getActivity());

                try {
                    JSONArray trailersJsonArray = MovieUtility.extractArray(trailersJsonStr, MovieUtility.getMAIN_VIDEO_MOVIE_LIST_NAME(getActivity()));
                    trailerKeysList = MovieUtility.extractAttributeList(trailersJsonArray, keyAtt);
                    trailerNamesList = MovieUtility.extractAttributeList(trailersJsonArray, nameAtt);
                    trailerSitesList = MovieUtility.extractAttributeList(trailersJsonArray, siteAtt);
                    trailerSizesList = MovieUtility.extractAttributeList(trailersJsonArray, sizeAtt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //favoriteDetails.put(MovieUtility.getMAIN_VIDEO_MOVIE_LIST_NAME(getActivity()), trailersList);
                favoriteDetails.put(keyAtt, trailerKeysList);
                favoriteDetails.put(nameAtt, trailerNamesList);
                favoriteDetails.put(siteAtt, trailerSitesList);
                favoriteDetails.put(sizeAtt, trailerSizesList);

                trailersLoaded = reviewsLoaded = primaryDetailsLoaded = false;

                setAttributes(favoriteDetails);
                //} while (data.moveToNext());
                //inserted = mContext.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, cvArray);
            }

        }
        else {//this was an update to the db

            if (updateResult == UPDATE_SUCCESS) {

                //detailViewHolder.favoriteButton.setText(MovieUtility.getMARK_FAVORITE_BUTTON_TEXT(getActivity()));

                int w = detailViewHolder.favoriteButton.getWidth();
                int h = detailViewHolder.favoriteButton.getHeight();
                detailViewHolder.favoriteButton.setLayoutParams(new LinearLayout.LayoutParams(w,h));

                //Set the text of the favorite button
                /*
                if(!isFavorite)
                    detailViewHolder.favoriteButton.setText(MovieUtility.getMARK_FAVORITE_BUTTON_TEXT(getActivity()));
                else
                    detailViewHolder.favoriteButton.setText(MovieUtility.getREMOVE_FAVORITE_BUTTON_TEXT(getActivity())+" ");
                */

                if (updateCode == DELETE) { //a delete operation was performed

                    //Remove the related data from the favorite data structures
                    //We must call the moviesGridFragment to perform the removal.
                    //int w = detailViewHolder.favoriteButton.getWidth();
                    //int h = detailViewHolder.favoriteButton.getHeight();
                    //detailViewHolder.favoriteButton.setLayoutParams(new LinearLayout.LayoutParams(w,h));

                    detailViewHolder.favoriteButton.setText(MovieUtility.getMARK_FAVORITE_BUTTON_TEXT(getActivity()));

                    //Each time we delete the movie details, we destroy the loader that was tracking the change to the details
                    getLoaderManager().destroyLoader(FAVORITE_DETAIL_LOADER);

                    isFavorite = false;

                } else if (updateCode == INSERT) { //an insert operation was performed
                    //int w = detailViewHolder.favoriteButton.getWidth();
                    //int h = detailViewHolder.favoriteButton.getHeight();
                    //detailViewHolder.favoriteButton.setLayoutParams(new LinearLayout.LayoutParams(w,h));
                    detailViewHolder.favoriteButton.setText(MovieUtility.getREMOVE_FAVORITE_BUTTON_TEXT(getActivity()));

                    //Every time we insert the movie details, we restart the loader to monitor the changes to the inserted details
                    //if(!isFavorite) {

                        updateLock.lock();
                        updatingCursor = false;
                        synchronized (lockMonitor) {
                            getLoaderManager().restartLoader(FAVORITE_DETAIL_LOADER, null, this);
                            try {
                                lockMonitor.wait(); //wait for the signal that the loader has been instanciated
                                updateLock.unlock(); //now free the update lock
                            } catch (InterruptedException iexp) {

                            }
                        }
                    //}

                    isFavorite = true;
                }

                updateFavoriteIcon();

                //Set the background color of the linear layout containing this button
                //View primaryDetailsContainer = getActivity().findViewById(R.id.primary_details_container);
                //primaryDetailsContainer.setBackgroundColor(getResources().getColor(R.color.background_color));
                //primaryDetailsContainer.invalidate();
                //updateCode = -1; //invalidate the updateCode

            }
            updateCode = -1; //invalidate the updateCode
            //Let's assign the default value to updateResult
            updateResult = 0;

            detailViewHolder.favoriteButton.setEnabled(true); //enable the button since it was disabled prior to the update
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    void resetMovieData(){

        //First discard the old adapters
        trailersAdapter = null;
        reviewsAdapter = null;

        //Initialize the review and trailer current indexes
        currTrailerIndex = 0;
        currReviewIndex = 0;

        reviewListFirstVisiblePosition = 0;
        trailerListFirstVisiblePosition = 0;

        movieDetailsLoaded = reviewsLoaded = primaryDetailsLoaded = trailersLoaded = false;

        currentDetails = null;
        //currMainList = null; //The default value is none
        //currMainListExtension = null; //The default value is none

        //detailsLoadingOrder = null;
        updatingCursor = false;
        updateCode = updateResult = 0;
    }


    public void updateMovieDetails(final Bundle args){

        if(args != null){
            movieId = args.getString(MovieUtility.getMOVIE_ID_ATTRIBUTE(getActivity()));
            isFavorite = (args.getString(MovieUtility.getSORT_FAVORITE(getActivity())) != null) ? true : false;
            imageRatio = args.getDouble(MovieUtility.getMOVIE_FAVORITE_ICON_KEY(getActivity()));
            resetMovieData();
        }

        if(args.keySet().size() == 3)
        {//Load details from favorites
                //Kick off the restart of the loader in a new handler
                //To ensure that the notification for the following wait() in the UI thread will always be done in a different thread

                                     updateLock.lock();

                                     isFavorite = true;

                                     movieId = args.getString(MovieUtility.getMOVIE_ID_ATTRIBUTE(getActivity()));

                                     updatingCursor = false;
                                     synchronized (lockMonitor) {
                                     getLoaderManager().restartLoader(FAVORITE_DETAIL_LOADER, null, this);
                                         try{
                                             lockMonitor.wait(); //wait for the signal that the loader has been instanciated
                                             updateLock.unlock(); //now free the update lock
                                         }
                                         catch(InterruptedException iexp){

                                         }
                                     }

        }
        else if(args.keySet().size() == 2)
        {//Load details from Network

            //First destroy any incompatible active loader (the loader that loads favorite movies)
            getLoaderManager().destroyLoader(FAVORITE_DETAIL_LOADER);


            isFavorite = false;
            //Initialize the review and trailer current indexes
            currTrailerIndex = 0;
            currReviewIndex = 0;

            detailsLoadingOrder = null;
            if(detailsLoadingOrder == null)
                setDetailsLoadingOrder();

            updateMainListMetaData();
            //if(!isFavorite) {
                //HashMap<String, String> urlParameters = new HashMap<String, String>();
                //urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()), MovieUtility.getAPI_KEY(getActivity()));

            isFavorite = false;

                loadMovieDetails();
            //}
        }

        //Set the text of the favorite button
        if(!isFavorite)
            detailViewHolder.favoriteButton.setText(" "+MovieUtility.getMARK_FAVORITE_BUTTON_TEXT(getActivity())+" ");
        else
            detailViewHolder.favoriteButton.setText(" "+MovieUtility.getREMOVE_FAVORITE_BUTTON_TEXT(getActivity())+" ");

    }

    @Override
    public boolean isUpdate() {
        return updatingCursor;
    }

    //This will be executed in the background thread
    @Override
    public void executeBackgroundUpdate() {

        try {
            Uri favoriteUri = FavoriteContract.FavoriteEntry.CONTENT_URI;
            if (updateCode == DELETE) {
                //Perform delete operation
                String selection = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID+"=?";
                String[] args = new String[1];
                args[0] = movieId;
                int deleteRes = getActivity().getContentResolver().delete(favoriteUri,selection,args);
                updateResult = UPDATE_SUCCESS;
            } else if (updateCode == INSERT) { //Perform insert operation
                ContentValues detailValues = new ContentValues();
                //Fill the detail values. Construct the json strings of reviews and trailers before insertion
                //the column values can be added to the contentValues object in any order.

                //First construct and add the review json string
                //...
                String reviewContentPrepend = MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()) ;
                String reviewAuthorPrepend = MovieUtility.getMOVIE_REVIEW_AUTHOR_ATTRIBUTE(getActivity()) ;
               String reviewJsonString = null;
               JSONArray reviewsJSONArray = new JSONArray();
               if(reviewsList != null && reviewsAuthors != null)
                for(int i=0; i< reviewsList.size(); i++){
                    JSONObject currReview = new JSONObject();
                    currReview.put(reviewContentPrepend, reviewsList.get(i));
                    currReview.put(reviewAuthorPrepend, reviewsAuthors.get(i));
                    reviewsJSONArray.put(currReview);
                    /*
                    reviewJsonString += "{"+reviewObjectPrepend+quoteString(reviewsList.get(i))+"}";
                    if(i<reviewsList.size()-1)
                        reviewJsonString+=",";
                    */
                }

                JSONObject reviewsObject = new JSONObject();
                reviewsObject.put(MovieUtility.getMAIN_REVIEW_MOVIE_LIST_NAME(getActivity()), reviewsJSONArray);
                //reviewJsonString += "]}";
                reviewJsonString = reviewsObject.toString();

                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_REVIEWS, reviewJsonString);
                //Construct and add the trailer json string
                //...
                String trailerJsonString = null;
                String keyPrepend = MovieUtility.getMOVIE_TRAILER_KEY_ATTRIBUTE(getActivity());
                String namePrepend = MovieUtility.getMOVIE_TRAILER_NAME_ATTRIBUTE(getActivity());
                String sitePrepend = MovieUtility.getMOVIE_TRAILER_SITE_ATTRIBUTE(getActivity());
                String sizePrepend = MovieUtility.getMOVIE_TRAILER_SIZE_ATTRIBUTE(getActivity());
                JSONArray trailersArray = new JSONArray();

                if(trailersKeys != null &&
                        trailersNames != null &&
                        trailersSites != null &&
                        trailersSizes != null)
                for(int i=0; i< trailersKeys.size(); i++){

                    JSONObject currTrailer = new JSONObject();
                    currTrailer.put(keyPrepend, trailersKeys.get(i));
                    currTrailer.put(namePrepend, trailersNames.get(i));
                    currTrailer.put(sitePrepend, trailersSites.get(i));
                    currTrailer.put(sizePrepend, trailersSizes.get(i));


                    trailersArray.put(currTrailer);
                    /*
                    trailerJsonString += "{"+keyPrepend+quoteString(trailersKeys.get(i))+","
                                         +namePrepend+quoteString(trailersNames.get(i))+","
                                         +sitePrepend+quoteString(trailersSites.get(i))+","
                                         +sizePrepend+quoteString(trailersSizes.get(i))+"}";

                    if(i < trailersKeys.size()-1)
                        trailerJsonString += ",";
                    */

                }

                JSONObject trailersObject = new JSONObject();
                trailersObject.put(MovieUtility.getMAIN_VIDEO_MOVIE_LIST_NAME(getActivity()),trailersArray);

                trailerJsonString = trailersObject.toString();

                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_TRAILERS, trailerJsonString);
                //add the remaining column values
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_IMAGE_PATH, (String)currentDetails.get(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity())));
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_DATE,(String)currentDetails.get(MovieUtility.getMOVIE_RELEASE_DATE_ATTRIBUTE(getActivity())) );
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_Duration, (String)(""+currentDetails.get(MovieUtility.getMOVIE_DURATION_ATTRIBUTE(getActivity()))));
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, (String)(""+currentDetails.get(MovieUtility.getMOVIE_ID_ATTRIBUTE(getActivity()))));
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, (String)currentDetails.get(MovieUtility.getMOVIE_SYPNOSIS_ATTRIBUTE(getActivity())));
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_RATING, (String)(""+currentDetails.get(MovieUtility.getMOVIE_RATING_ATTRIBUTE(getActivity()))));
                detailValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, (String)currentDetails.get(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity())));


                //Perform the insertion
                getActivity().getContentResolver().insert(favoriteUri,detailValues);

                updateResult = UPDATE_SUCCESS;
            }

            //The following line of code was removed because it will often put the updateResult variable
            // in an inconsistent state during startup if done at this point
            //updateResult = UPDATE_SUCCESS;
        }
        catch(Exception exp){

            updateResult = UPDATE_FAILURE;
        }

    }

    public void updateFavoriteIcon(){
        //Set the Favorite icon
        int favoriteIconPxSize = (int)MovieUtility.convertDpToPixel(favoriteIconDpSize, getActivity());
        Drawable sizedIconDrawable = null;
        int backGroundResource = (isFavorite) ? R.drawable.favorite_icon : R.drawable.un_favorite_icon;
        if(isFavorite)
            sizedIconDrawable = MovieUtility.getSizedDrawable(R.drawable.favorite_icon, favoriteIconDpSize, favoriteIconDpSize, getActivity());
        else
            sizedIconDrawable = MovieUtility.getSizedDrawable(R.drawable.un_favorite_icon, favoriteIconDpSize, favoriteIconDpSize, getActivity());


        //This method is called as a result of the user clicking the favorite button.
        //So we can be sure that the sizes of the button are already set; because the UI is
        //already drawn. So we can just get the favorite button and assigned it to the favorite icon
        int h = detailViewHolder.favoriteButton.getHeight();
        detailViewHolder.favoriteIconImageView.setBackgroundResource(backGroundResource);

        //if(h != 0)
        if(detailViewHolder.upTrailerButton == null)
        { // we are on the phone; we are not on a tablet
            if(h==0){
                h = (int)MovieUtility.convertDpToPixel(20,getActivity());
            }
                detailViewHolder.favoriteIconImageView.setLayoutParams(new LinearLayout.LayoutParams(
                                favoriteIconPxSize, h)
                );
        }
        else
            detailViewHolder.favoriteIconImageView.setLayoutParams(new LinearLayout.LayoutParams(
                            favoriteIconPxSize,favoriteIconPxSize)
            );

    }

    private String quoteString(String stringToQuote){

        return "\""+stringToQuote+"\"";
    }

    public class DetailViewHolder{
        final private TextView titleTextView;
        final private ImageView movieImageView;
        final private TextView yearTextView;
        final private TextView durationTextView;
        final private TextView ratingTextView;
        final private TextView sypnosisTextView;
        final private TextView trailerTitleTextView;
        final private TextView reviewTitleTextView;
        final private Button favoriteButton;
        final ListView reviewsContainer;
        final ListView trailersContainer;
        final private ImageView favoriteIconImageView;
        private ImageButton upTrailerButton;
        private ImageButton downTrailerButton;
        private ImageButton upReviewButton;
        private ImageButton downReviewButton;

        public ViewGroup container;
        //final private ScrollView scrollView;

        private DetailViewHolder(View detailContainer, Context context){
            container = (ViewGroup)detailContainer;
            this.titleTextView = (TextView)detailContainer.findViewById(R.id.title_textView);
            this.movieImageView = new CustomedImageView(context, 0.0f);//(ImageView)detailContainer.findViewById(R.id.movie_imageView);
            //Pass the container to the ImageView. It will add itself to the container when it will load the image
            ViewGroup imageContainer = (ViewGroup)detailContainer.findViewById(R.id.movie_imageView_container);
            ((CustomedImageView)movieImageView).setContainer(imageContainer);
            ViewGroup details_texts_container = (ViewGroup)detailContainer.findViewById(R.id.primary_details_texts_container);
            details_texts_container.setTag(detailContainer.findViewById(R.id.detail_inner_container));
            imageContainer.setTag(details_texts_container);

            //container = (ViewGroup)(detailContainer.findViewById(R.id.detail_inner_container)).getParent();
            //container.removeView(detailContainer.findViewById(R.id.detail_inner_container));

            this.yearTextView = (TextView)detailContainer.findViewById(R.id.year_textView);
            this.durationTextView = (TextView)detailContainer.findViewById(R.id.duration_textView);
            this.ratingTextView = (TextView)detailContainer.findViewById(R.id.rating_textView);
            this.sypnosisTextView = (TextView)detailContainer.findViewById(R.id.synopsis_textView);
            this.trailerTitleTextView = (TextView)detailContainer.findViewById(R.id.trailer_title_textView);
            this.reviewTitleTextView = (TextView)detailContainer.findViewById(R.id.review_title_textView);
            this.favoriteButton = (Button) detailContainer.findViewById(R.id.favorite_button);
                //Set the text of the favorite button
                if(!isFavorite)
                    favoriteButton.setText(" "+MovieUtility.getMARK_FAVORITE_BUTTON_TEXT(getActivity())+" ");
                else
                    favoriteButton.setText(" "+MovieUtility.getREMOVE_FAVORITE_BUTTON_TEXT(getActivity())+" ");

            favoriteButton.setOnClickListener(movieFavoriteButtonOnClickListener);

            this.reviewsContainer = (ListView)detailContainer.findViewById(R.id.reviews_container);
            reviewsContainer.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            this.trailersContainer = (ListView)detailContainer.findViewById(R.id.trailers_container);
            trailersContainer.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            this.favoriteIconImageView = (ImageView)detailContainer.findViewById(R.id.favorite_icon_imageView);
            //this.scrollView = (ScrollView)detailContainer.findViewById(R.id.movie_detail_scrollview);
            try {
                this.upTrailerButton = (ImageButton) detailContainer.findViewById(R.id.up_trailer_button);
                upTrailerButton.setOnClickListener(trailersListNavigatorListener);
                ((View) upTrailerButton.getParent()).setOnTouchListener(new MovieTouchListener());
                this.downTrailerButton = (ImageButton) detailContainer.findViewById(R.id.down_trailer_button);
                downTrailerButton.setOnClickListener(trailersListNavigatorListener);
                ((View) downTrailerButton.getParent()).setOnTouchListener(new MovieTouchListener());
                this.upReviewButton = (ImageButton) detailContainer.findViewById(R.id.up_review_button);
                upReviewButton.setOnClickListener(reviewsListNavigatorsListener);
                ((View) upReviewButton.getParent()).setOnTouchListener(new MovieTouchListener());
                this.downReviewButton = (ImageButton) detailContainer.findViewById(R.id.down_review_button);
                downReviewButton.setOnClickListener(reviewsListNavigatorsListener);
                ((View) downReviewButton.getParent()).setOnTouchListener(new MovieTouchListener());
            }catch(Exception exp){

            }
        }

    }

    private static final String[] FAVORITE_DETAIL_PROJECTION = {
            FavoriteContract.FavoriteEntry.COLUMN_TITLE,
            FavoriteContract.FavoriteEntry.COLUMN_IMAGE_PATH,
            FavoriteContract.FavoriteEntry.COLUMN_DATE,
            FavoriteContract.FavoriteEntry.COLUMN_Duration,
            FavoriteContract.FavoriteEntry.COLUMN_RATING,
            FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS,
            FavoriteContract.FavoriteEntry.COLUMN_TRAILERS,
            FavoriteContract.FavoriteEntry.COLUMN_REVIEWS
    };

    private static final int COL_TITLE = 0;
    private static final int COL_IMAGE_PATH = 1;
    private static final int COL_DATE =2;
    private static final int COL_DURATION = 3;
    private static final int COL_RATING = 4;
    private static final int COL_SYPNOSIS = 5;
    private static final int COL_TRAILERS = 6;
    private static final int COL_REVIEWS = 7;

    public class MovieTouchListener implements View.OnTouchListener {

        private boolean isActionDown = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Do stuff
                if(isActionDown)
                {
                    int buttonId = ((ViewGroup)v).getChildAt(0).getId();

                    if(buttonId == R.id.up_review_button){
                        reviewsListNavigatorsListener.onClick(detailViewHolder.upReviewButton);
                    }else if(buttonId == R.id.up_trailer_button){
                        trailersListNavigatorListener.onClick(detailViewHolder.upTrailerButton);
                    }else if(buttonId == R.id.down_review_button){
                        reviewsListNavigatorsListener.onClick(detailViewHolder.downReviewButton);
                    }
                    else if(buttonId == R.id.down_trailer_button){
                        trailersListNavigatorListener.onClick(detailViewHolder.downTrailerButton);
                    }
                }
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