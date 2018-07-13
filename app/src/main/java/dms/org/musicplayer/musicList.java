package dms.org.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class musicList extends Fragment {
    private ListView songsList;
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
        songsList = rootview.findViewById(R.id.song_list);

        mainWindow = (MainWindow) getActivity();

        adapter = new SongListAdapter(getContext(), R.layout.fragment_music_item, mainWindow.songs);

        Toast.makeText(getContext(), mainWindow.toString(adapter.getCount()), Toast.LENGTH_LONG).show();
        songsList.setAdapter(adapter);


        return rootview;
    }
}
