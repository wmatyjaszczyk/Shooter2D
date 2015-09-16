package com.shooter2d.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {
	public boolean keyDown(int k) {
		switch (k) {
		case Keys.W:
			GameInput.setKey(GameInput.KEY_UP, true);
			break;

		case Keys.S:
			GameInput.setKey(GameInput.KEY_DOWN, true);
			break;
			
		case Keys.A:
			GameInput.setKey(GameInput.KEY_LEFT, true);
			break;
			
		case Keys.D:
			GameInput.setKey(GameInput.KEY_RIGHT, true);
			break;
			
		case Keys.SPACE:
			GameInput.setKey(GameInput.KEY_SHOOT, true);
			break;
		}

		return true;
	}

	public boolean keyUp(int k) {
		switch (k) {
		case Keys.W:
			GameInput.setKey(GameInput.KEY_UP, false);
			break;

		case Keys.S:
			GameInput.setKey(GameInput.KEY_DOWN, false);
			break;

		case Keys.A:
			GameInput.setKey(GameInput.KEY_LEFT, false);
			break;

		case Keys.D:
			GameInput.setKey(GameInput.KEY_RIGHT, false);
			break;

		case Keys.SPACE:
			GameInput.setKey(GameInput.KEY_SHOOT, false);
			break;
		}

		return true;
	}
	
	public boolean mouseMoved(int x, int y) {
		GameInput.setMousePosition(x, y);
		
		return true;
	}
}
