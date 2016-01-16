package mod.wurmonline.serverlauncher;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.*;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.utils.SimpleArgumentParser;
import javafx.fxml.FXML;
import org.gotti.wurmunlimited.modloader.interfaces.WurmArgsMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import sun.management.VMManagement;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ServerController {
    protected Logger logger = Logger.getLogger(ServerController.class.getName());
    public List<WurmMod> mods;
    public SimpleArgumentParser arguments;
    protected List<ServerEntry> localServers = new ArrayList<>();
    protected List<ServerEntry> remoteServers = new ArrayList<>();
    public ServerLauncher currentServer;
    protected String currentDir = "";
    protected boolean rebuilding = false;
    public ResourceBundle server_messages = LocaleHelper.getBundle("ServerController");

    static String CURRENTDIR = "currentdir";
    static String GAMEDIR = "gamedir";
    static String WURM_INI = "wurm.ini";
    static String ORIGINALDIR = "originaldir";
    static String BACKUP_SIGNATURE = "_backup";
    static String adminPassword = "";

    public class CreateServerException extends Exception {
        String errorReason;
        String errorMessage;

        CreateServerException(String newReason, String newMessage) {
            errorMessage = newMessage;
            errorReason = newReason;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getErrorReason() {
            return errorReason;
        }
    }

    public class DeleteServerException extends Exception {
        String errorReason;
        String errorMessage;

        DeleteServerException(String newReason, String newMessage) {
            errorMessage = newMessage;
            errorReason = newReason;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getErrorReason() {
            return errorReason;
        }
    }

    // Should go in init?
    public void setMods(List<WurmMod> loadedMods) {
        mods = loadedMods;
    }

    public void setArguments(SimpleArgumentParser parser) {
        arguments = parser;
        for (WurmMod mod : mods) {
            if (mod instanceof WurmArgsMod) {
                ((WurmArgsMod) mod).parseArgs(this);
            }
        }

        if (arguments.hasOption("adminpwd")) {
            String adminPass = arguments.getOptionValue("adminpwd");
            if (adminPass != null && !adminPass.isEmpty()) {
                adminPassword = adminPass;
            } else {
                System.err.println(server_messages.getString("admin_password_not_set"));
            }
        }
    }

    public boolean shutdown(int time, String reason) {
        if (serverIsRunning()) {
            if (!askConfirmation(server_messages.getString("shutdown_confirm_title"),
                    server_messages.getString("shutdown_confirm_header"),
                    server_messages.getString("shutdown_confirm_message"))) {
                return false;
            }
            // TODO - Shutdown messages should be coloured?
            System.out.println(MessageFormat.format(LocaleHelper.getPluralString(server_messages, "shutting_down.", time), time, reason));
            currentServer.getServer().startShutdown(time, reason);
        }
        return true;
    }

    // TODO - Is this needed?
    public final int getPid() {
        try {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            VMManagement management = (VMManagement) jvm.get(runtime);
            Method pid_method = management.getClass().getDeclaredMethod("getProcessId");
            pid_method.setAccessible(true);
            return (int) (Integer) pid_method.invoke(management);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static void copyFolder(File src, File dest) throws IOException {
        int length;
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String[] in = src.list();

            for (String out : in) {
                File srcFile = new File(src, out);
                File destFile = new File(dest, out);
                copyFolder(srcFile, destFile);
            }
        } else if (!src.getName().equals(CURRENTDIR) && !src.getName().equals(ORIGINALDIR)) {
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dest);
            byte[] b = new byte[1024];

            while ((length = inStream.read(b)) > 0) {
                outStream.write(b, 0, length);
            }

            inStream.close();
            outStream.close();
        }

    }

    void delete(File fileName) throws IOException {
        if (fileName.isDirectory()) {
            for (File file : fileName.listFiles()) {
                delete(file);
            }
        }

        if (!fileName.delete()) {
            throw new FileNotFoundException(MessageFormat.format(server_messages.getString("file_not_found"), fileName));
        }
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String newCurrentDir) throws IOException {
        if (newCurrentDir.equals(currentDir)) {
            return;
        }
        boolean ok = false;
        File gameDir = new File(newCurrentDir);
        if (gameDir.isDirectory()) {
            for (File file : gameDir.listFiles()) {
                if (!file.isDirectory() && file.getName().equalsIgnoreCase("gamedir")) {
                    ok = true;
                    break;
                }
            }
        }
        if (ok) {
            String oldCurrentDir = currentDir;
            File oldFile = new File(currentDir + File.separator + "currentdir");
            oldFile.delete();
            File newFile = new File(newCurrentDir + File.separator + "currentdir");
            try {
                newFile.createNewFile();
            } catch (IOException ex) {
                try {
                    ex.printStackTrace();
                    newFile.delete();
                    oldFile.createNewFile();
                    currentDir = oldCurrentDir;
                    DbConnector.closeAll();
                    ServerDirInfo.setConstantsFileName(currentDir + File.separator + WURM_INI);
                } catch (IOException ex2) {
                    System.out.println(server_messages.getString("error_on_undo"));
                    ex2.printStackTrace();
                }
            }
            currentDir = newCurrentDir;
            ServerDirInfo.setFileDBPath(currentDir + (currentDir.endsWith(File.separator) ? "" : File.separator));
            ServerDirInfo.setConstantsFileName(ServerDirInfo.getFileDBPath() + WURM_INI);
            if (currentDir != null && currentDir.length() > 0) {
                Constants.load();
                Constants.dbHost = currentDir;
                Constants.dbPort = "";
                Constants.loginDbHost = currentDir;
                Constants.loginDbPort = "";
                Constants.siteDbHost = currentDir;
                Constants.siteDbPort = "";
                DbConnector.closeAll();
                DbConnector.initialize();
            }
            logger.info(MessageFormat.format(server_messages.getString("current_dir_set_to"), currentDir));
            return;
        }
        logger.warning(MessageFormat.format(server_messages.getString("current_dir_set_fail"), currentDir));
    }

    public void startServer() throws IOException {
        if (currentServer != null) {
            if (currentServer.wasStarted()) {
                logger.info(server_messages.getString("already_started"));
                return;
            }
        } else {
            currentServer = new ServerLauncher();
        }
        try {
            System.out.println(MessageFormat.format(server_messages.getString("starting"), Servers.localServer.getName()));
            LocateRegistry.createRegistry(Servers.localServer.REGISTRATION_PORT);
            ServerProperties.setValue("ADMINPASSWORD", adminPassword);
            currentServer.runServer(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.out.println("\n==================================================================\n");
            System.out.println(MessageFormat.format(server_messages.getString("launcher_finished"), new Date()));
            System.out.println("\n==================================================================\n");
        }
    }

    // TODO - Bit long winded, does it need to be this way?
    public static short getNewServerId() {
        Random random = new Random();
        int newRand = 0;
        HashSet<Integer> usedNumbers = new HashSet<>();

        for (ServerEntry tries : Servers.getAllServers()) {
            usedNumbers.add(tries.id);
        }

        int min = 0;
        int max = 30000;
        usedNumbers.add(0);

        while (usedNumbers.contains(newRand) && min < max) {
            newRand = random.nextInt(32767);
            if (!usedNumbers.contains(newRand)) {
                break;
            }
            ++min;
        }

        return (short) newRand;
    }

    public void addDatabase(String dbName, String serverName) throws CreateServerException {
        File orig = new File(currentDir);
        if (orig.exists()) {
            File newFile = new File(dbName);
            if (!newFile.exists()) {
                newFile.mkdir();
                try {
                    copyFolder(orig, newFile);
                    setCurrentDir(newFile.getName());
                } catch (IOException ex) {
                    throw new CreateServerException(server_messages.getString("error_copy_header"), MessageFormat.format(server_messages.getString("error_copy_message"), ex.getMessage()));
                }
                loadAllServers(true);
                localServers.addAll(remoteServers);
                for (ServerEntry server : localServers) {
                    deleteServer(server, false);
                }

                ServerEntry newServer = addServer(serverName);
                Servers.registerServer(newServer.id, newServer.getName(), newServer.HOMESERVER, newServer.SPAWNPOINTJENNX, newServer.SPAWNPOINTJENNY, newServer.SPAWNPOINTLIBX, newServer.SPAWNPOINTLIBY, newServer.SPAWNPOINTMOLX, newServer.SPAWNPOINTMOLY, newServer.INTRASERVERADDRESS, newServer.INTRASERVERPORT, newServer.INTRASERVERPASSWORD, newServer.EXTERNALIP, newServer.EXTERNALPORT, newServer.LOGINSERVER, newServer.KINGDOM, newServer.ISPAYMENT, newServer.getConsumerKey(), newServer.getConsumerSecret(), newServer.getApplicationToken(), newServer.getApplicationSecret(), newServer.isLocal, newServer.testServer, newServer.randomSpawns);
            } else {
                throw new CreateServerException(server_messages.getString("error_copy_exists_header"), server_messages.getString("error_copy_exists_message"));
            }
        } else {
            throw new CreateServerException(server_messages.getString("error_copy_not_exists_header"), server_messages.getString("error_copy_not_exists_message"));
        }
    }

    public boolean deleteDatabase() throws DeleteServerException {
        boolean confirm = askConfirmation(server_messages.getString("delete_db_confirm_title"),
                server_messages.getString("delete_db_confirm_header"),
                server_messages.getString("delete_db_confirm_message"));
        if (confirm) {
            File orig = new File(currentDir);
            if (orig.exists()) {
                DbConnector.closeAll();

                for (String fileName : orig.list()) {
                    if (fileName.equalsIgnoreCase(ORIGINALDIR)) {
                        throw new DeleteServerException(server_messages.getString("error_delete_db_protected_header"),
                                server_messages.getString("error_delete_db_protected_message"));
                    }
                }
                try {
                    delete(orig);
                } catch (IOException ex) {
                    throw new DeleteServerException(server_messages.getString("error_delete_db_fail_header"),
                            MessageFormat.format(server_messages.getString("error_delete_db_fail_message"), ex.getMessage()));
                }
                currentDir = "";
            }
        }
        return confirm;
    }

    public void copyCurrentDatabase(String dbName) throws CreateServerException {
        File orig = new File(currentDir);
        if (orig.exists()) {
            File newFile = new File(dbName);
            if (!newFile.exists()) {
                newFile.mkdir();
                try {
                    copyFolder(orig, newFile);
                    setCurrentDir(newFile.getName());
                } catch (IOException ex) {
                    throw new CreateServerException(server_messages.getString("error_copy_db_header"),
                            MessageFormat.format(server_messages.getString("error_copy_db_message"), ex.getMessage()));
                }
            } else {
                throw new CreateServerException(server_messages.getString("error_copy_db_exists_header"),
                        server_messages.getString("error_copy_db_exists_message"));
            }
        } else {
            throw new CreateServerException(server_messages.getString("error_copy_db_not_exists_header"),
                    server_messages.getString("error_copy_db_not_exists_message"));
        }
    }

    public ServerEntry addServer(String name) throws CreateServerException {
        // TODO - Remove/replace - Why is it random?
        short newRand = getNewServerId();
        if (newRand > 0) {
            ServerEntry server = new ServerEntry();
            server.isCreating = true;
            server.name = name;
            server.id = newRand;
            if (Servers.localServer != null) {
                server.EXTERNALIP = Servers.localServer.EXTERNALIP;
                server.EXTERNALPORT = Servers.localServer.EXTERNALPORT;
                server.INTRASERVERADDRESS = Servers.localServer.INTRASERVERADDRESS;
                server.INTRASERVERPORT = Servers.localServer.INTRASERVERPORT;
                server.INTRASERVERPASSWORD = Servers.localServer.INTRASERVERPASSWORD;
                server.RMI_PORT = Servers.localServer.RMI_PORT;
                server.REGISTRATION_PORT = Servers.localServer.REGISTRATION_PORT;
                remoteServers.add(server);

            } else {
                server.EXTERNALPORT = "3724";
                server.INTRASERVERPORT = "48010";
                server.RMI_PORT = 7220;
                server.REGISTRATION_PORT = 7221;
                server.isLocal = true;
                server.LOGINSERVER = true;
                server.SPAWNPOINTJENNX = 200;
                server.SPAWNPOINTJENNY = 200;
                Servers.localServer = server;
                localServers.add(server);
            }
            logger.info(MessageFormat.format(server_messages.getString("server_created"), server.getName()));
            return server;
        }
        // TODO - Why would this happen?
        throw new CreateServerException("Uh-oh", "No, seriously, I don't know what happened.");
    }

    @FXML
    public boolean deleteServer(ServerEntry server) {
        return deleteServer(server, true);
    }

    public boolean deleteServer(ServerEntry server, boolean askConfirm) {
        boolean confirm = true;
        if (askConfirm) {
            confirm = askConfirmation(server_messages.getString("delete_server_confirm_title"),
                    server_messages.getString("delete_server_confirm_header"),
                    server_messages.getString("delete_server_confirm_message"));
        }
        if (confirm) {
            Servers.deleteServerEntry(server.getId());
            Servers.localServer = null;
            // TODO - Any more values like this that need to be unset?
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                // TODO - Should probably move, but 0 doesn't seem to work.
                ps = dbcon.prepareStatement("DELETE FROM PLAYERS WHERE CURRENTSERVER=?");
                ps.setInt(1, server.id);
                ps.executeUpdate();
            } catch (SQLException ex) {
                logger.log(Level.WARNING, server_messages.getString("load_players_fail"), ex);
            } finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        return confirm;
    }

    protected void loadAllServers(boolean reload) {
        localServers.clear();
        remoteServers.clear();
        if (!serverIsRunning()) {
            // TODO - Not sure why this doesn't happen in loadAllServers.  Maybe I've misunderstood the Servers class?
            Servers.localServer = null;
            Servers.loadAllServers(reload);
        } else {
            Servers.loadAllServers(false);
        }
        for (ServerEntry server : Servers.getAllServers()) {
            if (server.isLocal) {
                localServers.add(server);
            } else {
                remoteServers.add(server);
            }
        }

    }

    public boolean serverIsRunning() {
        // TODO - Is there not a better way to do this?
        return currentServer != null && currentServer.wasStarted();
    }

    public synchronized boolean isInitialized() {
        return currentDir.equals("");
    }

    protected void setup() {
        DbConnector.setSqlite(true);
        if (!locateCurrentDir()) {
            String error = createStartDirs();
            if (error != null && error.length() > 0) {
                logger.warning(server_messages.getString("starting_directories_not_created"));
                logger.warning(error);
                System.exit(1);
            }
        }
    }

    boolean locateCurrentDir() {
        File startDir = new File(".");

        for (File dir : startDir.listFiles()) {
            if (dir.isDirectory()) {
                boolean isCurrent = false;
                boolean isGameDir = false;
                boolean isOrig = false;

                for (File file : dir.listFiles()) {
                    if (!file.isDirectory() && file.getName().equalsIgnoreCase(GAMEDIR)) {
                        isGameDir = true;
                    }

                    if (!file.isDirectory() && file.getName().equalsIgnoreCase(CURRENTDIR)) {
                        isCurrent = true;
                    }

                    if (!file.isDirectory() && file.getName().equalsIgnoreCase(ORIGINALDIR)) {
                        isOrig = true;
                    }
                }

                if (isGameDir && isCurrent) {
                    if (!isOrig) {
                        try {
                            setCurrentDir(dir.getName());
                        } catch (IOException ex) {
                            return false;
                        }
                        logger.info(MessageFormat.format(server_messages.getString("found_game_dir"), dir.getName(), currentDir));
                        return true;
                    }

                    logger.info(server_messages.getString("ignore_original"));
                }
            }
        }
        return false;
    }

    String createStartDirs() {
        File startDir = new File(".");
        logger.info(server_messages.getString("creating_starting_directories"));
        for (File dir : startDir.listFiles()) {
            if (dir.isDirectory()) {
                boolean isOrig = false;
                boolean isGameDir = false;
                for (File newFile : dir.listFiles()) {
                    if (!newFile.isDirectory() && newFile.getName().equalsIgnoreCase(GAMEDIR)) {
                        isGameDir = true;
                    }

                    if (!newFile.isDirectory() && newFile.getName().equalsIgnoreCase(ORIGINALDIR)) {
                        isOrig = true;
                    }
                }

                if (isGameDir && isOrig && dir.getName().toLowerCase().contains(BACKUP_SIGNATURE)) {
                    File newFile = new File(dir.getName().substring(0, dir.getName().indexOf(BACKUP_SIGNATURE)));
                    if (newFile.exists()) {
                        if (newFile.isFile()) {
                            if (askYesNo(server_messages.getString("overwrite_confirm_title"),
                                    server_messages.getString("overwrite_confirm_header"),
                                    MessageFormat.format(server_messages.getString("overwrite_confirm_message"), newFile.getName()))) {
                                continue;
                            }

                            if (!newFile.delete()) {
                                return MessageFormat.format(server_messages.getString("error_cannot_delete"), newFile.getAbsolutePath());
                            }
                        } else {
                            if (askYesNo(server_messages.getString("overwrite_dir_confirm_title"),
                                    server_messages.getString("overwrite_dir_confirm_header"),
                                    MessageFormat.format(server_messages.getString("overwrite_dir_confirm_message"), newFile.getName()))) {
                                continue;
                            }

                            File origDir = new File(newFile.getName() + File.separator + ORIGINALDIR);
                            if (origDir.exists() && !origDir.delete()) {
                                return MessageFormat.format(server_messages.getString("error_cannot_delete_marker"), origDir.getAbsolutePath());
                            }
                        }
                    }

                    try {
                        copyFolder(dir, newFile);
                        if (dir.getName().toLowerCase().contains("adventure")) {
                            setCurrentDir(newFile.getName());
                        }
                    } catch (IOException ex) {
                        return MessageFormat.format(server_messages.getString("error_create_dirs"), ex.getMessage());
                    }
                    logger.info(MessageFormat.format(server_messages.getString("created_game_dir"), newFile.getName()));
                }
            }
        }
        return "";
    }

    // TODO What about cancel?
    protected abstract boolean askConfirmation(String title, String header, String content);

    protected abstract boolean askYesNo(String title, String header, String content);
}
