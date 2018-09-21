package seedu.address.logic.commands;

import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;

public class PasswordCommand extends Command {
    private String message;
    public static final String COMMAND_WORD = "password";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Hashes file using password.\n"
            + "Parameters: KEYWORD PASSWORD...\\n"
            + "Example: " + COMMAND_WORD + " myPassword";

    // public static final String MESSAGE_SUCCESS = "Command executed";

    public PasswordCommand (String message) {
        this.message = message;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {

        return new CommandResult(this.message);
    }


}
