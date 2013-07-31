package will.kyle.engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import will.kyle.UtilClass;
import will.kyle.engine.parts.Ball;
import will.kyle.engine.parts.Ball.BALL_ANIMATION_STATE;
import will.kyle.engine.parts.Ball.BALL_STATE;
import will.kyle.engine.parts.Platform;
import android.util.Log;

public class GameEngine {

	private static final String TAG = "GameEngine";

	private static final int MAX_SEPERATE_COUNT = 15;
	private static final double BALL_COLLISION_RANGE = 15;

	//constants based on screen size
	private double GROUND_COLLISION_RANGE =  8;
	private double NO_BOUNCE_GROUND_THRESHOLD = .5f;
	private double BALL_COLLISION_BOTTOM_PLATFORM_RANGE =  15;

	private ConcurrentLinkedQueue<Platform> arrayOfPlatformsOnScreen;
	private ConcurrentLinkedQueue<Ball> arrayOfBalls;
	private LevelConfiguration level;
	private GameEventManager gameEventManager;

	private boolean hasBeenInitialized;	
	private boolean noWalls;
	private boolean infiniteMode;

	private double platformYVelocity_ratio;
	private double playformVelocity;
	private double platformHeight;
	private double stepsBetweenPlatforms;
	private double platformAcceleration;

	private double platformStepsBetweenAccel;
	private double platformStepsBetweenMinimum;

	private double collisionEnergyLost;
	private double gravity;	

	private int screenWidth;
	private int screenHeight;
	private int stepRateMili;

	private int platformCounter = 0;
	private int counterSinceLastPlatform = 0;
	private int stepCount = 0;
	

	public GameEngine(int maxWidth, int maxHeight, GameEventManager gameEventManager) 
	{
		this.screenWidth = maxWidth;
		this.screenHeight = maxHeight;
		hasBeenInitialized = false;
		arrayOfPlatformsOnScreen = new ConcurrentLinkedQueue<Platform>();
		arrayOfBalls = new ConcurrentLinkedQueue<Ball>();
		this.gameEventManager = gameEventManager;

		Log.d(TAG, "Game engine created.");
		Log.d(TAG, "Screen Height:" + maxHeight);
		Log.d(TAG, "Screen Width:" + maxWidth);
	}

	public void loadLevel(InputStream inputStream)
	{	

		try 
		{
			String fileContents = UtilClass.loadFileContents(inputStream);			
			level = (LevelConfiguration) UtilClass.unmarshellXML(LevelConfiguration.class, fileContents);

			loadLevelParams();
			loadBalls(level.getBallArray());
			//this should only be done once
			setBallRatios();
			setBallStartStates();

			hasBeenInitialized = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.d(TAG,"Unable to load level" + e.getLocalizedMessage());
		}

	}

	private void loadLevelParams()
	{		
		platformCounter = 0;
		counterSinceLastPlatform = 0;
		stepCount = 0;

		noWalls = level.isNoWalls();
		infiniteMode = level.isInfiniteMode();
		stepsBetweenPlatforms = level.getDistanceBetweenPlatforms();
		stepRateMili = level.getStepRateMili();
		platformHeight = level.getPlatformHeight();
		gravity = level.getGravity();


		platformYVelocity_ratio = level.getPlatformVelocity();		

		collisionEnergyLost = level.getCollisionEnergyLost();
		platformStepsBetweenAccel = level.getPlatformAccelDistanceBetween();
		platformStepsBetweenMinimum = level.getPlatformMinDistanceBetween();
		platformAcceleration = level.getPlatformAcceleration();

		playformVelocity = screenHeight / platformYVelocity_ratio;
		gravity = screenHeight / gravity;

		Log.d(TAG, "Platform Y velocity:" + playformVelocity);
		Log.d(TAG, "Gravity:" +  gravity);

		arrayOfPlatformsOnScreen.clear();	

	}


	private void setBallStartStates()
	{
		int  i = 0;

		for(Ball ball : arrayOfBalls)
		{	
			ball.resetBall();
			
			//set ball starting position
			double xPos = screenWidth/(arrayOfBalls.size() + 1) * (i + 1);
			double yPos = screenHeight / 2 ;	
			ball.setXYPosition(xPos, yPos);	
			i++;
		}
	}
	
