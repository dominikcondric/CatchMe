package ecs.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

public class GuiComponent implements Component, Disposable {
	public static boolean stageModified = true;
	
	private Actor guiElement;
	
	public GuiComponent(Actor actor) {
		guiElement = actor;
		stageModified = true;
	}

	public Actor getGuiElement() {
		return guiElement;
	}

	@Override
	public void dispose() {
		guiElement.remove();
		disposeImage(guiElement);
	}
	
	private void disposeImage(Actor actor) {
		
		if (actor instanceof Image) {
			Image image = (Image) actor;
			if (image.getDrawable() != null)
				((TextureRegionDrawable) image.getDrawable()).getRegion().getTexture().dispose();
		} else if (actor instanceof Group) {
			for (Actor a : ((Group) guiElement).getChildren()) {
				disposeImage(a);
			}
		} else if (actor instanceof Table) {
			for (Actor a : ((Table) guiElement).getChildren()) {
				disposeImage(a);
			}
		}

	}
}
