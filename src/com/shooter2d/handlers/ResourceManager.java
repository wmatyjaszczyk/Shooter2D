package com.shooter2d.handlers;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class ResourceManager {
	private HashMap<String, Texture> textures;
	
	public ResourceManager() {
		textures = new HashMap<String, Texture>();
	}
	
	public void loadTexture(String path, String name) {
		Texture t = new Texture(Gdx.files.internal(path));
		textures.put(name, t);
	}
	
	public Texture getTexture(String name) {
		return textures.get(name);
	}
	
	public void disposeTexture(String name) {
		Texture t = textures.get(name);
		
		if (t != null) {
			t.dispose();
		}
	}
}
