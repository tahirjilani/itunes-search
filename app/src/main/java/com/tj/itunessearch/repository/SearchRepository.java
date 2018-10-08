package com.tj.itunessearch.repository;

import android.arch.lifecycle.MutableLiveData;

import com.tj.itunessearch.dataModel.SearchResponse;
import com.tj.itunessearch.dataModel.Track;
import com.tj.itunessearch.executor.AppExecutors;
import com.tj.itunessearch.network.NetworkState;
import com.tj.itunessearch.network.Webservice;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class SearchRepository {

    private MutableLiveData<SearchResponse> searchResponse = null;
    private String searchTerm = null;

    private Webservice webservice;
    private AppExecutors appExecutors;

    private MutableLiveData<NetworkState> networkCallState = null;

    @Inject
    public SearchRepository(Webservice webservice, AppExecutors appExecutors){

        this.webservice = webservice;
        this.appExecutors = appExecutors;

        this.searchResponse = new MutableLiveData<>();
        this.networkCallState = new MutableLiveData<>();
    }

    public String getSearchTerm(){
        return searchTerm;
    }

    public MutableLiveData<SearchResponse> search(final String term){

        this.searchTerm = term;
        this.appExecutors.networkIO().execute(() -> {

            networkCallState.postValue(NetworkState.LOADING);

            webservice.fetchDeliveries(term).enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.isSuccessful()){

                        searchResponse.postValue(response.body());
                        networkCallState.postValue(NetworkState.COMPLETED);
                    }else{
                        NetworkState failureState = new NetworkState(NetworkState.Status.STATE_FAILED, "Something went wrong");
                        networkCallState.postValue(failureState);
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {

                    NetworkState failureState = new NetworkState(NetworkState.Status.STATE_FAILED, t.getMessage());
                    networkCallState.postValue(failureState);
                }
            });
        });

        return this.searchResponse;
    }

    public Track getTrack(int trackId){
        SearchResponse response = searchResponse.getValue();
        return response.getTrack(trackId);
    }

    public Track getNextAudioTrack(Track track){
        SearchResponse response = searchResponse.getValue();
        return response.getNextAudioTrack(track);
    }

    public Track getPreviousAudioTrack(Track track){
        SearchResponse response = searchResponse.getValue();
        return response.getPreviousAudioTrack(track);
    }
}
