package com.gdx.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.viewport.Viewport;

import ecs.ComponentDatabase;
import ecs.components.GuiComponent;
import ecs.components.SoundComponent;
import ecs.entities.CharacterPicker;
import ecs.systems.RenderingSystem;
import patterns.commands.ChangePlayerImageCommand;
import patterns.commands.ConfirmPickCommand;
import utility.CommandMapper;
import utility.Toolbox;

public class GameSetupPhase extends GamePhase {
	private CharacterPicker picker1, picker2; 
	private ComponentDatabase componentDatabase;
	private String[] characterTexturePaths;
	private float timer = 5.f;
	
	public GameSetupPhase() {
		this.componentDatabase = new ComponentDatabase();
		picker1 = new CharacterPicker(componentDatabase, true);
		picker2 = new CharacterPicker(componentDatabase, false);
		
		characterTexturePaths = new String[] {
			"Females//F_01.png", "Males//M_12.png", "Females//F_09.png", "Males//M_08.png", "Females//F_05.png", "Males//M_05.png",
			"Males//M_04.png", "Females//F_11.png", "Males//M_11.png", "Females//F_07.png", "Males//M_07.png", "Females//F_04.png",
			"Females//F_03.png", "Males//M_03.png", "Females//F_12.png", "Males//M_10.png", "Females//F_08.png", "Males//M_06.png",
			"Males//M_01.png", "Females//F_02.png",  "Males//M_02.png", "Females//F_10.png", "Males//M_09.png", "Females//F_06.png" 
		};
	}
	
	@Override
	public void setCommands(CommandMapper commandMapper) {
		commandMapper.addCommand(Keys.D, new ChangePlayerImageCommand(picker1, true, true));
		commandMapper.addCommand(Keys.A, new ChangePlayerImageCommand(picker1, false, true));
		commandMapper.addCommand(Keys.SPACE, new ConfirmPickCommand(picker1, true));
		
		commandMapper.addCommand(Keys.RIGHT, new ChangePlayerImageCommand(picker2, true, true));
		commandMapper.addCommand(Keys.LEFT, new ChangePlayerImageCommand(picker2, false, true));
		commandMapper.addCommand(Keys.ENTER, new ConfirmPickCommand(picker2, true));
	}
	
	@Override
	public void run(Toolbox toolbox, Viewport viewport, float deltaTime) {
		toolbox.getRenderingSystem().clearScreen();
		draw(toolbox.getRenderingSystem());
		toolbox.getAudioSystem().playAudio(componentDatabase.getComponentArray(SoundComponent.class), null);
	}
	
	public void draw(RenderingSystem renderingSystem) {
		renderingSystem.renderGUI(componentDatabase.getComponentArray(GuiComponent.class));
	}

	public boolean isFinished() {
		return timer <= 0.f;
	}
	
	@Override
	public void dispose() {
		componentDatabase.dispose();
	}

	@Override
	public GamePhase getNewGamePhase() {
		if (isFinished()) {
			return new GameplayPhase("32_Characters//" + characterTexturePaths[picker1.getTextureIndex()],
					"32_Characters//" + characterTexturePaths[picker2.getTextureIndex()], 60f);
		}
		
		return null;
	}

	@Override
	public void update(float deltaTime) {
		if (picker1.isPickConfirmed() && picker2.isPickConfirmed() && timer == 5f) {
			picker1.playGetReadySound();
			timer -= deltaTime;
		}
		
		if (timer < 5.f) {
			timer -= deltaTime;
		}
	}
}
