package com.example.parth.livetv;

import java.io.Serializable;

/**
 * Created by Parth on 03-11-2016.
 */

public class ImageUrlAndLabel implements Serializable {
    public ImageUrlAndLabel(){

    }
   public ImageUrlAndLabel(String label,String url){
        this.channelName = label;
        this.url = url;
    }
   public String url;
    public String channelName;
}
