package com.vivek.musiccentral;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;

import com.vivek.musicaidl.MusicAIDL;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MusicService extends Service {
    private String[] title;
    private String[] artist;
    private Bitmap[] image = new Bitmap[7];
    private String[] url;

    ArrayList<byte[]> imageByteList = new ArrayList();
    private final MusicAIDL.Stub mBinder = new MusicAIDL.Stub() {
        @Override
        public Bundle musicDetails() {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image[0] = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
            image[1] = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
            image[2] = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
            image[3] = BitmapFactory.decodeResource(getResources(), R.drawable.image4);
            image[4] = BitmapFactory.decodeResource(getResources(), R.drawable.image5);
            image[5] = BitmapFactory.decodeResource(getResources(), R.drawable.image6);
            image[6] = BitmapFactory.decodeResource(getResources(), R.drawable.image7);

            title = getResources().getStringArray(R.array.title);
            artist = getResources().getStringArray(R.array.artist);
            url = getResources().getStringArray(R.array.url);

            Bundle bundle = new Bundle();
            bundle.putStringArray("title", title);
            bundle.putStringArray("artist", artist);
            bundle.putParcelable("image1", image[0]);
            bundle.putParcelable("image2", image[1]);
            bundle.putParcelable("image3", image[2]);
            bundle.putParcelable("image4", image[3]);
            bundle.putParcelable("image5", image[4]);
            bundle.putParcelable("image6", image[5]);
            bundle.putParcelable("image7", image[6]);
            bundle.putStringArray("url", url);
            return bundle;
        }

        ;
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}