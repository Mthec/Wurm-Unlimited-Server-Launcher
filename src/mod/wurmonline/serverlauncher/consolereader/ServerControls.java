package mod.wurmonline.serverlauncher.consolereader;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationCallback;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationEnd;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationRequired;
import mod.wurmonline.serverlauncher.consolereader.exceptions.RebuildRequired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ServerControls {
    static ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    List<String> names = new ArrayList<>();

    // TODO - Change to ServerController when gui separation is complete.  Or possibly sooner.
    public Option[] getOptions(ServerConsoleController controller) {
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
                    // TODO - Cannot currently select another server when one is running.
                    if (controller.serverIsRunning()) {
                        return messages.getString("select_when_server_running");
                    } else {
                        try {
                            controller.setCurrentDir(folderName);
                            // TODO - How to wait, only possible when gui separation and rewrite are done?
                            controller.loadAllServers();
                            throw new RebuildRequired();
                        } catch (IOException ex) {
                            return MessageFormat.format(messages.getString("unknown_error"), ex.getMessage());
                        }
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
                public String action(List<String> tokens) throws ConfirmationRequired {
                    final int time;
                    final String reason;

                    if (tokens.size() > 1) {
                        try {
                            time = Integer.parseInt(tokens.get(0));
                            reason = String.join(" ", tokens.subList(1, tokens.size()));
                        } catch (NumberFormatException ex) {
                            return MessageFormat.format(messages.getString("shutdown_time_invalid_number"), tokens.get(0));
                        }
                    } else {
                        time = 0;
                        reason = "";
                    }

                    Map<String, ConfirmationCallback> answers = new HashMap<>();

                    ConfirmationCallback yes = new ConfirmationCallback() {
                        @Override
                        public void call() throws ConfirmationEnd {
                            controller.shutdown(time, reason);
                            // TODO - Replace when gui separation rewrite is complete.
                            System.exit(1);
                            throw new ConfirmationEnd(messages.getString("shutdown_confirm_yes"));
                        }
                    };

                    ConfirmationCallback no = new ConfirmationCallback() {
                        @Override
                        public void call() throws ConfirmationEnd {
                            throw new ConfirmationEnd(messages.getString("shutdown_confirm_no"));
                        }
                    };

                    answers.put("yes", yes);
                    answers.put("no", no);
                    throw new ConfirmationRequired(messages.getString("shutdown_confirm"), answers);

                }
            };
        }
        options[1] = new Command("status", messages.getString("status_text")) {
            @Override
            public String action(List<String> tokens) {
                if (controller.isInitialized()) {
                    // TODO - More information.
                    return MessageFormat.format(messages.getString("status_result"),
                            controller.getCurrentDir(),
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
