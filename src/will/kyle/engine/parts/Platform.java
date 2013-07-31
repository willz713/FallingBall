package will.kyle.engine.parts;

import java.util.ArrayList;

public class Platform {

	private ArrayList<PlatformSegment> platformSegments;
	private final static char PLATFORM = '=';	
	private int platformId;
	private ArrayList<Integer> xValues;
	private ArrayList<Integer> ballIdsPassedPlatform;
	private double yCoord;
	private double yVelocity;
	
	private final static double REMOVE_PLATFORM_BUFFER = -100;
	
	

	public Platform(String platforms, int platformId, double yVelocity)
	{
		this.platformId = platformId;

		platformSegments = new ArrayList<PlatformSegment>();
		ballIdsPassedPlatform = new ArrayList<Integer>();

		this.yVelocity = yVelocity;
		
		PlatformSegment platformSeg;		
		for(int i = 0; i < platforms.length(); i ++)
		{
			if(platforms.charAt(i) == PLATFORM)
			{
				platformSeg = new PlatformSegment(false);
			}
			else
			{
				platformSeg = new PlatformSegment(true);
			}

			platformSegments.add(platformSeg);
		}
	}

	public void addWall(PlatformSegment platformSegment)
	{
		platformSegments.add(platformSegment);
	}

	public ArrayList<PlatformSegment> getPlatformSegments()
	{
		return platformSegments;
	}


	public void generateLines(int screenWidth)
	{
		int ArrayLength = platformSegments.size();
		PlatformSegment wallSeg;
		boolean startedWall = false;
		boolean noWallYet = true;

		int wallSegmentSize = screenWidth / ArrayLength;
		xValues = new ArrayList<Integer>();

		for(int i = 0 ; i < ArrayLength; i++)
		{
			wallSeg = platformSegments.get(i);

			if(!wallSeg.isEmpty())
			{
				if(!startedWall)
				{
					xValues.add(i*wallSegmentSize);
				}				
				startedWall = true;
				noWallYet = false;
			}
			else
			{
				if(i != 0 && !noWallYet)
				{
					xValues.add(i*wallSegmentSize);
					startedWall = false;
				}
			}			
		}		
		if (xValues.size() % 2 != 0)
		{
			xValues.add(screenWidth);
		}	
	}

	public boolean checkPlatformNotOnScreen()
	{
		if(yCoord < REMOVE_PLATFORM_BUFFER)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public int getPlatformId() 
	{
		return platformId;
	}

	public ArrayList<Integer> getLineXValues() 
	{
		return xValues;
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof Platform)
		{
			Platform comparedWall = (Platform) obj;
			if(comparedWall.getPlatformId() == platformId)
			{
				return true;
			}
		}

		return false;
	}

	public double getyCoord() 
	{
		return yCoord;
	}

	public void setyCoord(double yCoord)
	{
		this.yCoord = yCoord;
	}

	public boolean isBallPassedPlatform(int id) 
	{
		
		if(ballIdsPassedPlatform.contains(id))
		{
			return true;
		}
		else return false;
	}

	public void setBallPassedPlatform(int ballId)
	{
		ballIdsPassedPlatform.add(ballId);
	}
	
	public void stepPlatform()
	{		
		setyCoord(yCoord - yVelocity);
	}
}
