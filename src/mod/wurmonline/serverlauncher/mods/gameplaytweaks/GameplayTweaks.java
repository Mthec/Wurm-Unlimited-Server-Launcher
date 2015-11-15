package mod.wurmonline.serverlauncher.mods.gameplaytweaks;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerProperties;
import com.wurmonline.server.Servers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;
import org.gotti.wurmunlimited.modloader.interfaces.WurmLoadDumpMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmUIMod;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameplayTweaks implements WurmMod, WurmUIMod, WurmLoadDumpMod {
    static Logger logger = Logger.getLogger(GameplayTweaks.class.getName());
    GameplayPropertySheet gameplayPropertySheet;
    static ServerGuiController controller;
    static ResourceBundle messages = LocaleHelper.getBundle("OfficialSettings");
    static ResourceBundle ui_messages = LocaleHelper.getBundle("UIWindow");
    String name = messages.getString("name");
    String CATEGORY = "gameplay.";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Region getRegion(ServerGuiController guiController) {
        controller = guiController;

        try {
            FXMLLoader fx = new FXMLLoader(GameplayTweaks.class.getResource("GameplayTweaks.fxml"), ui_messages);
            fx.setClassLoader(this.getClass().getClassLoader());
            return fx.load();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        Label label = new Label(ui_messages.getString("fxml_missing"));
        Pane pane = new Pane();
        pane.getChildren().add(label);
        return pane;
    }

    @FXML
    ScrollPane scrollPane;
    @FXML
    Label serverRunning;
    @FXML
    Button saveButton;

    @FXML
    void saveButtonClicked () {
        logger.info(messages.getString("save_button_clicked"));
        String error = gameplayPropertySheet.save();
        if (error != null && error.length() > 0 && error.equalsIgnoreCase("ok")) {
            controller.showInformationDialog(messages.getString("saved_title"),
                    messages.getString("saved_header"),
                    messages.getString("saved_message"));
        } else if (error != null && error.length() > 0) {
            controller.showErrorDialog(messages.getString("save_error_title"),
                    messages.getString("save_error_header"),
                    MessageFormat.format(messages.getString("save_error_message"), error));
        }
        refreshButtonClicked();
    }

    @FXML
    void refreshButtonClicked () {
        if (gameplayPropertySheet != null && gameplayPropertySheet.haveChanges()) {
            Optional<ButtonType> result = controller.showYesNoCancel(messages.getString("changes_title"),
                    messages.getString("changes_header"),
                    messages.getString("changes_message"));
            if (result.get() == ButtonType.CANCEL) {
                return;
            }
            else if (result.get() == ButtonType.YES) {
                saveButtonClicked();
                return;
            }
        }

        if (controller.serverIsRunning()) {
            serverRunning.setText(ui_messages.getString("block_changes_server_running"));
            saveButton.setDisable(true);
        }
        else {
            serverRunning.setText("");
            saveButton.setDisable(false);
            gameplayPropertySheet = new GameplayPropertySheet(Servers.localServer);
            scrollPane.setContent(gameplayPropertySheet);
            scrollPane.requestFocus();
        }
    }

    @FXML
    public void initialize () {
        refreshButtonClicked();
    }

    @Override
    public String getComment() {
        return messages.getString("gameplay_comment");
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
                switch (GameplayPropertySheet.PropertyType.valueOf(name).ordinal()) {
                    // Message of the Day
                    case 0:
                        current.setMotd(item);
                        break;
                    // Mode
                    case 1:
                        boolean npcs = Boolean.valueOf(item);
                        if (npcs != ServerProperties.getBoolean("NPCS", true)) {
                            ServerProperties.setValue("NPCS", Boolean.toString(npcs));
                            ServerProperties.checkProperties();
                        }
                        break;
                    case 2:
                        current.pLimit = Integer.valueOf(item);
                        break;
                    case 3:
                        current.PVPSERVER = Boolean.valueOf(item);
                        break;
                    case 4:
                        current.EPIC = Boolean.valueOf(item);
                        break;
                    case 5:
                        current.HOMESERVER = Boolean.valueOf(item);
                        break;
                    case 6:
                        current.KINGDOM = Byte.valueOf(item);
                        break;
                    case 7:
                        current.LOGINSERVER = Boolean.valueOf(item);
                        break;
                    case 8:
                        current.ISPAYMENT = false;
                        break;
                    case 9:
                        current.testServer = Boolean.valueOf(item);
                        break;
                    case 10:
                        current.maintaining = Boolean.valueOf(item);
                        break;
                    // Spawns
                    case 11:
                        current.randomSpawns = Boolean.valueOf(item);
                        break;
                    case 12:
                        current.SPAWNPOINTJENNX = Integer.valueOf(item);
                        break;
                    case 13:
                        current.SPAWNPOINTJENNY = Integer.valueOf(item);
                        break;
                    case 14:
                        current.SPAWNPOINTMOLX = Integer.valueOf(item);
                        break;
                    case 15:
                        current.SPAWNPOINTMOLY = Integer.valueOf(item);
                        break;
                    case 16:
                        current.SPAWNPOINTLIBX = Integer.valueOf(item);
                        break;
                    case 17:
                        current.SPAWNPOINTLIBY = Integer.valueOf(item);
                        break;
                    // Player
                    case 18:
                        current.setSkillGainRate(Float.valueOf(item));
                        break;
                    case 19:
                        current.setActionTimer(Float.valueOf(item));
                        break;
                    case 20:
                        current.setSkillbasicval(Float.valueOf(item));
                        break;
                    case 21:
                        current.setSkillmindval(Float.valueOf(item));
                        break;
                    case 22:
                        current.setSkillbcval(Float.valueOf(item));
                        break;
                    case 23:
                        current.setSkillfightval(Float.valueOf(item));
                        break;
                    case 24:
                        current.setSkilloverallval(Float.valueOf(item));
                        break;
                    case 25:
                        current.setCombatRatingModifier(Float.valueOf(item));
                        break;
                    // King
                    case 26:
                        current.setUpkeep(Boolean.valueOf(item));
                        break;
                    case 27:
                        current.setMaxDeedSize(Integer.valueOf(item));
                        break;
                    case 28:
                        current.setFreeDeeds(Boolean.valueOf(item));
                        break;
                    case 29:
                        current.setKingsmoneyAtRestart(Integer.valueOf(item) * 10000);
                        break;
                    case 30:
                        current.setTraderMaxIrons(Integer.valueOf(item) * 10000);
                        break;
                    case 31:
                        current.setInitialTraderIrons(Integer.valueOf(item) * 10000);
                        break;
                    // Environment
                    case 32:
                        current.maxCreatures = Integer.valueOf(item);
                        break;
                    case 33:
                        current.percentAggCreatures = Float.valueOf(item);
                        break;
                    case 34:
                        current.setTunnelingHits(Integer.valueOf(item));
                        break;
                    case 35:
                        current.setBreedingTimer(Long.valueOf(item));
                        break;
                    case 36:
                        current.setFieldGrowthTime((long) (Float.valueOf(item) * 3600.0F / 1000.0F));
                        break;
                    case 37:
                        current.treeGrowth = Integer.valueOf(item);
                    case 38:
                        current.setHotaDelay(Integer.valueOf(item));
                        break;
                    // Twitter
                    case 39:
                        current.setConsumerKeyToUse(item);
                        break;
                    case 40:
                        current.setConsumerSecret(item);
                        break;
                    case 41:
                        current.setApplicationToken(item);
                        break;
                    case 42:
                        current.setApplicationSecret(item);
                        break;
                }
            } catch (Exception ex) {
                logger.log(Level.INFO, MessageFormat.format(messages.getString("error"), ex.getMessage()), ex);
            }
        }

        current.saveNewGui(current.id);
        if(current.saveTwitter()) {
            logger.info(messages.getString("will_tweet"));
        } else {
            logger.info(messages.getString("wont_tweet"));
        }
        current.updateSpawns();
    }

    public Properties dumpSettings() {
        Properties properties = new Properties();
        ServerEntry current = Servers.localServer;
        for (GameplayPropertySheet.PropertyType prop : GameplayPropertySheet.PropertyType.values()) {
            try {
                switch(prop.ordinal()) {
                    // Message of the Day
                    case 0:
                        properties.setProperty(CATEGORY + prop.name(), current.getMotd());
                        break;
                    // Mode
                    case 1:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(ServerProperties.getBoolean("NPCS", true)));
                        break;
                    case 2:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.pLimit));
                        break;
                    case 3:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.PVPSERVER));
                        break;
                    case 4:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.EPIC));
                        break;
                    case 5:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.HOMESERVER));
                        break;
                    case 6:
                        properties.setProperty(CATEGORY + prop.name(), Byte.toString(current.KINGDOM));
                        break;
                    case 7:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.LOGINSERVER));
                        break;
                    case 8:
                        //properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.ISPAYMENT) + "# Not used.");
                        break;
                    case 9:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.testServer));
                        break;
                    case 10:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.maintaining));
                        break;
                    // Spawns
                    case 11:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.randomSpawns));
                        break;
                    case 12:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.SPAWNPOINTJENNX));
                        break;
                    case 13:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.SPAWNPOINTJENNY));
                        break;
                    case 14:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.SPAWNPOINTMOLX));
                        break;
                    case 15:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.SPAWNPOINTMOLY));
                        break;
                    case 16:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.SPAWNPOINTLIBX));
                        break;
                    case 17:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.SPAWNPOINTLIBY));
                        break;
                    // Player
                    case 18:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getSkillGainRate()));
                        break;
                    case 19:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getActionTimer()));
                        break;
                    case 20:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getSkillbasicval()));
                        break;
                    case 21:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getSkillmindval()));
                        break;
                    case 22:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getSkillbcval()));
                        break;
                    case 23:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getSkillfightval()));
                        break;
                    case 24:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getSkilloverallval()));
                        break;
                    case 25:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.getCombatRatingModifier()));
                        break;
                    // King
                    case 26:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.isUpkeep()));
                        break;
                    case 27:
                        //properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.getMaxDeedSize()) + "# Not enabled in official launcher.  Use at own risk.");
                        break;
                    case 28:
                        properties.setProperty(CATEGORY + prop.name(), Boolean.toString(current.isFreeDeeds()));
                        break;
                    case 29:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.getKingsmoneyAtRestart() / 10000));
                        break;
                    case 30:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.getTraderMaxIrons() / 10000));
                        break;
                    case 31:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.getInitialTraderIrons() / 10000));
                        break;
                    // Environment
                    case 32:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.maxCreatures));
                        break;
                    case 33:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString(current.percentAggCreatures));
                        break;
                    case 34:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.getTunnelingHits()));
                        break;
                    case 35:
                        properties.setProperty(CATEGORY + prop.name(), Long.toString(current.getBreedingTimer()));
                        break;
                    case 36:
                        properties.setProperty(CATEGORY + prop.name(), Float.toString((float) current.getFieldGrowthTime() / 1000.0F / 3600.0F));
                        break;
                    case 37:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.treeGrowth));
                        break;
                    case 38:
                        properties.setProperty(CATEGORY + prop.name(), Integer.toString(current.getHotaDelay()));
                        break;
                    // Twitter
                    case 39:
                        properties.setProperty(CATEGORY + prop.name(), current.getConsumerKey());
                        break;
                    case 40:
                        properties.setProperty(CATEGORY + prop.name(), current.getConsumerSecret());
                        break;
                    case 41:
                        properties.setProperty(CATEGORY + prop.name(), current.getApplicationToken());
                        break;
                    case 42:
                        properties.setProperty(CATEGORY + prop.name(), current.getApplicationSecret());
                        break;
                }
            } catch (Exception ex) {
                logger.log(Level.INFO, MessageFormat.format(messages.getString("error"), ex.getMessage()), ex);
            }
        }
        return properties;
    }
}
