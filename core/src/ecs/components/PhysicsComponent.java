package ecs.components;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PhysicsComponent implements Component {
	private CollisionCallback collisionCallback;
	private Array<Fixture> fixtureList;
	private Vector2 worldPosition;
	private Vector2 movingDirection;
	private float moveMultiplier;
	
	public class Fixture {
		private Vector2 position;
		private Vector2 size;
		private BodyType type;
		public boolean isSensor = false;
		public int collisionInitiationFlags = 0;
		public int collisionResponseFlags = 0;
		
		public Fixture(Vector2 relativePosition, Vector2 size, BodyType type, boolean sensor) {
			position = relativePosition;
			this.size = size;
			this.type = type;
			isSensor = sensor;
		}
		
		public Rectangle getBoundingRectangle() {
			return new Rectangle(worldPosition.x + position.x, worldPosition.y + position.y, size.x, size.y);
		}
		
		public BodyType getType() {
			return type;
		}
		
		public PhysicsComponent getPhysicsComponent() {
			return PhysicsComponent.this;
		}
		
		public void setPosition(Vector2 position) {
			this.position = position;
		}
	}
	
	public enum BodyType {
		Static, Dynamic
	}
	
	public static final int 
		PLAYER_FLAG = 1 << 0,
		OBSTACLE_FLAG = 1 << 1,
		ITEM_FLAG = 1 << 2;
	
	public PhysicsComponent(Vector2 worldPosition, CollisionCallback collisionCallback) {
		this(worldPosition, collisionCallback, new Vector2(0.f, 0.f), 0.f);
	}
	
	public PhysicsComponent(Vector2 worldPosition, CollisionCallback collisionCallback, Vector2 movingDirection, float moveMultiplier) {
		fixtureList = new Array<>();
		this.worldPosition = worldPosition;
		this.collisionCallback = collisionCallback;
		this.movingDirection = movingDirection;
		this.moveMultiplier = moveMultiplier;
	}
	
	public Vector2 getMovingDirection() {
		return new Vector2(movingDirection);
	}

	public void setMovingDirection(Vector2 movingDirection) {
		this.movingDirection = movingDirection;
	}

	public float getMoveMultiplier() {
		return moveMultiplier;
	}

	public void setMoveMultiplier(float moveMultiplier) {
		this.moveMultiplier = moveMultiplier;
	}

	public Fixture addFixture(Vector2 relativePosition, Vector2 size, BodyType type, boolean isSensor) {
		Fixture fixture = new Fixture(new Vector2(relativePosition), new Vector2(size), type, isSensor);
		fixtureList.add(fixture);
		return fixture;
	}
	
	public void removeFixture(Fixture fixture) {
		fixtureList.removeValue(fixture, true);
	}
	
	public Array<Fixture> getFixtureList() {
		return fixtureList;
	}
	
	public CollisionCallback getCollisionCallback() {
		return collisionCallback;
	}

	public Vector2 getWorldPosition() {
		return worldPosition;
	}

	public void setWorldPosition(Vector2 worldPosition) {
		this.worldPosition = worldPosition;
	}
}
