package ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ecs.components.AnimationComponent;
import ecs.components.GuiComponent;
import ecs.components.LightComponent;
import ecs.components.PhysicsComponent;
import ecs.components.PhysicsComponent.Fixture;
import ecs.components.SpriteComponent;
import utility.ImmutableArray;

public class RenderingSystem implements Disposable {
	private SpriteBatch spriteBatch;
	private OrthogonalTiledMapRenderer mapRenderer;
	private ShapeRenderer shapeRenderer;
	private Stage gui;
	
	public RenderingSystem() {
		spriteBatch = new SpriteBatch();
		mapRenderer = new OrthogonalTiledMapRenderer(null, 1.f / 32.f, spriteBatch);
		shapeRenderer = new ShapeRenderer();
		gui = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), spriteBatch);
	}
	
	public void clearScreen() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	public void renderEntities(ImmutableArray<SpriteComponent> spriteComponents, ImmutableArray<AnimationComponent> animationComponents, ImmutableArray<LightComponent> lights, TiledMap map, OrthographicCamera camera) {
		spriteBatch.setProjectionMatrix(camera.combined);
		mapRenderer.setMap(map);
		mapRenderer.setView(camera);
		mapRenderer.render();
		spriteBatch.begin();
		for (SpriteComponent renderingComp : spriteComponents) {
			renderingComp.getSprite().draw(spriteBatch);
		}
		
		for (AnimationComponent animationComp : animationComponents) {
			animationComp.getCurrentSprite().draw(spriteBatch);
		}
		spriteBatch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_DST_ALPHA);
		Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glStencilMask(0xFF);
		Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
		Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0.f, 0.f, 0.f, 0.f);
		for (LightComponent light : lights) {
			shapeRenderer.setColor(0.f, 0.f, 0.f, 0.f);
			Vector2 lightPosition = light.getPosition();
			shapeRenderer.circle(lightPosition.x, lightPosition.y, light.getRadius(), 50);
		}
		Gdx.gl.glBlendFunc(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_DST_ALPHA);
		Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 0, 0xFF);
		int mapWidth = map.getProperties().get("width", Integer.class);
		int mapHeight = map.getProperties().get("height", Integer.class);
		shapeRenderer.setColor(0f, 0f, 0f, .98f);
		shapeRenderer.rect(0f, 0.f, mapWidth, mapHeight);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}
	
	public void renderGUI(ImmutableArray<GuiComponent> guiComponents) {
		if (GuiComponent.stageModified) {
			gui.clear();
			for (GuiComponent guiComp  : guiComponents) {
				gui.addActor(guiComp.getGuiElement());
			}
		}
		
		gui.getViewport().apply();
		gui.act();
		gui.draw();
	}
	
	public void renderColliders(ImmutableArray<PhysicsComponent> physicsComponents, OrthographicCamera camera) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.BLUE);
		for (PhysicsComponent physicsComp : physicsComponents) {
			for (Fixture f : physicsComp.getFixtureList()) {
				Rectangle rect = f.getBoundingRectangle();
				shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			}
		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
		mapRenderer.dispose();
		shapeRenderer.dispose();
		gui.dispose();
	}
	
	public void onScreenResize(int width, int height) {
		gui.getViewport().setScreenSize(width, height);
	}
}
