package dl.recorder.videorecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements Callback
{
	private Camera mCamera;

	private Button beginBtn;
	private Button stopBtn;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private MediaRecorder mRecorder;

	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		beginBtn = (Button) findViewById(R.id.begin);
		stopBtn = (Button) findViewById(R.id.stop);
		stopBtn.setEnabled(false);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setFixedSize(480, 800);

		initCamera();

		beginBtn.setOnClickListener(new RecordOnClickListener());
		stopBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;

				stopBtn.setEnabled(false);
				beginBtn.setEnabled(true);

				Toast.makeText(MainActivity.this, "保存到" + path,
						Toast.LENGTH_LONG).show();
				
				if (mCamera != null)
				{
					try
					{
						mCamera.reconnect();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				mCamera.startPreview();
				
			}
		});
	}

	private class RecordOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCamera.stopPreview();
			mCamera.unlock();
			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			path = Environment.getExternalStorageDirectory() + File.separator
					+ format.format(new Date()) + ".3gp";
			File file = new File(path);
			mRecorder = new MediaRecorder();
			mRecorder.reset(); 
			mRecorder.setCamera(mCamera);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(file.getAbsolutePath());
			mRecorder.setVideoSize(320, 240);
			mRecorder.setVideoFrameRate(10);
			mRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
			try
			{
				mRecorder.prepare();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			mRecorder.start();

			stopBtn.setEnabled(true);
			beginBtn.setEnabled(false);
		}
	}

	private void initCamera()
	{
		try
		{
			mCamera = Camera.open();
			Camera.Parameters camParms = mCamera.getParameters();
			mCamera.lock();
			mCamera.setDisplayOrientation(90); // 旋转90度
			mCamera.setParameters(camParms);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		try
		{
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		mCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		// TODO Auto-generated method stub
	}

}
