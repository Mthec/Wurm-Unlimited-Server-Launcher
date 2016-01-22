package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.consolereader.exceptions.NoSuchOption;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
            public String action(List<String> tokens) {
                return null;
            }
        };

        subCommand = new Command(null, null) {
            @Override
            public String action(List<String> tokens) {
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
        assertEquals(helpText + System.lineSeparator() + menu.list(), menu.action(null));
    }

    @Test
    public void testAsk() throws Exception {
        assertEquals(command, menu.getOption(commandName));
    }

    @Test(expected = NoSuchOption.class)
    public void testAskNotFound() throws Exception {
        menu.getOption("Not Found");
    }

    @Test
    public void testHelp() throws Exception {
        assertEquals(helpText + System.lineSeparator() +
                "Menu - [" + subMenuName + "]" + System.lineSeparator() + "Options - [" + commandName + "]",
                menu.help());
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
    public void testListMenuCommand() throws Exception {
        assertEquals("Menu - [" + subMenuName + "]" + System.lineSeparator() + "Options - [" + commandName + "]", menu.list());
    }

    @Test
    public void testListMenu() throws Exception {
        Menu newMenu = new Menu(null, null, new Option[] {
                subMenu,
        });
        assertEquals("Menu - [" + subMenuName + "]", newMenu.list());
    }

    @Test
    public void testListCommand() throws Exception {
        Menu newMenu = new Menu(null, null, new Option[] {
                command,
        });
        assertEquals("Options - [" + commandName + "]", newMenu.list());
    }

    @Test(expected = AssertionError.class)
    public void testListEmpty() throws Exception {
        new Menu(null, null, new Option[0]);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(name, menu.getName());
    }

    @Test
    public void testGetReturnsMenu() throws Exception {
        assertEquals(menu, menu.get());
    }
}