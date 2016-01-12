package mod.wurmonline.serverlauncher.consolereader;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ConsoleReaderTest {
    PrintStream oldOut;
    ByteArrayOutputStream outPut = new ByteArrayOutputStream();
    PrintStream oldErr;
    ByteArrayOutputStream errPut = new ByteArrayOutputStream();
    String menuOption = "menuOption";
    String commandOption = "commandOption";
    String noOption = "blah";
    String result = "CONFIRMED";

    @Before
    public void setUp() throws Exception {
        oldOut = System.out;
        System.setOut(new PrintStream(outPut));
        oldErr = System.err;
        System.setErr(new PrintStream(errPut));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(oldOut);
        System.setErr(oldErr);
    }

    @Test()
    public void testRunDefaultCommands() throws Exception {
        for (String command : new String[] {
                "help",
                "list",
                "menu"
        }) {
            ConsoleReader reader = new ConsoleReader(new Option[]{
                    new Command(command, null) {
                        @Override
                        public String action(List<String> tokens) {
                            return result;
                        }
                    }
            });
            reader.reader = mock(BufferedReader.class);
            when(reader.reader.readLine()).thenReturn(command).thenReturn(null);
            outPut.reset();
            reader.run();
            assertNotEquals(result + System.lineSeparator(), outPut.toString());
        }
    }

    @Test()
    public void testRunOptionFound() throws Exception {
        ConsoleReader reader = new ConsoleReader(new Option[] {
                new Command(commandOption, null) {
                    @Override
                    public String action(List<String> tokens) {
                        return result;
                    }
                }
        });
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn(commandOption).thenReturn(null);
        outPut.reset();
        reader.run();
        assertEquals(result + System.lineSeparator(), outPut.toString());
    }

    @Test()
    public void testRunNoSuchOption() throws Exception {
        ConsoleReader reader = new ConsoleReader(new Option[0]);
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn(noOption).thenReturn(null);
        reader.run();
        assertEquals("Unknown command - " + noOption + System.lineSeparator(), errPut.toString());
    }

    @Test()
    public void testRunList() throws Exception {
        Menu menu = new Menu(menuOption, null, new Option[0]);

        ConsoleReader reader = new ConsoleReader(new Option[] {
            menu
        });
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn("list").thenReturn(null);
        outPut.reset();
        reader.run();
        assertEquals(reader.topMenu.list() + System.lineSeparator(), outPut.toString());
    }

    @Test()
    public void testRunBlankInput() throws Exception {
        ConsoleReader reader = new ConsoleReader(new Option[] {
                new Command(commandOption, null) {
                    @Override
                    public String action(List<String> tokens) {
                        return result;
                    }
                }
        });
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn(" ").thenReturn(null);
        outPut.reset();
        reader.run();
        assertEquals("", outPut.toString());
    }

    // TODO - More ConsoleReader.run tests.

    @Test
    public void testTokenize() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("this");
        list.add("is");
        list.add("a");
        list.add("test");
        assertEquals(list, ConsoleReader.tokenize(String.join(" ", list)));
    }

    @Test
    public void testTokenizeEmptyRemoval() throws Exception {
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.add("this");
        list.add("is");
        list.add("");
        list.add("a");
        list.add(" ");
        list.add("space");
        list.add("test");
        String string = String.join(" ", list);
        list.remove(" ");
        list.remove("");
        list.remove(" ");
        assertEquals(list, ConsoleReader.tokenize(string));
    }
}