package com.tj.itunessearch.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.tj.itunessearch.R;
import com.tj.itunessearch.activity.VideoPlayerActivity;
import com.tj.itunessearch.dataModel.SearchResponse;
import com.tj.itunessearch.dataModel.Track;
import com.tj.itunessearch.databinding.SongItemDataBinding;
import com.tj.itunessearch.service.AudioPlayerService;
import com.tj.itunessearch.service.Constants;
import com.tj.itunessearch.service.PlayingTrack;
import com.tj.itunessearch.util.Utils;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private AppCompatActivity context;
    private List<Track> trackList;
    private PlayingTrack playingTrack;

    public SearchAdapter(AppCompatActivity context, SearchResponse searchResponse){
        this.context = context;
        this.trackList = searchResponse.getResults();

        AudioPlayerService.getCurrentlyPlayingTrackLiveData().observe(context, playingTrack -> {
            this.playingTrack = playingTrack;
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        SongItemDataBinding itemBinding = SongItemDataBinding.inflate(layoutInflater, viewGroup, false);
        return new TrackViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((TrackViewHolder)viewHolder).bindTo(this.trackList.get(i));
    }

    @Override
    public int getItemCount() {
        return this.trackList.size();
    }


    private class TrackViewHolder extends RecyclerView.ViewHolder{

        private SongItemDataBinding binding;

        public TrackViewHolder(@NonNull SongItemDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(Track track) {

            Picasso.get()
                    .load(track.getArtworkUrl100())
                    .into(binding.imageView);

            if (track.getTrackName() == null) {
                binding.titleTextView.setText(R.string.unkown_title);
            } else{
                binding.titleTextView.setText(track.getTrackName());
            }
            if (track.getCollectionName() == null){
                binding.albumTextView.setText(R.string.unkown_collection);
            }else {
                binding.albumTextView.setText(track.getCollectionName());
            }
            if (track.getArtistName() == null){
                binding.singerTextView.setText(R.string.unkown_artist);
            }else {
                binding.singerTextView.setText(track.getArtistName());
            }
            binding.durationTextView.setText(Utils.getDuration(track.getTrackTimeMillis()));

            //show play pause icon for audio
            binding.playImageView.setSelected(false);
            if (playingTrack != null && playingTrack.track.getTrackId() == track.getTrackId()){
                binding.playImageView.setSelected(playingTrack.isPlaying);
            }

            binding.playImageView.setTag(track);
            binding.playImageView.setOnClickListener(SearchAdapter.this);
        }
    }

    @Override
    public void onClick(View v) {
        Track track = (Track) v.getTag();
        if (track.getKind() != null && track.getKind().equals("feature-movie")){

            if (AudioPlayerService.getInstance() != null){
                AudioPlayerService.getInstance().stopServiceAndClearNotification();
            }
            Intent intent = VideoPlayerActivity.createIntent(context, track);
            context.startActivity(intent);

        }else{

            //in pause mode, play paused item
            if (AudioPlayerService.getInstance() != null &&
                    playingTrack != null && playingTrack.track.getTrackId() == track.getTrackId()){

                Intent i = new Intent(context, AudioPlayerService.class);
                i.setAction(Constants.ACTION.PLAY_PAUSE_ACTION);
                context.startService(i);

                return;
            }

            Intent i = new Intent(context, AudioPlayerService.class);
            i.setAction(Constants.ACTION.START_ACTION);
            i.putExtra("trackId", track.getTrackId());

            context.startService(i);
        }
    }

}
