package ecs.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.powerups.PowerUp;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.EventComponent;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.BodyType;
import ecs.components.PhysicsComponent.Fixture;
import patterns.Event;
import patterns.EventCallback;
import utility.Resetable;
import ecs.components.SpriteComponent;

public class Item extends Entity implements Resetable {
	private PowerUp powerup;
	private int hoverDirection = 1;
	private float hoverMultiplier = .5f;
	private float hoverDistance = 0.f;
	private final Vector2 initialPosition;
	private String pickupEventName;
	private float lifetime = ITEM_LIFETIME;
	private static final float ITEM_LIFETIME = 30f;
	
	public Item(ComponentDatabase componentDB, PowerUp powerup, float positionX, float positionY) {
		super(componentDB);
		this.powerup = powerup;
		initialPosition = new Vector2(positionX, positionY);
	}
	
	private void createComponents() {
		TextureRegion powerUpRegion = powerup.textureRegion;
		SpriteComponent spriteComponent = new SpriteComponent(new Sprite(new TextureRegion(new Texture(powerUpRegion.getTexture().getTextureData()), powerUpRegion.getRegionX(), powerUpRegion.getRegionY(), powerUpRegion.getRegionWidth(), powerUpRegion.getRegionHeight())));
		spriteComponent.getSprite().setPosition(initialPosition.x + 0.125f, initialPosition.y + 0.125f);
		spriteComponent.getSprite().setSize(0.75f, 0.75f);
		addComponent(spriteComponent);
		
		PhysicsComponent physicsComp = new PhysicsComponent(new Vector2(initialPosition.x + 0.5f, initialPosition.y + 0.5f), null);
		Fixture fixture = physicsComp.addFixture(new Vector2(-0.375f, -0.375f), new Vector2(.75f, .75f), BodyType.Static, true);
		fixture.collisionInitiationFlags = PhysicsComponent.PLAYER_FLAG;
		fixture.collisionResponseFlags = PhysicsComponent.ITEM_FLAG;
		
		
		addComponent(physicsComp);
		
		EventComponent eventComponent = new EventComponent(new EventCallback() {
			
			@Override
			public void onMyEventObserved(Event event) {
				EventComponent eventComponent = getComponent(EventComponent.class);
				eventComponent.publishedEvents.removeValue(event, false);
				Item.this.destroy = true;
			}
			
			@Override
			public void onEventObserved(Event event) {
				if (event.message.contains("PickPowerUp")) {
					EventComponent eventComponent = getComponent(EventComponent.class);
					eventComponent.publishedEvents.add(new Event("CollectPowerUp", Item.this.powerup));
					eventComponent.observedEvents.removeValue(event.message, false);
				}
			}
		});
		
		
		pickupEventName = "PickPowerUp" + fixture.hashCode();
		eventComponent.observedEvents.add(pickupEventName);
		addComponent(eventComponent);
	}

	@Override
	public void update(float deltaTime) {
		PhysicsComponent physicsComp = getComponent(PhysicsComponent.class);
		hoverDistance += deltaTime * hoverMultiplier;
		if (hoverDistance > 0.3f) { 
			hoverDirection *= -1;
			hoverDistance = 0.f;
		}
		
		physicsComp.setWorldPosition(physicsComp.getWorldPosition().add(0.f, deltaTime * hoverMultiplier * hoverDirection));
		Sprite sprite = getComponent(SpriteComponent.class).getSprite();
		sprite.setPosition(sprite.getX(), physicsComp.getWorldPosition().y - sprite.getHeight() / 2.f);
		
		lifetime -= deltaTime;
		if (lifetime <= 0f) {
			destroy = true;
		}
	}

	public Vector2 getInitialPosition() {
		return initialPosition;
	}

	@Override
	public void reset() {
		lifetime = ITEM_LIFETIME;
		powerup.reset();
		createComponents();
		this.destroy = false;
	}
}
