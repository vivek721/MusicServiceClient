package com.vivek.musicclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vivek.musicaidl.MusicAIDL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static MusicAIDL mMusicAIDL;
    MediaPlayer mPlayer;
    private boolean bindStatus = false;
    private String[] title;
    private String[] artist;
    private Bitmap[] image = new Bitmap[7];
    private Spinner spinner;
    private byte[][] byteArray = new byte[7][];
    private Button bindButton;
    private Button unbindButton;
    private Button showMusicButton;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: ");
            mMusicAIDL = MusicAIDL.Stub.asInterface(service);
            bindStatus = true;

            //change button status as enabled or disabled
            buttonStatusChange(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicAIDL = null;
            bindStatus = false;
        }
    };
    private TextView singleTitle;
    private TextView singleArtist;
    private ImageView singleImage;
    private ImageView playPause;
    private Bundle b = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.planets_spinner);
        bindButton = (Button) findViewById(R.id.bind);
        unbindButton = (Button) findViewById(R.id.unbind);
        showMusicButton = (Button) findViewById(R.id.allMusic);
        singleTitle = (TextView) findViewById(R.id.singleTitle);
        singleArtist = (TextView) findViewById(R.id.singleArtist);
        singleImage = (ImageView) findViewById(R.id.singleImageView);
        playPause = (ImageView) findViewById(R.id.playPause);

        buttonStatusChange(false);

        bindButton.setOnClickListener(v -> {
            checkBindingAndBind();

            buttonStatusChange(false);

        });

        unbindButton.setOnClickListener(v -> {
            checkBindingAndUnbind();
        });

        showMusicButton.setOnClickListener(v -> {
            if (mPlayer != null)
                mPlayer.stop();
            checkBindingAndUnbind();
            try {
                Bundle mBundle;
                mBundle = mMusicAIDL.musicDetails();
                setAllMusicDetails(mBundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, MusicActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        });

        playPause.setOnClickListener(v -> {
            mPlayer.stop();
            playPause.setVisibility(View.INVISIBLE);
            singleImage.setImageBitmap(null);
            singleTitle.setText("");
            singleArtist.setText("");
        });

        ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.songTitle, R.layout.spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void setAllMusicDetails(Bundle mBundle) {

        title = mBundle.getStringArray("title");
        artist = mBundle.getStringArray("artist");

        b.putStringArray("title", title);
        b.putStringArray("artist", artist);

        for (int i = 0; i < 7; i++) {
            String s = "image" + (i + 1);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image[i] = mBundle.getParcelable(s);
            image[i].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray[i] = stream.toByteArray();
            b.putByteArray(s, byteArray[i]);
        }
    }

    protected void checkBindingAndUnbind() {
        if (bindStatus) {
            unbindService(this.mConnection);
            bindStatus = false;
            //change button status as enabled or disabled
            buttonStatusChange(false);
            Toast.makeText(this, "UnBounded Successfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Service not bounded!", Toast.LENGTH_LONG).show();
        }
    }

    protected void checkBindingAndBind() {
        if (!bindStatus) {
            boolean b = false;
            Intent i = new Intent(MusicAIDL.class.getName());

            // Converting the implicit intent to explicit
            ResolveInfo info = getPackageManager().resolveService(i, 0);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if (b) {
                Log.i(TAG, "bindService() succeeded!");
                Toast.makeText(this, "Bounded Successfully!", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "bindService() failed!");
                Toast.makeText(this, "Binding Failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Unbind from MusicCentral Service
    @Override
    protected void onStop() {

        super.onStop();

        if (bindStatus) {
            unbindService(this.mConnection);
        }
    }


    public void buttonStatusChange(Boolean status) {
        if (status) {
            // changing clickable
            bindButton.setEnabled(false);
            unbindButton.setEnabled(true);
            showMusicButton.setEnabled(true);

            bindButton.setBackgroundResource(R.color.buttonDisable);
            unbindButton.setBackgroundResource(R.color.buttonEnable);
            showMusicButton.setBackgroundResource(R.color.buttonEnable);

        } else {
            // changing clickable
            bindButton.setEnabled(true);
            unbindButton.setEnabled(false);
            showMusicButton.setEnabled(false);

            bindButton.setBackgroundResource(R.color.buttonEnable);
            unbindButton.setBackgroundResource(R.color.buttonDisable);
            showMusicButton.setBackgroundResource(R.color.buttonDisable);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemSelected: " + position);
        if (bindStatus) {
            if (position == 0) {
                Toast.makeText(this, "Select a song", Toast.LENGTH_LONG).show();
                singleImage.setImageBitmap(null);
                singleTitle.setText("");
                singleArtist.setText("");
            } else {
                Bundle mBundle;
                try {
                    mBundle = mMusicAIDL.getOneSongDetails(position - 1);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bmp = mBundle.getParcelable("image");
                    singleImage.setImageBitmap(bmp);
                    singleTitle.setText(mBundle.getString("title"));
                    singleArtist.setText(mBundle.getString("artist"));
                    playMusic(mMusicAIDL.getSongURL(position - 1));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Bind service First", Toast.LENGTH_LONG).show();
        }
    }

    private void playMusic(String audioUrl) {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        mPlayer = new MediaPlayer();
        playPause.setVisibility(View.VISIBLE);
        try {
            mPlayer.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}