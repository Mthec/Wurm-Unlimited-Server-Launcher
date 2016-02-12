package mod.wurmonline.serverlauncher.consolereader;

import com.wurmonline.server.Servers;
import com.wurmonline.server.utils.SimpleArgumentParser;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import org.gotti.wurmunlimited.modloader.ModLoader;
import org.gotti.wurmunlimited.modloader.ServerHook;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class TestDelegatedConsoleReader {
    static String MODS_DIR = "mods";

    public static void main(String[] args) {
        List<WurmMod> mods = new ArrayList<>();
        try {
            mods = new ModLoader().loadModsFromModDir(Paths.get(MODS_DIR));
            ServerHook.createServerHook().addMods(mods);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        ServerConsoleController controller = new ServerConsoleController();
        Servers.argumets = new SimpleArgumentParser(new String[0], new HashSet<>());
        controller.setMods(mods);
        //controller.startDB("Adventure");
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread t : threads) {
            if (Objects.equals(t.getName(), "Console Command Reader")) {
                t.stop();
                break;
            }
        }

        TestConsoleReader.start(controller);
    }
}
