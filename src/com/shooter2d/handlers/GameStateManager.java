package com.shooter2d.handlers;

import java.util.Stack;

import com.shooter2d.main.Game;
import com.shooter2d.states.GameState;
import com.shooter2d.states.Play;

public class GameStateManager {

	public static final int PLAY = 0;
	
	private Game game;
	private Stack<GameState> gameStates;
	
	public GameStateManager(Game game) {
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(PLAY);
	}
	
	public Game getGame() {
		return game;
	}
	
	public void update(float dt) {
		gameStates.peek().update(dt);
	}
	
	public void render() {
		gameStates.peek().render();
	}
	
	private GameState getState(int state) {
		if(state == PLAY) return new Play(this);
		
		return null;
	}
	
	public void setState(int state) {
		popState();
		pushState(state);
	}
	
	public void pushState(int state) {
		gameStates.push(getState(state));
	}
	
	public void popState() {
		GameState g = gameStates.pop();
		g.dispose();
	}
}
