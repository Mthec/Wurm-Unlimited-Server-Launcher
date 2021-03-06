package mod.wurmonline.serverlauncher.consolereader;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.confirmation.Confirmation;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationEnd;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationRequired;
import mod.wurmonline.serverlauncher.consolereader.exceptions.DuplicateOptionException;
import mod.wurmonline.serverlauncher.consolereader.exceptions.NoSuchOption;
import mod.wurmonline.serverlauncher.consolereader.exceptions.RebuildRequired;
import mod.wurmonline.serverlauncher.consolereader.exceptions.ReservedOptionException;
import org.gotti.wurmunlimited.modloader.interfaces.WurmCommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO - Add logging.
// TODO - Database controls.
public class ConsoleReader implements Runnable {
    private static Logger logger = Logger.getLogger(ConsoleReader.class.getName());
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    List<String> RESERVED_OPTIONS;
    ServerController controller;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Menu topMenu;
    Menu currentMenu = null;
    Confirmation awaitingConfirmation = null;

    public ConsoleReader(ServerConsoleController controller) {
        RESERVED_OPTIONS = Arrays.asList("help", "list", "menu", "up");
        this.controller = controller;
        buildMenu();
    }

    // For testing.
    ConsoleReader(Option[] options) {
        RESERVED_OPTIONS = Arrays.asList("help", "list", "menu", "up");
        topMenu = new Menu("menu", messages.getString("top_menu_text"), messages.getString("top_menu_help"), options);
        System.out.println(topMenu.action(null));
    }

    @Override
    public void run() {
        String nextLine;
        do {
            try {
                nextLine = reader.readLine();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                return;
            }

            if (nextLine == null || nextLine.isEmpty()) {
                continue;
            }

            // TODO - Help.
            if (awaitingConfirmation != null) {
                try {
                    System.out.println(awaitingConfirmation.action(nextLine));
                } catch (ConfirmationEnd confirmation) {
                    System.out.println(confirmation.message);
                    awaitingConfirmation = null;
                }
            } else {
                try {
                    // TODO - Remove.
                    if (nextLine.equals("exit")) {
                        System.exit(1);
                    }

                    // TODO - Is this needed?
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
                                System.out.println(messages.getString("help"));
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
                                System.err.println(messages.getString("top_menu_reached"));
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
                            currentMenu = ((Menu) option).get();
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
                    System.err.println(MessageFormat.format(messages.getString("option_not_found"), ex.option));
                } catch (ConfirmationRequired ex) {
                    awaitingConfirmation = ex.confirmation;
                    System.out.println(awaitingConfirmation.getText());
                } catch (RebuildRequired ex) {
                    System.out.println(messages.getString("rebuilding"));
                    buildMenu();
                }
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
            if (RESERVED_OPTIONS.contains(option.getName())) {
                throw new ReservedOptionException(option);
            }
            if (!set.add(option.getName())) {
                throw new DuplicateOptionException(option);
            }
        }

        topMenu = new Menu("menu",
                messages.getString("top_menu_text"),
                messages.getString("top_menu_help"),
                options.toArray(new Option[options.size()]));
        currentMenu = topMenu;
        System.out.println("========================================");
        System.out.println(topMenu.action(null));
    }
}
