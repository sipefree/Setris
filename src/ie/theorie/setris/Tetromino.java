package ie.theorie.setris;
import java.util.Collections;
import java.util.LinkedList;
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
	void drawArbitrary(int x, int y, float factor, int alpha)
	{
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				bounds[i][j].drawArbitrary((int)(x + i*Setris.blockSize*factor), (int)(y + j*Setris.blockSize*factor), factor, alpha);
			}
		}
	}
	void saveToBoard() {
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				if(bounds[i][j].isEmpty == false)
					Setris.instance.board[offsetX(i)][offsetY(j)].setColor(bounds[i][j].blockColor);
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
						pass = pass && Setris.instance.board[offsetX(i)][offsetY(j)].isEmpty == true;
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
	
	static LinkedList<Tetromino> bag = new LinkedList<Tetromino>();
	static LinkedList<Tetromino> bag2 = new LinkedList<Tetromino>();
	
	public static void fillBag()
	{
		bag2.clear();
		bag2.add(new ITetromino());
		bag2.add(new OTetromino());
		bag2.add(new TTetromino());
		bag2.add(new STetromino());
		bag2.add(new ZTetromino());
		bag2.add(new JTetromino());
		bag2.add(new LTetromino());
		Collections.shuffle(bag2);
	}
	
	public static Tetromino getTet()
	{
		LinkedList<Tetromino> tmp;
		if(bag.size() == 0)
		{
			if(bag2.size() == 0)
			{
				fillBag();
			}
			tmp = bag;
			bag = bag2;
			bag2 = tmp;
			fillBag();
		}
		return bag.removeFirst();
	}
	public static Tetromino peek1()
	{
		Tetromino retVal = null;
		if(bag.size() == 0)
			retVal = bag2.getFirst();
		else
			retVal = bag.getFirst();
		
		return retVal;
	}
	public static Tetromino peek2()
	{
		Tetromino retVal = null;
		if(bag.size() == 1)
			retVal = bag2.getFirst();
		else if(bag.size() == 0)
			retVal = bag2.get(1);
		else
			retVal = bag.get(1);
		
		return retVal;
	}
	public static void prime()
	{
		bag.clear();
		bag2.clear();
		fillBag();
	}
}