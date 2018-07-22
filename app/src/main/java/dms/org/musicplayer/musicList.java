package dms.org.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.Inflater;

public class musicList extends Fragment {
    private RecyclerView songsList;
    private View rootview;
    public ArrayList<Music> songs;
    private MainWindow mainWindow;
    // private MediaPlayerService player;
    // private MediaPlayer player;
    boolean serviceBound = false;
    public static SongListAdapter adapter;


    public static musicList newInstance() {
        musicList fragment = new musicList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_music_list, container, false);
        songsList = rootview.findViewById(R.id.music_list);

        mainWindow = (MainWindow) getActivity();

        adapter = new SongListAdapter(getContext(), mainWindow.songs);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        Collections.sort(mainWindow.songs, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        adapter.setHasStableIds(true);
        songsList.addItemDecoration(itemDecorator);
        songsList.setAdapter(adapter);
        songsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        MusicHelper.listListener();



        /*adapter.set_OnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout b, View v, Music obj, int position) {
                try
                {
                    if(player != null) {
                        player.stop();
                    }
                    player = new MediaPlayer();
                    player.setDataSource(obj.getMusicData());
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if(mp.isPlaying()){
                                mp.release();
                                mp.start();
                            }
                            else
                                mp.start();
                        }
                    });
                }
                catch (IOException e) { e.getStackTrace(); }
                playAudio(obj.getMusicData());
            }
        });*/



        return rootview;
    }

    /*private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(getActivity().getApplicationContext(), "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            mainWindow.startService(playerIntent);
            mainWindow.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }*/
}
