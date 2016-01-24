package mod.wurmonline.serverlauncher.consolereader;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.ServerEntry;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.exceptions.InvalidValue;

import java.util.List;

public abstract class Value extends Command {
    ServerController controller;

    public Value(String newName, String newHelpText) {
        super(newName, newHelpText);
    }

    public Value(String newName, String newHelpText, ServerController controller) {
        super(newName, newHelpText);
        this.controller = controller;
    }

    @Override
    public String action(List<String> tokens) {
        try {
            // TODO - Change to get server when gui separation is complete.
            ServerEntry current = null;
            if (controller != null) {
                if (!controller.isInitialized()) {
                    return messages.getString("no_server_selected");
                }
                current = controller.getLocalServer();
                if (current == null) {
                    return messages.getString("no_server_found");
                }
            }
            if (tokens.isEmpty()) {
                return MessageFormat.format(messages.getString("current_value"), this.getName(), this.get(current));
            } else {
                if (controller != null && controller.serverIsRunning()) {
                    return messages.getString("server_running");
                }
                // TODO - Exceptions?
                try {
                    // TODO - Tests and user documentation.
                    if (tokens.get(0).equals("set")) {
                        tokens.remove(0);
                        if (tokens.isEmpty()) {
                            tokens.add("");
                        }
                    }
                    this.set(current, tokens);
                } catch (InvalidValue ex) {
                    return MessageFormat.format(messages.getString("invalid_value"), ex.value, ex.message);
                }
                return MessageFormat.format(messages.getString("set_value"), this.getName(), this.get(current));
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    protected abstract String get(ServerEntry server);

    protected abstract void set(ServerEntry server, List<String> tokens) throws InvalidValue;
}
