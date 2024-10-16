package net.spaceeye.valkyrien_ship_schematics.interfaces

import net.spaceeye.valkyrien_ship_schematics.interfaces.v1.IShipInfo
import org.joml.Vector3d

interface IShipSchematicInfo {
    /**
     * maxObjectPos = objectAABB.max() - objectAABB.min() where objectAABB is a sum of all ships worldAABB's
     */
    val maxObjectPos: Vector3d

    var shipsInfo: List<IShipInfo>
}