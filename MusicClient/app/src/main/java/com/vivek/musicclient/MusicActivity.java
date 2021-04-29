package com.vivek.musicclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vivek.musicaidl.MusicAIDL;

import java.io.IOException;

public class MusicActivity extends AppCompatActivity {
    private static final String TAG = "MusicActivity";
    private String[] title;
    private String[] artist;
    private Bitmap[] image = new Bitmap[7];
    private String[] url;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView mImageView;

    private Bundle bundle;
    private MediaPlayer player;
    MainActivity mAcivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Log.i(TAG, "onCreate: ");

        bundle = getIntent().getExtras();
        title = bundle.getStringArray("title");
        artist = bundle.getStringArray("artist");
//        url = bundle.getStringArray("url");
        getImageId();

        RVClickListener listener = (view, position) -> {
            Log.i(TAG, "Starting Music : " + title[position]);
            Toast.makeText(this, " Starting Music :" + title[position],
                    Toast.LENGTH_SHORT).show();
            try {
                playMusic(mAcivity.mMusicAIDL.getSongURL(position));
                Log.i(TAG, "onCreate: " + mAcivity.mMusicAIDL.getSongURL(position));
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        };

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MusicAdapter(title, artist, image, listener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mImageView = (ImageView) findViewById(R.id.pauseButton);
        mImageView.setOnClickListener(v -> {
            if (player != null) {
                player.stop();
                mImageView.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void playMusic(String audioUrl) {
        if (player != null) {
            player.stop();
        }
        player = new MediaPlayer();
        mImageView.setVisibility(View.VISIBLE);
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
        byte[][] byteArray = new byte[7][];
        String s;
        for (int i = 0; i < 7; i++) {
            s = "image" + (i + 1);
            byteArray[i] = getIntent().getByteArrayExtra(s);
            image[i] = BitmapFactory.decodeByteArray(byteArray[i], 0, byteArray[i].length);
            Log.i(TAG, "getImageId: " + s + " " + byteArray);
        }
    }
}