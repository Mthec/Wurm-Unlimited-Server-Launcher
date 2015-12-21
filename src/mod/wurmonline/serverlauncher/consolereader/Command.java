package mod.wurmonline.serverlauncher.consolereader;

public abstract class Command implements Option {
    String helpText;
    String name;

    Command (String newName, String newHelpText) {
        name = newName;
        helpText = newHelpText;
    }

    @Override
    public abstract String action();

    @Override
    public Option ask(String input) {
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
