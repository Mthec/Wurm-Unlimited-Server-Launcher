package org.gotti.wurmunlimited.modloader.interfaces;

import javafx.scene.layout.Region;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;

public interface WurmUIMod {
    Region getRegion(ServerGuiController controller);

    String getName();
}
