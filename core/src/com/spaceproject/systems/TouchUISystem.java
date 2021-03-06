package com.spaceproject.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.spaceproject.ui.TouchButtonRectangle;
import com.spaceproject.ui.TouchButtonRound;
import com.spaceproject.ui.TouchJoyStick;

/*TODO: test multiple screen sizes
 * https://developer.android.com/guide/practices/screens_support.html
 */
public class TouchUISystem extends EntitySystem {

	//rendering
	private Matrix4 projectionMatrix = new Matrix4();
	private ShapeRenderer shape = new ShapeRenderer();
	
	Color white = new Color(1f, 1f, 1f, 0.5f);
	Color blue = new Color(0.5f, 0.5f, 1f, 0.7f);
	
	TouchButtonRound btnShoot = new TouchButtonRound(Gdx.graphics.getWidth() - 80, 100, 70, white, blue);
	TouchButtonRound btnVehicle = new TouchButtonRound(Gdx.graphics.getWidth() - 80, 300, 50, white, blue);
	TouchButtonRectangle btnLand = new TouchButtonRectangle(Gdx.graphics.getWidth()/2-60, Gdx.graphics.getHeight() - 60 - 20, 120, 60, white, blue);
	TouchButtonRectangle btnMap = new TouchButtonRectangle(Gdx.graphics.getWidth()-120-20, Gdx.graphics.getHeight() - 60 - 20, 120, 60, white, blue);
	
	TouchJoyStick joyMovement = new TouchJoyStick(230, 230, 200, white, blue);
	
	@Override
	public void update(float delta) {
		
		PlayerControlSystem.shoot = btnShoot.isTouched();
		PlayerControlSystem.changeVehicle = btnVehicle.isTouched();
		PlayerControlSystem.land = btnLand.isTouched();
		if (btnMap.isJustTouched()) {
			HUDSystem.drawSpaceMap = !HUDSystem.drawSpaceMap;
		}
		btnLand.hidden = !PlayerControlSystem.canLand;
		
		if (joyMovement.isTouched()) {

			// face finger
			PlayerControlSystem.angleFacing = joyMovement.getAngle();

			//apply thrust
			PlayerControlSystem.movementMultiplier = joyMovement.getPowerRatio();

			// if finger is close to center of joystick, apply breaks
			if (joyMovement.getPowerRatio() < 0.20f) {
				// breaks
				PlayerControlSystem.moveForward = false;
				PlayerControlSystem.applyBreaks = true;
			} else {
				// move
				PlayerControlSystem.moveForward = true;
				PlayerControlSystem.applyBreaks = false;
			}
		} else {
			PlayerControlSystem.moveForward = false;
			PlayerControlSystem.applyBreaks = false;
		}
	
		//set projection matrix so things render using correct coordinates
		// TODO: only needs to be called when screen size changes
		projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shape.setProjectionMatrix(projectionMatrix);
			
		//draw buttons on screen
		drawControls();

	}

	/**
	 * Draw on-screen buttons.
	 */
	private void drawControls() { 
		//enable transparency
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		joyMovement.render(shape);
	
		shape.begin(ShapeType.Filled);		

		//draw shoot button
		btnShoot.render(shape);
			
		//draw vehicle button
		//TODO: test if player is in vehicle or can get in a vehicle;
		btnVehicle.render(shape);
		
		btnLand.render(shape);
		btnMap.render(shape);
		
		shape.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	
}
