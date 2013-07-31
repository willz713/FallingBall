package will.kyle.engine.parts;

public class PlatformSegment 
{	

	private boolean isEmpty ;
	
	public PlatformSegment(boolean isEmpty)
	{
		this.setEmpty(isEmpty);
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	private void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

}
