package com.vivek.musicclient;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private static final String TAG = "MusicAdapter";
    private String[] title;
    private String[] artist;
    private Bitmap[] image = new Bitmap[7];
    private RVClickListener RVlistener; //listener defined in main activity

    public MusicAdapter(String[] title, String[] artist,
                        Bitmap[] image, RVClickListener listener) {
        this.title = title;
        this.artist = artist;
        this.RVlistener = listener;
        this.image = image;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(v, RVlistener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        holder.mTitle.setText(title[position]);
        holder.mArtist.setText(artist[position]);
        holder.mImageView.setImageBitmap(image[position]);
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mImageView;
        public TextView mTitle;
        public TextView mArtist;
        private RVClickListener listener;

        public ViewHolder(@NonNull View itemView, RVClickListener passedListener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitle = itemView.findViewById(R.id.title);
            mArtist = itemView.findViewById(R.id.artist);

            this.listener = passedListener;

            itemView.setOnClickListener(this); //set short click listener
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
