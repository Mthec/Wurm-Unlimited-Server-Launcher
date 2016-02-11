package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.consolereader.exceptions.NoSuchOption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Menu implements Option {
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    private Map<String, Option> options = new HashMap<>();
    private String name;
    private String text;
    private String help;
    private String listText;
    private Menu parent = null;

    public Menu(String name, String text, String help, Option[] options) {
        this.name = name;
        this.text = text;
        this.help = help;
        this.setOptions(options);
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

    public String getText() {
        return text + System.lineSeparator() + list();
    }

    @Override
    public String help() {
        return help;
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

    public Menu getParent() { return parent; }

    public void setParent(Menu menu) { parent = menu; }

    protected void setOptions(Option[] options) {
        for (Option option : options) {
            if (option != null) {
                this.options.put(option.getName(), option);
                if (option instanceof Menu) {
                    ((Menu)option).setParent(this);
                }
            }
        }
        assert !this.options.isEmpty();

        Predicate<Map.Entry<String, Option>> menusFilter = entry -> entry.getValue() instanceof Menu;
        String menus = this.options.entrySet().stream().filter(menusFilter).map(Map.Entry::getKey).sorted().collect(Collectors.toList()).toString();

        Predicate<Map.Entry<String, Option>> otherFilter = entry -> !(entry.getValue() instanceof Menu);
        String other = this.options.entrySet().stream().filter(otherFilter).map(Map.Entry::getKey).sorted().collect(Collectors.toList()).toString();

        listText = String.join(menus.equals("[]") || other.equals("[]") ? "" : System.lineSeparator(),
                menus.equals("[]") ? "" : messages.getString("menu_prefix") + " " + menus,
                other.equals("[]") ? "" : messages.getString("other_prefix") + " " + other);
    }

    public Menu get() {
        return this;
    }
}
