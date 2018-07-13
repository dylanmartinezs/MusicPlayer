package dms.org.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SongListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Music> musicList;
    private int resource;
    LayoutInflater inflater;

    public SongListAdapter(Context context,int resource, ArrayList<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
        this.resource = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {;
        convertView = inflater.inflate(resource, parent, false);
        TextView songName = (TextView) convertView.findViewById(R.id.list_song_title);
        TextView songArtist = (TextView) convertView.findViewById(R.id.list_song_artist);

        songName.setText(musicList.get(position).getTitle());
        songArtist.setText(musicList.get(position).getArtist());
        return convertView;
    }
}
