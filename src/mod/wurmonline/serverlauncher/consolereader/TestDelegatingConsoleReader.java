package mod.wurmonline.serverlauncher.consolereader;

import javassist.Loader;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.ServerController;
import org.gotti.wurmunlimited.modloader.ModLoader;
import org.gotti.wurmunlimited.modloader.ServerHook;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class TestDelegatingConsoleReader {
    public static void main(String[] args) {
        try {
            Loader loader = HookManager.getInstance().getLoader();
            loader.delegateLoadingOf("javafx.");
            loader.delegateLoadingOf("com.sun.");
            loader.delegateLoadingOf("org.controlsfx.");
            loader.delegateLoadingOf("impl.org.controlsfx");
            loader.delegateLoadingOf("com.mysql.");
            loader.delegateLoadingOf("org.sqlite.");
            loader.delegateLoadingOf("org.gotti.wurmunlimited.modloader.classhooks.");
            loader.delegateLoadingOf("javassist.");

            Thread.currentThread().setContextClassLoader(loader);

            loader.run("mod.wurmonline.serverlauncher.consolereader.TestDelegatedConsoleReader", args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}

