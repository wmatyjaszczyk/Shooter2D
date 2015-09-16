package com.shooter2d.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.shooter2d.main.Game;

public class Bullet extends Entity{
	private float maxTime;
	private float actualTime;
	private float speed;
	private float angle; 
	
	public Bullet(Body body, String textureName, float angle) {
		super(body, 16, 19, 64, 1);
		
		Texture t = Game.res.getTexture(textureName);
		TextureRegion[] tr = TextureRegion.split(t, 16, 19)[0];
		
		setAnimation(tr, 1 / 12f);
		
		maxTime = 2f;
		actualTime = 0f;
		speed = 1800f;
		
		this.angle = angle;
	}
	
	public void update(float dt) {
		animation.update(dt);
		
		// checking if the bullet's lifetime expired
		actualTime += dt;
		
		if(actualTime >= maxTime) {
			setToBeDestroyed(true);
		}
		
		// updating the position
		Vector2 velocity = new Vector2(MathUtils.cos(MathUtils.degreesToRadians * angle), 
				MathUtils.sin(MathUtils.degreesToRadians * angle));
		
		body.setLinearVelocity(velocity.cpy().scl(-(speed * dt)));
	}

}
