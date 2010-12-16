package ie.theorie.setris;

public class OTetromino extends Tetromino {
	OTetromino() {
		lineCol = 0xFFF5FA12;
		x = Setris.startX;
		y = Setris.startY;
		bounds = new Block[2][2];
		for(int i = 0; i < bounds.length; i++)
		{
			for(int j = 0; j < bounds[i].length; j++)
			{
				bounds[i][j] = new Block();
				bounds[i][j].setColor(Setris.yellowBlock);
			}
		}
	}
	void rot() {}
	int offsetX(int i)
	{
		return x + i;
	}
	int offsetY(int j)
	{
		return y + j;
	}
}
