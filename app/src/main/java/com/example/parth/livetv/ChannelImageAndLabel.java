package com.example.parth.livetv;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Parth on 25-10-2016.
 */

public class ChannelImageAndLabel implements Serializable {
   public ChannelImageAndLabel(){

   }
   public ChannelImageAndLabel(String label){
      channelName = label;
   }
   public String channelName;
   public int logo;
   public Bitmap cover;
}
