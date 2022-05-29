package patterns.commands;

import ecs.entities.CharacterPicker;

public class ConfirmPickCommand extends Command {
	private CharacterPicker picker;
	
	public ConfirmPickCommand(CharacterPicker picker, boolean singlePress) {
		super(singlePress);
		this.picker = picker;
	}

	@Override
	public void execute(float deltaTime) {
		picker.confirmPick();
	}

}
