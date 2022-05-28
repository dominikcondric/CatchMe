package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.GuiComponent;
import ecs.components.SoundComponent;
import screens.GameScreen;

public class TimeCounter extends Entity {
	private float remainingTime;

	public TimeCounter(ComponentDatabase componentDB, float matchLength) {
		super(componentDB);
		createComponents();
		remainingTime = matchLength;
	}
	
	private void createComponents() {
		LabelStyle labelStyle = new LabelStyle(GameScreen.font, Color.WHITE);
		Label counterLabel = new Label(String.valueOf((int) remainingTime), labelStyle);
		counterLabel.setFontScale(3f);
		counterLabel.setAlignment(Align.center);
		counterLabel.setSize(Gdx.graphics.getWidth() / 8.f, Gdx.graphics.getHeight() / 8.f);
		counterLabel.setPosition(Gdx.graphics.getWidth() / 2.f - counterLabel.getWidth() / 2.f, Gdx.graphics.getHeight() - counterLabel.getHeight());
		addComponent(new GuiComponent(counterLabel));
		
//		SoundComponent soundComp = new SoundComponent();
//		soundComp.addSound("TimeUp", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Jingle_Win_01.mp3"), false, false);
//		soundComp.addSound("GameOver", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Climb_Rope_Loop_00.mp3"), false, false);
	}
	
	@Override
	public void update(float deltaTime) {
		Label counterLabel = (Label) getComponent(GuiComponent.class).getGuiElement();
		if (remainingTime > 0.f) {
			remainingTime -= deltaTime; 
			counterLabel.setText((int)remainingTime);
		} else { 
			counterLabel.setText("inf");
			getComponent(SoundComponent.class).getSoundEffect("TimeUp").shouldPlay = true;
		}
	}

	public boolean isTimeUp() {
		return remainingTime < 0f;
	}
}
