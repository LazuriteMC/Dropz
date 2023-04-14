package dev.lazurite.dropz.forge;

import dev.lazurite.dropz.Dropz;
import net.minecraftforge.fml.common.Mod;

@Mod(Dropz.MODID)
public class DropzForge {

    public DropzForge() {
        Dropz.initialize();
    }

}
