package mod.wurmonline.serverlauncher.consolereader;

public interface Option {

    String action ();

    Option ask (String input);

    String help ();

    String getName ();
}
