package com.tj.itunessearch.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.tj.itunessearch.dataModel.SearchResponse;
import com.tj.itunessearch.repository.SearchRepository;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {

    private SearchRepository searchRepository;

    @Inject
    public SearchViewModel(SearchRepository repository){
        this.searchRepository = repository;
    }

    public MutableLiveData<SearchResponse> search(String term){
        return this.searchRepository.search(term);
    }

    public String lastSearchTerm(){
        return this.searchRepository.getSearchTerm();
    }
}
