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
    private byte[] byteArray1;
    private byte[] byteArray2;
    private byte[] byteArray3;
    private byte[] byteArray4;
    private byte[] byteArray5;
    private byte[] byteArray6;
    private byte[] byteArray7;
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
                Bundle b = new Bundle();
                b.putByteArray("image1", byteArray1);
                b.putByteArray("image2", byteArray2);
                b.putByteArray("image3" , byteArray3);
                b.putByteArray("image4", byteArray4);
                b.putByteArray("image5", byteArray5);
                b.putByteArray("image7", byteArray7);
                b.putStringArray("title", title);
                b.putStringArray("artist", artist);
                b.putStringArray("url", url);
                intent.putExtras(b);
//                intent.putExtra("title", title);
//                intent.putExtra("artist", artist);
//                intent.putExtra("url", url);

                startActivity(intent);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        });
    }

    private void setAllMusicDetails(Bundle mBundle) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        title = mBundle.getStringArray("title");
        artist = mBundle.getStringArray("artist");
        url = mBundle.getStringArray("url");
        image[0] = mBundle.getParcelable("image1");
        image[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray1 = stream.toByteArray();
        stream = new ByteArrayOutputStream();
        image[1] = mBundle.getParcelable("image2");
        image[1].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray2 = stream.toByteArray();
        stream = new ByteArrayOutputStream();
        image[2] = mBundle.getParcelable("image3");
        image[2].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray3 = stream.toByteArray();
        stream = new ByteArrayOutputStream();
        image[3] = mBundle.getParcelable("image4");
        image[3].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray4 = stream.toByteArray();
        stream = new ByteArrayOutputStream();
        image[4] = mBundle.getParcelable("image5");
        image[4].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray5 = stream.toByteArray();
        stream = new ByteArrayOutputStream();
        image[5] = mBundle.getParcelable("image6");
        image[5].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray6 = stream.toByteArray();
        Log.i(TAG, "setAllMusicDetails: " + image[5]);
        stream = new ByteArrayOutputStream();
        image[6] = mBundle.getParcelable("image7");
        image[6].compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray7 = stream.toByteArray();
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