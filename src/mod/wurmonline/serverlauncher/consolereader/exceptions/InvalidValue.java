package mod.wurmonline.serverlauncher.consolereader.exceptions;

public class InvalidValue extends Exception {
    public String value;
    public String message;

    public InvalidValue(String value, String message) {
        this.value = value;
        this.message = message;
    }
}
