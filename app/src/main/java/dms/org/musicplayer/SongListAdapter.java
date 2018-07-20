package dms.org.musicplayer;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>
{
    private Context context;
    private ArrayList<Music> musicList;

    public SongListAdapter(Context context, ArrayList<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_music_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.songTitle.setText(musicList.get(position).getTitle());
        holder.songArtist.setText(musicList.get(position).getArtist());

        Picasso.get().load(musicList.get(position).getAlbumArt()).error(R.drawable.coverbydefault).into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView songTitle;
        TextView songArtist;
        LinearLayout parentLayout;
        ImageView albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.list_song_title);
            songArtist = itemView.findViewById(R.id.list_song_artist);
            parentLayout = itemView.findViewById(R.id.music_list_item);
            albumArt = itemView.findViewById(R.id.listAlbumArt);
        }
    }
    @Override public long getItemId(int position) { return position; }
}
