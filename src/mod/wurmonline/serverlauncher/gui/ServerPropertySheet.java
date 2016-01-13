package mod.wurmonline.serverlauncher.gui;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
// Expanded version of ServerPropertySheet from Wurm Unlimited by Code Club AB
//

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerProperties;
import com.wurmonline.server.Servers;
import com.wurmonline.server.steam.SteamHandler;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerController;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerPropertySheet extends VBox {
    static Logger logger = Logger.getLogger(ServerPropertySheet.class.getName());
    ServerEntry current;
    PropertySheet propertySheet;
    ObservableList<PropertySheet.Item> list;
    static ResourceBundle messages = LocaleHelper.getBundle("ServerPropertySheet");
    String categoryServerSettings = messages.getString("server_settings");
    String categoryAdvancedSettings = messages.getString("advanced_settings");
    Set<PropertyType> changedProperties = new HashSet<>();
    boolean saveNewGui = false;
    boolean changedId = false;
    int oldId = 0;
    static String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

    public String save() {
        String toReturn = "";
        boolean saveAtAll = false;

        for (PropertySheet.Item old_item : list) {
            ServerPropertySheet.CustomPropertyItem item = (ServerPropertySheet.CustomPropertyItem) old_item;
            if (changedProperties.contains(item.getPropertyType()) || current.isCreating) {
                saveAtAll = true;

                try {
                    switch (item.getPropertyType().ordinal()) {
                        case 0:
                            if (current.isLocal) {
                                changedId = true;
                                oldId = current.id;
                            }
                            current.id = (Integer) item.getValue();
                            saveNewGui = true;
                            break;
                        case 1:
                            current.EXTERNALIP = item.getValue().toString();
                            saveNewGui = true;
                            break;
                        case 2:
                            current.EXTERNALPORT = item.getValue().toString();
                            saveNewGui = true;
                            break;
                        case 3:
                            current.INTRASERVERADDRESS = item.getValue().toString();
                            saveNewGui = true;
                            break;
                        case 4:
                            current.INTRASERVERPORT = item.getValue().toString();
                            saveNewGui = true;
                            break;
                        case 5:
                            current.RMI_PORT = (Integer) item.getValue();
                            saveNewGui = true;
                            break;
                        case 6:
                            current.REGISTRATION_PORT = (Integer) item.getValue();
                            saveNewGui = true;
                            break;
                        case 7:
                            current.INTRASERVERPASSWORD = item.getValue().toString();
                            saveNewGui = true;
                            break;
                        case 8:
                            current.name = item.getValue().toString();
                            saveNewGui = true;
                            break;
                        case 9:
                            current.setSteamServerPassword(item.getValue().toString());
                            saveNewGui = true;
                            break;
                        case 10:
                            short sqp = (Short) item.getValue();
                            if (sqp != ServerProperties.getShort("STEAMQUERYPORT", sqp)) {
                                ServerProperties.setValue("STEAMQUERYPORT", Short.toString(sqp));
                                ServerProperties.checkProperties();
                            }
                            break;
                    }
                } catch (Exception ex) {
                    saveAtAll = false;
                    toReturn = toReturn + MessageFormat.format(messages.getString("invalid_value"), item.getCategory(), item.getValue());
                    logger.log(Level.INFO, MessageFormat.format(messages.getString("error"), ex.getMessage()), ex);
                }
            }
        }

        if (toReturn.length() == 0 && saveAtAll) {
            if (current.isCreating) {
                toReturn = messages.getString("new_server_saved");
                Servers.registerServer(current.id, current.getName(), current.HOMESERVER, current.SPAWNPOINTJENNX, current.SPAWNPOINTJENNY, current.SPAWNPOINTLIBX, current.SPAWNPOINTLIBY, current.SPAWNPOINTMOLX, current.SPAWNPOINTMOLY, current.INTRASERVERADDRESS, current.INTRASERVERPORT, current.INTRASERVERPASSWORD, current.EXTERNALIP, current.EXTERNALPORT, current.LOGINSERVER, current.KINGDOM, current.ISPAYMENT, current.getConsumerKey(), current.getConsumerSecret(), current.getApplicationToken(), current.getApplicationSecret(), false, current.testServer, current.randomSpawns);
            } else {
                toReturn = messages.getString("properties_saved");
            }

            if (saveNewGui) {
                logger.info(messages.getString("saved_using_new"));
                saveNewGui = false;
                if (changedId) {
                    current.saveNewGui(oldId);
                    // TODO - So this is how it's done.  See addServer in ServerController.
                    current.movePlayersFromId(oldId);
                    changedId = false;
                    Servers.moveServerId(current, oldId);
                    oldId = 0;
                } else {
                    current.saveNewGui(current.id);
                }
            }

            changedProperties.clear();
            current.isCreating = false;
        }

        return toReturn;
    }

    public static String generateRandomPassword() {
        Random rand = new Random();
        int length = rand.nextInt(3) + 6;
        char[] password = new char[length];

        for (int i = 0; i < length; ++i) {
            int randDecimalAsciiVal = rand.nextInt(PASSWORD_CHARS.length());
            password[i] = PASSWORD_CHARS.charAt(randDecimalAsciiVal);
        }

        return String.valueOf(password);
    }

    public ServerPropertySheet(ServerEntry entry) {
        current = entry;
        list = FXCollections.observableArrayList();
        if (entry.isLocal) {
            initializeLocalServer(entry);
        } else {
            initializeNonLocalServer(entry);
        }

    }

    void initializeLocalServer(ServerEntry entry) {
        saveNewGui = false;
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.NAME, categoryServerSettings, messages.getString("name_label"), messages.getString("name_help_text"), true, entry.name));
        if (entry.id == 0) {
            changedId = true;
            oldId = 0;
            entry.id = ServerController.getNewServerId();
            list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.SERVERID, categoryAdvancedSettings, messages.getString("id_label"), messages.getString("id_help_text"), true, entry.id));
            changedProperties.add(ServerPropertySheet.PropertyType.SERVERID);
            saveNewGui = true;
        } else {
            list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.SERVERID, categoryAdvancedSettings, messages.getString("id_label"), messages.getString("id_help_text"), true, entry.id));
        }

        if ((entry.EXTERNALIP == null || entry.EXTERNALIP.equals("")) && entry.isLocal) {
            try {
                entry.EXTERNALIP = InetAddress.getLocalHost().getHostAddress();
                changedProperties.add(ServerPropertySheet.PropertyType.EXTSERVERIP);
                saveNewGui = true;
            } catch (Exception var4) {
                logger.log(Level.INFO, var4.getMessage());
            }
        }

        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.EXTSERVERIP, categoryServerSettings, messages.getString("external_ip_address_label"), messages.getString("external_ip_address_help_text"), true, entry.EXTERNALIP));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.EXTSERVERPORT, categoryServerSettings, messages.getString("external_ip_port_label"), messages.getString("external_ip_port_help_text"), true, entry.EXTERNALPORT));
        if ((entry.INTRASERVERADDRESS == null || entry.INTRASERVERADDRESS.equals("")) && entry.isLocal) {
            try {
                entry.INTRASERVERADDRESS = InetAddress.getLocalHost().getHostAddress();
                changedProperties.add(ServerPropertySheet.PropertyType.INTIP);
                saveNewGui = true;
            } catch (Exception var3) {
                logger.log(Level.INFO, var3.getMessage());
            }
        }

        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.INTIP, categoryAdvancedSettings, messages.getString("internal_ip_address_label"), messages.getString("internal_ip_address_help_text"), true, entry.INTRASERVERADDRESS));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.INTPORT, categoryAdvancedSettings, messages.getString("internal_ip_port_label"), messages.getString("internal_ip_port_help_text"), true, entry.INTRASERVERPORT));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.RMI_REG_PORT, categoryAdvancedSettings, messages.getString("rmi_registration_port_label"), messages.getString("rmi_registration_port_help_text"), true, entry.REGISTRATION_PORT));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.RMIPORT, categoryAdvancedSettings, messages.getString("rmi_port_label"), messages.getString("rmi_port_help_text"), true, entry.RMI_PORT));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.STEAMQUERYPORT, categoryAdvancedSettings, messages.getString("steam_query_port_label"), messages.getString("steam_query_port_help_text"), true, ServerProperties.getShort("STEAMQUERYPORT", SteamHandler.steamQueryPort)));
        if ((entry.INTRASERVERPASSWORD == null || entry.INTRASERVERPASSWORD.equals("")) && entry.isLocal) {
            entry.INTRASERVERPASSWORD = generateRandomPassword();
            changedProperties.add(ServerPropertySheet.PropertyType.INTRASERVERPASSWORD);
            saveNewGui = true;
        }

        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.INTRASERVERPASSWORD, categoryAdvancedSettings, messages.getString("intra_password_label"), messages.getString("intra_password_help_text"), true, entry.INTRASERVERPASSWORD));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.STEAMPW, categoryServerSettings, messages.getString("server_password_label"), messages.getString("server_password_help_text"), true, entry.getSteamServerPassword()));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.ADMINPWD, categoryServerSettings, messages.getString("admin_password_label"), messages.getString("admin_password_help_text"), true, ServerProperties.getString("ADMINPWD", "")));

        if (saveNewGui) {
            saveNewGui = false;
            if (entry.isCreating) {
                save();
            } else if (changedId) {
                current.saveNewGui(oldId);
                current.movePlayersFromId(oldId);
                changedId = false;
                Servers.moveServerId(current, oldId);
                oldId = 0;
            } else {
                entry.saveNewGui(entry.id);
            }

            changedProperties.clear();
            System.out.println(messages.getString("new_server_saved"));
        }

        SimpleObjectProperty<DefaultPropertyEditorFactory> propertyEditorFactory = new SimpleObjectProperty<>(this, "propertyEditor", new DefaultPropertyEditorFactory());
        propertySheet = new PropertySheet(list);
        propertySheet.setPropertyEditorFactory(param -> {
            if (param instanceof CustomPropertyItem) {
                CustomPropertyItem pi = (CustomPropertyItem) param;
                if (pi.getValue().getClass() == Float.class) {
                    return new FormattedFloatEditor(param);
                }
            }

            return propertyEditorFactory.get().call(param);
        });
        propertySheet.setMode(PropertySheet.Mode.CATEGORY);
        VBox.setVgrow(propertySheet, Priority.ALWAYS);
        getChildren().add(propertySheet);
    }

    void initializeNonLocalServer(ServerEntry entry) {
        saveNewGui = false;
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.NAME, categoryServerSettings, messages.getString("name_label"), messages.getString("name_help_text"), true, entry.name));
        if (entry.id == 0) {
            changedId = true;
            oldId = 0;
            entry.id = ServerController.getNewServerId();
            list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.SERVERID, categoryServerSettings, messages.getString("id_label"), messages.getString("id_help_text"), true, entry.id));
            changedProperties.add(ServerPropertySheet.PropertyType.SERVERID);
            saveNewGui = true;
        } else {
            list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.SERVERID, categoryServerSettings, messages.getString("id_label"), messages.getString("id_help_text"), true, entry.id));
        }

        if ((entry.EXTERNALIP == null || entry.EXTERNALIP.equals("")) && entry.isLocal) {
            try {
                entry.EXTERNALIP = InetAddress.getLocalHost().getHostAddress();
                changedProperties.add(ServerPropertySheet.PropertyType.EXTSERVERIP);
                saveNewGui = true;
            } catch (Exception ex) {
                logger.info(ex.getMessage());
            }
        }

        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.EXTSERVERIP, categoryServerSettings, messages.getString("external_ip_address_label"), messages.getString("external_ip_address_help_text"), true, entry.EXTERNALIP));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.EXTSERVERPORT, categoryServerSettings, messages.getString("external_ip_port_label"), messages.getString("external_ip_port_help_text"), true, entry.EXTERNALPORT));
        if ((entry.INTRASERVERADDRESS == null || entry.INTRASERVERADDRESS.equals("")) && entry.isLocal) {
            try {
                entry.INTRASERVERADDRESS = InetAddress.getLocalHost().getHostAddress();
                changedProperties.add(ServerPropertySheet.PropertyType.INTIP);
                saveNewGui = true;
            } catch (Exception ex) {
                logger.info(ex.getMessage());
            }
        }

        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.INTIP, categoryServerSettings, messages.getString("internal_ip_address_label"), messages.getString("internal_ip_address_help_text"), true, entry.INTRASERVERADDRESS));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.INTPORT, categoryServerSettings, messages.getString("internal_ip_port_label"), messages.getString("internal_ip_port_help_text"), true, entry.INTRASERVERPORT));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.RMI_REG_PORT, categoryServerSettings, messages.getString("rmi_registration_port_label"), messages.getString("rmi_registration_port_help_text"), true, entry.REGISTRATION_PORT));
        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.RMIPORT, categoryServerSettings, messages.getString("rmi_port_label"), messages.getString("rmi_port_help_text"), true, entry.RMI_PORT));
        if ((entry.INTRASERVERPASSWORD == null || entry.INTRASERVERPASSWORD.equals("")) && entry.isLocal) {
            entry.INTRASERVERPASSWORD = generateRandomPassword();
            changedProperties.add(ServerPropertySheet.PropertyType.INTRASERVERPASSWORD);
            saveNewGui = true;
        }

        list.add(new ServerPropertySheet.CustomPropertyItem(ServerPropertySheet.PropertyType.INTRASERVERPASSWORD, categoryServerSettings, messages.getString("intra_password_label"), messages.getString("intra_password_help_text"), true, entry.INTRASERVERPASSWORD));
        if (saveNewGui) {
            saveNewGui = false;
            if (entry.isCreating) {
                save();
            } else if (changedId) {
                current.saveNewGui(oldId);
                current.movePlayersFromId(oldId);
                changedId = false;
                Servers.moveServerId(current, oldId);
                oldId = 0;
            } else {
                entry.saveNewGui(entry.id);
            }

            changedProperties.clear();
            System.out.println(messages.getString("new_server_saved"));
        }

        propertySheet = new PropertySheet(list);
        propertySheet.setMode(PropertySheet.Mode.CATEGORY);
        VBox.setVgrow(propertySheet, Priority.ALWAYS);
        getChildren().add(propertySheet);
    }

    public void setReadOnly() {
        if (propertySheet != null) {
            propertySheet.setMode(PropertySheet.Mode.NAME);
            propertySheet.setDisable(true);
        }
    }

    public boolean haveChanges() {
        return !changedProperties.isEmpty();
    }

    public boolean checkIfIpIsValid() {
        return !current.EXTERNALIP.equals("127.0.0.1") && !current.EXTERNALIP.equals("127.0.1.1");
    }

    public ServerEntry getCurrentServerEntry() {
        return current;
    }

    class CustomPropertyItem implements PropertySheet.Item {
        ServerPropertySheet.PropertyType type;
        String category;
        String name;
        String description;
        boolean editable;
        Object value;

        CustomPropertyItem(ServerPropertySheet.PropertyType aType, String aCategory, String aName, String aDescription, boolean aEditable, Object aValue) {
            editable = true;
            type = aType;
            category = aCategory;
            name = aName;
            description = aDescription;
            editable = aEditable;
            value = aValue;
        }

        public ServerPropertySheet.PropertyType getPropertyType() {
            return type;
        }

        public Class<?> getType() {
            return value.getClass();
        }

        public String getCategory() {
            return category;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isEditable() {
            return editable;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object aValue) {
            if (!value.equals(aValue)) {
                ServerPropertySheet.this.changedProperties.add(type);
            }
            value = aValue;
        }
    }

    public enum PropertyType {
        SERVERID,
        EXTSERVERIP,
        EXTSERVERPORT,
        INTIP,
        INTPORT,
        RMIPORT,
        RMI_REG_PORT,
        INTRASERVERPASSWORD,
        NAME,
        STEAMPW,
        STEAMQUERYPORT,
        ADMINPWD
    }
}
