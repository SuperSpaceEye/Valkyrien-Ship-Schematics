package net.spaceeye.valkyrien_ship_schematics.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.spaceeye.valkyrien_ship_schematics.ModidKt.MOD_ID;

@Mod(MOD_ID)
public final class Valkyrien_ship_schematicsForge {
    public Valkyrien_ship_schematicsForge() {
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
