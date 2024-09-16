package net.spaceeye.valkyrien_ship_schematics.fabric;

import net.spaceeye.valkyrien_ship_schematics.Valkyrien_ship_schematics;
import net.fabricmc.api.ModInitializer;

public final class Valkyrien_ship_schematicsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Valkyrien_ship_schematics.init();
    }
}
