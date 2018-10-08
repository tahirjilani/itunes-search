package com.tj.itunessearch.di.moduels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.tj.itunessearch.vm.SearchViewModel;
import com.tj.itunessearch.vm.ViewModelFactory;
import com.tj.itunessearch.vm.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel provideSearchViewModel(SearchViewModel viewModel);

}
