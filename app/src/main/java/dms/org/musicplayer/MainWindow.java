package dms.org.musicplayer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.zip.Inflater;

public class MainWindow extends AppCompatActivity {

    /*private FrameLayout mainFrame;*/
    private BottomNavigationView bottomNavigationView;
    private FrameLayout musicSlideFrameMain;

    private Home homeFragment;
    private musicList musicFragment;
    private FrameLayout fragmentToReplace;

    private SlidingUpPanelLayout musicSlider;

    private LinearLayout compactPlayerC;

    private android.support.v4.app.Fragment selectedFragment = Home.newInstance();

    Inflater inflater;
    private ListView songsList;
    private SongListAdapter adapter;
    private View rootview;
    public ArrayList<Music> songs;
    public ArrayList<Albums> albums;

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

        compactPlayerC = (LinearLayout) findViewById(R.id.compactPlayerLayout);

        musicSlider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        songs = new ArrayList<>();
        albums = new ArrayList<>();

        setSongsAttributes();

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
        int albumID;
        Albums albumCursor;

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
    public void moveBottomBar(@FloatRange(from = 0, to = 1) float pos)
    {
        bottomNavigationView.setTranslationY(pos * 300);
    }
}
