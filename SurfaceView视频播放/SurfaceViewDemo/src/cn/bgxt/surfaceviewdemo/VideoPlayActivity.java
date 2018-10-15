package cn.bgxt.surfaceviewdemo;

import java.io.File;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.TimedText;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class VideoPlayActivity extends Activity {
	private final String TAG = "yzh";
	private EditText et_path;
	private SurfaceView sv;
	private Button btn_play, btn_pause, btn_replay, btn_stop , btn_subtitle;
	private TextView txt_subtitle;
	private MediaPlayer mediaPlayer;
	private SeekBar seekBar;
	private int currentPosition = 0;
	private boolean isPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		seekBar = (SeekBar) findViewById(R.id.seekBar);
		sv = (SurfaceView) findViewById(R.id.sv);
		et_path = (EditText) findViewById(R.id.et_path);
		
		txt_subtitle = (TextView) findViewById(R.id.txt_subtitle);

		btn_play = (Button) findViewById(R.id.btn_play);
		btn_pause = (Button) findViewById(R.id.btn_pause);
		btn_replay = (Button) findViewById(R.id.btn_replay);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_subtitle = (Button) findViewById(R.id.btn_subtitle);

		btn_play.setOnClickListener(click);
		btn_pause.setOnClickListener(click);
		btn_replay.setOnClickListener(click);
		btn_stop.setOnClickListener(click);
		btn_subtitle.setOnClickListener(click);
		
		// ΪSurfaceHolder��ӻص�
		sv.getHolder().addCallback(callback);
		
		// 4.0�汾֮����Ҫ���õ�����
		// ����Surface��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵�����
		// sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		// Ϊ��������ӽ��ȸ����¼�
		seekBar.setOnSeekBarChangeListener(change);
	}

	private Callback callback = new Callback() {
		// SurfaceHolder���޸ĵ�ʱ��ص�
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG, "SurfaceHolder ������");
			// ����SurfaceHolder��ʱ���¼��ǰ�Ĳ���λ�ò�ֹͣ����
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				currentPosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i(TAG, "SurfaceHolder ������");
			if (currentPosition > 0) {
				// ����SurfaceHolder��ʱ����������ϴβ��ŵ�λ�ã������ϴβ���λ�ý��в���
				play(currentPosition);
				currentPosition = 0;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.i(TAG, "SurfaceHolder ��С���ı�");
		}

	};

	private OnSeekBarChangeListener change = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// ��������ֹͣ�޸ĵ�ʱ�򴥷�
			// ȡ�õ�ǰ�������Ŀ̶�
			int progress = seekBar.getProgress();
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				// ���õ�ǰ���ŵ�λ��
				mediaPlayer.seekTo(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

		}
	};

	private View.OnClickListener click = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_play:
				play(0);
				break;
			case R.id.btn_pause:
				pause();
				break;
			case R.id.btn_replay:
				replay();
				break;
			case R.id.btn_stop:
				stop();
				break;
			case R.id.btn_subtitle:
				listenerSubtitle();
				break;
			default:
				break;
			}
		}
	};


	/*
	 * ֹͣ����
	 */
	protected void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			btn_play.setEnabled(true);
			isPlaying = false;
		}
	}

	/**
	 * ��ʼ����
	 * 
	 * @param msec ���ų�ʼλ��    
	 */
	protected void play(final int msec) {
		// ��ȡ��Ƶ�ļ���ַ
		String path = et_path.getText().toString().trim();
		File file = new File(path);
		if (!file.exists()) {
			Toast.makeText(this, "��Ƶ�ļ�·������", 0).show();
			return;
		}
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// ���ò��ŵ���ƵԴ
			mediaPlayer.setDataSource(file.getAbsolutePath());
			// ������ʾ��Ƶ��SurfaceHolder
			mediaPlayer.setDisplay(sv.getHolder());
			Log.i(TAG, "��ʼװ��");
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.i(TAG, "װ�����");
					mediaPlayer.start();
					// ���ճ�ʼλ�ò���
					mediaPlayer.seekTo(msec);
					// ���ý�������������Ϊ��Ƶ������󲥷�ʱ��
					seekBar.setMax(mediaPlayer.getDuration());
					// ��ʼ�̣߳����½������Ŀ̶�
					new Thread() {

						@Override
						public void run() {
							try {
								isPlaying = true;
								while (isPlaying) {
									int current = mediaPlayer
											.getCurrentPosition();
									seekBar.setProgress(current);
									
									sleep(500);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();

					btn_play.setEnabled(false);
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// �ڲ�����ϱ��ص�
					btn_play.setEnabled(true);
				}
			});

			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// �����������²���
					play(0);
					isPlaying = false;
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ���¿�ʼ����
	 */
	protected void replay() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.seekTo(0);
			Toast.makeText(this, "���²���", 0).show();
			btn_pause.setText("��ͣ");
			return;
		}
		isPlaying = false;
		play(0);
		

	}

	/**
	 * ��ͣ�����
	 */
	protected void pause() {
		if (btn_pause.getText().toString().trim().equals("����")) {
			btn_pause.setText("��ͣ");
			mediaPlayer.start();
			Toast.makeText(this, "��������", 0).show();
			return;
		}
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			btn_pause.setText("����");
			Toast.makeText(this, "��ͣ����", 0).show();
		}
	}
	
	protected void listenerSubtitle(){
		Log.i(TAG, "-----listenerSubtitle----------");
		/*try {
			mediaPlayer.addTimedTextSource(path, MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
		} catch (IOException e) {
		    e.printStackTrace();
		}*/
		MediaPlayer.TrackInfo[] trackInfos = mediaPlayer.getTrackInfo();
		 
		if (trackInfos != null && trackInfos.length > 0) {
		    for (int i = 0; i < trackInfos.length; i++) {
		        final MediaPlayer.TrackInfo info = trackInfos[i];
		 
		        Log.i(TAG, "TrackInfo: " + info.getTrackType() + " "+ info.getLanguage());
		 
		        if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
		            // mMediaPlayer.selectTrack(i);
		        } else if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT) {
		        	mediaPlayer.selectTrack(i);
		        }
		    }
		}
		mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
			@Override
			public void onTimedText(MediaPlayer mp, TimedText text) {
				if (text != null) {
					txt_subtitle.setText("");
					txt_subtitle.setText(text.getText().toString());
		            Log.i(TAG, "text = " + text.getText());
		        }
			}
		});
	}

}
