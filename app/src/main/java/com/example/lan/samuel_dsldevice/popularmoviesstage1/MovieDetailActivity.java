package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;


public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_parent_container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.detail_settings) {

            startActivity(new Intent(this, MovieSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements MovieTask.MovieTaskCallBackInstance{

        //private MovieTask movieTask;
        DetailViewHolder detailViewHolder;

        public PlaceholderFragment() {
            setRetainInstance(true); //We don't want this fragment instance to be killed while the background task is being executed
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            Intent intent = getActivity().getIntent();

            //We make sure the following is executed only when the fragment is first created
            //Not on screen configuration
            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){

                View detailInnerContainer = (ViewGroup)rootView.findViewById(R.id.detail_inner_container);
                //We initialize the view holder
                detailViewHolder = new DetailViewHolder(detailInnerContainer);

                MovieTask movieTask = new MovieTask(this);
                String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);

                HashMap<String, String> urlParameters = new HashMap<String, String>();
                urlParameters.put(MovieUtility.getAPI_KEY_NAME(getActivity()),MovieUtility.getAPI_KEY(getActivity()));

                URL url = movieTask.buildURL(MovieUtility.getBASE_URL(getActivity()), MovieUtility.getMOVIE_ENDPOINT(getActivity()) + movieId, urlParameters);

                movieTask.execute(url);

            }

            return rootView;
        }

        @Override
        public String getMainListName() {
            return null; //There is no list of JSON objects associated with this task callback instance
        }

        @Override
        public LinkedList<String> getAttributesNames() {

            LinkedList<String> attributesNames = new LinkedList<String>();

            attributesNames.add(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_RELEASE_DATE_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_DURATION_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_RATING_ATTRIBUTE(getActivity()));
            attributesNames.add(MovieUtility.getMOVIE_SYPNOSIS_ATTRIBUTE(getActivity()));

            return attributesNames;
        }

        @Override
        /*
        This implementation expects values of the hashMap to be of type String
         */
        public void setAttributes(HashMap<String, Object> attributes) {

            if(detailViewHolder != null)
            {
                detailViewHolder.titleTextView.setText((String)attributes.get(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity())));

                int currImageSizeRange = 3;
                String currUrl =  MovieUtility.getPOSTER_PATH_BASE_URL(getActivity())
                                + MovieUtility.getImageSizeKey(currImageSizeRange, getActivity())
                                + attributes.get(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(getActivity()));
                Picasso.with(getActivity()).load(currUrl).into(detailViewHolder.movieImageView);

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
                detailViewHolder.durationTextView.setText((String)attributes.get(MovieUtility.getMOVIE_DURATION_ATTRIBUTE(getActivity()))+getActivity().getString(R.string.movie_duration_unit));
                detailViewHolder.ratingTextView.setText((String)attributes.get(MovieUtility.getMOVIE_RATING_ATTRIBUTE(getActivity())));
                detailViewHolder.sypnosisTextView.setText((String)attributes.get(MovieUtility.getMOVIE_SYPNOSIS_ATTRIBUTE(getActivity())));
            }
        }


        public static class DetailViewHolder{
            final private TextView titleTextView;
            final private ImageView movieImageView;
            final private TextView yearTextView;
            final private TextView durationTextView;
            final private TextView ratingTextView;
            final private TextView sypnosisTextView;
            //final private ScrollView scrollView;

            private DetailViewHolder(View detailContainer){
                this.titleTextView = (TextView)detailContainer.findViewById(R.id.title_textView);
                this.movieImageView = (ImageView)detailContainer.findViewById(R.id.movie_imageView);
                this.yearTextView = (TextView)detailContainer.findViewById(R.id.year_textView);
                this.durationTextView = (TextView)detailContainer.findViewById(R.id.duration_textView);
                this.ratingTextView = (TextView)detailContainer.findViewById(R.id.rating_textView);
                this.sypnosisTextView = (TextView)detailContainer.findViewById(R.id.synopsis_textView);
                //this.scrollView = (ScrollView)detailContainer.findViewById(R.id.movie_detail_scrollview);
            }

        }
    }
}
