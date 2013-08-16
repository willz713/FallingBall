package will.kyle;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import will.kyle.draw.GdxDrawView;
import will.kyle.engine.GameEngine;
import will.kyle.engine.GameEventManager;
import will.kyle.engine.GameEventManager.GAME_EVENT;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.widget.Button;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class GdxLevelApplication extends AndroidApplication implements Observer {

	private int levelResourceId;
	private ApplicationListener gdxDrawView;
	private GameEngine gameEngine;
	private GameEventManager gameEventManager;
	private static final String TAG = "GdxLevelApplication";
	
	private SensorManager sensorManager;
//	private StepThread stepThread;
	private Timer timer;
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private int drawGameRate;
	private String titleText;
	private Button pauseButton;
	
	private StepThread stepThread;

	private boolean timerPaused;
	private boolean timerCancelled = false;

	MediaPlayer mediaPlayer;

	@Override
	public void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		levelResourceId = getIntent().getIntExtra(TitleScreenActivity.START_LEVEL_WITH_RESOURCE_ID, R.raw.level1);

		initializeGame();		
		
		  

	}
	
	private void initializeGame()
	{			

		if(gameEngine == null)
		{
			Log.d(TAG,"Attempting to Load Level");
			gameEventManager = new GameEventManager();
			gameEventManager.addObserver(this);
			
			Display display = getWindowManager().getDefaultDisplay();
			
			gameEngine = new GameEngine(display.getWidth(), display.getHeight(),gameEventManager);			
			gameEngine.loadLevel(getResources().openRawResource(levelResourceId));	
			drawGameRate = gameEngine.getLevelConfig().getDrawScreenRate();
			titleText = gameEngine.getLevelConfig().getLevelTitle() + " - Points: ";

			gdxDrawView = new GdxDrawView(gameEngine,gameEventManager);		
			initialize(gdxDrawView, false);   
			
		}
	}
	
	
	
	private class StepThread extends TimerTask {

		public void run() {	GdxLevelApplication.this.runOnUiThread(new Runnable() {
			public void run() {   
				if(!timerPaused)
				{
					gameEngine.incrementEngine();	

					if(gameEngine.getStepCount() % drawGameRate == 0)
					{
						gdxDrawView.render();
					}	
				}}});
		}}
	

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
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
				timer.schedule(stepThread, 0,gameEngine.getStepRateMili());
//				playBackgroundMusic();
				
			}
			else if (newGameState == GAME_EVENT.WIN)
			{
				stopStepThread();
//				stopBackgroundMusic();
//				drawingView.invalidate();
			}
			else if (newGameState == GAME_EVENT.LOSE)
			{
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_LOSE_DURATION);
				stopStepThread();
//				stopBackgroundMusic();
//				drawingView.invalidate();
			}
			else if (newGameState == GAME_EVENT.ADD_POINT)
			{
//				addPointsToPointTextView();
			}
			else if (newGameState == GAME_EVENT.PAUSE)
			{
				//Invalidate to draw the PAUSED string
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
//				timerPaused = true;
//				stopBackgroundMusic();
//				drawingView.invalidate();
			}
			else if (newGameState == GAME_EVENT.UNPAUSE)
			{
//				playBackgroundMusic();
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
//				timerPaused = false;
			}
			else if (newGameState == GAME_EVENT.BALL_LOST)
			{
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
			}
			else if (newGameState == GAME_EVENT.BALL_LOST)
			{
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
			}
			else if (newGameState == GAME_EVENT.BALL_EXPLOAD)
			{
//				playSound(R.raw.explosion);
			}
			else if (newGameState == GAME_EVENT.EXIT)
			{
//				((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(VIBRATE_DURATION);
//				this.finish();
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
}