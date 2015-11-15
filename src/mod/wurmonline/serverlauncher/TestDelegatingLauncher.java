package mod.wurmonline.serverlauncher;

import javassist.Loader;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

public class TestDelegatingLauncher {

    public static void main(String[] args) {
        try {
            Loader loader = HookManager.getInstance().getLoader();
            loader.delegateLoadingOf("javafx.");
            loader.delegateLoadingOf("com.sun.");
            loader.delegateLoadingOf("org.controlsfx.");
            loader.delegateLoadingOf("impl.org.controlsfx");
            loader.delegateLoadingOf("com.mysql.");
            loader.delegateLoadingOf("org.sqlite.");
            loader.delegateLoadingOf("org.gotti.wurmunlimited.modloader.");
            loader.delegateLoadingOf("javassist.");

            Thread.currentThread().setContextClassLoader(loader);

            loader.run("mod.wurmonline.serverlauncher.TestServerMain", args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
