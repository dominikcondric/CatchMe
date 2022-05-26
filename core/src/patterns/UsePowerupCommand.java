package patterns;

import ecs.entities.Player;
import ecs.Entity;

public class UsePowerupCommand implements Command {

	@Override
	public void execute(Entity entity, float deltaTime) {
		((Player)entity).getPowerUp().use((Player)entity);
	}
}
