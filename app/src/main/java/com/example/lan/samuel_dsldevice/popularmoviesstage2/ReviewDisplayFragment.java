package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewDisplayFragment extends Fragment{


public int currReviewIndex; //first index is 1
public int lastReviewIndex;

public MovieListeners.MovieReviewNavigationOnClickListener movieReviewNavigationOnClickListener;

private String movieDetailTitle;
private ArrayList<String> reviewsAuthors;
private ArrayList<String> reviewsList;

private ReviewDisplayViewHolder reviewDisplayViewHolder;

private String reviewIconUrl;

public Activity activity;

    public ReviewDisplayFragment(){
        setRetainInstance(true);
        currReviewIndex = 1;
        movieReviewNavigationOnClickListener = new MovieListeners.MovieReviewNavigationOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review_display, container, false);

        //Set a reference to this fragment's activitty.
        activity = getActivity();

        Bundle args = getArguments();

        if(args != null && reviewsList == null) {
            currReviewIndex = args.getInt("" + R.id.review_display_title_textview);
            movieDetailTitle = args.getString(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()));
            reviewIconUrl = args.getString(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(activity));
            reviewsAuthors = args.getStringArrayList(""+R.id.reviewer_textview);

            reviewsList = args.getStringArrayList(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()));
            lastReviewIndex = reviewsList.size();
        }

            View reviewDetailContainer = (ViewGroup) rootView.findViewById(R.id.review_detail_container);

            reviewDisplayViewHolder = new ReviewDisplayViewHolder(reviewDetailContainer, activity);
            //movieId = args.getString(MovieUtility.getMOVIE_ID_ATTRIBUTE(getActivity()));

        return rootView;
    }



    @Override
    public void onStart(){
        super.onStart();
        updateViews();
    }

    private void updateViews(){

        //Set the movie detail title
        reviewDisplayViewHolder.reviewMovieTitleTextView.setText(movieDetailTitle);

        updateReviewDisplayMessage();

    }


    public void updateMovieReviews(Bundle args){

        if(args!=null) {
            currReviewIndex = args.getInt("" + R.id.review_display_title_textview);
            movieDetailTitle = args.getString(MovieUtility.getMOVIE_TITLE_ATTRIBUTE(getActivity()));
            reviewIconUrl = args.getString(MovieUtility.getMOVIE_POSTER_PATH_ATTRIBUTE(activity));
            ((CustomedImageView)reviewDisplayViewHolder.reviewIcon).setImageUrl(reviewIconUrl);
            ((CustomedImageView)reviewDisplayViewHolder.reviewIcon).loadImage();
            reviewsAuthors = args.getStringArrayList("" + R.id.reviewer_textview);

            reviewsList = args.getStringArrayList(MovieUtility.getMOVIE_REVIEW_CONTENT_ATTRIBUTE(getActivity()));
            lastReviewIndex = reviewsList.size();

            updateViews();
        }
    }


    public void updateReviewDisplayMessage(){

        //set the current review title
        String currReviewTitle = "Review "+currReviewIndex+" of "+lastReviewIndex;
        reviewDisplayViewHolder.reviewDisplayTitleTextview.setText(currReviewTitle);

        //Set the reviewer name
        String reviewer = null;
        if(reviewsAuthors != null)
            reviewer = reviewsAuthors.get(currReviewIndex - 1);
        reviewDisplayViewHolder.reviewerTextView.setText("Reviewed by: "+reviewer);

        //Set the current Review message
        if(reviewsList != null && reviewsList.size() > currReviewIndex-1)
        reviewDisplayViewHolder.reviewDisplayMessageTextview.setText(reviewsList.get(currReviewIndex-1));
    }

    private class ReviewDisplayViewHolder{

        final private TextView reviewMovieTitleTextView;
        final private Button buttonPrevReview;
        final private Button buttonNextReview;
        final private TextView reviewDisplayTitleTextview;
        final private TextView reviewDisplayMessageTextview;
        final private TextView reviewerTextView;
        final private ImageView reviewIcon;

        private ReviewDisplayViewHolder(View reviewDisplayContainer, Context context){
            reviewMovieTitleTextView = (TextView)reviewDisplayContainer.findViewById(R.id.review_movie_title_textView);
            buttonPrevReview = (Button) reviewDisplayContainer.findViewById(R.id.button_prev_review);
            buttonNextReview = (Button) reviewDisplayContainer.findViewById(R.id.button_next_review);
            buttonPrevReview.setOnClickListener(movieReviewNavigationOnClickListener);
            buttonNextReview.setOnClickListener(movieReviewNavigationOnClickListener);
            reviewDisplayTitleTextview = (TextView) reviewDisplayContainer.findViewById(R.id.review_display_title_textview);
            reviewerTextView = (TextView) reviewDisplayContainer.findViewById(R.id.reviewer_textview);
            reviewDisplayMessageTextview = (TextView) reviewDisplayContainer.findViewById(R.id.review_display_message_textview);
            reviewIcon = new CustomedImageView(context,-1.0f, FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT); //An image ratio of -1 means the image will occupy all its available area
            ((CustomedImageView)reviewIcon).setImageUrl(reviewIconUrl);
            ViewGroup reviewIconContainer = ((FrameLayout)reviewDisplayContainer.findViewById(R.id.review_icon_container));
            ((CustomedImageView)reviewIcon).setContainer(reviewIconContainer);
        }
    }
}
