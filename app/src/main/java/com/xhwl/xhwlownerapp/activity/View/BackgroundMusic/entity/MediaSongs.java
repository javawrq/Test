package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/16.
 */

public class MediaSongs implements Serializable {
    private int songsId;
    private String songName;

    public int getSongsId() {
        return songsId;
    }

    public void setSongsId(int songsId) {
        this.songsId = songsId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
