package ecs.systems;

import com.badlogic.gdx.audio.Music;

import ecs.components.SoundComponent;
import ecs.components.SoundComponent.SoundEffect;
import utility.ImmutableArray;

public class AudioSystem {
	public void playAudio(ImmutableArray<SoundComponent> soundComponents, Music gameplayMusic) {
		if (gameplayMusic != null)
			gameplayMusic.play();
		
		for (SoundComponent soundComp : soundComponents) {
			for (SoundEffect soundEffect : soundComp.getSoundEffects()) {
				if (soundEffect.shouldPlay && !soundEffect.playing) {
					
					if (soundEffect.looped) {
						soundEffect.playing = true;
						soundEffect.sound.loop();
					} else {
						soundEffect.sound.play();
						soundEffect.shouldPlay = false;
					}
					
				} else if (!soundEffect.shouldPlay && soundEffect.playing && soundEffect.looped) {
					soundEffect.playing = false;
					soundEffect.sound.stop();
				}
			}
		}
	}
}
