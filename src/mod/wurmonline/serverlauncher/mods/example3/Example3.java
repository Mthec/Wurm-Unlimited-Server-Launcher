package mod.wurmonline.serverlauncher.mods.example3;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmArgsMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmUIMod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Example3 implements WurmMod, WurmUIMod, Configurable, WurmArgsMod {
    Set<String> args;
    String name;

    public String getName() {
        return name;
    }

    public Set<String> getArgs () { return args; }

    public void configure (Properties properties) {
        args = new HashSet<>(Arrays.asList(properties.getProperty("args").split(",")));
        name = properties.getProperty("name");
    }

    public Region getRegion(ServerGuiController controller) {
        VBox vbox = new VBox();
        Label label1 = new Label();
        label1.setText(controller.arguments.getOptionValue("test1"));
        Label label2 = new Label();
        label2.setText(controller.arguments.getOptionValue("test2"));
        vbox.getChildren().add(label1);
        vbox.getChildren().add(label2);
        return vbox;
    }
}
