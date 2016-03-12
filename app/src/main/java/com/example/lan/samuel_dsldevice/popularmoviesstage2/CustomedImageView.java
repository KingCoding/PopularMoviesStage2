package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

/**
 * Created by owner on 03/01/2016.
 * This custom ImageView can both be used to load a static image at the beginning of the
 * session before the onSizeChanged method gets called (by setting the URL before onSizeChanged is called)
 * or to load a dynamic image after the onSizeChangedMethod has been called (by calling the loadImage() method manually)
 */
public class CustomedImageView extends ImageView {

    private String imageUrl;
    private double imageRatio = 0.0f;
    private int imageWidth;
    private int imageHeight;
    private int maxImageAreaHeight;
    private boolean sizesComputed;
    private ViewGroup imageContainer;

    private boolean isFavoriteIcon; //decides if the icon is for a favorite movie or not

    private CustomedImageView favoriteIcon;

    private MovieDetailFragment fragment;

    public CustomedImageView(Context context, double imageRatio, int initialWidthLayout, int initialHeightLayout){

        super(context);

        this.imageRatio = imageRatio;

        //We set the layout of the imageView to the provided layout parameters
        FrameLayout.LayoutParams tempLayoutParams = new FrameLayout.LayoutParams(initialWidthLayout,initialHeightLayout);
        setLayoutParams(tempLayoutParams);

    }

    public CustomedImageView(Context context, double imageRatio){

        super(context);

        //Set the Layout
        //Use the match parent attributes for both with and height
        //...
        imageHeight = 0;

        this.imageRatio = imageRatio;

        //We set the layout of the imageView so that its height is zero and then the second call to setLayout with both math parent will be effective
        FrameLayout.LayoutParams tempLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        //tempLayoutParams.gravity = (Gravity.CENTER | Gravity.TOP);
        setLayoutParams(tempLayoutParams);

        //The above line can be improvised with the following line
        //setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        //So that we get the information about the available width and height in onSizeChanged to avoid
        //sizing a picture that exceeds the available height.

    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){

        if(w!=0&&h!=0){

            if(imageRatio > 0) {
                //compute the dimensions for the image area and load the image if appropriate
                imageWidth = w;

                maxImageAreaHeight = h;

                int expectedHeight = (int) (imageRatio * imageWidth);

                if (expectedHeight <= maxImageAreaHeight) {

                    imageHeight = expectedHeight;
                } else {

                    imageHeight = maxImageAreaHeight;

                    //adjust imageWidth to keep aspect ratio
                    imageWidth = (int) ((double) imageHeight / imageRatio);
                }

            }
            else if(imageRatio < 0){
                imageWidth = w;
                imageHeight = h;
            }
            loadImage();
        }

        /*else if(w!=0){
            //Set the height based on the ratio
            imageWidth = w;
            imageHeight = (int) (imageWidth*imageRatio);
            //setLayoutParams(new FrameLayout.LayoutParams(imageWidth,imageHeight));
        }
        */
    }

