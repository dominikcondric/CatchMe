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
		switch (direction) {
			case UP:
				physicsComp.setMovingDirection(new Vector2(0.f, 1.f));
				break;
			case DOWN:
				physicsComp.setMovingDirection(new Vector2(0.f, -1.f));
				break;
			case LEFT:
				physicsComp.setMovingDirection(new Vector2(-1.f, 0f));
				break;
			case RIGHT:
				physicsComp.setMovingDirection(new Vector2(1.f, 0f));
				break;
		}
	}
}
