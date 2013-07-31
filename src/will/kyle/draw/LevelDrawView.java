package will.kyle.draw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import will.kyle.LevelActivity;
import will.kyle.UtilClass;
import will.kyle.engine.GameEngine;
import will.kyle.engine.GameEventManager;
import will.kyle.engine.GameEventManager.GAME_STATE;
import will.kyle.engine.LevelConfiguration;
import will.kyle.engine.parts.Ball;
import will.kyle.engine.parts.Platform;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;

public class LevelDrawView extends View implements OnTouchListener
{	
	private static final String TAP_SCREEN_TO_START = "TAP Screen to Start";
	private static final String YOU_WIN_STRING = "You Win!, Tap Screen to Go Back";
	private static final String YOU_LOSE_STRING = "You Lose, Tap Screen to Retry";
	private static final String PAUSED_STRING = "Game Paused";
	private static final float DRAW_WALL_INSET_BUFFER = 4;
	
	private static final int TEXT_SIZE = 50;
	private static final int SUBTITLE_TEXT_SIZE = 30;
	private static final int TEXT_BUFFER = 10;
	private static final String TAG = "LevelDrawView";

	private Paint paint;
	private GameEngine gameEngine;
	private GameEventManager gameEventManager;
	private LevelConfiguration levelConfig;	

	private double wallHeight;
	private int ballColor;
	private int wallColor;
	private int wallSecondaryColor;

