package net.spaceeye.valkyrien_ship_schematics

import net.spaceeye.valkyrien_ship_schematics.interfaces.ISchematicEvent
import net.spaceeye.valkyrien_ship_schematics.interfaces.IShipSchematic
import net.spaceeye.valkyrien_ship_schematics.util.Registry
import org.jetbrains.annotations.ApiStatus
import java.util.function.Supplier

/**
 * Schematic types should be registered at mod initialization.
 * To register schematic do
 * ```kotlin
 * SchematicRegistry.register(SchematicType::class)
 * ```
 */
object SchematicRegistry: Registry<IShipSchematic>(true)

/**
 * Schematic types should be registered at mod initialization.
 * To register schematic do
 * ```kotlin
 * SchematicEventRegistry.register(SchematicType::class)
 * ```
 */

@ApiStatus.Internal
data class IdkWhatToNameIt(val roots: MutableList<ISchematicEvent>, val branches: MutableMap<String, MutableSet<Pair<String, Supplier<ISchematicEvent>>>>)

object SchematicEventRegistry: Registry<ISchematicEvent>(true) {
    @ApiStatus.Internal
    fun makeOrderedInstances(): IdkWhatToNameIt {
        val roots = mutableListOf<ISchematicEvent>()
        val branches = mutableMapOf<String, MutableSet<Pair<String, Supplier<ISchematicEvent>>>>()

        asList().forEach { supplier ->
            val instance = supplier.get()
            instance.shouldBeExecutedAfter()?.let {
                branches.getOrPut(typeToString(it)) { mutableSetOf() }.add(instance.getName() to supplier)
            } ?: run {
                roots.add(instance)
            }
        }

        return IdkWhatToNameIt(roots, branches)
    }
}
