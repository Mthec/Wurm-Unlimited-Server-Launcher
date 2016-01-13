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
import java.util.Objects;
import java.util.Set;

public class TestDelegatedConsoleReader {
    static String MODS_DIR = "mods";

    public static void main(String[] args) {
        try {
            List<WurmMod> mods = new ModLoader().loadModsFromModDir(Paths.get(MODS_DIR));
            ServerHook.createServerHook().addMods(mods);
            //for (WurmMod mod : mods) {
            //    if (mod instanceof WurmCommandLine) {
            //        WurmArgsMod argMod = (WurmArgsMod)mod;
            //        ACCEPTED_ARGS.addAll(argMod.getArgs());
            //    }
            //}
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        ServerConsoleController controller = new ServerConsoleController();
        Servers.argumets = new SimpleArgumentParser(new String[0], new HashSet<>());
        controller.startDB("Adventure");
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
