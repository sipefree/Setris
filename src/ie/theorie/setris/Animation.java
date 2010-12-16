package ie.theorie.setris;

public class Animation
{
	int y;
	int x;
	int targetX;
	int targetY;
	int opacity;
	int targetOpacity;
	
	int dx;
	int dy;
	int dop;
	
	int t;
	int lt;
	Animation(int x, int y, int targetX, int targetY, int opacity, int targetOpacity, int t)
	{
		this.x = x;
		this.y = y;
		this.targetX = targetX;
		this.targetY = targetY;
		this.opacity = opacity;
		this.targetOpacity = targetOpacity;
		this.t = t;

		this.lt = Setris.instance.millis();
		
		this.dx = targetX - x;
		this.dy = targetY - y;
		this.dop = targetOpacity - opacity;
	}
	boolean step()
	{
		
		int ct = Setris.instance.millis();
		int dt = ct - lt;
		float frac = (float)dt / (float)(t);
		if(x != targetX)
		{
			if(x < targetX)
				x = (int) Setris.min(targetX, (int)(x + (dx*frac)));
			else
				x = (int) Setris.max(targetX, (int)(x + (dx*frac)));
		}
		
		if(y != targetY)
		{
			if(y < targetY)
				y = (int) Setris.min(targetY, (int)(y + (dy*frac)));
			else
				y = (int) Setris.max(targetY, (int)(y + (dy*frac)));
		}
		
		if(opacity != targetOpacity)
		{
			if(opacity < targetOpacity)
				opacity = (int) Setris.min(targetOpacity, (int)(opacity + (dop*frac)));
			else
				opacity = (int) Setris.max(targetOpacity, (int)(opacity + (dop*frac)));
			
		}
		
		if(x == targetX && y == targetY && opacity == targetOpacity)
			return true;
		else
			return false;
	}
}




