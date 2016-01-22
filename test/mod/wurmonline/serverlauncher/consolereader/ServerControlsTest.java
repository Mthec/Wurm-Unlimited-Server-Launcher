package mod.wurmonline.serverlauncher.consolereader;

import com.ibm.icu.text.MessageFormat;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.consolereader.exceptions.RebuildRequired;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ServerControlsTest {
    ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    String name = "name";

    @Test
    public void testGetOptionsStartNotSelected() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.getCurrentDir()).thenReturn("");
        Command start = (Command)new ServerControls().getOptions(fakeController)[0];
        assert start.getName().equals("start");

        assertEquals("No server selected.", start.action(null));
        // Choose between start and shutdown.
        verify(fakeController).serverIsRunning();
        // start
        verify(fakeController).isInitialized();
    }

    @Test
    public void testGetOptionsStatusNone() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.isInitialized()).thenReturn(false);
        Command status = (Command)new ServerControls().getOptions(fakeController)[1];
        assert status.getName().equals("status");

        assertEquals("No server selected.", status.action(null));

        verify(fakeController).isInitialized();
    }

    @Test
    public void testGetOptionsStatusRunning() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.isInitialized()).thenReturn(true);
        when(fakeController.serverIsRunning()).thenReturn(true);
        when(fakeController.getCurrentDir()).thenReturn("Adventure");
        Command status = (Command)new ServerControls().getOptions(fakeController)[1];
        assert status.getName().equals("status");

        assertEquals(MessageFormat.format(messages.getString("status_result"), "Adventure", System.lineSeparator(), messages.getString("status_running")), status.action(null));

        // Once for start/shutdown check, once for actual test.
        verify(fakeController, times(2)).serverIsRunning();
        verify(fakeController).isInitialized();
        verify(fakeController).getCurrentDir();
    }

    @Test
    public void testGetOptionsStatusNotRunning() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.isInitialized()).thenReturn(true);
        when(fakeController.serverIsRunning()).thenReturn(false);
        when(fakeController.getCurrentDir()).thenReturn("Adventure");
        Command status = (Command)new ServerControls().getOptions(fakeController)[1];
        assert status.getName().equals("status");

        assertEquals(MessageFormat.format(messages.getString("status_result"), "Adventure", System.lineSeparator(), messages.getString("status_not_running")), status.action(null));

        // Once for start/shutdown check, once for actual test.
        verify(fakeController, times(2)).serverIsRunning();
        verify(fakeController).isInitialized();
        verify(fakeController).getCurrentDir();
    }

    @Test(expected = RebuildRequired.class)
    public void testGetOptionsSelect() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        Command option = (Command)new ServerControls().getOptions(fakeController)[2].getOption("Adventure");

        option.action(new ArrayList<>());
        verify(fakeController).setCurrentDir("Adventure");
    }

    @Test
    public void testGetShortenedName() throws Exception {
        assertEquals("Thisisatest", new ServerControls().getShortenedName("This is a test"));
    }

    @Test
    public void testGetShortenedNameDuplicates() throws Exception {
        ServerControls controls = new ServerControls();
        controls.getShortenedName(name);
        controls.getShortenedName(name);
        assertEquals(name + "2", controls.getShortenedName(name));
    }

    @Test
    public void testGetServersTemp() throws Exception {
        List<String> servers = new ArrayList<>();
        servers.add("Adventure");
        servers.add("Creative");
        assertEquals(servers, new ServerControls().getServersTemp());
    }
}