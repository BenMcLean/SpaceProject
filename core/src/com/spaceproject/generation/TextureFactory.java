package com.spaceproject.generation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.spaceproject.SpaceProject;
import com.spaceproject.utility.OpenSimplexNoise;

public class TextureFactory {

	static Pixmap pixmap;
	
	public static Texture createTile(Color c) {
		pixmap = new Pixmap(1, 1, Format.RGB888);
		pixmap.setColor(c);
		pixmap.fill();
	
		Texture tex = new Texture(pixmap);
		pixmap.dispose();
		return tex;
	}
	
	public static Texture generateNoiseTile(long seed, int tileSize) {
		pixmap = new Pixmap(tileSize, tileSize, Format.RGBA4444);
		
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		OpenSimplexNoise rGen = new OpenSimplexNoise(seed + 1);
		OpenSimplexNoise gGen = new OpenSimplexNoise(seed + 2);
		OpenSimplexNoise bGen = new OpenSimplexNoise(seed + 3);
		double featureSize = 24;

		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
			
				double nx = x/featureSize, ny = y/featureSize;
				double i = noise.eval(nx, ny, 0);
				i = (i * 0.5) + 0.5; //convert from range [-1:1] to [0:1]
			
				double r = rGen.eval(nx, ny, 0);
				r = (r * 0.5) + 0.5; //convert from range [-1:1] to [0:1]
				double g = gGen.eval(nx, ny, 0);
				g = (g * 0.5) + 0.5; //convert from range [-1:1] to [0:1]
				double b = bGen.eval(nx, ny, 0);
				b = (b * 0.5) + 0.5; //convert from range [-1:1] to [0:1]
				
				pixmap.setColor(new Color((float)r, (float)g, (float)b, (float) i));
				pixmap.drawPixel(x, y);
			}
		}
		
		
		Texture tex = new Texture(pixmap);
		pixmap.dispose();
		return tex;
	}
	
	public static Texture generateSpaceBackground(int tileX, int tileY, int tileSize, float depth) {

		/* Note: A Global seed of 0 causes non-unique seeds. 
		 * Consider either disallowing a seed of 0 or change formula for getting each tiles seed
		 * This probably affects other parts of the program.
		 */
		
		MathUtils.random.setSeed((long)(tileX * depth + tileY * SpaceProject.SEED));
		
		//pixmap = new Pixmap(tileSize, tileSize, Format.RGB565);
		pixmap = new Pixmap(tileSize, tileSize, Format.RGBA4444);
		
		int numStars = 300;
		pixmap.setColor(Color.WHITE);
		for (int i = 0; i < numStars; ++i){					
			
			int newX = MathUtils.random(tileSize);
			int newY = MathUtils.random(tileSize);
			
			pixmap.drawPixel(newX, newY);
			//pixmap.drawPixel(newX, newY, Color.rgba8888(MathUtils.random(1), MathUtils.random(1), MathUtils.random(1), 1));
		}
		
		/*
		//DEBUG - fill tile to visualize boundaries
		pixmap.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.5f);
		pixmap.fill();
		*/
		
		//create texture and dispose pixmap to prevent memory leak
		Texture t = new Texture(pixmap);
		pixmap.dispose(); 
		return t;
	}

	/**
	 * Generate a unique ship.
	 * @param x position for seed
	 * @param y position for seed
	 * @param size of ship
	 * @return Texture of ship
	 */
	public static Texture generateShip(int x, int y, int size) {
		MathUtils.random.setSeed(x * size + y * SpaceProject.SEED);
		
		boolean debugImage = false;
		
		// generate pixmap texture
		pixmap = new Pixmap(size, size / 2, Format.RGBA4444);

		int width = pixmap.getWidth() - 1;
		int height = pixmap.getHeight() - 1;

		// 0-----width
		// |
		// |
		// |
		// height

		// smallest height a ship can be (4 because player is 4 pixels)
		int minEdge = 4;
		// smallest starting point for an edge
		float initialMinimumEdge = height * 0.8f;
		// edge to create shape of ship. initialize to random starting size
		int edge = MathUtils.random((int) initialMinimumEdge, height - 1);

		for (int yY = 0; yY <= width; yY++) {
			// draw body
			if (yY == 0 || yY == width) {
				// if first or last position of texture, "cap" it to complete
				// the edging
				pixmap.setColor(0.7f, 0.7f, 0.7f, 1);
			} else {
				pixmap.setColor(0, 0.5f, 0, 1);
			}
			
			if (!debugImage) {
				pixmap.drawLine(yY, edge, yY, height - edge);
			}

			// draw edging
			pixmap.setColor(0.7f, 0.7f, 0.7f, 1);
			pixmap.drawPixel(yY, edge);// bottom edge
			pixmap.drawPixel(yY, height - edge);// top edge

			// generate next edge
			// beginning and end of ship have special rule to not be greater
			// than the consecutive or previous edge
			// so that the "caps" look right
			if (yY == 0) { // beginning
				++edge;
			} else if (yY == width - 1) { // end
				--edge;
			} else { // body
				// random decide to move edge. if so, move edge either up or
				// down 1 pixel
				edge = MathUtils.randomBoolean() ? (MathUtils.randomBoolean() ? --edge
						: ++edge)
						: edge;
			}

			// keep edges within height and minEdge
			if (edge > height) {
				edge = height;
			}
			if (edge - (height - edge) < minEdge) {
				edge = (height + minEdge) / 2;
			}
		}	
				
		if (debugImage) {
			// fill to see image size/visual aid---------
			pixmap.setColor(1, 1, 1, 1);
			//pixmap.fill();

			// corner pins for visual aid----------------
			pixmap.setColor(1, 0, 0, 1);// red: top-right
			pixmap.drawPixel(0, 0);

			pixmap.setColor(0, 1, 0, 1);// green: top-left
			pixmap.drawPixel(width, 0);

			pixmap.setColor(0, 0, 1, 1);// blue: bottom-left
			pixmap.drawPixel(0, height);

			pixmap.setColor(1, 1, 0, 1);// yellow: bottom-right
			pixmap.drawPixel(width, height);
		}
				
		// create texture and dispose pixmap to prevent memory leak
		Texture t = new Texture(pixmap);
		pixmap.dispose();
		return t;
	}

	public static Texture generatePlanet(int radius) {
		OpenSimplexNoise noise = new OpenSimplexNoise();
		
		Pixmap pixmap = new Pixmap(radius * 2, radius * 2, Format.RGBA4444);
		
		double featureSize = 24;
		
		// draw circle for planet
		pixmap.setColor(1, 1, 1, 1);
		pixmap.fillCircle(radius, radius, radius - 1);
		
		//add layer of noise
		for (int y = 0; y < pixmap.getHeight(); ++y) {
			for (int x = 0; x < pixmap.getWidth(); ++x) {
				//only draw on circle
				if (pixmap.getPixel(x, y) != 0) {
					double nx = x/featureSize, ny = y/featureSize;
					double i = noise.eval(nx, ny, 0);
					i = (i * 0.5) + 0.5; //convert from range [-1:1] to [0:1]
					pixmap.setColor(new Color(0,(float)i,(float)(1-i), 1));
					pixmap.drawPixel(x, y);
				}				
			}
		}
		

		Texture t = new Texture(pixmap);
		pixmap.dispose();
		return t;
	}

	public static Texture generateStar(int radius) {
		OpenSimplexNoise noise = new OpenSimplexNoise();
		
		Pixmap pixmap = new Pixmap(radius * 2, radius * 2, Format.RGBA4444);
		
		double scale = 20;//zoom
		
		// draw circle for planet
		pixmap.setColor(0.5f, 0.5f, 0.5f, 1);
		pixmap.fillCircle(radius, radius, radius - 1);
		
		//add layer of noise
		for (int y = 0; y < pixmap.getHeight(); ++y) {
			for (int x = 0; x < pixmap.getWidth(); ++x) {
				//only draw on circle
				if (pixmap.getPixel(x, y) != 0) {
					double nx = x/scale, ny = y/scale;
					double i = noise.eval(nx, ny, 0);
					i = (i * 0.5) + 0.5; //convert from range [-1:1] to [0:1]
					if (i > 0.5f){
						pixmap.setColor(new Color(1, 1, 0, (float)i));
					} else {
						pixmap.setColor(new Color(1, 0, 0, (float)(1-i)));
					}
					pixmap.drawPixel(x, y);
				}				
			}
		}
		

		Texture t = new Texture(pixmap);
		pixmap.dispose();
		return t;
	}
	
	public static Texture generateNoise(long seed, int size, double featureSize) {
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA4444);
		
		//add layer of noise
		for (int y = 0; y < pixmap.getHeight(); ++y) {
			for (int x = 0; x < pixmap.getWidth(); ++x) {
				
				double nx = x / featureSize, ny = y / featureSize;
				double i = noise.eval(nx, ny, 0);
				i = (i * 0.5) + 0.5; // convert from range [-1:1] to [0:1]

				pixmap.setColor(new Color((float) i, (float) i, (float) i, 1));
				pixmap.drawPixel(x, y);

			}
		}
		

		Texture t = new Texture(pixmap);
		pixmap.dispose();
		return t;
	}
	
	public static Texture generateCharacter() {
		pixmap = new Pixmap(4, 4, Format.RGB565);
		
		//fill square
		pixmap.setColor(0.5f, 0.5f, 0.5f, 1);
		pixmap.fill();
		
		//draw face/eyes (front of character)
		pixmap.setColor(0, 1, 1, 1);
		pixmap.drawPixel(3, 2);
		pixmap.drawPixel(3, 1);
		
		Texture t = new Texture(pixmap);
		pixmap.dispose(); 
		return t;
	}
	
	public static Texture generateProjectile(int size) {
		pixmap = new Pixmap(size, size/2 == 0 ? 1 : size/2, Format.RGB565);
		pixmap.setColor(1,1,1,1);
		pixmap.fill();
		
		Texture t = new Texture(pixmap);
		pixmap.dispose(); 
		return t;
	}

}
