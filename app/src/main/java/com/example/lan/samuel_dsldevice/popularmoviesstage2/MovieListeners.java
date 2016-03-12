package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by owner on 24/10/2015.
 * Class used to declared all listener classes used by this application
 * It was implemented to help prevent any leaky instance associated to the listeners
 */
public class MovieListeners {



    public static class MovieDetailTrailerReviewsOnItemClickListener implements AdapterView.OnItemClickListener{

        MovieDetailFragment movieDetailsFragment;
        MovieDetailAdapter movieDetailAdapter;

        public MovieDetailTrailerReviewsOnItemClickListener(MovieDetailFragment movieDetailFragment) {

            this.movieDetailsFragment = movieDetailFragment;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(movieDetailAdapter.adapterType == movieDetailAdapter.trailerCode)
            {
                movieDetailsFragment.showCurrentTrailer(position);
            }
            else if(movieDetailAdapter.adapterType == movieDetailAdapter.reviewCode){
                movieDetailsFragment.showReview(position);
            }

        }

        public void setMovieDetailAdapter(MovieDetailAdapter movieDetailAdapter){

            this.movieDetailAdapter = movieDetailAdapter;
        }
    }

    public static class MoviesGridOnItemClickListener implements AdapterView.OnItemClickListener{

        MoviesGridFragment moviesGridFragment;

        public MoviesGridOnItemClickListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            moviesGridFragment.tempFirstPos = position;

            moviesGridFragment.firstVisiblePositions.put(moviesGridFragment.currPage, position);

            if(moviesGridFragment.isTablet){

                int currSelection = moviesGridFragment.currSelection;
                moviesGridFragment.currSelection = position;
                moviesGridFragment.currSelectionPage = moviesGridFragment.currPage;
                moviesGridFragment.currSelectionId = (moviesGridFragment.displayingFavorite)?
                        moviesGridFragment.favoriteIds.get(position) : moviesGridFragment.movieIds.get(position);
                moviesGridFragment.updateGridItemBackgroundForTablet(currSelection);
                moviesGridFragment.updateGridItemBackgroundForTablet(position);

            }


