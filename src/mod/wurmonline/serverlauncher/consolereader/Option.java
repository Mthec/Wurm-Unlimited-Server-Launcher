package mod.wurmonline.serverlauncher.consolereader;

import java.util.List;

public interface Option {

    String action ();

    default String action (List<String> tokens) {
        return getName() + " does not take any options.";
    }

    Option ask (String input) throws NoSuchOption;

    String help ();

    String getName ();
}
