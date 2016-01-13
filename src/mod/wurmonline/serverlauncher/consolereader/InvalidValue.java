package mod.wurmonline.serverlauncher.consolereader;

public class InvalidValue extends Exception {
    String value;
    String message;

    public InvalidValue(String value, String message) {
        this.value = value;
        this.message = message;
    }
}
