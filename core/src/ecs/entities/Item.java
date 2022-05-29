package ecs.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.powerups.PowerUp;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.CollisionCallback;
import ecs.components.EventComponent;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.BodyType;
import ecs.components.PhysicsComponent.Fixture;
import patterns.Event;
import patterns.EventCallback;
import ecs.components.SpriteComponent;

public class Item extends Entity {
	private PowerUp powerup;
	private int hoverDirection = 1;
	private float hoverMultiplier = .5f;
	private float hoverDistance = 0.f;
	private final Vector2 initialPosition;
	private boolean inCollision = false;
	
	public Item(ComponentDatabase componentDB, PowerUp powerup, float positionX, float positionY) {
		super(componentDB);
		this.powerup = powerup;
		initialPosition = new Vector2(positionX, positionY);
		
		SpriteComponent spriteComponent = new SpriteComponent(new Sprite(powerup.textureRegion));
		spriteComponent.getSprite().setPosition(positionX + 0.125f, positionY + 0.125f);
		spriteComponent.getSprite().setSize(0.75f, 0.75f);
		addComponent(spriteComponent);
		
		PhysicsComponent physicsComp = new PhysicsComponent(new Vector2(positionX + 0.5f, positionY + 0.5f), new CollisionCallback() {
			
			@Override
			public void onCollision(Fixture fixture) {
				inCollision = true;
			}
		});
		
		Fixture fixture = physicsComp.addFixture(new Vector2(-0.375f, -0.375f), new Vector2(.75f, .75f), BodyType.Static, true);
		fixture.collisionInitiationFlags = PhysicsComponent.PLAYER_FLAG;
		fixture.collisionResponseFlags = PhysicsComponent.ITEM_FLAG;
		
		
		addComponent(physicsComp);
		
		EventComponent eventComponent = new EventComponent(new EventCallback() {
			
			@Override
			public void onMyEventObserved(Event event) {
				Item.this.destroy = true;
			}
			
			@Override
			public void onEventObserved(Event event) {
				if (event.message.contentEquals("PickPowerUp") && inCollision) {
					System.out.println("aa");
					EventComponent eventComponent = getComponent(EventComponent.class);
					eventComponent.publishedEvents.add(new Event("CollectPowerUp", Item.this.powerup));
					eventComponent.observedEvents.removeValue(event.message, false);
				}
			}
		});
		
		
		eventComponent.observedEvents.add("PickPowerUp");
		addComponent(eventComponent);
	}

	@Override
	public void update(float deltaTime) {
		inCollision = false;
		PhysicsComponent physicsComp = getComponent(PhysicsComponent.class);
		hoverDistance += deltaTime * hoverMultiplier;
		if (hoverDistance > 0.3f) { 
			hoverDirection *= -1;
			hoverDistance = 0.f;
		}
		
		physicsComp.setWorldPosition(physicsComp.getWorldPosition().add(0.f, deltaTime * hoverMultiplier * hoverDirection));
		Sprite sprite = getComponent(SpriteComponent.class).getSprite();
		sprite.setPosition(sprite.getX(), physicsComp.getWorldPosition().y - sprite.getHeight() / 2.f);
	}

	public Vector2 getInitialPosition() {
		return initialPosition;
	}
}
