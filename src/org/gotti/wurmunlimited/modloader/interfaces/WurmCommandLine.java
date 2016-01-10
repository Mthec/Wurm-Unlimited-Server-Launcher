package org.gotti.wurmunlimited.modloader.interfaces;

import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.Option;

public interface WurmCommandLine {
    Option getOptions(ServerController controller);
}
