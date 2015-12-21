package mod.wurmonline.serverlauncher.consolereader;

public class TestConsoleReader {
    public static void main (String[] args) {
        Option[] options = {
                new Menu("dostuff", "This is a menu, please type \"hello\"", new Option[] {new Command("hello", "something")}),
                new Command("test", "wheee")
        };
        (new Thread(new ConsoleReader(options))).start();
    }
}
