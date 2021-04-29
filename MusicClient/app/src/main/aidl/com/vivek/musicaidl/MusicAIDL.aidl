package com.vivek.musicaidl;

interface MusicAIDL {
    Bundle musicDetails();
    Bundle getOneSongDetails(int songIndex);
    String getSongURL(int songIndex);
}