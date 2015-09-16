package com.shooter2d.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.shooter2d.entities.Bullet;
import com.shooter2d.entities.Enemy;

public class GameContactListener implements ContactListener {
	public GameContactListener() {
		super();
	}

	@Override
	public void beginContact(Contact c) {
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();

		// marking bullets to be destroyed when they hit the wall
		if (fa.getUserData().equals("bullet") && fb.getUserData().equals("wall")) {
			Bullet b = (Bullet) fa.getBody().getUserData();

			if (!b.getToBeDestroyed()) {
				b.setToBeDestroyed(true);
			}

		}

		if (fb.getUserData().equals("bullet") && fa.getUserData().equals("wall")) {
			Bullet b = (Bullet) fb.getBody().getUserData();

			if (!b.getToBeDestroyed()) {
				b.setToBeDestroyed(true);
			}
		}

		// when bullet collides with enemy
		if (fa.getUserData().equals("bullet") && fb.getUserData().equals("enemy")) {
			Bullet b = (Bullet) fa.getBody().getUserData();
			Enemy e = (Enemy) fb.getBody().getUserData();

			// destroy bullet
			if (!b.getToBeDestroyed()) {
				b.setToBeDestroyed(true);
			}

			// kill the enemy
			e.die();
		}

		if (fb.getUserData().equals("bullet") && fa.getUserData().equals("enemy")) {
			Bullet b = (Bullet) fb.getBody().getUserData();
			Enemy e = (Enemy) fa.getBody().getUserData();

			if (!b.getToBeDestroyed()) {
				b.setToBeDestroyed(true);
			}
			
			e.die();
		}
	}

	@Override
	public void endContact(Contact c) {
	}

	@Override
	public void postSolve(Contact c, ContactImpulse arg1) {

	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
	}

}
