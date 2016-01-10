package mod.wurmonline.serverlauncher.consolereader;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CommandTest {
    String testString;

    @Test
    public void testAction() throws Exception {
        assertEquals(testString, new Command(null, null) {
            @Override
            public String action(List<String> tokens) {
                return testString;
            }
        }.action(null));
    }

    @Test
    public void testActionTokens() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add(testString);
        assertEquals(testString, new Command(null, null) {
            @Override
            public String action(List<String> tokens) {
                return tokens.get(0);
            }
        }.action(list));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAsk() throws Exception {
        new Command(null, null) {
            @Override
            public String action(List<String> tokens) {
                return null;
            }
        }.ask("");
    }

    @Test
    public void testHelp() throws Exception {
        assertEquals(testString, new Command(null, testString) {
            @Override
            public String action(List<String> tokens) {
                return null;
            }
        }.help());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(testString, new Command(testString, null) {
            @Override
            public String action(List<String> tokens) {
                return null;
            }
        }.getName());
    }
}