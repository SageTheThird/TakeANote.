package com.homie.takeanote.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.homie.roompersistence.R;
import com.homie.takeanote.Utils.UniversalImageLoader;


import java.util.List;

public class NewNotePagerAdapter extends PagerAdapter {
    private static final String TAG = "UltraPagerAdapter";

    private Context context;
    private List<String> walls_list;



    public NewNotePagerAdapter(List<String> walls_list, Context context) {

        this.context=context;
        this.walls_list = walls_list;

    }

   @Override
    public int getCount() {

       Log.d(TAG, "getCount: "+walls_list.size());
        return walls_list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }


    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {


        LayoutInflater mInflater = LayoutInflater.from(context);
        View item_view = mInflater.inflate(R.layout.pager_fullscreen, container, false);

        final ImageView imageView = item_view.findViewById(R.id.test_player_fullscreen_iv);
        final ProgressBar progressBar=item_view.findViewById(R.id.newNote_progressBar);



        //mUrlPosition=position;
        Log.d(TAG, "AdapterPosition: "+position);



        UniversalImageLoader.setImage(walls_list.get(position),imageView,progressBar,"");


        container.addView(item_view);
//        linearLayout.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, container.getContext().getResources().getDisplayMetrics());
//        linearLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, container.getContext().getResources().getDisplayMetrics());
        return item_view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);

    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container,position, object);

    }

    public void addNewImage(String image){
        walls_list.add(image);
        notifyDataSetChanged();
    }


}
