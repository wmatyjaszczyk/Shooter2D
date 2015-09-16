package com.shooter2d.map;

import java.awt.Rectangle;

import com.badlogic.gdx.math.Vector2;

public class Room {
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Room(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean intersect(Room room) {
		Rectangle r1 = new Rectangle(x, y, width, height);
		Rectangle r2 = new Rectangle(room.getX(), room.getY(), room.getWidth(), room.getHeight());
		
		return r1.intersects(r2);
	}
	
	public Vector2 getCenter() {
		return new Vector2(x + width / 2, y + height / 2);
	}
	
	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
