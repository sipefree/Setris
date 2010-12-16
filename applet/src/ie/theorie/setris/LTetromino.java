package ie.theorie.setris;

public class LTetromino extends Tetromino {
	LTetromino() {
		lineCol = 0xFFF56200;
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
		bounds[2][0].setColor(Setris.orangeBlock);
		bounds[0][1].setColor(Setris.orangeBlock);
		bounds[1][1].setColor(Setris.orangeBlock);
		bounds[2][1].setColor(Setris.orangeBlock);
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
