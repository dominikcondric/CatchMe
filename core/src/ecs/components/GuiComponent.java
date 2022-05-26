package ecs.components;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class GuiComponent implements Component {
	public static boolean stageModified = true;
	
	private Actor guiElement;
	
	public GuiComponent(Actor actor) {
		guiElement = actor;
		stageModified = true;
	}

	public Actor getGuiElement() {
		return guiElement;
	}
}
