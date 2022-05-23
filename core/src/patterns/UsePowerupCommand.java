package patterns;

import ecs.Entity;
import ecs.components.Event;
import ecs.components.EventComponent;

public class UsePowerupCommand implements Command {

	@Override
	public void execute(Entity entity, float deltaTime) {
		EventComponent eventComponent = entity.getComponent(EventComponent.class);
		eventComponent.publishedEvents.add(new Event("OpenChest", null));
	}
}
