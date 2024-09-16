package net.spaceeye.valkyrien_ship_schematics.containers.v1

import net.spaceeye.valkyrien_ship_schematics.interfaces.IShipSchematicInfo
import net.spaceeye.valkyrien_ship_schematics.interfaces.v1.IShipInfo
import org.joml.Vector3d

open class ShipSchematicInfo(
    override val maxObjectPos: Vector3d,
    override var shipsInfo: List<IShipInfo>
) : IShipSchematicInfo