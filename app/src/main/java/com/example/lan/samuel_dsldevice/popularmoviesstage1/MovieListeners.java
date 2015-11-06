package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by owner on 24/10/2015.
 * Class used to declared all listener classes used by this application
 * It was implemented to help prevent any leaky instance associated to the listeners
 */
public class MovieListeners {



    public static class MoviesGridOnItemClickListener implements AdapterView.OnItemClickListener{

        MoviesGridFragment moviesGridFragment;

        public MoviesGridOnItemClickListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            moviesGridFragment.tempFirstPos = position;
            ((MoviesGridFragment.MovieDetailInitiator) moviesGridFragment.activity).sendIntent(moviesGridFragment.movieIds.get(position));
        }
    }

    //onScrollListener for the gridView
    public static class MovieOnScrollListener implements AbsListView.OnScrollListener{

        MoviesGridFragment moviesGridFragment;
        public MovieOnScrollListener(MoviesGridFragment moviesGridFragment) {

            this.moviesGridFragment = moviesGridFragment;
        }

        @Override
        public void onScrollStateChanged (android.widget.AbsListView view,int scrollState){

                /**/
            //if(scrollState == SCROLL_STATE_IDLE) {
            if ((moviesGridFragment.custGd != null && moviesGridFragment.movieAdapter != null)
                    && moviesGridFragment.movieAdapter.getCount() != 0) {

                //if(!moviesGridFragment.scrollListenerEnabled)
                moviesGridFragment.scrollListenerEnabled = true; //signal the start of the manual scrolling so that the first position begins to be recorded

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

                if ((moviesGridFragment.firstPos / moviesGridFragment.custGd.getColumnsNumber())
                        != (firstVisibleItem / moviesGridFragment.custGd.getColumnsNumber())) {
                    //This block will be executed repeatedly at the beginning after the creation of the gridView instance
                    // in order to adjust its selected position.
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
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                boolean isShiftKeyNotPressed;
                try{
                    isShiftKeyNotPressed = !event.isShiftPressed();
                }
                catch(Exception exp){
                    isShiftKeyNotPressed = true;
                }


                if (isShiftKeyNotPressed) {
                    // the user is done typing.
                    synchronized (moviesGridFragment.currentPageEnterred) {
                        String enterredText = ((EditText) v).getText().toString();
                        if(!moviesGridFragment.currentPageEnterred.equals(enterredText)) {
                            try {
                                int p = Integer.parseInt(enterredText);
                                if(p>0 && p<1001) {
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
                }
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

}
