package dms.org.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.Inflater;

public class musicList extends Fragment {
    private RecyclerView songsList;
    private SongListAdapter adapter;
    private View rootview;
    public ArrayList<Music> songs;
    private MainWindow mainWindow;

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


        return rootview;
    }
}
