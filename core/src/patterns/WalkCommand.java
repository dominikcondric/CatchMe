package patterns;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ecs.Entity;
import ecs.components.PhysicsComponent;

public class WalkCommand implements Command {
	public enum Directions {
		UP, RIGHT, DOWN, LEFT
	}
	
	private Directions direction;
	private float multiplier;

	public WalkCommand(Directions direction, float multiplier) {
		this.direction = direction;
		this.multiplier = multiplier;
	}
	
	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	@Override
	public void execute(Entity entity, float deltaTime) {
		PhysicsComponent physicsComp = entity.getComponent(PhysicsComponent.class);
		Vector2 worldPosition = physicsComp.getWorldPosition();
		switch (direction) {
			case UP:
				worldPosition.y += deltaTime * multiplier;
				break;
			case DOWN:
				worldPosition.y -= deltaTime * multiplier;
				break;
			case LEFT:
				worldPosition.x -= deltaTime * multiplier;
				break;
			case RIGHT:
				worldPosition.x += deltaTime * multiplier;
		}
		
		physicsComp.setWorldPosition(worldPosition);
	}
}
