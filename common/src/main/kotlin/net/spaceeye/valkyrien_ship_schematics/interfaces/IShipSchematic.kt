package net.spaceeye.valkyrien_ship_schematics.interfaces

import io.netty.buffer.ByteBuf
import net.spaceeye.valkyrien_ship_schematics.interfaces.IShipSchematicInfo
import net.spaceeye.valkyrien_ship_schematics.interfaces.Serializable

interface IShipSchematic {
    fun getInfo(): IShipSchematicInfo

    fun serialize(): Serializable
    fun deserialize(buf: ByteBuf): Boolean
}