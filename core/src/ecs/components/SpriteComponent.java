package ecs.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class SpriteComponent implements Component, Disposable {
	private Sprite sprite;
	
	public SpriteComponent(Sprite sprite) {
		this.sprite = sprite; 
	}
	
	public SpriteComponent(TextureRegion texture) {
		this.sprite = new Sprite(texture);
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public void dispose() {
		sprite.getTexture().dispose();
	}
}
