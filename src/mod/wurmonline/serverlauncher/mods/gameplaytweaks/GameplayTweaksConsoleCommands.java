package mod.wurmonline.serverlauncher.mods.gameplaytweaks;

import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.ServerProperties;
import com.wurmonline.server.Servers;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.Command;
import mod.wurmonline.serverlauncher.consolereader.Menu;
import mod.wurmonline.serverlauncher.consolereader.Option;
import mod.wurmonline.serverlauncher.consolereader.Value;

import java.util.List;
import java.util.ResourceBundle;

public class GameplayTweaksConsoleCommands {
    static Option getOptions(ServerController controller, ResourceBundle messages) {
        return new Menu("settings", messages.getString("name"), new Option[] {
                new Value("motd", messages.getString("motd_help_text"), controller) {
                    @Override
                    protected String get(ServerEntry server) {
                        return server.getMotd();
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        server.setMotd(String.join(" ", tokens));
                        server.saveNewGui(server.id);
                    }
                },
                new Menu("modes", "Server Modes", new Option[]{
                        new Value("npcs", messages.getString("npcs_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return Boolean.toString(ServerProperties.getBoolean("NPCS", true));
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                boolean npcs = Boolean.parseBoolean(tokens.get(0));
                                if (npcs != ServerProperties.getBoolean("NPCS", true)) {
                                    ServerProperties.setValue("NPCS", Boolean.toString(npcs));
                                    ServerProperties.checkProperties();
                                }
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("max_players", messages.getString("max_players_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.pLimit);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.pLimit = Integer.valueOf(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("pvp", messages.getString("pvp_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.PVPSERVER);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.PVPSERVER = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("epic", messages.getString("epic_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.EPIC);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.EPIC = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("home", messages.getString("home_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.HOMESERVER);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.HOMESERVER = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("home_kingdom", messages.getString("home_kingdom_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.KINGDOM);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.KINGDOM = Byte.parseByte(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("login_server", messages.getString("login_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.LOGINSERVER);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.LOGINSERVER = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("test_server", messages.getString("test_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.testServer);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.testServer = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("maintenance", messages.getString("maintenance_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.maintaining);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.maintaining = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                }),
                new Menu("spawns", "Spawn Point Settings", new Option[]{
                        new Value("random_spawns", messages.getString("random_spawns_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.randomSpawns);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.randomSpawns = Boolean.parseBoolean(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("jenn_spawn_x", messages.getString("jenn_spawn_x_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.SPAWNPOINTJENNX);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.SPAWNPOINTJENNX = Integer.parseInt(tokens.get(0));
                                server.updateSpawns();
                            }
                        },
                        new Value("jenn_spawn_y", messages.getString("jenn_spawn_y_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.SPAWNPOINTJENNY);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.SPAWNPOINTJENNY = Integer.parseInt(tokens.get(0));
                                server.updateSpawns();
                            }
                        },
                        new Value("mol_spawn_x", messages.getString("mol_spawn_x_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.SPAWNPOINTMOLX);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.SPAWNPOINTMOLX = Integer.parseInt(tokens.get(0));
                                server.updateSpawns();
                            }
                        },
                        new Value("mol_spawn_y", messages.getString("mol_spawn_y_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.SPAWNPOINTMOLY);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.SPAWNPOINTMOLY = Integer.parseInt(tokens.get(0));
                                server.updateSpawns();
                            }
                        },
                        new Value("lib_spawn_x", messages.getString("lib_spawn_x_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.SPAWNPOINTLIBX);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.SPAWNPOINTLIBX = Integer.parseInt(tokens.get(0));
                                server.updateSpawns();
                            }
                        },
                        new Value("lib_spawn_y", messages.getString("lib_spawn_y_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.SPAWNPOINTLIBY);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.SPAWNPOINTLIBY = Integer.parseInt(tokens.get(0));
                                server.updateSpawns();
                            }
                        },
                }),
                // Skill
                new Menu("skill", "Skill Settings", new Option[]{
                        new Value("skill_multiplier", messages.getString("skill_gain_multiplier_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getSkillGainRate());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setSkillGainRate(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("action_timer", messages.getString("action_speed_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getActionTimer());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setActionTimer(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("characteristics", messages.getString("characteristics_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getSkillbasicval());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setSkillbasicval(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("mind_logic", messages.getString("mind_logic_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getSkillmindval());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setSkillmindval(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("body_control", messages.getString("body_control_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getSkillbcval());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setSkillbcval(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("fight_skill", messages.getString("fight_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getSkillfightval());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setSkillfightval(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("overall_skill", messages.getString("overall_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getSkilloverallval());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setSkilloverallval(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("combat_modifier", messages.getString("combat_rating_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getCombatRatingModifier());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setCombatRatingModifier(Float.parseFloat(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                }),
                // Deeds/Economy
                new Value("upkeep", messages.getString("upkeep_help_text"), controller) {
                    @Override
                    protected String get(ServerEntry server) {
                        return String.valueOf(server.isUpkeep());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        server.setUpkeep(Boolean.parseBoolean(tokens.get(0)));
                        server.saveNewGui(server.id);
                    }
                },
                new Value("free_deeds", messages.getString("deed_help_text"), controller) {
                    @Override
                    protected String get(ServerEntry server) {
                        return String.valueOf(server.isFreeDeeds());
                    }

                    @Override
                    protected void set(ServerEntry server, List<String> tokens) {
                        server.setFreeDeeds(Boolean.parseBoolean(tokens.get(0)));
                        server.saveNewGui(server.id);
                    }
                },
                new Menu("economy", "Economy Settings", new Option[]{
                        new Value("money_pool", messages.getString("money_pool_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getKingsmoneyAtRestart() / 10000);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setKingsmoneyAtRestart(Integer.parseInt(tokens.get(0)) * 10000);
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("trader_max_coin", messages.getString("trader_max_coin_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getTraderMaxIrons() / 10000);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setTraderMaxIrons(Integer.parseInt(tokens.get(0)) * 10000);
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("trader_start_coin", messages.getString("trader_start_coin_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getInitialTraderIrons() / 10000);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setInitialTraderIrons(Integer.parseInt(tokens.get(0)) * 10000);
                                server.saveNewGui(server.id);
                            }
                        },
                }),
                // Environment
                new Menu("environment", "Environment Settings", new Option[]{
                        new Value("max_creatures", messages.getString("max_creatures_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.maxCreatures);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.maxCreatures = Integer.parseInt(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("aggressive_creatures", messages.getString("aggressive_creatures_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.percentAggCreatures);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.percentAggCreatures = Float.parseFloat(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("mining_hits", messages.getString("mining_hits_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getTunnelingHits());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setTunnelingHits(Integer.parseInt(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("breeding_time", messages.getString("breeding_time_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getBreedingTimer());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setBreedingTimer(Long.parseLong(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("field_growth", messages.getString("field_growth_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getFieldGrowthTime());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setFieldGrowthTime((long) (Float.valueOf(tokens.get(0)) * 3600.0F / 1000.0F));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("tree_spread", messages.getString("tree_spread_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.treeGrowth);
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.treeGrowth = Integer.parseInt(tokens.get(0));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("hota_delay", messages.getString("hota_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return String.valueOf(server.getHotaDelay());
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setHotaDelay(Integer.parseInt(tokens.get(0)));
                                server.saveNewGui(server.id);
                            }
                        },
                }),
                // Twitter
                new Menu("twitter", "Twitter Settings", new Option[]{
                        new Value("consumer_key", messages.getString("consumer_key_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return server.getConsumerKey();
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setConsumerKeyToUse(String.join(" ", tokens));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("consumer_secret", messages.getString("consumer_secret_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return server.getConsumerSecret();
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setConsumerSecret(String.join(" ", tokens));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("application_token", messages.getString("application_token_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return server.getApplicationToken();
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setApplicationToken(String.join(" ", tokens));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Value("application_secret", messages.getString("application_secret_help_text"), controller) {
                            @Override
                            protected String get(ServerEntry server) {
                                return server.getApplicationSecret();
                            }

                            @Override
                            protected void set(ServerEntry server, List<String> tokens) {
                                server.setApplicationSecret(String.join(" ", tokens));
                                server.saveNewGui(server.id);
                            }
                        },
                        new Command("status", "Tweet status") {
                            @Override
                            public String action(List<String> tokens) {
                                // TODO - Change when gui separation is complete.
                                if (Servers.localServer.saveTwitter()) {
                                    return messages.getString("will_tweet");
                                } else {
                                    return messages.getString("wont_tweet");
                                }
                            }
                        }
                }),
        });
    }
}
