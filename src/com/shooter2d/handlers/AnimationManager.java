package com.shooter2d.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationManager {
	private TextureRegion[] frames;
	private float time;
	private float delay;
	private int currentFrame;
	private int timesPlayed;

	public AnimationManager(TextureRegion[] frames, float delay) {
		setFrames(frames, delay);
	}
	
	public void setFrames(TextureRegion[] frames, float delay) {
		this.frames = frames;
		this.delay = delay;
		time = 0;
		currentFrame = 0;
		timesPlayed = 0;
	}
	
	public void update(float dt) {
		if(delay <= 0) return;
		
		time += dt;
		while(time >= delay) {
			step();
		}
	}
	
	private void step() {
		time -= delay;
		currentFrame++;
		
		//if the last frame is reached go back to the first
		if(currentFrame == frames.length) {
			timesPlayed++;
			currentFrame = 0;
		}
	}
	
	public TextureRegion getFrame() {
		return frames[currentFrame];
	}

	public int getTimesPlayed() {
		return timesPlayed;
	}
}
