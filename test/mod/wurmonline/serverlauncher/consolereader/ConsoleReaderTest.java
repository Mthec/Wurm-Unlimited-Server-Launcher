package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.ServerConsoleController;
import mod.wurmonline.serverlauncher.ServerController;
import mod.wurmonline.serverlauncher.consolereader.exceptions.DuplicateOptionException;
import mod.wurmonline.serverlauncher.consolereader.exceptions.RebuildRequired;
import mod.wurmonline.serverlauncher.consolereader.exceptions.ReservedOptionException;
import org.gotti.wurmunlimited.modloader.interfaces.WurmCommandLine;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void testMainConstructor() throws Exception {
        ServerConsoleController controller = mock(ServerConsoleController.class);
        controller.mods = new ArrayList<>();
        ConsoleReader reader = new ConsoleReader(controller);
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn(null);
        reader.run();
        assertEquals(reader.topMenu.help() + System.lineSeparator(), outPut.toString());
    }

    @Test(expected = ReservedOptionException.class)
    public void testReservedOptions() throws Exception {
        ServerConsoleController controller = mock(ServerConsoleController.class);
        controller.mods = new ArrayList<>();

        class TestMod implements WurmMod, WurmCommandLine {
            @Override
            public Option getOption(ServerController controller) {
                return new Command("help", null) {
                    @Override
                    public String action(List<String> tokens) throws RebuildRequired {
                        return null;
                    }
                };
            }
        }

        controller.mods.add(new TestMod());
        new ConsoleReader(controller);
    }

    @Test(expected = DuplicateOptionException.class)
    public void testDuplicateOption() throws Exception {
        ServerConsoleController controller = mock(ServerConsoleController.class);
        controller.mods = new ArrayList<>();

        class TestMod implements WurmMod, WurmCommandLine {
            @Override
            public Option getOption(ServerController controller) {
                return new Command(commandOption, null) {
                    @Override
                    public String action(List<String> tokens) throws RebuildRequired {
                        return null;
                    }
                };
            }
        }

        controller.mods.add(new TestMod());
        controller.mods.add(new TestMod());
        new ConsoleReader(controller);
    }

    @Test()
    public void testRunDefaultCommands() throws Exception {
        for (String command : new String[] {
                "help",
                "list",
                "menu",
                "up"
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
        ConsoleReader reader = new ConsoleReader(new Option[] {
                new Command(commandOption, null) {
                    @Override
                    public String action(List<String> tokens) {
                        return "";
                    }
                }
        });
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn(noOption).thenReturn(null);
        reader.run();
        assertEquals("Unknown option - " + noOption + System.lineSeparator(), errPut.toString());
    }

    @Test()
    public void testRunList() throws Exception {
        Menu menu = new Menu(menuOption, null, new Option[] {
                new Command(menuOption, null) {
                    @Override
                    public String action(List<String> tokens) {
                        return "";
                    }
                }
        });

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

    @Test()
    public void testRunGoToMenu() throws Exception {
        Menu menu = new Menu(menuOption, null, new Option[] {
                new Command("", "") {
                    @Override
                    public String action(List<String> tokens) throws RebuildRequired {
                        return null;
                    }
        }});
        ConsoleReader reader = new ConsoleReader(new Option[] { menu });
        reader.reader = mock(BufferedReader.class);
        when(reader.reader.readLine()).thenReturn(menuOption).thenReturn(null);
        outPut.reset();
        reader.run();
        assertEquals(menu.help() + System.lineSeparator(), outPut.toString());
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