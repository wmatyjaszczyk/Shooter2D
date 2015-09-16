package com.shooter2d.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.shooter2d.main.Game;

public class Player extends Entity{
	public Player(Body body, String textureName) {
		super(body, 32, 32, 256, 2);
		
		Texture t = Game.res.getTexture(textureName);
		TextureRegion[] tr = TextureRegion.split(t, 32, 32)[0];
		
		setAnimation(tr, 1 / 12f);
	}
}
