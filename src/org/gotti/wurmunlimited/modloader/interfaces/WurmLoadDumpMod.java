package org.gotti.wurmunlimited.modloader.interfaces;

import java.util.Properties;

public interface WurmLoadDumpMod {
    void loadSettings (Properties properties);
    Properties dumpSettings ();
    default String getComment() {return "";}
}
