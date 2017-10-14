package com.example.parth.livetv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parth.livetv.ImageUrlAndLabel;
import com.example.parth.livetv.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Parth on 06-11-2016.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

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
    public PlaylistAdapter(ArrayList<ImageUrlAndLabel> imageUrlAndLabels,Context context){
        this.context = context;
        this.imageUrlAndLabelArrayList = imageUrlAndLabels;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUrlAndLabel imageUrlAndLabel = imageUrlAndLabelArrayList.get(position);
        holder.label.setText(imageUrlAndLabel.channelName);

        Picasso.with(context)
                .load(imageUrlAndLabel.url)
                .placeholder(R.drawable.tv_placeholder2)
                .error(R.drawable.tv_placeholder2)
                .into(holder.logo);

    }

    @Override
    public int getItemCount() {
        return imageUrlAndLabelArrayList.size();
    }


}
