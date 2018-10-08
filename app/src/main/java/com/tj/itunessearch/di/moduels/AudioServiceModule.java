package com.tj.itunessearch.di.moduels;

import com.tj.itunessearch.service.AudioPlayerService;

import dagger.Module;
import dagger.Provides;

@Module
public class AudioServiceModule {

    private AudioPlayerService mService;

    public AudioServiceModule(AudioPlayerService service) {
        mService = service;
    }

    @Provides
    AudioPlayerService provideMyService() {
        return mService;
    }
}
