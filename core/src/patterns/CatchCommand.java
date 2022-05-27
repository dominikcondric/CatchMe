package patterns;

import ecs.Entity;
import ecs.components.EventComponent;

public class CatchCommand implements Command {
	private String playerName;
	
	public CatchCommand(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public void execute(Entity entity, float deltaTime) {
		EventComponent eventComp = entity.getComponent(EventComponent.class);
		eventComp.publishedEvents.add(new Event("Caught", playerName));
	}

}
