package mod.wurmonline.serverlauncher.consolereader;

import static org.mockito.Mockito.*;
import mod.wurmonline.serverlauncher.ServerConsoleController;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ServerControlsTest {
    String name = "name";

    @Test
    public void testGetOptionsStatusNone() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.getCurrentDir()).thenReturn("");
        assertEquals("No server selected.", new ServerControls().getOptions(fakeController)[2].action(null));

        verify(fakeController).getCurrentDir();
    }

    @Test
    public void testGetOptionsStatusRunning() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.getCurrentDir()).thenReturn("Adventure");
        when(fakeController.serverIsRunning()).thenReturn(true);
        assertEquals("Name - Adventure" + System.lineSeparator() + "Server is running.",
                new ServerControls().getOptions(fakeController)[2].action(null));
        verify(fakeController, times(2)).getCurrentDir();
    }

    @Test
    public void testGetOptionsStatusNotRunning() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        when(fakeController.getCurrentDir()).thenReturn("Adventure");
        when(fakeController.serverIsRunning()).thenReturn(false);
        assertEquals("Name - Adventure" + System.lineSeparator() + "Server is not running.",
                new ServerControls().getOptions(fakeController)[2].action(null));
        verify(fakeController, times(2)).getCurrentDir();
    }

    @Test
    public void testGetOptionsSelect() throws Exception {
        ServerConsoleController fakeController = mock(ServerConsoleController.class);
        Command option = (Command)new ServerControls().getOptions(fakeController)[3].getOption("Adventure");

        assertEquals("Adventure selected", option.action(new ArrayList<>()));
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