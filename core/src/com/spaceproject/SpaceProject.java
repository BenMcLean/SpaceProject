package com.spaceproject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import com.spaceproject.components.PlanetComponent;
import com.spaceproject.config.CelestialConfig;
import com.spaceproject.config.KeyConfig;
import com.spaceproject.config.LandConfig;
import com.spaceproject.screens.*;
import com.spaceproject.utility.MyScreenAdapter;

public class SpaceProject extends Game {

	public static long SEED = 4; //test seed
	
	public static CelestialConfig celestcfg;
	public static KeyConfig keycfg;
	
	@Override
	public void create() {	
		MyScreenAdapter.game = this;
		
		//load values for things like key mapping, settings, default values for generation
		loadConfigs();
				
	
		//load test default values
		LandConfig landCFG = new LandConfig();
		landCFG.position = new Vector3();//start player at 0,0
		
		//test values for world
		PlanetComponent planet = new PlanetComponent();
		planet.mapSize = 512;
		planet.scale = 100;
		planet.octaves = 4;
		planet.persistence = 0.68f;
		planet.lacunarity = 2.6f;
		landCFG.planet = planet;
		
		setScreen(new SpaceScreen(landCFG));
		//setScreen(new WorldScreen(landCFG));
		//setScreen(new TestShipGenerationScreen(this));
		//setScreen(new TestNoiseScreen(this));
		//setScreen(new MainMenuScreen(this));
	}
	
	private static void loadConfigs() {
		//KEYS
		keycfg = new KeyConfig();
		keycfg.loadDefault();
		keycfg.saveToJson();
		
		/*
		FileHandle keyFile = Gdx.files.local("controls.txt");
		if (keyFile.exists()) {
			Json json = new Json();
			json.setUsePrototypes(false);
			
			keycfg = json.fromJson(KeyConfig.class, keyFile.readString());
			System.out.println("Loaded keys from json: " + json.toJson(keycfg));
		} else {
			keycfg = new KeyConfig();
			keycfg.loadDefault();
			//keycfg.saveToJson();
			System.out.println("No key file found. Loaded defaults.");
		}*/
		
		
		//CELESTIAL OBJECTS
		celestcfg = new CelestialConfig();
		celestcfg.loadDefault();
		celestcfg.saveToJson();
	}		
	
}
