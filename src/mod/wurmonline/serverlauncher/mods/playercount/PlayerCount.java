package mod.wurmonline.serverlauncher.mods.playercount;

import com.wurmonline.server.Players;
import com.wurmonline.server.players.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.Command;
import mod.wurmonline.serverlauncher.consolereader.Menu;
import mod.wurmonline.serverlauncher.consolereader.Option;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;
import org.gotti.wurmunlimited.modloader.interfaces.WurmCommandLine;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmUIMod;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerCount implements WurmMod, WurmUIMod, Initializable, WurmCommandLine {
    Logger logger = Logger.getLogger(PlayerCount.class.getName());
    static ServerGuiController controller;
    static ResourceBundle count_messages = LocaleHelper.getBundle("PlayerCount");
    static ResourceBundle ui_messages = LocaleHelper.getBundle("UIWindow");
    String name = count_messages.getString("player_count_name");

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Region getRegion(ServerGuiController guiController) {
        controller = guiController;

        try {
            FXMLLoader fx = new FXMLLoader(PlayerCount.class.getResource("PlayerCount.fxml"), count_messages);
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
    Label serverStatus;
    @FXML
    TableView<PlayerEntry> playersList;
    @FXML
    TableColumn<PlayerEntry, String> idCol;
    @FXML
    TableColumn<PlayerEntry, String> nameCol;
    @FXML
    TableColumn<PlayerEntry, String> kingdomCol;

    @FXML
    void refreshButtonClicked () {
        playersList.getItems().clear();
        if (!controller.serverIsRunning()) {
            serverStatus.setText(count_messages.getString("server_not_running"));
        }
        else {
            serverStatus.setText(count_messages.getString("player_count_title"));
            for (Player player : Players.getInstance().getPlayers()) {
                playersList.getItems().add(new PlayerEntry(player));
            }
        }
    }

    @FXML
    public void initialize (URL url, ResourceBundle bundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        kingdomCol.setCellValueFactory(new PropertyValueFactory<>("kingdom"));

        refreshButtonClicked();
    }

    @Override
    public Option getOptions(ServerController controller) {
        return new Menu("online_players", "Online Player Information.", new Option[]{
                new Command("player_count", "Count logged in players.") {
                    @Override
                    public String action() {
                        if (controller == null || !controller.serverIsRunning()) {
                            return count_messages.getString("server_not_running");
                        }
                        return String.format("There are %s players logged in.", Players.getInstance().getPlayers().length);
                    }
                },
                new Command("list_players", "List all logged in players.") {
                    @Override
                    public String action() {
                        if (controller == null || !controller.serverIsRunning()) {
                            return count_messages.getString("server_not_running");
                        }
                        return Arrays.toString(Players.getInstance().getPlayers());
                    }
                }
        });
    }

    public class PlayerEntry {
        SimpleStringProperty id;
        SimpleStringProperty name;
        SimpleStringProperty kingdom;

        PlayerEntry (Player player) {
            id = new SimpleStringProperty(Long.toString(player.getWurmId()));
            name = new SimpleStringProperty(player.getName());
            kingdom = new SimpleStringProperty(player.getKingdomName());
        }

        public String getId () {
            return id.get();
        }

        public void setId (String newId) {
            id.set(newId);
        }

        public String getName () {
            return name.get();
        }

        public void setName (String newName) {
            name.set(newName);
        }

        public String getKingdom () {
            return kingdom.get();
        }

        public void setKingdom (String newKingdom) {
            kingdom.set(newKingdom);
        }
    }
}