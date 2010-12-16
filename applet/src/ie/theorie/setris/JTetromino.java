package ie.theorie.setris;

public class JTetromino extends Tetromino {
	JTetromino() {
		lineCol = 0xFF6961EA;
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
		bounds[0][0].setColor(Setris.blueBlock);
		bounds[0][1].setColor(Setris.blueBlock);
		bounds[1][1].setColor(Setris.blueBlock);
		bounds[2][1].setColor(Setris.blueBlock);
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
