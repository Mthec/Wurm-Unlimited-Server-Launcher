package mod.wurmonline.serverlauncher.consolereader;

import static org.mockito.Mockito.*;
import com.wurmonline.server.ServerEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ValueTest {
    String name;
    String aValue;
    @Mock
    List<String> fakeTokens = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAction() throws Exception {
        // TODO
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
}