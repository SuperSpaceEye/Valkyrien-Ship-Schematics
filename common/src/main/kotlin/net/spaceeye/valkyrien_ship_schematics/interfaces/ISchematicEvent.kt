package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.spaceeye.valkyrien_ship_schematics.SchematicEventRegistry
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import java.util.function.Supplier

/**
 * Class using this interface should be default-initializable
 */
interface ISchematicEvent {
    /**
     * Should be called on schematic copy, before blocks were copied.
     */
    fun onCopy(
        level: ServerLevel,
        shipsToBeSaved: List<ServerShip>,
        centerPositions: Map<ShipId, Vector3d>
    ): ISerializable?

    /**
     * Should be called after each individual ship is created, but blocks haven't been placed yet.
     * [maybeLoadedShips] should contain ships that have their blocks already loaded, or that are empty.
     */
    fun onPasteBeforeBlocksAreLoaded(
        level: ServerLevel,
        maybeLoadedShips: List<Pair<ServerShip, Long>>,
        emptyShip: Pair<ServerShip, Long>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<FriendlyByteBuf>?,
    )

    /**
     * Should be called after all ships are created with their blocks placed and block entities loaded
     */
    fun onPasteAfterBlocksAreLoaded(
        level: ServerLevel,
        loadedShips: List<Pair<ServerShip, Long>>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<FriendlyByteBuf>?,
    )

    /**
     * TODO description
     */
    fun shouldBeExecutedAfter(): Class<ISchematicEvent>? = null

    fun getName() = SchematicEventRegistry.typeToString(this.javaClass)
}