            String currId = moviesGridFragment.displayingFavorite? moviesGridFragment.favoriteIds.get(position) : moviesGridFragment.movieIds.get(position);
            Bundle args = new Bundle();
            args.putDouble(MovieUtility.getMOVIE_FAVORITE_ICON_KEY(moviesGridFragment.activity), moviesGridFragment.imageRatio);
            if(moviesGridFragment.favoriteIds!=null && moviesGridFragment.favoriteIds.contains(currId)) {
                args.putString(MovieUtility.getMOVIE_ID_ATTRIBUTE(moviesGridFragment.activity), currId);
                args.putString(MovieUtility.getSORT_FAVORITE(moviesGridFragment.activity), "true");
                        ((MovieUtility.MovieDetailInitiator) moviesGridFragment.activity).sendIntent(MovieUtility.MovieDetailInitiator.REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL, args);
            }
            else //The movie is not yet stored as favorite
            {
                //Bundle args = new Bundle();
                args.putString(MovieUtility.getMOVIE_ID_ATTRIBUTE(moviesGridFragment.activity), currId);
                ((MovieUtility.MovieDetailInitiator) moviesGridFragment.activity).sendIntent(MovieUtility.MovieDetailInitiator.REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL, args);
            }

        }
    }

    //onScrollListener for the gridView
    public static class MovieOnScrollListener implements AbsListView.OnScrollListener{

        MoviesGridFragment moviesGridFragment;

        boolean userTouched = false;
        public MovieOnScrollListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        @Override
        public void onScrollStateChanged (android.widget.AbsListView view,int scrollState){

            int i, j = 0 ;
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){

                /**/
            //if(scrollState == SCROLL_STATE_IDLE) {
            if ((moviesGridFragment.custGd != null && moviesGridFragment.movieAdapter != null)
                    && moviesGridFragment.movieAdapter.getCount() != 0) {

                //if(!moviesGridFragment.scrollListenerEnabled)
                moviesGridFragment.scrollListenerEnabled = true; //signal the start of the manual scrolling so that the first position begins to be recorded
                userTouched = true;
            }
                //Remove the scroll listener. It's no longer needed from this point. Otherwise it will add a lot of overload on the gridView at runtime
                //moviesGridFragment.custGd.setOnScrollListener(null);

                    /*
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            custGd.setSelection(tempFirstPos);
                            firstPos = tempFirstPos;
                        }
                    });
                    */
            }
            else if(!userTouched && (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)){

                moviesGridFragment.scrollListenerEnabled = true;

            }

        }

        @Override
        public void onScroll (android.widget.AbsListView view,int firstVisibleItem,
        int visibleItemCount, int totalItemCount){

            if (moviesGridFragment.scrollListenerEnabled) {

                moviesGridFragment.tempFirstPos = moviesGridFragment.firstPos =
                moviesGridFragment.custGd.getFirstVisiblePosition(); //Record the current first position
                moviesGridFragment.firstVisiblePositions.
                        put(moviesGridFragment.currPage, moviesGridFragment.firstPos); //Also record the first current position
                                                                                       // in the map of fisrt positions
            } else if ((moviesGridFragment.custGd != null && moviesGridFragment.movieAdapter != null)
                    && moviesGridFragment.movieAdapter.getCount() != 0) { //if(firstPos != custGd.getFirstVisiblePosition()){
                View first = moviesGridFragment.custGd.getChildAt(0);
                View veryLast = moviesGridFragment.custGd.getChildAt(moviesGridFragment.movieAdapter.count - 1);
                if (moviesGridFragment.custGd.getColumnsNumber() !=0 && ( (moviesGridFragment.firstPos / moviesGridFragment.custGd.getColumnsNumber())
                        != (firstVisibleItem / moviesGridFragment.custGd.getColumnsNumber()) )
                        ||
                        (! (first.getTop() == 0 ||
                                (veryLast != null && veryLast.getBottom() >= moviesGridFragment.custGd.getHeight() && first.getTop() < 0 )))
                        ) {

                    //This block will be executed repeatedly at the beginning after the creation of the gridView instance
                    // in order to adjust its selected position.
                    //moviesGridFragment.custGd.setSelection(moviesGridFragment.firstPos);
                    if(moviesGridFragment.displayingFavorite)
                        moviesGridFragment.custGd.setSelection(0);
                    else
                        moviesGridFragment.custGd.setSelection(moviesGridFragment.firstPos);
                }
            }
        }
    }


    //onClickListener for the buttons of the navigation bar
    public static class MovieButtonsOnClickListener implements View.OnClickListener{

        MoviesGridFragment moviesGridFragment;
        public MovieButtonsOnClickListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        @Override
        public void onClick(View v) {

            //moviesGridFragment.custGd.setOnScrollListener(null);

            if (v.getId() == R.id.button_prev) {
                if(moviesGridFragment.currPage <= 1){
                    Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                            "Page 1 is the first page",
                            Toast.LENGTH_SHORT).show();
                }
                else if(moviesGridFragment.currPage > 1001){
                    Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                            "Page 1000 is the last page",
                            Toast.LENGTH_SHORT).show();
                }
                else if (moviesGridFragment.currPage > 1) {
                    moviesGridFragment.currPage--;
                }

            } else if (v.getId() == R.id.button_next) {

                if(moviesGridFragment.currPage <0){
                    Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                            "Page 1 is the first page",
                            Toast.LENGTH_SHORT).show();
                }
                else if(moviesGridFragment.currPage > 999){
                    Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                            "Page 1000 is the last page",
                            Toast.LENGTH_SHORT).show();
                }else if (moviesGridFragment.currPage < 1000) {
                    moviesGridFragment.currPage++;
                }

            }
            //if(moviesGridFragment.isTablet)
            //moviesGridFragment.currSelection = -1;
            moviesGridFragment.firstPos = moviesGridFragment.tempFirstPos = 0; //reset the gridView position to the beginning
            moviesGridFragment.currentPageEnterred = ""+moviesGridFragment.currPage;
            moviesGridFragment.updateMovies();
            //moviesGridFragment.scrollListenerEnabled = false;
        }
    }

    //Editor Action Listener
    public static class MovieEditorActionListener implements EditText.OnEditorActionListener{

        MoviesGridFragment moviesGridFragment;
        public MovieEditorActionListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        private void hideSoftKeyboard(Activity activity){

            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(0, 0);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    ) {
                /*
                boolean isShiftKeyNotPressed;
                try{
                    isShiftKeyNotPressed = !event.isShiftPressed();
                }
                catch(Exception exp){
                    isShiftKeyNotPressed = true;
                }
                */

                //if (isShiftKeyNotPressed) {
                    // the user is done typing.
                    synchronized (moviesGridFragment.currentPageEnterred) {
                        String enterredText = ((EditText) v).getText().toString();
                        if(!moviesGridFragment.currentPageEnterred.equals(enterredText)) {
                            try {
                                int p = Integer.parseInt(enterredText);
                                if(p>0 && p<1001) {
                                    //if(moviesGridFragment.isTablet)
                                    //moviesGridFragment.currSelection = -1;
                                    moviesGridFragment.currentPageEnterred = enterredText;
                                    moviesGridFragment.currPage = p;
                                    moviesGridFragment.updateMovies();
                                }
                                else{
                                    Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                                            "You must enter and integer page number between 1 and 1000 to navigate to page",
                                            Toast.LENGTH_SHORT).show();
                                    ((TextView) v).setText(moviesGridFragment.currentPageEnterred);
                                }
                            } catch (Exception rte) {
                                Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                                        "You must enter and integer page number between 1 and 1000 to navigate to page",
                                        Toast.LENGTH_SHORT).show();
                                ((TextView) v).setText(moviesGridFragment.currentPageEnterred);
                            }

                            hideSoftKeyboard(moviesGridFragment.getAttachedActivity());
                            return true; // consume.

                        }
                    }
                //}
            }

            hideSoftKeyboard(moviesGridFragment.getAttachedActivity());

            return false; // pass on to other listeners.
        }


    }

    //onFocusChangeListener for the editText of the navigation bar
    public static class MovieEditTextOnFocusChangeListener implements View.OnFocusChangeListener{

        MoviesGridFragment moviesGridFragment;
        public MovieEditTextOnFocusChangeListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
                    /* When focus is lost check that the text field
                    * has valid values.
                    */
            if (!hasFocus) {
                synchronized (moviesGridFragment.currentPageEnterred) {
                    String enterredText = ((EditText) v).getText().toString();
                    if(!moviesGridFragment.currentPageEnterred.equals(enterredText)) {
                        try {
                            int p = Integer.parseInt(enterredText);
                            moviesGridFragment.currentPageEnterred = enterredText;
                            moviesGridFragment.currPage = p;
                            moviesGridFragment.updateMovies();
                        } catch (RuntimeException rte) {
                            Toast.makeText(moviesGridFragment.activity.getApplicationContext(),
                                    "You must enter and integer page number to navigate to page",
                                    Toast.LENGTH_SHORT).show();
                            ((TextView) v).setText(moviesGridFragment.currentPageEnterred);
                        }
                    }
            }
            }
        }
    };




    public static class MovieFavoriteButtonOnClickListener implements View.OnClickListener{

        MovieDetailFragment movieDetailFragment;
        public MovieFavoriteButtonOnClickListener(MovieDetailFragment movieDetailFragment) {

            this.movieDetailFragment = movieDetailFragment;
        }

        @Override
        public void onClick(View v) {

            String favoriteButtonText = ((Button)v).getText().toString().trim();

            boolean markFavorite = false;
            if(MovieUtility.getMARK_FAVORITE_BUTTON_TEXT(movieDetailFragment.getActivity())
                    .equals(favoriteButtonText))
                markFavorite = true;
            else if (MovieUtility.getREMOVE_FAVORITE_BUTTON_TEXT(movieDetailFragment.getActivity())
                    .equals(favoriteButtonText))
                    markFavorite = false;


            movieDetailFragment.updateFavoriteStatu(markFavorite);
        }
    }




