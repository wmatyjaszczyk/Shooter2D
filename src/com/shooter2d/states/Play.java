package com.shooter2d.states;

import static com.shooter2d.handlers.B2DVars.PPM;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.shooter2d.entities.Bloodstain;
import com.shooter2d.entities.Bullet;
import com.shooter2d.entities.Enemy;
import com.shooter2d.entities.Player;
import com.shooter2d.handlers.B2DVars;
import com.shooter2d.handlers.GameContactListener;
import com.shooter2d.handlers.GameInput;
import com.shooter2d.handlers.GameStateManager;
import com.shooter2d.main.Game;
import com.shooter2d.map.RandomMapGenerator;

public class Play extends GameState {
	private final int MAP_WIDTH = 700;
	private final int MAP_HEIGHT = 700;
	
	private int[][] tiles;
	private boolean isNight;
	private World world;
	private Box2DDebugRenderer b2dr;
	private OrthographicCamera b2dCam;
	private GameContactListener cl;
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer otmr;
	private Player player;
	private Array<Bullet> bullets;
	private Array<Enemy> enemies;
	private Array<Bloodstain> bloodstains;
	private Array<ParticleEffect> particles;
	
	public Play(GameStateManager gsm) {
		super(gsm);

		cl = new GameContactListener();
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.WIDTH / PPM, Game.HEIGHT / PPM);
		bullets = new Array<Bullet>();
		enemies = new Array<Enemy>();
		bloodstains = new Array<Bloodstain>();
		particles = new Array<ParticleEffect>();

		// create player
		createPlayer();

		// create tiled map
		createMap(MAP_WIDTH, MAP_HEIGHT, 115);

		// create enemies
		createEnemies();
		
