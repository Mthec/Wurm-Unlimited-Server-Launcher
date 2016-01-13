package mod.wurmonline.serverlauncher.mods.players;

import com.wurmonline.server.Server;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.Servers;
import com.wurmonline.server.gui.PlayerDBInterface;
import com.wurmonline.server.gui.PlayerData;
import mod.wurmonline.serverlauncher.consolereader.InvalidValue;
import mod.wurmonline.serverlauncher.consolereader.Menu;
import mod.wurmonline.serverlauncher.consolereader.Option;
import mod.wurmonline.serverlauncher.consolereader.Value;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class PlayerMenu extends Menu {
    static ResourceBundle players_messages;

    public PlayerMenu(String name, String text, ResourceBundle messages) {
        super(name, text, getPlayerMenus(messages));
    }

    private static Option[] getPlayerMenus(ResourceBundle messages) {
        players_messages = messages;
        PlayerDBInterface.loadAllData();
        PlayerDBInterface.loadAllPositionData();

        PlayerData[] players = PlayerDBInterface.getAllData();
        Option[] list = new Option[players.length];

        PlayerData player;
        for (int i = 0; i < players.length; ++i) {
            player = players[i];
            list[i] = new Menu(player.getName(), "Settings for " + player.getName(), getValues(player));
        }
        return list;
    }

    private static Option[] getValues(PlayerData playerData) {
        return new Option[] {
                new Value("name", players_messages.getString("name_help_text")) {
                    PlayerData player = playerData;

                    @Override
                    protected String get(ServerEntry server) {
                        return player.getName();
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        player.setName(tokens.get(0));
                        try {
                            player.save();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Value("position_x", players_messages.getString("position_x_help_text")) {
                    PlayerData player = playerData;

                    @Override
                    protected String get(ServerEntry server) {
                        return String.valueOf(player.getPosx());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        player.setPosx(Float.parseFloat(tokens.get(0)));
                        try {
                            player.savePosition();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Value("position_y", players_messages.getString("position_y_help_text")) {
                    PlayerData player = playerData;

                    @Override
                    protected String get(ServerEntry server) {
                        return String.valueOf(player.getPosy());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        player.setPosy(Float.parseFloat(tokens.get(0)));
                        try {
                            player.savePosition();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Value("power", players_messages.getString("power_help_text")) {
                    PlayerData player = playerData;

                    @Override
                    protected String get(ServerEntry server) {
                        return String.valueOf(player.getPower());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {
                        Integer power = Integer.valueOf(tokens.get(0));
                        if (power < 0 || power > 5) {
                            throw new InvalidValue(power.toString(), "Power must be between 0 and 5.");
                        }
                        player.setPower(power);
                        try {
                            player.save();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Value("server", players_messages.getString("current_server_help_text")) {
                    PlayerData player = playerData;

                    @Override
                    protected String get(ServerEntry server) {
                        return String.format("%s (%s)", String.valueOf(player.getServer()), Servers.getServerWithId(player.getServer()).getName());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        player.setServer(Integer.valueOf(tokens.get(0)));
                        try {
                            player.save();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Value("undead", players_messages.getString("undead_help_text")) {
                    PlayerData player = playerData;

                    @Override
                    protected String get(ServerEntry server) {
                        return String.valueOf(player.isUndead());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        if (!player.isUndead()) {
                            player.setUndeadType((byte) (1 + Server.rand.nextInt(3)));
                        } else {
                            player.setUndeadType((byte) 0);
                        }
                        try {
                            player.save();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
        };
    }
}
