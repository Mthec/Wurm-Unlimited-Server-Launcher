package mod.wurmonline.serverlauncher.consolereader;

import java.util.List;
import java.util.Random;

public class TestConsoleReader {
    public static void main (String[] args) {
        Option[] options = {
                new Menu("dostuff", "This is a menu, please type \"hello\"",
                        new Option[] {
                                new Command("hello", "something"){
                                    @Override
                                    public String action() {
                                        return "Hi there.";
                                    }
                                },
                                new Command("bye", "something else") {
                                    @Override
                                    public String action() {
                                        return "I SAID TYPE HELLO!";
                                    }
                                }
                        }),
                new Command("status", "Check the status of the server."){
                    @Override
                    public String action () {
                        return "*shrugs*";
                    }
                },
                new Command("shutdown", "Shutdown the server."){
                    @Override
                    public String action () {

                        return "Done!";
                    }
                },
                new Command("player_info", "Get information about a player.") {
                    @Override
                    public String action() {
                        return "Please provide a player name or id.";
                    }

                    @Override
                    public String action(List<String> tokens) {
                        if (tokens.isEmpty()) {
                            return action();
                        } else {
                            String[] strings = new String[]{"annoying", "happy"};
                            Random random = new Random();
                            return String.join(" ", tokens) + " is " + strings[random.nextInt(2)] + ".";
                        }
                    }
                },
        };
        (new Thread(new ConsoleReader(options))).start();
    }
}
