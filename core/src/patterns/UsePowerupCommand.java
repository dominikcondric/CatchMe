package patterns;

import ecs.entities.Player;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ecs.Entity;
import ecs.components.GuiComponent;

public class UsePowerupCommand implements Command {

	@Override
	public void execute(Entity entity, float deltaTime) {
		((Player)entity).getPowerUp().use((Player)entity);
		((Image)((Group) entity.getComponent(GuiComponent.class).getGuiElement()).getChild(1)).setDrawable(null);
	}
}
