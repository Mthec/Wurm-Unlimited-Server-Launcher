package mod.wurmonline.serverlauncher.consolereader.confirmation;


public class ConfirmationEnd extends Exception {
    public String message;

    public ConfirmationEnd(String message) {
        this.message = message;
    }
}
