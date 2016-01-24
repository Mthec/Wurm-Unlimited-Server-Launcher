package mod.wurmonline.serverlauncher.consolereader.confirmation;


// TODO - Rename.
public class ConfirmationFinished extends Exception {
    public String message;

    public ConfirmationFinished(String message) {
        this.message = message;
    }
}
