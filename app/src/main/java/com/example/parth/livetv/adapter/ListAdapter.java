package com.example.parth.livetv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parth.livetv.ChannelImageAndLabel;
import com.example.parth.livetv.R;

import java.util.ArrayList;

/**
 * Created by Parth on 11-11-2016.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    ArrayList<ChannelImageAndLabel> channelImageAndLabels;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView logo;
        View wrapper;

        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.label);
            logo = (ImageView) itemView.findViewById(R.id.logo);
//            wrapper = itemView.findViewById(R.id.wrapper_list);
            //all findViewById here
        }

    }

    public ListAdapter(ArrayList<ChannelImageAndLabel> imageUrlAndLabels, Context context) {
        this.context = context;
        this.channelImageAndLabels = imageUrlAndLabels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_list, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChannelImageAndLabel channelImageAndLabel = channelImageAndLabels.get(position);
        holder.label.setText(channelImageAndLabel.channelName);
        holder.logo.setImageResource(channelImageAndLabel.logo);

    }

    @Override
    public int getItemCount() {
        return channelImageAndLabels.size();
    }


}
