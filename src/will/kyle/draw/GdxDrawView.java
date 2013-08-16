package will.kyle.draw;

import will.kyle.UtilClass;
import will.kyle.engine.GameEngine;
import will.kyle.engine.GameEventManager;
import will.kyle.engine.GameEventManager.GAME_STATE;
import will.kyle.engine.LevelConfiguration;
import will.kyle.engine.parts.Ball;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GdxDrawView implements ApplicationListener, InputProcessor {
	
	private ShapeRenderer shareRenderer;

	private OrthographicCamera camera ;
	private SpriteBatch batch;
	private Sprite sprite;
	private GameEngine gameEngine;
	private GameEventManager gameEventManager;
	private LevelConfiguration levelConfig;	

	private final static String TAG = "GdxDrawView";
	
	private double wallHeight;
	private int ballColor;
	private int wallColor;
	private int wallSecondaryColor;

	public GdxDrawView(GameEngine gameEngine, GameEventManager gameEventManager)
	{
		this.gameEngine = gameEngine;
		this.gameEventManager = gameEventManager;
		intializeDrawView();
	}
	
	private void intializeDrawView()
	{		

		wallHeight = gameEngine.getWallHeight();
		levelConfig = gameEngine.getLevelConfig();

		wallColor = UtilClass.covertStringToColor(levelConfig.getPlatformColor());
		ballColor = UtilClass.covertStringToColor(levelConfig.getBallColor());
		wallSecondaryColor = UtilClass.covertStringToColor(levelConfig.getPlatformSecondaryColor());

//		setBackgroundImage(levelConfig.getBackgroundImage());
		initializeBalls();


		Log.d(TAG, "Game Engine Loaded");
	}
	
	private void initializeBalls()
	{		

		//iterate through each ball set normal image, and load death animation
		for(Ball ball : gameEngine.getArrayOfBalls())
		{			
			
			
//			//set normal bitmap
//			Bitmap bitMap = getScaledBitMap(ball.getBallImage(), ball.getRadius());
//			
//			ball.setScaledBitmap(bitMap);	

			//load death animation
//			ArrayList<Bitmap> deathAnimationForBall = new ArrayList<Bitmap>();
//			Bitmap deathBitmap;
//			for(String imageName : ball.getDeathAnimation())
//			{
//				deathBitmap = getScaledBitMap(imageName, ball.getRadius() );	
//				deathAnimationForBall.add(deathBitmap);
//			}			
//			ball.initializeBallDeathAnimationBitmap(deathAnimationForBall);

			//load spawn animation
//			ArrayList<Bitmap> spawnAnimationForBall = new ArrayList<Bitmap>();
//			Bitmap spawnBitmap;
//			for(String imageName : ball.getSpawnAnimation())
//			{
//				spawnBitmap = getScaledBitMap(imageName, ball.getRadius() );	
//				spawnAnimationForBall.add(spawnBitmap);
//			}			
//			ball.initializeBallSpawnAnimationBitmap(spawnAnimationForBall);

		}

	}
	
	
	public void create () {

//		Texture redBall = new Texture(Gdx.files.internal("ball_red.png"));
//
//
//		// TODO Auto-generated method stub
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
//
//		shareRenderer = new ShapeRenderer();
//		batch = new SpriteBatch();
//
//
//		// setting a filter is optional, default = Nearest
//		//texture.setFilter(Texture.!TextureFilter.Linear, Texture.!TextureFilter.Linear);
//
//
//		// binding texture to sprite and setting some attributes
//		sprite = new Sprite(redBall);
		
		initializeBalls();


	}

	public void render () {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		shareRenderer.setProjectionMatrix(camera.combined);
		shareRenderer.begin(ShapeType.Filled);
		shareRenderer.rect(100, 100, 100, 15);
		shareRenderer.end();

		batch.setProjectionMatrix(camera.combined);
		camera.update();

		batch.begin();
		sprite.draw(batch);


		batch.end();
	}


	@Override
	public void dispose() { }

	@Override
	public void pause() { }


	@Override
	public void resize(int width, int height) { }

	@Override
	public void resume() { }
	

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		
		
		if(gameEngine != null && gameEngine.hasBeenInitialized())
		{

			if(	gameEventManager.getGameState() == GAME_STATE.NOT_STARTED)
			{
				gameEventManager.startGame();
			}
			else if(gameEventManager.getGameState() == GAME_STATE.PLAYING)
			{
				//gameEventManager.pauseGame();
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

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
