package dms.org.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.Random;
import java.util.zip.Inflater;

public class MainWindow extends AppCompatActivity {

    /*private FrameLayout mainFrame;*/
    private BottomNavigationView bottomNavigationView;
    private FrameLayout musicSlideFrameMain;

    private Home homeFragment;
    private musicList musicFragment;
    private FrameLayout fragmentToReplace;

    private SlidingUpPanelLayout musicSlider;

    private android.support.v4.app.Fragment selectedFragment = Home.newInstance();

    Inflater inflater;
    private SongListAdapter adapter;
    private View rootview;
    public static ArrayList<Music> songs;
    public ArrayList<Albums> albums;

    //Media player service
    boolean serviceBound = false;
    private MediaPlayerService player;
    public static Random suffle;

    //Compact Player
    private LinearLayout compactPlayerC;
    public ImageView playStopBtn;
    public  TextView compactPlayerTitle;
    public ProgressBar compactPlayerProgressBar;
    public static Handler handler = new Handler();

    //Big Player Stuff
    public ImageView bigPlayerPlayStopBtn;
    public ImageView bigPlayerPrevBtn;
    public ImageView bigPlayerNextBtn;
    public ImageView bigPlayerRandom;
    public ImageView bigPlayerRepeat;
    public ImageView bigPlayerCover;
    public TextView bigPlayerTitle;
    public TextView bigPlayerArtist;
    public SeekBar bigPlayerSeekBar;
    public TextView currentTime;
    public TextView maxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        ActivityCompat.requestPermissions(MainWindow.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        fragmentToReplace = (FrameLayout) findViewById(R.id.fragment_to_replace);

        homeFragment = new Home();
        musicFragment = musicList.newInstance();

        initCompactPlayer();
        initBigPlayer();

        /*Thread t = new MusicHelper.ProgressThread();
        t.start();*/

        musicSlider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        musicSlider.getChildAt(1).setOnClickListener(null);
        songs = new ArrayList<>();
        albums = new ArrayList<>();
        suffle = new Random();

        setSongsAttributes();
        MusicHelper.playBtnStuff(this);
        MusicHelper.bigPlayerButtons(this);
        MusicHelper.seekBarListener(this);

        musicSlider.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, @FloatRange(from = 0, to = 1) float slidePos) {
                setCompactPlayerAlpha(slidePos);
                moveBottomBar(slidePos);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }

        });

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_to_replace, selectedFragment);
        transaction.commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        musicSlider.setClipPanel(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.nav_home:
                        selectedFragment = Home.newInstance();
                        break;

                    case R.id.nav_artists:
                        return true;

                    case R.id.nav_albums:
                        return true;

                    case R.id.nav_songs:
                        selectedFragment = musicList.newInstance();
                        break;

                    case R.id.nav_playlists:
                        return true;

                    default:
                        return false;
                }
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_to_replace, selectedFragment);
                transaction.commit();

                return true;
            }

        });

    }
    public void setSongsAttributes() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        String title, artist, album, genre, url;

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    if(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) != null)
                        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    else
                        title = "Unknown";

                    if(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) != null)
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    else
                        artist = "unknown";
                    album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    //genre = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
                    url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                    Music current = new Music(title, artist, album, /*genre,*/ url, albumArtUri);
                    songs.add(current);
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }
    }



    public void setCompactPlayerAlpha(@FloatRange(from = 0, to = 1) float pos)
    {
       float alpha = 1 - pos;
       compactPlayerC.setAlpha(alpha);
       compactPlayerProgressBar.setAlpha(alpha);
       if(alpha == 0)
           compactPlayerC.setVisibility(View.GONE);
       else
           compactPlayerC.setVisibility(View.VISIBLE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(getApplicationContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
            }
        }
    }
    public void moveBottomBar(@FloatRange(from = 0, to = 1) float pos)
    {
        bottomNavigationView.setTranslationY(pos * 300);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(MainWindow.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    public void initCompactPlayer() {
        compactPlayerC = findViewById(R.id.compactPlayerLayout);
        playStopBtn = findViewById(R.id.playStopBtn);
        compactPlayerTitle = findViewById(R.id.compactPlayerSongTitle);
        compactPlayerProgressBar = findViewById(R.id.compactPlayerProgressBar);
    }

    public void initBigPlayer() {
        bigPlayerPlayStopBtn = findViewById(R.id.playerPlayBtn);
        bigPlayerPrevBtn = findViewById(R.id.playerPrevious);
        bigPlayerNextBtn = findViewById(R.id.playerNext);
        bigPlayerRandom = findViewById(R.id.playerRandom);
        bigPlayerRepeat = findViewById(R.id.playerRepeat);
        bigPlayerCover = findViewById(R.id.bigPlayerCover);
        bigPlayerTitle = findViewById(R.id.playerTitle);
        bigPlayerArtist = findViewById(R.id.playerArtist);
        bigPlayerSeekBar = findViewById(R.id.playerSeekBar);
        currentTime = findViewById(R.id.currentTime);
        maxTime = findViewById(R.id.maxTime);
    }

    public static Music previousSong() {
        if(MusicHelper.songPos > 0)
            MusicHelper.songPos--;

        else
            MusicHelper.songPos = songs.size() - 1;

        return songs.get(MusicHelper.songPos);
    }

    public static Music nextSong() {
        if(MusicHelper.suffleMode)
            MusicHelper.songPos = suffle.nextInt(songs.size());

        else {
            switch(MusicHelper.repeatValue) {
                case MusicHelper.REPEAT_ALL:
                    if(MusicHelper.songPos < (songs.size() - 1))
                        MusicHelper.songPos++;
                    else
                        MusicHelper.songPos = 0;
                    break;

                case MusicHelper.REPEAT_NONE:
                    if(MusicHelper.songPos < (songs.size() - 1))
                        MusicHelper.songPos++;
                    else
                        MusicHelper.songPos = 0;
                    break;

                case MusicHelper.REPEAT_ONE:
                    break;
            }
        }

        return songs.get(MusicHelper.songPos);
    }

    public void setPlayerStuff(Music m, int d) {
        Picasso.get()
                .load(m.getAlbumArt())
                .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                .resize(0, bigPlayerCover.getHeight())
                .into(bigPlayerCover);
        bigPlayerArtist.setText(m.getArtist());
        playStopBtn.setImageResource(R.drawable.pausebtn);
        bigPlayerPlayStopBtn.setImageResource(R.drawable.pausebtn);
        compactPlayerTitle.setText(m.getTitle());
        bigPlayerTitle.setText(m.getTitle());
        compactPlayerProgressBar.setMax(d);
        bigPlayerSeekBar.setMax(d);
    }
    public static int getSongsNumber() { return songs.size() - 1; }
}
