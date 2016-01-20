package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.ServerController;
import org.gotti.wurmunlimited.modloader.interfaces.WurmCommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO - Convert strings to locales.
// TODO - Add logging.
public class ConsoleReader implements Runnable {
    private static Logger logger = Logger.getLogger(ConsoleReader.class.getName());
    ServerController controller;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Menu topMenu;
    Menu currentMenu = null;

    // TODO - Decide which constructors are necessary.
    public ConsoleReader(ServerConsoleController controller) {
        this.controller = controller;
        buildMenu();
    }

    ConsoleReader(ServerConsoleController controller, Option[] options) {
        this.controller = controller;
        topMenu = new Menu("menu", "Wurm Server Controller - Main Menu", options);
        System.out.println(topMenu.action(null));
    }

    // For testing.
    ConsoleReader(Option[] options) {
        topMenu = new Menu("menu", "Wurm Server Controller - Main Menu", options);
        System.out.println(topMenu.action(null));
    }

    @Override
    public void run() {
        String nextLine = null;
        do {
            try {
                try {
                    nextLine = reader.readLine();
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                    return;
                }

                if (nextLine == null || nextLine.isEmpty()) {
                    continue;
                }

                // TODO - Remove.
                if (nextLine.equals("exit")) {
                    System.exit(1);
                }

                if (currentMenu == null) {
                    currentMenu = topMenu;
                }

                List<String> tokens = tokenize(nextLine);
                if (tokens.size() == 0) {
                    continue;
                }

                Option option = null;
                while (!tokens.isEmpty()) {
                    // Default commands.
                    if (tokens.get(0).equals("help")) {
                        if (tokens.size() == 1) {
                            System.out.println(currentMenu.help());
                        } else {
                            System.out.println(currentMenu.help(tokens.get(1)));
                        }
                        option = currentMenu;
                        tokens.clear();
                        continue;
                    } else if (tokens.get(0).equals("list")) {
                        System.out.println(currentMenu.list());
                        option = currentMenu;
                        tokens.clear();
                        continue;
                    } else if (tokens.get(0).equals("menu")) {
                        currentMenu = topMenu;
                        option = currentMenu;
                        if (tokens.size() > 1) {
                            tokens.remove(0);
                            continue;
                        } else {
                            System.out.println(currentMenu.action(tokens));
                            tokens.clear();
                            continue;
                        }
                    } else if (tokens.get(0).equals("up")) {
                        Menu nextMenu = currentMenu.getParent();
                        if (nextMenu == null) {
                            option = currentMenu;
                            // TODO - Reword;
                            System.err.println("Cannot go up a level.");
                            tokens.clear();
                            break;
                        } else {
                            currentMenu = nextMenu;
                        }
                        option = currentMenu;
                        if (tokens.size() > 1) {
                            tokens.remove(0);
                            continue;
                        } else {
                            System.out.println(currentMenu.action(tokens));
                            tokens.clear();
                            continue;
                        }
                    }

                    option = currentMenu.getOption(tokens.remove(0));

                    if (option instanceof Menu) {
                        currentMenu = (Menu) option;
                        if (tokens.isEmpty()) {
                            System.out.println(option.action(tokens));
                        }
                    } else {
                        System.out.println(option.action(tokens));
                        tokens.clear();
                    }
                }
                if (option == null) {
                    throw new NoSuchOption(nextLine);
                }
            } catch (NoSuchOption ex) {
                System.err.println("Unknown command - " + ex.option);
            }
        } while (nextLine != null);
    }

    static List<String> tokenize(String input) {
        List<String> list = new ArrayList<>(Arrays.asList(input.split(" ")));
        list.removeAll(Arrays.asList("", null));
        return list;
    }

    void buildMenu() {
        List<Option> options = new ArrayList<>();
        Collections.addAll(options, new ServerControls().getOptions((ServerConsoleController) controller));
        options.addAll(controller.mods.stream().filter(mod -> mod instanceof WurmCommandLine).map(mod -> ((WurmCommandLine) mod).getOption(controller)).collect(Collectors.toList()));
        Set<String> set = new HashSet<>();
        for (Option option : options) {
            if (!set.add(option.getName())) {
                // TODO - Reserved words.
                logger.severe(String.format("Duplicate menu item found - %s.", option.getName()));
                System.exit(-1);
            }
        }

        topMenu = new Menu("menu", "Wurm Server Controller - Main Menu", options.toArray(new Option[options.size()]));
        System.out.println(topMenu.action(null));
    }

    public void rebuildMenu() {
        buildMenu();
    }
}
