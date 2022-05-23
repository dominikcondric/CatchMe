package ecs.components;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class AnimationComponent implements Component, Disposable {
	private HashMap<String, Animation<TextureRegion>> animationMap;
	private String activeAnimation;
	private String previousAnimation;
	private Sprite currentSprite;
	private float stateTimer = 0.f;
	
	public AnimationComponent(float spriteX, float spriteY, float spriteWidth, float spriteHeight) {
		currentSprite = new Sprite();
		currentSprite.setPosition(spriteX, spriteY);
		currentSprite.setSize(spriteWidth, spriteHeight);
		animationMap = new HashMap<String, Animation<TextureRegion>>();
	}
	
	public String getActiveAnimation() {
		return activeAnimation;
	}
	
	public Sprite getCurrentSprite() {
		return currentSprite;
	}
	
	private Animation<TextureRegion> getAnimation(String name) {
		return animationMap.get(name);
	}
	
	public void updateAnimation(float deltaTime) {
		stateTimer += deltaTime;
		currentSprite.setRegion(getAnimation(activeAnimation).getKeyFrame(stateTimer));
	}
	
	public void addAnimation(String name, Array<TextureRegion> textureRegions, float keyFrameTime, PlayMode playMode, boolean active) {
		if (animationMap.containsKey(name)) {
			System.out.println("Animation name already exists");
			return;
		}
		
		Animation<TextureRegion> newAnimation = new Animation<TextureRegion>(keyFrameTime, textureRegions, playMode);
		animationMap.put(name, newAnimation);
		if (active) {
			activeAnimation = name;
			previousAnimation = activeAnimation;
		}
	}
	
	public void setActiveAnimation(String name) {
		if (!animationMap.containsKey(name)) {
			System.out.println("Animation name doesn't exist!");
			return;
		}
		
		previousAnimation = activeAnimation;
		activeAnimation = name;
		if (previousAnimation != activeAnimation)
			stateTimer = 0.f;
	}
	
	@Override
	public void dispose() {
		for (Animation<TextureRegion> a : animationMap.values()) {
			for (Object tr : a.getKeyFrames()) {
				((TextureRegion)tr).getTexture().dispose();
			}
		}
			
	}
}
