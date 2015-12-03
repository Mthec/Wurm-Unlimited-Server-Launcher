package mod.wurmonline.serverlauncher.mods.serversettings;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerProperties;
import com.wurmonline.server.Servers;
import com.wurmonline.server.steam.SteamHandler;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.gui.ServerPropertySheet;
import org.gotti.wurmunlimited.modloader.interfaces.WurmLoadDumpMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSettings implements WurmMod, WurmLoadDumpMod {
    static Logger logger = Logger.getLogger(ServerSettings.class.getName());
    static ResourceBundle messages = LocaleHelper.getBundle("ServerPropertySheet");
    String CATEGORY = "serversettings.";
    boolean changedId = false;
    int oldId;

    @Override
    public String getComment() {
        return messages.getString("server_settings_comment");
    }

    public void loadSettings(Properties properties) {
        ServerEntry current = Servers.localServer;
        Enumeration prop = properties.propertyNames();
        while (prop.hasMoreElements()) {
            String name = (String)prop.nextElement();
            if (!name.startsWith(CATEGORY)) {
                continue;
            }
            String item = properties.getProperty(name);
            name = name.replace(CATEGORY, "");    
            try {
                switch(ServerPropertySheet.PropertyType.valueOf(name).ordinal()) {
                    case 0:
                        if(current.isLocal) {
                            changedId = true;
                            oldId = current.id;
                        }
                        current.id = Integer.valueOf(item);
                        break;
                    case 1:
                        current.EXTERNALIP = item;
                        break;
                    case 2:
                        current.EXTERNALPORT = item;
                        break;
                    case 3:
                        current.INTRASERVERADDRESS = item;
                        break;
                    case 4:
                        current.INTRASERVERPORT = item;
                        break;
                    case 5:
                        current.RMI_PORT = Integer.valueOf(item);
                        break;
                    case 6:
                        current.REGISTRATION_PORT = Integer.valueOf(item);
                        break;
                    case 7:
                        current.INTRASERVERPASSWORD = item;
                        break;
                    case 8:
                        current.name = item;
                        break;
                    case 9:
                        current.setSteamServerPassword(item);
                        break;
                    case 10:
                        short sqp = Short.valueOf(item);
                        if(sqp != ServerProperties.getShort("STEAMQUERYPORT", sqp)) {
                            ServerProperties.setValue("STEAMQUERYPORT", Short.toString(sqp));
                            ServerProperties.checkProperties();
                        }
                        break;
                }
            } catch (Exception ex) {
                logger.log(Level.INFO, MessageFormat.format(messages.getString("error"), ex.getMessage()), ex);
            }

            if(changedId) {
                current.saveNewGui(oldId);
                current.movePlayersFromId(oldId);
                changedId = false;
                Servers.moveServerId(current, oldId);
            } else {
                current.saveNewGui(current.id);
            }
        }
    }

    public Properties dumpSettings() {
        Properties properties = new Properties();
        ServerEntry current = Servers.localServer;
        for (ServerPropertySheet.PropertyType prop : ServerPropertySheet.PropertyType.values()) {
            try {
                switch(prop.ordinal()) {
                    case 0:
                        properties.setProperty(CATEGORY + prop.name(), String.valueOf(current.id));
                        break;
                    case 1:
                        properties.setProperty(CATEGORY + prop.name(), current.EXTERNALIP);
                        break;
                    case 2:
                        properties.setProperty(CATEGORY + prop.name(), current.EXTERNALPORT);
                        break;
                    case 3:
                        properties.setProperty(CATEGORY + prop.name(), current.INTRASERVERADDRESS);
                        break;
                    case 4:
                        properties.setProperty(CATEGORY + prop.name(), current.INTRASERVERPORT);
                        break;
                    case 5:
                        properties.setProperty(CATEGORY + prop.name(), String.valueOf(current.RMI_PORT));
                        break;
                    case 6:
                        properties.setProperty(CATEGORY + prop.name(), String.valueOf(current.REGISTRATION_PORT));
                        break;
                    case 7:
                        properties.setProperty(CATEGORY + prop.name(), current.INTRASERVERPASSWORD);
                        break;
                    case 8:
                        properties.setProperty(CATEGORY + prop.name(), current.name);
                        break;
                    case 9:
                        properties.setProperty(CATEGORY + prop.name(), current.getSteamServerPassword());
                        break;
                    case 10:
                        properties.setProperty(CATEGORY + prop.name(), ServerProperties.getString("STEAMQUERYPORT", String.valueOf(SteamHandler.steamQueryPort)));
                        break;
                }
            } catch (Exception ex) {
                logger.log(Level.INFO, MessageFormat.format(messages.getString("error"), ex.getMessage()), ex);
            }
        }
        return properties;
    }
}
