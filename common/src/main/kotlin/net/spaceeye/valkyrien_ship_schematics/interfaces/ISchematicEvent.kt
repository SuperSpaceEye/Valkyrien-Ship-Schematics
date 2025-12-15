package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
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
     * [onCopyTag] is an alternative if you want to use [CompoundTag].
     */
    fun onCopy(
        level: ServerLevel,
        shipsToBeSaved: List<ServerShip>,
        centerPositions: Map<ShipId, Vector3d>
    ): ISerializable?
    /**
     * Alternative to [onCopy]. Only one should return actual data, the other should return null.
     */
    fun onCopyTag(
        level: ServerLevel,
        shipsToBeSaved: List<ServerShip>,
        centerPositions: Map<ShipId, Vector3d>
    ): CompoundTag? = null

    /**
     * Should be called after each individual ship is created, but blocks haven't been placed yet.
     * [maybeLoadedShips] is a map of old shipId to a maybe ship. Should contain ships that have their blocks already loaded, or that are empty.
     * [centerPositions] is a map of old shipId to a pair of previous ship center, and new ship center.
     * Has alternative [onPasteBeforeBlocksAreLoadedTag], only one should be implemented, the other should do nothing.
     */
    fun onPasteBeforeBlocksAreLoaded(
        level: ServerLevel,
        maybeLoadedShips: Map<Long, ServerShip>,
        emptyShip: Pair<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<FriendlyByteBuf>?,
    )
    /**
     * Should be called after each individual ship is created, but blocks haven't been placed yet.
     * [maybeLoadedShips] is a map of old shipId to a maybe ship. Should contain ships that have their blocks already loaded, or that are empty.
     * [centerPositions] is a map of old shipId to a pair of previous ship center, and new ship center.
     * Has alternative [onPasteBeforeBlocksAreLoaded], only one should be implemented, the other should do nothing.
     */
    fun onPasteBeforeBlocksAreLoadedTag(
        level: ServerLevel,
        maybeLoadedShips: Map<Long, ServerShip>,
        emptyShip: Pair<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<CompoundTag>?,
    ) {}

    /**
     * Should be called after all ships are created with their blocks placed and block entities loaded
     * [loadedShips] is a map of old shipId to a new ship.
     * [centerPositions] is a map of old shipId to a pair of previous ship center, and new ship center.
     * Has alternative [onPasteAfterBlocksAreLoadedTag], only one should be implemented, the other should do nothing.
     */
    fun onPasteAfterBlocksAreLoaded(
        level: ServerLevel,
        loadedShips: Map<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<FriendlyByteBuf>?,
    )
    /**
     * Should be called after all ships are created with their blocks placed and block entities loaded
     * [loadedShips] is a map of old shipId to a new ship.
     * [centerPositions] is a map of old shipId to a pair of previous ship center, and new ship center.
     * Has alternative [onPasteAfterBlocksAreLoaded], only one should be implemented, the other should do nothing.
     */
    fun onPasteAfterBlocksAreLoadedTag(
        level: ServerLevel,
        loadedShips: Map<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>,
        data: Supplier<CompoundTag>?,
    ) {}

    /**
     * Should return what items are required for pasting in survival. Should be ignored for creative schematics.
     * Has alternative [pasteSurvivalCostTag], only one should be implemented, the other should do nothing.
     */
    fun pasteSurvivalCost(data: Supplier<FriendlyByteBuf>?): Map<Item, Int>? = null
    /**
     * Should return what items are required for pasting in survival. Should be ignored for creative schematics.
     * Has alternative [pasteSurvivalCost], only one should be implemented, the other should do nothing.
     */
    fun pasteSurvivalCostTag(data: Supplier<CompoundTag>?): Map<Item, Int>? = null

    /**
     * If not null, then event will only be fired after the event given by this fn
     */
    fun shouldBeExecutedAfter(): Class<ISchematicEvent>? = null

    fun getName() = SchematicEventRegistry.typeToString(this.javaClass)
}