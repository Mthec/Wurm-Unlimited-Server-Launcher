package mod.wurmonline.serverlauncher.consolereader;

import java.util.List;

public abstract class Command implements Option {
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
        throw new UnsupportedOperationException("Command object cannot be asked.");
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
