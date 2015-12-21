package mod.wurmonline.serverlauncher.consolereader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ConsoleReader implements Runnable {
    private static Logger logger = Logger.getLogger(ConsoleReader.class.getName());
    Menu topMenu;
    Menu currentMenu = null;

    ConsoleReader (Option[] options) {
        topMenu = new Menu(null, null, options);
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

                if (nextLine.equals("help")) {
                    System.out.println(currentMenu.help());
                    continue;
                } else if (nextLine.startsWith("help ")) {
                    System.out.println(currentMenu.help(nextLine.substring(5)));
                    continue;
                } else if (nextLine.equals("menu")) {
                    currentMenu = topMenu;
                    currentMenu.action();
                    continue;
                }

                Option response = currentMenu.ask(nextLine);
                if (response == null) {
                    System.err.println("Unknown command - " + nextLine);
                } else {
                    if (response instanceof Menu) {
                        currentMenu = (Menu)response;
                    }
                    System.out.println(response.action());
                }
                throw new NoSuchOption(nextLine);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NoSuchOption ex) {
                System.err.println("Unknown command - " + ex.option);
            }
        } while (nextLine != null);
    }
}
