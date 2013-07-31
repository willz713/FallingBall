package will.kyle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.simpleframework.xml.core.Persister;

import will.kyle.engine.parts.Ball;

import android.graphics.Color;

public class UtilClass 
{	

	private static String[] randomWalls =
		{
		"-====",
		"-====",
		"-====",
		"-====-",
		"==-==",
		"==-==",
		"=-===",
		"====-",
		"====-",
		"====-",
		"===-=",
		"=-=-=",
		"-=-=-"	
		};

	public static Object unmarshellXML(Class<?> aClass,String xmlString) throws NullPointerException, Exception
	{	
		Persister persister = new Persister();
		Object object;

		object = persister.read(aClass, xmlString);
		return object;
	}	

	public static int covertStringToColor(String color)
	{
		if(color.equalsIgnoreCase("BLACK"))
		{
			return Color.BLACK;
		}
		else if(color.equalsIgnoreCase("BLUE"))
		{
			return Color.BLUE;
		}
		else if(color.equalsIgnoreCase("GRAY"))
		{
			return Color.GRAY;
		}
		else if(color.equalsIgnoreCase("GREEN"))
		{
			return Color.GREEN;
		}
		else if(color.equalsIgnoreCase("PINK"))
		{
			return Color.MAGENTA;
		}
		else if(color.equalsIgnoreCase("RED"))
		{
			return Color.RED;
		}
		else if(color.equalsIgnoreCase("YELLOW"))
		{
			return Color.YELLOW;
		}
		else if(color.equalsIgnoreCase("WHITE"))
		{
			return Color.WHITE;
		}
		else if(color.equalsIgnoreCase("DARKGRAY"))
		{
			return Color.DKGRAY;
		}
		else if(color.equalsIgnoreCase("LIGHTGRAY"))
		{
			return Color.LTGRAY;
		}
		else if(color.equalsIgnoreCase("CYAN"))
		{
			return Color.CYAN;
		}
		else
		{
			return Color.BLACK;
		}

	}

	public static String loadFileContents(InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int readInt;

		try{
			readInt = inputStream.read();
			while (readInt != -1)
			{
				byteArrayOutputStream.write(readInt);
				readInt = inputStream.read();
			}
			inputStream.close();	
		}

		finally
		{
			inputStream.close();
		}

		return byteArrayOutputStream.toString();
	}

	public static double distanceFormula(double x1, double y1, double x2, double y2)
	{
		double sqrtValue = ((x1-x2) * (x1-x2)) + ((y1-y2) * (y1-y2));		
		return Math.sqrt(sqrtValue);
	}	

	/* returns the magnitude of a vector */
	public static double magnitude(Vector2d myVector){	
		return Math.sqrt((myVector.x * myVector.x) + (myVector.y * myVector.y));
	}

	/* given a vector it normalizes it */
	public static void normalize(Vector2d normVector){	
		double mag;
		mag = magnitude(normVector);	
		normVector.x = (double) (normVector.x / mag);
		normVector.y = (double) (normVector.y / mag);
	}

	/* returns the dot product value of a vector */
	public static double dotProduct(Vector2d myVect1, Vector2d myVect2){	
		return ((myVect1.x * myVect2.x) + (myVect1.y * myVect2.y));
	}

	/* subtracts 2 vectors */
	public static Vector2d subtractVector(Vector2d myVect1, Vector2d myVect2){	

		Vector2d newVector = new Vector2d();
		newVector.x = myVect1.x - myVect2.x;
		newVector.y = myVect1.y - myVect2.y;	
		return newVector;
	}

	/* adds 2 vectors */
	public static Vector2d addVector(Vector2d myVect1, Vector2d myVect2){	

		Vector2d newVector = new Vector2d();
		newVector.x = myVect1.x + myVect2.x;
		newVector.y = myVect1.y + myVect2.y;
		return newVector;
	}

	//This is horrible physics but i'm too lazy to do it right =D
	public static void collisionResponse(Ball ball1, Ball ball2)
	{

		double m1 = ball1.getMass();
		double m2 = ball2.getMass();

		//new velocities
		Vector2d U1x = new Vector2d(), U2x = new Vector2d(), U1y, U2y;	
		Vector2d V1x = new Vector2d(), V2x = new Vector2d();

		//original velocities
		Vector2d U1 = ball1.getVelocityVector();
		Vector2d U2 = ball2.getVelocityVector();

		//x_axis of collision
		Vector2d X_Axis;
		Vector2d X_Axis_Neg = new Vector2d();

		//find center and normalize
		X_Axis = subtractVector(ball2.getPositionVector(), ball1.getPositionVector());	
		normalize(X_Axis);

		//create a negative x axis
		X_Axis_Neg.x = -1*(X_Axis.x);
		X_Axis_Neg.y = -1*(X_Axis.y);

		//find U1x, U1y
		U1x.x = (double) (X_Axis.x * dotProduct(X_Axis, U1));
		U1x.y = (double) (X_Axis.y * dotProduct(X_Axis, U1));
		U1y = subtractVector(U1,U1x);

		//find U2x, U2y
		U2x.x = (double) ((X_Axis_Neg.x) * dotProduct(X_Axis_Neg, U2));
		U2x.y = (double) ((X_Axis_Neg.y) * dotProduct(X_Axis_Neg, U2));
		U2y = subtractVector(U2,U2x);

		//V1x = [(m1-m2)/(m1+m2)] U1x + [(2m2)/(m1+m2)] U2x    
		//V2x = [(2m1)/(m1+m2)] U1x + [(m2-m1)/(m1+m2)] U2x	
		V1x.x = (double) (((m1-m2)/(m1+m2)) * U1x.x + ((2*m2)/(m1+m2)) * U2x.x);
		V1x.y = (double) (((m1-m2)/(m1+m2)) * U1x.y + ((2*m2)/(m1+m2)) * U2x.y);

		V2x.x = (double) (((2*m1)/(m1+m2)) * U1x.x + ((m2-m1)/(m1+m2)) * U2x.x);
		V2x.y = (double) (((2*m1)/(m1+m2)) * U1x.y + ((m2-m1)/(m1+m2)) * U2x.y);

		//V1f = V1x+U1y
		//V2f = V2x+U2y
		ball1.setVelocityVector(addVector(V1x,U1y));
		ball2.setVelocityVector(addVector(V2x,U2y));


	}

	public static String generateRandomWall()
	{		
		Random generator = new Random();
		int randomIndex = generator.nextInt( randomWalls.length );		
		return randomWalls[randomIndex];
	}


	public static class Vector2d
	{
		public double x;
		public double y;

		public Vector2d(double x, double y)
		{
			this.x = x;
			this.y = y;
		}

		public Vector2d()
		{
			this.x = 0f;
			this.y = 0f;
		}
	}
	
	

}
