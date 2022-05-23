package ecs.components;

import ecs.components.PhysicsComponent.Fixture;

public interface CollisionCallback {
	public void onCollision(Fixture fixture);
}
