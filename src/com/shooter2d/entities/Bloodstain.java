package com.shooter2d.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.shooter2d.main.Game;

public class Bloodstain extends Entity{
	private float maxTime;
	private float actualTime;
	
	public Bloodstain(Body body) {
		super(body, 32, 32);
		
		maxTime = 5f;
		actualTime = 0f;
		
		Texture t = Game.res.getTexture("bloodstain");
		TextureRegion[] tr = TextureRegion.split(t, 32, 32)[1];
		
		setAnimation(tr, 0f);
	}
	
	public void update(float dt) {
		animation.update(dt);
		
		// checking if the bloodstain's lifetime expired
		actualTime += dt;
		
		if(actualTime >= maxTime) {
			setToBeDestroyed(true);
		}
	}
}
