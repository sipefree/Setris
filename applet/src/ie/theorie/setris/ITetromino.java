package ie.theorie.setris;

public class ITetromino extends Tetromino {
	ITetromino() {
		lineCol = 0xFF12B2FA;
		x = Setris.startX;
		y = Setris.startY;
		bounds = new Block[4][4];
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				bounds[i][j] = new Block();
			}
		}
		bounds[0][1].setColor(Setris.cyanBlock);
		bounds[1][1].setColor(Setris.cyanBlock);
		bounds[2][1].setColor(Setris.cyanBlock);
		bounds[3][1].setColor(Setris.cyanBlock);
	}
	int offsetX(int i)
	{
		return x + 1 - i;
	}
	int offsetY(int j)
	{
		return y + 1 - j;
	}
}
