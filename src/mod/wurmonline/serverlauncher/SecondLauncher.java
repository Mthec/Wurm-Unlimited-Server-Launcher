package mod.wurmonline.serverlauncher;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

public class SecondLauncher {

    public static void main(String[] args) {

        try {
            HookManager.getInstance().getLoader().run("mod.wurmonline.serverlauncher.ServerMain", args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
