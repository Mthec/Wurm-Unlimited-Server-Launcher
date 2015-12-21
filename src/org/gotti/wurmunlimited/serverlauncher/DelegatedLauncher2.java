package org.gotti.wurmunlimited.serverlauncher;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

public class DelegatedLauncher2 {

    public static void main(String[] args) {

        try {
            HookManager.getInstance().getLoader().run("mod.wurmonline.serverlauncher.ServerMain", args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
