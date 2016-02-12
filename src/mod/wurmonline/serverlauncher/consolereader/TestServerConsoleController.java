package mod.wurmonline.serverlauncher.consolereader;

import com.wurmonline.server.Servers;
import com.wurmonline.server.utils.SimpleArgumentParser;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import org.gotti.wurmunlimited.modloader.ModLoader;
import org.gotti.wurmunlimited.modloader.ServerHook;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class TestServerConsoleController {
    public static void main(String[] args) {
        List<WurmMod> mods = null;
        try {
            mods = new ModLoader().loadModsFromModDir(Paths.get("mods"));
            ServerHook.createServerHook().addMods(mods);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        ServerConsoleController controller = new ServerConsoleController();
        Servers.argumets = new SimpleArgumentParser(new String[0], new HashSet<>());
        controller.setMods(mods);
        controller.startDB("Adventure");
    }
}
