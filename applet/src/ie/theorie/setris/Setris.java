package ie.theorie.setris;

import processing.core.*;
import ddf.minim.*;
import java.util.LinkedList;
import java.util.ListIterator;


public class Setris extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;





	 
	Minim minim;
	AudioSnippet clickSnd;
	AudioSnippet foomSnd;
	AudioSnippet gameOverSnd;
	AudioSnippet placeSnd;
	AudioSnippet startSnd;
	AudioSnippet clearSnd;
	AudioSnippet tetrisSnd;
	AudioSnippet rotateSnd;

	static int blockSize = 40;
	static int rows = 20;
	static int cols = 10;
	static int paddingLeft = 200;
	static int paddingRight = 300;
	static int paddingTop = 0;
	static int paddingBottom = 20;

	static int boardOffsetX = 200;
	static int boardOffsetY = 0;

	int Y_AXIS = 1;
	int X_AXIS = 2;

	int xspacing = 1;   // How far apart should each horizontal location be spaced

	static float theta = 0.0f;  // Start angle at 0
	static float amplitude = 75.0f;  // Height of wave
	static float period = 1200.0f;  // How many pixels before the wave repeats
	static float dx;  // Value for incrementing X, a function of period and xspacing
	static float[] yvalues;  // Using an array to store height values for the wave
	static PImage bg;
	static PImage bg2;
	static PImage cyanBlock;
	static PImage yellowBlock;
	static PImage purpleBlock;
	static PImage greenBlock;
	static PImage redBlock;
	static PImage blueBlock;
	static PImage orangeBlock;
	static PImage foomImage;

	static int startX = 4;
	static int startY = 0;

	static boolean playing = false;
	static boolean paused = false;
	static boolean gameOver = false;
	
	static Block[][] board;

	static Setris instance;
	
	static PFont font;

	static int lastTime;

	static Tetromino current;
	
	LinkedList<Block> blockAnimations = new LinkedList<Block>();
	class FoomAnimation extends Animation
	{
		int height;
		FoomAnimation(int x, int y, int height, int opacity,
				int targetOpacity, int t) {
			super(x, y, x, y, opacity, targetOpacity, t);
			this.height = height;
		}
		
	}
	FoomAnimation[] foomAnimations = new FoomAnimation[cols];

	int level;
	int points;
	int totLines;
	public int pointsForLines(int lines)
	{
		int pts = 0;
		switch(lines)
		{
			case 1:
				pts = 40 * level;
				break;
			case 2:
				pts = 100 * level;
				break;
			case 3:
				pts = 300 * level;
				break;
			case 4:
				pts = 1200 * level;
				break;
		}
		return pts;
	}
	void newGame()
	{
		for(int x = 0; x < cols; x++ )
		{
			for(int y = 0; y < rows; y++)
			{
				board[x][y] = new Block();
			}
			foomAnimations[x] = null;
		}
		current = randomTet();
		gameOverSnd.pause();
		gameOverSnd.rewind();
		startSnd.rewind();
		startSnd.play();
		
		playing = true;
		paused = false;
		gameOver = false;
		level = 1;
		totLines = 0;
		points = 0;
	}

	void gameOver()
	{
		playing = false;
		gameOverSnd.rewind();
		gameOverSnd.play();
		gameOver = true;
	}

	public void setup()
	{
		instance = this;
		size(paddingLeft + cols*blockSize + paddingRight, paddingTop+rows*blockSize+paddingBottom);
		frameRate(30);
		bg = loadImage("background.png");
		bg2 = loadImage("lines.png");
		dx = (TWO_PI / period) * xspacing;
		yvalues = new float[width/xspacing];
		cyanBlock = loadImage("cyanBlock.png");
		yellowBlock = loadImage("yellowBlock.png");
		purpleBlock = loadImage("purpleBlock.png");
		greenBlock = loadImage("greenBlock.png");
		redBlock = loadImage("redBlock.png");
		blueBlock = loadImage("blueBlock.png");
		orangeBlock = loadImage("orangeBlock.png");
		foomImage = loadImage("foom.png");
		
		font = loadFont("CMUSansSerif-Bold-48.vlw");
		textFont(font);
		
		minim = new Minim(this);
		clickSnd = minim.loadSnippet("Click.wav");
	    foomSnd = minim.loadSnippet("Foom.wav");
	    gameOverSnd = minim.loadSnippet("GameOver.wav");
	    placeSnd = minim.loadSnippet("Place.wav");
	    startSnd = minim.loadSnippet("Start.wav");
	    clearSnd = minim.loadSnippet("Clear.wav");
		tetrisSnd = minim.loadSnippet("Tetris.wav");
		rotateSnd = minim.loadSnippet("Rotate.wav");

		board = new Block[cols][rows];
		newGame();
		
		lastTime = millis();
	}



	public void calcWave() {
		// Increment theta (try different values for 'angular velocity' here
		theta += 0.02;

		// For every x value, calculate a y value with sine function
		float x = theta;
		for (int i = 0; i < yvalues.length; i++) {
			yvalues[i] = sin(x)*amplitude;
			x+=dx;
		}
	}

	public void renderWave() {
		// A simple way to draw the wave with an ellipse at each location
		beginShape(POLYGON);
		fill(70, 124, 199, max(100*cos((float)frameCount/50.0f), 50));
		for (int x = 0; x < yvalues.length; x++) {
			noStroke();
			vertex(x, yvalues[x]+height/2);
		}
		vertex(width, height);
		vertex(0, height);
		endShape();
		
		beginShape(POLYGON);
		fill(70, 124, 199, max(100*sin((float)frameCount/50.0f), 50));
		for (int x = 0; x < yvalues.length; x++) {
			noStroke();
			vertex(x, amplitude - yvalues[x]*1.5f + height/2);
		}
		vertex(width, height);
		vertex(0, height);
		endShape();
	}

	public void drawBackground()
	{
		noStroke();
		noTint();
		image(bg, 0, 0, width, height);
		calcWave();
		renderWave();
		//blend(bg2, 0, 0, width, height, 0, 0, width, height, MULTIPLY);
	}

	public void drawBoard()
	{
		stroke(0);
		strokeWeight(1);
		fill(20, 20, 20, 200);
		rect(boardOffsetX, boardOffsetY, cols*blockSize, rows*blockSize);
		
		for(int x = 0; x < cols; x++ )
		{
			if(foomAnimations[x] != null)
			{
				FoomAnimation f = foomAnimations[x];
				boolean done = f.step();
				tint(255, f.opacity);
				image(foomImage, f.x, f.y, blockSize, f.height);
				if(done)
					foomAnimations[x] = null;
			}
			for(int y = 0; y < rows; y++)
			{
				board[x][y].drawBlock(x, y);
			}
		}
		ListIterator<Block> li = blockAnimations.listIterator();
		LinkedList<Block> deletedBlocks = new LinkedList<Block>();
		Block b;
        while (li.hasNext()) {
        	b = li.next();
            b.drawBlock(0, 0);
            if(b.animation == null)
            {
            	deletedBlocks.add(b);
            }
        }
        li = deletedBlocks.listIterator();
        while (li.hasNext()) {
        	blockAnimations.remove(li.next());
        }
	}

	public void currentLanded()
	{
		current.saveToBoard();
		current = randomTet();
		
		int linesClear = 0;
		for(int y = rows-1; y >= 0; y--)
		{
			boolean clear = true;
			for(int x = 0; x < cols; x++)
			{
				clear = clear && board[x][y].isEmpty == false;
				if(!clear) break;
			}
			if(clear)
			{
				
				// set animations
				Block b;
				for(int x2 = 0; x2 < cols; x2++)
				{
					b = new Block();
					b.setColor(board[x2][y].blockColor);
					b.animation = new Animation(
							boardOffsetX + x2*blockSize,
							boardOffsetY + (y-linesClear)*blockSize,
							boardOffsetX + x2*blockSize + (int)random(-10, 10),
							boardOffsetY + (y+1-linesClear)*blockSize,
							0xFF,
							0x0, 1000);
					blockAnimations.add(b);
				}
				linesClear++;
				for(int y2 = y-1; y2 > 0; y2--)
				{
					for(int x2 = 0; x2 < cols; x2++)
					{
						if(board[x2][y2].isEmpty)
							board[x2][y2+1].setEmpty();
						else
							board[x2][y2+1].setColor(board[x2][y2].blockColor);
					}
				}
				for(int i = 0; i < cols; i++)
					board[0][i].setEmpty();
				y++;
			}
		}
		int pts = pointsForLines(linesClear);
		points += pts;
		if(totLines == linesClear)
		{
			if(totLines >= (level*10))
				level++;
		}
		
		if(linesClear == 4)
		{
			tetrisSnd.rewind();
			tetrisSnd.play();
		}
		else if(linesClear > 0)
		{
			clearSnd.rewind();
			clearSnd.play();
		}
		if(current.collision())
		{
			gameOver();
		}
	}

	public void stepGame()
	{
		int dt = millis() - lastTime;
		if(dt >= 500)
		{
			if(!current.step())
			{
				currentLanded();
				placeSnd.rewind();
				placeSnd.play();
			}
			lastTime = millis();
		}
	}

	public void draw()
	{
		if(playing)
			stepGame();
		drawBackground();
		drawBoard();
		current.drawTet();
		current.drawOutline();
		
		if(paused)
		{
			fill(255);
			textAlign(CENTER);
			textFont(font);
			text("Paused", boardOffsetX + (cols*blockSize)/2, height/2);
		}
		if(gameOver)
		{
			fill(255);
			textAlign(CENTER);
			textFont(font);
			text("Game Over", boardOffsetX + (cols*blockSize)/2, height/2);
		}
		
		fill(255);
		textFont(font, 14);
		textAlign(LEFT);
		text("FPS: " + frameRate, 10, 800);
		
		textFont(font, 20);
		textAlign(LEFT);
		text("Points:\n" + points, boardOffsetX + cols*blockSize + 40, height/2);
	}

	public void keyPressed()
	{
		if(key == CODED)
		{
			if(keyCode == UP)
			{
				if(playing)
				{
					current.rot();
					clickSnd.rewind();
					clickSnd.play();
				}
			}
			else if(keyCode == LEFT)
			{
				if(playing)
				{
					current.pushLeft();
					clickSnd.rewind();
					clickSnd.play();
					//lastTime = millis();
				}
			}
			else if(keyCode == RIGHT)
			{
				if(playing)
				{
					current.pushRight();
					clickSnd.rewind();
					clickSnd.play();
					//lastTime = millis();
				}
			}
			else if(keyCode == DOWN)
			{
				if(playing)
				{
					if(!current.step())
					{
						currentLanded();
						placeSnd.rewind();
						placeSnd.play();
					}
					else
					{
						clickSnd.rewind();
						clickSnd.play();
					}
					lastTime = millis();
				}
			}
		}
		else if(key == ' ')
		{
			if(playing)
			{
				/*int[][] startX = new int[current.bounds.length][current.bounds[0].length];
				int[][] startY = new int[current.bounds.length][current.bounds[0].length];
				for(int i = 0; i < current.bounds.length; i++)
				{
					for(int j = 0; j < current.bounds[0].length; j++)
					{
						print("Block " + current.offsetX(i) + ", " + current.offsetY(j) + "\n");
						startX[i][j] = boardOffsetX + current.offsetX(i)*blockSize;
						startY[i][j] = boardOffsetY + current.offsetY(j)*blockSize;
					}
				}*/
				int ax = -1;
				int ay = -1;
				int by = -1;
				int bx = -1;
				for(int j = 0; j < current.bounds[0].length; j++)
				{
					for(int i = 0; i < current.bounds.length; i++)
					{
						
						if(current.bounds[i][j].isEmpty == false)
						{
							if(ax == -1)
								ax = current.offsetX(i);
							else
								ax = min(current.offsetX(i), ax);
							if(ay == -1)
								ay = current.offsetY(j);
							if(bx == -1)
								bx = current.offsetX(i);
							else
								bx = max(bx, current.offsetX(i));
							by = j;
						}
					}
				}
				while(current.step());
				foomSnd.rewind();
				foomSnd.play();
				for(int xx = ax, i= 0; xx <= bx; xx++, i++)
				{
					foomAnimations[xx] = new FoomAnimation(boardOffsetX + (ax+i)*blockSize, boardOffsetY + ay*blockSize, (current.offsetY(by) - ay + 1)*blockSize, 0xAA, 0x00, 1000);
				}
				/*for(int i = 0; i < current.bounds.length; i++)
				{
					for(int j = 0; j < current.bounds[0].length; j++)
					{
						current.bounds[i][j].animation = new Animation(
							startX[i][j],
							startY[i][j],
							boardOffsetX + current.offsetX(i)*blockSize,
							boardOffsetY + current.offsetY(j)*blockSize,
							255,
							255,
							30);
						blockAnimations.add(current.bounds[i][j]);
					}
				}*/
				currentLanded();
			}
		}
		else if(key == 'n')
		{
			newGame();
		}
		else if(key == 'p')
		{
			if(playing)
			{
				playing = false;
				paused = true;
			}
			else
			{
				playing = true;
				paused = false;
			}
		}
	}


	public Tetromino randomTet()
	{
		int n = (int)random(7);
		switch(n)
		{
			case 0:
				return new ITetromino();
			case 1:
				return new OTetromino();
			case 2:
				return new TTetromino();
			case 3:
				return new STetromino();
			case 4:
				return new ZTetromino();
			case 5:
				return new JTetromino();
			case 6:
				return new LTetromino();
			default: // never reached
				return new ITetromino();
		}
	}


























}