	private void setBallRatios()
	{
		
		double ballXVelocityRatio = 0;
		double ballMaxXVelocityRatio = 0;
		double ballMaxYVelocityRatio = 0;
		
		for(Ball ball : arrayOfBalls)
		{				
			double newRadius = (((double)screenWidth * (double)screenHeight ) / 696000 ) * ball.getRadius();
			
			Log.d(TAG, "Ball radius:" + newRadius);			
			ball.setRadius(newRadius);			
			
			//set individual ball ratios based on screen size
			ballXVelocityRatio = ball.getBallXVelocityRatio();
			ballXVelocityRatio = screenWidth / ballXVelocityRatio;
			ball.setBallXVelocityRatio(ballXVelocityRatio);			

			ballMaxYVelocityRatio = gravity * ball.getMaxYVelocityRatio();
			ball.setMaxYVelocityRatio(ballMaxYVelocityRatio);

			ballMaxXVelocityRatio = ball.getMaxXVelocityRatio() * ball.getBallXVelocityRatio();
			ball.setMaxVelocityRatio(ballMaxXVelocityRatio);		
		}	
	}

	private void loadBalls(ArrayList<Ball> ballArray)
	{		
		for(Ball ball : ballArray)
		{
			arrayOfBalls.add(ball);			
		}		
	}

	public void restartGame() 
	{
		loadLevelParams();
		loadBalls(level.getBallArray());
		setBallStartStates();
		gameEventManager.resetGameManager();
		gameEventManager.startGame();
	}


	public void incrementEngine()
	{
		
		//need to do this first
		removeDeadBallsCheckForLose();

		stepPlatforms();

		clearBallCollisions();
		for(Ball ball : arrayOfBalls)
		{

			if(arrayOfBalls.size() > 1)
			{
				checkForCollisionsWithOtherBalls(ball);
			}			

			checkCollisionWithPlatforms(ball);
			setBallState(ball);				

			ball.stepBall();
			putBallInBoundsAndSwapVelocities(ball);

		}

		if(counterSinceLastPlatform > stepsBetweenPlatforms)
		{
			counterSinceLastPlatform = 0;
			if(!infiniteMode)
			{
				addNewPlatform();
			}
			else
			{				
				addPlatformToCurrentPlatforms(UtilClass.generateRandomWall());
			}			

			increasePlatformAcceleration();
			decreaseDistanceBetweenPlatforms();
		}
		
		stepCount++;
		counterSinceLastPlatform++;
	}



	private void removeDeadBallsCheckForLose() {

	    Iterator<Ball> itr = arrayOfBalls.iterator();

	    while(itr.hasNext())
	    {	    	
	    	Ball ball = itr.next();
	    	if(ball.getBallAnimationState() == BALL_ANIMATION_STATE.DEAD)
	    	{
	    		itr.remove();
	    		gameEventManager.ballLost();
	    	}
	    }
	    
	    if(arrayOfBalls.size() == 0)
	    {
	    	gameEventManager.loseGame();
	    }
		
	}

	private void increasePlatformAcceleration() {

		playformVelocity = playformVelocity + platformAcceleration;

	}

	private void putBallInBoundsAndSwapVelocities(Ball ball) {

		double newXPos = ball.getxCoord();
		double newYPos = ball.getyCoord();	

		//set the X position
		if(!noWalls){
			if(newXPos + ball.getRadius() > screenWidth ){
				newXPos = screenWidth - ball.getRadius();
				ball.setXVelocity(-ball.getXVelocity() / collisionEnergyLost);
			}	
			else if (newXPos - ball.getRadius() < 0){			
				newXPos = ball.getRadius();
				ball.setXVelocity(-ball.getXVelocity() / collisionEnergyLost);
			}
		}
		else{
			if(newXPos + ball.getRadius() > screenWidth + (2*ball.getRadius()) ){
				newXPos = ball.getRadius();
			}	
			else if (newXPos - ball.getRadius() < -(2*ball.getRadius())){			
				newXPos = screenWidth - ball.getRadius();
			}
		}		

		//set the Y positoin
		if(newYPos + ball.getRadius() > screenHeight ){
			newYPos = screenHeight - ball.getRadius();
		}		
		else if  (newYPos - ball.getRadius() < 0){
			//if the ball touches the top of the screen
			
			if(ball.getBallAnimationState() == BALL_ANIMATION_STATE.ALIVE)
			{			
				gameEventManager.ballExpload();
				ball.setBallAnimationState(BALL_ANIMATION_STATE.DYING);
			}

		}

		ball.setXYPosition(newXPos, newYPos);


	}

