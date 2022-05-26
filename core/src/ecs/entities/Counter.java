package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.GuiComponent;
import screens.GameScreen;

public class Counter extends Entity {
	private float remainingTime;

	public Counter(ComponentDatabase componentDB, float matchLength) {
		super(componentDB);
		createComponents(matchLength);
		remainingTime = matchLength;
	}
	
	private void createComponents(float matchLength) {
		Label counterLabel = new Label(String.valueOf((int) matchLength), new LabelStyle(GameScreen.font, Color.WHITE));
		counterLabel.setFontScale(3f);
		counterLabel.setAlignment(Align.center);
		counterLabel.setSize(Gdx.graphics.getWidth() / 8.f, Gdx.graphics.getHeight() / 8.f);
		counterLabel.setPosition(Gdx.graphics.getWidth() / 2.f - counterLabel.getWidth() / 2.f, Gdx.graphics.getHeight() - counterLabel.getHeight());
		addComponent(new GuiComponent(counterLabel));
	}
	
	@Override
	public void update(float deltaTime) {
		remainingTime -= deltaTime;
		Label counterLabel = (Label) getComponent(GuiComponent.class).getGuiElement();
		counterLabel.setText((int)remainingTime);
	}

	public float getRemainingTime() {
		return remainingTime;
	}

}
