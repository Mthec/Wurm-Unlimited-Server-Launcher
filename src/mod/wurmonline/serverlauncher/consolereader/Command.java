package mod.wurmonline.serverlauncher.consolereader;

public class Command implements Option {
    String helpText;
    String name;

    Command (String newName, String newHelpText) {
        name = newName;
        helpText = newHelpText;
    }

    @Override
    public String action() {
        System.out.println("1 + 1 = 5");
        return "Done";
    }

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
