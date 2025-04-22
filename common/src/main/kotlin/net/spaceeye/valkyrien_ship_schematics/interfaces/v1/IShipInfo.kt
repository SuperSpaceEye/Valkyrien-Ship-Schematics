package net.spaceeye.valkyrien_ship_schematics.interfaces.v1

import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.primitives.AABBic

interface IShipInfo {
    /**
     * Doesn't need to represent actual id. It's just needed to differentiate saved ships
     */
    val id: Long

    /**
     * Position of this ship relative to center of all ships included in schematic
     */
    val relPositionToCenter: Vector3d

    /**
     * idk how to explain it so here's pseudocode
     * ```kotlin
     * val b = ship.shipAABB
     * val centeredShipAABB = b.translate(
     *      ((b.maxX() - b.minX()) / 2.0 + b.minX()).toInt(),
     *      ((b.maxY() - b.minY()) / 2.0 + b.minY()).toInt(),
     *      ((b.maxZ() - b.minZ()) / 2.0 + b.minZ()).toInt(),
     *      AABBi()
     * )
     * ```
     */
    val centeredShipAABB: AABBic

    /**
     * Ship's center position during schematic creation
     */
    val previousCenterPosition: Vector3d

    val shipScale: Double

    /**
     * Rotation of ship assuming the object's quaternion is Quaternion(x=0, y=0, z=0, w=1)
     */
    val rotation: Quaterniondc
}