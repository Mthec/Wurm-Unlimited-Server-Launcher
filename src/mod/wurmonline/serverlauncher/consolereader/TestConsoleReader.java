package mod.wurmonline.serverlauncher.consolereader;


import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.ServerController;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class TestConsoleReader {
    static Option[] options;
    static ServerController controller;

    public static void buildMenu() {

        options = new Option[] {
                new Menu("dostuff", "This is a menu, please type \"hello\"",
                        new Option[] {
                                new Command("hello", "something") {
                                    @Override
                                    public String action(List<String> tokens) {
                                        return "Hi there.";
                                    }
                                },
                                new Command("bye", "something else") {
                                    @Override
                                    public String action(List<String> tokens) {
                                        return "I SAID TYPE HELLO!";
                                    }
                                }
                        }),
                new Command("status", "Check the status of the server.") {
                    @Override
                    public String action(List<String> tokens) {
                        return "*shrugs*";
                    }
                },
                new Command("shutdown", "Shutdown the server.") {
                    @Override
                    public String action(List<String> tokens) {
                        return "Done!";
                    }
                },
                new Command("player_info", "Get information about a player.") {
                    @Override
                    public String action(List<String> tokens) {
                        if (tokens.isEmpty()) {
                            return "Please provide a player name or id.";
                        } else {
                            String[] strings = new String[] {"annoying", "happy"};
                            Random random = new Random();
                            return String.join(" ", tokens) + " is " + strings[random.nextInt(2)] + ".";
                        }
                    }
                },
                new Menu("controls", "Server Controls", ServerControls.getOptions((ServerConsoleController) controller))
        };
    }

    public static void start(ServerConsoleController controller) {
        TestConsoleReader.controller = controller;
        buildMenu();
        (new Thread(new ConsoleReader(controller, options))).start();
        // TODO - How to tell when ready to receive commands?
    }

    public static void main(String[] args) {
        controller = new FakeController();

        start((ServerConsoleController) controller);
    }

    static class FakeController extends ServerConsoleController {
        boolean running = false;

        @Override
        public void startDB(String dbName) {
            running = true;
            System.out.println("Started server!");
        }

        @Override
        public synchronized boolean shutdown(int time, String reason) {
            // TODO - What if server is not running.
            return true;
        }

        @Override
        public synchronized void setCurrentDir(String newCurrentDir) throws IOException {
            currentDir = newCurrentDir;
            System.out.println(String.format("Dir set to - %s", newCurrentDir));
        }

        @Override
        public boolean serverIsRunning() {
            return running;
        }
    }
}
