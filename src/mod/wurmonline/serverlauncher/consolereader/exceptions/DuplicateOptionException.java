package mod.wurmonline.serverlauncher.consolereader.exceptions;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.consolereader.Option;

public class DuplicateOptionException extends BadOptionException {
    public DuplicateOptionException(Option option) {
        super(MessageFormat.format(messages.getString("duplicate_option"), option.getName()));
    }
}