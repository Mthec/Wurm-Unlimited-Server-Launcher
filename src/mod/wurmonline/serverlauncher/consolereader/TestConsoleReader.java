package mod.wurmonline.serverlauncher.consolereader;


import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.ServerController;

import java.io.IOException;

public class TestConsoleReader {
    static ServerController controller;

    public static void start(ServerConsoleController controller) {
        TestConsoleReader.controller = controller;
        (new Thread(new ConsoleReader(controller))).start();
        // TODO - How to tell when ready to receive commands?
    }

    public static void main(String[] args) {
        controller = new FakeController();
        start((ServerConsoleController) controller);
    }

    static class FakeController extends ServerConsoleController {
        boolean running = false;

        @Override
        public void startDB(String dbName) {
            running = true;
            System.out.println("Started server!");
        }

        @Override
        public synchronized boolean shutdown(int time, String reason) {
            // TODO - What if server is not running.
            return true;
        }

        @Override
        public synchronized void setCurrentDir(String newCurrentDir) throws IOException {
            currentDir = newCurrentDir;
            System.out.println(String.format("Dir set to - %s", newCurrentDir));
        }

        @Override
        public boolean serverIsRunning() {
            return running;
        }
    }
}
