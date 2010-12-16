package ie.theorie.setris;

import processing.core.*;

public class Block {
	PImage blockColor;
	Animation animation;
	boolean isEmpty;

	Block() {
		isEmpty = true;
		animation = null;
	}

	void setColor(PImage col) {
		blockColor = col;
		isEmpty = false;
	}

	void setEmpty() {
		isEmpty = true;
	}

	void drawBlock(int x, int y) {
		int opacity = 255;
		if (animation != null) {
			boolean done = animation.step();
			x = animation.x;
			y = animation.y;
			opacity = animation.opacity;
			if (done)
				animation = null;
			drawArbitrary(x, y, 1, opacity);
		} else {
			if (!Setris.playing)
				opacity = 80;
			Setris.instance.tint(255, opacity);
			if (!isEmpty)
				Setris.instance.image(blockColor, Setris.boardOffsetX + x
						* Setris.blockSize, Setris.boardOffsetY + y
						* Setris.blockSize);
		}
	}

	void drawArbitrary(int x, int y, float factor, int opacity) {
		Setris.instance.tint(0xFF, opacity);
		if (!isEmpty)
			Setris.instance.image(blockColor, x, y, factor * Setris.blockSize,
					factor * Setris.blockSize);
		Setris.instance.noTint();
	}

	void drawOutline(int x, int y) {
		Setris.instance.smooth();
		Setris.instance.strokeWeight(2);
		Setris.instance.strokeJoin(Setris.BEVEL);
		Setris.instance.noFill();
		Setris.instance.beginShape(Setris.POLYGON);
		Setris.print(Setris.boardOffsetX + x * Setris.blockSize + "\n");
		Setris.instance.vertex(Setris.boardOffsetX + x * Setris.blockSize,
				Setris.boardOffsetY + y * Setris.blockSize);
		Setris.instance.vertex(Setris.boardOffsetX + x * Setris.blockSize
				+ Setris.blockSize, Setris.boardOffsetY + y * Setris.blockSize);
		Setris.instance.vertex(Setris.boardOffsetX + x * Setris.blockSize
				+ Setris.blockSize, Setris.boardOffsetY + y * Setris.blockSize
				+ Setris.blockSize);
		Setris.instance.vertex(Setris.boardOffsetX + x * Setris.blockSize,
				Setris.boardOffsetY + y * Setris.blockSize + Setris.blockSize);
		Setris.instance.vertex(Setris.boardOffsetX + x * Setris.blockSize,
				Setris.boardOffsetY + y * Setris.blockSize);
		Setris.instance.endShape(Setris.POLYGON);
	}
}
