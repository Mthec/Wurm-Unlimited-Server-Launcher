package mod.wurmonline.serverlauncher.consolereader;

import java.util.List;

public interface Option {

    String action ();

    default String action (List<String> tokens) {
        return "Does not take any options.";
    }

    Option ask (String input);

    String help ();

    String getName ();
}
