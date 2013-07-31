package will.kyle.engine;

import java.util.Observable;

import android.util.Log;

public class GameEventManager extends Observable
{

	private int points = 0;
	private static final String TAG = "GameEventManager";
	private static final int POINT_INCREMENT = 10;

	private GAME_STATE gameState = GAME_STATE.NOT_STARTED;


	public enum GAME_STATE
	{
		NOT_STARTED,PLAYING,WON,LOST,PAUSED;
	}

	public enum GAME_EVENT
	{
		ADD_POINT,WIN,LOSE,STARTED,PAUSE,UNPAUSE,EXIT,BALL_LOST,BALL_EXPLOAD;
	}


	public void resetGameManager()
	{
		points = 0;
	}
	
	public int getPoints()
	{
		return points;
	}


	public void startGame() {

		Log.d(TAG,"Starting Level...");		
		this.gameState = GAME_STATE.PLAYING;
		this.setChanged();
		this.notifyObservers(GAME_EVENT.STARTED);

	}

	public void winGame()
	{
		gameState = GAME_STATE.WON;
		this.setChanged();
		this.notifyObservers(GAME_EVENT.WIN);
	}

	public void loseGame()
	{
		gameState = GAME_STATE.LOST;
		this.setChanged();
		this.notifyObservers(GAME_EVENT.LOSE);
	}

	public void addPoint()
	{
		points = points + POINT_INCREMENT;
		this.setChanged();
		this.notifyObservers(GAME_EVENT.ADD_POINT);
	}

	public void pauseGame()
	{
		gameState = GAME_STATE.PAUSED;
		this.setChanged();
		this.notifyObservers(GAME_EVENT.PAUSE);
	}

	public void unPauseGame()
	{
		gameState = GAME_STATE.PLAYING;
		this.setChanged();
		this.notifyObservers(GAME_EVENT.UNPAUSE);
	}

	public GAME_STATE getGameState()
	{
		return gameState;
	}

	public void ballExpload()
	{		
		this.setChanged();
		this.notifyObservers(GAME_EVENT.BALL_EXPLOAD);
	}
	
	public void ballLost()
	{
		this.setChanged();
		this.notifyObservers(GAME_EVENT.BALL_LOST);
	}

	public void exit() {
		this.setChanged();
		this.notifyObservers(GAME_EVENT.EXIT);
	}
}
