package com.shooter2d.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.shooter2d.handlers.GameStateManager;
import com.shooter2d.main.Game;

public abstract  class GameState {
	protected Game game;
	protected GameStateManager gsm;
	protected SpriteBatch sb;
	protected OrthographicCamera cam;
	protected OrthographicCamera hudCam;
	
	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
		game = gsm.getGame();
		sb = game.getSb();
		cam = game.getCam();
		hudCam = game.getHudCam();
	}
	
	public abstract void handleInput();
	public abstract void render();
	public abstract void update(float dt);
	public abstract void dispose();
}
