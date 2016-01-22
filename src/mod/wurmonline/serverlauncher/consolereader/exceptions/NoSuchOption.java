package mod.wurmonline.serverlauncher.consolereader.exceptions;

public class NoSuchOption extends Exception {
    public String option;

    public NoSuchOption(String option) {
        this.option = option;
    }
}
