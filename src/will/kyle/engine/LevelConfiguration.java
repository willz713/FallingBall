
package will.kyle.engine;


import java.util.ArrayList;

import org.simpleframework.xml.Default;

import will.kyle.engine.parts.Ball;


@Default
public class LevelConfiguration {

	private String levelTitle;	
	private String levelSubTitle;

	private double platformAccelDistanceBetween;
	private double platformMinDistanceBetween;
	private double platformAcceleration;
	private double distanceBetweenPlatforms;
	private double platformVelocity;
	private double platformHeight;
	private String platformColor;
	private String platformSecondaryColor;
		
	private double collisionEnergyLost;
	private double gravity;
	private String ballColor;

	private int stepRateMili;
	private int drawScreenRate;
	
	private boolean noWalls;
	private boolean infiniteMode;

	private String backgroundImage;
	private ArrayList<String> platforms;
	private ArrayList<Ball> ballArray;		
	
	
	public String getLevelTitle() {
		return levelTitle;
	}

	public String getLevelSubTitle() {
		return levelSubTitle;
	}

	public double getPlatformAccelDistanceBetween() {
		return platformAccelDistanceBetween;
	}

	public double getPlatformMinDistanceBetween() {
		return platformMinDistanceBetween;
	}

	public double getPlatformAcceleration() {
		return platformAcceleration;
	}

	public double getDistanceBetweenPlatforms() {
		return distanceBetweenPlatforms;
	}

	public double getPlatformVelocity() {
		return platformVelocity;
	}

	public double getPlatformHeight() {
		return platformHeight;
	}

	public String getPlatformColor() {
		return platformColor;
	}

	public double getCollisionEnergyLost() {
		return collisionEnergyLost;
	}

	public double getGravity() {
		return gravity;
	}

	public String getBallColor() {
		return ballColor;
	}


	public int getStepRateMili() {
		return stepRateMili;
	}

	public int getDrawScreenRate() {
		return drawScreenRate;
	}

	public boolean isNoWalls() {
		return noWalls;
	}

	public boolean isInfiniteMode() {
		return infiniteMode;
	}

	public ArrayList<String> getPlatforms() {
		return platforms;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	public ArrayList<Ball> getBallArray() {
		return ballArray;
	}

	public String getPlatformSecondaryColor() {
		return platformSecondaryColor;
	}

}
