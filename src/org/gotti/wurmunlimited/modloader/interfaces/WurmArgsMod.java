package org.gotti.wurmunlimited.modloader.interfaces;

import mod.wurmonline.serverlauncher.ServerController;

import java.util.Set;

public interface WurmArgsMod {
    Set<String> getArgs();

    default void parseArgs(ServerController controller) {
    }
}