public static class MovieReviewNavigationOnClickListener implements View.OnClickListener{

    ReviewDisplayFragment reviewDisplayFragment;
    public MovieReviewNavigationOnClickListener(ReviewDisplayFragment reviewDisplayFragment) {

        this.reviewDisplayFragment = reviewDisplayFragment;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_prev_review) {
            if(reviewDisplayFragment.currReviewIndex <= 1){
                Toast.makeText(reviewDisplayFragment.activity.getApplicationContext(),
                        "Review "+ 1+" is the first review",
                        Toast.LENGTH_SHORT).show();
            }
            else if(reviewDisplayFragment.currReviewIndex > reviewDisplayFragment.lastReviewIndex + 1){
                Toast.makeText(reviewDisplayFragment.activity.getApplicationContext(),
                        "Review "+ reviewDisplayFragment.lastReviewIndex+" is the first review",
                        Toast.LENGTH_SHORT).show();
            }
            else if (reviewDisplayFragment.currReviewIndex > 1) {
                reviewDisplayFragment.currReviewIndex--;
            }

        } else if (v.getId() == R.id.button_next_review) {

            if(reviewDisplayFragment.currReviewIndex <0){
                Toast.makeText(reviewDisplayFragment.activity.getApplicationContext(),
                        "Review "+ 1+" is the first review",
                        Toast.LENGTH_SHORT).show();
            }
            else if(reviewDisplayFragment.currReviewIndex > reviewDisplayFragment.lastReviewIndex-1){
                Toast.makeText(reviewDisplayFragment.activity.getApplicationContext(),
                        "Review "+ reviewDisplayFragment.lastReviewIndex+" is the last review",
                        Toast.LENGTH_SHORT).show();
            }else if (reviewDisplayFragment.currReviewIndex < reviewDisplayFragment.lastReviewIndex) {
                reviewDisplayFragment.currReviewIndex++;
            }

        }

