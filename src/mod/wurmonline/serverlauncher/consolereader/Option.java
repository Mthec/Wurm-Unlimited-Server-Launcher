package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationRequired;
import mod.wurmonline.serverlauncher.consolereader.exceptions.NoSuchOption;
import mod.wurmonline.serverlauncher.consolereader.exceptions.RebuildRequired;

import java.util.List;

public interface Option {

    String action(List<String> tokens) throws RebuildRequired, ConfirmationRequired;

    Option getOption(String input) throws NoSuchOption;

    String help();

    String getName();
}
