package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.game.powerups.LightPowerUp;
import com.gdx.game.powerups.PowerUp;
import com.gdx.game.powerups.SpeedBootsPowerUp;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.AnimationComponent;
import ecs.components.EventComponent;
import ecs.components.GuiComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.SoundComponent;
import ecs.components.SpriteComponent;
import ecs.entities.TimeCounter;
import ecs.entities.Item;
import ecs.entities.Obstacle;
import ecs.entities.Player;
import ecs.systems.AudioSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;
import patterns.commands.CatchCommand;
import patterns.commands.UsePowerupCommand;
import patterns.commands.WalkCommand;
import utility.CommandMapper;
import utility.ImmutableArray;
import utility.ObjectPool;
import utility.ObjectPool.PoolElementFactory;
import utility.Toolbox;

public class GameplayPhase extends GamePhase {
	private ComponentDatabase componentDatabase;
	private Array<Obstacle> obstacles;
	private ObjectPool<Item> items;
	private Array<Vector2> availableFreePositions;
	private Player player1;
	private Player player2;
	private TimeCounter timeCounter;
	private Music gameplayMusic;
	private TiledMap map;
	private final int mapWidth, mapHeight;
	private float timeToNextPowerUp = 0f;
	private static final float POWERUP_RESPAWN_TIME = 10f;
	
	public GameplayPhase(String player1TexPath, String player2TexPath, float matchLength) {
		this.componentDatabase = new ComponentDatabase();
		obstacles = new Array<Obstacle>();
		
		timeCounter = new TimeCounter(componentDatabase, matchLength);
		map = new TmxMapLoader().load("Maps//Map.tmx");
		availableFreePositions = new Array<>();
		
		gameplayMusic = Gdx.audio.newMusic(Gdx.files.internal("Red Curtain.ogg"));
		gameplayMusic.setLooping(true);
		gameplayMusic.setVolume(0.2f);
		mapWidth = map.getProperties().get("width", Integer.class);
		mapHeight = map.getProperties().get("height", Integer.class);
		
		for (RectangleMapObject mapObject : map.getLayers().get("Objects").getObjects().getByType(RectangleMapObject.class)) {
			obstacles.add(new Obstacle(componentDatabase, mapObject));
		}
		
		for (RectangleMapObject mapObject : map.getLayers().get("ItemLocations").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = mapObject.getRectangle();
			for (float x = rectangle.getX(); x < rectangle.x + rectangle.width; x += 32f) {
				for (float y = rectangle.getY(); y < rectangle.y + rectangle.height; y += 32f) {
					availableFreePositions.add(new Vector2(x / 32.f, y / 32.f));
				}
			}
		}
		
		items = new ObjectPool<Item>(new PoolElementFactory<Item>() {
			int counter = 0; 
			
			@Override
			public Item generateElement() {
				Vector2 randomPosition = availableFreePositions.random();
				
				PowerUp powerUp = null;
				if (counter < 5)
					powerUp = new LightPowerUp();
				else if (counter < 10) 
					powerUp = new SpeedBootsPowerUp();
				
				counter++;
				return new Item(componentDatabase, powerUp, randomPosition.x, randomPosition.y);
			}
		}, 10);
		
		player1 = new Player(componentDatabase, true, availableFreePositions.random(), player1TexPath);
		player2 = new Player(componentDatabase, false, availableFreePositions.random(), player2TexPath);
	}
	
	public void setCommands(CommandMapper commandMapper) {
		commandMapper.addCommand(Keys.D, new WalkCommand(player1, WalkCommand.Directions.RIGHT, false)); 
		commandMapper.addCommand(Keys.A, new WalkCommand(player1, WalkCommand.Directions.LEFT, false)); 
		commandMapper.addCommand(Keys.W, new WalkCommand(player1, WalkCommand.Directions.UP, false)); 
		commandMapper.addCommand(Keys.S, new WalkCommand(player1, WalkCommand.Directions.DOWN, false));
		commandMapper.addCommand(Keys.SPACE, new CatchCommand(player1, true));
		commandMapper.addCommand(Keys.SHIFT_LEFT, new UsePowerupCommand(player1, true));
		
		commandMapper.addCommand(Keys.RIGHT, new WalkCommand(player2, WalkCommand.Directions.RIGHT, false)); 
		commandMapper.addCommand(Keys.LEFT, new WalkCommand(player2, WalkCommand.Directions.LEFT, false)); 
		commandMapper.addCommand(Keys.UP, new WalkCommand(player2, WalkCommand.Directions.UP, false)); 
		commandMapper.addCommand(Keys.DOWN, new WalkCommand(player2, WalkCommand.Directions.DOWN, false));
		commandMapper.addCommand(Keys.ENTER, new CatchCommand(player2, true));
		commandMapper.addCommand(Keys.SHIFT_RIGHT, new UsePowerupCommand(player2, true));
	}
	
	@Override
	public void run(Toolbox toolbox, Viewport viewport, float deltaTime) {
		checkCollisions(toolbox.getPhysicsSystem(), deltaTime);
		toolbox.getEventSystem().checkEvents(componentDatabase.getComponentArray(EventComponent.class));
		playAudio(toolbox.getAudioSystem());
		draw(toolbox.getRenderingSystem(), map, viewport);
	}
	
	@Override
	public void update(float deltaTime) {
		// Update players
		player1.update(deltaTime);
		player2.update(deltaTime);
		
		// Update items
		updateItems(deltaTime);
		
		// Update animations
		for (AnimationComponent animationComp : componentDatabase.getComponentArray(AnimationComponent.class)) {
			animationComp.updateAnimation(deltaTime);
		}
		
		spawnRandomPowerUp(deltaTime);
		
		timeCounter.update(deltaTime);
	}
	
