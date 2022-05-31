package ecs.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.BodyType;
import ecs.components.PhysicsComponent.Fixture;

public class Obstacle extends Entity {

	public Obstacle(ComponentDatabase componentDB, RectangleMapObject mapObject) {
		super(componentDB);
		createComponents(mapObject);
	}
	
	protected void createComponents(RectangleMapObject mapObject) {
		Rectangle boundingRect = mapObject.getRectangle();
		boundingRect.set(boundingRect.x / 32.f, boundingRect.y / 32.f, boundingRect.width / 32.f, boundingRect.height / 32.f);
		PhysicsComponent physicsComp = new PhysicsComponent(new Vector2(boundingRect.getCenter(new Vector2())), null);
		Fixture fixture = physicsComp.addFixture(new Vector2(-boundingRect.width / 2.f, -boundingRect.height / 2.f), new Vector2(boundingRect.width, boundingRect.height), BodyType.Static, false);
		fixture.collisionInitiationFlags = PhysicsComponent.PLAYER_FLAG;
		fixture.collisionResponseFlags = PhysicsComponent.OBSTACLE_FLAG;
		addComponent(physicsComp);
	}

	@Override
	public void update(float deltaTime) {
	}
}
