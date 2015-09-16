package com.shooter2d.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Shooter2DDesktop {

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = Game.TITLE;
		cfg.width = Game.WIDTH;
		cfg.height = Game.HEIGHT;
		
		new LwjglApplication(new Game(), cfg);
	}
	
}
