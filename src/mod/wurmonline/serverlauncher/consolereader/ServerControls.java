package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.ServerConsoleController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerControls {
    static List<String> names = new ArrayList<>();

    // TODO - Change to ServerController when gui separation is complete.  Or possibly sooner.
    public static Option[] getOptions(ServerConsoleController controller) {
        if (controller == null) {
            // TODO - Temp
            System.err.println("ServerController shouldn't be null!");
            System.exit(-1);
        }
        List<String> servers = getServersTemp();
        Command[] serverOptions = new Command[servers.size()];
        String server;
        for (int i = 0; i < servers.size(); ++i) {
            server = servers.get(i);
            // TODO - server.getName(), String.format("Name - %s, id - %s", server.getName(), server.getId())
            serverOptions[i] = new Command(getShortenedName(server), server) {
                String folderName = this.help();
                @Override
                public String action(List<String> tokens) {
                    try {
                        controller.setCurrentDir(folderName);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // HELP!
                    }
                    // Should this respond at all?
                    return folderName + " selected";
                }
            };
        }

        return new Option[] {
                new Command("start", "Start the currently selected server.") {
                    @Override
                    public String action(List<String> tokens) {
                        controller.startDB(controller.getCurrentDir());
                        return "";
                    }
                },
                new Command("shutdown", "Shutdown currently selected server.") {
                    @Override
                    public String action(List<String> tokens) {
                        // TODO - Options
                        // TODO - Confirmation
                        controller.shutdown(0, "");
                        return "";
                    }
                },
                new Command("status", "Status of currently selected server.") {
                    @Override
                    public String action(List<String> tokens) {
                        if (!controller.getCurrentDir().equals("")) {
                            // TODO - More information.
                            return String.format(
                                    "Name - %s" + System.lineSeparator() + "%s",
                                    controller.getCurrentDir(),
                                    controller.serverIsRunning() ? "Server is running." : "Server is not running.");
                        } else {
                            return "No server selected.";
                        }
                    }
                },
                new Menu("select", "Select a server.", serverOptions),
        };
    }

    public static String getShortenedName(String serverName) {
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
    public static List<String> getServersTemp() {
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
