package jp.co.tis.tc.command.master;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MasterActivity extends Activity {
	private static final String COMMAND_MASTER_TAG = "jp.co.tis.tc.command.master";
	private static final int FREQUENCY = 8000;
	private static final float LIMIT = 1000f;
	
	private static final int REQUEST_CODE = 1;
	
	private Recorder recorder = null;
	
	private boolean pauseFlg = false;
	
	private String address;
	private int port;
	private Socket socket;
	private BufferedWriter writer;
	
	private GestureDetector gd;
	private ScaleGestureDetector sgd;
	
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.master);
		
		Intent intent = getIntent();
		address = intent.getStringExtra("address");
		port = intent.getIntExtra("port", 0);
		
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseFlg = !pauseFlg;
				((Button)findViewById(R.id.button1)).setText(pauseFlg ? "Pause" : "Recording");
				recorder.setPaused(pauseFlg);
			}
		});
		gd = new GestureDetector(this, new MasterOnGestureListener());
		sgd = new ScaleGestureDetector(this, new MasterOnScaleGestureListener());
		
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
			@Override
			public void onInit(int status) {
				if (status != TextToSpeech.SUCCESS) {
					throw new RuntimeException("Could not initialize TTS");
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(address, port));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			Log.i(COMMAND_MASTER_TAG, String.format("socket opened to %s:%d%n", socket.getInetAddress().getHostAddress(), socket.getPort()));
		} catch (IOException e) {
			String mes = String.format("socket open fail : %s", e.getMessage());
			Log.e(COMMAND_MASTER_TAG, mes);
			Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (recorder != null) {
			recorder.terminate();
		}
		recorder = new Recorder();
		recorder.start();
	}
	
	@Override
	protected void onPause() {
		recorder.terminate();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		try {
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			Log.i(COMMAND_MASTER_TAG, "socket closed");
		} catch (IOException e) {
			String mes = String.format("socket close fail : %s", e.getMessage());
			Log.e(COMMAND_MASTER_TAG, mes);
			Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	private void onStartNoise() {
		Log.d(COMMAND_MASTER_TAG, "onStartNoise");
	}
	
	private void onNoising() {
		Log.d(COMMAND_MASTER_TAG, "onNoising");		
	}
	
	private void onStopNoise() {
		Log.d(COMMAND_MASTER_TAG, "onStopNoise");
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.recognize_label));
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(COMMAND_MASTER_TAG, "onActivityResult");
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (candidates.size() > 0) {
				Command command = Command.parceVoice(candidates);
				int result = tts.setLanguage(Locale.JAPAN);
				if (!"".equals(command.getMessage())) {
					send(command.getMessage());
					if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
						tts.speak(command.getVoice(), TextToSpeech.QUEUE_FLUSH, null);
					}
					else {
						Toast.makeText(this, "can't speakback in this locale", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
						tts.speak(getString(R.string.recognize_error), TextToSpeech.QUEUE_FLUSH, null);
					}
					else {
						Toast.makeText(this, "can't speakback in this locale", Toast.LENGTH_SHORT).show();
					}
				}
				Toast.makeText(this, command.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class Recorder extends Thread {
		private static final int CHANNEL_CONFIGURATION = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		
		private float beforeAvg = -1;
		private volatile boolean isRecording = true;
		private volatile boolean isPaused;
		private Object lock = new Object();
		
		@Override
		public void run() {
			Log.i(COMMAND_MASTER_TAG, "Recorder loop run");
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			int bufferRead = 0;
			int bufferSize = AudioRecord.getMinBufferSize(
					FREQUENCY, 
					CHANNEL_CONFIGURATION, 
					AUDIO_ENCODING);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					FREQUENCY,
					CHANNEL_CONFIGURATION,
					AUDIO_ENCODING,
					bufferSize);
			short[] buffer = new short[bufferSize];
			while(isRecording) {
				synchronized (lock) {
					if (isPaused) {
						Log.i(COMMAND_MASTER_TAG, "Recorder pause on");
						if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
							audioRecord.stop();
						}
						try {
							lock.wait();
							Log.i(COMMAND_MASTER_TAG, "Recorder pause off");
						}
						catch (InterruptedException ignore) {
							Log.i(COMMAND_MASTER_TAG, "recorder interrupted");
						}
					}
				}
				if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
					audioRecord.startRecording();
				}
				bufferRead = audioRecord.read(buffer, 0, bufferSize);
				if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) throw new IllegalStateException("AudioRecord.ERROR_INVALID_OPERATION");
				if (bufferRead == AudioRecord.ERROR_BAD_VALUE) throw new IllegalStateException("AudioRecord.ERROR_BAD_VALUE");
				if (bufferRead == AudioRecord.ERROR) throw new IllegalStateException("AudioRecord.ERROR");
				
				long sum = 0;
				for (short s : buffer) {
					sum += Math.abs((long)s);
				}
				float avg = (float)sum / (float)bufferSize;
				if (beforeAvg > LIMIT) {
					if (avg > LIMIT) {
						onNoising();
					}
					else {
						onStopNoise();
					}
				}
				else {
					if (avg > LIMIT) {
						onStartNoise();
					}
				}
				beforeAvg = avg;
			}
			if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
				audioRecord.stop();
			}
			audioRecord.release();
			Log.i(COMMAND_MASTER_TAG, "Recorder loop end");
		}
		public void setPaused(boolean isPaused) {
			synchronized (lock) {
				this.isPaused = isPaused;
				if (!isPaused) {
					lock.notify();
				}
			}
		}
		public void terminate() {
			Log.i(COMMAND_MASTER_TAG, "Recorder terminate");
			synchronized (lock) {
				this.isRecording = false;
			}
			interrupt();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() == 1) {
			gd.onTouchEvent(event);
		}
		else if (event.getPointerCount() == 2) {
			sgd.onTouchEvent(event);
		}
		return true;
	}
	
	private class MasterOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			send("ZOOM_FIT\n");
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			send(String.format("FLING,vx=%f,vy=%f\n", velocityX, velocityY));
			return true;
		}
	}

	private class MasterOnScaleGestureListener extends SimpleOnScaleGestureListener {
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			float scaleFactor = detector.getScaleFactor();
			if (scaleFactor > 1.0f) {
				send("ZOOM_IN\n");
			}
			else if (scaleFactor < 1.0f) {
				send("ZOOM_OUT\n");
			}
		}
	}
	
	private void send(String message) {
		try {
			writer.write(message);
			writer.flush();
			Log.i(COMMAND_MASTER_TAG, String.format("send message => %s", message));
		} catch (IOException e) {
			String mes = String.format("socket send fail %s : %s", message, e.getMessage());
			Log.e(COMMAND_MASTER_TAG, mes);
			Toast.makeText(MasterActivity.this, mes, Toast.LENGTH_SHORT).show();
		}
	}
}
