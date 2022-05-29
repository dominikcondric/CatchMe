package com.gdx.game;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import utility.Pair;
import utility.Toolbox;

public class GameplayPhase extends GamePhase {
	private ComponentDatabase componentDatabase;
	private Array<Obstacle> obstacles;
	private Array<Item> items;
	private Array<Pair<Vector2, Boolean>> availableItemPositions;
	private Player player1;
	private Player player2;
	private TimeCounter timeCounter;
	private Music gameplayMusic;
	private TiledMap map;
	private final int mapWidth, mapHeight;
	private float timeToNextPowerUp = 0f;
	private static final float POWERUP_RESPAWN_TIME = 20f;
	
	private enum PowerUpItem {
		SPEED(SpeedBootsPowerUp.class),
		LIGHT(LightPowerUp.class);
		
		private final Class<? extends PowerUp> powerUpClass;
		PowerUpItem(Class<? extends PowerUp> powerUpClass) {
			this.powerUpClass = powerUpClass;
		}
		
		public static final PowerUp getRandomPowerUp() {
			try {
				return PowerUpItem.values()[new Random().nextInt(PowerUpItem.values().length)].powerUpClass.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	public GameplayPhase(String player1TexPath, String player2TexPath, float matchLength) {
		this.componentDatabase = new ComponentDatabase();
		obstacles = new Array<Obstacle>();
		items = new Array<Item>();
		availableItemPositions = new Array<>();
		timeCounter = new TimeCounter(componentDatabase, matchLength);
		map = new TmxMapLoader().load("Maps//Map.tmx");
		
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
					availableItemPositions.add(new Pair<>(new Vector2(x / 32.f, y / 32.f), false));
				}
			}
		}
		
		player1 = new Player(componentDatabase, true, availableItemPositions.random().first, player1TexPath);
		player2 = new Player(componentDatabase, false, availableItemPositions.random().first, player2TexPath);
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
		for (Item item : items) 
			item.update(deltaTime);
		destroyPickedItems();
		
		// Update animations
		for (AnimationComponent animationComp : componentDatabase.getComponentArray(AnimationComponent.class)) {
			animationComp.updateAnimation(deltaTime);
		}
		
		spawnRandomPowerUp(deltaTime);
		
		timeCounter.update(deltaTime);
	}
	
	private void destroyPickedItems() {
		Array<Integer> itemsToDestroy = new Array<>();
		for (int i = 0; i < items.size; ++i) {
			if (items.get(i).shouldDestroy()) {
				itemsToDestroy.add(i);
			}
		}
		
		for (Integer i : itemsToDestroy) {
			availableItemPositions.get(availableItemPositions.indexOf(new Pair<>(items.get(i).getInitialPosition(), true), false)).second = false;
			items.removeIndex(i);
		}

	}
	
	private void spawnRandomPowerUp(float deltaTime) {
		timeToNextPowerUp -= deltaTime;
		if (timeToNextPowerUp < 0f) {
			Vector2 randomPosition;
			while (true) {
				Pair<Vector2, Boolean> pointPair = availableItemPositions.get(new Random().nextInt(availableItemPositions.size));
				if (pointPair.second == false) {
					pointPair.second = true;
					randomPosition = pointPair.first;
					break;
				}
			}
			items.add(new Item(componentDatabase, PowerUpItem.getRandomPowerUp(), randomPosition.x, randomPosition.y));
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
		return false;
	}

	@Override
	public GamePhase getNewGamePhase() {
		return null;
	}
}
