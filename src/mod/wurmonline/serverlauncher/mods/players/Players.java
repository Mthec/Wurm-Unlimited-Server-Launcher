package mod.wurmonline.serverlauncher.mods.players;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.gui.PlayerDBInterface;
import com.wurmonline.server.gui.PlayerData;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.Menu;
import mod.wurmonline.serverlauncher.consolereader.Option;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmCommandLine;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmUIMod;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO - Add player deletion.
public class Players implements WurmMod, WurmUIMod, Configurable, WurmCommandLine {
    Logger logger = Logger.getLogger(Players.class.getName());
    String name;
    PlayerPropertySheet playerPropertySheet;
    boolean resettingPlayers;
    static ServerGuiController controller;
    static ResourceBundle players_messages = LocaleHelper.getBundle("Players");

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Region getRegion(ServerGuiController guiController) {
        controller = guiController;
        try {
            FXMLLoader fx = new FXMLLoader(Players.class.getResource("Players.fxml"), players_messages);
            fx.setClassLoader(this.getClass().getClassLoader());
            return fx.load();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        Label label = new Label(players_messages.getString("fxml_missing"));
        Pane pane = new Pane();
        pane.getChildren().add(label);
        return pane;
    }

    @Override
    public void configure(Properties properties) {
        name = properties.getProperty("name");
    }

    @FXML
    Button savePlayerButton;
    @FXML
    ListView<String> playersList;
    @FXML
    ScrollPane playersPane;

    // TODO - BUG - Somewhere in changing the player positions.  Not my fault, honest!
    @FXML
    void savePlayerBtnClicked() {
        logger.info(players_messages.getString("save_button_clicked"));
        String error = playerPropertySheet.save();
        if (error != null && error.length() > 0 && error.equalsIgnoreCase("ok")) {
            controller.showInformationDialog(players_messages.getString("saved_title"),
                    players_messages.getString("saved_header"),
                    players_messages.getString("saved_message"));

            if (controller.serverIsRunning() && playerPropertySheet != null && playerPropertySheet.getCurrentData() != null) {
                PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(playerPropertySheet.getCurrentData().getWurmid());
                if (info != null) {
                    info.loaded = false;

                    try {
                        info.load();
                    } catch (IOException ex) {
                        logger.log(Level.WARNING, MessageFormat.format(players_messages.getString("failed_loading"), info.getName()), ex);
                    }
                }
            }
        } else if (error != null && error.length() > 0) {
            controller.showErrorDialog(players_messages.getString("save_error_title"),
                    players_messages.getString("save_error_header"),
                    MessageFormat.format(players_messages.getString("save_error_message"), error));
        }
        populatePlayersList();
    }

    @FXML
    void refreshButtonClicked() {
        populatePlayersList();
    }

    @FXML
    void selectPlayerListChanged() {
        if (!resettingPlayers) {
            String name = playersList.getSelectionModel().getSelectedItem();
            logger.info(MessageFormat.format(players_messages.getString("selecting_player"), name));
            PlayerData data = PlayerDBInterface.getPlayerData(name);
            if (data != null) {
                playerPropertySheet = new PlayerPropertySheet(data, players_messages);
                playersPane.setContent(playerPropertySheet);
                playersPane.requestFocus();
            }
        }
    }

    void populatePlayersList() {
        resettingPlayers = true;
        playersList.getItems().clear();
        PlayerDBInterface.loadAllData();
        PlayerDBInterface.loadAllPositionData();

        for (PlayerData entry : PlayerDBInterface.getAllData()) {
            playersList.getItems().add(entry.getName());
        }

        resettingPlayers = false;
    }

    @FXML
    void initialize() {
        populatePlayersList();
        playersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectPlayerListChanged());
    }

    @Override
    public Option getOption(ServerController controller) {
        if (controller != null && controller.isInitialized()) {
            return new PlayerMenu("players", "Player Entries", players_messages.getString("players_console_help"), players_messages);
        } else {
            return new Menu("players", "Player Entries", players_messages.getString("players_console_help"), new Option[0]) {
                @Override
                public String action(List<String> tokens) {
                    return "Please select a server first.";
                }

                @Override
                public Menu get() {
                    return getParent();
                }
            };
        }
    }
}