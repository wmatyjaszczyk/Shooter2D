package com.shooter2d.entities;

import com.badlogic.gdx.graphics.Texture;
import com.shooter2d.main.Game;

public class LightEntity {
	private Texture light;
	private int size;
	private int intensity;
	
	public void setLight(String name, int size, int intensity) {
		light = Game.res.getTexture(name);
		this.size = size;
		this.intensity = intensity;
	}

	public Texture getLight() {
		return light;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getIntensity() {
		return intensity;
	}
}
