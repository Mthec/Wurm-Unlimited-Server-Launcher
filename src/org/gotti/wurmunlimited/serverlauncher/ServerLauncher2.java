package org.gotti.wurmunlimited.serverlauncher;

import javassist.Loader;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

public class ServerLauncher2 {

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

            loader.run("org.gotti.wurmunlimited.serverlauncher.DelegatedLauncher2", args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

