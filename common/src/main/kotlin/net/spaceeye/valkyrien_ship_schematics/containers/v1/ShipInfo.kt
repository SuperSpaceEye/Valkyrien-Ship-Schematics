package net.spaceeye.valkyrien_ship_schematics.containers.v1

import net.spaceeye.valkyrien_ship_schematics.interfaces.v1.IShipInfo
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.primitives.AABBic

open class ShipInfo(
    override val id: Long,
    override val relPositionToCenter: Vector3d,
    override val centeredShipAABB: AABBic,
    override val previousCenterPosition: Vector3d,
    override val previousCOMPosition: Vector3d,
    override val shipScale: Double,
    override val rotation: Quaterniondc,
) : IShipInfo