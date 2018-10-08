package com.tj.itunessearch.network;

import com.tj.itunessearch.dataModel.SearchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {

    @GET("search")
    Call<SearchResponse> fetchDeliveries(@Query("term") String term);

}
