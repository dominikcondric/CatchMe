package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.gdx.game.CatchMe;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.GuiComponent;
import ecs.components.SoundComponent;
import ecs.systems.RenderingSystem;

public class CharacterPicker extends Entity implements Disposable {
	private Image characterImage;
	private boolean pickConfirmed = false;
	private TextureRegion[] characterTextures;
	private int currentTextureIndex = 0; 
	private static final int ROWS = 4;
	private static final int COLUMNS = 6;
	private Label readyLabel;

	public CharacterPicker(ComponentDatabase componentDB, boolean leftScreen) {
		super(componentDB);
		characterTextures = new TextureRegion[24];
		Texture characterTexture = new Texture("32_Characters//All.png");
		int regionWidth = characterTexture.getWidth() / COLUMNS;
		int regionHeight = characterTexture.getHeight() / ROWS;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				characterTextures[i * COLUMNS + j] = new TextureRegion(characterTexture, j * regionWidth, i * regionHeight, regionWidth, regionHeight);
			}
		}
		addComponents(leftScreen);
	}
	
	public void changeTexture(boolean next) {
		if (!pickConfirmed) {
			if (next)
				currentTextureIndex = (currentTextureIndex + 1) % characterTextures.length;
			else
				currentTextureIndex -= 1;
			
			if (currentTextureIndex < 0)
				currentTextureIndex = characterTextures.length - 1;
			
			characterImage.setDrawable(new TextureRegionDrawable(characterTextures[currentTextureIndex]));
			getComponent(SoundComponent.class).getSoundEffect("TextureChanged").shouldPlay = true;
		}
	}
	
	public void confirmPick() {
		if (!pickConfirmed) {
			pickConfirmed = true;
			readyLabel.setText("Ready!");
			getComponent(SoundComponent.class).getSoundEffect("PickConfirmed").shouldPlay = true;
		}
	}
	
	public int getTextureIndex() {
		return currentTextureIndex;
	}
	
	public void playGetReadySound() {
		getComponent(SoundComponent.class).getSoundEffect("GetReady").shouldPlay = true;
	}
	
	private void addComponents(boolean leftScreen) {
		Table table = new Table();
		table.center();
		final int screenWidth = RenderingSystem.GUI_WORLD_WIDTH;
		final int screenHeight = RenderingSystem.GUI_WORLD_HEIGHT;
		table.setSize(screenWidth / 2, screenHeight / 2);
		if (leftScreen)
			table.setPosition(0, screenHeight / 4.f);
		else
			table.setPosition(screenWidth / 2, screenHeight / 4.f);
			
		table.row().center().height(table.getHeight() / 5.f);
		Label pickCharacterLabel = new Label("Pick your character", new LabelStyle(CatchMe.font, Color.ORANGE));
		pickCharacterLabel.setFontScale(1.3f);
		table.add(pickCharacterLabel).colspan(3);
		
		table.row().center().width(table.getWidth() / 12f).height(table.getHeight() / 5.f * 3f);
		Image leftArrowImage = new Image(new TextureRegionDrawable(new Texture("LeftArrow.png")));
		table.add(leftArrowImage);
		characterImage = new Image(new TextureRegionDrawable(characterTextures[0]));
		characterImage.setSize(table.getHeight(), table.getHeight());
		table.add(characterImage).width(table.getHeight() / 5f * 3f);
		Image rightArrowImage = new Image(new TextureRegionDrawable(new Texture("RightArrow.png")));
		table.add(rightArrowImage);
		addComponent(new GuiComponent(table));
		
		table.row().height(table.getHeight() / 5.f).center();
		readyLabel = new Label("", new LabelStyle(CatchMe.font, Color.GREEN));
		readyLabel.setFontScale(1.3f);
		table.add(readyLabel).colspan(3);
		
		SoundComponent soundComp = new SoundComponent();
		soundComp.addSound("TextureChanged", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Menu_Navigate_02.mp3"), false, false);
		soundComp.addSound("PickConfirmed", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Pickup_04.mp3"), false, false);
		soundComp.addSound("GetReady", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Jingle_Win_00.mp3"), false, false);
		addComponent(soundComp);
	}

	public boolean isPickConfirmed() {
		return pickConfirmed;
	}

	@Override
	public void dispose() {
		characterTextures[0].getTexture().dispose();
	}
}
