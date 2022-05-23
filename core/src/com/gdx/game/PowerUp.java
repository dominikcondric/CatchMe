package com.gdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum PowerUp {
	SPEED(2, 8);
	
	PowerUp(int x, int y) {
		this.textureRegion = new TextureRegion(new Texture(commonTexture), x * 32, y * 32, 32, 32);
	}
	
	static PowerUp getRandomPowerUp() {
		return PowerUp.values()[new Random().nextInt(PowerUp.values().length)]; 
	}
	
	public final TextureRegion textureRegion;
	public static final String commonTexture = "Shikashi's Fantasy Icons Pack v2//BG 6.png";
}
