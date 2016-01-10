package mod.wurmonline.serverlauncher.consolereader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ConsoleReader implements Runnable {
    private static Logger logger = Logger.getLogger(ConsoleReader.class.getName());
    Menu topMenu;
    Menu currentMenu = null;

    ConsoleReader (Option[] options) {
        topMenu = new Menu("menu", "Wurm Server Controller - Main Menu", options);
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String nextLine = null;
        do {
            try {
                nextLine = reader.readLine();

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

                // Default commands.
                if (tokens.get(0).equals("help")) {
                    if (tokens.size() == 1) {
                        System.out.println(currentMenu.help());
                    } else {
                        System.out.println(currentMenu.help(tokens.get(1)));
                    }
                    continue;
                } else if (tokens.get(0).equals("list")) {
                    System.out.println(currentMenu.list());
                    continue;
                } else if (tokens.get(0).equals("menu")) {
                    currentMenu = topMenu;
                    System.out.println(currentMenu.action());
                    continue;
                }

                // Other commands.
                Option response = currentMenu.ask(tokens.get(0));
                if (response != null) {
                    if (response instanceof Menu) {
                        currentMenu = (Menu)response;
                    }
                    if (tokens.size() == 1) {
                        System.out.println(response.action());
                    } else {
                        System.out.println(response.action(tokens.subList(1, tokens.size())));
                    }
                    continue;
                }
                throw new NoSuchOption(nextLine);
            } catch (IOException ex) {
                ex.printStackTrace();
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
