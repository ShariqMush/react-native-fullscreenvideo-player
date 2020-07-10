package com.my.package;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class VideoActivity extends AppCompatActivity {
    private String videoPath;
    private Map<String, String> videoHeaders = null;

    private static ProgressDialog progressDialog;
    VideoView myVideoView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.player_fullscreen);
        Intent i = getIntent();
        if(i != null){
            myVideoView = (VideoView) findViewById(R.id.videoView);
            videoPath = i.getStringExtra("VIDEO_URL");
            if (i.hasExtra("VIDEO_HEADERS_JSON")) {
                String videoHeadersJson = i.getStringExtra("VIDEO_HEADERS_JSON");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    videoHeaders = mapper.readValue(videoHeadersJson, new TypeReference<Map<String, String>>(){});
                } catch (JsonGenerationException e) {
                    System.err.println(e.getMessage());
                } catch (JsonProcessingException e) {
                    System.err.println(e.getMessage());
                }
            }
            progressDialog = ProgressDialog.show(VideoActivity.this, "", "Buffering video...", true);
            progressDialog.setCancelable(true);
            PlayVideo();
        }
        else{
            Toast.makeText(VideoActivity.this, "VideoURL not found", Toast.LENGTH_SHORT).show();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(VideoActivity.this);
            mediaController.setAnchorView(myVideoView);

            Uri video = Uri.parse(videoPath);
            Map<String, String> headers = videoHeaders;
            myVideoView.setMediaController(mediaController);
            if (headers != null)
                myVideoView.setVideoURI(video, headers);
            else 
                myVideoView.setVideoURI(video);
            myVideoView.requestFocus();
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    if(BridgeModule.duration!=0)
                     myVideoView.seekTo(BridgeModule.duration);
                    myVideoView.start();
                }
            });


        } catch (Exception e) {
            progressDialog.dismiss();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }

    }
}
