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
	AudioSnippet holdSnd;

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

	int xspacing = 1; // How far apart should each horizontal location be spaced

	static float theta = 0.0f; // Start angle at 0
	static float amplitude = 75.0f; // Height of wave
	static float period = 1200.0f; // How many pixels before the wave repeats
	static float dx; // Value for incrementing X, a function of period and
	// xspacing
	static float[] yvalues; // Using an array to store height values for the
	// wave
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

	boolean playing = false;
	boolean paused = false;
	boolean gameOver = false;

	Block[][] board;

	static Setris instance;

	static PFont font;

	int lastTime;

	Tetromino current;
	Tetromino held = null;
	boolean heldUsed = false;

	LinkedList<Block> blockAnimations = new LinkedList<Block>();

	class FoomAnimation extends Animation {
		int height;

		FoomAnimation(int x, int y, int height, int opacity, int targetOpacity,
				int t) {
			super(x, y, x, y, opacity, targetOpacity, t);
			this.height = height;
		}

	}

	FoomAnimation[] foomAnimations = new FoomAnimation[cols];

	class TextAnimation extends Animation {
		String str;

		TextAnimation(String s, int x, int y, int targetX, int targetY,
				int opacity, int targetOpacity, int t) {
			super(x, y, targetX, targetY, opacity, targetOpacity, t);
			str = s;
		}

		boolean drawText() {
			boolean done = step();
			fill(255, opacity);
			noStroke();
			textFont(font, 38);
			text(str, x, y);
			return done;
		}
	}

	LinkedList<TextAnimation> textAnimations = new LinkedList<TextAnimation>();

	int level;
	int points;
	int totLines;
	int lastClear = 0;
	int lastPts = 0;

	public int pointsForLines(int lines) {
		if(lines == 0) return 0;
		int pts = 0;
		if(lines == lastClear)
		{
			animateText("Back to Back +3/2");
			pts = (3 * lastPts) / 2;
		}
		else
		{
			switch (lines) {
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
		}
		lastPts = pts;
		lastClear = lines;
		return pts;
	}

	void newGame() {
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				board[x][y] = new Block();
			}
			foomAnimations[x] = null;
		}
		Tetromino.prime();
		
		current = Tetromino.getTet();
		gameOverSnd.pause();
		gameOverSnd.rewind();
		startSnd.rewind();
		startSnd.play();

		held = null;
		playing = true;
		paused = false;
		gameOver = false;
		level = 1;
		totLines = 0;
		points = 0;

		
	}

	void gameOver() {
		playing = false;
		startSnd.pause();
		startSnd.rewind();
		gameOverSnd.rewind();
		gameOverSnd.play();
		gameOver = true;
	}

	void hold() {
		if (!heldUsed) {
			if (held == null) {
				current.x = startX;
				current.y = startY;
				held = current;
				current = Tetromino.getTet();
			} else {
				Tetromino tmp = current;
				current = held;
				held = tmp;
			}
			holdSnd.rewind();
			holdSnd.play();
			heldUsed = true;
		}
	}

	public void setup() {
		instance = this;
		size(paddingLeft + cols * blockSize + paddingRight, paddingTop + rows
				* blockSize + paddingBottom);
		frameRate(30);
		bg = loadImage("background.png");
		bg2 = loadImage("lines.png");
		dx = (TWO_PI / period) * xspacing;
		yvalues = new float[width / xspacing];
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
		holdSnd = minim.loadSnippet("Hold.wav");

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
			yvalues[i] = sin(x) * amplitude;
			x += dx;
		}
	}

	public void renderWave() {
		// A simple way to draw the wave with an ellipse at each location
		beginShape(POLYGON);
		fill(70, 124, 199, max(100 * cos((float) frameCount / 50.0f), 50));
		for (int x = 0; x < yvalues.length; x++) {
			noStroke();
			vertex(x, yvalues[x] + height / 2);
		}
		vertex(width, height);
		vertex(0, height);
		endShape();

		beginShape(POLYGON);
		fill(70, 124, 199, max(100 * sin((float) frameCount / 50.0f), 50));
		for (int x = 0; x < yvalues.length; x++) {
			noStroke();
			vertex(x, amplitude - yvalues[x] * 1.5f + height / 2);
		}
		vertex(width, height);
		vertex(0, height);
		endShape();
	}

	public void drawBackground() {
		noStroke();
		noTint();
		image(bg, 0, 0, width, height);
		calcWave();
		renderWave();
		// blend(bg2, 0, 0, width, height, 0, 0, width, height, MULTIPLY);
	}

	public void drawBoard() {
		stroke(0);
		strokeWeight(1);
		fill(20, 20, 20, 200);
		rect(boardOffsetX, boardOffsetY, cols * blockSize, rows * blockSize);

		for (int x = 0; x < cols; x++) {
			if (foomAnimations[x] != null) {
				FoomAnimation f = foomAnimations[x];
				boolean done = f.step();
				tint(255, f.opacity);
				image(foomImage, f.x, f.y, blockSize, f.height);
				if (done)
					foomAnimations[x] = null;
			}
			for (int y = 0; y < rows; y++) {
				board[x][y].drawBlock(x, y);
			}
		}
		ListIterator<Block> li = blockAnimations.listIterator();
		LinkedList<Block> deletedBlocks = new LinkedList<Block>();
		Block b;
		while (li.hasNext()) {
			b = li.next();
			b.drawBlock(0, 0);
			if (b.animation == null) {
				deletedBlocks.add(b);
			}
		}
		li = deletedBlocks.listIterator();
		while (li.hasNext()) {
			blockAnimations.remove(li.next());
		}

		ListIterator<TextAnimation> li2 = textAnimations.listIterator();
		LinkedList<TextAnimation> deletedTexts = new LinkedList<TextAnimation>();
		TextAnimation a;
		while (li2.hasNext()) {
			a = li2.next();
			if (a.drawText() == true) {
				deletedTexts.add(a);
			}
		}
		li2 = deletedTexts.listIterator();
		while (li2.hasNext()) {
			textAnimations.remove(li2.next());
		}

		if (!gameOver) {
			if (held != null) {
				held.drawArbitrary(50, 50, 0.6f, 255);
			}
			Tetromino.peek1().drawArbitrary(
					boardOffsetX + cols * blockSize + 50, 50, 0.7f, 255);
			Tetromino.peek2().drawArbitrary(
					boardOffsetX + cols * blockSize + 55, 150, 0.5f, 200);
		}

	}

	public void currentLanded() {
		current.saveToBoard();
		current = Tetromino.getTet();

		int linesClear = 0;
		for (int y = rows - 1; y >= 0; y--) {
			boolean clear = true;
			for (int x = 0; x < cols; x++) {
				clear = clear && board[x][y].isEmpty == false;
				if (!clear)
					break;
			}
			if (clear) {

				// set animations
				Block b;
				for (int x2 = 0; x2 < cols; x2++) {
					b = new Block();
					b.setColor(board[x2][y].blockColor);
					b.animation = new Animation(boardOffsetX + x2 * blockSize,
							boardOffsetY + (y - linesClear) * blockSize,
							boardOffsetX + x2 * blockSize
									+ (int) random(-10, 10), boardOffsetY
									+ (y + 1 - linesClear) * blockSize, 0xFF,
							0x0, 1000);
					blockAnimations.add(b);
				}
				linesClear++;
				for (int y2 = y - 1; y2 > 0; y2--) {
					for (int x2 = 0; x2 < cols; x2++) {
						if (board[x2][y2].isEmpty)
							board[x2][y2 + 1].setEmpty();
						else
							board[x2][y2 + 1]
									.setColor(board[x2][y2].blockColor);
					}
				}
				for (int i = 0; i < cols; i++)
					board[0][i].setEmpty();
				y++;
			}
		}
		int pts = pointsForLines(linesClear);
		points += pts;
		if (totLines == linesClear) {
			if (totLines >= (level * 10))
				level++;
		}

		if (linesClear == 4) {
			tetrisSnd.rewind();
			tetrisSnd.play();
			animateText("Tetris");
		} else if (linesClear > 0) {
			if (linesClear == 3)
				animateText("Triple");
			else if (linesClear == 2)
				animateText("Double");
			clearSnd.rewind();
			clearSnd.play();
		}
		if (pts > 0)
			animateText(Integer.toString(pts));
		if (current.collision()) {
			gameOver();
		}
		heldUsed = false;
	}

	public void animateText(String s) {
		int x = boardOffsetX + (int) random(cols * blockSize);
		int y = boardOffsetY + 150 + (int) random(rows * blockSize - 250);
		int mid = boardOffsetX + (cols * blockSize) / 2;
		int tx = 0;
		int ty = y - 10;
		if (x < mid)
			tx = x - 10;
		else
			tx = x + 10;
		TextAnimation a = new TextAnimation(s, x, y, tx, ty, 255, 0, 4000);
		textAnimations.add(a);
	}

	public void stepGame() {
		int dt = millis() - lastTime;
		if (dt >= 1000) {
			if (!current.step()) {
				currentLanded();
				placeSnd.rewind();
				placeSnd.play();
			}
			lastTime = millis();
		}
	}

	public void draw() {
		if (playing)
			stepGame();
		drawBackground();
		drawBoard();
		current.drawTet();
		current.drawOutline();

		if (paused) {
			fill(255);
			textAlign(CENTER);
			textFont(font);
			text("Paused", boardOffsetX + (cols * blockSize) / 2, height / 2);
		}
		if (gameOver) {
			fill(255);
			textAlign(CENTER);
			textFont(font);
			text("Game Over", boardOffsetX + (cols * blockSize) / 2, height / 2);
		}

		fill(255);
		textFont(font, 14);
		textAlign(LEFT);
		text("FPS: " + frameRate, 10, 800);

		textFont(font, 20);
		textAlign(LEFT);
		text("Points:\n" + points, boardOffsetX + cols * blockSize + 40,
				height / 2);
	}

	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == UP) {
				if (playing) {
					current.rot();
					clickSnd.rewind();
					clickSnd.play();
				}
			} else if (keyCode == LEFT) {
				if (playing) {
					current.pushLeft();
					clickSnd.rewind();
					clickSnd.play();
					// lastTime = millis();
				}
			} else if (keyCode == RIGHT) {
				if (playing) {
					current.pushRight();
					clickSnd.rewind();
					clickSnd.play();
					// lastTime = millis();
				}
			} else if (keyCode == DOWN) {
				if (playing) {
					if (!current.step()) {
						currentLanded();
						placeSnd.rewind();
						placeSnd.play();
					} else {
						clickSnd.rewind();
						clickSnd.play();
					}
					lastTime = millis();
				}
			} else if (keyCode == SHIFT) {
				if (playing) {
					hold();
				}
			}
		} else if (key == ' ') {
			if (playing) {
				/*
				 * int[][] startX = new
				 * int[current.bounds.length][current.bounds[0].length]; int[][]
				 * startY = new
				 * int[current.bounds.length][current.bounds[0].length]; for(int
				 * i = 0; i < current.bounds.length; i++) { for(int j = 0; j <
				 * current.bounds[0].length; j++) { print("Block " +
				 * current.offsetX(i) + ", " + current.offsetY(j) + "\n");
				 * startX[i][j] = boardOffsetX + current.offsetX(i)*blockSize;
				 * startY[i][j] = boardOffsetY + current.offsetY(j)*blockSize; }
				 * }
				 */
				int ax = -1;
				int ay = -1;
				int by = -1;
				int bx = -1;
				for (int j = 0; j < current.bounds[0].length; j++) {
					for (int i = 0; i < current.bounds.length; i++) {

						if (current.bounds[i][j].isEmpty == false) {
							if (ax == -1)
								ax = current.offsetX(i);
							else
								ax = min(current.offsetX(i), ax);
							if (ay == -1)
								ay = current.offsetY(j);
							if (bx == -1)
								bx = current.offsetX(i);
							else
								bx = max(bx, current.offsetX(i));
							by = j;
						}
					}
				}
				while (current.step())
					;
				foomSnd.rewind();
				foomSnd.play();
				for (int xx = ax, i = 0; xx <= bx; xx++, i++) {
					foomAnimations[xx] = new FoomAnimation(boardOffsetX
							+ (ax + i) * blockSize, boardOffsetY + ay
							* blockSize, (current.offsetY(by) - ay + 1)
							* blockSize, 0xAA, 0x00, 1000);
				}
				/*
				 * for(int i = 0; i < current.bounds.length; i++) { for(int j =
				 * 0; j < current.bounds[0].length; j++) {
				 * current.bounds[i][j].animation = new Animation( startX[i][j],
				 * startY[i][j], boardOffsetX + current.offsetX(i)*blockSize,
				 * boardOffsetY + current.offsetY(j)*blockSize, 255, 255, 30);
				 * blockAnimations.add(current.bounds[i][j]); } }
				 */
				currentLanded();
			}
		} else if (key == 'n') {
			newGame();
		} else if (key == 'p') {
			if (playing) {
				playing = false;
				paused = true;
			} else {
				playing = true;
				paused = false;
			}
		}
	}
}
