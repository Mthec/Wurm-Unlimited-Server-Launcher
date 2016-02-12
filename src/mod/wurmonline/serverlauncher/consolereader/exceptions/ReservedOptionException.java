package mod.wurmonline.serverlauncher.consolereader.exceptions;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.consolereader.Option;

public class ReservedOptionException extends BadOptionException {
    public ReservedOptionException(Option option) {
        super(MessageFormat.format(messages.getString("reserved_option"), option.getName()));
    }
}
