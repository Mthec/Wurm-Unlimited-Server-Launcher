package mod.wurmonline.serverlauncher.consolereader.confirmation;

public abstract class ConfirmationCallback {
    public abstract void call() throws ConfirmationFinished;
}
