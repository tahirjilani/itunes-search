package com.tj.itunessearch.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Iterator;
import java.util.List;

public class SearchResponse {

    @SerializedName("resultCount")
    @Expose
    private Integer resultCount;

    @SerializedName("results")
    @Expose
    private List<Track> results;

    public Integer getResultCount() {
        return resultCount;
    }

    public List<Track> getResults() {
        return results;
    }

    /*
    *
    * Find track by trackId
    * **/
    public Track getTrack(int trackId){

        if (results != null){
            for (Iterator<Track> iterator = results.iterator(); iterator.hasNext(); ) {
                Track track = iterator.next();
                if (track != null){
                    if (track.getTrackId() == trackId){
                        return track;
                    }
                }
            }
        }
        return null;
    }

    /*
    *
    * Find next audio track
    *
    * */
    public Track getNextAudioTrack(Track previousTrack){

        if (results != null){

            int position = results.indexOf(previousTrack);
            if (position < results.size()-1 && position > -1) {
                for (int i = position + 1; i < results.size(); i++) {
                    Track track = results.get(i);
                    if (track.getKind() != null && !track.getKind().equals("feature-movie")) {
                        return track;
                    }
                }
            }
        }
        return null;
    }

    /*
    * Find preview audio track within the list
    * */
    public Track getPreviousAudioTrack(Track previousTrack){

        if (results != null){

            int position = results.indexOf(previousTrack);
            if (position > 0) {
                for (int i = position-1; i > 0; i--) {
                    Track track = results.get(i);
                    if (track.getKind() != null && !track.getKind().equals("feature-movie")) {
                        return track;
                    }
                }
            }
        }
        return null;
    }
}
