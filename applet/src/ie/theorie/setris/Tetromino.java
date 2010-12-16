package ie.theorie.setris;
public class Tetromino {
	int x;
	int y;
	int facing;
	int lineCol;
	Block[][] bounds;
	void doRot() {
		int n=bounds.length;
		Block tmp;
		for (int i=0; i<n/2; i++) {
			for (int j=i; j<n-i-1; j++) {
				tmp = bounds[i][j];
				bounds[i][j] = bounds[j][n-i-1];
				bounds[j][n-i-1] = bounds[n-i-1][n-j-1];
				bounds[n-i-1][n-j-1] = bounds[n-j-1][i];
				bounds[n-j-1][i] = tmp;
			}
		}
	}
	void rot() {
		doRot();
		if(collision())
		{
			// hackish undo.
			doRot();
			doRot();
			doRot();
		}
	}
	int offsetX(int i)
	{
		return x;
	}
	int offsetY(int j)
	{
		return y;
	}
	void drawTet() {
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				bounds[i][j].drawBlock(offsetX(i), offsetY(j));
			}
		}
	}
	void saveToBoard() {
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				if(bounds[i][j].isEmpty == false)
					Setris.board[offsetX(i)][offsetY(j)].setColor(bounds[i][j].blockColor);
			}
		}	
	}
	boolean step() {
		y++;
		if(collision())
		{
			y--;
			return false;
		}
		return true;
	}
	boolean collision()
	{
		boolean pass = true;
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				if(bounds[i][j].isEmpty == false)
				{
					pass = pass && offsetX(i) >= 0 && offsetX(i) < Setris.cols;
					pass = pass && offsetY(j) < Setris.rows;
					if(offsetY(j) <= 20 && offsetY(j) > 0)
						pass = pass && Setris.board[offsetX(i)][offsetY(j)].isEmpty == true;
					if(!pass) break;
				}
			}
			if(!pass) break;
		}
		return !pass;
	}
	void pushLeft()
	{
		x--;
		if(collision()) x++;
	}
	void pushRight()
	{
		x++;
		if(collision()) x--;
	}
	void drawOutline()
	{
		int backY = y;
		while(step());
		Setris.instance.stroke(lineCol, 100);
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				if(bounds[i][j].isEmpty == false)
					bounds[i][j].drawOutline(offsetX(i), offsetY(j));
			}
		}
		y = backY;
	}
	
}