	public LevelDrawView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);

	}
	public LevelDrawView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setOnTouchListener(this);
	}
	public LevelDrawView(Context context)
	{
		super(context);
		this.setOnTouchListener(this);
	}

	public void intializeDrawView(GameEngine gameEngine, GameEventManager gameEventManager)
	{		
		this.gameEventManager = gameEventManager;
		this.gameEngine = gameEngine;
		wallHeight = gameEngine.getWallHeight();

		levelConfig = gameEngine.getLevelConfig();

		wallColor = UtilClass.covertStringToColor(levelConfig.getPlatformColor());
		ballColor = UtilClass.covertStringToColor(levelConfig.getBallColor());
		wallSecondaryColor = UtilClass.covertStringToColor(levelConfig.getPlatformSecondaryColor());

		setBackgroundImage(levelConfig.getBackgroundImage());
		initializeBalls();
		this.invalidate();

		Log.d(TAG, "Game Engine Loaded");
	}
	


	private void initializeBalls()
	{		

		//iterate through each ball set normal image, and load death animation
		for(Ball ball : gameEngine.getArrayOfBalls())
		{
			//set normal bitmap
			Bitmap bitMap = getScaledBitMap(ball.getBallImage(), ball.getRadius());
			
			ball.setScaledBitmap(bitMap);	

			//load death animation
			ArrayList<Bitmap> deathAnimationForBall = new ArrayList<Bitmap>();
			Bitmap deathBitmap;
			for(String imageName : ball.getDeathAnimation())
			{
				deathBitmap = getScaledBitMap(imageName, ball.getRadius() );	
				deathAnimationForBall.add(deathBitmap);
			}			
			ball.initializeBallDeathAnimationBitmap(deathAnimationForBall);

			//load spawn animation
			ArrayList<Bitmap> spawnAnimationForBall = new ArrayList<Bitmap>();
			Bitmap spawnBitmap;
			for(String imageName : ball.getSpawnAnimation())
			{
				spawnBitmap = getScaledBitMap(imageName, ball.getRadius() );	
				spawnAnimationForBall.add(spawnBitmap);
			}			
			ball.initializeBallSpawnAnimationBitmap(spawnAnimationForBall);

		}

	}

	private void createPaintObjects() 
	{	
		paint = new Paint();	
	}	

	protected void onDraw(Canvas canvas)
	{
		if (paint == null)
		{
			createPaintObjects();
		}

		if(gameEngine != null && gameEventManager != null)
		{

			if(gameEngine.hasBeenInitialized() && 
					gameEventManager.getGameState() == GAME_STATE.PLAYING)
			{
				drawBalls(canvas);
				drawWalls(canvas);
			}
			else if(gameEventManager.getGameState() == GAME_STATE.NOT_STARTED)
			{
				drawTextOnScreen(canvas, levelConfig.getLevelTitle(), TEXT_SIZE, - TEXT_SIZE - TEXT_BUFFER,Color.BLACK);
				drawTextOnScreen(canvas, levelConfig.getLevelSubTitle(), SUBTITLE_TEXT_SIZE, 0,Color.BLACK);
				drawTextOnScreen(canvas, TAP_SCREEN_TO_START,SUBTITLE_TEXT_SIZE,SUBTITLE_TEXT_SIZE + TEXT_BUFFER,Color.BLACK);
			}
			else if(gameEventManager.getGameState() == GAME_STATE.WON)
			{
				drawBalls(canvas);
				drawWalls(canvas);		
				drawTextOnScreen(canvas, YOU_WIN_STRING,TEXT_SIZE,0,Color.GREEN);
			}
			else if(gameEventManager.getGameState() == GAME_STATE.LOST)
			{
				drawBalls(canvas);
				drawWalls(canvas);
				drawTextOnScreen(canvas, YOU_LOSE_STRING,TEXT_SIZE,0,Color.RED);
			}
			else if(gameEventManager.getGameState() == GAME_STATE.PAUSED)
			{
				drawBalls(canvas);
				drawWalls(canvas);
				drawTextOnScreen(canvas, PAUSED_STRING,TEXT_SIZE,0,Color.BLACK);
			}
		}
	}


	private void drawBalls(Canvas canvas)
	{

		if(gameEngine != null)
		{
			paint.setColor(ballColor);
			ConcurrentLinkedQueue<Ball> balls = gameEngine.getArrayOfBalls();

			for(Ball ball : balls)
			{
				canvas.drawBitmap(ball.getBitmapForBall(), 
						(float) ( ball.getxCoord() - ball.getRadius()),
						(float) (ball.getyCoord() - ball.getRadius()), 
						paint);

//				canvas.drawCircle((float)ball.getxCoord(), (float)ball.getyCoord(), (float)ball.getRadius(), paint);

			}

		}

	}

	private void drawWalls(Canvas canvas)
	{
		if(gameEngine != null)
		{
			//paint.setColor(wallColor);

			ConcurrentLinkedQueue<Platform> walls = gameEngine.getArrayOfCurrentWalls();
			ArrayList<Integer> xCoords;

			for(Platform wall : walls)
			{
				xCoords = wall.getLineXValues();

				for(int i = 0; i <xCoords.size(); i = i + 2)
				{				
					//draw outer wall
					paint.setColor(wallColor);
					canvas.drawRect(
							(float)xCoords.get(i), 
							(float)wall.getyCoord(), 
							(float)xCoords.get(i+1), 
							(float)(wall.getyCoord() + wallHeight),
							paint);

					//draw inner wall
					paint.setColor(wallSecondaryColor);					
					canvas.drawRect(
							(float)(xCoords.get(i) + DRAW_WALL_INSET_BUFFER), 
							(float)(wall.getyCoord() + DRAW_WALL_INSET_BUFFER), 
							(float)(xCoords.get(i+1) - DRAW_WALL_INSET_BUFFER), 
							(float)(wall.getyCoord() + wallHeight - DRAW_WALL_INSET_BUFFER),
							paint);					
				}
			}
		}
	}	

	
	public void drawPauseButton()
	{
		
	}


	public boolean onTouch(View v, android.view.MotionEvent event) 
	{
		

		if(gameEngine != null && gameEngine.hasBeenInitialized())
		{

			if(	gameEventManager.getGameState() == GAME_STATE.NOT_STARTED)
			{
				gameEventManager.startGame();
			}
			else if(gameEventManager.getGameState() == GAME_STATE.PLAYING)
			{
				//gameEventManager.pauseGame();
				//TODO set balls velocity maybe here?
			}
//			else if(gameEventManager.getGameState() == GAME_STATE.PAUSED)
//			{
//				gameEventManager.unPauseGame();
//			}
			else if(gameEventManager.getGameState() == GAME_STATE.WON)
			{
				gameEventManager.exit();
			}
			else if(gameEventManager.getGameState() == GAME_STATE.LOST)
			{
				gameEventManager.resetGameManager();
				gameEngine.restartGame();
			}
		}
		return false;
	}
	


	private void drawTextOnScreen(Canvas canvas,String text, int textSize, int bufferFromCenter, int color)
	{
		float canvasWidth;
		float sentenceWidth;
		float startPositionX;

		paint.setColor(color);
		paint.setTextSize(textSize);		
		canvasWidth = canvas.getWidth();
		sentenceWidth = paint.measureText(text);
		startPositionX = (canvasWidth - sentenceWidth) / 2;			
		canvas.drawText(text,startPositionX, getHeight()/2 + bufferFromCenter, paint); 
	}

	private void setBackgroundImage(String uri) 
	{
		int imageResource = getResources().getIdentifier(uri, null, LevelActivity.PACKAGE_NAME);
		Drawable image = getResources().getDrawable(imageResource);
		setBackgroundDrawable(image);
	}


	
	public Bitmap getScaledBitMap(String uri, double radius)
	{
		BitmapScaler scaler = null;
		try {
			
			scaler = new BitmapScaler(
					getResources(),
					getResources().getIdentifier(uri,null, LevelActivity.PACKAGE_NAME),
					(int)(radius * 2 ));
			
		} catch (IOException e) {
			Log.d(TAG,"Failed to scale bitmap:" + uri + " " + e.getLocalizedMessage());
		}
		
		return scaler.getScaled();
	}
	



}