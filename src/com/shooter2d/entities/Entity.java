package com.shooter2d.entities;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.shooter2d.handlers.AnimationManager;
import com.shooter2d.handlers.B2DVars;

public abstract class Entity {
	protected Body body;
	protected float width;
	protected float height;
	protected AnimationManager animation;
	protected LightEntity light;
	protected boolean isEmittingLight;
	protected boolean canEmitLight;
	protected boolean toBeDestroyed;
	protected float rotation;

	public Entity(Body body, float width, float height) {
		this.body = body;
		this.width = width;
		this.height = height;
		isEmittingLight = false;
		canEmitLight = false;
		toBeDestroyed = false;
		rotation = 0;
	}

	public Entity(Body body, float width, float height, int lightSize, int lightIntensity) {
		this.body = body;
		this.width = width;
		this.height = height;
		light = new LightEntity();
		light.setLight("light", lightSize, lightIntensity);
		isEmittingLight = true;
		canEmitLight = true;
		toBeDestroyed = false;
		rotation = 0;
	}

	public void setAnimation(TextureRegion[] textureRegion, float delay) {
		animation = new AnimationManager(textureRegion, delay);
		width = textureRegion[0].getRegionWidth();
		height = textureRegion[0].getRegionHeight();
	}

	public void setIsEmittingLight(boolean isEmittingLight) {
		this.isEmittingLight = isEmittingLight;
	}

	public void update(float dt) {
		animation.update(dt);
	}

	public void render(SpriteBatch sb) {
		Sprite sprite = new Sprite(animation.getFrame());
		
		sb.setColor(1, 1, 1, 1);
		
		if (isEmittingLight && canEmitLight) {
			// draw light
			sb.begin();
			for (int i = 0; i < light.getIntensity(); i++) {
				sb.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);
				sb.draw(light.getLight(), body.getPosition().x * B2DVars.PPM - light.getSize() / 2,
						body.getPosition().y * B2DVars.PPM - light.getSize() / 2, light.getSize(), light.getSize());
				sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
			sb.end();
		}

		// draw animation
		sprite.setRotation(rotation);
		
		sb.begin();
		sprite.setPosition(body.getPosition().x * B2DVars.PPM - getWidth() / 2, 
				body.getPosition().y * B2DVars.PPM - getHeight() / 2);
		sprite.draw(sb);
		sb.end();
	}

	public Body getBody() {
		return body;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public boolean getToBeDestroyed() {
		return toBeDestroyed;
	}
	
	public void setToBeDestroyed(boolean toBeDestroyed) {
		this.toBeDestroyed = toBeDestroyed;
	}
	
	public void setRotation(float angle) {
		rotation = angle;
	}
}
