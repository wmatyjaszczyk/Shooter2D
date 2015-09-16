package com.shooter2d.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.shooter2d.handlers.GameInput;
import com.shooter2d.handlers.GameInputProcessor;
import com.shooter2d.handlers.GameStateManager;
import com.shooter2d.handlers.ResourceManager;

public class Game implements ApplicationListener {
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 648;
	public static final String TITLE = "Shooter 2D";
	public static final float STEP = 1 / 60f;
	
	public static ResourceManager res;
	
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	private GameStateManager gsm;
	private float accum;

	public SpriteBatch getSb() {
		return sb;
	}

	public OrthographicCamera getCam() {
		return cam;
	}

	public OrthographicCamera getHudCam() {
		return hudCam;
	}

	@Override
	public void create() {
		Gdx.input.setInputProcessor(new GameInputProcessor());
		
		//load textures
		res = new ResourceManager();
		res.loadTexture("res/images/player.png", "player");
		res.loadTexture("res/images/light.png", "light");
		res.loadTexture("res/images/bullet.png", "bullet");
		res.loadTexture("res/images/default_tiles.png", "tiles");
		res.loadTexture("res/images/enemy1.png", "enemy1");
		res.loadTexture("res/images/bloodstain.png", "bloodstain");
		
		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		hudCam = new OrthographicCamera();
		gsm = new GameStateManager(this);
		
		cam.setToOrtho(false, WIDTH, HEIGHT);
		hudCam.setToOrtho(false, WIDTH, HEIGHT);
	}

	@Override
	public void render() {
		accum += Gdx.graphics.getDeltaTime();
		
		while(accum >= STEP) {
			accum -= STEP;
			
			gsm.update(STEP);
			gsm.render();
			
			GameInput.update();
		}
	}
	
	@Override
	public void dispose() {}

	@Override
	public void resize(int arg0, int arg1) {}
	
	@Override
	public void pause() {}
	
	@Override
	public void resume() {}

}
