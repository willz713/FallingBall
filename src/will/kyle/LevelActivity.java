package will.kyle;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import will.kyle.draw.LevelDrawView;
import will.kyle.engine.GameEngine;
import will.kyle.engine.GameEventManager;
import will.kyle.engine.GameEventManager.GAME_EVENT;
import will.kyle.engine.GameEventManager.GAME_STATE;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LevelActivity extends Activity implements SensorEventListener,Observer {

	private static final String TAG = "LevelActivity";
	private static final int VIBRATE_DURATION = 100;
	private static final int VIBRATE_LOSE_DURATION = 500;

	private LevelDrawView drawingView;
	private GameEngine gameEngine;
	private GameEventManager gameEventManager;
	private SensorManager sensorManager;
	private StepThread stepThread;
	private Timer timer;
	private TextView titleView;
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private int drawGameRate;
	private String titleText;
	private Button pauseButton;

	private boolean timerPaused;
	private boolean timerCancelled = false;

	MediaPlayer mediaPlayer;

	public static String PACKAGE_NAME;

	private int levelResourceId;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level);
		levelResourceId = getIntent().getIntExtra(TitleScreenActivity.START_LEVEL_WITH_RESOURCE_ID, R.raw.level1);
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "FALLING_BALL");
		setViewVariables();
		timerPaused = false;

		PACKAGE_NAME = getApplicationContext().getPackageName();

	}

	public void onWindowFocusChanged(boolean hasFocus) 
	{ 
		initializeGame();
	} 

	private void setViewVariables()
	{
		drawingView = (LevelDrawView) findViewById(R.id.drawing);
		titleView = (TextView) findViewById(R.id.levelNameTextView);
		pauseButton = (Button) findViewById(R.id.pauseResumeButton);
		
		//pauseButton.setOnTouchListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy){}

	public void onSensorChanged(SensorEvent event) 
	{		
		if(gameEngine != null && gameEngine.hasBeenInitialized())
		{
			float yValue = event.values[1];
			gameEngine.setBallXVelocity(yValue);		
		}
	}

	protected void onResume() 
	{
		Log.d(TAG,"Registering Sensor");
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager.SENSOR_DELAY_GAME);		
		wakeLock.acquire();		
		super.onResume();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		timerPaused = true;
		stopBackgroundMusic();
		Log.d(TAG,"Un Registering Sensor");
		sensorManager.unregisterListener(this);		
		wakeLock.release();
	}


	private class StepThread extends TimerTask {

		public void run() {	LevelActivity.this.runOnUiThread(new Runnable() {
			public void run() {   
				if(!timerPaused)
				{
					gameEngine.incrementEngine();	

					if(gameEngine.getStepCount() % drawGameRate == 0)
					{
						drawingView.invalidate();
					}	
				}}});
		}}


	private void initializeGame()
	{			

		if(gameEngine == null)
		{
			Log.d(TAG,"Attempting to Load Level");
			gameEventManager = new GameEventManager();
			gameEventManager.addObserver(this);
			
			gameEngine = new GameEngine(drawingView.getWidth(), drawingView.getHeight(),gameEventManager);	
			
			gameEngine.loadLevel(getResources().openRawResource(levelResourceId));	

			drawGameRate = gameEngine.getLevelConfig().getDrawScreenRate();
			drawingView.intializeDrawView(gameEngine,gameEventManager);

			titleText = gameEngine.getLevelConfig().getLevelTitle() + " - Points: ";
			titleView.setText(titleText);
		}
	}

	@Override
	public void update(Observable observable, Object data) 
	{	
		if(stepThread == null || timer == null || timerCancelled)
		{
			stepThread = new StepThread();
			timer = new Timer();
			timerCancelled = false;
		}

		if(data instanceof GAME_EVENT)
		{
			GAME_EVENT newGameState = (GAME_EVENT) data;

			if(newGameState == GAME_EVENT.STARTED)
			{			
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
				timer.schedule(stepThread, 0,gameEngine.getStepRateMili());
				playBackgroundMusic();
				
			}
			else if (newGameState == GAME_EVENT.WIN)
			{
				stopStepThread();
				stopBackgroundMusic();
				drawingView.invalidate();
			}
			else if (newGameState == GAME_EVENT.LOSE)
			{
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_LOSE_DURATION);
				stopStepThread();
				stopBackgroundMusic();
				drawingView.invalidate();
			}
			else if (newGameState == GAME_EVENT.ADD_POINT)
			{
				addPointsToPointTextView();
			}
			else if (newGameState == GAME_EVENT.PAUSE)
			{
				//Invalidate to draw the PAUSED string
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
				timerPaused = true;
				stopBackgroundMusic();
				drawingView.invalidate();
			}
			else if (newGameState == GAME_EVENT.UNPAUSE)
			{
				playBackgroundMusic();
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
				timerPaused = false;
			}
			else if (newGameState == GAME_EVENT.BALL_LOST)
			{
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
			}
			else if (newGameState == GAME_EVENT.BALL_LOST)
			{
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
			}
			else if (newGameState == GAME_EVENT.BALL_EXPLOAD)
			{
				playSound(R.raw.explosion);
			}
			else if (newGameState == GAME_EVENT.EXIT)
			{
				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
				this.finish();
			}

		}
	}

	//note once this is called you cannot un-pause it
	private void stopStepThread()
	{
		if(timer != null)
		{
			timer.cancel();
			timerCancelled = true;
		}
	}

	private void addPointsToPointTextView()
	{
		int points = gameEventManager.getPoints();
		titleView.setText(titleText + String.valueOf(points));
	}



	
	public void playBackgroundMusic()
	{
		mediaPlayer = MediaPlayer.create(LevelActivity.this, R.raw.peaceful); 
		mediaPlayer.setLooping(true); // Set looping 
		mediaPlayer.setVolume(30,30);
		mediaPlayer.start(); 
	}
	
	public void stopBackgroundMusic()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.release();
		}
	}
	

	public void playSound(int soundId)
	{
		MediaPlayer mPlayer = MediaPlayer.create(LevelActivity.this, soundId); 
		mPlayer.setVolume(100,100);
		mPlayer.start();
	}

	public void pauseClick(View v) {
		
		if(gameEngine != null && gameEngine.hasBeenInitialized())
		{
			if(gameEventManager.getGameState() == GAME_STATE.PLAYING)
			{
				Log.d(TAG, "un pausing Game");
				pauseButton.setText("Resume");
				gameEventManager.pauseGame();
			}
			else if(gameEventManager.getGameState() == GAME_STATE.PAUSED)
			{
				Log.d(TAG, "Pausing Game");
				pauseButton.setText("Pause");
				gameEventManager.unPauseGame();
			}
		}
		
	}




}