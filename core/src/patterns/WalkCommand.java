package patterns;

import com.badlogic.gdx.math.Vector2;

import ecs.Entity;
import ecs.components.PhysicsComponent;
import ecs.components.SoundComponent;
import ecs.components.SoundComponent.SoundEffect;

public class WalkCommand implements Command {
	public enum Directions {
		UP, RIGHT, DOWN, LEFT
	}
	
	private Directions direction;

	public WalkCommand(Directions direction) {
		this.direction = direction;
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
		
		SoundEffect walkSoundEffect = entity.getComponent(SoundComponent.class).getSoundEffect("Footsteps");
		walkSoundEffect.shouldPlay = true;
	}
}
