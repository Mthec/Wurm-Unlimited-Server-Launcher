package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.ServerConsoleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ServerControlsLiveTest {
    ServerConsoleController controller;

    @Before
    public void setUp() throws Exception {
        controller = new ServerConsoleController();
    }

    @After
    public void tearDown() throws Exception {
        if (controller.serverIsRunning()) {
            controller.shutdown(0, "");
        }
        controller = null;
    }

    @Test
    public void testGetOptionsStart() throws Exception {
        new ServerControls().getOptions(controller)[3].getOption("Adventure").action(null);
        new ServerControls().getOptions(controller)[0].action(null);

        wait(5000);

        assertTrue(controller.serverIsRunning());
    }

    // TODO - Test parameters.
    @Test
    public void testGetOptionsShutdown() throws Exception {
        new ServerControls().getOptions(controller)[3].getOption("Adventure").action(null);
        new ServerControls().getOptions(controller)[0].action(null);

        wait(5000);

        assertTrue(controller.serverIsRunning());

        new ServerControls().getOptions(controller)[3].getOption("Adventure").action(null);
        new ServerControls().getOptions(controller)[1].action(null);
    }
}