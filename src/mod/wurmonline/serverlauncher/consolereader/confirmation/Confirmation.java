package mod.wurmonline.serverlauncher.consolereader.confirmation;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.LocaleHelper;

import java.util.Map;
import java.util.ResourceBundle;

public class Confirmation {
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    String text;
    Map<String, ConfirmationCallback> answers;

    public Confirmation(String text, Map<String, ConfirmationCallback> answers) {
        this.text = text;
        this.answers = answers;
    }

    // TODO - y, yes, etc.
    public String action(String input) throws ConfirmationFinished {
        // TODO - Feedback.
        if (answers.containsKey(input)) {
            answers.get(input).call();
        }
        return MessageFormat.format(messages.getString("option_not_found"), input);
    }

    public String getText() {
        return this.text;
    }
}
