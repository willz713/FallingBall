package will.kyle.engine.parts;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import will.kyle.UtilClass.Vector2d;
import android.graphics.Bitmap;


@Root
public class Ball
{

	private final static long BALL_ANIMATION_TIME = 30;
	
	
	private double xCoord;
	private double yCoord;
	private double xVelocity = 0;
	private double yVelocity = 0;
	private ArrayList<Integer> hasCollidedWith = new ArrayList<Integer>();
	private ArrayList<Bitmap> deathAnimationBitmaps;
	private ArrayList<Bitmap> spawnAnimationBitmaps;
	private Bitmap aliveBallBitmap;
	private Bitmap currentBallBitmap;


	private long timeOfLastRequestedImage = 0;
	private int positionInAnimation = 0;

	@Element
	private double mass;
	@Element
	private double radius;	
	@Element
	private int ballId;	
	@Element
	private String ballImage;
	@Element
	private double ballXVelocityRatio;
	@Element
	private double maxXVelocityRatio;
	@Element
	private double maxYVelocityRatio;
	@ElementList
	private ArrayList<String> deathAnimation;
	@ElementList
	private ArrayList<String> spawnAnimation;


	public static enum BALL_STATE
	{
		ON_PLATFORM,IN_AIR,ON_GROUND,UNKNOWN;
	}
	
	public static enum BALL_ANIMATION_STATE
	{
		ALIVE,DYING,DEAD,SPAWNING;
	}

	private BALL_STATE ballState;
	private BALL_ANIMATION_STATE ballAnimationState;


	public boolean getHasCollidedWith(int ballId) {
		if(hasCollidedWith.contains(ballId))
		{
			return true;
		}
		else return false;
	}
	
	public void resetBall()
	{
		positionInAnimation = 0;
		timeOfLastRequestedImage = 0;
		setBallState(BALL_STATE.IN_AIR);
		setBallAnimationState(BALL_ANIMATION_STATE.SPAWNING);	
		setVelocityVector(new Vector2d(0,0));
	}

	public Vector2d getVelocityVector()
	{		
		//avoid division by zero
		double xVel = .000001f;
		double yVel = .000001f;
		
		if(xVelocity != 0)
		{
			xVel = xVelocity;
		}
		
		if(yVelocity != 0)
		{
			yVel = yVelocity;
		}
		
		return new Vector2d(xVel,yVel);		
	}



	public void clearHasCollidedWith()
	{
		hasCollidedWith.clear();
	}

	
	public void stepBall()
	{
		//set animation bitmap here		
		if(getBallAnimationState() == BALL_ANIMATION_STATE.DYING )
		{					
			dyingAnimation();
		}
		else if(getBallAnimationState() == BALL_ANIMATION_STATE.SPAWNING)
		{
			spawningAnimation();
		}
		else
		{
			this.currentBallBitmap=aliveBallBitmap;
		}		
		
		double newXPos = getxCoord() + getXVelocity();
		double newYPos = getyCoord() + getYVelocity();	
		setXYPosition(newXPos, newYPos);
	}
	
	private void dyingAnimation()
	{
		if( (System.currentTimeMillis() - timeOfLastRequestedImage ) > BALL_ANIMATION_TIME )
		{						
			
			if(positionInAnimation < deathAnimationBitmaps.size() - 1)
			{
				positionInAnimation++;					
			}
			else
			{
				timeOfLastRequestedImage = 0;
				positionInAnimation = 0;
				setBallAnimationState(BALL_ANIMATION_STATE.DEAD);
			}				
		}				
		timeOfLastRequestedImage = System.currentTimeMillis();
		this.currentBallBitmap = deathAnimationBitmaps.get(positionInAnimation);
	}
	
	private void spawningAnimation()
	{
		if( (System.currentTimeMillis() - timeOfLastRequestedImage ) > BALL_ANIMATION_TIME )
		{				
			if(positionInAnimation < spawnAnimationBitmaps.size() - 1)
			{
				positionInAnimation++;					
			}
			else
			{
				timeOfLastRequestedImage = 0;
				positionInAnimation = 0;
				setBallAnimationState(BALL_ANIMATION_STATE.ALIVE);
			}				
		}				
		timeOfLastRequestedImage = System.currentTimeMillis();
		this.currentBallBitmap = spawnAnimationBitmaps.get(positionInAnimation);
	}
	

	
	private double checkYVelocityOfBall(double newYVelocity)
	{				
		if(Math.abs(newYVelocity) >= maxYVelocityRatio)
		{
			if(newYVelocity > 0)
			{
				return maxYVelocityRatio;
			}
			else
			{
				return -maxYVelocityRatio;
			}
		}
		else
		{
			return newYVelocity;
		}
	}

