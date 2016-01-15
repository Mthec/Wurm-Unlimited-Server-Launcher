package mod.wurmonline.serverlauncher.consolereader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// TODO - Convert strings to locales.
// TODO - Add logging.
// TODO - Create ServerControls.
public class ConsoleReader implements Runnable {
    private static Logger logger = Logger.getLogger(ConsoleReader.class.getName());
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Menu topMenu;
    Menu currentMenu = null;

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
}
