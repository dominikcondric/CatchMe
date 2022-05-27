package ecs.systems;

import com.badlogic.gdx.audio.Music;

import ecs.components.SoundComponent;
import ecs.components.SoundComponent.SoundEffect;
import utility.ImmutableArray;

public class AudioSystem {
	public void playAudio(ImmutableArray<SoundComponent> soundComponents, Music gameplayMusic) {
		gameplayMusic.play();
		for (SoundComponent soundComp : soundComponents) {
			for (SoundEffect soundEffect : soundComp.getSoundEffects()) {
				if (soundEffect.shouldPlay && !soundEffect.playing) {
					soundEffect.playing = true;
					
					if (soundEffect.looped)
						soundEffect.sound.loop();
					else
						soundEffect.sound.play();
					
				} else if (!soundEffect.shouldPlay && soundEffect.playing) {
					soundEffect.playing = false;
					soundEffect.sound.stop();
				}
			}
		}
	}
}
