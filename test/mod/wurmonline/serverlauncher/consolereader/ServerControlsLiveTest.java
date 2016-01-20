package mod.wurmonline.serverlauncher.consolereader;

import com.wurmonline.server.Servers;
import com.wurmonline.server.utils.SimpleArgumentParser;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ServerControlsLiveTest {
    ServerConsoleController controller;

    @Before
    public void setUp() throws Exception {
        controller = new ServerConsoleController();
        Servers.argumets = new SimpleArgumentParser(new String[0], new HashSet<>());
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
        ServerControls controls = new ServerControls();
        controls.getOptions(controller)[3].getOption("Adventure").action(null);
        controls.getOptions(controller)[0].action(null);

        assertTrue(controller.serverIsRunning());
    }

    // TODO - Test parameters.
    // FIXME - Fix problem with running servers when testing.
    @Test
    public void testGetOptionsShutdown() throws Exception {
        ServerControls controls = new ServerControls();
        controls.getOptions(controller)[3].getOption("Adventure").action(null);
        controls.getOptions(controller)[0].action(null);

        assertTrue(controller.serverIsRunning());

        controls.getOptions(controller)[3].getOption("Adventure").action(null);
        controls.getOptions(controller)[1].action(new ArrayList<>());
    }
}