package mod.wurmonline.serverlauncher.mods.gameplaytweaks;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.Constants;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerProperties;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.gui.FormattedFloatEditor;
import mod.wurmonline.serverlauncher.gui.MinMax;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameplayPropertySheet extends VBox {
    static Logger logger = Logger.getLogger(GameplayPropertySheet.class.getName());
    static ResourceBundle messages = LocaleHelper.getBundle("OfficialSettings");
    String categoryMOTD = messages.getString("motd");
    String categoryMode = messages.getString("server_modes");
    String categorySpawnPoints = messages.getString("spawn_points");
    String categoryPlayers = messages.getString("players");
    String categoryEnvironment = messages.getString("environment");
    String categoryKing = messages.getString("king");
    String categoryTwitter = messages.getString("twitter");
    ServerEntry current;
    ObservableList<PropertySheet.Item> list;
    PropertySheet propertySheet;
    Set<PropertyType> changedProperties = new HashSet<>();

    boolean saveNewGui = false;
    boolean saveSpawns = false;
    boolean saveTwitter = false;

    public String save() {
        String toReturn = "";
        boolean saveAtAll = false;

        for (PropertySheet.Item old_item : list) {
            GameplayPropertySheet.CustomPropertyItem item = (GameplayPropertySheet.CustomPropertyItem) old_item;
            if (changedProperties.contains(item.getPropertyType()) || current.isCreating) {
                saveAtAll = true;

                try {
                    switch (item.getPropertyType().ordinal()) {
                        // Message of the Day
                        case 0:
                            current.setMotd(item.getValue().toString());
                            saveNewGui = true;
                            break;
                        // Mode
                        case 1:
                            boolean npcs = (Boolean) item.getValue();
                            if (npcs != ServerProperties.getBoolean("NPCS", Constants.loadNpcs)) {
                                ServerProperties.setValue("NPCS", Boolean.toString(npcs));
                                ServerProperties.checkProperties();
                            }
                            break;
                        case 2:
                            current.pLimit = (Integer) item.getValue();
                            saveNewGui = true;
                            break;
                        case 3:
                            current.PVPSERVER = (Boolean) item.getValue();
                            saveNewGui = true;
                            break;
                        case 4:
                            current.EPIC = (Boolean) item.getValue();
                            saveNewGui = true;
                            break;
                        case 5:
                            current.HOMESERVER = (Boolean) item.getValue();
                            saveNewGui = true;
                            break;
                        case 6:
                            current.KINGDOM = (Byte) item.getValue();
                            saveNewGui = true;
                            break;
                        case 7:
                            current.LOGINSERVER = (Boolean) item.getValue();
                            saveNewGui = true;
                            break;
                        case 8:
                            current.ISPAYMENT = false;
                            saveNewGui = true;
                            break;
                        case 9:
                            current.testServer = (Boolean) item.getValue();
                            saveNewGui = true;
                            break;
                        case 10:
                            current.maintaining = (Boolean) item.getValue();
                            break;
                        // Spawns
                        case 11:
                            current.randomSpawns = (Boolean) item.getValue();
                            saveNewGui = true;
                            break;
                        case 12:
                            current.SPAWNPOINTJENNX = (Integer) item.getValue();
                            saveSpawns = true;
                            break;
                        case 13:
                            current.SPAWNPOINTJENNY = (Integer) item.getValue();
                            saveSpawns = true;
                            break;
                        case 14:
                            current.SPAWNPOINTMOLX = (Integer) item.getValue();
                            saveSpawns = true;
                            break;
                        case 15:
                            current.SPAWNPOINTMOLY = (Integer) item.getValue();
                            saveSpawns = true;
                            break;
                        case 16:
                            current.SPAWNPOINTLIBX = (Integer) item.getValue();
                            saveSpawns = true;
                            break;
                        case 17:
                            current.SPAWNPOINTLIBY = (Integer) item.getValue();
                            saveSpawns = true;
                            break;
                        // Player
                        case 18:
                            current.setSkillGainRate((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 19:
                            current.setActionTimer((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 20:
                            current.setSkillbasicval((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 21:
                            current.setSkillmindval((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 22:
                            current.setSkillbcval((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 23:
                            current.setSkillfightval((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 24:
                            current.setSkilloverallval((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        case 25:
                            current.setCombatRatingModifier((Float) item.getValue());
                            saveNewGui = true;
                            break;
                        // King
                        case 26:
                            current.setUpkeep((Boolean) item.getValue());
                            saveNewGui = true;
                            break;
                        case 27:
                            current.setMaxDeedSize((Integer) item.getValue());
                            saveNewGui = true;
                            break;
                        case 28:
                            current.setFreeDeeds((Boolean) item.getValue());
                            saveNewGui = true;
                            break;
                        case 29:
                            current.setKingsmoneyAtRestart((Integer) item.getValue() * 10000);
                            saveNewGui = true;
                            break;
                        case 30:
                            current.setTraderMaxIrons((Integer) item.getValue() * 10000);
                            saveNewGui = true;
                            break;
                        case 31:
                            current.setInitialTraderIrons((Integer) item.getValue() * 10000);
                            saveNewGui = true;
                            break;
                        // Environment
                        case 32:
                            current.maxCreatures = (Integer) item.getValue();
                            saveNewGui = true;
                            break;
                        case 33:
                            current.percentAggCreatures = (Float) item.getValue();
                            saveNewGui = true;
                            break;
                        case 34:
                            current.setTunnelingHits((Integer) item.getValue());
                            saveNewGui = true;
                            break;
                        case 35:
                            current.setBreedingTimer((Long) item.getValue());
                            saveNewGui = true;
                            break;
                        case 36:
                            current.setFieldGrowthTime((long) ((Float) item.getValue() * 3600.0F * 1000.0F));
                            saveNewGui = true;
                            break;
                        case 37:
                            current.treeGrowth = (Integer) item.getValue();
                            saveNewGui = true;
                        case 38:
                            current.setHotaDelay((Integer) item.getValue());
                            saveNewGui = true;
                            break;
                        // Twitter
                        case 39:
                            current.setConsumerKeyToUse(item.getValue().toString());
                            saveTwitter = true;
                            break;
                        case 40:
                            current.setConsumerSecret(item.getValue().toString());
                            saveTwitter = true;
                            break;
                        case 41:
                            current.setApplicationToken(item.getValue().toString());
                            saveTwitter = true;
                            break;
                        case 42:
                            current.setApplicationSecret(item.getValue().toString());
                            saveTwitter = true;
                            break;
                        case 43:
                            boolean loadEGI = (Boolean) item.getValue();
                            if (loadEGI != ServerProperties.getBoolean("ENDGAMEITEMS", Constants.loadEndGameItems)) {
                                ServerProperties.setValue("ENDGAMEITEMS", Boolean.toString(loadEGI));
                                ServerProperties.checkProperties();
                            }
                    }
                } catch (Exception ex) {
                    saveAtAll = false;
                    toReturn = toReturn + MessageFormat.format(messages.getString("invalid_value"), item.getCategory(), item.getValue());
                    logger.log(Level.INFO, MessageFormat.format(messages.getString("error"), ex.getMessage()), ex);
                }
            }
        }

        if (toReturn.length() == 0 && saveAtAll) {
            if (saveNewGui) {
                logger.info(messages.getString("saved_settings"));
                saveNewGui = false;
                current.saveNewGui(current.id);
            }

            if (saveTwitter && !current.isCreating) {
                if (current.saveTwitter()) {
                    logger.info(messages.getString("will_tweet"));
                } else {
                    logger.info(messages.getString("wont_tweet"));
                }

                saveTwitter = false;
            }

            if (saveSpawns) {
                current.updateSpawns();
                logger.info(messages.getString("saved_spawns"));
                saveSpawns = false;
            }

            changedProperties.clear();
            current.isCreating = false;
        }

        return toReturn;
    }

    public GameplayPropertySheet(ServerEntry entry) {
        if (!entry.isLocal) {
            setReadOnly();
            return;
        }
        current = entry;
        list = FXCollections.observableArrayList();
        saveNewGui = false;
        saveSpawns = false;
        saveTwitter = false;

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.MOTD, categoryMOTD, messages.getString("motd_label"), messages.getString("motd_help_text"), true, entry.getMotd()));

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.NPCS, categoryMode, messages.getString("npcs_label"), messages.getString("npcs_help_text"), true, ServerProperties.getBoolean("NPCS", Constants.loadNpcs)));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.MAXPLAYERS, categoryMode, messages.getString("max_players_label"), messages.getString("max_players_help_text"), true, entry.pLimit));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.PVPSERVER, categoryMode, messages.getString("pvp_label"), messages.getString("pvp_help_text"), true, entry.PVPSERVER));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.EPIC, categoryMode, messages.getString("epic_label"), messages.getString("epic_help_text"), true, entry.EPIC));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.ENDGAMEITEMS, categoryMode, messages.getString("end_game_items_label"), messages.getString("end_game_items_help_text"), true, ServerProperties.getBoolean("ENDGAMEITEMS", Constants.loadEndGameItems)));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.HOMESERVER, categoryMode, messages.getString("home_label"), messages.getString("home_help_text"), true, entry.HOMESERVER));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.KINGDOM, categoryMode, messages.getString("home_kingdom_label"), messages.getString("home_kingdom_help_text"), true, entry.KINGDOM));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.LOGINSERVER, categoryMode, messages.getString("login_label"), messages.getString("login_help_text"), true, entry.LOGINSERVER));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TESTSERVER, categoryMode, messages.getString("test_label"), messages.getString("test_help_text"), true, entry.testServer));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.MAINTAINING, categoryMode, messages.getString("maintenance_label"), messages.getString("maintenance_help_text"), true, false));

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.RANDOMSPAWNS, categorySpawnPoints, messages.getString("random_spawns_label"), messages.getString("random_spawns_help_text"), true, entry.randomSpawns));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SPAWNPOINTJENNX, categorySpawnPoints, messages.getString("jenn_spawn_x_label"), messages.getString("jenn_spawn_x_help_text"), true, entry.SPAWNPOINTJENNX));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SPAWNPOINTJENNY, categorySpawnPoints, messages.getString("jenn_spawn_y_label"), messages.getString("jenn_spawn_y_help_text"), true, entry.SPAWNPOINTJENNY));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SPAWNPOINTMOLX, categorySpawnPoints, messages.getString("mol_spawn_x_label"), messages.getString("mol_spawn_x_help_text"), true, entry.SPAWNPOINTMOLX));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SPAWNPOINTMOLY, categorySpawnPoints, messages.getString("mol_spawn_y_label"), messages.getString("mol_spawn_y_help_text"), true, entry.SPAWNPOINTMOLY));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SPAWNPOINTLIBX, categorySpawnPoints, messages.getString("lib_spawn_x_label"), messages.getString("lib_spawn_x_help_text"), true, entry.SPAWNPOINTLIBX));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SPAWNPOINTLIBY, categorySpawnPoints, messages.getString("lib_spawn_y_label"), messages.getString("lib_spawn_y_help_text"), true, entry.SPAWNPOINTLIBY));

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SKILLGAINRATE, categoryPlayers, messages.getString("skill_gain_multiplier_label"), messages.getString("skill_gain_multiplier_help_text"), true, current.getSkillGainRate(), 0.01F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.ACTIONTIMER, categoryPlayers, messages.getString("action_speed_label"), messages.getString("action_speed_help_text"), true, current.getActionTimer(), 0.01F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SKBASIC, categoryPlayers, messages.getString("characteristics_label"), messages.getString("characteristics_help_text"), true, current.getSkillbasicval(), 1.0F, 100.0F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SKMIND, categoryPlayers, messages.getString("mind_logic_label"), messages.getString("mind_logic_help_text"), true, current.getSkillmindval(), 1.0F, 100.0F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SKBC, categoryPlayers, messages.getString("body_control_label"), messages.getString("body_control_help_text"), true, current.getSkillbcval(), 1.0F, 100.0F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SKFIGHT, categoryPlayers, messages.getString("fight_label"), messages.getString("fight_help_text"), true, current.getSkillfightval(), 1.0F, 100.0F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.SKOVERALL, categoryPlayers, messages.getString("overall_label"), messages.getString("overall_help_text"), true, current.getSkilloverallval(), 1.0F, 100.0F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.CRMOD, categoryPlayers, messages.getString("combat_rating_label"), messages.getString("combat_rating_help_text"), true, current.getCombatRatingModifier()));

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.UPKEEP, categoryKing, messages.getString("upkeep_label"), messages.getString("upkeep_help_text"), true, current.isUpkeep()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.FREEDEEDS, categoryKing, messages.getString("deed_label"), messages.getString("deed_help_text"), true, current.isFreeDeeds()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.KINGSMONEY, categoryKing, messages.getString("money_pool_label"), messages.getString("money_pool_help_text"), true, current.getKingsmoneyAtRestart() / 10000));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TRADERMAX, categoryKing, messages.getString("trader_max_coin_label"), messages.getString("trader_max_coin_help_text"), true, current.getTraderMaxIrons() / 10000));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TRADERINIT, categoryKing, messages.getString("trader_start_coin_label"), messages.getString("trader_start_coin_help_text"), true, current.getInitialTraderIrons() / 10000));

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.MAXCREATURES, categoryEnvironment, messages.getString("max_creatures_label"), messages.getString("max_creatures_help_text"), true, current.maxCreatures));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.PERCENTAGG, categoryEnvironment, messages.getString("aggressive_creatures_label"), messages.getString("aggressive_creatures_help_text"), true, current.percentAggCreatures, 0.0F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TUNNELING, categoryEnvironment, messages.getString("mining_hits_label"), messages.getString("mining_hits_help_text"), true, current.getTunnelingHits()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.BREEDING, categoryEnvironment, messages.getString("breeding_time_label"), messages.getString("breeding_time_help_text"), true, current.getBreedingTimer()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.FIELDGROWTH, categoryEnvironment, messages.getString("field_growth_label"), messages.getString("field_growth_help_text"), true, (float) current.getFieldGrowthTime() / 1000.0F / 3600.0F, 0.01F));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TREEGROWTH, categoryEnvironment, messages.getString("tree_spread_label"), messages.getString("tree_spread_help_text"), true, current.treeGrowth, 0));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.HOTADELAY, categoryEnvironment, messages.getString("hota_label"), messages.getString("hota_help_text"), true, current.getHotaDelay()));

        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TWITTERCONSUMERKEY, categoryTwitter, messages.getString("consumer_key_label"), messages.getString("consumer_key_help_text"), true, entry.getConsumerKey()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TWITTERCONSUMERSECRET, categoryTwitter, messages.getString("consumer_secret_label"), messages.getString("consumer_secret_help_text"), true, entry.getConsumerSecret()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TWITTERAPPTOKEN, categoryTwitter, messages.getString("application_token_label"), messages.getString("application_token_help_text"), true, entry.getApplicationToken()));
        list.add(new GameplayPropertySheet.CustomPropertyItem(GameplayPropertySheet.PropertyType.TWITTERAPPSECRET, categoryTwitter, messages.getString("application_secret_label"), messages.getString("application_secret_help_text"), true, entry.getApplicationSecret()));

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

    public void setReadOnly() {
        if (propertySheet != null) {
            propertySheet.setMode(PropertySheet.Mode.NAME);
            propertySheet.setDisable(true);
        }
    }

    public boolean haveChanges() {
        return !changedProperties.isEmpty();
    }

    class CustomPropertyItem implements PropertySheet.Item, MinMax {
        GameplayPropertySheet.PropertyType type;
        String category;
        String name;
        String description;
        boolean editable;
        Object value;
        Object minValue;
        Object maxValue;

        CustomPropertyItem(GameplayPropertySheet.PropertyType aType, String aCategory, String aName, String aDescription, boolean aEditable, Object aValue) {
            editable = true;
            type = aType;
            category = aCategory;
            name = aName;
            description = aDescription;
            editable = aEditable;
            value = aValue;
        }

        CustomPropertyItem(GameplayPropertySheet.PropertyType aType, String aCategory, String aName, String aDescription, boolean aEditable, Object aValue, Object aMinValue) {
            this(aType, aCategory, aName, aDescription, aEditable, aValue);
            minValue = aMinValue;
        }

        CustomPropertyItem(GameplayPropertySheet.PropertyType aType, String aCategory, String aName, String aDescription, boolean aEditable, Object aValue, Object aMinValue, Object aMaxValue) {
            this(aType, aCategory, aName, aDescription, aEditable, aValue, aMinValue);
            maxValue = aMaxValue;
        }

        public GameplayPropertySheet.PropertyType getPropertyType() {
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

        public Float getMinValue() {
            return (Float) minValue;
        }

        public Float getMaxValue() {
            return (Float) maxValue;
        }

        public void setValue(Object aValue) {
            if (!value.equals(aValue)) {
                GameplayPropertySheet.this.changedProperties.add(type);
            }
            value = aValue;
        }
    }

    // TODO - ISPAYMENT and MAXDEEDS not used.  Why?
    public enum PropertyType {
        MOTD,

        NPCS,
        MAXPLAYERS,
        PVPSERVER,
        EPIC,
        HOMESERVER,
        KINGDOM,
        LOGINSERVER,
        ISPAYMENT,
        TESTSERVER,
        MAINTAINING,

        RANDOMSPAWNS,
        SPAWNPOINTJENNX,
        SPAWNPOINTJENNY,
        SPAWNPOINTMOLX,
        SPAWNPOINTMOLY,
        SPAWNPOINTLIBX,
        SPAWNPOINTLIBY,

        SKILLGAINRATE,
        ACTIONTIMER,
        SKBASIC,
        SKMIND,
        SKBC,
        SKFIGHT,
        SKOVERALL,
        CRMOD,

        UPKEEP,
        MAXDEED,
        FREEDEEDS,
        KINGSMONEY,
        TRADERMAX,
        TRADERINIT,

        MAXCREATURES,
        PERCENTAGG,
        TUNNELING,
        BREEDING,
        FIELDGROWTH,
        TREEGROWTH,
        HOTADELAY,

        TWITTERCONSUMERKEY,
        TWITTERCONSUMERSECRET,
        TWITTERAPPTOKEN,
        TWITTERAPPSECRET,

        ENDGAMEITEMS,
    }
}
