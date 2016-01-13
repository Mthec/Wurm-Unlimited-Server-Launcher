package mod.wurmonline.serverlauncher.consolereader;

import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.Servers;
import mod.wurmonline.serverlauncher.ServerController;

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
            ServerEntry current = Servers.localServer;
            if (tokens.isEmpty()) {
                return this.getName() + " is currently - " + this.get(current);
            } else {
                if (controller != null && controller.serverIsRunning()) {
                    return "Server is running, values cannot be changed.";
                }
                // TODO - Exceptions?
                try {
                    this.set(current, tokens);
                } catch (InvalidValue ex) {
                    return "Invalid value - " + ex.value + " " + ex.message;
                }
                return this.getName() + " set to - " + this.get(current);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    protected abstract String get(ServerEntry server);

    protected abstract void set(ServerEntry server, List<String> tokens) throws InvalidValue;
}