	public int getStepCount() {
		return stepCount;
	}

	private void clearBallCollisions()
	{
		for (Ball ball : arrayOfBalls)
		{
			ball.clearHasCollidedWith();
		}

	}

	private void checkForCollisionsWithOtherBalls(Ball ball) 
	{
		for (Ball otherBall : arrayOfBalls)
		{
			//don't compare ourself to ourself!!!!! or don't compare balls that have already touched
			if(ball.getBallId() != otherBall.getBallId()  && 
					!ball.getHasCollidedWith(otherBall.getBallId()))
			{
				double distance = UtilClass.distanceFormula(ball.getxCoord(), ball.getyCoord(),
						otherBall.getxCoord(), otherBall.getyCoord());

				if(distance <= ball.getRadius() + BALL_COLLISION_RANGE)
				{		
					//mark them as collided
					ball.setHasCollidedWith(otherBall.getBallId());
					otherBall.setHasCollidedWith(ball.getBallId());
					UtilClass.collisionResponse(ball, otherBall);

					int counter = 0;

					//Move balls out of range (This prevents the balls from "fusing")
					while(distance <= ball.getRadius() + BALL_COLLISION_RANGE && counter < MAX_SEPERATE_COUNT)
					{							

						//give the balls a tiny kick
						//ball.setYVelocity(ball.getYVelocity() + (ball.getYVelocity() * .001));
						//ball.setXVelocity(ball.getXVelocity() + (ball.getXVelocity() * .001));
						//otherBall.setXVelocity(otherBall.getXVelocity() + (otherBall.getXVelocity() * .001));
						//otherBall.setYVelocity(otherBall.getYVelocity() + (otherBall.getYVelocity() * .001));

						ball.stepBall();
						otherBall.stepBall();												

						//Re-calculate distance and save old distance
						distance = UtilClass.distanceFormula(ball.getxCoord(), ball.getyCoord(),
								otherBall.getxCoord(), otherBall.getyCoord());						

						counter++;
					}

					//We give up move the balls apart manually ignoring velocities, we tried....
					if(counter == MAX_SEPERATE_COUNT)
					{
						//Ball leftBall, rightBall;
						//						
						//if(ball.getxCoord() < otherBall.getxCoord())
						//{
						//leftBall = ball;
						//rightBall = otherBall;
						//}
						//else
						//{
						//rightBall = ball;
						//leftBall = otherBall;
						//}						
						//leftBall.setXYPosition(leftBall.getxCoord() - ball.getRadius()/2, leftBall.getyCoord());
						//rightBall.setXYPosition(rightBall.getxCoord() + rightBall.getRadius()/2, rightBall.getyCoord());						
					}

				}
			}
		}

	}

	private void addNewPlatform() 
	{

		if(platformCounter < level.getPlatforms().size())
		{
			String platform = level.getPlatforms().get(platformCounter);
			if (platform != null)
			{
				addPlatformToCurrentPlatforms(level.getPlatforms().get(platformCounter));
			}	
		}
		else
		{
			if(arrayOfPlatformsOnScreen.size() == 0)
			{
				//if the ball gets passed all the platforms
				gameEventManager.winGame();
			}
		}
	}


	public void setBallXVelocity(double velocity)
	{
		for(Ball ball : arrayOfBalls)
		{		
			//can't change the direction of the ball in the air or its dying
			if(ball.getBallState() != BALL_STATE.IN_AIR && ball.getBallAnimationState() != BALL_ANIMATION_STATE.DYING)
			{		
				double finalXVelocity = ball.getXVelocity() + (int) velocity * ball.getBallXVelocityRatio();
				ball.setXVelocity( finalXVelocity );
			}
			else if(ball.getBallAnimationState() == BALL_ANIMATION_STATE.DYING)
			{
				ball.setXVelocity(0);
			}
		}
	}


	private void setBallState(Ball ball)
	{
		//if we don't know the state of the ball check 
		//to see if its in the air or on the ground
		if(ball.getBallState() == BALL_STATE.UNKNOWN)
		{
			if(ball.getyCoord() + ball.getRadius() >= screenHeight - GROUND_COLLISION_RANGE)
			{
				ball.setBallState(BALL_STATE.ON_GROUND);
				ball.setYVelocity(checkForGroundNoBounceThreshold(-ball.getYVelocity()  / collisionEnergyLost));
				ball.setYPosition(screenHeight - ball.getRadius());			
			}
			else
			{		
				ball.setBallState(BALL_STATE.IN_AIR);
				ball.setYVelocity(ball.getYVelocity() + gravity );
			}			
		}
		//otherwise it must be on a platform reverse the velocity for bounce
		else 
		{		
			ball.setYVelocity(-ball.getYVelocity() / collisionEnergyLost ) ;
		}

	}



