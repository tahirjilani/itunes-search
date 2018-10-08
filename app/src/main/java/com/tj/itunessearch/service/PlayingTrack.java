package com.tj.itunessearch.service;

import com.tj.itunessearch.dataModel.Track;

public class PlayingTrack {
    public Track track;
    public boolean isPlaying;

    public PlayingTrack(Track track, boolean isPlaying) {
        this.track = track;
        this.isPlaying = isPlaying;
    }
}
