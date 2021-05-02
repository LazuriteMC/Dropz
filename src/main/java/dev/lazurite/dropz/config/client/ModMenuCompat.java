package dev.lazurite.dropz.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.config.Config;
import dev.lazurite.dropz.util.YeetType;
import me.shedaniel.clothconfiglite.api.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigScreen screen = ConfigScreen.create(new TranslatableText("config." + Dropz.MODID + ".title"), parent);

            screen.add(new TranslatableText("config." + Dropz.MODID + ".merge"),
                    Config.getInstance().merge,
                    () -> Config.getInstance().merge,
                    value -> {
                        Config.getInstance().merge = (boolean) value;
                        Config.getInstance().save();
                    });

            screen.add(new TranslatableText("config." + Dropz.MODID + ".yeet_multiplier"),
                    Config.getInstance().yeetMultiplier,
                    () -> Config.getInstance().yeetMultiplier,
                    value -> {
                        Config.getInstance().yeetMultiplier = (YeetType) value;
                        Config.getInstance().save();
                    });

            return screen.get();
        };
    }
}
