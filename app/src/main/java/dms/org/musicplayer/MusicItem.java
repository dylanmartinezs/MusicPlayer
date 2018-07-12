package dms.org.musicplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicItem extends Fragment
{
    public MusicItem() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MusicItem newInstance(String param1, String param2) {
        MusicItem fragment = new MusicItem();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_item, container, false);
    }

}
