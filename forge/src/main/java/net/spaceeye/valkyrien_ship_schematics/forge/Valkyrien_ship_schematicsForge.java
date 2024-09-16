package net.spaceeye.valkyrien_ship_schematics.forge;

import net.spaceeye.valkyrien_ship_schematics.Valkyrien_ship_schematics;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Valkyrien_ship_schematics.MOD_ID)
public final class Valkyrien_ship_schematicsForge {
    public Valkyrien_ship_schematicsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(Valkyrien_ship_schematics.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        Valkyrien_ship_schematics.init();
    }
}
