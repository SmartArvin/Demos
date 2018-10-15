package com.ktc.videoplay;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnClickListener 
		, OnPreparedListener , OnInfoListener , OnCompletionListener{

	private VideoView mVideoView;
	private EditText edit_url ;
	private TextView txt_progress , txt_status;
	private MediaController mMediaController ;
	private String videoUrl = "http://vodresource.cleartv.cn/movies/0151f60b7fea76ed0702a6188af12685_150122303051_transcoded.mp4";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File mFile = new File("/system/bin/bk_ba_cache.sh");
		if(mFile.exists()){
			
		}
        
        mVideoView = (VideoView) findViewById(R.id.videoView); 
        edit_url = (EditText) findViewById(R.id.edit_url);
        txt_status = (TextView) findViewById(R.id.txt_status);
        txt_progress = (TextView) findViewById(R.id.txt_progress);
        edit_url.setText(videoUrl);
        playVideo(edit_url.getText().toString());
    }
    
    private void playVideo(String pathUrl){
    	Uri uri = Uri.parse(pathUrl);
    	mMediaController = new MediaController(this);
    	mVideoView.setMediaController(mMediaController);  
    	mVideoView.setVideoURI(uri);  
    	mVideoView.start();  
    	mVideoView.setOnCompletionListener(this);
    	mVideoView.setOnPreparedListener(this);
    	mVideoView.setOnInfoListener(this);
    	mMediaController.show();
    }
    
    @Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_play:
			playVideo(edit_url.getText().toString());
			break;

		default:
			break;
		}
	}
    
    @Override
	public void onPrepared(MediaPlayer mMediaPlayer) {
		mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				refreshProgress("已播放："+mVideoView.getCurrentPosition()+"/"+mVideoView.getDuration());
             }
         });
	}
	
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
        handleOnInfo(mp, what) ;
        return true;
    }
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		Toast.makeText(getApplicationContext(), "播放结束~~~", Toast.LENGTH_SHORT).show();
	}
	
    private void handleOnInfo(MediaPlayer mp, int what){
        switch (what){
        	case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://视频开始播放
        		refreshStatus("视频开始播放...");
        		break ;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始缓冲(视频卡顿)
                refreshStatus("视频卡顿，正在缓冲...");
                break ;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束(视频可以播放)
                refreshStatus("缓冲结束，正在播放...");
                break ;
        }
    }
    
    private void refreshStatus(String content){
    	txt_status.setText(content);
    }
    
    private void refreshProgress(String content){
    	txt_progress.setText(content);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoView != null){
            mVideoView.stopPlayback();
            mVideoView = null ;
        }
    }

}
