package patterns;

import ecs.Entity;

public interface Command {
	public abstract void execute(Entity entity, float deltaTime);
}
