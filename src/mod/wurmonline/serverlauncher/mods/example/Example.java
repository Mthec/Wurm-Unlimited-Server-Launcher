package mod.wurmonline.serverlauncher.mods.example;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import mod.wurmonline.serverlauncher.gui.ServerGuiController;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmUIMod;

import java.util.Properties;

public class Example implements WurmMod, WurmUIMod, Configurable {
    String name;

    public String getName() {
        return name;
    }

    public Region getRegion(ServerGuiController controller) {
        Pane pane = new Pane();
        Label label = new Label();
        label.setText("It worked?");
        pane.getChildren().add(label);
        return pane;
    }

    @Override
    public void configure(Properties properties) {
        name = properties.getProperty("name");
    }
}
