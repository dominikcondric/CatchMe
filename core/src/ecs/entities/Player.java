package ecs.entities;

import java.beans.EventSetDescriptor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.powerups.PowerUp;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.AnimationComponent;
import ecs.components.CollisionCallback;
import ecs.components.EventComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.BodyType;
import ecs.components.PhysicsComponent.Fixture;
import patterns.Event;
import patterns.EventCallback;
import patterns.WalkCommand;
import patterns.WalkCommand.Directions;
import utility.CommandMapper;
import utility.CommandMapper.CommandMap;

public class Player extends Entity {
	private String playerName;
	private boolean moving = false;
	private WalkCommand.Directions lastMovingDirection = null;
	private PowerUp powerUp = null;

	public Player(ComponentDatabase componentDB, boolean player1) {
		super(componentDB);
		if (player1)
			playerName = "P1";
		else
			playerName = "P2";
		
		createComponents();
	}
	
	protected void createComponents() {
		AnimationComponent mcAnimationComp = new AnimationComponent(25.f, 40.f, 1.f, 1.f);
		Array<TextureRegion> mcAnimationSprites = new Array<>(3);
		final Texture characterTexture = new Texture("32_Characters//Males//M_01.png");
		TextureRegion[] charactedTextureRegions = new TextureRegion[12];
		for (int i = 0; i < charactedTextureRegions.length; ++i) {
			charactedTextureRegions[i] = new TextureRegion(characterTexture, i % 4 * 16, 1 + i / 4 * 17, 16, 16);
		}
		
		mcAnimationSprites.add(charactedTextureRegions[0]);
		mcAnimationComp.addAnimation("IdleDown", mcAnimationSprites, 0.f, PlayMode.NORMAL, true);
		mcAnimationSprites.clear();
		mcAnimationSprites.add(charactedTextureRegions[1]);
		mcAnimationComp.addAnimation("IdleRight", mcAnimationSprites, 0.f, PlayMode.NORMAL, false);
		mcAnimationSprites.clear();
		mcAnimationSprites.add(charactedTextureRegions[2]);
		mcAnimationComp.addAnimation("IdleUp", mcAnimationSprites, 0.f, PlayMode.NORMAL, false);
		mcAnimationSprites.clear();
		mcAnimationSprites.add(charactedTextureRegions[3]);
		mcAnimationComp.addAnimation("IdleLeft", mcAnimationSprites, 0.f, PlayMode.NORMAL, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[4]);
		mcAnimationSprites.add(charactedTextureRegions[8]);
		mcAnimationComp.addAnimation("WalkDown", mcAnimationSprites, 0.2f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[5]);
		mcAnimationSprites.add(charactedTextureRegions[1]);
		mcAnimationSprites.add(charactedTextureRegions[9]);
		mcAnimationComp.addAnimation("WalkRight", mcAnimationSprites, 0.1f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[6]);
		mcAnimationSprites.add(charactedTextureRegions[10]);
		mcAnimationComp.addAnimation("WalkUp", mcAnimationSprites, 0.2f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[7]);
		mcAnimationSprites.add(charactedTextureRegions[3]);
		mcAnimationSprites.add(charactedTextureRegions[11]);
		mcAnimationComp.addAnimation("WalkLeft", mcAnimationSprites, 0.1f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		addComponent(mcAnimationComp);
		
		Rectangle boundingRectangle = mcAnimationComp.getCurrentSprite().getBoundingRectangle();
		PhysicsComponent playerPhysicsComp = new PhysicsComponent(boundingRectangle.getCenter(new Vector2()), new CollisionCallback() {
			
			@Override
			public void onCollision(Fixture other) {
				if ((other.collisionResponseFlags & PhysicsComponent.ITEM_FLAG) != 0) {
					if (powerUp == null)
						getComponent(EventComponent.class).publishedEvents.add(new Event("PickPowerUp", null));
				}
			}
			
		}, new Vector2(), 5.f);
		
		Vector2 bodyPosition = new Vector2(-boundingRectangle.width / 4.f, -boundingRectangle.height / 2.f);
		Vector2 bodySize = new Vector2(boundingRectangle.width / 2f, boundingRectangle.height / 4f);
		Fixture bodyFixture = playerPhysicsComp.addFixture(bodyPosition, bodySize, BodyType.Dynamic, false);
		bodyFixture.collisionInitiationFlags = PhysicsComponent.OBSTACLE_FLAG | PhysicsComponent.ITEM_FLAG;
		bodyFixture.collisionResponseFlags = PhysicsComponent.PLAYER_FLAG;
		
		Vector2 sensorPosition = new Vector2(-boundingRectangle.width, -boundingRectangle.height);
		Vector2 sensorSize = new Vector2(boundingRectangle.width * 2.f, boundingRectangle.height * 2.f);
		Fixture sensorFixture = playerPhysicsComp.addFixture(sensorPosition, sensorSize, BodyType.Dynamic, true);
		sensorFixture.collisionInitiationFlags = PhysicsComponent.PLAYER_FLAG;
		sensorFixture.collisionResponseFlags = PhysicsComponent.PLAYER_FLAG;
		
		addComponent(playerPhysicsComp);
		
		Vector2 worldPosition = playerPhysicsComp.getWorldPosition();
		addComponent(new LightComponent(worldPosition.x, worldPosition.y, 2.f, .5f));
		EventComponent ec = new EventComponent(new EventCallback() {
			
			@Override
			public void onEventObserved(Event event) {
				EventComponent eventComp = getComponent(EventComponent.class);
				if (event.message.contentEquals("CollectPowerUp")) {
					PowerUp powerup = (PowerUp) event.data;
					Player.this.powerUp = powerup;
					eventComp.observedEvents.removeValue(event.message, false);
				}
			}

			@Override
			public void onMyEventObserved(Event event) {
				EventComponent eventComp = getComponent(EventComponent.class);
				if (event.message.contentEquals("PickPowerUp")) {
					eventComp.publishedEvents.removeValue(event, false);
					eventComp.observedEvents.add("CollectPowerUp");
				}
			}
		});
		
		ec.observedEvents.add("PickPowerUp");
		addComponent(ec);
	}
	
	public PowerUp getPowerUp() {
		return powerUp;
	}
	
	@Override
	public void update(float deltaTime) {
		CommandMapper commandMapper = CommandMapper.getInstance();
		CommandMap walkRightCommand = commandMapper.getCommandKey(getPlayerName() + "WalkRight");
		CommandMap walkLeftCommand = commandMapper.getCommandKey(getPlayerName() + "WalkLeft");
		CommandMap walkUpCommand = commandMapper.getCommandKey(getPlayerName() + "WalkUp");
		CommandMap walkDownCommand = commandMapper.getCommandKey(getPlayerName() + "WalkDown");
		
		boolean walking = false;
		AnimationComponent animationComponent = getComponent(AnimationComponent.class);
		
		if (Gdx.input.isKeyPressed(walkRightCommand.getKey()) && (lastMovingDirection == Directions.RIGHT || !moving)) {
			walking = true;
			lastMovingDirection = Directions.RIGHT;
			animationComponent.setActiveAnimation("WalkRight");
			walkRightCommand.getCommand().execute(this, deltaTime);
		}
		
		if (Gdx.input.isKeyPressed(walkLeftCommand.getKey()) && (lastMovingDirection == Directions.LEFT || !moving)) {
			walking = true;
			lastMovingDirection = Directions.LEFT;
			animationComponent.setActiveAnimation("WalkLeft");
			walkLeftCommand.getCommand().execute(this, deltaTime);
		}
		
		if (Gdx.input.isKeyPressed(walkUpCommand.getKey()) && (lastMovingDirection == Directions.UP || !moving)) {
			lastMovingDirection = Directions.UP;
			animationComponent.setActiveAnimation("WalkUp");
			walking = true;
			walkUpCommand.getCommand().execute(this, deltaTime);
		}
		
		if (Gdx.input.isKeyPressed(walkDownCommand.getKey()) && (lastMovingDirection == Directions.DOWN || !moving)) {
			lastMovingDirection = Directions.DOWN;
			animationComponent.setActiveAnimation("WalkDown");
			walking = true;
			walkDownCommand.getCommand().execute(this, deltaTime);
		}
		
		if (!walking) {
			animationComponent.setActiveAnimation(animationComponent.getActiveAnimation().replace("Walk", "Idle"));
			getComponent(PhysicsComponent.class).setMovingDirection(new Vector2(0.f, 0.f));
		}
		
		moving = walking;

		Sprite sprite = animationComponent.getCurrentSprite();
		Vector2 worldPosition = getComponent(PhysicsComponent.class).getWorldPosition();
		sprite.setX(worldPosition.x - sprite.getWidth() / 2f);
		sprite.setY(worldPosition.y - sprite.getHeight() / 2f);
		
		LightComponent lightComponent = getComponent(LightComponent.class);
		lightComponent.setPosition(worldPosition);
		
		if (powerUp != null) {
			CommandMap powerUpCommand = commandMapper.getCommandKey(playerName + "Interact");
			if (Gdx.input.isKeyJustPressed(powerUpCommand.getKey())) {
				powerUpCommand.getCommand().execute(this, deltaTime);
			}
			
			powerUp.update(this, deltaTime);
			if (powerUp.isFinished()) {
				powerUp = null;
			}
		}
	}

	public String getPlayerName() {
		return playerName;
	}
}
