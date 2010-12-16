package ie.theorie.setris;

public class ZTetromino extends Tetromino {
	ZTetromino() {
		lineCol = 0xFFE30202;
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
		bounds[0][0].setColor(Setris.redBlock);
		bounds[1][0].setColor(Setris.redBlock);
		bounds[1][1].setColor(Setris.redBlock);
		bounds[2][1].setColor(Setris.redBlock);
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
