package mod.wurmonline.serverlauncher;

import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;
import com.wurmonline.server.Constants;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.ServerDirInfo;
import com.wurmonline.server.Servers;
import com.wurmonline.server.utils.SimpleArgumentParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;
import org.gotti.wurmunlimited.modloader.ModLoader;
import org.gotti.wurmunlimited.modloader.ServerHook;
import org.gotti.wurmunlimited.modloader.interfaces.WurmArgsMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmLoadDumpMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain extends Application {
    static Logger logger = Logger.getLogger(ServerMain.class.getName());
    static String MODS_DIR = "";
    static List<WurmMod> mods;
    static Set<String> ACCEPTED_ARGS;
    public static String ARG_START = "start";
    public static String ARG_QUERY_PORT = "queryport";
    public static String ARG_INTERNAL_PORT = "internalport";
    public static String ARG_EXTERNAL_PORT = "externalport";
    public static String ARG_IP_ADDRESS = "ip";
    public static String ARG_RMI_REG = "rmiregport";
    public static String ARG_RMI_PORT = "rmiport";
    public static String ARG_SERVER_PASS = "serverpassword";
    public static String ARG_PLAYER_NUM = "maxplayers";
    public static String ARG_LOGIN_SERVER = "loginserver";
    public static String ARG_PVP = "pvp";
    public static String ARG_HOME_SERVER = "homeserver";
    public static String ARG_HOME_KINGDOM = "homekingdom";
    public static String ARG_EPIC_SETTINGS = "epicsettings";
    public static String ARG_SERVER_NAME = "servername";
    public static String ADMIN_PWD = "adminpwd";
    public static String DUMP_SETTINGS = "dumpsettings";
    public static String LOAD_SETTINGS = "loadsettings";
    public static String SERVER_SETTINGS_FILE = "/server_settings.properties";
    static SimpleArgumentParser parser;
    static ResourceBundle main_messages = LocaleHelper.getBundle("ServerMain");

    static {
        MODS_DIR = "mods";
        HashSet<String> acceptedArgs = new HashSet<>(1);
        acceptedArgs.add(ARG_START);
        acceptedArgs.add(ARG_QUERY_PORT);
        acceptedArgs.add(ARG_INTERNAL_PORT);
        acceptedArgs.add(ARG_EXTERNAL_PORT);
        acceptedArgs.add(ARG_IP_ADDRESS);
        acceptedArgs.add(ARG_RMI_REG);
        acceptedArgs.add(ARG_RMI_PORT);
        acceptedArgs.add(ARG_SERVER_PASS);
        acceptedArgs.add(ARG_PLAYER_NUM);
        acceptedArgs.add(ARG_LOGIN_SERVER);
        acceptedArgs.add(ARG_PVP);
        acceptedArgs.add(ARG_HOME_SERVER);
        acceptedArgs.add(ARG_HOME_KINGDOM);
        acceptedArgs.add(ARG_EPIC_SETTINGS);
        acceptedArgs.add(ARG_SERVER_NAME);
        acceptedArgs.add(ADMIN_PWD);
        acceptedArgs.add(DUMP_SETTINGS);
        acceptedArgs.add(LOAD_SETTINGS);
        ACCEPTED_ARGS = acceptedArgs;
    }

    static Properties loadProperties (String currentDir) {
        if (!Files.exists(Paths.get(currentDir))) {
            logger.warning(MessageFormat.format(main_messages.getString("db_not_exists"), currentDir));
            System.exit(-1);
        }
        DbConnector.closeAll();
        ServerDirInfo.setFileDBPath(currentDir + (currentDir.endsWith(File.separator) ? "" : File.separator));
        ServerDirInfo.setConstantsFileName(ServerDirInfo.getFileDBPath() + "wurm.ini");
        Constants.load();
        Constants.dbHost = currentDir;
        Constants.dbPort = "";
        Constants.loginDbHost = currentDir;
        Constants.loginDbPort = "";
        Constants.siteDbHost = currentDir;
        Constants.siteDbPort = "";
        DbConnector.closeAll();
        DbConnector.initialize();
        Servers.loadAllServers(true);
        Properties properties = new Properties();

        String filePath = currentDir + SERVER_SETTINGS_FILE;


        try {
            FileInputStream stream = new FileInputStream(filePath);
            try {
                properties.load(stream);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        } catch (FileNotFoundException fnf) {
            return null;
        }

        return properties;
    }

    public static void main(String[] args) {
        logger.info(main_messages.getString("starting"));
        // By ago.
        try {
            mods = new ModLoader().loadModsFromModDir(Paths.get(MODS_DIR));
            ServerHook.createServerHook().addMods(mods);
            for (WurmMod mod : mods) {
                if (mod instanceof WurmArgsMod) {
                    WurmArgsMod argMod = (WurmArgsMod)mod;
                    ACCEPTED_ARGS.addAll(argMod.getArgs());
                }
            }
            PluralRules pluralRules = PluralRules.forLocale(ULocale.getDefault());
            int num_of_mods = mods.size();
            String resourceKey = "number_of_mods." + pluralRules.select(num_of_mods);
            logger.info(MessageFormat.format(main_messages.getString(resourceKey), num_of_mods));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        logger.info(main_messages.getString("accepted_arguments") + " - " + ACCEPTED_ARGS.toString());


        parser = new SimpleArgumentParser(args, ACCEPTED_ARGS);
        if (parser.hasOption("dumpsettings")) {
            String database = parser.getOptionValue("dumpsettings");
            if (database == null || database.equals("")) {
                logger.warning(main_messages.getString("database_blank"));
                System.exit(-1);
            }
            Properties properties = loadProperties(database);
            if (properties == null) {
                properties = new Properties();
            }
            properties.clear();
            String filePath = database + SERVER_SETTINGS_FILE;

            try {
                try (FileOutputStream stream = new FileOutputStream(filePath)) {
                    properties.store(stream, MessageFormat.format(main_messages.getString("settings_comment"), database));

                    for (WurmMod mod : mods) {
                        if (mod instanceof WurmLoadDumpMod) {
                            WurmLoadDumpMod ldMod = (WurmLoadDumpMod) mod;
                            properties.putAll(ldMod.dumpSettings());
                            properties.store(stream, ldMod.getComment());
                            properties.clear();
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
            DbConnector.closeAll();
            logger.info(MessageFormat.format(main_messages.getString("dumped_settings"), database, filePath));
            System.exit(1);
        }
        else if (parser.hasOption("loadsettings")) {
            String database = parser.getOptionValue("loadsettings");
            if (database == null || database.equals("")) {
                logger.warning(main_messages.getString("database_blank"));
                System.exit(-1);
            }
            Properties properties = loadProperties(database);
            String filePath = database + SERVER_SETTINGS_FILE;
            if (properties != null) {
                for (WurmMod mod : mods) {
                    if (mod instanceof WurmLoadDumpMod) {
                        WurmLoadDumpMod ldMod = (WurmLoadDumpMod) mod;
                        ldMod.loadSettings(properties);
                    }
                }
                DbConnector.closeAll();
                logger.info(MessageFormat.format(main_messages.getString("loaded_settings"), database, filePath));
            }
            else {
                logger.warning(MessageFormat.format(main_messages.getString("settings_file_not_found"), filePath));
            }
        }

        Servers.argumets = parser;
        String dbToStart = "";
        if(parser.hasOption(ARG_START)) {
            dbToStart = parser.getOptionValue(ARG_START);
            if(dbToStart == null || dbToStart.isEmpty()) {
                System.err.println(main_messages.getString("no_start_parameter"));
                System.exit(1);
            }
        }

        if(!dbToStart.isEmpty()) {
            System.out.println(main_messages.getString("no_gui"));
            ServerConsoleController.startDB(dbToStart);
        } else {
            launch(args);
        }

        logger.info(main_messages.getString("finished"));
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxml = ServerGuiController.class.getResource("ServerGui.fxml");
            if (fxml == null) {
                logger.severe(ServerGuiController.ui_messages.getString("fxml_missing"));
                System.exit(-1);
            }
            FXMLLoader fx = new FXMLLoader(fxml, ServerGuiController.ui_messages);
            VBox page = fx.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle(ServerGuiController.gui_messages.getString("window_title"));
            ServerGuiController controller = fx.getController();
            controller.setMods(mods);
            getParameters();
            controller.setArguments(parser);
            scene.getWindow().setOnCloseRequest(ev -> {
                if (controller.checkIfWeWantToSaveTab() == ButtonType.CANCEL) {
                    ev.consume();
                    return;
                }
                if (!controller.shutdown(0, ServerGuiController.gui_messages.getString("no_shutdown_reason"))) {
                    ev.consume();
                }
            });
            primaryStage.show();
            controller.shown();
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
