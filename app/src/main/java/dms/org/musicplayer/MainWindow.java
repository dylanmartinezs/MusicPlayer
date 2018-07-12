package dms.org.musicplayer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends AppCompatActivity {

    private FrameLayout mainFrame;
    private BottomNavigationView bottomNavigationView;

    private Home homeFragment;
    private musicList musicFragment;

    private SlidingUpPanelLayout musicSlider;

    private LinearLayout compactPlayerC;

    private android.support.v4.app.Fragment selectedFragment = Home.newInstance();

    private ListView songList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        ActivityCompat.requestPermissions(MainWindow.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        mainFrame = (FrameLayout) findViewById(R.id.main_frame_layout);

        homeFragment = new Home();
        musicFragment = new musicList();
        compactPlayerC = (LinearLayout) findViewById(R.id.compactPlayerLayout);

        musicSlider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        songList = (ListView) findViewById(R.id.song_list);
        ArrayList<File> songs = loadSongs(Environment.getExternalStorageDirectory());

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
    public ArrayList<File> loadSongs(File root){
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files)
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                al.addAll(loadSongs(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".mp3"))
                {
                    al.add(singleFile);
                }
            }
        }
        return al;
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
