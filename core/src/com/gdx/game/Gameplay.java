package com.gdx.game;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
import ecs.components.SpriteComponent;
import ecs.entities.Counter;
import ecs.entities.Item;
import ecs.entities.Obstacle;
import ecs.entities.Player;
import ecs.systems.EventSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;
import utility.ImmutableArray;
import utility.Pair;

public class Gameplay {
	private ComponentDatabase componentDatabase;
	private Array<Obstacle> obstacles;
	private Array<Item> items;
	private Array<Pair<Vector2, Boolean>> availableItemPositions;
	private Player player1;
	private Player player2;
	private Counter timeCounter;
	private TiledMap map;
	private OrthographicCamera camera;
	private FitViewport viewport;
	private final int mapWidth, mapHeight;
	private float timeToNextPowerUp = 0f;
	private static final float POWERUP_RESPAWN_TIME = 40f;
	private float matchLength; 
	
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	public Gameplay(ComponentDatabase componentDatabase, String mapFilePath, float matchLength) {
		this.componentDatabase = componentDatabase;
		this.matchLength = matchLength;
		obstacles = new Array<Obstacle>();
		items = new Array<Item>();
		availableItemPositions = new Array<>();
		player1 = new Player(componentDatabase, true);
		player2 = new Player(componentDatabase, false);
		timeCounter = new Counter(componentDatabase, matchLength);
		map = new TmxMapLoader().load(mapFilePath);
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
		
		camera = new OrthographicCamera();
		final float aspectRatio = Gdx.graphics.getWidth() / 2.f / Gdx.graphics.getHeight();
		viewport = new FitViewport(aspectRatio * 15.f, 15.f, camera);
		viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
		
		camera.position.x = player1.getComponent(AnimationComponent.class).getCurrentSprite().getOriginX();
		camera.position.y = player1.getComponent(AnimationComponent.class).getCurrentSprite().getOriginY();
		camera.update();
	}
	
	public void update(EventSystem eventSystem, float deltaTime) {
		// Check events
		eventSystem.checkEvents(componentDatabase.getComponentArray(EventComponent.class));
		
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
	
	private void updateCameraPosition(Entity entityToFollow) {
		Vector2 entityPosition = entityToFollow.getComponent(PhysicsComponent.class).getWorldPosition();
		camera.position.set(entityPosition, camera.position.z);
		
		camera.position.x = Math.max(camera.viewportWidth / 2.f, Math.min(mapWidth - camera.viewportWidth / 2.f, camera.position.x));
		camera.position.y = Math.max(camera.viewportHeight / 2.f, Math.min(mapHeight - camera.viewportHeight / 2.f, camera.position.y));
		camera.update();
	}
	
	public void draw(RenderingSystem renderingSystem) {
		// Entity rendering
		renderingSystem.clearScreen();
		viewport.setScreenX(0);
		viewport.apply();
		LightComponent player1LightComp = player1.getComponent(LightComponent.class);
		LightComponent player2LightComp = player2.getComponent(LightComponent.class);
		float player1LightRadius = player1LightComp.getRadius();
		float player2LightRadius = player2LightComp.getRadius();
		player2LightComp.setRadius(0.f);
		updateCameraPosition(player1);
		renderingSystem.renderEntities(componentDatabase.getComponentArray(SpriteComponent.class),
					componentDatabase.getComponentArray(AnimationComponent.class), componentDatabase.getComponentArray(LightComponent.class), map, camera);
		player1LightComp.setRadius(0.f);
		player2LightComp.setRadius(player2LightRadius);
		updateCameraPosition(player2);
		viewport.setScreenX(viewport.getScreenWidth());
		viewport.apply();
		renderingSystem.renderEntities(componentDatabase.getComponentArray(SpriteComponent.class),
				componentDatabase.getComponentArray(AnimationComponent.class), componentDatabase.getComponentArray(LightComponent.class), map, camera);
		player1LightComp.setRadius(player1LightRadius);
		
		// Gui rendering
		renderingSystem.renderGUI(componentDatabase.getComponentArray(GuiComponent.class));
	}
	
	public void checkCollisions(PhysicsSystem physicsSystem, float deltaTime) {
		ImmutableArray<PhysicsComponent> physicsComponents = componentDatabase.getComponentArray(PhysicsComponent.class);
		physicsSystem.resolveCollisions(physicsComponents, deltaTime, 100f, 100.f);
	}
	
	public void onScreenResize(int width, int height) {
		final float aspectRatio = width / 2.f / height;
		viewport.setWorldSize(aspectRatio * 15.f, 15.f);
		viewport.setScreenBounds(0, 0, width / 2, height);
	}
}
