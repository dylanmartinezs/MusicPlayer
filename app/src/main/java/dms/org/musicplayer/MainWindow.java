package dms.org.musicplayer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.util.zip.Inflater;

public class MainWindow extends AppCompatActivity {

    private FrameLayout mainFrame;
    private BottomNavigationView bottomNavigationView;

    private Home homeFragment;
    private musicList musicFragment;

    private SlidingUpPanelLayout musicSlider;

    private LinearLayout compactPlayerC;

    private android.support.v4.app.Fragment selectedFragment = Home.newInstance();

    Inflater inflater;
    private ListView songsList;
    private SongListAdapter adapter;
    private View rootview;
    public ArrayList<Music> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        ActivityCompat.requestPermissions(MainWindow.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        mainFrame = (FrameLayout) findViewById(R.id.main_frame_layout);

        homeFragment = new Home();
        musicFragment = musicList.newInstance();

        compactPlayerC = (LinearLayout) findViewById(R.id.compactPlayerLayout);

        musicSlider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        songs = setSongsAttributes(Environment.getExternalStorageDirectory());

        try
        {
            songsList.setAdapter(adapter);
        }
        catch (Exception e) {System.out.println(e.getMessage());}

        musicSlider.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, @FloatRange(from = 0, to = 1) float slidePos) {
                setCompactPlayerAlpha(slidePos);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }

        });

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, selectedFragment);
        transaction.commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

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
                transaction.replace(R.id.mainFrame, selectedFragment);
                transaction.commit();
                return true;
            }

        });

    }
    public ArrayList<Music> setSongsAttributes(File root)
    {
        String artist;
        String title;
        String album;
        String genre;
        MediaMetadataRetriever metadata = new MediaMetadataRetriever();
        ArrayList<Music> songsWithAttrs = new ArrayList<Music>();
        File[] files = root.listFiles();
        for(File singleFile : files)
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                songsWithAttrs.addAll(setSongsAttributes(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".mp3"))
                {
                    metadata.setDataSource(singleFile.getPath());
                    if(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
                        title = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    else
                        title = singleFile.getName().substring(0, (singleFile.getName().length() - 2));
                    artist = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    album = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    genre = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                    songsWithAttrs.add(new Music(title, artist, album, genre));
                }
            }
        }
        return songsWithAttrs;
    }



    public void setCompactPlayerAlpha(@FloatRange(from = 0, to = 1) float pos)
    {
       float alpha = 1 - pos;
       compactPlayerC.setAlpha(alpha);
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

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(getApplicationContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
                return;
            }
        }
    }
    public String toString(int i)
    {
        return i + "";
    }
}
