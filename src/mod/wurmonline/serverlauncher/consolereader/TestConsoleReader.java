package mod.wurmonline.serverlauncher.consolereader;

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
        };
        (new Thread(new ConsoleReader(options))).start();
    }
}