		setNight(true);
	}

	@Override
	public void handleInput() {
		// convert mouse position from screen to in-game
		Vector3 mousePosition = cam
				.unproject(new Vector3(GameInput.getMousePosition().x, GameInput.getMousePosition().y, 0));

		float angle = (float) Math.atan2(player.getPosition().y * PPM - mousePosition.y,
				player.getPosition().x * PPM - mousePosition.x) * MathUtils.radiansToDegrees;

		if (GameInput.isDown(GameInput.KEY_UP)) {
			player.getBody().setLinearVelocity(0, 4);
		}

		if (GameInput.isDown(GameInput.KEY_DOWN)) {
			player.getBody().setLinearVelocity(0, -4);
		}

		if (GameInput.isDown(GameInput.KEY_LEFT)) {
			player.getBody().setLinearVelocity(-4, 0);
		}

		if (GameInput.isDown(GameInput.KEY_RIGHT)) {
			player.getBody().setLinearVelocity(4, 0);
		}

		if (GameInput.isDown(GameInput.KEY_UP) && GameInput.isDown(GameInput.KEY_LEFT)) {
			player.getBody().setLinearVelocity(-4, 4);
		}

		if (GameInput.isDown(GameInput.KEY_UP) && GameInput.isDown(GameInput.KEY_RIGHT)) {
			player.getBody().setLinearVelocity(4, 4);
		}

		if (GameInput.isDown(GameInput.KEY_DOWN) && GameInput.isDown(GameInput.KEY_LEFT)) {
			player.getBody().setLinearVelocity(-4, -4);
		}

		if (GameInput.isDown(GameInput.KEY_DOWN) && GameInput.isDown(GameInput.KEY_RIGHT)) {
			player.getBody().setLinearVelocity(4, -4);
		}

		if (GameInput.isPressed(GameInput.KEY_SHOOT)) {
			createBullet(angle);
		}

		// rotate the player if mouse is moved (in degrees)
		player.setRotation(90 + angle);
		player.getBody().setTransform(player.getBody().getPosition(), MathUtils.degreesToRadians * (90 + angle));
	}

	@Override
	public void render() {
		// clear the screen
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// render tiled map - if isNight is set true then render it darker
		if (isNight) {
			sb.setColor(0.3f, 0.3f, 0.3f, 0.9f);
		}

		otmr.setView(cam);
		otmr.render();

		// render blood
		for (Bloodstain i : bloodstains) {
			i.render(sb);
		}

		// render player
		player.render(sb);

		// render enemies
		for (Enemy i : enemies) {
			i.render(sb);
		}

		// render bullets
		for (Bullet i : bullets) {
			i.render(sb);
		}
		
		// render particles
		for (ParticleEffect i : particles) {
			sb.begin();
			i.draw(sb);
			sb.end();
		}

		// render box2d debug
		b2dr.render(world, b2dCam.combined);
	}

	@Override
	public void update(float dt) {
		// handle the input
		handleInput();

		// update camera
		cameraUpdate(dt);

		// update physics
		world.step(dt, 6, 2);

		// update player
		player.update(dt);

		// reset player velocity
		player.getBody().setLinearVelocity(0, 0);

		// update enemies
		for (Enemy i : enemies) {
			i.update(dt);

			// check if they are ready to be removed
			if (i.getToBeDestroyed()) {
				enemies.removeValue(i, true);
				world.destroyBody(i.getBody());
				
				// when enemy is destroyed create blood and particles
				createBloodstain(i.getPosition());
				createParticles(new Vector2(i.getPosition().x * PPM, i.getPosition().y * PPM), "res/images/blood.p");
			}
		}

		// update bullets
		for (Bullet i : bullets) {
			i.update(dt);

			// check if they are ready to be removed
			if (i.getToBeDestroyed()) {
				bullets.removeValue(i, true);
				world.destroyBody(i.getBody());
			}
		}

		// update blood
		for (Bloodstain i : bloodstains) {
			i.update(dt);

			// check if they are ready to be removed
			if (i.getToBeDestroyed()) {
				bloodstains.removeValue(i, true);
				world.destroyBody(i.getBody());
			}
		}

		// update particles
		for (ParticleEffect i : particles) {
			i.update(dt);
			
			if(i.isComplete()) {
				particles.removeValue(i, true);
			}
		}
	}

	@Override
	public void dispose() {
	}

	private void cameraUpdate(float dt) {
		// using lerp makes the cameras movement smoother
		float lerp = 0.9f;
		Vector3 camPosition;

		// update box2d camera
		camPosition = b2dCam.position;
		camPosition.x += (player.getPosition().x - camPosition.x) * lerp * dt;
		camPosition.y += (player.getPosition().y - camPosition.y) * lerp * dt;
		b2dCam.position.set(camPosition);
		b2dCam.update();

		// update main camera
		camPosition = cam.position;
		camPosition.x += (player.getPosition().x * PPM - camPosition.x) * lerp * dt;
		camPosition.y += (player.getPosition().y * PPM - camPosition.y) * lerp * dt;
		sb.setProjectionMatrix(cam.combined);
		cam.position.set(camPosition);
		cam.update();
	}

	private void createPlayer() {
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		// create body for player
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(250 / PPM, 450 / PPM);
		Body body = world.createBody(bdef);

		// create fixture and shape for player's body
		shape.setAsBox(16 / PPM, 16 / PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_WALL | B2DVars.BIT_ENEMY;
		body.createFixture(fdef).setUserData("player");

		// create new player using "player" texture
		player = new Player(body, "player");
	}
	
	private void createEnemies() {
		Random rand = new Random();
		
		for(int i = 0; i < MAP_WIDTH; i++) {
			for(int j = 0; j < MAP_HEIGHT; j++) {
				if(tiles[i][j] == RandomMapGenerator.TILE_RED_ROOM) {
					if(rand.nextInt(100) > 97) {
						// if tile is a redroom there is a chance to create enemy on a tile
						BodyDef bdef = new BodyDef();
						FixtureDef fdef = new FixtureDef();
						PolygonShape shape = new PolygonShape();
						
						// enemy has to be a kinematic body
						bdef.type = BodyType.KinematicBody;
						bdef.position.set(0.5f + i, 0.5f + j);
						Body body = world.createBody(bdef);

						shape.setAsBox(16 / PPM, 16 / PPM);
						fdef.shape = shape;
						fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
						fdef.filter.maskBits = B2DVars.BIT_WALL | B2DVars.BIT_BULLET | B2DVars.BIT_PLAYER;
						body.createFixture(fdef).setUserData("enemy");

						Enemy enemy = new Enemy(body, "enemy1");
						enemies.add(enemy);
						body.setUserData(enemy);
					}
				}
			}
		}
	}

	private void createMap(int width, int height, int numberOfRooms) {
		tiles = new int[width][height];
		RandomMapGenerator map = new RandomMapGenerator();
		tiledMap = new TiledMap();

		// generate random map
		tiles = map.generateMap(width, height, numberOfRooms);

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		MapLayers layers = tiledMap.getLayers();
		TiledMapTileLayer layerGround = new TiledMapTileLayer(width, height, 32, 32);
		TiledMapTileLayer layerWall = new TiledMapTileLayer(width, height, 32, 32);

		otmr = new OrthogonalTiledMapRenderer(tiledMap, sb);

		// set cells for layers
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (tiles[i][j] == RandomMapGenerator.TILE_RED_ROOM || tiles[i][j] == RandomMapGenerator.TILE_HALLWAY) {
					Cell cell = new Cell();
					cell.setTile(
							new StaticTiledMapTile(TextureRegion.split(Game.res.getTexture("tiles"), 32, 32)[0][0]));
					layerGround.setCell(i, j, cell);

					player.getBody().setTransform(i, j, 0);
				}

				if (tiles[i][j] == RandomMapGenerator.TILE_WALL) {
					Cell cell = new Cell();
					cell.setTile(
							new StaticTiledMapTile(TextureRegion.split(Game.res.getTexture("tiles"), 32, 32)[0][3]));
					layerWall.setCell(i, j, cell);
				}
			}
		}

		layers.add(layerGround);
		layers.add(layerWall);

		// create box2d bodies and fixtures for wall tiles
		for (int col = 0; col < layerWall.getWidth(); col++) {
			for (int row = 0; row < layerWall.getHeight(); row++) {
				Cell cell = layerWall.getCell(col, row);

				if (cell != null && cell.getTile() != null) {
					bdef.type = BodyType.StaticBody;
					bdef.position.set((col + 0.5f) * layerWall.getTileWidth() / PPM,
							(row + 0.5f) * layerWall.getTileHeight() / PPM);

					shape.setAsBox(layerWall.getTileWidth() / PPM / 2, layerWall.getTileHeight() / PPM / 2);
					fdef.shape = shape;
					fdef.filter.categoryBits = B2DVars.BIT_WALL;
					fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_BULLET | B2DVars.BIT_ENEMY;
					fdef.isSensor = false;

					world.createBody(bdef).createFixture(fdef).setUserData("wall");
				}
			}
		}
	}

	private void setNight(boolean night) {
		isNight = night;
	}

	private void createBullet(float angle) {
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		CircleShape cshape = new CircleShape();
		Body body;
		Bullet bullet;

		cshape.setRadius(4 / PPM);

		bdef.type = BodyType.DynamicBody;
		bdef.position.set(player.getPosition().x, player.getPosition().y);

		fdef.shape = cshape;
		fdef.isSensor = true;
		fdef.filter.categoryBits = B2DVars.BIT_BULLET;
		fdef.filter.maskBits = B2DVars.BIT_WALL | B2DVars.BIT_ENEMY;

		body = world.createBody(bdef);
		body.createFixture(fdef).setUserData("bullet");

		bullet = new Bullet(body, "bullet", angle);
		bullets.add(bullet);

		body.setUserData(bullet);
	}

	private void createBloodstain(Vector2 position) {
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		bdef.type = BodyType.StaticBody;
		bdef.position.set(position.x, position.y);
		Body body = world.createBody(bdef);

		// create fixture and shape for bloodstain's body
		shape.setAsBox(16 / PPM, 16 / PPM);
		fdef.shape = shape;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("bloodstain");

		// create new bloodstain
		Bloodstain blood = new Bloodstain(body);
		bloodstains.add(blood);
	}

	private void createParticles(Vector2 position, String effect) {
		ParticleEffect particle = new ParticleEffect();

		particle.load(Gdx.files.internal(effect), Gdx.files.internal("res/images/"));
		particle.setPosition(position.x, position.y);
		particle.start();

		particles.add(particle);
	}
}