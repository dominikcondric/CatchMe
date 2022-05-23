package ecs.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteComponent implements Component {
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
}
