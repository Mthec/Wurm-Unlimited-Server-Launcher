package mod.wurmonline.serverlauncher.consolereader;

import java.util.List;

public interface Option {

    String action(List<String> tokens);

    Option getOption(String input) throws NoSuchOption;

    String help();

    String getName();
}
