package mod.wurmonline.serverlauncher.consolereader;

import com.wurmonline.server.ServerEntry;
import mod.wurmonline.serverlauncher.LocaleHelper;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.exceptions.InvalidValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ValueTest {
    ResourceBundle messages = LocaleHelper.getBundle("ConsoleReader");
    String name = "name";
    String aValue = "value";
    String newValue = "not a value";
    String message = "message";

    @Mock
    List<String> fakeTokens = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testActionRead() throws Exception {
        Value value = new Value(name, "") {
            @Override
            protected String get(ServerEntry server) {
                return aValue;
            }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {}
        };
        assertEquals(name + " is currently - " + aValue, value.action(new ArrayList<>()));
    }

    @Test
    public void testActionSet() throws Exception {
        Value value = new Value(name, "") {
            String myValue = aValue;

            @Override
            protected String get(ServerEntry server) {
                return myValue;
            }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue { myValue = tokens.get(0); }
        };
        List<String> newTokens = new ArrayList<>();
        newTokens.add(newValue);
        value.action(newTokens);
        assertEquals(name + " is currently - " + newValue, value.action(new ArrayList<>()));
    }

    @Test
    public void testActionSetInvalid() throws Exception {
        Value value = new Value(name, "") {
            @Override
            protected String get(ServerEntry server) { return ""; }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue { throw new InvalidValue(name, message); }
        };
        List<String> newTokens = new ArrayList<>();
        newTokens.add(newValue);
        assertEquals("Invalid value - " + name + " " + message, value.action(newTokens));
    }

    @Test
    public void testActionServerNotInitialized() throws Exception {
        ServerController fakeServer = mock(ServerController.class);
        when(fakeServer.isInitialized()).thenReturn(false);
        Value value = new Value(name, "", fakeServer) {
            @Override
            protected String get(ServerEntry server) { return ""; }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {}
        };
        List<String> newTokens = new ArrayList<>();
        newTokens.add(newValue);
        assertEquals(messages.getString("no_server_selected"), value.action(newTokens));

        verify(fakeServer).isInitialized();
    }

    @Test
    public void testActionServerRunning() throws Exception {
        ServerController fakeServer = mock(ServerController.class);
        when(fakeServer.isInitialized()).thenReturn(true);
        when(fakeServer.getLocalServer()).thenReturn(new ServerEntry());
        when(fakeServer.serverIsRunning()).thenReturn(true);
        Value value = new Value(name, "", fakeServer) {
            @Override
            protected String get(ServerEntry server) { return ""; }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {}
        };
        List<String> newTokens = new ArrayList<>();
        newTokens.add(newValue);
        assertEquals(messages.getString("server_running"), value.action(newTokens));
    }

    @Test
    public void testGet() throws Exception {
        Value value = new Value("", "") {
            @Override
            protected String get(ServerEntry server) {
                return aValue;
            }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {}
        };
        assertEquals(aValue, value.get(null));
    }

    @Test
    public void testSet() throws Exception {
        Value value = new Value("", "") {
            String storedValue;
            @Override
            protected String get(ServerEntry server) {
                return storedValue;
            }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {
                storedValue = tokens.get(0);
            }
        };
        when(fakeTokens.get(0)).thenReturn(aValue);
        value.set(null, fakeTokens);
        assertEquals(aValue, value.get(null));
        verify(fakeTokens).get(0);
    }

    @Test
    public void testSetCommand() throws Exception {
        Value value = new Value(aValue, "") {
            String storedValue;
            @Override
            protected String get(ServerEntry server) {
                return storedValue;
            }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {
                storedValue = tokens.get(0);
            }
        };
        when(fakeTokens.get(0)).thenReturn("set").thenReturn(newValue);
        value.action(fakeTokens);
        assertEquals(newValue, value.get(null));
        verify(fakeTokens, times(2)).get(0);
    }

    @Test
    public void testSetCommandMultipleTokens() throws Exception {
        Value value = new Value(aValue, "") {
            String storedValue;
            @Override
            protected String get(ServerEntry server) {
                return storedValue;
            }

            @Override
            protected void set(ServerEntry server, List<String> tokens) throws InvalidValue {
                storedValue = String.join(" ", tokens);
            }
        };
        List<String> tokens = new ArrayList<>();
        Collections.addAll(tokens, "set", newValue, newValue, newValue);
        value.action(tokens);
        assertEquals(String.join(" ", newValue, newValue, newValue), value.get(null));
    }
}