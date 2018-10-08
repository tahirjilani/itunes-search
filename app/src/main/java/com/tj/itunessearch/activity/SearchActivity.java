package com.tj.itunessearch.activity;

import android.app.SearchManager;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tj.itunessearch.App;
import com.tj.itunessearch.R;
import com.tj.itunessearch.adapter.SearchAdapter;
import com.tj.itunessearch.dataModel.SearchResponse;
import com.tj.itunessearch.dataModel.Track;
import com.tj.itunessearch.databinding.SearchActivityBinding;
import com.tj.itunessearch.service.AudioPlayerService;
import com.tj.itunessearch.service.Constants;
import com.tj.itunessearch.vm.SearchViewModel;
import com.tj.itunessearch.vm.ViewModelFactory;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity {

    private SearchActivityBinding binding;
    private SearchViewModel viewModel;
    private MutableLiveData<String> searchTermLiveData = new MutableLiveData<>();
    private SearchView searchView;
    public static SearchActivity instance = null;


    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        App.getInstance().getAppComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchTermLiveData.observe(this, term -> {
            viewModel.search(term).observe(SearchActivity.this, searchResponse -> {

                showSearchResults(searchResponse);
            });
        });

        handleIntent(getIntent());
    }

    private void showSearchResults(SearchResponse searchResponse){

        if (searchResponse.getResultCount() < 1){

            binding.msgTextView.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else{
            binding.msgTextView.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        SearchAdapter searchAdapter = new SearchAdapter(this, searchResponse);
        binding.recyclerView.setAdapter(searchAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.expandActionView();
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (getIntent().getAction().equals(Constants.ACTION.NOTIFICATION_CLICKED_ACTION)){
            searchView.setQuery(viewModel.lastSearchTerm(), true);
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (intent == null) return;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            //perform new search
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchTermLiveData.postValue(query);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        instance = null;
        releaseForegroundService();
        //destroy my activity
        super.onDestroy();
    }

    private void releaseForegroundService(){
        //release audio service from foreground and enable stop on notification swipe
        if (AudioPlayerService.getInstance() != null){
            AudioPlayerService.getInstance().stopForeground();
        }
    }

    /*
    *
    * We restart service
    * **/
    private void reInitForegroundServiceIfNeeded(){
        if (AudioPlayerService.getInstance() != null){

            final int trackId = AudioPlayerService.getCurrentlyPlayingTrackLiveData().getValue().track.getTrackId();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SearchActivity.this, AudioPlayerService.class);
                    i.setAction(Constants.ACTION.START_ACTION);
                    i.putExtra("trackId", trackId);
                    startService(i);
                }
            }, 2000);
        }
    }
}
