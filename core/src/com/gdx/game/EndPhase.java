package com.gdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;

import ecs.components.GuiComponent;
import ecs.components.SoundComponent;
import ecs.entities.WinnerGuiEntity;
import patterns.commands.Command;
import utility.CommandMapper;
import utility.Toolbox;

public class EndPhase extends GamePhase {
	private WinnerGuiEntity winnerEntity;
	private boolean finished = false;

	public EndPhase(TextureRegion winnerTextureRegion, String winnerName) {
		winnerEntity = new WinnerGuiEntity(componentDatabase, winnerTextureRegion, winnerName);
	}
	
	@Override
	public void dispose() {
		componentDatabase.dispose();
	}

	@Override
	public GamePhase getNewGamePhase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(float deltaTime) {
		winnerEntity.update(deltaTime);
	}

	@Override
	public void run(Toolbox toolbox, Viewport viewport, float deltaTime) {
		toolbox.getRenderingSystem().clearScreen();
		toolbox.getRenderingSystem().renderGUI(componentDatabase.getComponentArray(GuiComponent.class));
		toolbox.getAudioSystem().playAudio(componentDatabase.getComponentArray(SoundComponent.class), null);
	}

	@Override
	public void setCommands(CommandMapper commandMapper) {
		commandMapper.addCommand(Input.Keys.ENTER, new Command(true) {

			@Override
			public void execute(float deltaTime) {
				if (winnerEntity.isCounterFinished())
					finished = true;
			}
		});
	}

	@Override
	public boolean isFinished() {
		return finished && winnerEntity.isCounterFinished();
	}

}