    public void setLayoutAndComputeImageDimensions(){

        //The following line is used to reset the layout of the imageView to its default
        //FrameLayout.LayoutParams tempLayoutParams = new FrameLayout.LayoutParams(0,0);

        //setLayoutParams(tempLayoutParams);
        if(sizesComputed){
            loadImage();
        }
        else {
            FrameLayout.LayoutParams finalLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            //finalLayoutParams.gravity = (Gravity.CENTER | Gravity.TOP);
            setLayoutParams(finalLayoutParams);
        }

        //((View)((View)(imageContainer.getTag())).getTag()).setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    public void loadImage(){

        //Compute the appropriate dimensions for the image
        //int imageWidth = 0;
        //int imageHeight = 0;
        //imageWidth will always be greater than 0 at this point because the primary call to setLayoutParams
        //in the constructor passed the width through the onSizeChanged method
        //if(imageHeight == 0 /* && imageWidth != 0  && imageRatio != 0 */) {
            //imageHeight = (int) (imageWidth * imageRatio);
            //setLayoutParams(new FrameLayout.LayoutParams(imageWidth,imageHeight));

            //setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        //}

        if(//fragment != null && fragment.detailViewHolder != null && fragment.detailViewHolder.container != null &&
           imageUrl != null && (imageWidth>0 && imageHeight >0) && imageContainer!=null
                //&& imageContainer.getTag() != null
                ) {

            Picasso.with(getContext()).load(imageUrl).resize(imageWidth, imageHeight).into(this);
            sizesComputed = true;
            //Make sure the image is positioned at the top
            FrameLayout.LayoutParams finalLayoutParams = new FrameLayout.LayoutParams(imageWidth,imageHeight);
            finalLayoutParams.gravity = (Gravity.LEFT | Gravity.TOP);
            setLayoutParams(finalLayoutParams);
            LinearLayout.LayoutParams finalContainerLayoutParams =
                    //new LinearLayout.LayoutParams(imageWidth,imageHeight);
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            finalContainerLayoutParams.gravity = (Gravity.LEFT | Gravity.TOP);
            imageContainer.setLayoutParams(finalContainerLayoutParams);

            //Modify the layout of the parent of the image container
            ViewGroup imageContainerParent = (ViewGroup)imageContainer.getParent();
            LinearLayout.LayoutParams icpLp =
                    //new LinearLayout.LayoutParams(imageWidth,imageHeight);
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageContainerParent.setLayoutParams(icpLp);

            final View detailsContainer = (View)imageContainer.getTag();

            if(detailsContainer != null) {


                //Add an empty view under the image container to
                // help make it stable
                Button stabilizer = new Button(imageContainer.getContext());
                stabilizer.setBackgroundColor(Color.parseColor("#CDDC39"));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0x0, 1);
                //lp.gravity = (Gravity.BOTTOM);
                stabilizer.setLayoutParams(lp);
                //imageContainer.addView(stabilizer,1);

                detailsContainer.setLayoutParams(
                        new LinearLayout.LayoutParams(0x0, LinearLayout.LayoutParams.MATCH_PARENT, 1)
                );

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // do UI work
                        View image = imageContainer.getChildAt(0);
                        int imageHeight = 0;

                        do {
                            if (image != null)
                                imageHeight = image.getHeight();

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            ViewGroup container = fragment.detailViewHolder.container;
                            if (container != null) {
                                ViewGroup parent = (ViewGroup) container.getParent();

                                //Force the parent layout to be laid during the next layout pass following the next addView
                                parent.forceLayout();

                                parent.removeView(container);

                                fragment.detailViewHolder.container = parent;

                                // Let`s change the layout of a child when the hierarchy is removed.
                                // Since its layout params are relative to that of its parent and thus can affect sibling views,
                                // that will cause its layout and that of its parent to be automatically relaid
                                // when the hierarchy will be added back (NOT SO SURE)
                                if(detailsContainer != null)
                                    detailsContainer.setLayoutParams(
                                            new LinearLayout.LayoutParams(0, 0)
                                    );

                                if(detailsContainer != null)
                                    detailsContainer.setLayoutParams(
                                            new LinearLayout.LayoutParams(0x0, LinearLayout.LayoutParams.MATCH_PARENT, 1)
                                    );
                                //((View)detailsContainer.getTag()).requestLayout();
                                fragment.detailViewHolder.container.addView((View) (detailsContainer.getTag()));

                                parent.forceLayout();
                            }
                            //Make sure the loadImage method is not called as a result of the above modification to the layout
                            imageUrl = null;
                            fragment.detailViewHolder.container = null;
                            imageContainer.setTag(null);

                        } while (imageHeight == 0);

                        }
                    });
                    //((View)detailsContainer.getTag()).setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            }

    }
    }

    private void setIcon(){

    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setContainer(ViewGroup imageContainer){

        this.imageContainer = imageContainer;
        imageContainer.addView(this);
    }

    public void setImageRatio(double imageRatio){
        this.imageRatio = imageRatio;
    }

    public void setImageType(boolean isFavoriteIcon){
        this.isFavoriteIcon = isFavoriteIcon;
    }

    public void setFragment(MovieDetailFragment fragment){

        this.fragment = fragment;
    }

}
