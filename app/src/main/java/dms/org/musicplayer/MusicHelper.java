package dms.org.musicplayer;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.logging.Handler;

public class MusicHelper {
    private static MediaPlayer player;
    private static Runnable runnable;

    public static void initMediaPlayer() {
    }

    public static void listListener() {
        musicList.adapter.set_OnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout b, View v, Music obj, int position) {
                try
                {
                    if(player != null)
                        if(player.isPlaying()) {
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
                            else {
                                mp.start();
                                MainWindow.playStopBtn.setImageResource(R.drawable.pausebtn);
                                MainWindow.compactPlayerTitle.setText(obj.getTitle());
                                MainWindow.compactPlayerProgressBar.setMax(player.getDuration());
                                updateProgressBar();
                            }

                        }
                    });
                }
                catch (IOException e) { e.getStackTrace(); }
                /*playAudio(obj.getMusicData());*/
            }
        });
    }

    private static void updateProgressBar() {
        MainWindow.compactPlayerProgressBar.setProgress(player.getCurrentPosition());

        if(player.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateProgressBar();
                }
            };
            MainWindow.handler.postDelayed(runnable, 1000);
        }
    }


    public static void playBtnStuff() {
        MainWindow.playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()) {
                    player.pause();
                    MainWindow.playStopBtn.setImageResource(R.drawable.playbtn);
                }
                else {
                    player.start();
                    MainWindow.playStopBtn.setImageResource(R.drawable.pausebtn);
                }
            }
        });

    }
    public static class ProgressThread extends Thread {
        @Override
        public void run() {
            if(player != null)
                MainWindow.compactPlayerProgressBar.setProgress(player.getCurrentPosition());
        }
    }
}
