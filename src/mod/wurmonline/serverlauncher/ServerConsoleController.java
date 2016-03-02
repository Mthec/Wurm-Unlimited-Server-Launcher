package mod.wurmonline.serverlauncher;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerProperties;
import com.wurmonline.server.Servers;
import com.wurmonline.server.kingdom.Kingdoms;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ServerConsoleController extends ServerController {
    protected static Logger logger = Logger.getLogger(ServerConsoleController.class.getName());
    ResourceBundle server_messages = LocaleHelper.getBundle("ServerController");

    public void startDB(String dbName) {
        try {
            setCurrentDir(dbName + (dbName.endsWith(File.separator) ? "" : File.separator));
            initServer(dbName);
            startServer(false);
        } catch (IOException ex) {
            logger.severe(MessageFormat.format(server_messages.getString("cannot_find_db"), dbName));
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    void initServer(String dbName) {
        // TODO - Move to ServerMain/ServerController.
        // TODO - Should use ServerController.loadAllServers.
        Servers.loadAllServers(true);
        int _int;

        if (Servers.argumets.hasOption("adminpwd")) {
            String adminPass = Servers.argumets.getOptionValue("adminpwd");
            if (adminPass != null && !adminPass.isEmpty()) {
                adminPassword = adminPass;
            } else {
                System.err.println(server_messages.getString("admin_password_not_set"));
            }
        }

        for (ServerEntry entry : Servers.getAllServers()) {
            if (entry.name.equals(dbName)) {
                Servers.localServer = entry;
                break;
            }
        }

        if (Servers.localServer != null && Servers.argumets != null) {
            logger.info(server_messages.getString("settings_from_cmd_line"));
            boolean settingChanged = false;
            String settingValue;
            if (Servers.argumets.hasOption("ip")) {
                settingValue = Servers.argumets.getOptionValue("ip");
                Servers.localServer.EXTERNALIP = settingValue;
                Servers.localServer.INTRASERVERADDRESS = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_ip"), settingValue));
            }

            if (Servers.argumets.hasOption("externalport")) {
                settingValue = Servers.argumets.getOptionValue("externalport");
                Servers.localServer.EXTERNALPORT = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_external_port"), settingValue));
            }

            if (Servers.argumets.hasOption("internalport")) {
                settingValue = Servers.argumets.getOptionValue("internalport");
                Servers.localServer.INTRASERVERPORT = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_internal_port"), settingValue));
            }

            boolean settingBoolean;
            if (Servers.argumets.hasOption("epicsettings")) {
                settingValue = Servers.argumets.getOptionValue("epicsettings");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.EPIC = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_epic"), settingBoolean));
            }

            if (Servers.argumets.hasOption("homekingdom")) {
                settingValue = Servers.argumets.getOptionValue("homekingdom");
                byte settingByte = Byte.parseByte(settingValue);
                Servers.localServer.KINGDOM = settingByte;
                settingChanged = true;
                String kingdomName = Kingdoms.getNameFor(settingByte);
                logger.info(MessageFormat.format(server_messages.getString("set_homekingdom"), settingByte, kingdomName));
            }

            if (Servers.argumets.hasOption("homeserver")) {
                settingValue = Servers.argumets.getOptionValue("homeserver");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.HOMESERVER = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_homeserver"), settingBoolean));
            }

            if (Servers.argumets.hasOption("loginserver")) {
                settingValue = Servers.argumets.getOptionValue("loginserver");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.LOGINSERVER = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_loginserver"), settingBoolean));
            }

            if (Servers.argumets.hasOption("maxplayers")) {
                settingValue = Servers.argumets.getOptionValue("maxplayers");
                _int = Integer.parseInt(settingValue);
                Servers.localServer.pLimit = _int;
                Servers.localServer.playerLimitOverridable = false;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_max_players"), _int));
            }

            if (Servers.argumets.hasOption("pvp")) {
                settingValue = Servers.argumets.getOptionValue("pvp");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.PVPSERVER = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_pvp"), settingBoolean));
            }

            if (Servers.argumets.hasOption("queryport")) {
                settingValue = Servers.argumets.getOptionValue("queryport");
                ServerProperties.loadProperties();
                ServerProperties.setValue("STEAMQUERYPORT", settingValue);
                Servers.localServer.STEAMQUERYPORT = settingValue;
                logger.info(MessageFormat.format(server_messages.getString("set_query_port"), settingValue));
            }

            if (Servers.argumets.hasOption("rmiport")) {
                settingValue = Servers.argumets.getOptionValue("rmiport");
                _int = Integer.parseInt(settingValue);
                Servers.localServer.RMI_PORT = _int;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_rmi_port"), _int));
            }

            if (Servers.argumets.hasOption("rmiregport")) {
                settingValue = Servers.argumets.getOptionValue("rmiregport");
                _int = Integer.parseInt(settingValue);
                Servers.localServer.REGISTRATION_PORT = _int;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_rmi_register_port"), _int));
            }

            if (Servers.argumets.hasOption("servername")) {
                settingValue = Servers.argumets.getOptionValue("servername");
                Servers.localServer.name = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_servername"), settingValue));
            }

            if (Servers.argumets.hasOption("serverpassword")) {
                settingValue = Servers.argumets.getOptionValue("serverpassword");
                Servers.localServer.setSteamServerPassword(settingValue);
                settingChanged = true;
            }

            if (settingChanged) {
                Servers.localServer.saveNewGui(Servers.localServer.id);
            }
        }
    }

    protected boolean askConfirmation(String title, String header, String content) {
        return true;
    }

    protected boolean askYesNo(String title, String header, String content) {
        return true;
    }
}
