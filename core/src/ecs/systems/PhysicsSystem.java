package ecs.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import ecs.components.CollisionCallback;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.Fixture;
import utility.ImmutableArray;
import utility.Pair;
import utility.QuadTree;
import utility.QuadTree.Node;

public class PhysicsSystem {
	private QuadTree quadTree;
	
	public PhysicsSystem() {
		quadTree = new QuadTree(new Vector2(0.f, 0.f), Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
	public void resolveCollisions(ImmutableArray<PhysicsComponent> physicsComponents, float deltaTime, float maxWidth, float maxHeight) {
		for (PhysicsComponent physicsComp : physicsComponents) {
			quadTree.addComponent(physicsComp);
			Vector2 worldPosition = physicsComp.getWorldPosition();
			worldPosition.mulAdd(physicsComp.getMovingDirection(), physicsComp.getMoveMultiplier() * deltaTime);
			physicsComp.setWorldPosition(worldPosition);
		}
		
		for (Node node : quadTree) {
			Array<Fixture> fixtures = new Array<>(); 
			quadTree.findAllSiblingsAndChildrenElements(fixtures, node);
			for (int i = 0; i < node.getElementCount(); ++i) {
				for (int j = i+1; j < fixtures.size; ++j) {
					Fixture c1 = fixtures.get(i);
					Fixture c2 = fixtures.get(j);
					if ((c1.collisionInitiationFlags & c2.collisionResponseFlags) != 0
							&& (c2.collisionInitiationFlags & c1.collisionResponseFlags) != 0) {
						Rectangle c1Rect = c1.getBoundingRectangle();
						Rectangle c2Rect = c2.getBoundingRectangle();
						if (c1Rect.overlaps(c2Rect)) {
							if (!c1.isSensor && !c2.isSensor) {
								Vector2 direction = c2Rect.getCenter(new Vector2()).sub(c1Rect.getCenter(new Vector2())).nor();
								float diffX, diffY = 0f;
								
								if (direction.x > 0.f) {
									diffX = c1Rect.x + c1Rect.width - c2Rect.x;
								} else {
									diffX = c2Rect.x + c2Rect.width - c1Rect.x;
								}
								
								if (direction.y > 0.f) {
									diffY = c1Rect.y + c1Rect.height - c2Rect.y;
								} else {
									diffY = c2Rect.y + c2Rect.height - c1Rect.y;
								}
								
								Vector2 c1WorldPosition = c1.getPhysicsComponent().getWorldPosition();
								Vector2 c2WorldPosition = c2.getPhysicsComponent().getWorldPosition();
								if (c1.getType() == PhysicsComponent.BodyType.Dynamic) {
									if (diffX < diffY) {
										if (direction.x > 0f)
											c1WorldPosition.x -= diffX;
										else 
											c1WorldPosition.x += diffX;
									} else {
										if (direction.y > 0f)
											c1WorldPosition.y -= diffY;
										else 
											c1WorldPosition.y += diffY;
									}
								}
								
								if (c2.getType() == PhysicsComponent.BodyType.Dynamic) {
									if (diffX < diffY) {
										if (direction.x > 0f)
											c2WorldPosition.x += diffX;
										else 
											c2WorldPosition.x -= diffX;
									} else {
										if (direction.y > 0f)
											c2WorldPosition.y += diffY;
										else 
											c2WorldPosition.y -= diffY;
									}
								}
								
								c1.getPhysicsComponent().setWorldPosition(c1WorldPosition);
								c2.getPhysicsComponent().setWorldPosition(c2WorldPosition);
							}
							
							CollisionCallback c1Callback = c1.getPhysicsComponent().getCollisionCallback();
							CollisionCallback c2Callback = c2.getPhysicsComponent().getCollisionCallback();
							if (c1Callback != null) {
								c1Callback.onCollision(c1);
							}
							
							if (c2Callback != null) {
								c2Callback.onCollision(c1);
							}
						}
					}
				}
			}
		}

		quadTree.clear();
	}
}
