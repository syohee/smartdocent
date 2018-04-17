package com.example.tg.myapplication;

import android.media.MediaPlayer;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by TG on 2018-04-01.
 */

public class HttpURLConnection {
    MediaPlayer mediaPlayer ;
    public MediaPlayer StartDoecnt(String url){
        try{
           /* if(mediaPlayer != mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }*/
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            return mediaPlayer;
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                }
//            });
        }catch (Exception e){
            Log.e("MusicPlayer",e.getMessage());
            return null;
        }
    }
}
