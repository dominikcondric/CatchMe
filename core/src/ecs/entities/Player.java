package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.powerups.PowerUp;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.AnimationComponent;
import ecs.components.CollisionCallback;
import ecs.components.EventComponent;
import ecs.components.GuiComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.SoundComponent;
import ecs.components.PhysicsComponent.BodyType;
import ecs.components.PhysicsComponent.Fixture;
import patterns.Event;
import patterns.EventCallback;
import patterns.WalkCommand;
import patterns.WalkCommand.Directions;
import screens.GameScreen;
import utility.CommandMapper;
import utility.CommandMapper.CommandMap;

public class Player extends Entity {
	private String playerName;
	private boolean moving = false;
	private WalkCommand.Directions lastMovingDirection = null;
	private PowerUp powerUp = null;
	private float blockedDuration = 0.f;
	private boolean inCatchRange = false;
	private boolean catching = true; 

	public Player(ComponentDatabase componentDB, boolean player1, Vector2 playerPosition, String playerTexturePath) {
		super(componentDB);
		if (player1)
			playerName = "P1";
		else
			playerName = "P2";
		
		createComponents(playerPosition, playerTexturePath);
	}
	
	protected void createComponents(Vector2 playerPosition, String playerTexturePath) {
		/////////////////////////////// Animation component ////////////////////////////////
		AnimationComponent mcAnimationComp = new AnimationComponent(playerPosition.x, playerPosition.y, 1.f, 1.f);
		Array<TextureRegion> mcAnimationSprites = new Array<>(3);
		final Texture characterTexture = new Texture(playerTexturePath);
		TextureRegion[] charactedTextureRegions = new TextureRegion[12];
		for (int i = 0; i < charactedTextureRegions.length; ++i) {
			charactedTextureRegions[i] = new TextureRegion(characterTexture, i % 4 * 16, 1 + i / 4 * 17, 16, 16);
		}
		
		mcAnimationSprites.add(charactedTextureRegions[0]);
		mcAnimationComp.addAnimation("IdleDown", mcAnimationSprites, 0.f, PlayMode.NORMAL, true);
		mcAnimationSprites.clear();
		mcAnimationSprites.add(charactedTextureRegions[1]);
		mcAnimationComp.addAnimation("IdleRight", mcAnimationSprites, 0.f, PlayMode.NORMAL, false);
		mcAnimationSprites.clear();
		mcAnimationSprites.add(charactedTextureRegions[2]);
		mcAnimationComp.addAnimation("IdleUp", mcAnimationSprites, 0.f, PlayMode.NORMAL, false);
		mcAnimationSprites.clear();
		mcAnimationSprites.add(charactedTextureRegions[3]);
		mcAnimationComp.addAnimation("IdleLeft", mcAnimationSprites, 0.f, PlayMode.NORMAL, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[4]);
		mcAnimationSprites.add(charactedTextureRegions[8]);
		mcAnimationComp.addAnimation("WalkDown", mcAnimationSprites, 0.2f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[5]);
		mcAnimationSprites.add(charactedTextureRegions[1]);
		mcAnimationSprites.add(charactedTextureRegions[9]);
		mcAnimationComp.addAnimation("WalkRight", mcAnimationSprites, 0.1f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[6]);
		mcAnimationSprites.add(charactedTextureRegions[10]);
		mcAnimationComp.addAnimation("WalkUp", mcAnimationSprites, 0.2f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		
		mcAnimationSprites.add(charactedTextureRegions[7]);
		mcAnimationSprites.add(charactedTextureRegions[3]);
		mcAnimationSprites.add(charactedTextureRegions[11]);
		mcAnimationComp.addAnimation("WalkLeft", mcAnimationSprites, 0.1f, PlayMode.LOOP_PINGPONG, false);
		mcAnimationSprites.clear();
		addComponent(mcAnimationComp);
		
		/////////////////////////////// Physics component ////////////////////////////////
		Rectangle boundingRectangle = mcAnimationComp.getCurrentSprite().getBoundingRectangle();
		PhysicsComponent playerPhysicsComp = new PhysicsComponent(boundingRectangle.getCenter(new Vector2()), new CollisionCallback() {
			
			@Override
			public void onCollision(Fixture other) {
				if ((other.collisionResponseFlags & PhysicsComponent.ITEM_FLAG) != 0) {
					if (powerUp == null)
						getComponent(EventComponent.class).publishedEvents.add(new Event("PickPowerUp", null));
				} 
				
				if ((other.collisionResponseFlags & PhysicsComponent.PLAYER_FLAG) != 0) {
					inCatchRange = true;
				}
			}
			
		}, new Vector2(), 5.f);
		
		Vector2 bodyPosition = new Vector2(-boundingRectangle.width / 4.f, -boundingRectangle.height / 2.f);
		Vector2 bodySize = new Vector2(boundingRectangle.width / 2f, boundingRectangle.height / 4f);
		Fixture bodyFixture = playerPhysicsComp.addFixture(bodyPosition, bodySize, BodyType.Dynamic, false);
		bodyFixture.collisionInitiationFlags = PhysicsComponent.OBSTACLE_FLAG | PhysicsComponent.ITEM_FLAG;
		bodyFixture.collisionResponseFlags = PhysicsComponent.PLAYER_FLAG;
		
		Vector2 sensorPosition = new Vector2(-boundingRectangle.width, -boundingRectangle.height);
		Vector2 sensorSize = new Vector2(boundingRectangle.width * 2.f, boundingRectangle.height * 2.f);
		Fixture sensorFixture = playerPhysicsComp.addFixture(sensorPosition, sensorSize, BodyType.Dynamic, true);
		sensorFixture.collisionInitiationFlags = PhysicsComponent.PLAYER_FLAG;
		sensorFixture.collisionResponseFlags = PhysicsComponent.PLAYER_FLAG;
		
		addComponent(playerPhysicsComp);
		
		Vector2 worldPosition = playerPhysicsComp.getWorldPosition();
		
		/////////////////////////////// Lightcomponent ////////////////////////////////
		addComponent(new LightComponent(worldPosition.x, worldPosition.y, 2.f, .5f));
		
		/////////////////////////////// Event component ////////////////////////////////
		EventComponent ec = new EventComponent(new EventCallback() {
			
			@Override
			public void onEventObserved(Event event) {
				EventComponent eventComp = getComponent(EventComponent.class);
				if (event.message.contentEquals("CollectPowerUp")) {
					PowerUp powerup = (PowerUp) event.data;
					Player.this.powerUp = powerup;
					getComponent(SoundComponent.class).getSoundEffect("PowerUpPickUp").shouldPlay = true;
					Image powerUpImage = (Image) ((Group)getComponent(GuiComponent.class).getGuiElement()).getChild(1);
					powerUpImage.setDrawable(new TextureRegionDrawable(powerup.textureRegion));
					eventComp.observedEvents.removeValue(event.message, false);
				} else if (event.message.contentEquals("Caught")) {
					blockedDuration = 3.f;
					catching = true;
					getComponent(SoundComponent.class).getSoundEffect("Caught").shouldPlay = true; 
					Label label = ((Label) ((Group) getComponent(GuiComponent.class).getGuiElement()).getChild(0));
					label.setColor(Color.ORANGE);
					label.setText("Catching");
				}
			}

			@Override
			public void onMyEventObserved(Event event) {
				EventComponent eventComp = getComponent(EventComponent.class);
				if (event.message.contentEquals("PickPowerUp")) {
					eventComp.publishedEvents.removeValue(event, false);
					eventComp.observedEvents.add("CollectPowerUp");
				}
				
				if (event.message.contentEquals("Caught")) {
					eventComp.publishedEvents.removeValue(event, false);
					Label label = ((Label) ((Group) getComponent(GuiComponent.class).getGuiElement()).getChild(0));
					label.setColor(Color.GREEN);
					label.setText("Fleeing");
				}
			}
		});
		
		ec.observedEvents.add("PickPowerUp", "Caught");
		addComponent(ec);
		
		/////////////////////////////// Sound component ////////////////////////////////
		SoundComponent soundComp = new SoundComponent();
		soundComp.addSound("Footsteps", Gdx.files.internal("footsteps//step_cloth1.ogg"), false, true);
		soundComp.addSound("PowerUpPickUp", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Collect_Point_01.mp3"), false, false);
		soundComp.addSound("Caught", Gdx.files.internal("8-Bit Sound Library//8-Bit Sound Library//Mp3//Collect_Point_00.mp3"), false, false);
		addComponent(soundComp);
		
		/////////////////////////////// Gui component ////////////////////////////////
		Group group = new Group();
		group.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 8.f);
		
		if (playerName.equals("P1"))
			group.setPosition(0.f, Gdx.graphics.getHeight() * 7f/8f);
		else 
			group.setPosition(group.getWidth(), Gdx.graphics.getHeight() * 7f/8f);
		
		Label catchingLabel = new Label("Catching", new LabelStyle(GameScreen.font, Color.ORANGE));
		catchingLabel.setSize(group.getWidth() / 4.f, group.getHeight());
		catchingLabel.setPosition(group.getWidth() / 2.f - catchingLabel.getWidth() / 2.f, 0f);
		catchingLabel.setAlignment(Align.center);
		catchingLabel.setFontScale(2.f);
		group.addActor(catchingLabel);
		
		Image powerUpImage = new Image();
		powerUpImage.setSize(group.getHeight() / 2.f, group.getHeight() / 2.f);
		if (playerName.equals("P1"))
			powerUpImage.setPosition(group.getHeight() / 4.f, group.getHeight() / 4.f);
		else 
			powerUpImage.setPosition(group.getWidth() - group.getHeight(), group.getHeight() / 2f);
		group.addActor(powerUpImage);
		
		addComponent(new GuiComponent(group));
	}
	
	public PowerUp getPowerUp() {
		return powerUp;
	}
	
	@Override
	public void update(float deltaTime) {
		boolean walking = false;
		AnimationComponent animationComponent = getComponent(AnimationComponent.class);
		blockedDuration -= deltaTime;
		
		if (blockedDuration <= 0.f) {
			blockedDuration = 0.f;
			CommandMapper commandMapper = CommandMapper.getInstance();
			CommandMap walkRightCommand = commandMapper.getCommandKey(getPlayerName() + "WalkRight");
			CommandMap walkLeftCommand = commandMapper.getCommandKey(getPlayerName() + "WalkLeft");
			CommandMap walkUpCommand = commandMapper.getCommandKey(getPlayerName() + "WalkUp");
			CommandMap walkDownCommand = commandMapper.getCommandKey(getPlayerName() + "WalkDown");
			CommandMap catchCommand = commandMapper.getCommandKey(getPlayerName() + "Catch");
			
			
			if (Gdx.input.isKeyPressed(walkRightCommand.getKey()) && (lastMovingDirection == Directions.RIGHT || !moving)) {
				walking = true;
				lastMovingDirection = Directions.RIGHT;
				animationComponent.setActiveAnimation("WalkRight");
				walkRightCommand.getCommand().execute(this, deltaTime);
			}
			
			if (Gdx.input.isKeyPressed(walkLeftCommand.getKey()) && (lastMovingDirection == Directions.LEFT || !moving)) {
				walking = true;
				lastMovingDirection = Directions.LEFT;
				animationComponent.setActiveAnimation("WalkLeft");
				walkLeftCommand.getCommand().execute(this, deltaTime);
			}
			
			if (Gdx.input.isKeyPressed(walkUpCommand.getKey()) && (lastMovingDirection == Directions.UP || !moving)) {
				lastMovingDirection = Directions.UP;
				animationComponent.setActiveAnimation("WalkUp");
				walking = true;
				walkUpCommand.getCommand().execute(this, deltaTime);
			}
			
			if (Gdx.input.isKeyPressed(walkDownCommand.getKey()) && (lastMovingDirection == Directions.DOWN || !moving)) {
				lastMovingDirection = Directions.DOWN;
				animationComponent.setActiveAnimation("WalkDown");
				walking = true;
				walkDownCommand.getCommand().execute(this, deltaTime);
			}
			
			if (powerUp != null) {
				CommandMap powerUpCommand = commandMapper.getCommandKey(playerName + "UsePowerUp");
				if (Gdx.input.isKeyJustPressed(powerUpCommand.getKey())) {
					powerUpCommand.getCommand().execute(this, deltaTime);
				}
				
				if (powerUp.isUsed()) {
					powerUp.update(this, deltaTime);
					if (powerUp.isFinished()) {
						powerUp = null;
					}
				}
			}
			
			if (Gdx.input.isKeyJustPressed(catchCommand.getKey()) && inCatchRange && catching) {
				catchCommand.getCommand().execute(this, deltaTime);
				catching = false;
			}
		}
		
		if (!walking) {
			animationComponent.setActiveAnimation(animationComponent.getActiveAnimation().replace("Walk", "Idle"));
			getComponent(PhysicsComponent.class).setMovingDirection(new Vector2(0.f, 0.f));
			getComponent(SoundComponent.class).getSoundEffect("Footsteps").shouldPlay = false;
		}
		
		moving = walking;

		Sprite sprite = animationComponent.getCurrentSprite();
		Vector2 worldPosition = getComponent(PhysicsComponent.class).getWorldPosition();
		sprite.setX(worldPosition.x - sprite.getWidth() / 2f);
		sprite.setY(worldPosition.y - sprite.getHeight() / 2f);
		
		LightComponent lightComponent = getComponent(LightComponent.class);
		lightComponent.setPosition(worldPosition);
		
		inCatchRange = false;
	}

	public String getPlayerName() {
		return playerName;
	}
}
