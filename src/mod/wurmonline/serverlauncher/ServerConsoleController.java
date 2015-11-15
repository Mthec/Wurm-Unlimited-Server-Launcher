package mod.wurmonline.serverlauncher;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.*;
import com.wurmonline.server.console.CommandReader;
import com.wurmonline.server.kingdom.Kingdoms;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ServerConsoleController extends ServerController {
    protected static Logger logger = Logger.getLogger(ServerConsoleController.class.getName());
    static ResourceBundle server_messages = LocaleHelper.getBundle("ServerController");

    public static void startDB(String dbName) {
        File gameDir = new File(dbName);
        boolean ok = false;
        File newCurrent;
        if(gameDir.isDirectory()) {
            File[] var6;
            int iox = (var6 = gameDir.listFiles()).length;

            for(int launcher = 0; launcher < iox; ++launcher) {
                newCurrent = var6[launcher];
                if(!newCurrent.isDirectory() && newCurrent.getName().equalsIgnoreCase("gamedir")) {
                    ok = true;
                }
            }
        }

        if(ok) {
            newCurrent = new File(dbName + File.separator + "currentdir");

            try {
                newCurrent.createNewFile();
                DbConnector.closeAll();
                ServerDirInfo.setFileDBPath(dbName + (dbName.endsWith(File.separator) ? "" : File.separator));
                ServerDirInfo.setConstantsFileName(ServerDirInfo.getFileDBPath() + "wurm.ini");
                Constants.load();
                Constants.dbHost = dbName;
                Constants.dbPort = "";
                Constants.loginDbHost = dbName;
                Constants.loginDbPort = "";
                Constants.siteDbHost = dbName;
                Constants.siteDbPort = "";
                DbConnector.closeAll();
                DbConnector.initialize();
                initServer(dbName);
            } catch (Exception var12) {
                var12.printStackTrace();

                try {
                    newCurrent.delete();
                    DbConnector.closeAll();
                    ServerDirInfo.setConstantsFileName(dbName + File.separator + "wurm.ini");
                    return;
                } catch (Exception ex) {
                    return;
                }
            }

            ServerLauncher staticCurrentServer = new ServerLauncher();

            try {
                LocateRegistry.createRegistry(Servers.localServer.REGISTRATION_PORT);
                ServerProperties.setValue("ADMINPASSWORD", adminPassword);
                staticCurrentServer.runServer(true);
                (new Thread(new CommandReader(staticCurrentServer.getServer(), System.in), "Console Command Reader")).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                System.out.println("\n==================================================================\n");
                System.out.println(MessageFormat.format(server_messages.getString("launcher_finished"), new Date()));
                System.out.println("\n==================================================================\n");
            }

        } else {
            System.out.println(MessageFormat.format(server_messages.getString("cannot_find_db"), dbName));
        }
    }

    static void initServer(String dbName) {
        // TODO - Sort static stuff, should use ServerController.loadAllServers.
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

        for(ServerEntry entry : Servers.getAllServers()) {
            if(entry.name.equals(dbName)) {
                Servers.localServer = entry;
                break;
            }
        }

        if(Servers.localServer != null && Servers.argumets != null) {
            logger.info(server_messages.getString("settings_from_cmd_line"));
            boolean settingChanged = false;
            String settingValue;
            if(Servers.argumets.hasOption("ip")) {
                settingValue = Servers.argumets.getOptionValue("ip");
                Servers.localServer.EXTERNALIP = settingValue;
                Servers.localServer.INTRASERVERADDRESS = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_ip"), settingValue));
            }

            if(Servers.argumets.hasOption("externalport")) {
                settingValue = Servers.argumets.getOptionValue("externalport");
                Servers.localServer.EXTERNALPORT = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_external_port"), settingValue));
            }

            if(Servers.argumets.hasOption("internalport")) {
                settingValue = Servers.argumets.getOptionValue("internalport");
                Servers.localServer.INTRASERVERPORT = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_internal_port"), settingValue));
            }

            boolean settingBoolean;
            if(Servers.argumets.hasOption("epicsettings")) {
                settingValue = Servers.argumets.getOptionValue("epicsettings");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.EPIC = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_epic"), settingBoolean));
            }

            if(Servers.argumets.hasOption("homekingdom")) {
                settingValue = Servers.argumets.getOptionValue("homekingdom");
                byte settingByte = Byte.parseByte(settingValue);
                Servers.localServer.KINGDOM = settingByte;
                settingChanged = true;
                String kingdomName = Kingdoms.getNameFor(settingByte);
                logger.info(MessageFormat.format(server_messages.getString("set_homekingdom"), settingByte, kingdomName));
            }

            if(Servers.argumets.hasOption("homeserver")) {
                settingValue = Servers.argumets.getOptionValue("homeserver");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.HOMESERVER = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_homeserver"), settingBoolean));
            }

            if(Servers.argumets.hasOption("loginserver")) {
                settingValue = Servers.argumets.getOptionValue("loginserver");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.LOGINSERVER = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_loginserver"), settingBoolean));
            }

            if(Servers.argumets.hasOption("maxplayers")) {
                settingValue = Servers.argumets.getOptionValue("maxplayers");
                _int = Integer.parseInt(settingValue);
                Servers.localServer.pLimit = _int;
                Servers.localServer.playerLimitOverridable = false;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_max_players"), _int));
            }

            if(Servers.argumets.hasOption("pvp")) {
                settingValue = Servers.argumets.getOptionValue("pvp");
                settingBoolean = Boolean.parseBoolean(settingValue);
                Servers.localServer.PVPSERVER = settingBoolean;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_pvp"), settingBoolean));
            }

            if(Servers.argumets.hasOption("queryport")) {
                settingValue = Servers.argumets.getOptionValue("queryport");
                ServerProperties.loadProperties();
                ServerProperties.setValue("STEAMQUERYPORT", settingValue);
                Servers.localServer.STEAMQUERYPORT = settingValue;
                logger.info(MessageFormat.format(server_messages.getString("set_query_port"), settingValue));
            }

            if(Servers.argumets.hasOption("rmiport")) {
                settingValue = Servers.argumets.getOptionValue("rmiport");
                _int = Integer.parseInt(settingValue);
                Servers.localServer.RMI_PORT = _int;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_rmi_port"), _int));
            }

            if(Servers.argumets.hasOption("rmiregport")) {
                settingValue = Servers.argumets.getOptionValue("rmiregport");
                _int = Integer.parseInt(settingValue);
                Servers.localServer.REGISTRATION_PORT = _int;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_rmi_register_port"), _int));
            }

            if(Servers.argumets.hasOption("servername")) {
                settingValue = Servers.argumets.getOptionValue("servername");
                Servers.localServer.name = settingValue;
                settingChanged = true;
                logger.info(MessageFormat.format(server_messages.getString("set_servername"), settingValue));
            }

            if(Servers.argumets.hasOption("serverpassword")) {
                settingValue = Servers.argumets.getOptionValue("serverpassword");
                Servers.localServer.setSteamServerPassword(settingValue);
                settingChanged = true;
            }

            if(settingChanged) {
                Servers.localServer.saveNewGui(Servers.localServer.id);
            }
        }
    }

    protected boolean askConfirmation(String title, String header, String content) {
        return false;
    }
    protected boolean askYesNo(String title, String header, String content) {
        return false;
    }
}
