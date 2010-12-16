package ie.theorie.setris;

public class TTetromino extends Tetromino {
	TTetromino() {
		lineCol = 0xFFA012FA;
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
		bounds[1][0].setColor(Setris.purpleBlock);
		bounds[0][1].setColor(Setris.purpleBlock);
		bounds[1][1].setColor(Setris.purpleBlock);
		bounds[2][1].setColor(Setris.purpleBlock);
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
