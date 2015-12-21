package mod.wurmonline.serverlauncher.consolereader;

import java.util.HashMap;
import java.util.Map;

public class Menu implements Option {
    private Map<String, Option> options = new HashMap<>();
    private String name;
    private String text;

    Menu (String name, String text, Option[] options) {
        this.name = name;
        this.text = text;
        for (Option option : options) {
            this.options.put(option.getName(), option);
        }
    }

    @Override
    public String action() {
        return text;
    }

    @Override
    public Option ask(String input) {
        if (options.containsKey(input)) {
            return options.get(input);
        }
        return null;
    }

    @Override
    public String help() {
        return "Options - " + options.keySet().toString();
    }

    public String help(String option) throws NoSuchOption {
        if (option.equals("")) {
            return "Options - " + options.keySet().toString();
        } else {
            try {
                return options.get(option).help();
            } catch (NullPointerException ex) {
                throw new NoSuchOption(option);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
