package com.spaceproject.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Disposable;
import com.spaceproject.components.CameraFocusComponent;
import com.spaceproject.components.CharacterComponent;
import com.spaceproject.components.TextureComponent;
import com.spaceproject.config.LandConfig;
import com.spaceproject.generation.EntityFactory;
import com.spaceproject.systems.BoundsSystem;
import com.spaceproject.systems.CameraSystem;
import com.spaceproject.systems.CollisionSystem;
import com.spaceproject.systems.DebugUISystem;
import com.spaceproject.systems.DesktopInputSystem;
import com.spaceproject.systems.ExpireSystem;
import com.spaceproject.systems.HUDSystem;
import com.spaceproject.systems.MovementSystem;
import com.spaceproject.systems.OrbitSystem;
import com.spaceproject.systems.PlayerControlSystem;
import com.spaceproject.systems.SpaceLoadingSystem;
import com.spaceproject.systems.SpaceParallaxSystem;
import com.spaceproject.systems.SpaceRenderingSystem;
import com.spaceproject.systems.TouchUISystem;
import com.spaceproject.utility.MyScreenAdapter;

public class SpaceScreen extends MyScreenAdapter {
	
	public static Engine engine;
	
	public SpaceScreen(LandConfig landCFG) {
		
		// engine to handle all entities and components
		engine = new Engine();
		
		//add temporary test entities--------------------------------------------
		//TODO: need refactor, put in spawn system or initializer of sorts...
		
		//add test ships
		engine.addEntity(EntityFactory.createShip3(-100, 400));
		engine.addEntity(EntityFactory.createShip3(-200, 400));		
		engine.addEntity(EntityFactory.createShip3(-300, 400));
		engine.addEntity(EntityFactory.createShip3(-400, 400));
			
		//add player
		boolean startAsShip = true;//debug: start as ship or player
		Entity player = EntityFactory.createCharacter(landCFG.position.x, landCFG.position.y);
		Entity playerTESTSHIP = EntityFactory.createShip3(landCFG.position.x, landCFG.position.y, landCFG.shipSeed, player);
		player.getComponent(CharacterComponent.class).vehicle = playerTESTSHIP;
		
		if (startAsShip) {
			//start as ship	
			playerTESTSHIP.add(new CameraFocusComponent());
			engine.addEntity(playerTESTSHIP);
		} else {
			//start as player
			player.add(new CameraFocusComponent());
			engine.addEntity(player);
		}
		
		// Add systems to engine---------------------------------------------------------
		engine.addSystem(new PlayerControlSystem(this, player, landCFG));
		/*
		if (startAsShip) {
			engine.addSystem(new PlayerControlSystem(this, player, playerTESTSHIP, landCFG));//start as ship
		} else {
			engine.addSystem(new PlayerControlSystem(this, player, landCFG));//start as player
		}*/
		
		engine.addSystem(new SpaceRenderingSystem());
		engine.addSystem(new SpaceLoadingSystem());
		engine.addSystem(new SpaceParallaxSystem());
		engine.addSystem(new MovementSystem());
		engine.addSystem(new OrbitSystem());
		engine.addSystem(new DebugUISystem());
		engine.addSystem(new BoundsSystem());
		engine.addSystem(new ExpireSystem(1));
		engine.addSystem(new CameraSystem());
		engine.addSystem(new CollisionSystem());
		engine.addSystem(new HUDSystem());
		
		//add input system. touch on android and keys on desktop.
		if (Gdx.app.getType() == ApplicationType.Android) {
			engine.addSystem(new TouchUISystem());
		} else {
			engine.addSystem(new DesktopInputSystem());
		}
		
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		//update engine
		engine.update(delta);
			
		//terminate
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();			

	}

	@Override
	public void dispose() {
		System.out.println("Disposing: " + this.getClass().getSimpleName());
		super.dispose();
		
		//clean up after self
		//dispose of spritebatches and textures
		
		for (EntitySystem sys : engine.getSystems()) {
			if (sys instanceof Disposable)
				((Disposable) sys).dispose();
		}
		
		for (Entity ents : engine.getEntitiesFor(Family.all(TextureComponent.class).get())) {
			TextureComponent tex = ents.getComponent(TextureComponent.class);
			if (tex != null)
				tex.texture.dispose();
		}
		
		//engine.removeAllEntities();
		/*
		 * EXCEPTION_ACCESS_VIOLATION (0xc0000005) at pc=0x000007fefe7911cb,
		 * pid=5620, tid=5876 # # JRE version: Java(TM) SE Runtime Environment
		 * (8.0_91-b14) (build 1.8.0_91-b14) # Java VM: Java HotSpot(TM) 64-Bit
		 * Server VM (25.91-b14 mixed mode windows-amd64 compressed oops) #
		 * Problematic frame: # C [msvcrt.dll+0x11cb]
		 */
	}
	
	@Override
	public void hide() {
		//dispose();
	}
	
	@Override
	public void pause() { }
	@Override
	public void resume() { }

}
