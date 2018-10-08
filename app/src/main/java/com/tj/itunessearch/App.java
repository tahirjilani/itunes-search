package com.tj.itunessearch;

import android.app.Application;

import com.tj.itunessearch.di.AppComponent;
import com.tj.itunessearch.di.DaggerAppComponent;
import com.tj.itunessearch.di.moduels.AppModule;
import com.tj.itunessearch.di.moduels.NetModule;
import com.tj.itunessearch.di.moduels.ViewModelModule;

public class App extends Application {

    private static App instance;
    public static App getInstance(){
        return instance;
    }

    private AppComponent appComponent;
    public AppComponent getAppComponent() {
        return appComponent;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //dagger2 dependency injection
        AppModule appModule = new AppModule();
        NetModule netModule = new NetModule();

        appComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .netModule(netModule)
                .build();
    }
}