	private void updateItems(float deltaTime) {
		Array<Item> itemsToDestroy = new Array<>();
		for (Item item : items) {
			item.update(deltaTime);
			if (item.shouldDestroy()) {
				itemsToDestroy.add(item);
			}
		}
		
		for (Item item : itemsToDestroy) {
			items.removeElement(item);
		}
	}
	
	private void spawnRandomPowerUp(float deltaTime) {
		timeToNextPowerUp -= deltaTime;
		if (timeToNextPowerUp < 0f) {
			Item itemToSpawn = items.useNewElement(true);
			itemToSpawn.getInitialPosition().set(availableFreePositions.random());
			timeToNextPowerUp = POWERUP_RESPAWN_TIME;
		}
	}
	
	private void updateCameraPosition(Entity entityToFollow, OrthographicCamera camera) {
		Vector2 entityPosition = entityToFollow.getComponent(PhysicsComponent.class).getWorldPosition();
		camera.position.set(entityPosition, camera.position.z);
		
		camera.position.x = Math.max(camera.viewportWidth / 2.f, Math.min(mapWidth - camera.viewportWidth / 2.f, camera.position.x));
		camera.position.y = Math.max(camera.viewportHeight / 2.f, Math.min(mapHeight - camera.viewportHeight / 2.f, camera.position.y));
		camera.update();
	}
	
	public void draw(RenderingSystem renderingSystem, TiledMap map, Viewport viewport) {
		// Player1
		OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();
		renderingSystem.clearScreen();
		viewport.setScreenX(0);
		viewport.apply();
		LightComponent player1LightComp = player1.getComponent(LightComponent.class);
		LightComponent player2LightComp = player2.getComponent(LightComponent.class);
		float player1LightRadius = player1LightComp.getRadius();
		float player2LightRadius = player2LightComp.getRadius();
		player2LightComp.setRadius(0.f);
		updateCameraPosition(player1, camera);
		renderingSystem.renderMap(map, camera);
		renderingSystem.renderEntities(componentDatabase.getComponentArray(SpriteComponent.class),
					componentDatabase.getComponentArray(AnimationComponent.class), camera);
		renderingSystem.renderLights(componentDatabase.getComponentArray(LightComponent.class), camera, mapWidth, mapHeight);
		
//		 Player2
		player1LightComp.setRadius(0.f);
		player2LightComp.setRadius(player2LightRadius);
		updateCameraPosition(player2, camera);
		viewport.setScreenX(viewport.getScreenWidth());
		viewport.apply();
		renderingSystem.renderMap(map, camera);
		renderingSystem.renderEntities(componentDatabase.getComponentArray(SpriteComponent.class),
				componentDatabase.getComponentArray(AnimationComponent.class), camera);
		renderingSystem.renderLights(componentDatabase.getComponentArray(LightComponent.class), camera, mapWidth, mapHeight);
		player1LightComp.setRadius(player1LightRadius);
		
		// Gui rendering
		renderingSystem.renderGUI(componentDatabase.getComponentArray(GuiComponent.class));
	}
	
	public void checkCollisions(PhysicsSystem physicsSystem, float deltaTime) {
		ImmutableArray<PhysicsComponent> physicsComponents = componentDatabase.getComponentArray(PhysicsComponent.class);
		physicsSystem.resolveCollisions(physicsComponents, deltaTime, 100f, 100.f);
	}
	
	public void playAudio(AudioSystem audioSystem) {
		audioSystem.playAudio(componentDatabase.getComponentArray(SoundComponent.class), gameplayMusic);
	}

	@Override
	public void dispose() {
		gameplayMusic.dispose();
		componentDatabase.dispose();
	}

	@Override
	public boolean isFinished() {
		return timeCounter.isTimeUp() && (!player1.isCatching() || !player2.isCatching());
	}

	@Override
	public GamePhase getNewGamePhase() {
		TextureRegion texRegion = new TextureRegion();
		AnimationComponent player1AnimationComponent = player1.getComponent(AnimationComponent.class);
		AnimationComponent player2AnimationComponent = player2.getComponent(AnimationComponent.class);
		player1AnimationComponent.setActiveAnimation("IdleDown");
		player1AnimationComponent.updateAnimation(0f);
		player2AnimationComponent.setActiveAnimation("IdleDown");
		player2AnimationComponent.updateAnimation(0f);
				
		Sprite player1Sprite = player1AnimationComponent.getCurrentSprite();
		Sprite player2Sprite = player2AnimationComponent.getCurrentSprite();
		
		if (player1.isCatching()) {
			texRegion.setTexture(new Texture(player2Sprite.getTexture().getTextureData()));
			texRegion.setRegion((int)player2Sprite.getRegionX(), (int)player2Sprite.getRegionY(), (int)player2Sprite.getRegionWidth(), (int)player2Sprite.getRegionHeight());
			return new EndPhase(texRegion, player2.getPlayerName());
		} else {
			texRegion.setTexture(new Texture(player1Sprite.getTexture().getTextureData()));
			texRegion.setRegion((int)player1Sprite.getRegionX(), (int)player1Sprite.getRegionY(), (int)player1Sprite.getRegionWidth(), (int)player1Sprite.getRegionHeight());
			return new EndPhase(texRegion, player1.getPlayerName());
		}
	}
}
