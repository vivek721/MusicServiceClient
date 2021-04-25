package com.vivek.musicclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

public class MusicActivity extends AppCompatActivity {
    private static final String TAG = "MusicActivity";
    private String[] title;
    private String[] artist;
    private Bitmap[] image = new Bitmap[7];
    private String[] url;
    private Integer[] imageId = new Integer[7];
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Bundle bundle;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        bundle = getIntent().getExtras();
        title = bundle.getStringArray("title");
        artist = bundle.getStringArray("artist");
        url = bundle.getStringArray("url");
        getImageId();

        RVClickListener listener = (view, position) -> {
            Log.i(TAG, "Starting Music : " + title[position]);
            Toast.makeText(this, " Starting Music :" + title[position],
                    Toast.LENGTH_SHORT).show();

            playMusic(url[position]);

            Intent musicintent = new Intent(this,
                    MusicNotificationService.class);
            musicintent.putExtra("URL", url[position]);
            startService(musicintent);
        };

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MusicAdapter(title, artist, imageId, listener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

    private void playMusic(String audioUrl) {
        if (player != null) {
            player.stop();
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            player.prepare();
            player.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        Log.i(TAG, "onDestroy: music stopped");
    }

    public void getImageId() {
        imageId[0] = R.drawable.image1;
        imageId[1] = R.drawable.image2;
        imageId[2] = R.drawable.image3;
        imageId[3] = R.drawable.image4;
        imageId[4] = R.drawable.image5;
        imageId[5] = R.drawable.image6;
        imageId[6] = R.drawable.image7;
    }
}