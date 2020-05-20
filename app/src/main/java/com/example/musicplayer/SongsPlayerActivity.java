package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SongsPlayerActivity extends AppCompatActivity {

    Button next_btn, prev_btn, pause_btn;
    TextView songTitle, ellapseTime, remainingTime;
    SeekBar songBar, volBar;
    String sName;

    static MediaPlayer mediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_player);

        next_btn=findViewById(R.id.songnext);
        prev_btn=findViewById(R.id.songprevious);
        pause_btn=findViewById(R.id.songpause);
        songTitle=findViewById(R.id.songname);
        songBar=findViewById(R.id.songbar);
        volBar=findViewById(R.id.songvol);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentpos = 0;

                while (currentpos<totalDuration){
                    try {
                        sleep(500);
                        currentpos = mediaPlayer.getCurrentPosition();
                        songBar.setProgress(currentpos);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");

        sName = mySongs.get(position).getName().toString();
        final String songName = intent.getStringExtra("songname");

        songTitle.setText(songName);
        songTitle.setSelected(true);

        position = bundle.getInt("pos",0);

        Uri uri=Uri.parse(mySongs.get(position).toString());

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        mediaPlayer.setVolume(0.5f,0.5f);

        songBar.setMax(mediaPlayer.getDuration());

        updateSeekbar.start();

        songBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        songBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        volBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        volBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volNum = progress/100f;
                mediaPlayer.setVolume(volNum,volNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songBar.setMax(mediaPlayer.getDuration());

                if (mediaPlayer.isPlaying()){
                    pause_btn.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    mediaPlayer.pause();
                }
                else{
                    pause_btn.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    mediaPlayer.start();
                }
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position < mySongs.size()-1) {
                    position = position + 1;
                }else {
                    position = 0;
                }
                Uri uri1=Uri.parse(mySongs.get(position).toString());

                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri1);
                sName = mySongs.get(position).getName().toString();
                songTitle.setText(sName);
                mediaPlayer.start();
            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position<=0){
                    position = mySongs.size()-1;
                }else {
                    position--;
                }

                Uri uri2 = Uri.parse(mySongs.get(position).toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri2);
                sName = mySongs.get(position).getName().toString();
                songTitle.setText(sName);
                mediaPlayer.start();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
