package mod.wurmonline.serverlauncher.consolereader.confirmation;

import java.util.Map;

public class ConfirmationRequired extends Exception {
    public Confirmation confirmation;

    public ConfirmationRequired(String text, Map<String, ConfirmationCallback> answers) {
        this.confirmation = new Confirmation(text, answers);
    }
}
