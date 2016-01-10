package mod.wurmonline.serverlauncher.consolereader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu implements Option {
    private Map<String, Option> options = new HashMap<>();
    private String name;
    private String text;

    public Menu (String name, String text, Option[] options) {
        this.name = name;
        this.text = text;
        for (Option option : options) {
            if (option != null) {
                this.options.put(option.getName(), option);
            }
        }
    }

    @Override
    public String action(List<String> tokens) {
        return text + System.lineSeparator() + list();
    }

    @Override
    public Option ask(String input) throws NoSuchOption {
        if (!options.containsKey(input)) {
            throw new NoSuchOption(input);
        }
        return options.get(input);
    }

    @Override
    public String help() {
        return text + System.lineSeparator() + list();
    }

    public String help(String option) throws NoSuchOption {
        try {
            return options.get(option).help();
        } catch (NullPointerException ex) {
            throw new NoSuchOption(option);
        }
    }

    public String list() {
        // TODO - Mark menus vs. commands.
        return "Options - " + options.keySet().toString();
    }

    @Override
    public String getName() {
        return name;
    }
}