        reviewDisplayFragment.updateReviewDisplayMessage();
    }
}

    public static class ListNavigators implements View.OnClickListener{

        MovieDetailFragment movieDetailFragment;

        public ListNavigators(MovieDetailFragment movieDetailFragment){

            this.movieDetailFragment = movieDetailFragment;
        }


        @Override
        public void onClick(View v) {

            int currPosition = 0;
            if(v.getId() == R.id.up_trailer_button){

                currPosition = movieDetailFragment.detailViewHolder.trailersContainer.getFirstVisiblePosition();

                View first = movieDetailFragment.detailViewHolder.trailersContainer.getChildAt(currPosition -
                        movieDetailFragment.detailViewHolder.trailersContainer.getFirstVisiblePosition());

                if(first != null) {
                    int top = first.getTop();
                    //int height = movieDetailFragment.detailViewHolder.trailersContainer.getHeight();
                    if (top < 0) //if the first position is not visible, we set it as selected
                        ;//movieDetailFragment.detailViewHolder.trailersContainer.setSelection(currPosition);
                    else {
                        if (currPosition == 0)
                            currPosition = movieDetailFragment.trailersNames.size() - 1;
                        else
                            currPosition = currPosition - 1;
                        //movieDetailFragment.reviewListFirstVisiblePosition += 1;
                    }

                    movieDetailFragment.detailViewHolder.trailersContainer.setSelection(currPosition);

                    movieDetailFragment.trailerListFirstVisiblePosition = currPosition;
                }
            }else if(v.getId() == R.id.up_review_button){
                currPosition = movieDetailFragment.detailViewHolder.reviewsContainer.getFirstVisiblePosition();

                View first = movieDetailFragment.detailViewHolder.reviewsContainer.getChildAt(currPosition -
                        movieDetailFragment.detailViewHolder.reviewsContainer.getFirstVisiblePosition());

                if(first != null) {
                    int top = first.getTop();
                    //int height = movieDetailFragment.detailViewHolder.trailersContainer.getHeight();
                    if (top < 0) //if the first position is not visible, we set it as selected
                        ;//movieDetailFragment.detailViewHolder.trailersContainer.setSelection(currPosition);
                    else {
                        if (currPosition == 0)
                            currPosition = movieDetailFragment.reviewsList.size() - 1;
                        else
                            currPosition = currPosition - 1;
                        //movieDetailFragment.reviewListFirstVisiblePosition += 1;
                    }

                    movieDetailFragment.detailViewHolder.reviewsContainer.setSelection(currPosition);

                    movieDetailFragment.reviewListFirstVisiblePosition = currPosition;
                }
            }else if(v.getId() == R.id.down_trailer_button){

                currPosition = movieDetailFragment.detailViewHolder.trailersContainer.getLastVisiblePosition();

                View last = movieDetailFragment.detailViewHolder.trailersContainer.getChildAt(currPosition -
                        movieDetailFragment.detailViewHolder.trailersContainer.getFirstVisiblePosition());
                if(last != null) {
                    int bottom = last.getBottom();
                    int height = movieDetailFragment.detailViewHolder.trailersContainer.getHeight();
                    if (bottom > height)
                        ;//movieDetailFragment.detailViewHolder.reviewsContainer.setSelection(currPosition);
                    else {
                        currPosition = (currPosition + 1) % movieDetailFragment.trailersNames.size();
                        //movieDetailFragment.reviewListFirstVisiblePosition += 1;
                    }
                    movieDetailFragment.detailViewHolder.trailersContainer.setSelection(currPosition);
                    movieDetailFragment.trailerListFirstVisiblePosition = currPosition;
                }
            }
            else if(v.getId() == R.id.down_review_button){

                currPosition = movieDetailFragment.detailViewHolder.reviewsContainer.getLastVisiblePosition();

                View last = movieDetailFragment.detailViewHolder.reviewsContainer.getChildAt(currPosition -
                        movieDetailFragment.detailViewHolder.reviewsContainer.getFirstVisiblePosition());

                if(last != null){
                    int bottom = last.getBottom();
                    int height = movieDetailFragment.detailViewHolder.reviewsContainer.getHeight();
                    if (bottom > height)
                        ;//movieDetailFragment.detailViewHolder.reviewsContainer.setSelection(currPosition);
                    else {
                        currPosition = (currPosition + 1) % movieDetailFragment.reviewsList.size();
                        //movieDetailFragment.reviewListFirstVisiblePosition += 1;
                    }
                    movieDetailFragment.detailViewHolder.reviewsContainer.setSelection(currPosition);
                    movieDetailFragment.reviewListFirstVisiblePosition = currPosition;
                }
            }

        }
    }

}