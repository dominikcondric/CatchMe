package patterns.commands;

import ecs.entities.CharacterPicker;

public class ChangePlayerImageCommand extends Command {
	private CharacterPicker characterPicker;
	private boolean next;
	
	public ChangePlayerImageCommand(CharacterPicker picker, boolean next, boolean singlePress) {
		super(singlePress);
		characterPicker = picker;
		this.next = next;
	}

	@Override
	public void execute(float deltaTime) {
		characterPicker.changeTexture(next);
	}
}
