package ecs.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import ecs.ComponentDatabase;
import ecs.Entity;
import ecs.components.EventComponent;
import ecs.components.GuiComponent;
import patterns.Event;
import patterns.EventCallback;
import screens.GameScreen;

public class GameStatus extends Entity {
	private float remainingTime;
	private String winningPlayer = null;

	public GameStatus(ComponentDatabase componentDB, float matchLength) {
		super(componentDB);
		createComponents();
		remainingTime = matchLength;
	}
	
	private void createComponents() {
		Group group = new Group();
		LabelStyle labelStyle = new LabelStyle(GameScreen.font, Color.WHITE);
		
		Label counterLabel = new Label(String.valueOf((int) remainingTime), labelStyle);
		counterLabel.setFontScale(3f);
		counterLabel.setAlignment(Align.center);
		counterLabel.setSize(Gdx.graphics.getWidth() / 8.f, Gdx.graphics.getHeight() / 8.f);
		counterLabel.setPosition(Gdx.graphics.getWidth() / 2.f - counterLabel.getWidth() / 2.f, Gdx.graphics.getHeight() - counterLabel.getHeight());
		group.addActor(counterLabel);
		
		Label p1Label = new Label("Catching", labelStyle);
		p1Label.setColor(Color.FIREBRICK);
		p1Label.setSize(Gdx.graphics.getWidth() / 8.f, Gdx.graphics.getHeight() / 8.f);
		p1Label.setPosition(Gdx.graphics.getWidth() / 4.f - p1Label.getWidth() / 2.f, Gdx.graphics.getHeight() - p1Label.getHeight());
		p1Label.setAlignment(Align.center);
		p1Label.setFontScale(2.f);
		group.addActor(p1Label);
		
		Label p2Label = new Label("Catching", labelStyle);
		p2Label.setColor(Color.FIREBRICK);
		p2Label.setSize(Gdx.graphics.getWidth() / 8.f, Gdx.graphics.getHeight() / 8.f);
		p2Label.setPosition(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 4.f - p1Label.getWidth() / 2.f, Gdx.graphics.getHeight() - p1Label.getHeight());
		p2Label.setAlignment(Align.center);
		p2Label.setFontScale(2.f);
		group.addActor(p2Label);
		
		addComponent(new GuiComponent(group));
		
		EventComponent eventComp = new EventComponent(new EventCallback() {
			
			@Override
			public void onMyEventObserved(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEventObserved(Event event) {
				if (event.message.contentEquals("Caught")) {
					String playerName = (String) event.data;
					winningPlayer = playerName;
					GuiComponent guiComp = getComponent(GuiComponent.class);
					Label catcherLabel, fleeingLabel = null;
					if (playerName.equals("P1")) {
						catcherLabel = (Label) ((Group)guiComp.getGuiElement()).getChild(1);
						fleeingLabel = (Label) ((Group)guiComp.getGuiElement()).getChild(2);
					} else {
						catcherLabel = (Label) ((Group)guiComp.getGuiElement()).getChild(2);
						fleeingLabel = (Label) ((Group)guiComp.getGuiElement()).getChild(1);
					}
					
					catcherLabel.setText("Fleeing");
					catcherLabel.setColor(Color.GREEN);
					
					fleeingLabel.setText("Catching");
					fleeingLabel.setColor(Color.RED);
				}
			}
		});
		
		eventComp.observedEvents.add("Caught");
		addComponent(eventComp);
	}
	
	@Override
	public void update(float deltaTime) {
		remainingTime -= deltaTime;
		Label counterLabel = (Label) ((Group)getComponent(GuiComponent.class).getGuiElement()).getChild(0);
		if (remainingTime >= 0f)
			counterLabel.setText((int)remainingTime);
		else 
			counterLabel.setText("inf");
	}

	public boolean isGameOver() {
		if (winningPlayer != null)
			return remainingTime < 0f;
		
		return false;
	}
	
	public String getWinnerName() {
		return winningPlayer;
	}
}
