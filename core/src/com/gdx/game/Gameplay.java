package com.gdx.game;

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

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.AnimationComponent;
import ecs.components.EventComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.SpriteComponent;
import ecs.entities.Item;
import ecs.entities.Obstacle;
import ecs.entities.Player;
import ecs.systems.EventSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderingSystem;
import utility.ImmutableArray;

public class Gameplay {
	private ComponentDatabase componentDatabase;
	private Array<Obstacle> obstacles;
	private Array<Item> items;
	private Array<Vector2> availableItemPositions;
	private Player player;
	private TiledMap map;
	private OrthographicCamera camera;
	private final int mapWidth, mapHeight;
	private float timeToNextPowerUp = 0f;
	private static final float POWERUP_RESPAWN_TIME = 60f;
//	private Stage ui;
	
	public Gameplay(ComponentDatabase componentDatabase, String mapFilePath) {
		this.componentDatabase = componentDatabase;
		obstacles = new Array<Obstacle>();
		items = new Array<Item>();
		availableItemPositions = new Array<Vector2>();
		player = new Player(componentDatabase, true);
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
					availableItemPositions.add(new Vector2(x / 32.f, y / 32.f));
				}
			}
		}
		
		camera = new OrthographicCamera();
		final float aspectRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		camera.setToOrtho(false, aspectRatio * 15.f, 15.f);
		camera.position.x = player.getComponent(AnimationComponent.class).getCurrentSprite().getOriginX();
		camera.position.y = player.getComponent(AnimationComponent.class).getCurrentSprite().getOriginY();
		camera.update();
	}
	
	public void update(EventSystem eventSystem, float deltaTime) {
		eventSystem.checkEvents(componentDatabase.getComponentArray(EventComponent.class));
		destroyItems();
		player.update(deltaTime);
		for (Item item : items) 
			item.update(deltaTime);
		
		updateCamera();
		
		for (AnimationComponent animationComp : componentDatabase.getComponentArray(AnimationComponent.class)) {
			animationComp.updateAnimation(deltaTime);
		}
		
		generateRandomPowerUp(deltaTime);
	}
	
	private void destroyItems() {
		Array<Integer> itemsToDestroy = new Array<>();
		for (int i = 0; i < items.size; ++i) {
			if (items.get(i).shouldDestroy()) {
				itemsToDestroy.add(i);
			}
		}
		
		for (Integer i : itemsToDestroy) {
			items.removeIndex(i);
		}

	}
	
	private void generateRandomPowerUp(float deltaTime) {
		timeToNextPowerUp -= deltaTime;
		if (timeToNextPowerUp < 0f) {
			Vector2 randomPosition = availableItemPositions.get(new Random().nextInt(availableItemPositions.size));
			items.add(new Item(componentDatabase, PowerUp.getRandomPowerUp(), randomPosition.x, randomPosition.y));
			timeToNextPowerUp = POWERUP_RESPAWN_TIME;
		}
	}
	
	private void updateCamera() {
		Sprite playerSprite = player.getComponent(AnimationComponent.class).getCurrentSprite();
		camera.position.x = playerSprite.getX() + playerSprite.getWidth();
		camera.position.y = playerSprite.getY() + playerSprite.getHeight();
		
		camera.position.x = Math.max(camera.viewportWidth / 2.f, Math.min(mapWidth - camera.viewportWidth / 2.f, camera.position.x));
		camera.position.y = Math.max(camera.viewportHeight / 2.f, Math.min(mapHeight - camera.viewportHeight / 2.f, camera.position.y));
		camera.update();
	}
	
	public void draw(RenderingSystem renderingSystem) {
		renderingSystem.renderEntities(componentDatabase.getComponentArray(SpriteComponent.class),
				componentDatabase.getComponentArray(AnimationComponent.class), componentDatabase.getComponentArray(LightComponent.class), map, camera);
		renderingSystem.renderColliders(componentDatabase.getComponentArray(PhysicsComponent.class), camera);
	}
	
	public void checkCollisions(PhysicsSystem physicsSystem, float deltaTime) {
		ImmutableArray<PhysicsComponent> physicsComponents = componentDatabase.getComponentArray(PhysicsComponent.class);
		physicsSystem.resolveCollisions(physicsComponents, deltaTime, 100f, 100.f);
	}
}
