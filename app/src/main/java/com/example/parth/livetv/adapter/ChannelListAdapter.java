package com.example.parth.livetv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parth.livetv.ChannelImageAndLabel;
import com.example.parth.livetv.ImageUrlAndLabel;
import com.example.parth.livetv.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Parth on 25-10-2016.
 */

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {
    private static final int DRAWABLEIMAGEADAPTER = 1;
    private static final int URLIMAGEADAPTER = 2;
    private int adapterType;
    private int lastPosition = -1;
    ArrayList<ChannelImageAndLabel> imageAndLabels;
    ArrayList<ImageUrlAndLabel> imageUrlAndLabelArrayList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView logo;

        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.label);
            logo = (ImageView) itemView.findViewById(R.id.logo);
            //all findViewById here
        }

    }

    public ChannelListAdapter(ArrayList<ChannelImageAndLabel> imageAndLabel, Context context) {
        this.imageAndLabels = imageAndLabel;
        this.context = context;
        adapterType = DRAWABLEIMAGEADAPTER;
    }

    public ChannelListAdapter(ArrayList<ImageUrlAndLabel> imageAndLabel, Context context, int x) {
        this.imageUrlAndLabelArrayList = imageAndLabel;
        this.context = context;
        adapterType = URLIMAGEADAPTER;

    }

    @Override
    public ChannelListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelListAdapter.ViewHolder holder, int position) {
        if (adapterType == DRAWABLEIMAGEADAPTER) {
            ChannelImageAndLabel imageAndLabel = imageAndLabels.get(position);

            holder.label.setText(imageAndLabel.channelName);
            if (imageAndLabel.channelName.equals("Live News")) {
                holder.label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_circle, 0, 0, 0);
            }
            holder.logo.setImageResource(imageAndLabel.logo);

        }else if(adapterType == URLIMAGEADAPTER){
            ImageUrlAndLabel imageUrlAndLabel = imageUrlAndLabelArrayList.get(position);
            holder.label.setText(imageUrlAndLabel.channelName);
            Picasso.with(context)
                    .load(imageUrlAndLabel.url)
                    .placeholder(R.drawable.tv_placeholder)
                    .error(R.drawable.tv_placeholder)
                    .into(holder.logo);
        }


            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

//        holder.coverImage.setImageResource(imageAndLabel.cover);
    }

    @Override
    public int getItemCount() {
        if(adapterType == DRAWABLEIMAGEADAPTER){
        return imageAndLabels.size();
        }else if(adapterType == URLIMAGEADAPTER){
            return imageUrlAndLabelArrayList.size();
        }else{
            return 4;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
