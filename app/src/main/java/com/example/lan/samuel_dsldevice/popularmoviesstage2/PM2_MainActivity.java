package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;


public class PM2_MainActivity extends ActionBarActivity implements MovieUtility.MovieDetailInitiator{

    private String DETAIL_EXTRA = "MOVIE_ID";
    private String movieGridFragmentTag = "movieGridFragmentTag";
    String movieDetailFragmentTag = "movieDetailFragmentTag";
    private String movieReviewFragmentTag = "movieReviewFragmentTag";
    private Fragment moviesGridFragment;
    Fragment movieDetailFragment;
    private Fragment reviewDisplayFragment;
    private HashMap<Fragment,Integer> fragmentContainersMapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pm2_main);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float wDp = MovieUtility.convertPixelsToDp(widthPixels, this);

        float hDp = MovieUtility.convertPixelsToDp(heightPixels, this);

        if(fragmentContainersMapper == null)
            fragmentContainersMapper = new HashMap<Fragment, Integer>();

        if (savedInstanceState == null) {

            //We add the movie grid fragment.
            moviesGridFragment = getSupportFragmentManager().findFragmentByTag(movieGridFragmentTag);
            if(moviesGridFragment == null)
                moviesGridFragment = new MoviesGridFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_grid_container, moviesGridFragment, movieGridFragmentTag)
                            //.addToBackStack("movieFragmentTag")
                    .commit();

            //Check if this is a tablet or a phone by relying on the loaded layout
            FrameLayout detailContainer = (FrameLayout)findViewById(R.id.movies_detail_container);

            if(detailContainer != null)
            { //We are on a tablet; we add the detail container

                ((MoviesGridFragment)moviesGridFragment).isTablet = true;
                //We add the movie grid fragment.
                movieDetailFragment = getSupportFragmentManager().findFragmentByTag(movieDetailFragmentTag);
                if(movieDetailFragment == null)
                    movieDetailFragment = new MovieDetailFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container, movieDetailFragment, movieDetailFragmentTag)
                                //.addToBackStack("movieFragmentTag")
                        .commit();
            }
            else
            { //We are on a phone. Nothing more should be done


            }

        }
        else{

            /*
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, moviesGridFragment, "movieFragmentTag")
                    .addToBackStack("movieFragmentTag")
                    .commit();

            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentByTag("movieFragmentTag")).commit();
            getSupportFragmentManager().popBackStack();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, moviesGridFragment, "movieFragmentTag")
                    .commit();
            */
            //ViewGroup layout = (ViewGroup) findViewById(R.id.container);

            //layout.requestLayout();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pm1__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, MovieSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    This method can be called either from moviesGridFragment or from MovieDetailfragment.
     */
    @Override
    public void sendIntent(int transactionCode, Bundle arguments){

        View detailContainer = findViewById(R.id.movies_detail_container);

        //Check if this is a tablet or a phone by relying on the loaded layout
        boolean isTablet = detailContainer!=null;

        Fragment replacerFragment = null;
        int containerId = -1;
        String replacerTag = null;
        boolean transactionDone = true;

        if(transactionCode == REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL) {
            transactionDone = false;
            //We are replacing the grid fragment with the detail fragment
            movieDetailFragment = getSupportFragmentManager().findFragmentByTag(movieDetailFragmentTag);
            if(movieDetailFragment == null)
                movieDetailFragment = new MovieDetailFragment();
            replacerFragment = movieDetailFragment;
            replacerTag = movieDetailFragmentTag;

            if(!isTablet) {
                containerId = R.id.movies_grid_container;
                movieDetailFragment.setArguments(arguments);
            }
            else{
                containerId = R.id.movies_detail_container;
                //((MovieDetailFragment)movieDetailFragment).updateMovieDetails(arguments);

                //if the fragment is already in its appropriate container, we mark the transaction as being done.
                transactionDone = ((ViewGroup)(((ViewGroup)detailContainer).getChildAt(0))).getChildAt(0).getId()== R.id.detail_inner_container;
            }


            /*
            if(isTablet &&
        /*and detailContainer is not null and it already contains the detailFragment*/
        /*            ((ViewGroup)(((ViewGroup)detailContainer).getChildAt(0))).getChildAt(0).getId()== R.id.review_detail_container) {

                reviewDisplayFragment = getSupportFragmentManager().findFragmentByTag(movieReviewFragmentTag);

                //getSupportFragmentManager().beginTransaction().remove(reviewDisplayFragment).commit();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container , movieDetailFragment).addToBackStack(null).commit();
                try{
                    //getSupportFragmentManager().executePendingTransactions();
                }catch(Exception exp) {

                }
            }
            */
        }

        if(transactionCode == REPLACE_DETAIL_WITH_REVIEW) {
            transactionDone = false;
            //We are replacing the detail fragment with the review fragment
            reviewDisplayFragment = getSupportFragmentManager().findFragmentByTag(movieReviewFragmentTag);
            if(reviewDisplayFragment == null)
                reviewDisplayFragment = new ReviewDisplayFragment();
            replacerFragment = reviewDisplayFragment;
            replacerTag = movieReviewFragmentTag;

            if(!isTablet) {
                containerId = R.id.movies_grid_container ;
                reviewDisplayFragment.setArguments(arguments);
            }
            else
            {
                containerId = R.id.movies_detail_container;
                //((ReviewDisplayFragment)reviewDisplayFragment).updateMovieReviews(arguments);
            }

            /*
            if(isTablet) {
                movieDetailFragment = getSupportFragmentManager().findFragmentByTag(movieDetailFragmentTag);

                //getSupportFragmentManager().beginTransaction().remove(movieDetailFragment).commit();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container, reviewDisplayFragment).addToBackStack(null).commit();
                try{
                    //getSupportFragmentManager().executePendingTransactions();
                }catch(Exception exp) {

                }
            }
            else*/
        }


        //if(isTablet && transactionCode == REPLACE_DETAIL_WITH_REVIEW)
        //    ;
        //else
        //if(isTablet && transactionCode == REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL //&&
        /*and detailContainer is not null and it already contains the detailFragment*/
                //((ViewGroup)(((ViewGroup)detailContainer).getChildAt(0))).getChildAt(0).getId()== R.id.detail_inner_container
                //)

        //In this case, the fragment is already added to the tablet; just update it with the arguments
            //((MovieDetailFragment)movieDetailFragment).updateMovieDetails(arguments);
            /*
            Bundle args = new Bundle();
            args.putString(MovieUtility.getMOVIE_ID_ATTRIBUTE(this),intentData[0]);

            if(intentData.length == 2)
                args.putString(MovieUtility.getSORT_FAVORITE(this), intentData[1]);

            movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);
            */
        /*
        else{ //Tablet

            if(transactionCode == REPLACE_GRID_WITH_DETAIL)
                ((MovieDetailFragment)movieDetailFragment).updateMovieDetails(arguments);
            else if(transactionCode == REPLACE_DETAIL_WITH_REVIEW)


        }
        */

        //else

        if(!transactionDone){
 //review fragment gets added twice.
 //movieDetailFragment gets added at this point for tablets
            //getSupportFragmentManager().beginTransaction().remove(replacerFragment).commit();
            //getSupportFragmentManager().executePendingTransactions();
            //Let's perform the fragment replacement transaction according to the currently set parameters

            if(isTablet && transactionCode == REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL)
            { //We first empty the backStack if we are replacing with the detail fragment
              //and if we are on a tablet.
                FragmentManager fm = getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(containerId, replacerFragment, replacerTag);

            if(!isTablet){
                transaction.addToBackStack(null);
            }
            else if(transactionCode != REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL)
                transaction.addToBackStack(null);

            //fragmentContainersMapper.put(replacerFragment,containerId); //Map the added fragment to its container
            //replacerFragment
            // Commit the transaction
            transaction.commit();

            if(isTablet)
            try{
                getSupportFragmentManager().executePendingTransactions();
            }catch(Exception exp) {

            }

        }


        if(isTablet){

            if(transactionCode == REPLACE_DETAIL_WITH_REVIEW)
                ((ReviewDisplayFragment)reviewDisplayFragment).updateMovieReviews(arguments);
            else if(transactionCode == REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL)
                ((MovieDetailFragment)movieDetailFragment).updateMovieDetails(arguments);;
        }
    }


    public void resetFragment(Fragment fragmentToReset){

        //int containerID = MovieUtility.getIntSharedPreference(this, movieGridFragmentTag, -1);
        getSupportFragmentManager().beginTransaction().remove(fragmentToReset).commit();
        try{
            getSupportFragmentManager().executePendingTransactions();
        }catch(Exception exp) {

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_grid_container , fragmentToReset).commit();
    }


}
