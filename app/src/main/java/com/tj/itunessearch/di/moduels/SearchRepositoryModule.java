package com.tj.itunessearch.di.moduels;

import com.tj.itunessearch.executor.AppExecutors;
import com.tj.itunessearch.network.Webservice;
import com.tj.itunessearch.repository.SearchRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchRepositoryModule {

    @Provides
    @Singleton
    SearchRepository provideSearchRepository(Webservice webservice, AppExecutors executors){
        return new SearchRepository(webservice, executors);
    }

}
