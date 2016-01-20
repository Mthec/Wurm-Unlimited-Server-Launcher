package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.LocaleHelper;

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
    public abstract String action(List<String> tokens);

    @Override
    public Option getOption(String input) {
        assert messages != null;
        throw new UnsupportedOperationException(messages.getString("command_has_no_options"));
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
