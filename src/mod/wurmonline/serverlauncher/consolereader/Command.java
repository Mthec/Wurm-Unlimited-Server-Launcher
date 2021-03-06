package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationRequired;
import mod.wurmonline.serverlauncher.consolereader.exceptions.RebuildRequired;

import java.util.List;
import java.util.ResourceBundle;

public abstract class Command implements Option {
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    String helpText;
    String name;

    public Command(String newName, String newHelpText) {
        name = newName;
        helpText = newHelpText;
    }

    @Override
    public abstract String action(List<String> tokens) throws RebuildRequired, ConfirmationRequired;

    @Override
    public Option getOption(String input) {
        assert messages != null;
        throw new UnsupportedOperationException("Command object does not have any options to get.");
    }

    @Override
    public String help() {
        return helpText;
    }

    @Override
    public String getName() {
        return name;
    }
}
