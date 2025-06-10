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
     * [maybeLoadedShips] is a map of old shipId to a maybe ship. Should contain ships that have their blocks already loaded, or that are empty.
     * [centerPositions] is a map of old shipId to a pair of previous ship center, and new ship center
     */
    fun onPasteBeforeBlocksAreLoaded(
        level: ServerLevel,
        maybeLoadedShips: Map<Long, ServerShip>,
        emptyShip: Pair<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<FriendlyByteBuf>?,
    )

    /**
     * Should be called after all ships are created with their blocks placed and block entities loaded
     * [loadedShips] is a map of old shipId to a new ship.
     * [centerPositions] is a map of old shipId to a pair of previous ship center, and new ship center
     */
    fun onPasteAfterBlocksAreLoaded(
        level: ServerLevel,
        loadedShips: Map<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<FriendlyByteBuf>?,
    )

    /**
     * If not null, then event will only be fired after the event given by this fn
     */
    fun shouldBeExecutedAfter(): Class<ISchematicEvent>? = null

    fun getName() = SchematicEventRegistry.typeToString(this.javaClass)
}