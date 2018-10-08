package com.tj.itunessearch.di;

import com.tj.itunessearch.activity.SearchActivity;
import com.tj.itunessearch.activity.VideoPlayerActivity;
import com.tj.itunessearch.di.moduels.AppModule;
import com.tj.itunessearch.di.moduels.AudioServiceModule;
import com.tj.itunessearch.di.moduels.NetModule;
import com.tj.itunessearch.di.moduels.SearchRepositoryModule;
import com.tj.itunessearch.di.moduels.ViewModelModule;
import com.tj.itunessearch.service.AudioPlayerService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ViewModelModule.class, NetModule.class, SearchRepositoryModule.class, AudioServiceModule.class})
public interface AppComponent {

    void inject(SearchActivity activity);

    void inject(VideoPlayerActivity videoPlayerActivity);

    void inject(AudioPlayerService service);
}