	private double checkXVelocityOfBall(double newXVelocity)
	{				
		if(Math.abs(newXVelocity) >= maxXVelocityRatio)
		{
			if(newXVelocity > 0)
			{
				return maxXVelocityRatio;
			}
			else
			{
				return -maxXVelocityRatio;
			}
		}
		else
		{
			return newXVelocity;
		}
	}
	
	
	public Bitmap getBitmapForBall()	
	{
		return currentBallBitmap;
	}
	
	//---------------------------Getters and Setters----------------------------
	
	public void initializeBallDeathAnimationBitmap(ArrayList<Bitmap> deathAnimationBitmaps)
	{
		this.deathAnimationBitmaps = deathAnimationBitmaps;
	}
	public void initializeBallSpawnAnimationBitmap(ArrayList<Bitmap> spawnAnimationBitmaps)
	{
		this.spawnAnimationBitmaps = spawnAnimationBitmaps;
	}

	public ArrayList<String> getDeathAnimation() {
		return deathAnimation;
	}

	public double getMaxYVelocityRatio() {
		return maxYVelocityRatio;
	}
	
	public void setMaxYVelocityRatio(double maxYVelocityRatio) {
		this.maxYVelocityRatio = maxYVelocityRatio;
	}
	
	public void setScaledBitmap(Bitmap bitmap)
	{
		this.aliveBallBitmap = bitmap;
	}

	public String getBallImage() {
		return ballImage;
	}
	
	public void setBallImage(String image)
	{
		ballImage = image;
	}	

	public void setVelocityVector(Vector2d velVector) 
	{
		xVelocity = velVector.x;
		yVelocity = velVector.y;
	}

	public double getMass()
	{
		return mass;
	}
	
	public Vector2d getPositionVector()
	{		
		return new Vector2d(xCoord,yCoord);		
	}

	public void setHasCollidedWith(int ballId) {
		hasCollidedWith.add(ballId);
	}
	
	public double getxCoord() {
		return xCoord;
	}

	public void setXVelocity(double xVelocity)
	{
		this.xVelocity = checkXVelocityOfBall(xVelocity);
	
	}

	public void setYVelocity(double yVelocity)
	{		
		this.yVelocity = checkYVelocityOfBall(yVelocity);
	}

	public double getXVelocity()
	{
		return xVelocity;
	}

	public double getYVelocity()
	{
		return yVelocity;
	}

	public void setXYPosition(double x, double y)
	{
		xCoord = x;
		yCoord = y;
	}	

	public void setYPosition(double y)
	{
		yCoord = y;
	}

	public double getyCoord() {
		return yCoord;
	}	

	public double getRadius()
	{
		return radius;
	}


	public BALL_STATE getBallState() {
		return ballState;
	}

	public void setBallState(BALL_STATE ballState) {
		this.ballState = ballState;
	}	
	public BALL_ANIMATION_STATE getBallAnimationState() {
		return ballAnimationState;
	}
	
	public void setBallAnimationState(BALL_ANIMATION_STATE ballState) {
		this.ballAnimationState = ballState;
	}	

	public int getBallId()
	{
		return ballId;
	}
	
	public double getBallXVelocityRatio() {
		return ballXVelocityRatio;
	}
	
	public double getMaxXVelocityRatio() {
		return maxXVelocityRatio;
	}
	
	public void setBallXVelocityRatio(double ballVelocity)
	{
		this.ballXVelocityRatio = ballVelocity;
	}
	
	public void setMaxVelocityRatio(double maxVelocityRatio)
	{
		this.maxXVelocityRatio = maxVelocityRatio;
	}

	public ArrayList<String> getSpawnAnimation() {
		return spawnAnimation;
	}
	
	public void setRadius(double radius)
	{
		this.radius = radius;
	}
	
}
