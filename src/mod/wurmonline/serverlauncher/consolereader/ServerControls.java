package mod.wurmonline.serverlauncher.consolereader;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerConsoleController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServerControls {
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    List<String> names = new ArrayList<>();

    // TODO - Change to ServerController when gui separation is complete.  Or possibly sooner.
    public Option[] getOptions(ServerConsoleController controller) {
        if (controller == null) {
            // TODO - Temp
            System.err.println("ServerController shouldn't be null!");
            System.exit(-1);
        }
        names.clear();

        List<String> servers = getServersTemp();
        Command[] serverOptions = new Command[servers.size()];
        String server;
        for (int i = 0; i < servers.size(); ++i) {
            server = servers.get(i);
            // TODO - server.getName(), String.format("Name - %s, id - %s", server.getName(), server.getId())
            serverOptions[i] = new Command(getShortenedName(server), server) {
                String folderName = this.help();

                @Override
                public String action(List<String> tokens) throws RebuildRequired {
                    try {
                        controller.setCurrentDir(folderName);
                        throw new RebuildRequired();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // TODO - HELP!
                        // Would happen if folder was deleted after menu creation?
                        // Should rebuild?
                        return "Uh-oh, why did this happen?";
                    }
                }
            };
        }

        Option[] options = new Option[3];
        if (!controller.serverIsRunning()) {
            options[0] = new Command("start", messages.getString("start_text")) {
                @Override
                public String action(List<String> tokens) throws RebuildRequired {
                    if (controller.isInitialized()) {
                        controller.startDB(controller.getCurrentDir());
                        throw new RebuildRequired();
                    }
                    return messages.getString("no_server_selected");
                }
            };
        } else {
            options[0] = new Command("shutdown", messages.getString("shutdown_text")) {
                @Override
                public String action(List<String> tokens) throws RebuildRequired {
                    int time = 0;
                    String reason = "";

                    if (tokens.size() > 1) {
                        try {
                            time = Integer.parseInt(tokens.get(0));
                            reason = String.join(" ", tokens.subList(1, tokens.size()));
                        } catch (NumberFormatException ex) {
                            return MessageFormat.format(messages.getString("shutdown_time_invalid_number"), tokens.get(0));
                        }
                    }
                    // TODO - Confirmation
                    controller.shutdown(time, reason);
                    // TODO - How to handle shutdown until gui separation is completed?
                    throw new RebuildRequired();
                }
            };
        }
        options[1] = new Command("status", messages.getString("status_text")) {
            @Override
            public String action(List<String> tokens) {
                if (controller.isInitialized()) {
                    // TODO - More information.
                    // FIXME - How to add os independent newline in .properties file.
                    return MessageFormat.format(messages.getString("status_result"),
                            controller.getCurrentDir(),
                            System.lineSeparator(),
                            controller.serverIsRunning() ?
                                    messages.getString("status_running") :
                                    messages.getString("status_not_running"));
                } else {
                    return messages.getString("no_server_selected");
                }
            }
        };
        options[2] = new Menu("select", messages.getString("select_text"), serverOptions);
        return options;
    }

    public String getShortenedName(String serverName) {
        String name = serverName.replace(" ", "");
        if (names.contains(name)) {
            int num = 1;
            String newName = name + String.valueOf(num);
            while (names.contains(newName)) {
                ++num;
                newName = name + String.valueOf(num);
            }
            name = newName;
        }
        names.add(name);
        return name;
    }

    // TODO - Replace with servers from controller once gui separation is complete.
    public List<String> getServersTemp() {
        List<String> servers = new ArrayList<>();
        Path currentRelativePath = Paths.get("");
        File currentDirectory = new File(currentRelativePath.toAbsolutePath().toString());

        for (File gameDir : currentDirectory.listFiles()) {
            boolean add = false;
            boolean skip = false;
            if (gameDir.isDirectory()) {

                for (File file : gameDir.listFiles()) {
                    if (!file.isDirectory() && file.getName().equalsIgnoreCase("originaldir")) {
                        skip = true;
                    }
                    if (!file.isDirectory() && file.getName().equalsIgnoreCase("gamedir")) {
                        add = true;
                    }
                }
            }

            if (add && !skip) {
                servers.add(gameDir.getName());
            }
        }
        return servers;
    }
}
