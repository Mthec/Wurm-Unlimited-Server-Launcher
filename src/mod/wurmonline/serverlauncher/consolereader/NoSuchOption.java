package mod.wurmonline.serverlauncher.consolereader;

public class NoSuchOption extends Exception {
    String option;

    NoSuchOption (String option) {
        this.option = option;
    }
}
