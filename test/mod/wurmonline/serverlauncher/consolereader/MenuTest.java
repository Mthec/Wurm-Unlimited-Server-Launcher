package mod.wurmonline.serverlauncher.consolereader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MenuTest {
    static Menu menu;
    static String name = "Name";
    static String helpText = "Help";
    static Menu subMenu;
    static String subMenuName = "Sub Name";
    static Command command;
    static String commandName = "Command";
    static String commandHelpText = "Command Help";
    static Command subCommand;

    @BeforeClass
    public static void setUp() throws Exception {
        command = new Command(commandName, commandHelpText) {
            @Override
            public String action() {
                return null;
            }
        };

        subCommand = new Command(null, null) {
            @Override
            public String action() {
                return null;
            }
        };

        subMenu = new Menu(subMenuName, null, new Option[] {
                subCommand
        });

        menu = new Menu(name, helpText, new Option[] {
                subMenu,
                command,
        });
    }

    @AfterClass
    public static void tearDown() throws Exception {
        menu = null;
    }

    @Test
    public void testAction() throws Exception {
        assertEquals(helpText, menu.action());
    }

    @Test
    public void testAsk() throws Exception {
        assertEquals(command, menu.ask(commandName));
    }

    @Test(expected = NoSuchOption.class)
    public void testAskNotFound() throws Exception {
        menu.ask("Not Found");
    }

    @Test
    public void testHelp() throws Exception {
        List<String> expected = new ArrayList<>();
        expected.add("Options - [" + subMenuName + ", " + commandName + "]");
        expected.add("Options - [" + commandName + ", " + subMenuName + "]");
        assertTrue(expected.contains(menu.help()));
    }

    @Test
    public void testHelp1() throws Exception {
        assertEquals(commandHelpText, menu.help(commandName));
    }

    @Test(expected = NoSuchOption.class)
    public void testHelp1NotFound() throws Exception {
        menu.help("Not Found");
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(name, menu.getName());
    }
}