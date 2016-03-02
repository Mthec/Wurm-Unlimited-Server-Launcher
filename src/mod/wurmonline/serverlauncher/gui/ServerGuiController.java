package mod.wurmonline.serverlauncher.gui;

import com.ibm.icu.text.MessageFormat;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerLauncher;
import com.wurmonline.server.Servers;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Callback;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerController;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmUIMod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ServerGuiController extends ServerController {
    protected Logger logger = Logger.getLogger(ServerGuiController.class.getName());
    protected ServerPropertySheet serverPropertySheet;
    protected List<Region> regions = new ArrayList<>();
    public static ResourceBundle gui_messages = LocaleHelper.getBundle("ServerGuiController");
    public static ResourceBundle ui_messages = LocaleHelper.getBundle("UIWindow");
    protected boolean serverStartError = false;

    @FXML
    protected ComboBox<String> databaseDropdown;
    @FXML
    protected ComboBox<String> localServerDropdown;
    @FXML
    protected Text statusText;
    // Run Server Tab
    @FXML
    protected Text serverToRun;
    @FXML
    protected GridPane serverControls;
    @FXML
    protected ChoiceBox<String> broadcastMessageType;
    @FXML
    protected TextField broadcastMessageText;
    @FXML
    protected Button startServerButton;
    @FXML
    protected Button startOfflineServerButton;
    @FXML
    protected ProgressIndicator startServerProgress;
    @FXML
    protected Spinner<Integer> serverShutdownTime;
    @FXML
    protected TextField serverShutdownReason;
    @FXML
    protected Text localServerText;
    // Network Settings Tab
    @FXML
    protected Accordion localRemoteAccordion;
    @FXML
    protected ListView<String> networkSettingsLocalServerList;
    @FXML
    protected ListView<String> networkSettingsRemoteServerList;
    @FXML
    protected ScrollPane networkSettingsScrollPane;
    @FXML
    protected Button saveNetworkSettingsButton;
    // Neighbour Tab
    @FXML
    protected Text neighbourText;
    // Server Settings Tab
    @FXML
    protected ListView<String> serverSettingsList;
    @FXML
    protected AnchorPane serverSettingsPane;
    @FXML
    protected ComboBox<String> north;
    @FXML
    protected ComboBox<String> south;
    @FXML
    protected ComboBox<String> east;
    @FXML
    protected ComboBox<String> west;
    // Create Database Tab
    @FXML
    protected TextField newDatabaseName;
    @FXML
    protected TextField newDatabaseServerName;

    // TODO - Is this important?
    public ServerGuiController() {
    }

    public ServerGuiController(ServerLauncher launchInstance) {
        currentServer = launchInstance;
    }

    void disableAllControls() {
        databaseDropdown.setDisable(true);
        disableRunServerTab();
        disableNetworkSettingsTab();
        disableServerSettingsTab();
    }

    // Database Dropdown
    void populateDatabaseDropdown() {
        disableRunServerTab();
        rebuilding = true;

        databaseDropdown.getItems().clear();
        disableAllControls();

        Path currentRelativePath = Paths.get("");
        File currentDirectory = new File(currentRelativePath.toAbsolutePath().toString());

        for (File gameDir : currentDirectory.listFiles()) {
            boolean add = false;
            boolean skip = false;
            if (gameDir.isDirectory()) {

                for (File file : gameDir.listFiles()) {
                    if (!file.isDirectory() && file.getName().equalsIgnoreCase("originaldir")) {
                        skip = true;
                    }
                    if (!file.isDirectory() && file.getName().equalsIgnoreCase("gamedir")) {
                        add = true;
                    }
                }
            }

            if (add && !skip) {
                databaseDropdown.getItems().add(gameDir.getName());
            }
        }

        // TODO - Switching when server is running causes error.
        if (serverIsRunning()) {
            databaseDropdown.setDisable(true);
        } else {
            databaseDropdown.setDisable(false);
        }
        rebuilding = false;
        databaseDropdown.getSelectionModel().select(currentDir);
    }

    @FXML
    void databaseDropdownChanged() {
        if (!rebuilding) {
            logger.info(gui_messages.getString("database_dropdown_change"));

            String selectedGame = databaseDropdown.getValue();
            try {
                setCurrentDir(selectedGame);
            } catch (IOException ex) {
                ex.printStackTrace();
                showErrorDialog(gui_messages.getString("error"),
                        gui_messages.getString("error_select_header"),
                        MessageFormat.format(gui_messages.getString("error_select_message"), ex.getMessage()));
                // Try re-populating.
                populateDatabaseDropdown();
            }
            loadAllServers(true);
            updateRunServerTab();
            updateNetworkSettingsTab();
            updateServerSettingsTab();
        }
    }

    void populateLocalServerDropdown() {
        localServerDropdown.getItems().clear();
        for (ServerEntry server : localServers) {
            localServerDropdown.getItems().add(server.getName());
        }
        localServerDropdown.getSelectionModel().selectFirst();
        // Temporary, till multiple servers are available.
        localServerDropdown.setDisable(true);
    }

    void localServerDropdownChanged() {
        // TODO - Multiple local servers.
    }

    // Run Server tab
    void updateRunServerTab() {
        String serverName = Servers.getLocalServerName();
        if (serverIsRunning() && !serverStartError) {
            statusText.setText(MessageFormat.format(ui_messages.getString("server_running_message"), currentDir, serverName));
            serverToRun.setText(MessageFormat.format(ui_messages.getString("database_server_name"), currentDir, serverName));
            startServerButton.setDisable(true);
            startOfflineServerButton.setDisable(true);
            startServerProgress.setVisible(true);
            startServerProgress.setProgress(1.0);
            serverControls.setDisable(false);
        } else if (localServers.isEmpty()) {
            statusText.setText(ui_messages.getString("no_local_servers"));
            disableRunServerTab();
        } else {
            statusText.setText(ui_messages.getString("ready"));
            serverToRun.setText(serverName);
            startServerButton.setDisable(false);
            startOfflineServerButton.setDisable(false);
            startServerProgress.setVisible(false);
            serverControls.setDisable(true);
        }
    }

    void disableRunServerTab() {
        serverToRun.setText("");
        startServerButton.setDisable(true);
        startOfflineServerButton.setDisable(true);
        startServerProgress.setVisible(false);
        serverControls.setDisable(true);
    }

    // TODO - Needs UI feedback.
    @FXML
    void broadcastMessageButtonClicked() {
        if (serverIsRunning()) {
            switch (broadcastMessageType.getSelectionModel().getSelectedIndex()) {
                case 0:
                    currentServer.getServer().broadCastAlert(broadcastMessageText.getText());
                    break;
                case 1:
                    currentServer.getServer().broadCastNormal(broadcastMessageText.getText());
                    break;
                case 2:
                    currentServer.getServer().broadCastSafe(broadcastMessageText.getText());
                    break;
            }
        }
    }

    // TODO - Move server to new thread?
    @FXML
    void startServerButtonClicked() {
        if (!serverPropertySheet.checkIfIpIsValid()) {
            showErrorDialog(gui_messages.getString("error_start_title"), MessageFormat.format(gui_messages.getString("error_ip_header"), serverPropertySheet.getCurrentServerEntry().EXTERNALIP), gui_messages.getString("error_ip_message"), true);
        } else {
            startServerClicked(false);
        }
    }

    @FXML
    void startOfflineServerButtonClicked() {
        startServerClicked(true);
    }

    private void startServerClicked(boolean offline) {
        if (currentServer != null) {
            if (currentServer.wasStarted()) {
                disableAllControls();
                showErrorDialog(gui_messages.getString("error_start_title"), gui_messages.getString("error_already_started_message"), gui_messages.getString("error_gui_restart"));
                return;
            }
        } else {
            currentServer = new ServerLauncher();
        }
        startingServerState();
        try {
            startServer(offline);
            serverStartError = false;
        } catch (IOException ex) {
            showErrorDialog(gui_messages.getString("error_start_title"), gui_messages.getString("error_start_message"), ex.getMessage());
            serverStartError = true;
            statusText.setText(ui_messages.getString("status_error"));
        }
        startedServerState();
    }

    // Server is starting up.
    void startingServerState() {
        statusText.setText((MessageFormat.format(ui_messages.getString("server_starting_message"), currentDir, Servers.localServer.getName())));
        disableAllControls();
        startServerProgress.setVisible(true);
        startServerProgress.setProgress(-1.0);
    }

    // Server is running.
    void startedServerState() {
        // TODO - Switching when server is running causes error.
        updateRunServerTab();
        updateNetworkSettingsTab();
        updateServerSettingsTab();
    }

    @FXML
    void shutdownButtonClicked() {
        if (currentServer != null) {
            disableAllControls();
            // TODO - Stop Launcher exit.
            shutdown(serverShutdownTime.getValue(), serverShutdownReason.getText());
            statusText.setText(MessageFormat.format(ui_messages.getString("server_shutting_down_message"), currentDir, Servers.localServer.getName()));
        } else {
            showErrorDialog(gui_messages.getString("error_shutdown"), gui_messages.getString("error_shutdown_header"), gui_messages.getString("error_shutdown_message"));
        }
    }

    // Network Settings Tab
    void updateNetworkSettingsTab() {
        updateNetworkSettingsTab(true);
    }

    void updateNetworkSettingsTab(boolean reload) {
        if (reload) {
            networkSettingsLocalServerList.getItems().clear();
            networkSettingsRemoteServerList.getItems().clear();
            serverPropertySheet = null;

            populateLocalServerDropdown();
            updateNeighboursTab();
            for (ServerEntry server : localServers) {
                serverPropertySheet = new ServerPropertySheet(server);
                // TODO - Causes UI issue, for some reason.
                networkSettingsScrollPane.setContent(serverPropertySheet);
                networkSettingsScrollPane.requestFocus();
                networkSettingsLocalServerList.getItems().add(server.getName());
            }
            for (ServerEntry server : remoteServers) {
                networkSettingsRemoteServerList.getItems().add(server.getName());
            }
            localRemoteAccordion.getPanes().get(0).setExpanded(true);

            if (serverPropertySheet == null && !remoteServers.isEmpty()) {
                serverPropertySheet = new ServerPropertySheet(remoteServers.get(0));
                networkSettingsScrollPane.setContent(serverPropertySheet);
                networkSettingsScrollPane.requestFocus();
                localRemoteAccordion.getPanes().get(1).setExpanded(true);
            }
        }

        networkSettingsLocalServerList.setDisable(false);
        networkSettingsRemoteServerList.setDisable(false);
        saveNetworkSettingsButton.setDisable(false);

        localServerText.setText("");
        if (serverPropertySheet != null) {
            if (serverIsRunning()) {
                serverPropertySheet.setReadOnly();
                serverPropertySheet.setDisable(true);
                localServerText.setText(ui_messages.getString("block_changes_server_running"));
            } else {
                serverPropertySheet.setDisable(false);
            }
        }
    }

    void disableNetworkSettingsTab() {
        if (serverPropertySheet != null) {
            serverPropertySheet.setDisable(true);
        }
        networkSettingsLocalServerList.setDisable(true);
        networkSettingsRemoteServerList.setDisable(true);
        saveNetworkSettingsButton.setDisable(true);
    }

    @FXML
    void networkSettingsLocalServerListChanged() {
        int idx = networkSettingsLocalServerList.getSelectionModel().getSelectedIndex();
        if (idx != -1) {
            networkSettingsRemoteServerList.getSelectionModel().clearSelection();
            serverPropertySheet = new ServerPropertySheet(localServers.get(idx));
            networkSettingsScrollPane.setContent(serverPropertySheet);
            networkSettingsScrollPane.requestFocus();
            updateNetworkSettingsTab(false);
        }
    }

    @FXML
    void networkSettingsRemoteServerListChanged() {
        int idx = networkSettingsRemoteServerList.getSelectionModel().getSelectedIndex();
        if (idx != -1) {
            networkSettingsLocalServerList.getSelectionModel().clearSelection();
            serverPropertySheet = new ServerPropertySheet(remoteServers.get(idx));
            networkSettingsScrollPane.setContent(serverPropertySheet);
            networkSettingsScrollPane.requestFocus();
            updateNetworkSettingsTab(false);
        }
    }

    @FXML
    void saveNetworkSettings() {
        logger.info(gui_messages.getString("saving_network"));
        String error = serverPropertySheet.save();
        if (error != null && error.length() > 0 && !error.toLowerCase().contains("invalid")) {
            showInformationDialog(gui_messages.getString("saved_title"), gui_messages.getString("saved_header"), error);
        }
        // TODO - Replace dialog.
        else if (error != null && error.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(gui_messages.getString("error_save_title"));
            alert.setHeaderText(gui_messages.getString("error_save_header"));
            alert.setContentText(gui_messages.getString("error_save_message"));
            Label label = new Label("These are the errors:");
            TextArea textArea = new TextArea(error);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(1.7976931348623157E308D);
            textArea.setMaxHeight(1.7976931348623157E308D);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(1.7976931348623157E308D);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
        }
        updateRunServerTab();
        updateNetworkSettingsTab();
        updateServerSettingsTab();
    }

    @FXML
    void addServerButtonClicked() {
        ButtonType result = checkIfWeWantToSaveTab();
        if (result != ButtonType.CANCEL) {
            try {
                ServerEntry newServer = addServer(ui_messages.getString("new_server_name"));
                if (newServer.isLocal) {
                    networkSettingsLocalServerList.getItems().add(newServer.getName());
                    networkSettingsLocalServerList.getSelectionModel().selectLast();
                } else {
                    networkSettingsRemoteServerList.getItems().add(newServer.getName());
                    networkSettingsRemoteServerList.getSelectionModel().selectLast();
                }

            } catch (CreateServerException ex) {
                showErrorDialog(gui_messages.getString("error_add_server_title"), ex.getErrorReason(), ex.getErrorMessage());
            }
        }
    }

    @FXML
    void deleteServerButtonClicked() {
        ServerEntry server;
        if (serverPropertySheet != null) {
            server = serverPropertySheet.getCurrentServerEntry();
        } else {
            showErrorDialog(gui_messages.getString("error_delete_server_title"), gui_messages.getString("error_delete_server_header"), gui_messages.getString("error_delete_server_message"));
            return;
        }
        if (deleteServer(server)) {
            populateDatabaseDropdown();
        }
    }

    // Neighbours
    void updateNeighboursTab() {
        if (serverIsRunning()) {
            neighbourText.setText(ui_messages.getString("block_changes_server_running"));
            neighbourText.getParent().setDisable(true);
            return;
        } else {
            neighbourText.setText("");
            neighbourText.getParent().setDisable(false);
        }

        north.getItems().clear();
        south.getItems().clear();
        east.getItems().clear();
        west.getItems().clear();
        int northIndex = -1;
        int southIndex = -1;
        int westIndex = -1;
        int eastIndex = -1;
        north.getItems().add(ui_messages.getString("none"));
        south.getItems().add(ui_messages.getString("none"));
        east.getItems().add(ui_messages.getString("none"));
        west.getItems().add(ui_messages.getString("none"));


        if (!localServers.isEmpty()) {
            ServerEntry server;
            for (int i = 0; i < remoteServers.size(); ++i) {
                server = remoteServers.get(i);
                if (!server.isLocal) {
                    north.getItems().add(server.getName());
                    if (Servers.localServer.serverNorth == server) {
                        northIndex = i;
                    }
                    south.getItems().add(server.getName());
                    if (Servers.localServer.serverSouth == server) {
                        southIndex = i;
                    }
                    east.getItems().add(server.getName());
                    if (Servers.localServer.serverEast == server) {
                        eastIndex = i;
                    }
                    west.getItems().add(server.getName());
                    if (Servers.localServer.serverWest == server) {
                        westIndex = i;
                    }
                }
            }

            if (northIndex != -1) {
                north.getSelectionModel().select(northIndex + 1);
            }
            if (southIndex != -1) {
                south.getSelectionModel().select(southIndex + 1);
            }
            if (eastIndex != -1) {
                east.getSelectionModel().select(eastIndex + 1);
            }
            if (westIndex != -1) {
                west.getSelectionModel().select(westIndex + 1);
            }
        }
    }

    @FXML
    void saveNeighboursButtonClicked() {
        int idx = west.getSelectionModel().getSelectedIndex();
        ServerEntry entry;
        if (idx > 0) {
            entry = remoteServers.get(idx - 1);
            if (Servers.localServer.serverWest != entry) {
                Servers.addServerNeighbour(entry.id, "WEST");
            }
        } else if (Servers.localServer.serverWest != null) {
            Servers.deleteServerNeighbour("WEST");
        }

        idx = north.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            entry = remoteServers.get(idx - 1);
            if (Servers.localServer.serverNorth != entry) {
                Servers.addServerNeighbour(entry.id, "NORTH");
            }
        } else if (Servers.localServer.serverNorth != null) {
            Servers.deleteServerNeighbour("NORTH");
        }

        idx = south.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            entry = remoteServers.get(idx - 1);
            if (Servers.localServer.serverSouth != entry) {
                Servers.addServerNeighbour(entry.id, "SOUTH");
            }
        } else if (Servers.localServer.serverSouth != null) {
            Servers.deleteServerNeighbour("SOUTH");
        }

        idx = east.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            entry = remoteServers.get(idx - 1);
            if (Servers.localServer.serverEast != entry) {
                Servers.addServerNeighbour(entry.id, "EAST");
            }
        } else if (Servers.localServer.serverEast != null) {
            Servers.deleteServerNeighbour("EAST");
        }
    }

    // Server Settings Tab
    void populateServerSettingsList() {
        regions.clear();
        serverSettingsList.getItems().clear();

        for (WurmMod mod : mods) {
            if (mod instanceof WurmUIMod) {
                WurmUIMod uimod = (WurmUIMod) mod;
                Region region = uimod.getRegion(this);
                regions.add(region);
                serverSettingsList.getItems().add(uimod.getName());
            }
        }
    }

    @FXML
    void serverSettingsListChanged() {
        int idx = serverSettingsList.getSelectionModel().getSelectedIndex();
        serverSettingsPane.getChildren().clear();
        Region region = regions.get(idx);
        serverSettingsPane.getChildren().add(region);
        AnchorPane.setTopAnchor(region, 0.0);
        AnchorPane.setRightAnchor(region, 0.0);
        AnchorPane.setBottomAnchor(region, 0.0);
        AnchorPane.setLeftAnchor(region, 0.0);
        serverSettingsPane.setMinWidth(region.getWidth());
        serverSettingsPane.setMinHeight(region.getHeight());
    }

    void updateServerSettingsTab() {
        serverSettingsList.setDisable(false);
        populateServerSettingsList();
    }

    void disableServerSettingsTab() {
        serverSettingsList.setDisable(true);
        serverSettingsPane.getChildren().clear();
    }

    // Create Database Tab
    // TODO - Needs a base server to copy?  Need to provide a basic one?
    // TODO - Move database saving to new thread?
    // TODO - Maps Tab/List.
    @FXML
    void addDatabaseButtonClicked() {
        try {
            addDatabase(newDatabaseName.getText(), newDatabaseServerName.getText());
            populateDatabaseDropdown();
        } catch (CreateServerException ex) {
            showErrorDialog(gui_messages.getString("error_create_db_title"), ex.getErrorReason(), ex.getErrorMessage());
        }
    }

    // TODO - Add list and delete button to Create Database page?
    @FXML
    void deleteDatabaseButtonClicked() {
        boolean isDeleted;
        try {
            isDeleted = deleteDatabase();
        } catch (DeleteServerException ex) {
            showErrorDialog(gui_messages.getString("error_delete_db_title"), ex.getErrorReason(), ex.getErrorMessage());
            return;
        }
        if (isDeleted) {
            loadAllServers(true);
            populateDatabaseDropdown();
            updateNetworkSettingsTab();
            populateServerSettingsList();
        }
    }

    @FXML
    void copyCurrentDatabase() {
        try {
            copyCurrentDatabase(newDatabaseName.getText());
            populateDatabaseDropdown();
        } catch (CreateServerException ex) {
            showErrorDialog(gui_messages.getString("error_copy_db_title"), ex.getErrorReason(), ex.getErrorMessage());
        }
    }

    // Start Up
    @FXML
    void initialize() {
        setup();

        EventHandler<InputEvent> checkEvent = ((event) -> {
            ButtonType result = checkIfWeWantToSaveTab();
            if (result == ButtonType.CANCEL) {
                event.consume();
            }
        });
        Callback<ListView<String>, ListCell<String>> newCellFactory = (lv -> {
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, checkEvent);
            cell.addEventFilter(KeyEvent.KEY_PRESSED, checkEvent);
            return cell;
        });

        databaseDropdown.setCellFactory(newCellFactory);
        databaseDropdown.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> databaseDropdownChanged());
        localServerDropdown.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> localServerDropdownChanged());
        networkSettingsLocalServerList.setCellFactory(newCellFactory);
        networkSettingsLocalServerList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> networkSettingsLocalServerListChanged());
        networkSettingsRemoteServerList.setCellFactory(newCellFactory);
        networkSettingsRemoteServerList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> networkSettingsRemoteServerListChanged());
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000);
        serverShutdownTime.setValueFactory(factory);
        TextFormatter<Integer> serverShutdownText = new TextFormatter<>(factory.getConverter(), factory.getValue());
        serverShutdownTime.getEditor().setTextFormatter(serverShutdownText);
        factory.valueProperty().bindBidirectional(serverShutdownText.valueProperty());

        serverSettingsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> serverSettingsListChanged());

        broadcastMessageType.getItems().add(ui_messages.getString("broadcast_alert"));
        broadcastMessageType.getItems().add(ui_messages.getString("broadcast_normal"));
        broadcastMessageType.getItems().add(ui_messages.getString("broadcast_safe"));
        broadcastMessageType.getSelectionModel().selectFirst();
    }

    // Mods not loaded by initialize.
    public void shown() {
        populateDatabaseDropdown();
    }

    public ButtonType checkIfWeWantToSaveTab() {
        if (serverPropertySheet != null && serverPropertySheet.haveChanges()) {
            ButtonType result = showYesNoCancel(gui_messages.getString("changes_title"), gui_messages.getString("changes_header"), gui_messages.getString("changes_message")).get();
            if (result == ButtonType.YES) {
                saveNetworkSettings();
            }
            return result;
        }
        return new ButtonType("", ButtonBar.ButtonData.YES);
    }

    // Dialogs
    public void showErrorDialog(String errorTitle, String errorHeader, String errorContent, boolean isResizable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(errorTitle);
        alert.setHeaderText(errorHeader);
        alert.setContentText(errorContent);
        alert.setResizable(isResizable);
        alert.showAndWait();
    }

    public void showErrorDialog(String errorTitle, String errorHeader, String errorContent) {
        showErrorDialog(errorTitle, errorHeader, errorContent, false);
    }

    public void showInformationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Optional<ButtonType> showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public Optional<ButtonType> showYesNoDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public Optional<ButtonType> showYesNoCancel(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    protected boolean askConfirmation(String title, String header, String content) {
        Optional<ButtonType> result = showConfirmationDialog(title, header, content);
        return result.get() == ButtonType.OK;
    }

    protected boolean askYesNo(String title, String header, String content) {
        Optional<ButtonType> result = showYesNoDialog(title, header, content);
        return result.get() == ButtonType.YES;
    }
}
