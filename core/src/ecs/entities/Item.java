package ecs.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.PowerUp;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.CollisionCallback;
import ecs.components.Event;
import ecs.components.EventComponent;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.BodyType;
import ecs.components.PhysicsComponent.Fixture;
import patterns.EventCallback;
import ecs.components.SpriteComponent;

public class Item extends Entity {
	private PowerUp powerup;
	private int hoverDirection = 1;
	private float hoverMultiplier = .5f;
	private float hoverDistance = 0.f;
	
	public Item(ComponentDatabase componentDB, PowerUp powerup, float positionX, float positionY) {
		super(componentDB);
		this.powerup = powerup;
		
		SpriteComponent spriteComponent = new SpriteComponent(new Sprite(powerup.textureRegion));
		spriteComponent.getSprite().setPosition(positionX + 0.125f, positionY + 0.125f);
		spriteComponent.getSprite().setSize(0.75f, 0.75f);
		addComponent(spriteComponent);
		
		PhysicsComponent physicsComp = new PhysicsComponent(new Vector2(positionX + 0.5f, positionY + 0.5f), new CollisionCallback() {
			
			@Override
			public void onCollision(Fixture other) {
				EventComponent eventComponent = getComponent(EventComponent.class);
				eventComponent.publishedEvents.add(new Event("PickItem", Item.this));
				Item.this.destroy = true;
			}
		});
		
		Fixture fixture = physicsComp.addFixture(new Vector2(-0.375f, -0.375f), new Vector2(.75f, .75f), BodyType.Static, true);
		fixture.collisionInitiationFlags = PhysicsComponent.PLAYER_FLAG;
		fixture.collisionResponseFlags = PhysicsComponent.ITEM_FLAG;
		
		
		addComponent(physicsComp);
		
		EventComponent eventComponent = new EventComponent(new EventCallback() {
			
			@Override
			public void onMyEventObserved(Event event) {
				EventComponent eventComponent = getComponent(EventComponent.class);
				eventComponent.publishedEvents.removeValue(event, false);
			}
			
			@Override
			public void onEventObserved(Event event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
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
	}
	
	
	
}
