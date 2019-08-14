package com.homie.takeanote.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.homie.roompersistence.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class UniversalImageLoader {

    public static final int DEFAULT_IMAGE= R.drawable.ic_change;

    private Context mContext;

    public UniversalImageLoader(Context mContext) {
        this.mContext = mContext;
    }


    public ImageLoaderConfiguration getConfig(){

        DisplayImageOptions defaultOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(DEFAULT_IMAGE)
                .showImageForEmptyUri(DEFAULT_IMAGE)
                .considerExifParams(true)
                .showImageOnFail(DEFAULT_IMAGE)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration imageLoaderConfiguration=new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 *1024 *1024).build();

        return imageLoaderConfiguration;
    }
    public ImageLoaderConfiguration getConfigCards(){

        int width=mContext.getResources().getDisplayMetrics().widthPixels;
        final int cardWidth=width/3;
        DisplayImageOptions defaultOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(DEFAULT_IMAGE)
                .showImageForEmptyUri(DEFAULT_IMAGE)
                .considerExifParams(true)
                .showImageOnFail(DEFAULT_IMAGE)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return Bitmap.createScaledBitmap(bitmap,cardWidth,450,false);
                    }
                })
                .build();

        ImageLoaderConfiguration imageLoaderConfiguration=new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 *1024 *1024).build();

        return imageLoaderConfiguration;
    }

    public static void setImage(String url, final ImageView image, final ProgressBar progressBar, String append){

        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.handleSlowNetwork(true);
        imageLoader.displayImage(append + url, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(progressBar != null){
                    progressBar.setVisibility(View.VISIBLE);
                }
                image.setBackgroundResource(R.drawable.ic_change);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }




}