	private void checkCollisionWithPlatforms(Ball ball) {

		ArrayList<Integer> xCoords;
		int startOfWall;
		int endOfWall;
		double ballXCoord = ball.getxCoord();
		double ballYCoord = ball.getyCoord();
		double ballRadius = ball.getRadius();	
		double platformYCoord;

		for(Platform platform : arrayOfPlatformsOnScreen)
		{
			platformYCoord = platform.getyCoord();

			//Check for possible collision with platform
			if( (platformYCoord + BALL_COLLISION_BOTTOM_PLATFORM_RANGE >= ballYCoord + ballRadius ) &&
					( platformYCoord <= ballYCoord + ballRadius ) &&
					!platform.isBallPassedPlatform(ball.getBallId()) )
			{				
				xCoords = platform.getLineXValues();

				//Iterate through platform segments, see if we are in a empty spot				
				for(int i = 0; i < xCoords.size(); i = i + 2)
				{
					startOfWall = xCoords.get(i);
					endOfWall = xCoords.get(i+1);
					ballXCoord = ball.getxCoord();

					//extend the walls off screen if we have no walls
					if(noWalls && startOfWall == 0)
					{
						startOfWall = startOfWall + (int) -(ball.getRadius()*2);
					}
					else if(noWalls && endOfWall == screenWidth)
					{
						endOfWall = endOfWall + (int) (ball.getRadius()*2);
					}

					if(ballXCoord > startOfWall && ballXCoord < endOfWall)
					{
						//Set the balls state to be on a platform and put the ball on the platform
						ball.setYPosition(platform.getyCoord() - ballRadius);
						ball.setBallState(BALL_STATE.ON_PLATFORM);	
						return;
					}					
				}			
			}
			//make a wall as passed
			else if (!platform.isBallPassedPlatform(ball.getBallId()) && ballYCoord - ballRadius >=  platformYCoord)
			{
				platform.setBallPassedPlatform(ball.getBallId());
				gameEventManager.addPoint();
			}
		}

		//If we arnt on a platform we are either in the air or on the ground
		ball.setBallState(BALL_STATE.UNKNOWN);		
	}

	private void stepPlatforms()
	{
		for(Platform platform : arrayOfPlatformsOnScreen)
		{					
			platform.stepPlatform();

			if(platform.checkPlatformNotOnScreen())
			{
				//addPoint();
				arrayOfPlatformsOnScreen.remove(platform);
			}
		}
	}

	public ConcurrentLinkedQueue<Platform> getArrayOfCurrentWalls() 
	{
		return arrayOfPlatformsOnScreen;
	}

	public void addPlatformToCurrentPlatforms(String platform)
	{
		Platform newPlatform = new Platform(platform,platformCounter++,playformVelocity);
		newPlatform.generateLines(screenWidth);
		newPlatform.setyCoord(screenHeight);

		arrayOfPlatformsOnScreen.add(newPlatform);
	}

	public double getWallHeight() {
		return platformHeight;
	}


	public LevelConfiguration getLevelConfig()
	{
		return level;
	}

	public int getMaxWidth()
	{
		return screenWidth;
	}

	public int getMaxHeight()
	{
		return screenHeight;
	}


	public boolean hasBeenInitialized()
	{
		return hasBeenInitialized;
	}

	public ConcurrentLinkedQueue<Ball> getArrayOfBalls()
	{
		return arrayOfBalls;
	}

	public int getStepRateMili()
	{
		return stepRateMili;
	}

	private double checkForGroundNoBounceThreshold(double newYVelocity)
	{		
		if(Math.abs(newYVelocity) <= NO_BOUNCE_GROUND_THRESHOLD)	{
			return 0;
		}else{
			return newYVelocity;
		}		
	}


	private void decreaseDistanceBetweenPlatforms() 
	{		
		stepsBetweenPlatforms = stepsBetweenPlatforms - platformStepsBetweenAccel;

		if(stepsBetweenPlatforms <= platformStepsBetweenMinimum)
		{
			stepsBetweenPlatforms = platformStepsBetweenMinimum;
		}		
	}

}
