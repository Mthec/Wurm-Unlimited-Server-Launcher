package mod.wurmonline.serverlauncher.mods.players;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
// Expanded version of PlayerPropertySheet from Wurm Unlimited by Code Club AB
//
import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Server;
import com.wurmonline.server.gui.PlayerData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerPropertySheet extends VBox implements MiscConstants {
    static Logger logger = Logger.getLogger(PlayerPropertySheet.class.getName());
    PlayerData current;
    ObservableList<PropertySheet.Item> list;
    Set<PropertyType> changedProperties = new HashSet<>();
    ResourceBundle players_messages;

    public PlayerPropertySheet(PlayerData entry, ResourceBundle messageBundle) {
        players_messages = messageBundle;
        current = entry;
        list = FXCollections.observableArrayList();
        list.add(new PlayerPropertySheet.CustomPropertyItem(PlayerPropertySheet.PropertyType.NAME,
                                                            players_messages.getString("name_category"),
                                                            players_messages.getString("name_label"),
                                                            players_messages.getString("name_help_text"),
                                                            true, entry.getName()));
        list.add(new PlayerPropertySheet.CustomPropertyItem(PlayerPropertySheet.PropertyType.POSX,
                                                            players_messages.getString("position_category"),
                                                            players_messages.getString("position_x_label"),
                                                            players_messages.getString("position_x_help_text"),
                                                            true, entry.getPosx()));
        list.add(new PlayerPropertySheet.CustomPropertyItem(PlayerPropertySheet.PropertyType.POSY,
                                                            players_messages.getString("position_category"),
                                                            players_messages.getString("position_y_label"),
                                                            players_messages.getString("position_y_help_text"),
                                                            true, entry.getPosy()));
        list.add(new PlayerPropertySheet.CustomPropertyItem(PlayerPropertySheet.PropertyType.POWER,
                                                            players_messages.getString("power_category"),
                                                            players_messages.getString("power_label"),
                                                            players_messages.getString("power_help_text"),
                                                            true, entry.getPower()));
        list.add(new PlayerPropertySheet.CustomPropertyItem(PlayerPropertySheet.PropertyType.CURRENTSERVER,
                                                            players_messages.getString("current_server_category"),
                                                            players_messages.getString("current_server_label"),
                                                            players_messages.getString("current_server_help_text"),
                                                            true, entry.getServer()));
        list.add(new PlayerPropertySheet.CustomPropertyItem(PlayerPropertySheet.PropertyType.UNDEAD,
                                                            players_messages.getString("undead_category"),
                                                            players_messages.getString("undead_label"),
                                                            players_messages.getString("undead_help_text"),
                                                            true, entry.isUndead()));
        PropertySheet propertySheet = new PropertySheet(list);
        VBox.setVgrow(propertySheet, Priority.ALWAYS);
        getChildren().add(propertySheet);
    }

    public PlayerData getCurrentData() {
        return current;
    }

    public String save() {
        String toReturn = "";
        boolean saveAtAll = false;

        for(PlayerPropertySheet.CustomPropertyItem player : new CustomPropertyItem[list.size()]) {
            if(changedProperties.contains(player.getPropertyType())) {
                saveAtAll = true;

                try {
                    switch(player.getPropertyType().ordinal()) {
                        case 1:
                            current.setName(player.getValue().toString());
                            break;
                        case 2:
                            current.setPosx((Float) player.getValue());
                            break;
                        case 3:
                            current.setPosy((Float) player.getValue());
                            break;
                        case 4:
                            current.setPower((Integer) player.getValue());
                            break;
                        case 5:
                            current.setServer((Integer) player.getValue());
                            break;
                        case 6:
                            if(!current.isUndead()) {
                                current.setUndeadType((byte)(1 + Server.rand.nextInt(3)));
                            } else {
                                current.setUndeadType((byte)0);
                            }
                    }
                } catch (Exception ex) {
                    saveAtAll = false;
                    toReturn = toReturn + MessageFormat.format(players_messages.getString("invalid_value"), player.getCategory(), player.getValue());
                    logger.log(Level.INFO, MessageFormat.format(players_messages.getString("error"), ex.getMessage()), ex);
                }
            }
        }

        if (toReturn.length() == 0 && saveAtAll) {
            try {
                current.save();
                toReturn = "ok";
            } catch (Exception ex) {
                toReturn = ex.getMessage();
            }
        }
        return toReturn;
    }

    class CustomPropertyItem implements PropertySheet.Item {
        PlayerPropertySheet.PropertyType type;
        String category;
        String name;
        String description;
        boolean editable = true;
        Object value;

        CustomPropertyItem (PlayerPropertySheet.PropertyType aType, String aCategory, String aName, String aDescription, boolean aEditable, Object aValue) {
            type = aType;
            category = aCategory;
            name = aName;
            description = aDescription;
            editable = aEditable;
            value = aValue;
        }

        public PlayerPropertySheet.PropertyType getPropertyType() {
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
            if(!value.equals(aValue)) {
                PlayerPropertySheet.this.changedProperties.add(type);
            }

            value = aValue;
        }
    }

    enum PropertyType {
        NAME,
        POSX,
        POSY,
        POWER,
        CURRENTSERVER,
        UNDEAD
    }
}
