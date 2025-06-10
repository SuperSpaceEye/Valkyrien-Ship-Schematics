package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.minecraft.server.level.ServerLevel
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId

/**
 * Should be saved before any ship blocks is saved
 * Should be loaded after all ships were created and all blocks were placed and loaded
 */
interface ICopyableForcesInducer {
    /**
     * Should be called before jackson serialization
     */
    fun onCopy(
        level: ServerLevel,
        shipOn: LoadedServerShip,
        shipsToBeSaved: List<ServerShip>,
        centerPositions: Map<ShipId, Vector3d>
    )

    /**
     * Should be called after jackson deserialization
     */
    fun onPaste(
        level: ServerLevel,
        shipOn: LoadedServerShip,
        loadedShips: Map<Long, ServerShip>,
        centerPositions: Map<ShipId, Pair<Vector3d, Vector3d>>
    )
}