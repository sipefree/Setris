package ie.theorie.setris;

public class STetromino extends Tetromino {
	STetromino() {
		lineCol = 0xFF02E329;
		x = Setris.startX;
		y = Setris.startY;
		bounds = new Block[3][3];
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				bounds[i][j] = new Block();
			}
		}
		bounds[1][0].setColor(Setris.greenBlock);
		bounds[2][0].setColor(Setris.greenBlock);
		bounds[0][1].setColor(Setris.greenBlock);
		bounds[1][1].setColor(Setris.greenBlock);
	}
	int offsetX(int i)
	{
		return x + i;
	}
	int offsetY(int j)
	{
		return y + j;
	}
}
