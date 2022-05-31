package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gdx.game.CatchMe;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.GuiComponent;
import ecs.components.SoundComponent;
import ecs.systems.RenderingSystem;

public class WinnerGuiEntity extends Entity {
	private float counter = 5.f;
	private Label endLabel;

	public WinnerGuiEntity(ComponentDatabase componentDB, TextureRegion winnerTextureRegion, String playerName) {
		super(componentDB);
		
		Group group = new Group();
		
		Label winnerLabel = new Label("Congratulations " + playerName + ", you won!", new LabelStyle(CatchMe.font, Color.ORANGE));
		winnerLabel.setSize(RenderingSystem.GUI_WORLD_WIDTH / 2f, RenderingSystem.GUI_WORLD_HEIGHT / 8f);
		winnerLabel.setPosition(RenderingSystem.GUI_WORLD_WIDTH / 4.f, RenderingSystem.GUI_WORLD_HEIGHT / 8f * 7f);
		winnerLabel.setAlignment(Align.center);
		winnerLabel.setFontScale(2.5f);
		group.addActor(winnerLabel);
		
		Image playerImage = new Image();
		playerImage.setDrawable(new TextureRegionDrawable(winnerTextureRegion));
		playerImage.setSize(RenderingSystem.GUI_WORLD_WIDTH / 4f, RenderingSystem.GUI_WORLD_HEIGHT / 2.f);
		playerImage.setPosition(RenderingSystem.GUI_WORLD_WIDTH / 2f - playerImage.getWidth() / 2f, RenderingSystem.GUI_WORLD_HEIGHT / 2f - playerImage.getHeight() / 2.f);
		group.addActor(playerImage);
		
		endLabel = new Label("Press enter to end the game...", new LabelStyle(CatchMe.font, Color.ORANGE));
		endLabel.setSize(RenderingSystem.GUI_WORLD_WIDTH / 2f, RenderingSystem.GUI_WORLD_HEIGHT / 8f);
		endLabel.setPosition(RenderingSystem.GUI_WORLD_WIDTH / 4.f, RenderingSystem.GUI_WORLD_HEIGHT / 8f);
		endLabel.setAlignment(Align.center);
		endLabel.setFontScale(2.5f);
		endLabel.setVisible(false);
		group.addActor(endLabel);
		
		addComponent(new GuiComponent(group));
		
		SoundComponent soundComp = new SoundComponent();
		soundComp.addSound("Winner", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Jingle_Win_01.mp3"), true, false);
		addComponent(soundComp);
	}

	@Override
	public void update(float deltaTime) {
		counter -= deltaTime;
		if (isCounterFinished()) {
			endLabel.setVisible(true);
		}
	}
	
	public boolean isCounterFinished() {
		return counter <= 0.f;
	}
}
