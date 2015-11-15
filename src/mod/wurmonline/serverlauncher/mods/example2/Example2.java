package mod.wurmonline.serverlauncher.mods.example2;

import mod.wurmonline.serverlauncher.ServerController;
import org.gotti.wurmunlimited.modloader.interfaces.WurmArgsMod;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import java.util.HashSet;
import java.util.Set;

public class Example2 implements WurmMod, WurmArgsMod {
    String ARG = "present";

    @Override
    public Set<String> getArgs() {
        Set<String> set = new HashSet<>();
        set.add(ARG);
        return set;
    }

    @Override
    public void parseArgs(ServerController controller) {
        String present = controller.arguments.getOptionValue(ARG);
        System.out.println(present != null && !present.isEmpty() ? String.format("Somebody gave me a %s!", present) : "I got nothing. :(");
    }
}
