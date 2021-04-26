package com.vivek.musicclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.vivek.musicaidl.MusicAIDL;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private boolean bindStatus = false;
    private String[] title;
    private String[] artist;
    private Bitmap[] image = new Bitmap[7];
    private String[] url;
    private byte[][] byteArray = new byte[7][];
    private Button bindButton;
    private Button unbindButton;
    private Button showMusicButton;
    private MusicAIDL mMusicAIDL;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
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
    private Bundle b = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindButton = (Button) findViewById(R.id.bind);
        unbindButton = (Button) findViewById(R.id.unbind);
        showMusicButton = (Button) findViewById(R.id.allMusic);

        Log.i(TAG, "onCreate: " + bindStatus);

        bindButton.setOnClickListener(v -> {
            checkBindingAndBind();
            buttonStatusChange(false);
        });

        unbindButton.setOnClickListener(v -> {
            checkBindingAndUnbind();
        });

        showMusicButton.setOnClickListener(v -> {
            checkBindingAndUnbind();
            try {
                Bundle mBundle;
                mBundle = mMusicAIDL.musicDetails();
                setAllMusicDetails(mBundle);
                Intent intent = new Intent(this, MusicActivity.class);
                intent.putExtras(b);
                startActivity(intent);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        });
    }

    private void setAllMusicDetails(Bundle mBundle) {

        title = mBundle.getStringArray("title");
        artist = mBundle.getStringArray("artist");
        url = mBundle.getStringArray("url");

        b.putStringArray("title", title);
        b.putStringArray("artist", artist);
        b.putStringArray("url", url);

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
}