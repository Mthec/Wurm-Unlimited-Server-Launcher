package mod.wurmonline.serverlauncher.consolereader;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Menu implements Option {
    private Map<String, Option> options = new HashMap<>();
    private String name;
    private String text;
    private String listText;

    public Menu (String name, String text, Option[] options) {
        this.name = name;
        this.text = text;
        for (Option option : options) {
            if (option != null) {
                this.options.put(option.getName(), option);
            }
        }
        assert !this.options.isEmpty();

        Predicate<Map.Entry<String, Option>> menusFilter = entry -> entry.getValue() instanceof Menu;
        String menus = this.options.entrySet().stream().filter(menusFilter).map(Map.Entry::getKey).sorted().collect(Collectors.toList()).toString();

        Predicate<Map.Entry<String, Option>> otherFilter = entry -> !(entry.getValue() instanceof Menu);
        String other = this.options.entrySet().stream().filter(otherFilter).map(Map.Entry::getKey).sorted().collect(Collectors.toList()).toString();

        listText = String.join(menus.equals("[]") || other.equals("[]") ? "" : System.lineSeparator(),
                menus.equals("[]") ? "" : "Menu - " + menus,
                other.equals("[]") ? "" : "Options - " + other);
    }

    @Override
    public String action(List<String> tokens) {
        return text + System.lineSeparator() + list();
    }

    @Override
    public Option getOption(String input) throws NoSuchOption {
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
        return listText;
    }

    @Override
    public String getName() {
        return name;
    }
}
