package mod.wurmonline.serverlauncher.consolereader.exceptions;

import mod.wurmonline.serverlauncher.LocaleHelper;

import java.util.ResourceBundle;

class BadOptionException extends RuntimeException {
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");

    BadOptionException(String message) {
        super(message);
    }
}