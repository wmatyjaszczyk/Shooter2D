package com.shooter2d.handlers;

import com.badlogic.gdx.math.Vector2;

public class GameInput {
	private static boolean[] keys;
	private static boolean[] prevKeys;
	private static Vector2 mousePosition;
	
	public static final int NUM_KEYS = 5;
	public static final int KEY_UP = 0;
	public static final int KEY_DOWN = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_SHOOT = 4;
	
	static {
		keys = new boolean[NUM_KEYS];
		prevKeys = new boolean[NUM_KEYS];
		mousePosition = new Vector2();
	}
	
	public static void update() {
		for(int i = 0; i < NUM_KEYS; i++) {
			prevKeys[i] = keys[i];
		}
	}
	
	public static boolean isDown(int i) {
		return keys[i];
	}
	
	public static boolean isPressed(int i) {
		return keys[i] && !prevKeys[i];
	}
	
	public static void setKey(int i, boolean b) {
		keys[i] = b;
	}
	
	public static void setMousePosition(int x, int y) {
		mousePosition.x = x;
		mousePosition.y = y;
	}
	
	public static Vector2 getMousePosition() {
		return mousePosition;
	}
}
